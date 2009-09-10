package org.ikasan.demo.jms;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class JmsTextMessagePublisher {

	
    private ConnectionFactory connectionFactory;




    public JmsTextMessagePublisher(ConnectionFactory connectionFactory) {
		super();
		this.connectionFactory = connectionFactory;
	}



	public void publishTextMessage(Destination destination, final String messageText, final int priority) {
      try
    {

    	JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
    	jmsTemplate.setPriority(priority);
    	jmsTemplate.setExplicitQosEnabled(true);
    	  
        jmsTemplate.send(destination, new MessageCreator(){
			public Message createMessage(Session session) throws JMSException {
				return  session.createTextMessage(messageText);
		}});

    }
    catch (JmsException e)
    {
      throw new RuntimeException(e);
    }
     
    }
}
