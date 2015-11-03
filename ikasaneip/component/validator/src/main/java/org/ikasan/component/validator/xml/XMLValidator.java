

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

import org.apache.log4j.Logger;
import org.ikasan.component.validator.ValidationException;
import org.ikasan.component.validator.ValidationResult;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * XML Validator uses an injected XML parser to validate each incoming payload content.
 * <p/>
 * The required parser must be pre-configured for validation.
 *
 * @author Ikasan Development Team
 */
public class XMLValidator<SOURCE, TARGET> implements Converter<SOURCE, Object>, ManagedResource, ConfiguredResource<XMLValidatorConfiguration> {
    /**
     * Logger instance
     */
    private static Logger logger = Logger.getLogger(XMLValidator.class);

    /**
     * Unique id for configured resource in this component
     */
    private String configuredResourceId;

    /**
     * Configured resource in this component
     */
    private XMLValidatorConfiguration configuration;

    /**
     * Pre-configured document builder factory
     */
    private DocumentBuilderFactory factory;

    /**
     * ErrorHandler instance
     */
    private ErrorHandler errorHandler = new ExceptionThrowingErrorHandler();


    /**
     * Source to InputStream converter
     */
    private Converter<SOURCE, ByteArrayInputStream> sourceToByteArrayInputStreamConverter;


    /**
     * Constructor
     *
     * @param factory - pre configured document builder factory
     */
    public XMLValidator(final DocumentBuilderFactory factory) {
        this.factory = factory;
        if (this.factory == null) {
            throw new IllegalArgumentException("document builder factory cannot be 'null'.");
        }
    }

    /**
     * Implementation of the onEvent XMLValidation
     *
     * @param source - source to be validated
     * @throws TransformationException - Thrown if error parsing payload content
     */
    public Object convert(SOURCE source) throws EndpointException {

        ValidationResult<SOURCE, TARGET> validationResult = new ValidationResult<>();
        validationResult.setSource(source);
        if (configuration.isSkipValidation()) {

            if (configuration.isReturnValidationResult()) {
                validationResult.setResult(ValidationResult.Result.VALID);
                return validationResult;
            } else {
                return source;
            }

        }

        try {

            DocumentBuilder builder = this.factory.newDocumentBuilder();
            builder.setErrorHandler(this.errorHandler);

            InputStream sourceAsInputStream = this.createSourceAsBytes(source);
            builder.parse(sourceAsInputStream);

            if (!configuration.isReturnValidationResult()) {
                return source;
            }

            validationResult.setResult(ValidationResult.Result.VALID);

        } catch (SAXException e) {

            if (configuration.isThrowExceptionOnValidationFailure()||!configuration.isReturnValidationResult()) {
                throw new ValidationException(e.getMessage(), e);
            }
            validationResult.setResult(ValidationResult.Result.INVALID);
            validationResult.setException(e);

        } catch (IOException e) {
            if (configuration.isThrowExceptionOnValidationFailure()||!configuration.isReturnValidationResult()) {
                throw new ValidationException(e.getMessage(), e);
            }
            validationResult.setResult(ValidationResult.Result.INVALID);
            validationResult.setException(e);
        } catch (ParserConfigurationException e) {
            if (configuration.isThrowExceptionOnValidationFailure()||!configuration.isReturnValidationResult()) {
                throw new ValidationException(e.getMessage(), e);
            }
            validationResult.setResult(ValidationResult.Result.INVALID);
            validationResult.setException(e);
        }
        return validationResult;
    }

    private ByteArrayInputStream createSourceAsBytes(SOURCE xml) {
        if (sourceToByteArrayInputStreamConverter == null && xml instanceof String) {
            return new ByteArrayInputStream(((String) xml).getBytes());
        } else {
            return sourceToByteArrayInputStreamConverter.convert(xml);
        }
    }

    @Override
    public String getConfiguredResourceId() {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId) {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public XMLValidatorConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(XMLValidatorConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void startManagedResource() {

    }

    @Override
    public void stopManagedResource() {

    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager) {

    }

    @Override
    public boolean isCriticalOnStartup() {
        return false;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup) {

    }
}