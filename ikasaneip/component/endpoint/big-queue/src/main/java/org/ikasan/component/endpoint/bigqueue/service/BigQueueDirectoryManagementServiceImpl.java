package org.ikasan.component.endpoint.bigqueue.service;

import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.bigqueue.service.BigQueueDirectoryManagementService;
import org.ikasan.spec.bigqueue.service.BigQueueManagementService;
import org.ikasan.spec.bigqueue.service.exception.BigQueueNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BigQueueDirectoryManagementServiceImpl implements BigQueueDirectoryManagementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BigQueueDirectoryManagementServiceImpl.class);

    private final String queueDirectory;
    private final BigQueueManagementService bigQueueManagementService;

    public BigQueueDirectoryManagementServiceImpl(BigQueueManagementService bigQueueManagementService, String queueDirectory) {
        this.queueDirectory = queueDirectory;
        if (this.queueDirectory == null) {
            throw new IllegalArgumentException("queueDirectory can not be null!");
        }

        this.bigQueueManagementService = bigQueueManagementService;
        if (this.bigQueueManagementService == null) {
            throw new IllegalArgumentException("bigQueueManagementService can not be null!");
        }
    }

    @Override
    public Map<String, Long> size(boolean includeZeros) throws IOException {
        Map<String, Long> mapQueuesWithSize = new HashMap<>();
        List<String> listQueues = listQueues();
        for (String queue: listQueues) {
            try {
                long size = size(queue);
                // Skip if includeZero == false and the size of the queue is 0
                if (!includeZeros && size == 0) {
                    continue;
                }
                mapQueuesWithSize.put(queue, size);
            } catch (BigQueueNotFoundException e) {
                LOGGER.info(String.format("Requesting queue size of [%s] but the queue does not exist!", queue));
            }
        }
        return mapQueuesWithSize;
    }

    @Override
    public long size(String queueName) throws IOException, BigQueueNotFoundException {
        return bigQueueManagementService.size(queueName);
    }

    @Override
    public BigQueueMessage peek(String queueName) throws IOException, BigQueueNotFoundException {
        return bigQueueManagementService.peek(queueName);
    }

    @Override
    public List<BigQueueMessage> getMessages(String queueName) throws IOException, BigQueueNotFoundException {
        return bigQueueManagementService.getMessages(queueName);
    }

    @Override
    public void deleteAllMessage(String queueName) throws IOException, BigQueueNotFoundException {
        bigQueueManagementService.deleteAllMessage(queueName);
    }

    @Override
    public void deleteMessage(String queueName, String biQueueMessageId) throws IOException, BigQueueNotFoundException {
        bigQueueManagementService.deleteMessage(queueName, biQueueMessageId);
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
