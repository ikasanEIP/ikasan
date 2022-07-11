package org.ikasan.component.endpoint.bigqueue.service;

import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.bigqueue.service.BigQueueDirectoryManagementService;
import org.ikasan.spec.bigqueue.service.BigQueueManagementService;

import java.io.IOException;
import java.util.List;

public class BigQueueDirectoryManagementServiceImpl implements BigQueueDirectoryManagementService {

    private final String queueDirectory;
    private final BigQueueManagementService bigQueueManagementService;

    public BigQueueDirectoryManagementServiceImpl(String queueDirectory) {
        this.queueDirectory = queueDirectory;
        if (this.queueDirectory == null) {
            throw new IllegalArgumentException("queueDirectory can not be null!");
        }

        this.bigQueueManagementService = new BigQueueManagementServiceImpl();
    }

    @Override
    public long size(String queueName) throws IOException {
        return bigQueueManagementService.size(queueDirectory, queueName);
    }

    @Override
    public BigQueueMessage peek(String queueName) throws IOException {
        return bigQueueManagementService.peek(queueDirectory, queueName);
    }

    @Override
    public List<BigQueueMessage> getMessages(String queueName) throws IOException {
        return bigQueueManagementService.getMessages(queueDirectory, queueName);
    }

    @Override
    public void deleteMessage(String queueName, String biQueueMessageId) throws IOException {
        bigQueueManagementService.deleteMessage(queueDirectory, queueName, biQueueMessageId);
    }

    @Override
    public List<String> listQueues() throws IOException {
        return bigQueueManagementService.listQueues(queueDirectory);
    }

    @Override
    public void deleteQueue(String queueName) throws IOException {
        bigQueueManagementService.deleteQueue(queueDirectory, queueName);
    }

    @Override
    public String getQueueDirectory() {
        return this.queueDirectory;
    }
}
