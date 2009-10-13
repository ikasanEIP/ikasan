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
package org.ikasan.demo.businesserror.eai;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.ikasan.demo.businesserror.eai.converter.BusinessErrorConverter;
import org.ikasan.demo.businesserror.model.BusinessError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class JmsEaiAdapter implements EaiAdapter, MessageListener{
	
	private JmsTemplate jmsTemplate;
	
	private Destination outgoingDestination;
	
	private BusinessErrorListener businessErrorListener;
	
	private Logger logger = Logger.getLogger(JmsEaiAdapter.class);
	
	private String originatingSystem;
	

	
	private BusinessErrorConverter<String> errorConverter;

	
	public void onMessage(Message message) {
		logger.info("onMessage called with["+message+"]");
		
		// TODO get the error out of the message and add push it to the DAO
		TextMessage textMessage = (TextMessage)message;

		
		

		BusinessError businessError = null;
		try {
			String text = textMessage.getText();
			businessError = errorConverter.convertFrom(text, originatingSystem);
			
		
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		businessErrorListener.onBusinessError(businessError);
	}

	public void postBusinessError(final BusinessError businessError) {
		//publish the business error back to the resubmission topic
		logger.info("called with businessError["+businessError+"]");
		jmsTemplate.send(outgoingDestination, new MessageCreator() {
			
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(errorConverter.convertTo(businessError));
			}
		});
		
	}

	@Autowired
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.jmsTemplate = new JmsTemplate(connectionFactory);
	}
	
	@Autowired
	public void setOutgoingDestination(Destination outgoingDestination){
		this.outgoingDestination = outgoingDestination;
	}

	@Autowired
	public void setBusinessErrorListener(
			BusinessErrorListener businessErrorListener) {
		this.businessErrorListener = businessErrorListener;
	}
	
	@Autowired
	public void setErrorConverter(BusinessErrorConverter<String> errorConverter){
		this.errorConverter = errorConverter;
	}
	
	public String getOriginatingSystem() {
		return originatingSystem;
	}

	public void setOriginatingSystem(String originatingSystem) {
		this.originatingSystem = originatingSystem;
	}
	


}
