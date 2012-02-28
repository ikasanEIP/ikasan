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
package org.ikasan.framework.payload.serialisation;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.ikasan.common.Payload;

/**
 * Serialisation/Deserialisation interface for converting a <code>Payload</code> to and from a JMS <code>Message</code>
 * 
 * @author Ikasan Development Team
 *
 * @param <T> - A class that extends Message
 */
public interface JmsMessagePayloadSerialiser<T extends Message> {

	/**
	 * Converts a Payload to a Message
	 * 
	 * @param payload - Payload to convert into a Message
	 * @param session - JMS Session we're in
	 * @return a Message
	 * @throws JMSException - JMS related exception
	 */
	public T toMessage(Payload payload, Session session) throws JMSException;
	
	/**
	 * Converts a Message to a Payload
	 * 
	 * @param message - Message to convert into a Payload
	 * @return a Payload
	 * @throws JMSException - JMS related exception
	 */
	public Payload toPayload(T message) throws JMSException;
	
	/**
	 * Indicates if a particular Message implementation is supported
	 * 
	 * @param messageClass - A class that extends Message
	 * @return true if the given message class is supported
	 */
	public boolean supports(Class<? extends Message> messageClass);
	
	/**
	 * Get the supported message type
	 * 
	 * @return the supported message type
	 */
	public Class<? extends Message> getSupportedMessageType();
}
