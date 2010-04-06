package org.ikasan.demo.jms;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

public class MessageMemeneto {
	private String correlationID;
	
	private int deliveryMode;

	private Destination destination;


	private long expiration;


	private Map<String, Object> mappedValues = new HashMap<String, Object>();


	private String messageID;


	private Map<String, Object> messageProperties = new HashMap<String, Object>();


	private int priority;


	private boolean redelivered;


	private Destination replyTo;


	private String textContent;


	private long timestamp;


	private String type;
	
	private Logger logger = Logger.getLogger(MessageMemeneto.class);


	public MessageMemeneto(Message message) throws JMSException{
		correlationID = message.getJMSCorrelationID();
		deliveryMode = message.getJMSDeliveryMode();
		destination = message.getJMSDestination();
		expiration = message.getJMSExpiration();
		messageID = message.getJMSMessageID();
		priority = message.getJMSPriority();
		redelivered = message.getJMSRedelivered();
		replyTo = message.getJMSReplyTo();
		timestamp = message.getJMSTimestamp();
		type = message.getJMSType();
		
		
		Enumeration propertyNames = message.getPropertyNames();
		while (propertyNames.hasMoreElements()){
			String propertyName = (String) propertyNames.nextElement();
			messageProperties.put(propertyName, message.getObjectProperty(propertyName));
		}
		
		if (message instanceof TextMessage){
			textContent = ((TextMessage)message).getText();
		} else if (message instanceof MapMessage){
			MapMessage mapMessage = (MapMessage)message;
			Enumeration mapNames = mapMessage.getMapNames();
			while(mapNames.hasMoreElements()){
				String mapName = (String) mapNames.nextElement();
				mappedValues.put(mapName, mapMessage.getObject(mapName));
			}
		}
		
	}


	public String getCorrelationID() {
		return correlationID;
	}
	public int getDeliveryMode() {
		return deliveryMode;
	}
	public Destination getDestination() {
		return destination;
	}
	public long getExpiration() {
		return expiration;
	}
	public Map<String, Object> getMappedValues() {
		return mappedValues;
	}
	public String getMessageID() {
		return messageID;
	}
	public Map<String, Object> getMessageProperties() {
		return messageProperties;
	}
	public int getPriority() {
		return priority;
	}
	public Destination getReplyTo() {
		return replyTo;
	}
	
	public String getTextContent() {
		return textContent;
	}
	public long getTimestamp() {
		return timestamp;
	}
	
	public String getType() {
		return type;
	}
	
	
	public boolean isRedelivered() {
		return redelivered;
	}
}
