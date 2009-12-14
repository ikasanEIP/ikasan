package org.ikasan.tools.messaging.subscriber;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.log4j.Logger;

public class SimpleSubscriber extends BaseSubscriber {

	private Logger logger = Logger.getLogger(SimpleSubscriber.class);
	
	public SimpleSubscriber(ConnectionFactory connectionFactory,
			Destination destination) {
		super(connectionFactory, destination);
	}

	private int maximumMessages = 10;
	
	private BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<Message>(maximumMessages);

	
	public void handleMessage(Message message) {
		logger.info("onMessage called with ["+message+"]");
		if (receivedMessages.remainingCapacity()==0){
			receivedMessages.remove();
		}
		receivedMessages.add(message);
		logger.info("now there are ["+receivedMessages.size()+"] received messages");
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
	

	
	public int getMaximumMessages(){
		return maximumMessages;
	}

}
