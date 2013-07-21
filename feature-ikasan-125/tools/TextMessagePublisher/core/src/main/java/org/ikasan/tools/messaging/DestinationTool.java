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
package org.ikasan.tools.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.tools.messaging.destination.DestinationHandle;
import org.ikasan.tools.messaging.destination.discovery.DestinationDiscoverer;
import org.ikasan.tools.messaging.model.MessageWrapper;
import org.ikasan.tools.messaging.model.TextMessageWrapper;
import org.ikasan.tools.messaging.repository.BoundedMemoryMessageRepository;
import org.ikasan.tools.messaging.repository.FileSystemMessageRepository;
import org.ikasan.tools.messaging.repository.MessageRepository;
import org.ikasan.tools.messaging.serialisation.DefaultMessageXmlSerialiser;
import org.ikasan.tools.messaging.serialisation.MessageXmlSerialiser;
import org.ikasan.tools.messaging.subscriber.BaseSubscriber;
import org.ikasan.tools.messaging.subscriber.MessageWrapperListenerSubscriber;
import org.ikasan.tools.messaging.subscriber.listener.PersistingMessageWrapperListener;

public class DestinationTool {
	
	private Logger logger = Logger.getLogger(DestinationTool.class);
	
	private List<DestinationHandle> destinations = new ArrayList<DestinationHandle>();

	
	private MessageXmlSerialiser messageXmlSerialiser = new DefaultMessageXmlSerialiser();
	
	private Map<String,MessageRepository> repositories = new HashMap<String,MessageRepository>();
	
	
	public DestinationTool(DestinationDiscoverer destinationDiscoverer){
		this.destinations = destinationDiscoverer.findDestinations();
	}
	
	
	
	public List<DestinationHandle> getDestinations() {
		return destinations;
	}

	
	public void publishTextMessage(String destinationPath, String messageText, int priority){
		TextMessageWrapper textMessageWrapper = new TextMessageWrapper(messageText, null);
		
		getDestination(destinationPath).publishMessage(textMessageWrapper, priority);
	}
	

	
	
	
	
	public void createSubscription(String subscriptionName, String destinationPath, String repositoryName){
		MessageRepository messageDao = null;
		if (repositoryName==null || "".equals(repositoryName)){
			messageDao = new BoundedMemoryMessageRepository(repositoryName);
		} else{
			messageDao = repositories.get(repositoryName);
		}
		
		
		getDestination(destinationPath).createSubscription(subscriptionName, new PersistingMessageWrapperListener(messageDao));
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
		MessageWrapperListenerSubscriber messageWrapperListenerSubscriber = (MessageWrapperListenerSubscriber)subscriber;
		PersistingMessageWrapperListener persistingMessageWrapperListener = (PersistingMessageWrapperListener)messageWrapperListenerSubscriber.getMessageListener();
		MessageRepository messageDao = persistingMessageWrapperListener.getRepository();
		
		return messageDao.getMessage(messageId);
	}



	public String getMessageAsXml(String destinationPath,String subscriptionName, String messageId) {
		MessageWrapper message = getMessage(destinationPath,subscriptionName, messageId);
		String messageXml = null;
		messageXml =  messageXmlSerialiser.toXml(message);
		
		return messageXml;
	}



	public void publishXmlMessage(String destinationPath, String xml,
			int priority) {
		
		MessageWrapper messageWrapper = messageXmlSerialiser.getMessageObject(xml);

			getDestination(destinationPath).publishMessage(messageWrapper, priority);
	}



	public Map<String,MessageRepository> getRepositories() {
		return repositories;
	}



	public void createFileSystemRepository(String name, String fileSystemPath) {
		repositories.put(name,new FileSystemMessageRepository(name, fileSystemPath, messageXmlSerialiser));
	}
	
	
}
