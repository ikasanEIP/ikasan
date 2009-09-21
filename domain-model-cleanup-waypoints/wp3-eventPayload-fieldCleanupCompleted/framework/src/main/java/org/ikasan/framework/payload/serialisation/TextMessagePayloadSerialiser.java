package org.ikasan.framework.payload.serialisation;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.ikasan.common.Payload;
import org.ikasan.common.factory.PayloadFactory;

public class TextMessagePayloadSerialiser implements
		JmsMessagePayloadSerialiser<TextMessage> {
	
	private PayloadFactory payloadFactory;


	public Class<? extends Message> getSupportedMessageType() {
		return TextMessage.class;
	}

	public boolean supports(Class<? extends Message> messageClass) {
		return TextMessage.class.isAssignableFrom(messageClass);
	}

	public TextMessage toMessage(Payload payload, Session session) throws JMSException {
		TextMessage textMessage = session.createTextMessage();
		//TODO, how do we handle the payload encoding??
        textMessage.setText(new String(payload.getContent()));

        //TODO do we need to set any of the QOS properties on the message?

		return textMessage;
	}

	public Payload toPayload(TextMessage message) throws JMSException {

		//strictly speaking this method is not really necessary as the RawMessageDrivenInitiator already handles incoming text messages
		return  payloadFactory.newPayload(message.getJMSMessageID(),  message.getText().getBytes());
	}
	
	public void setPayloadFactory(PayloadFactory payloadFactory) {
		this.payloadFactory = payloadFactory;
	}

}
