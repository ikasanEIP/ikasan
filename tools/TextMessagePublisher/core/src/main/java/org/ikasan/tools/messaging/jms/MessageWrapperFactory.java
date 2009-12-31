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
package org.ikasan.tools.messaging.jms;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.ikasan.tools.messaging.model.MapMessageWrapper;
import org.ikasan.tools.messaging.model.MessageWrapper;
import org.ikasan.tools.messaging.model.TextMessageWrapper;

public class MessageWrapperFactory {
	
	private static Logger logger = Logger.getLogger(MessageWrapperFactory.class);

	public static MessageWrapper wrapMessage(Message message) throws JMSException{
		MessageWrapper result = null;
		
		Map<String, Object> properties = new HashMap<String, Object>();

		
		Enumeration propertyNames = message.getPropertyNames();
		while (propertyNames.hasMoreElements()){
			String propertyName = (String)propertyNames.nextElement();
			properties.put(propertyName,message.getObjectProperty(propertyName));
		}
		
		
		if (message instanceof TextMessage){
			result =  new TextMessageWrapper(((TextMessage) message).getText(), properties);
		} else if (message instanceof MapMessage ){
			MapMessage mapMessage = (MapMessage)message;
			
			
			Map<String, Object> map = new HashMap<String, Object>();
			Enumeration mapNames = mapMessage.getMapNames();
			while (mapNames.hasMoreElements()){
				String mapName = (String)mapNames.nextElement();
				map.put(mapName, mapMessage.getObject(mapName));
			}
			
			result =  new MapMessageWrapper(map, properties);
		} else{
			throw new RuntimeException("Unsupported message type:"+message);
		}
		
		String messageId = message.getJMSMessageID();
		messageId=cleanupMessageId(messageId);
		
		result.setMessageId(messageId);
		result.setTimestamp(message.getJMSTimestamp());
		
		
		
		
		Map<String, Object> messagingProperties = new HashMap<String, Object>();
		handleMessagingProperty(messagingProperties, message.getJMSCorrelationID(), "jmsCorrelationId");
		handleMessagingProperty(messagingProperties, message.getJMSDeliveryMode(), "jmsDeliveryMode");
		handleMessagingProperty(messagingProperties, message.getJMSDestination(), "jmsDestination");
		handleMessagingProperty(messagingProperties, message.getJMSExpiration(), "jmsExpiration");
		handleMessagingProperty(messagingProperties, message.getJMSMessageID(), "jmsMessageID");
		handleMessagingProperty(messagingProperties, message.getJMSPriority(), "jmsPriority");
		handleMessagingProperty(messagingProperties, message.getJMSRedelivered(), "jmsRedelivered");
		handleMessagingProperty(messagingProperties, message.getJMSReplyTo(), "jmsReplyTo");
		handleMessagingProperty(messagingProperties, message.getJMSTimestamp(), "jmsTimestamp");
		handleMessagingProperty(messagingProperties, message.getJMSType(), "jmsType");
		
		result.setMessagingProperties(messagingProperties);
		return result;
	}

	private static void handleMessagingProperty(
			Map<String, Object> messagingProperties, Object jmsObject,
			String jmsObjectName) {
		if (jmsObject!=null){
			messagingProperties.put(jmsObjectName, jmsObject.toString());
		}
	}

	private static String cleanupMessageId(String messageId) {
		// just for jboss mq
		return messageId.replaceAll(":", "");
	}
}
