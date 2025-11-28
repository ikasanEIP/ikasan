package org.ikasan.spec.bigqueue.service;

import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.bigqueue.service.exception.BigQueueNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface BigQueueDirectoryManagementService {

    /**
     * Gets the size of the specified queue.
     *
     * @param queueName the name of the queue to get the size of
     * @return the size of the queue
     * @throws IOException if an I/O error occurs while retrieving the size
     */
    long size(String queueName) throws IOException, BigQueueNotFoundException;

    /**
     * Retrieves the first message in the specified queue without removing it.
     *
     * @param queueName the name of the queue to peek the message from
     * @return the first message in the queue as a BigQueueMessage object
     * @throws IOException if an I/O error occurs while trying to peek the message
     */
    BigQueueMessage peek(String queueName) throws IOException, BigQueueNotFoundException;

    /**
     * Retrieves a list of messages from the specified queue.
     *
     * @param queueName the name of the queue to retrieve messages from
     * @return a list of BigQueueMessage objects representing the messages in the queue
     * @throws IOException if an I/O error occurs while trying to retrieve the messages
     */
    List<BigQueueMessage> getMessages(String queueName) throws IOException, BigQueueNotFoundException;

    /**
     * Deletes all messages in the specified queue.
     *
     * @param queueName the name of the queue from which to delete all messages
     * @throws IOException if an I/O error occurs while trying to delete all messages
     */
    void deleteAllMessage(String queueName) throws IOException, BigQueueNotFoundException;

    /**
     * Deletes a specific message from the specified queue.
     *
     * @param queueName the name of the queue from which to delete the message
     * @param biQueueMessageId the ID of the message to be deleted
     * @throws IOException if an I/O error occurs while trying to delete the message
     */
    void deleteMessage(String queueName, String biQueueMessageId) throws IOException, BigQueueNotFoundException;

    /**
     * Returns a list of all queues available in the queue directory.
     *
     * @return a list of queue names as Strings
     * @throws IOException if an I/O error occurs while retrieving the list of queues
     */
    List<String> listQueues() throws IOException;

    /**
     * Deletes the specified queue.
     *
     * @param queueName the name of the queue to be deleted
     * @throws IOException if an I/O error occurs while trying to delete the queue
     */
    void deleteQueue(String queueName) throws IOException, BigQueueNotFoundException;

    /**
     * Retrieves the directory path where the queues are stored.
     *
     * @return the directory path as a String
     */
    String getQueueDirectory();

    /**
     * Gets the sizes of all queues in the directory.
     *
     * @param includeZero a boolean indicating whether to include queues with size zero
     * @return a map containing the queue names as keys and their respective sizes as values
     * @throws IOException if an I/O error occurs while retrieving the sizes
     */
    Map<String, Long> size(boolean includeZero) throws IOException;
}
