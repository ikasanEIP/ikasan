package org.ikasan.tools.messaging.publisher;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.naming.NamingException;

import org.ikasan.tools.messaging.destination.JndiDestinationResolver;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class TextMessagePublisherImpl implements TextMessagePublisher {

	private JmsTemplate jmsTemplate;
	
	private JndiDestinationResolver jndiDestinationResolver;
	
	public TextMessagePublisherImpl(ConnectionFactory connectionFactory, JndiDestinationResolver jndiDestinationResolver){
		this.jmsTemplate = new JmsTemplate(connectionFactory);
		this.jndiDestinationResolver = jndiDestinationResolver;
	}
	
	public void publishTextMessage(String destinationPath, final String messageText) {
		try {
			jmsTemplate.send(jndiDestinationResolver.getDestination(destinationPath), new MessageCreator() {
				
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(messageText);
				}
			});
		} catch (JmsException e) {
			throw new RuntimeException(e);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}

	}

}
