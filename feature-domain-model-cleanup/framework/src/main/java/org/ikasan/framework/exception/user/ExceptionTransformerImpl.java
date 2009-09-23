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
package org.ikasan.framework.exception.user;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.ikasan.common.CommonXSLTransformer;
import org.ikasan.common.Payload;
import org.ikasan.common.component.PayloadConverter;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.exception.ExceptionContext;
import org.ikasan.framework.exception.ResubmissionInfo;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * Concrete implementation of the exception transformer interface
 * 
 * @author Ikasan Development Team
 */
public class ExceptionTransformerImpl implements ExceptionTransformer
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(ExceptionTransformerImpl.class);

    /** We need an XML doc to seed the XSL transformation */
    private static String DUMMY_XML_DOC = "<?xml version=\"1.0\"?><dummy/>";

    /** Module name */
    private String moduleName;
    
    /** XML document builder factory */
    private DocumentBuilderFactory documentBuilderFactory;

    /** Common XSL Transformer */
    private CommonXSLTransformer xslt;

    /** Payload converter */
    private PayloadConverter payloadConverter;

    /** Payload class */
    private Class<? extends Payload> payloadClass;

    /**
     * Constructor
     * 
     * @param moduleName The name of the module
     * @param documentBuilderFactory The document builder factory used for creating the XML
     * @param xslt The XSLT to use
     * @param payloadFactory The payload factory to use
     */
    public ExceptionTransformerImpl(final String moduleName, final DocumentBuilderFactory documentBuilderFactory, final CommonXSLTransformer xslt,
            final PayloadFactory payloadFactory)
    {
        this.moduleName = moduleName;
        this.documentBuilderFactory = documentBuilderFactory;
        this.xslt = xslt;
        this.payloadConverter = new PayloadConverter(payloadFactory.getPayloadImplClass(), payloadFactory);
        this.payloadClass = payloadFactory.getPayloadImplClass();
    }

    public String transform(ExceptionContext exceptionContext, final ExternalExceptionDefinition externalExceptionDef)
            throws TransformerException
    {
        // Get map of transformer parameters
        Map<String, String> xslParams = setTransformConfiguration(exceptionContext, externalExceptionDef);
        // Set the parameters on the transformer
        xslt.setParameters(xslParams);
        try
        {
            // Invoke the transform
            String xml = xslt.transformToString(DUMMY_XML_DOC);
            DocumentBuilder documentBuilder = this.documentBuilderFactory.newDocumentBuilder();
            documentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
            return xml;
        }
        catch (ParserConfigurationException e)
        {
            throw new TransformerException(e);
        }
        catch (SAXException e)
        {
            throw new TransformerException(e);
        }
        catch (IOException e)
        {
            throw new TransformerException(e);
        }
    }

    /**
     * Set all transformation map configurations from here.
     * 
     * @param exceptionContext The context
     * @param externalExceptionDef The external definition
     * @return Map of transform configurations
     */
    private Map<String, String> setTransformConfiguration(ExceptionContext exceptionContext,
            ExternalExceptionDefinition externalExceptionDef)
    {
        Map<String, String> params = new HashMap<String, String>();
        // Set class based parameters
        params.put("subsystem", this.moduleName);
        // Set external exception source parameters
        if (externalExceptionDef.getMajorCode() != null)
        {
            params.put("major", externalExceptionDef.getMajorCode().toString());
        }
        if (externalExceptionDef.getMinorCode() != null)
        {
            params.put("minor", externalExceptionDef.getMinorCode().toString());
        }
        if (externalExceptionDef.getReturnSystemRef() != null)
        {
            params.put("returnSystem", externalExceptionDef.getReturnSystemRef());
        }
        if (externalExceptionDef.getUserAction() != null)
        {
            params.put("possibleActions", externalExceptionDef.getUserAction().toString());
        }
        // Add Throwable sourced parameters
        // TODO - this may not be the same cause as we matched on earlier
        if (exceptionContext.getThrowable() != null)
        {
            params.put("error", exceptionContext.getThrowable().getMessage());
        }
        // Create an XStream instance for Java -> XML
        XStream xstream = new XStream(new XppDriver(new XmlFriendlyReplacer("$", "_")));
        String resubmissionInfo = getResubmissionInfo(xstream, exceptionContext.getComponentName());
        if (resubmissionInfo != null)
        {
            params.put("resubmissionInfo", resubmissionInfo);
        }
        // Event sourced data depends on whether we have an event
        Event event = exceptionContext.getEvent();
        if (event != null)
        {
        	
            params.put("timestamp", new Date(event.getTimestamp()).toString());
            params.put("timezone", "UTC");
            params.put("originalEvent", null);
            params.put("exceptionEvent", getExceptionEvent(xstream, exceptionContext.getEvent().getPayloads(),
                externalExceptionDef));
        }
        return params;
    }

    /**
     * Get the re-submission information
     * 
     * @param xstream The XStream representation of the message
     * @param componentName The name of the component we are re-submitting to
     * 
     * @return re-submission information
     */
    private String getResubmissionInfo(XStream xstream, String componentName)
    {
        ResubmissionInfo resubmissionInfo = new ResubmissionInfo("", "", this.moduleName, componentName);
        // Transform incoming object into XML
        xstream.alias("resubmissionInfo", ResubmissionInfo.class);
        String xmlFragment = xstream.toXML(resubmissionInfo);
        logger.info("Completed creation of resubmissionInfo XML.");
        return xmlFragment;
    }

    

    /**
     * Returns the payloads as a String exceptionEvent
     * 
     * @param xstream The XStream representing the event
     * @param payloads The list of payloads
     * @param externalExceptionDef The external exception definition
     * @return exception event
     */
    private String getExceptionEvent(XStream xstream, List<Payload> payloads,
            ExternalExceptionDefinition externalExceptionDef)
    {
        logger.info("Creating exceptionEvent XML..."); //$NON-NLS-1$
        if (externalExceptionDef.getMaxPayloadSize().intValue() > 0)
        {
            managePayloadSize(payloads, externalExceptionDef);
        }
        xstream.registerConverter(payloadConverter, 0);
        xstream.alias("payload", payloadClass);
        xstream.alias("exceptionEvent", List.class);

        String xmlFragment = xstream.toXML(payloads);
        logger.info("Completed creation of exceptionEvent XML.");
        return xmlFragment;
    }

   

    /**
     * Utility method for calculating payload sizes to see if they require truncation.
     * 
     * TODO - This should be moved to Payload class.
     * 
     * @param payloads List of payloads to manage
     * @param externalExceptionDef The external excpetion definition
     */
    private void managePayloadSize(List<Payload> payloads, ExternalExceptionDefinition externalExceptionDef)
    {
        // No truncation required
        if (externalExceptionDef.getMaxPayloadSize().intValue() <= 0)
        {
            return;
        }
        // Calculate the total payload size
        long totalSizeOfPayloads = 0;
        for (Payload payload : payloads)
        {
            totalSizeOfPayloads += payload.getSize();
        }
        // If the total payload size is too big then truncate payloads as needed
        if (totalSizeOfPayloads > externalExceptionDef.getMaxPayloadSize().intValue())
        {
            int maxIndividualPayloadSize = externalExceptionDef.getMaxPayloadSize().intValue();
            if (payloads.size() > 0)
            {
                maxIndividualPayloadSize = externalExceptionDef.getMaxPayloadSize().intValue() / payloads.size();
            }
            truncatePayloads(payloads, maxIndividualPayloadSize);
        }
    }

    /**
     * Utility method for truncating payloads based on a given list and size.
     * 
     * TODO - This should be moved to Payload class.
     * 
     * @param payloads The list of payloads to truncate
     * @param size The size to truncate at
     */
    private void truncatePayloads(List<Payload> payloads, int size)
    {
        byte[] trimmedPayload = new byte[size];
        String tempPayloadContent = null;
        for (Payload payload : payloads)
        {
            if (payload.getContent().length > size)
            {
                // Force the truncation
                logger.debug("payload length = [" + payload.getContent().length + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                tempPayloadContent = new String(payload.getContent());
                tempPayloadContent = tempPayloadContent.substring(0, size);
                trimmedPayload = tempPayloadContent.getBytes();
                logger.debug("trimmedPayload length = [" + trimmedPayload.length + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                payload.setContent(trimmedPayload);
                logger.info("The content of Payload: [" + payload.getId() //$NON-NLS-1$
                        + "] was trimmed to [" + size //$NON-NLS-1$
                        + "] bytes, as it was too large to handle."); //$NON-NLS-1$
            }
        }
    }
}
