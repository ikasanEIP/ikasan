/*
 * $Id: FileSystemMessageDao.java 2728 2009-12-11 20:40:52Z magicduncan $
 * $URL: https://open.jira.com/svn/IKASAN/trunk/tools/TextMessagePublisher/core/src/main/java/org/ikasan/tools/messaging/dao/FileSystemMessageDao.java $
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
package org.ikasan.tools.messaging.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.ikasan.tools.messaging.model.MessageWrapper;

public class BoundedMemoryMessageRepository implements MessageRepository {

	private int maximumMessages = 10;
	
	private BlockingQueue<MessageWrapper> receivedMessages = new LinkedBlockingQueue<MessageWrapper>(maximumMessages);

	
	public MessageWrapper getMessage(String messageId) {
		for (MessageWrapper messageWrapper : receivedMessages){
			if (messageWrapper.getMessageId().equals(messageId)){
				return messageWrapper;
			}
		}
		return null;
	}

	public List<String> getMessages() {
		List<String> messageIds = new ArrayList<String>();
		for (MessageWrapper messageWrapper : receivedMessages){
			messageIds.add(messageWrapper.getMessageId());
		}
		return messageIds;
	}

	public void save(MessageWrapper message) {
		if (receivedMessages.remainingCapacity()==0){
			receivedMessages.remove();
		}
		receivedMessages.add(message);
	}

}
