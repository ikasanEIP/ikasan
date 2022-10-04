package org.ikasan.component.endpoint.bigqueue.service;

import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.bigqueue.service.BigQueueDirectoryManagementService;
import org.ikasan.spec.bigqueue.service.BigQueueManagementService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, Long> size(boolean includeZeros) throws IOException {
        Map<String, Long> mapQueuesWithSize = new HashMap<>();
        List<String> listQueues = listQueues();
        for (String queue: listQueues) {
            long size = size(queue);
            // Skip if includeZero == false and the size of the queue is 0
            if (!includeZeros && size == 0) {
                continue;
            }
            mapQueuesWithSize.put(queue, size);
        }
        return mapQueuesWithSize;
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
    public void deleteAllMessage(String queueName) throws IOException {
        bigQueueManagementService.deleteAllMessage(queueDirectory, queueName);
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
