package org.ikasan.demo.jms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.log4j.Logger;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SessionAwareMessageListener;


public class SimpleSubscriber implements SessionAwareMessageListener 
{
    private static Logger logger = Logger.getLogger(SimpleSubscriber.class);
    
    private Destination destination;
    
    private List<MessageMemeneto> messagesReceived = new ArrayList<MessageMemeneto>();
    
    
    
    private ConnectionFactory connectionFactory;
    
    private DefaultMessageListenerContainer listener;
   
    private SimpleSubscriber(ConnectionFactory connectionFactory){
        this.connectionFactory = connectionFactory;
    }
    
    public SimpleSubscriber(ConnectionFactory connectionFactory, Destination destination){
        
        this(connectionFactory);
        this.destination = destination;
        subscribe();
    }
    

    public void subscribe()
    {
        try
        {
            logger.info("subscribing to destination ["+getDestinationName()+"]");
        }
        catch (JMSException e)
        {
            throw new RuntimeException(e); 
        }
        
        
        
        
        initialiseNewListener();
    }

    private String getDestinationName()
            throws JMSException
    {
        String destinationName = "";
        if (destination instanceof Topic){
            destinationName=((Topic)destination).getTopicName();
        } else if (destination instanceof Queue){
            destinationName=((Queue)destination).getQueueName();
        }
        return destinationName;
    }

    private void initialiseNewListener()
    {
        listener = new DefaultMessageListenerContainer();
        listener.setConnectionFactory(connectionFactory);
        listener.setDestination(destination);
        listener.setConcurrentConsumers(1);
        //listener.setMessageListener(context.getBean("messageListener"));
        listener.setMessageListener(this);
        listener.initialize();
    }
    

    public void onMessage(Message message, Session session) throws JMSException
    {
    	logger.info("received message");
        
        messagesReceived.add(new MessageMemeneto(message));
        logger.info("received message, now holding "+messagesReceived.size()+" messages");

    }
    
    
    public List<MessageMemeneto> getMessagesReceived() {
		return messagesReceived;
	}

	public void unsubscribe(){
       String destinationName = null;
        
        try
        {
            destinationName = getDestinationName();
            logger.info("unsubscribing from destination ["+destinationName+"]");
        }
        catch (JMSException e)
        {
            throw new RuntimeException(e); 
        }
        
        if (listener!=null){
            //listener.stop();

            try{   
                listener.shutdown();
            } catch (java.lang.IllegalStateException e){
                logger.info("illegal state exception when unsubscribing from topic ["+destinationName+"]");
            }
 
        }
    }  
}
