/*
 * $Id: 
 * $URL:
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
package org.ikasan.framework.payload.serialisation;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.ikasan.common.Payload;
import org.ikasan.common.factory.PayloadFactory;

/**
 * Default <code>TextMessage</code> implementation of <code>JmsMessagePayloadSerialiser</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class TextMessagePayloadSerialiser implements
		JmsMessagePayloadSerialiser<TextMessage> {
	
	/**
	 * factory for creating Payloads
	 */
	private PayloadFactory payloadFactory;


	/* (non-Javadoc)
	 * @see org.ikasan.framework.payload.serialisation.JmsMessagePayloadSerialiser#getSupportedMessageType()
	 */
	public Class<? extends Message> getSupportedMessageType() {
		return TextMessage.class;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.payload.serialisation.JmsMessagePayloadSerialiser#supports(java.lang.Class)
	 */
	public boolean supports(Class<? extends Message> messageClass) {
		return TextMessage.class.isAssignableFrom(messageClass);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.payload.serialisation.JmsMessagePayloadSerialiser#toMessage(org.ikasan.common.Payload, javax.jms.Session)
	 */
	public TextMessage toMessage(Payload payload, Session session) throws JMSException {
		TextMessage textMessage = session.createTextMessage();
		//TODO, how do we handle the payload encoding??
        textMessage.setText(new String(payload.getContent()));

        //TODO do we need to set any of the QOS properties on the message?

		return textMessage;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.payload.serialisation.JmsMessagePayloadSerialiser#toPayload(javax.jms.Message)
	 */
	public Payload toPayload(TextMessage message) throws JMSException {

		//strictly speaking this method is not really necessary as the RawMessageDrivenInitiator already handles incoming text messages
		return  payloadFactory.newPayload(message.getJMSMessageID(),  message.getText().getBytes());
	}
	
	/**
	 * Allows the payload factory to be set, only necessary if we are deserialising
	 * 
	 * @param payloadFactory
	 */
	public void setPayloadFactory(PayloadFactory payloadFactory) {
		this.payloadFactory = payloadFactory;
	}

}
