package org.ikasan.spec.bigqueue.service;

import org.ikasan.spec.bigqueue.message.BigQueueMessage;

import java.io.IOException;
import java.util.List;

public interface BigQueueManagementService {

    long size(String queueName);

    BigQueueMessage peek(String queueName) throws IOException;

    List<BigQueueMessage> getMessages(String queueName) throws IOException;

    void deleteMessage(String queueName, String biQueueMessageId) throws IOException;

    void deleteAllMessage(String queueName) throws IOException;

    List<String> listQueues(String queueDir) throws IOException;

    void deleteQueue(String queueDir, String queueName) throws IOException;
}
