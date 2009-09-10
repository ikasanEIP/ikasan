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
