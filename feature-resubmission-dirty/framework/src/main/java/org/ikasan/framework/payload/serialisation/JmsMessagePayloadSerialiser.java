package org.ikasan.framework.payload.serialisation;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.ikasan.common.Payload;

public interface JmsMessagePayloadSerialiser<T extends Message> {

	public T toMessage(Payload payload, Session session) throws JMSException;
	
	public Payload toPayload(T message) throws JMSException;
	
	public boolean supports(Class<? extends Message> messageClass);
	
	public Class<? extends Message> getSupportedMessageType();
}
