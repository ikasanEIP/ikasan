package org.ikasan.tools.messaging.publisher;

public interface TextMessagePublisher {

	public void publishTextMessage(String destinationPath, String messageText);
	
}
