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
package org.ikasan.tools.messaging.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.log4j.Logger;
import org.ikasan.tools.messaging.dao.BoundedMemoryMessageDao;
import org.ikasan.tools.messaging.dao.FileSystemMessageDao;
import org.ikasan.tools.messaging.dao.MessageDao;
import org.ikasan.tools.messaging.destination.DestinationHandle;
import org.ikasan.tools.messaging.destination.discovery.DestinationDiscoverer;
import org.ikasan.tools.messaging.model.MapMessageWrapper;
import org.ikasan.tools.messaging.model.MessageWrapper;
import org.ikasan.tools.messaging.model.MessageWrapperFactory;
import org.ikasan.tools.messaging.model.TextMessageWrapper;
import org.ikasan.tools.messaging.serialisation.DefaultMessageXmlSerialiser;
import org.ikasan.tools.messaging.serialisation.MessageXmlSerialiser;
import org.ikasan.tools.messaging.subscriber.BaseSubscriber;
import org.ikasan.tools.messaging.subscriber.PersistingSubscriber;

public class DestinationServer {
	
	private Logger logger = Logger.getLogger(DestinationServer.class);

	
	private List<DestinationHandle> destinations = new ArrayList<DestinationHandle>();
	
	private ConnectionFactory connectionFactory;
	
	private MessageXmlSerialiser messageXmlSerialiser = new DefaultMessageXmlSerialiser();
	
	private Map<String,MessageDao> repositories = new HashMap<String,MessageDao>();
	
	
	public DestinationServer(DestinationDiscoverer destinationDiscoverer, ConnectionFactory connectionFactory){
		this.destinations = destinationDiscoverer.findDestinations();
		this.connectionFactory = connectionFactory;
	}
	
	
	
	public List<DestinationHandle> getDestinations() {
		return destinations;
	}

	
	public void publishTextMessage(String destinationPath, String messageText, int priority){
		getDestination(destinationPath).publishTextMessage(connectionFactory,  messageText, priority);
	}
	

	
	
	
	
	public void createSubscription(String subscriptionName, String destinationPath, String repositoryName, boolean simpleSubscription){
		MessageDao messageDao = repositories.get(repositoryName);
		if (simpleSubscription){
			messageDao = new BoundedMemoryMessageDao();
		}
		
		
		getDestination(destinationPath).createSubscription(subscriptionName, connectionFactory, messageDao);
	}
	
	public void destroyPersistingSubscription(String destinationPath, String subscriptionName){
		getDestination(destinationPath).destroySubscription(subscriptionName);
	}



	public DestinationHandle getDestination(String destinationPath) {
		for (DestinationHandle destinationHandle : destinations){
			if (destinationHandle.getDestinationPath().equals(destinationPath)){
				return destinationHandle;
			}
		}
		return null;
		
	}



	public MessageWrapper getMessage(String destinationPath,String subscriptionName, String messageId) {
		BaseSubscriber subscriber = getDestination(destinationPath).getSubscriptions().get(subscriptionName);
		return ((PersistingSubscriber)subscriber).getMessage(messageId);
	}



	public String getMessageAsXml(String destinationPath,String subscriptionName, String messageId) {
		MessageWrapper message = getMessage(destinationPath,subscriptionName, messageId);
		String messageXml = null;
		messageXml =  messageXmlSerialiser.toXml(message);
		
		return messageXml;
	}



	public void publishXmlMessage(String destinationPath, String xml,
			int priority) {
		
		Object messageObject = messageXmlSerialiser.getMessageObject(xml);
		if (messageObject instanceof TextMessageWrapper){
			getDestination(destinationPath).publishTextMessage(connectionFactory,  ((TextMessageWrapper)messageObject).getText(), priority);
		} 
		
		else if (messageObject instanceof MapMessageWrapper){
			getDestination(destinationPath).publishMapMessage(connectionFactory,  ((MapMessageWrapper)messageObject).getMap(), priority);
		}
		else{
			throw new RuntimeException("Unknown message object["+messageObject+"]");
		}

		
	}



	public Map<String,MessageDao> getRepositories() {
		return repositories;
	}



	public void createFileSystemRepository(String name, String fileSystemPath) {
		repositories.put(name,new FileSystemMessageDao(new File(fileSystemPath), messageXmlSerialiser));
	}
	
	
}
