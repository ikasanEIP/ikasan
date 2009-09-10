 /* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.common.xml.serializer;

import org.ikasan.common.CommonExceptionType;
import org.ikasan.common.CommonRuntimeException;
import org.ikasan.common.Envelope;
import org.ikasan.common.Payload;
import org.ikasan.common.component.EnvelopeConverter;
import org.ikasan.common.component.PayloadConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.AbstractXmlDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * Envelope XML serialiser/deserialiser
 * 
 * @author Ikasan Development Team
 *
 */
public class EnvelopeXmlSerializer implements XMLSerializer<Envelope>
{
    /**
     * Implementation class for <code>Envelope</code>
     */
    private Class<? extends Envelope> envelopeClass;
    
    /**
     * Implementation class for <code>Payload</code>
     */
    private Class<? extends Payload> payloadClass;
    
    /**
     * Payload Converter
     */
    private PayloadConverter payloadConverter;
    
    /**
     * EnvelopeConverter
     */
    private EnvelopeConverter envelopeConverter;
    
    /**
     * Constructor 
     * 
     * @param payloadClass
     * @param envelopeClass 
     */
    public EnvelopeXmlSerializer(Class<? extends Payload> payloadClass, Class<? extends Envelope> envelopeClass)
    {
        super();
        this.payloadClass = payloadClass;
        this.payloadConverter = new PayloadConverter(payloadClass);
        
        this.envelopeClass = envelopeClass;
        this.envelopeConverter = new EnvelopeConverter(envelopeClass);
    }

    /* (non-Javadoc)
     * @see org.ikasan.common.xml.serializer.XMLSerializer#toObject(java.lang.String)
     */
    public Envelope toObject(String xml)
    {
        try
        {
            XStream xstream = getXstream(new DomDriver());
            return (Envelope)xstream.fromXML(xml);
        }
        catch(ConversionException e)
        {
            throw new CommonRuntimeException("Unable to convert the XML "  //$NON-NLS-1$
                    + "string [" + xml + "] into an Envelope instance. "  //$NON-NLS-1$//$NON-NLS-2$
                    + e.getMessage(), e, CommonExceptionType.ENVELOPE_INSTANTIATION_FAILED);
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.common.xml.serializer.XMLSerializer#toXml(java.lang.Object)
     */
    public String toXml(Envelope subject)
    {
        XppDriver driver = new XppDriver(new XmlFriendlyReplacer("$", "_"));  
        return getXstream(driver).toXML(subject);
    }
    
    /**
     * Get the Xstream for the envelope
     * 
     * @param driver
     * @return XStream
     */
    private XStream getXstream(AbstractXmlDriver driver)
    {
        XStream xstream = new XStream(driver);
        xstream.registerConverter(envelopeConverter, 0);
        xstream.registerConverter(payloadConverter, 0);
        xstream.alias("envelope", envelopeClass); 
        xstream.alias("payload", payloadClass); 
        return xstream;
    }
}
