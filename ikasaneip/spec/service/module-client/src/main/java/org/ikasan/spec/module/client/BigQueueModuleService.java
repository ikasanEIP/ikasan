package org.ikasan.spec.module.client;

import org.ikasan.spec.bigqueue.message.BigQueueMessage;

import java.util.List;
import java.util.Map;

public interface BigQueueModuleService {

    /**
     * Calls the module to get the size of a given queue
     * @param contextUrl    url of the module
     * @param queueName     name of the queue we want to check the size for
     *
     * @return size of the queue for the module being checked
     */
    long size(String contextUrl, String queueName);

    /**
     * Calls the module to get the first message on the provided queue
     * @param contextUrl    url of the module
     * @param queueName     name of the queue to take a peek for
     *
     * @return BigQueueMessage
     */
    BigQueueMessage peek(String contextUrl, String queueName);

    /**
     * Calls the module to get the all the messages on the provided queue
     * @param contextUrl    url of the module
     * @param queueName     name of the queue to get all messages for
     *
     * @return list of BigQueueMessage
     */
    List<BigQueueMessage> getMessages(String contextUrl, String queueName);

    /**
     * Calls the module to get all the queue names that exist
     * @param contextUrl    url of the module
     *
     * @return a list of all queues for the module.
     */
    List<String> listQueues(String contextUrl);

    /**
     * Calls the module to get all queue with its current queue depth
     * @param contextUrl    url of the module
     * @param includeZeros  Set to true if required to return queue depth for all queue, even if it has size 0.
     *                      Set to false if queue depth greater than 0 should be returned.
     *
     * @return Map of queue and its size for the module
     */
    Map<String, Long> size(String contextUrl, boolean includeZeros);

    /**
     * Calls the module to remove a message from a queue
     * @param contextUrl    url of the module
     * @param queueName     name of the queue to delete a message from
     * @param messageId     id of the message to delete
     *
     * @return true if successfully removed, false if something went wrong
     */
    boolean deleteMessage(String contextUrl, String queueName, String messageId);
}
