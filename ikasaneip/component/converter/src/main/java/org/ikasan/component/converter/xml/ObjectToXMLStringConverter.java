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
package org.ikasan.component.converter.xml;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.support.SaxResourceUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Marshall the incoming Object into an XML String.
 * 
 * @author Ikasan Development Team
 */
public class ObjectToXMLStringConverter implements Converter<Object, Object>, ConfiguredResource<XmlConfiguration>
{
    /** class logger */
    private static Logger logger = Logger.getLogger(ObjectToXMLStringConverter.class);

    /** JAXB Context is thread-safe so keep handle to the context */
    private JAXBContext context;
 
    /** configuration id */
    private String configuredResourceId;
    
    /** configuration */
    private XmlConfiguration xmlConfiguration;

    /** schema */
    private Schema schema;

    /** whether to route or throw exception on validation failure */
    private boolean routeOnValidationException;

    /**
     * Constructor
     * @param context
     */
    public ObjectToXMLStringConverter(JAXBContext context)
    {
        this.context = context;
        if(context == null)
        {
            throw new IllegalArgumentException("JAXBContext cannot be 'null'");
        }
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.spec.component.transformation.Converter#convert(java.lang.Object)
     */
    @Override
    public Object convert(Object object) throws TransformationException
    {
        try
        {
            Marshaller marshaller = this.context.createMarshaller();
            StringWriter writer = new StringWriter();
            
            if(this.xmlConfiguration.getSchemaLocation() != null)
            {
                marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, this.xmlConfiguration.getSchemaLocation());
            }

            if(this.xmlConfiguration.isValidate())
            {
                marshaller.setSchema(this.schema);
            }
            else
            {
                marshaller.setSchema(null);
            }

            marshaller.setEventHandler(new XmlValidationEventHandler());
            marshaller.marshal(object, writer);
            String marshalledXml = writer.toString();
            if(logger.isDebugEnabled())
            {
                logger.debug("Marshalled XML [" + marshalledXml + "]");
            }

            return marshalledXml;
        }
        catch (XmlValidationException e)
        {
            String failedXml = getFailedMessageXml(object);
            e.setFailedEvent(failedXml);
            if(this.routeOnValidationException)
            {
                logger.info("Failed XML Validation on[" + failedXml + "]", e);
                return e;
            }

            throw new TransformationException(e);
        }
        catch (JAXBException e)
        {
            String failedXml = getFailedMessageXml(object);
            if(e.getLinkedException() instanceof XmlValidationException)
            {
                XmlValidationException validationException = (XmlValidationException) e.getLinkedException();
                validationException.setFailedEvent(failedXml);

                if(this.routeOnValidationException)
                {
                    logger.info("Failed XML Validation on[" + failedXml + "]", e);
                    return validationException;
                }
            }
            
            throw new TransformationException(e);
        }
    }

    /**
     * Load the XSD and return the Source. If not specified then return null.
     * @param schema
     * @return
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     */
    private Schema loadSchema(String schema) throws IOException, SAXException
    {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        if(schema == null)
        {
            return schemaFactory.newSchema();
        }
        
        Resource resource = new ClassPathResource(schema);
        InputSource inputSource = SaxResourceUtils.createInputSource(resource);
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
        Source schemaSource = new SAXSource(xmlReader, inputSource);
        return schemaFactory.newSchema(schemaSource);
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.spec.configuration.ConfiguredResource#getConfiguration()
     */
    @Override
    public XmlConfiguration getConfiguration()
    {
        return this.xmlConfiguration;
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.configuration.ConfiguredResource#getConfiguredResourceId()
     */
    @Override
    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.configuration.ConfiguredResource#setConfiguration(java.lang.Object)
     */
    @Override
    public void setConfiguration(XmlConfiguration xmlConfiguration)
    {
        this.xmlConfiguration = xmlConfiguration;
        this.routeOnValidationException = this.xmlConfiguration.isRouteOnValidationException();

        // try to load the schema
        try
        {
            this.schema = this.loadSchema(this.xmlConfiguration.getSchema());
        }
        catch(SAXException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.configuration.ConfiguredResource#setConfiguredResourceId(java.lang.String)
     */
    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    private String getFailedMessageXml(Object object)
    {
        try
        {
            Marshaller marshaller = this.context.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.setEventHandler(new XmlValidationEventHandler());
            marshaller.marshal(object, writer);
            return writer.toString();
        }
        catch(XmlValidationException e)
        {
            return null;
        }
        catch(JAXBException e)
        {
            return null;
        }
    }
    
    /**
     * Validating parser callbacks
     */
    private class XmlValidationEventHandler implements ValidationEventHandler
    {

        /* (non-Javadoc)
         * @see javax.xml.bind.ValidationEventHandler#handleEvent(javax.xml.bind.ValidationEvent)
         */
        public boolean handleEvent(ValidationEvent event)
        {
            if(ValidationEvent.WARNING == event.getSeverity())
            {
                logger.warn( getLogEntry(event) );
                return true;
            }
            else if(ValidationEvent.ERROR == event.getSeverity())
            {
                throw new XmlValidationException(event);
            }
            else if(ValidationEvent.FATAL_ERROR == event.getSeverity())
            {
                throw new XmlValidationException(event);
            }
            else
            {
                throw new XmlValidationException(event);
            }
        }

        private String getLogEntry(ValidationEvent event)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(event.getMessage());
            sb.append(event.getLinkedException());
            Object failedObject = event.getLocator().getObject();
            if(failedObject != null)
            {
                sb.append(failedObject.toString());
            }
            return sb.toString();
        }
    }

}
