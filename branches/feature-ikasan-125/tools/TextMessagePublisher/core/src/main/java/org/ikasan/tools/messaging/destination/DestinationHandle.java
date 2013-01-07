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
package org.ikasan.tools.messaging.destination;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.ikasan.tools.messaging.model.MapMessageWrapper;
import org.ikasan.tools.messaging.model.MessageWrapper;
import org.ikasan.tools.messaging.model.TextMessageWrapper;
import org.ikasan.tools.messaging.subscriber.BaseSubscriber;
import org.ikasan.tools.messaging.subscriber.MessageWrapperListenerSubscriber;
import org.ikasan.tools.messaging.subscriber.listener.MessageWrapperListener;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class DestinationHandle implements Comparable<DestinationHandle> {
	
	private String destinationPath;
	
	private Destination destination;
	
	private ConnectionFactory connectionFactory;
	
	
	private Map<String, BaseSubscriber> subscriptions = new HashMap<String, BaseSubscriber>();

	public Map<String, BaseSubscriber> getSubscriptions() {
		return subscriptions;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public DestinationHandle(String destinationPath, Destination destination, ConnectionFactory connectionFactory) {
		super();
		this.destinationPath = destinationPath;
		this.destination = destination;
		this.connectionFactory = connectionFactory;
	}

	
	public void publishMessage(final MessageWrapper messageWrapper, int priority) {
		
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        
		// explicit QoS must be set when defined priority, deliveryMode, or timeToLive
        jmsTemplate.setPriority(priority);
        jmsTemplate.setExplicitQosEnabled(true);
		try {
			jmsTemplate.send(destination, new MessageCreator() {
				
				public Message createMessage(Session session) throws JMSException {
					Message message = null;
					if (messageWrapper instanceof TextMessageWrapper){
						message =  session.createTextMessage(((TextMessageWrapper)messageWrapper).getText());
					} else if (messageWrapper instanceof MapMessageWrapper){
						message = session.createMapMessage();
						Map<String, Object> map = ((MapMessageWrapper) messageWrapper).getMap();
						for (String mapKey: map.keySet()){
							Object mapValue = map.get(mapKey);
							((MapMessage)message).setObject(mapKey, mapValue);
						}
					}
						
					
					return message;
				}
			});
		} catch (JmsException e) {
			throw new RuntimeException(e);
		} 

	}
	

	
	public void createSubscription(String subscriptionName, MessageWrapperListener messageListener){
		if (subscriptions.get(subscriptionName)!=null){
			throw new IllegalStateException("Subscriber already exists for ["+destinationPath+"]");
		}
		MessageWrapperListenerSubscriber subscription = new MessageWrapperListenerSubscriber(connectionFactory, destination,messageListener);
		subscriptions.put(subscriptionName, subscription);
	}
	
	public void destroySubscription(String subscriptionName) {
		BaseSubscriber subscriber = subscriptions.get(subscriptionName);
		if (subscriber!=null){
			subscriber.shutdown();
		}
		subscriptions.remove(subscriptionName);
		
	}

	public int compareTo(DestinationHandle other) {
		return this.getDestinationPath().compareTo(other.getDestinationPath());
	}
	

}
