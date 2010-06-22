/*
 * $Id
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
package org.ikasan.framework.initiator.messagedriven.jca;

import java.io.UnsupportedEncodingException;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.ikasan.common.MetaDataInterface;
import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.flow.Flow;

/**
 * A <code>JmsMessageDrivenInitiator</code> implementation that seeks to create and fire new <code>Event</code>s based
 * on raw JMS messages.
 * 
 * This implementation places no expectation on the incoming message data. Do not use this for deserialising existing
 * <code>Event</code>s, rather use the <code>EventMessageDrivenInitiator</code>
 * 
 * @author Ikasan Development Team
 */
public class RawMessageDrivenInitiator extends JmsMessageDrivenInitiatorImpl
{
    /**
     * Logger instance for this class
     */
    private Logger logger = Logger.getLogger(RawMessageDrivenInitiator.class);

    /**
     * Factory for constructing Payloads
     */
    protected PayloadFactory payloadFactory;

    /**
     * Respect the priority of received messages by setting this on the Event
     */
    private boolean respectPriority;

    // The character encoding of the incoming message
    private String incomingCharacterEncoding = null;

    /*
     * The intermediate character encoding to transition difficult encodings such as CP1252 to more ISO standard
     * encodings such as UTF-8
     * 
     * e.g. CP1252 --> ISO-88591 --> UTF-8
     */
    private String intermediateCharacterEncoding = null;

    // The outgoing encoding of the Event, UTF-8 by default
    private String outgoingCharacterEncoding = "UTF-8";
    
    /**
     * Set the character encoding of the incoming message
     * 
     * @param incomingCharacterEncoding
     */
    public void setIncomingCharacterEncoding(String incomingCharacterEncoding)
    {
        this.incomingCharacterEncoding = incomingCharacterEncoding;
    }

    /**
     * Set the intermediate character encoding used to transition difficult character encodings
     * 
     * @param intermediateCharacterEncoding
     */
    public void setIntermediateCharacterEncoding(String intermediateCharacterEncoding)
    {
        this.intermediateCharacterEncoding = intermediateCharacterEncoding;
    }

    /**
     * Set the character encoding on the outgoing Event
     * 
     * @param outgoingCharacterEncoding
     */
    public void setOutgoingCharacterEncoding(String outgoingCharacterEncoding)
    {
        this.outgoingCharacterEncoding = outgoingCharacterEncoding;
    }

    /**
     * Constructor
     * 
     * @param moduleName - name of the module
     * @param name - name of this initiator
     * @param flow - flow to invoke
     * @param payloadFactory - means for creating new <code>Payload</code>s
     */
    public RawMessageDrivenInitiator(String moduleName, String name, Flow flow, PayloadFactory payloadFactory)
    {
        super(moduleName, name, flow);
        this.payloadFactory = payloadFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.ikasan.framework.initiator.messagedriven.jca.
     * JmsMessageDrivenInitiatorImpl#handleTextMessage(javax.jms.TextMessage )
     */
    @Override
    protected Event handleTextMessage(TextMessage message) throws JMSException
    {
        // TODO This will be corrected in the 0.8.0+ code base, do not merge
        // this change in!
        byte[] characterEncodedContent;
        try
        {
            String encodedMessage = null;
            byte[] incomingMessageBytes = null;
            // Get the message using the incoming character encoding, else use the default platform character encoding
            if (this.incomingCharacterEncoding != null && !this.incomingCharacterEncoding.equals(""))
            {
                incomingMessageBytes = message.getText().getBytes(this.incomingCharacterEncoding);
                encodedMessage = new String(incomingMessageBytes, this.incomingCharacterEncoding);
            }
            else
            {
                incomingMessageBytes = message.getText().getBytes();
                encodedMessage = new String(incomingMessageBytes);
            }

            // If supplied, use the intermediate encoding step
            if (this.intermediateCharacterEncoding != null && !this.intermediateCharacterEncoding.equals(""))
            {
                byte[] intermediateMessageBytes = encodedMessage.getBytes(this.intermediateCharacterEncoding);
                encodedMessage = new String(intermediateMessageBytes, this.intermediateCharacterEncoding);
            }
            
            // Encode the outgoing data for the Event, use UTF-8 as a default even if we are supplied a null            
            byte[] outgoingMessageBytes = null;
            if (this.outgoingCharacterEncoding != null && !this.outgoingCharacterEncoding.equals(""))
            {
                outgoingMessageBytes = encodedMessage.getBytes(this.outgoingCharacterEncoding);
                encodedMessage = new String(outgoingMessageBytes, this.outgoingCharacterEncoding);
                characterEncodedContent = encodedMessage.getBytes(this.outgoingCharacterEncoding);
            }
            else
            {
                outgoingMessageBytes = encodedMessage.getBytes("UTF-8");
                encodedMessage = new String(outgoingMessageBytes, "UTF-8");
                characterEncodedContent = encodedMessage.getBytes("UTF-8");
            }
        }
        catch (UnsupportedEncodingException e)
        {
            // Hardly ideal to throw a JMSException at this point, but it
            // matches the method signature
            // Fix if we remain with the 0.7.x code base (unlikely)
            throw new JMSException("UnsupportedEncoding");
        }
        // From this point forward, this is roughly what the 'old' code would
        // have done with a TextMessage
        Payload payload = payloadFactory.newPayload(MetaDataInterface.UNDEFINED, Spec.TEXT_XML,
            MetaDataInterface.UNDEFINED, characterEncodedContent);
        Event event = new Event(moduleName, name);
        // re-use the message's priority if we are configured to respect it
        if (respectPriority)
        {
            event.setPriority(new Integer(message.getJMSPriority()));
        }
        event.setPayload(payload);
        return event;
    }

    @Override
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Respect the priority of received messages by setting this on the Event
     * 
     * @param respectPriority
     */
    public void setRespectPriority(boolean respectPriority)
    {
        this.respectPriority = respectPriority;
    }

    /**
     * Accessor for respectPriority
     * 
     * @return respectPriority
     */
    public boolean isRespectPriority()
    {
        return this.respectPriority;
    }
}
