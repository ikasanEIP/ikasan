/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.component.validator.xml;

import org.apache.commons.io.IOUtils;
import org.ikasan.component.validator.ValidationException;
import org.ikasan.component.validator.ValidationResult;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.catalog.CatalogFeatures;
import javax.xml.catalog.CatalogManager;
import javax.xml.catalog.CatalogResolver;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Non-Xerces XML Validator that extracts schema locations from incoming XML documents,
 * compiles and caches schemas for reuse, and validates XML using the compiled schemas.
 * <p/>
 * This implementation does not use any Xerces classes and relies on the standard
 * Java XML validation API.
 *
 * @author Ikasan Development Team
 */
public class XMLValidator<SOURCE, TARGET>
        implements Converter<SOURCE, Object>, ManagedResource, ConfiguredResource<XMLValidatorConfiguration>
{
    /**
     * Logger instance
     */
    private static Logger logger = LoggerFactory.getLogger(XMLValidator.class);

    /**
     * Unique id for configured resource in this component
     */
    private String configuredResourceId;

    /**
     * Configured resource in this component
     */
    private XMLValidatorConfiguration configuration;

    /**
     * Source to InputStream converter
     */
    private Converter<SOURCE, ByteArrayInputStream> sourceToByteArrayInputStreamConverter;

    /**
     * Cache of compiled schemas keyed by schema location(s)
     */
    private Map<String, Schema> schemaCache;

    /**
     * Schema factory for creating schemas
     */
    private SchemaFactory schemaFactory;

    /**
     * Catalog resolver for resolving external schemas
     */
    private CatalogResolver catalogResolver;

    /**
     * XMLInputFactory for reading XML streams
     */
    private XMLInputFactory xmlInputFactory;

    /**
     * Constructor
     */
    public XMLValidator()
    {
        this.schemaCache = new ConcurrentHashMap<>();
        this.xmlInputFactory = XMLInputFactory.newInstance();
        this.xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
    }

    /**
     * Implementation of the onEvent XMLValidation
     *
     * @param source - source to be validated
     * @throws EndpointException - Thrown if error parsing payload content
     */
    @Override
    public Object convert(SOURCE source) throws EndpointException
    {
        if(source == null) {
            throw new ValidationException("The source that is being converted" +
                " cannot be null!");
        }
        ValidationResult<SOURCE, TARGET> validationResult = new ValidationResult<>();
        validationResult.setSource(source);

        if (configuration.isSkipValidation())
        {
            if (configuration.isReturnValidationResult())
            {
                validationResult.setResult(ValidationResult.Result.VALID);
                return validationResult;
            }
            else
            {
                return source;
            }
        }

        try
        {
            ByteArrayInputStream sourceAsInputStream = this.createSourceAsBytes(source);

            // First pass: extract schema locations
            byte[] xmlBytes = sourceAsInputStream.readAllBytes();
            sourceAsInputStream = new ByteArrayInputStream(xmlBytes);

            List<String> schemaLocations = extractSchemaLocations(new ByteArrayInputStream(xmlBytes));

            // Get or compile schema
            Schema schema = getOrCompileSchema(schemaLocations);

            // Validate the XML
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(sourceAsInputStream));

            if (!configuration.isReturnValidationResult())
            {
                return source;
            }
            validationResult.setResult(ValidationResult.Result.VALID);
        }
        catch (SAXException e)
        {
            if (configuration.isThrowExceptionOnValidationFailure() || !configuration.isReturnValidationResult())
            {
                throw new ValidationException(generateErrorMessage(e, source), e);
            }
            validationResult.setResult(ValidationResult.Result.INVALID);
            validationResult.setException(e);
        }
        catch (IOException | XMLStreamException e)
        {
            if (configuration.isThrowExceptionOnValidationFailure() || !configuration.isReturnValidationResult())
            {
                throw new ValidationException(e);
            }
            validationResult.setResult(ValidationResult.Result.INVALID);
            validationResult.setException(e);
        }

        return validationResult;
    }

    /**
     * Extract schema locations from the XML document
     *
     * @param inputStream the XML input stream
     * @return list of schema locations
     * @throws XMLStreamException if error reading XML
     */
    private List<String> extractSchemaLocations(InputStream inputStream) throws XMLStreamException
    {
        List<String> schemaLocations = new ArrayList<>();
        XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(inputStream);

        try
        {
            while (reader.hasNext())
            {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT)
                {
                    // Check for xsi:schemaLocation attribute
                    String schemaLocation = reader.getAttributeValue(
                        XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation");

                    if (schemaLocation != null && !schemaLocation.trim().isEmpty())
                    {
                        // schemaLocation contains pairs of namespace URI and schema URI
                        String[] parts = schemaLocation.trim().split("\\s+");
                        for (int i = 1; i < parts.length; i += 2)
                        {
                            schemaLocations.add(parts[i]);
                        }
                    }

                    // Check for xsi:noNamespaceSchemaLocation attribute
                    String noNamespaceSchemaLocation = reader.getAttributeValue(
                        XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "noNamespaceSchemaLocation");

                    if (noNamespaceSchemaLocation != null && !noNamespaceSchemaLocation.trim().isEmpty())
                    {
                        schemaLocations.add(noNamespaceSchemaLocation.trim());
                    }

                    // Only check the root element
                    break;
                }
            }
        }
        finally
        {
            reader.close();
        }

        return schemaLocations;
    }

    /**
     * Get a cached schema or compile a new one
     *
     * @param schemaLocations list of schema locations
     * @return compiled Schema
     * @throws SAXException if error compiling schema
     * @throws IOException if error reading schema
     */
    private Schema getOrCompileSchema(List<String> schemaLocations) throws SAXException, IOException
    {
        if (schemaLocations.isEmpty())
        {
            throw new SAXException("No schema location found in XML document");
        }

        // Create a cache key from all schema locations
        String cacheKey = String.join("|", schemaLocations);

        // Check cache first
        Schema schema = schemaCache.get(cacheKey);
        if (schema != null)
        {
            logger.debug("Using cached schema for: {}", cacheKey);
            return schema;
        }

        // Compile new schema
        logger.info("Compiling schema for: {}", cacheKey);

        StreamSource[] sources = new StreamSource[schemaLocations.size()];
        for (int i = 0; i < schemaLocations.size(); i++)
        {
            String location = schemaLocations.get(i);

            // Use catalog resolver to resolve the location if available
            if (catalogResolver != null)
            {
                try
                {
                    InputSource resolvedSource = catalogResolver.resolveEntity(null, location);
                    if (resolvedSource != null)
                    {
                        logger.debug("Resolved schema location {} via catalog", location);
                        sources[i] = new StreamSource(resolvedSource.getByteStream(), resolvedSource.getSystemId());
                    }
                    else
                    {
                        logger.debug("Schema location {} not found in catalog, using direct location", location);
                        sources[i] = new StreamSource(location);
                    }
                }
                catch (Exception e)
                {
                    logger.warn("Error resolving schema location {} via catalog: {}", location, e.getMessage());
                    sources[i] = new StreamSource(location);
                }
            }
            else
            {
                sources[i] = new StreamSource(location);
            }
        }

        schema = schemaFactory.newSchema(sources);

        // Cache for future use
        schemaCache.put(cacheKey, schema);

        return schema;
    }

    /**
     * Generate error message for validation failure
     *
     * @param e the exception
     * @param source the source document
     * @return formatted error message
     */
    private String generateErrorMessage(Exception e, SOURCE source)
    {
        String payload;
        if (sourceToByteArrayInputStreamConverter == null && source instanceof String string)
        {
            payload = string;
        }
        else
        {
            try
            {
                payload = IOUtils.toString(sourceToByteArrayInputStreamConverter.convert(source));
            }
            catch (IOException ioe)
            {
                logger.error("Cannot convert to String", ioe);
                payload = "An exception occurred whilst converting the payload to a String: %s".formatted(
                    ioe.getMessage());
            }
        }
        String errorMessage = "XML validation error: %s\n\nXML:\n%s".formatted(e.getMessage(), payload);
        return errorMessage;
    }

    /**
     * Convert source to byte array input stream
     *
     * @param xml the source
     * @return ByteArrayInputStream
     */
    private ByteArrayInputStream createSourceAsBytes(SOURCE xml)
    {
        if (sourceToByteArrayInputStreamConverter == null && xml instanceof String string)
        {
            return new ByteArrayInputStream(string.getBytes());
        }
        else
        {
            return sourceToByteArrayInputStreamConverter.convert(xml);
        }
    }

    @Override
    public String getConfiguredResourceId()
    {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public XMLValidatorConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(XMLValidatorConfiguration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public void startManagedResource()
    {
        if(this.configuration == null) {
            throw new RuntimeException("The configuration cannot be null!");
        }
        // Initialize schema factory
        schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // Set up catalog resolver if configured
        if (configuration.getCatalogUrl() != null)
        {
            try
            {
                logger.debug("Setting up Schema Factory with catalog.xml file [{}]", configuration.getCatalogUrl());
                catalogResolver = CatalogManager
                    .catalogResolver(CatalogFeatures.defaults(), new URI(configuration.getCatalogUrl()));
                schemaFactory.setResourceResolver(catalogResolver);
            }
            catch (Exception e)
            {
                logger.error("Cannot set up catalog resolver", e);
                throw new RuntimeException("Cannot set up catalog resolver", e);
            }
        }

        // Clear the cache
        schemaCache.clear();

        logger.info("XMLValidator started successfully");
    }

    @Override
    public void stopManagedResource()
    {
        // Clear the schema cache
        if (schemaCache != null)
        {
            schemaCache.clear();
        }
        schemaFactory = null;
        catalogResolver = null;

        logger.info("XMLValidator stopped successfully");
    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        // No recovery needed for this component
    }

    @Override
    public boolean isCriticalOnStartup()
    {
        return true;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup)
    {
        // Not configurable
    }

    /**
     * Set the source to byte array input stream converter
     *
     * @param sourceToByteArrayInputStreamConverter the converter
     */
    public void setSourceToByteArrayInputStreamConverter(
        Converter<SOURCE, ByteArrayInputStream> sourceToByteArrayInputStreamConverter)
    {
        this.sourceToByteArrayInputStreamConverter = sourceToByteArrayInputStreamConverter;
    }
}
