package org.ikasan.component.endpoint.bigqueue.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leansoft.bigqueue.IBigQueue;
import com.leansoft.bigqueue.IBigQueue.ItemIterator;
import org.apache.commons.io.FileUtils;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.bigqueue.service.BigQueueManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of a BigQueue management service.
 *
 * @author Ikasan Development Team
 */
public abstract class AbstractBigQueueManagementService implements BigQueueManagementService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBigQueueManagementService.class);

    /**
     * Defines the implementation of how to get the associated Big Queue based on the queueName
     * It is important that you associated the Big Queue Object attached to the application in this
     * method. Do not create new IBigQueue instance as you will get unexpected behaviours
     * @param queueName Name of the queue
     * @return the related IBigQueue instance of that queueName.
     */
    public abstract IBigQueue getBigQueue(String queueName);

    /**
     * Users getBigQueue(String queueName) to check if a queue exist
     * @param queueName name of the queue
     * @return true if non null is returned
     */
    public boolean queueExists(String queueName) {
        return getBigQueue(queueName) != null;
    }

    /**
     * This uses the directory of where the queue is located to see if the queue exist.
     * @param queueDir directory of where the queues are located
     * @param queueName name of the queue
     * @return true if the file path of the queue exist.
     */
    private boolean queueExists(String queueDir, String queueName) {
        return Files.exists(Paths.get(queueDir + File.separator + queueName));
    }

    /**
     * Checks if a messageId exist in the Big Queue
     * @param queueName name of the queue
     * @param biQueueMessageId messageId passed
     * @return true if the messageId is found
     * @throws IOException if error looking at the queue
     */
    private boolean messageIdExistsInMessages(String queueName, String biQueueMessageId) throws IOException {
        return getMessages(queueName).stream().anyMatch(m -> biQueueMessageId.equals(m.getMessageId()));
    }

    /**
     * Get the size of a given queue.
     * Ensures that the queue exists before finding out the size of the queue, otherwise retuns 0
     * @param queueName - the name of the queue to inspect
     */
    @Override
    public synchronized long size(String queueName) {
        if (queueExists(queueName)) {
            return getBigQueue(queueName).size();
        }
        return 0;
    }



    /**
     * Get the first message off the queue.
     * Ensures that the queue exists before doing a peek of the queue. Returns null otherwise or if nothing on the queue.
     * @param queueName - the name of the queue to inspect
     */
    @Override
    public synchronized BigQueueMessage peek(String queueName) throws IOException {
        if (queueExists(queueName)) {
            byte[] peek = getBigQueue(queueName).peek();
            if (peek != null) {
                return MAPPER.readValue(peek, BigQueueMessageImpl.class);
            }
        }
        return null;
    }

    /**
     * Deletes a message off the queue.
     * Ensures that the queue exists and the messageId is in the queue before looping i.e. dequeue and enqueue without
     * enqueuing the message to be deleted.
     * @param queueName - the name of the queue to inspect
     * @param biQueueMessageId - the message id of the big queue message to delete
     */
    @Override
    public synchronized void deleteMessage(String queueName, String biQueueMessageId) throws IOException {
        if (biQueueMessageId != null
            && queueExists(queueName)
            && messageIdExistsInMessages(queueName, biQueueMessageId)) {
            long size = getBigQueue(queueName).size();
            LOGGER.info("Start of delete for [{}] messageId on the queue [{}]", biQueueMessageId, queueName);
            for (int i = 0; i < size; i++) {
                byte[] dequeueMsg = getBigQueue(queueName).dequeue();
                getBigQueue(queueName).gc();
                BigQueueMessage message = MAPPER.readValue(dequeueMsg, BigQueueMessageImpl.class);
                LOGGER.debug("MessageId Read is = [{}]", message.getMessageId());
                if (!biQueueMessageId.equals(message.getMessageId())) {
                    LOGGER.debug("MessageId is not equal to [{}]. Re-adding to the back of the queue", biQueueMessageId);
                    getBigQueue(queueName).enqueue(dequeueMsg);
                } else {
                    LOGGER.info("MessageId FOUND! Removing [{}]",biQueueMessageId);
                }
            }
            LOGGER.info("End of delete [{}] messageId on the queue [{}]", biQueueMessageId, queueName);
        }
    }

    /**
     * Deletes all message off the queue.
     * Will check if the queue exist and then it will action removeAll() method from BigQueueImpl
     * @param queueName - the name of the queue to inspect
     * @throws IOException exception throws if there is any IO error during dequeue operation.
     */
    @Override
    public synchronized void deleteAllMessage(String queueName) throws IOException {
        if (queueExists(queueName)) {
            getBigQueue(queueName).removeAll();
            getBigQueue(queueName).gc();
        }
    }

    /**
     * Get a list of the directories in the queue directory.
     * Ensures that the queue exists before returning the list of directory names under the queue directory.
     * Returns empty list otherwise.
     * @param queueDir - the directory where the queue exists
     */
    @Override
    public synchronized List<String> listQueues(String queueDir) {
        List<String> queueNames = new ArrayList<>();
        if (queueDir != null && Files.exists(Path.of(queueDir))) {
            File[] directories = new File(queueDir).listFiles(File::isDirectory);
            if (directories != null) {
                Arrays.stream(directories).forEach(file -> queueNames.add(file.getName()));
            }
        }

        return queueNames;
    }

    /**
     * Deletes a queue directory.
     * @param queueDir - the directory name
     * @param queueName - the name of the queue
     */
    @Override
    public void deleteQueue(String queueDir, String queueName) throws IOException {
        if (queueExists(queueDir, queueName)) {
            FileUtils.forceDelete(new File(queueDir + File.separator + queueName));
        }
    }

    /**
     * Get the messages on the queue.
     * Ensures that the queue exists before returning the list of messages. Returns empty list otherwise.
     * @param queueName - the name of the queue to inspect
     */
    @Override
    public synchronized List<BigQueueMessage> getMessages(String queueName) throws IOException {
        if (queueExists(queueName)) {
            MessagesIterator messagesIterator = new MessagesIterator();
            getBigQueue(queueName).applyForEach(messagesIterator);
            return messagesIterator.getMessages();
        }

        return Collections.emptyList();
    }

    private class MessagesIterator implements ItemIterator {
        List<BigQueueMessage> messages = new ArrayList<>();

        @Override
        public void forEach(byte[] item) throws IOException {
            messages.add(MAPPER.readValue(item, BigQueueMessageImpl.class));
        }

        public List<BigQueueMessage> getMessages() {
            return messages;
        }
    }
}