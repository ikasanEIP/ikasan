 /* 
 * $Id$
 * $URL$
 *
 * =============================================================================
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
 * =============================================================================
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
