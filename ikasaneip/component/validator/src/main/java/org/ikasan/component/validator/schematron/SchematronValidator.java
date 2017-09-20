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
package org.ikasan.component.validator.schematron;

import net.sf.saxon.lib.StandardURIResolver;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.component.validator.ValidationException;
import org.ikasan.component.validator.ValidationResult;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

/**
 * Validator implementation using pre-compiled XSLT Schematron rules.
 *
 * There are two ways to use this validator:
 *   1. Set throwExceptionOnValidationFailure to true in the configuration, and handle the Exception thrown on failure,
 *      or extract the original document downstream from the ValidationResult
 *   2. Use a router component after this to route successful (ValidationResult.Result.VALID) down one route and
 *      failed validations (ValidationResult.Result.INVALID) down another
 *
 * In all cases (but not the thrown exception case obviously) the original document is stored in the
 * returned ValidationResult via the getSource() method
 *
 * This class implements ManagedResource in order to cache any XSLT documents. Stopping and starting this component
 * will re-initialise this cache
 */
public class SchematronValidator implements Broker<Document, ValidationResult<Document, Document>>, ManagedResource, ConfiguredResource<SchematronValidatorConfiguration>
{
    private static final Logger logger = LoggerFactory.getLogger(SchematronValidator.class);

    private String configurationId;

    private SchematronValidatorConfiguration configuration;

    /** an optional URIResolver used to resolve import/include statements in the XSLT
        defaults to Saxons StandardURIResolver if left null */
    private URIResolver uriResolver;

    private Templates templates;

    private Transformer transformer;

    private DocumentBuilder documentBuilder;

    private Jaxb2Marshaller marshaller;


    @Override
    public ValidationResult<Document, Document> invoke(Document document) throws EndpointException
    {
        ValidationResult<Document, Document> validationResult = new ValidationResult<>();
        validationResult.setSource(document);
        if (configuration.isSkipValidation())
        {
            validationResult.setResult(ValidationResult.Result.VALID);
            return validationResult;
        }

        //Transformer transformer;
        /*try
        {
            transformer = templates.newTransformer();
        }
        catch (TransformerConfigurationException e)
        {
            // shouldn't happen since we cached the template
            throw new ValidationException("Cannot create XSLT transformer", e);
        }*/
        //transformer.setURIResolver(uriResolver);
        try
        {
            Source source = new DOMSource(document);
            source.setSystemId(configuration.getSchematronUri());
            // document to hold the result of the transform
            Document svrlDocument = documentBuilder.newDocument();
            // perform the transform
            transformer.transform(source, new DOMResult(svrlDocument));
            validationResult.setRawResult(svrlDocument);
            // unmarshal result into pojo for ease of processing
            Object o = marshaller.unmarshal(new DOMSource(svrlDocument));
            SchematronOutputType schematronOutputType;
            if (o instanceof JAXBElement)
            {
                schematronOutputType = (SchematronOutputType)((JAXBElement) o).getValue();
            }
            else
            {
                schematronOutputType = (SchematronOutputType)o;
            }
            // process the result
            processValidationResult(schematronOutputType, validationResult);
            if (configuration.isThrowExceptionOnValidationFailure() && validationResult.getResult().equals(ValidationResult.Result.INVALID))
            {
                throw new ValidationException("Validation failed: " + validationResult.getFailureReason() );
            }
        }
        catch (TransformerException e)
        {
            if (configuration.isThrowExceptionOnValidationFailure())
            {
                throw new ValidationException("Transformer exception", e);
            }
            validationResult.setResult(ValidationResult.Result.INVALID);
            validationResult.setException(e);
        }
        return validationResult;
    }

    /*
      Loops through the result SchematronOutputType and checks failed rules against the configuration,
      setting the validationResult appropriately
     */
    protected void processValidationResult(SchematronOutputType schematronOutputType, ValidationResult validationResult)
    {
        boolean valid = true;
        StringBuilder sb = new StringBuilder();
        // loop through the fired rules and possible failed asserts
        for (Object object : schematronOutputType.getActivePatternAndFiredRuleAndFailedAssert())
        {
            if (object instanceof FailedAssert)
            {
                String assertId = ((FailedAssert) object).getId();
                if (configuration.getRulesToIgnore() != null && configuration.getRulesToIgnore().contains(assertId))
                {
                    logger.debug("Ignored failed Schematron assert rule: " + assertId);
                    validationResult.getIgnoredRules().add(assertId);
                }
                else
                {
                    sb.append(valid ? "Failed assert rule [" : ", ");
                    sb.append(assertId);
                    valid = false;
                }
            }
        }
        if (valid)
        {
            validationResult.setResult(ValidationResult.Result.VALID);
        }
        else
        {
            sb.append("] ");
            validationResult.setResult(ValidationResult.Result.INVALID);
            validationResult.setFailureReason(sb.toString());
        }
    }


    @Override
    public void startManagedResource()
    {

        if (uriResolver == null)
        {
            uriResolver = new StandardURIResolver();
        }
        // force Saxon as the Transformer, Schematron may not work with Xalan or JVM/JAXP internal
        TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
        try
        {
            templates = factory.newTemplates(uriResolver.resolve(configuration.getSchematronUri(),""));
            transformer = templates.newTransformer();
            transformer.setURIResolver(uriResolver);
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            marshaller = new Jaxb2Marshaller();
            marshaller.setContextPath("org.oclc.purl.dsdl.svrl");
        }
        catch (TransformerException e)
        {
            throw new RuntimeException("Cannot create XSLT Templates", e);
        }
        catch (ParserConfigurationException e)
        {
            throw new RuntimeException("Cannot create DocumentBuilder", e);
        }
    }

    @Override
    public void stopManagedResource()
    {
        templates = null;
        documentBuilder = null;
        marshaller = null;
    }


    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
    }

    @Override
    public boolean isCriticalOnStartup()
    {
        return true;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup)
    {
    }

    @Override
    public String getConfiguredResourceId()
    {
        return configurationId;
    }

    @Override
    public void setConfiguredResourceId(String id)
    {
        this.configurationId = id;
    }

    @Override
    public SchematronValidatorConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(SchematronValidatorConfiguration configuration)
    {
        this.configuration = configuration;
    }

    public URIResolver getUriResolver()
    {
        return uriResolver;
    }

    public void setUriResolver(URIResolver uriResolver)
    {
        this.uriResolver = uriResolver;
    }
}
