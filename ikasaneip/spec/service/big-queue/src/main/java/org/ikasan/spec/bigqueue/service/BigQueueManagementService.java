package org.ikasan.spec.bigqueue.service;

import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.bigqueue.service.exception.BigQueueNotFoundException;

import java.io.IOException;
import java.util.List;

public interface BigQueueManagementService {

    /**
     * Gets the size of the specified queue.
     *
     * @param queueName the name of the queue to get the size of
     * @return the size of the queue
     * @throws BigQueueNotFoundException if the specified BigQueue is not found
     */
    long size(String queueName) throws BigQueueNotFoundException;

    /**
     * Retrieves the top message from the specified queue.
     *
     * @param queueName the name of the queue to retrieve the top message from
     * @return the top message in the queue
     * @throws IOException if an I/O error occurs
     * @throws BigQueueNotFoundException if the specified BigQueue is not found
     */
    BigQueueMessage peek(String queueName) throws IOException, BigQueueNotFoundException;

    /**
     * Retrieves all messages from the specified queue.
     *
     * @param queueName the name of the queue to retrieve messages from
     * @return a list of BigQueueMessage objects representing the messages in the queue
     * @throws IOException if an I/O error occurs
     * @throws BigQueueNotFoundException if the specified BigQueue is not found
     */
    List<BigQueueMessage> getMessages(String queueName) throws IOException, BigQueueNotFoundException;

    /**
     * Deletes a message from the specified queue based on the message ID.
     *
     * @param queueName the name of the queue from which to delete the message
     * @param biQueueMessageId the unique identifier of the message to be deleted
     * @throws IOException if an I/O error occurs
     * @throws BigQueueNotFoundException if the specified BigQueue is not found
     */
    void deleteMessage(String queueName, String biQueueMessageId) throws IOException, BigQueueNotFoundException;

    /**
     * Deletes all messages from the specified queue.
     *
     * @param queueName the name of the queue from which to delete all messages
     * @throws IOException if an I/O error occurs
     * @throws BigQueueNotFoundException if the specified BigQueue is not found
     */
    void deleteAllMessage(String queueName) throws IOException, BigQueueNotFoundException;

    /**
     * Retrieves a list of queue names located in the specified directory.
     *
     * @param queueDir the directory path where the queues are located
     * @return a list of queue names in the specified directory
     * @throws IOException if an I/O error occurs
     */
    List<String> listQueues(String queueDir) throws IOException;

    /**
     * Deletes the specified queue from the given directory.
     *
     * @param queueDir the directory path where the queue is located
     * @param queueName the name of the queue to be deleted
     * @throws IOException if an I/O error occurs during deletion
     */
    void deleteQueue(String queueDir, String queueName) throws IOException;
}
