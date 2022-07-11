package org.ikasan.spec.bigqueue.service;

import org.ikasan.spec.bigqueue.message.BigQueueMessage;

import java.io.IOException;
import java.util.List;

public interface BigQueueManagementService {

    long size(String queueDir, String queueName) throws IOException;

    BigQueueMessage peek(String queueDir, String queueName) throws IOException;

    List<BigQueueMessage> getMessages(String queueDir, String queueName) throws IOException;

    void deleteMessage(String queueDir, String queueName, String biQueueMessageId) throws IOException;

    List<String> listQueues(String queueDir) throws IOException;

    void deleteQueue(String queueDir, String queueName) throws IOException;
}
