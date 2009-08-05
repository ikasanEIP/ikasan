package org.ikasan.demo.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class JmsTextMessagePublisher {

	
    private JmsTemplate jmsTemplate;



    public JmsTextMessagePublisher(JmsTemplate jmsTemplate) {
		super();
		this.jmsTemplate = jmsTemplate;
	}



	public void publishTextMessage(Destination destination, final String messageText, final int priority) {
      try
    {

        jmsTemplate.send(destination, new MessageCreator(){

			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage();
				textMessage.setText(messageText);
				textMessage.setJMSPriority(priority);
				return textMessage;
			}});

    }
    catch (JmsException e)
    {
      throw new RuntimeException(e);
    }
     
    }
}
