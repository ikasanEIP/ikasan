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
package org.ikasan.framework.initiator.messagedriven;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
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
    /** Default Message Priority used by the Ikasan Raw Message Driven Initiator */
    private static final int DEFAULT_MESSAGE_PRIORITY = 4;

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
     * @see
     * org.ikasan.framework.initiator.messagedriven.JmsMessageDrivenInitiatorImpl#handleTextMessage(javax.jms.TextMessage
     * )
     */
    @Override
    protected Event handleTextMessage(TextMessage message) throws JMSException
    {
        Payload payload = payloadFactory.newPayload(message.getJMSMessageID(), message.getText().getBytes());
        //
        Event event = new Event(moduleName, name, message.getJMSMessageID(), payload);
        // Reuse the message's priority if we are configured to respect it
        if (respectPriority)
        {
            event.setPriority(message.getJMSPriority());
        }
        else
        {
            event.setPriority(DEFAULT_MESSAGE_PRIORITY);
        }
        return event;
    }



    @Override
    protected Logger getLogger()
    {
        return logger;
    }
    

    /**
     * Respect the priority of received messages by setting this on the Event
	 * @param respectPriority
	 */
	public void setRespectPriority(boolean respectPriority) {
		this.respectPriority = respectPriority;
	}

	/**
	 * Accessor for respectPriority
	 * @return respectPriority
	 */
	public boolean isRespectPriority() {
		return respectPriority;
	}
}
