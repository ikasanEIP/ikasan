package org.ikasan.spec.bigqueue.service;

import org.ikasan.spec.bigqueue.message.BigQueueMessage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface BigQueueDirectoryManagementService {

    long size(String queueName) throws IOException;

    BigQueueMessage peek(String queueName) throws IOException;

    List<BigQueueMessage> getMessages(String queueName) throws IOException;

    void deleteAllMessage(String queueName) throws IOException;

    void deleteMessage(String queueName, String biQueueMessageId) throws IOException;

    List<String> listQueues() throws IOException;

    void deleteQueue(String queueName) throws IOException;

    String getQueueDirectory();

    Map<String, Long> size(boolean includeZero) throws IOException;
}
