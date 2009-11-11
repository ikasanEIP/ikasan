package org.ikasan.tools.messaging.subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class SimpleSubscriber implements MessageListener{

	private BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<Message>(10);
	
	private DefaultMessageListenerContainer container;
	
	private Logger logger = Logger.getLogger(SimpleSubscriber.class);
	
	private Destination destination;
	
	public void onMessage(Message message) {
		logger.info("onMessage called with ["+message+"]");
		if (receivedMessages.remainingCapacity()==0){
			receivedMessages.remove();
		}
		receivedMessages.add(message);
		logger.info("now there are ["+receivedMessages.size()+"] received messages");
	}
	
	public SimpleSubscriber(ConnectionFactory connectionFactory, Destination destination){
		container = new DefaultMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setDestination(destination);
		container.setConcurrentConsumers(1);
		container.setMessageListener(this);
		container.initialize();
	}
	
	public void shutdown(){
        try{   
        	container.shutdown();
        } catch (java.lang.IllegalStateException e){
            logger.info("illegal state exception when unsubscribing from destination ["+destination+"]");
        }
	}

	
	public List<Message> getMessages(){
		List<Message> result = new ArrayList<Message>();
		result.addAll(receivedMessages);
		logger.info("getMessages called, got result size ["+result.size()+"]");
		return result;
	}
	
	public Message getMessage(String messageId){
		for (Message message: receivedMessages){
			try {
				if (message.getJMSMessageID().equals(messageId)){
					return message;
				}
			} catch (JMSException e) {
				logger.error(e);
			}
		}
		return null;
	}

}
