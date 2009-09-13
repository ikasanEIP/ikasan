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
package org.ikasan.common.factory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.ikasan.common.Envelope;
import org.ikasan.common.MetaDataInterface;
import org.ikasan.common.Payload;
import org.ikasan.common.component.EnvelopeOperationException;
import org.ikasan.common.component.PayloadOperationException;
import org.ikasan.common.component.UnknownMessageContentException;

/**
 * The default implementation class for the EnvelopeFactory
 * 
 * @author Ikasan Development Team
 */
public class EnvelopeFactoryImpl implements EnvelopeFactory
{
    /** The envelope implementation class key */
    public static String ENVELOPE_IMPL_CLASS = "envelopeImpl.class";

    /** The envelope implementation class */
    private Class<? extends Envelope> envelopeImplClass;

    /** JmsMessageFactory */
    private JMSMessageFactory jmsMessageFactory;

    /** The logger */
    private static Logger logger = Logger.getLogger(EnvelopeFactoryImpl.class);

    /**
     * Constructor
     * 
     * @param envelopeImplClass The envelope implementation class to use
     * @param jmsMessageFactory The JMS message factory to use
     */
    public EnvelopeFactoryImpl(Class<? extends Envelope> envelopeImplClass, JMSMessageFactory jmsMessageFactory)
    {
        super();
        this.envelopeImplClass = envelopeImplClass;
        this.jmsMessageFactory = jmsMessageFactory;
    }

    /**
     * Create a new instance of the Envelope for the incoming Payload
     * 
     * @param payload The payload to create the envelope from
     * @return Envelope
     */
    public Envelope newEnvelope(Payload payload)
    {
        logger.debug("Instantiating envelope based on class [" //$NON-NLS-1$
                + this.envelopeImplClass + "]"); //$NON-NLS-1$
        Class<?>[] paramTypes = { Payload.class };
        Object[] params = { payload };
        return (Envelope) ClassInstantiationUtils.instantiate(this.envelopeImplClass, paramTypes, params);
    }

    /**
     * Create a new instance of the Envelope for incoming Payload List
     * 
     * @param payloads The payloads to create the envelope from
     * @return Envelope
     */
    public Envelope newEnvelope(List<Payload> payloads)
    {
        logger.debug("Instantiating envelope based on class [" //$NON-NLS-1$
                + this.envelopeImplClass + "]"); //$NON-NLS-1$
        Class<?>[] paramTypes = { List.class };
        Object[] params = { payloads };
        return (Envelope) ClassInstantiationUtils.instantiate(this.envelopeImplClass, paramTypes, params);
    }

    /**
     * Set the envelope concrete implementation class name.
     * 
     * @param envelopeImplClass the envelopeImplClass to set
     */
    public void setEnvelopeImplClass(final Class<? extends Envelope> envelopeImplClass)
    {
        this.envelopeImplClass = envelopeImplClass;
        logger.debug("Setting envelopeImplClass [" //$NON-NLS-1$
                + this.envelopeImplClass + "]"); //$NON-NLS-1$
    }

    /**
     * Get the envelope concrete implementation class
     * 
     * @return the envelopeImplClass
     */
    public Class<? extends Envelope> getEnvelopeImplClass()
    {
        logger.debug("Getting envelopeImplClass [" //$NON-NLS-1$
                + this.envelopeImplClass + "]"); //$NON-NLS-1$
        return this.envelopeImplClass;
    }

    /**
     * Converts a message into an Envelope
     * 
     * @param message The JMS Message to create the enveloper from
     * @return Envelope 
     * @throws UnknownMessageContentException Exception if the message content is unknown
     * @throws EnvelopeOperationException Exception if there is a problem, with creating the envelope
     * @throws PayloadOperationException Exception if there is a problem with creating the payload
     * @throws JMSException Exception if there is a JMS related problem
     */
    public Envelope fromMessage(Message message) throws UnknownMessageContentException, EnvelopeOperationException,
            PayloadOperationException, JMSException
    {
        List<Payload> payloads = jmsMessageFactory.fromMessage(message);
        Envelope envelope = newEnvelope(payloads);
        // Get the payload based on the JMS implementation
        if (message instanceof TextMessage)
        {
            logger.debug("Retrieving envelope from JMS TextMessage..."); //$NON-NLS-1$
            TextMessage tm = (TextMessage) message;
            // Restore id, name, spec and srcSystem from the JMS properties.
            // Not a great use of properties, but convenient
            String prop = tm.getStringProperty(MetaDataInterface.ID);
            if (prop != null)
            {
                envelope.setId(prop);
            }
            prop = tm.getStringProperty(MetaDataInterface.NAME);
            if (prop != null)
            {
                envelope.setName(prop);
            }
            prop = tm.getStringProperty(MetaDataInterface.SRC_SYSTEM);
            if (prop != null)
            {
                envelope.setSrcSystem(prop);
            }
            prop = tm.getStringProperty(MetaDataInterface.SPEC);
            if (prop != null)
            {
                envelope.setSpec(prop);
            }
            return envelope;
        }
        else if (message instanceof MapMessage)
        {
            logger.debug("Retrieving envelope from JMS MapMessage..."); //$NON-NLS-1$
            MapMessage mm = (MapMessage) message;
            String key = null;
            String keyName = null;
            Enumeration<?> mapMessageEnum = mm.getMapNames();
            // TODO - improve performance by removing non-envelope entries
            // iterate through all map entries
            while (mapMessageEnum.hasMoreElements())
            {
                // Get the key of each map entry
                key = (String) mapMessageEnum.nextElement();
                if (key.startsWith(MetaDataInterface.ENVELOPE_PREFIX))
                {
                    // Get length of payloadLiteral
                    int envelopeLiteral = MetaDataInterface.ENVELOPE_PREFIX.length();
                    keyName = key.substring(envelopeLiteral);
                    // Establish the payload number within the mapMessage
                    try
                    {
                        BeanInfo info = Introspector.getBeanInfo(envelope.getClass(), Object.class);
                        for (PropertyDescriptor pd : info.getPropertyDescriptors())
                        {
                            if (keyName.equals(pd.getName()))
                            {
                                // Get the object from the MapMessage
                                Object obj = mm.getObject(key);
                                if (obj instanceof Boolean || obj instanceof Byte || obj instanceof Short
                                        || obj instanceof Character || obj instanceof Integer || obj instanceof Long
                                        || obj instanceof Float || obj instanceof Double || obj instanceof String
                                        || obj instanceof byte[])
                                {
                                    logger.debug("Object primative is [" //$NON-NLS-1$
                                            + obj.getClass().getName() + "]"); //$NON-NLS-1$
                                }
                                else
                                {
                                    throw new JMSException("Unsupported class [" //$NON-NLS-1$
                                            + obj.getClass().getName() + "] found in JMS MapMessage"); //$NON-NLS-1$
                                }
                                pd.getWriteMethod().invoke(envelope, obj);
                                break;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        // This is very bad
                        throw new EnvelopeOperationException("Unable to extract " //$NON-NLS-1$
                                + "envelope from incoming JMS MapMessage. " //$NON-NLS-1$
                                + "Problem invoking setter for [" //$NON-NLS-1$
                                + keyName + "] ", e); //$NON-NLS-1$
                    }
                }
                else
                {
                    logger.debug("Ignoring key [" + key + "]..."); //$NON-NLS-1$//$NON-NLS-2$
                }
            }
        }
        else
        {
            String msg = "Received unsupported JMS Message Type."; //$NON-NLS-1$
            throw new JMSException(msg);
        }
        return envelope;
    }
}
