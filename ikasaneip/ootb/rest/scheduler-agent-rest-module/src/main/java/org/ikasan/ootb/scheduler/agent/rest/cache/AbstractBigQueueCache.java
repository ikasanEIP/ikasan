package org.ikasan.ootb.scheduler.agent.rest.cache;

import org.ikasan.bigqueue.IBigQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBigQueueCache
{
    private Logger logger = LoggerFactory.getLogger(AbstractBigQueueCache.class);

    private ConcurrentHashMap<String, IBigQueue> cache;


    /**
     * This private constructor initializes a new instance of InboundJobQueueCache,
     * creating a ConcurrentHashMap to store IBigQueue objects.
     */
    protected AbstractBigQueueCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    /**
     * Adds the specified IBigQueue object to the cache under the given context name.
     *
     * @param queueName the name of the queue under which the IBigQueue object will be stored
     * @param queue the IBigQueue object to be stored in the cache
     */
    public void put(String queueName, IBigQueue queue) {
        logger.debug("%s attempting to put key[%s]".formatted(this, queueName));

        this.cache.put(queueName, queue);
    }


    /**
     * Retrieves the IBigQueue object associated with the given context name from the cache.
     *
     * @param queueName the name of the queue for which the IBigQueue object is to be retrieved
     * @return the IBigQueue object associated with the context name, or null if not found
     */
    public IBigQueue get(String queueName) {
        logger.debug("%s attempting to get context[%s]".formatted(this, queueName));

        return this.cache.get(queueName);
    }

    /**
     * Checks if the cache contains the specified context name.
     *
     * @param queueName the name of the queue to check in the cache
     * @return true if the cache contains the queue name, false otherwise
     */
    public boolean contains(String queueName) {
        logger.debug("%s check contains[%s] - result [%s]".formatted(this
        , queueName, this.cache.containsKey(queueName)));
        return this.cache.containsKey(queueName);
    }

    /**
     * Removes the IBigQueue object associated with the specified queue name from the cache.
     *
     * @param queueName the name of the queue for which the IBigQueue object should be removed
     * @return true if the IBigQueue object was successfully removed, false if no object was associated with the queue name
     */
    public boolean remove(String queueName) {
        logger.debug("%s remove[%s] - result [%s]".formatted(this
            , queueName, this.cache.containsKey(queueName)));
        IBigQueue removed = this.cache.remove(queueName);
        logger.debug("%s remove[%s] - result [%s]".formatted(this
            , queueName, removed != null));
        return removed != null;
    }

    /**
     * Returns a Set view of the keys contained in this cache.
     *
     * @return a Set view of the keys contained in this cache
     */
    public Set<String> keys() {
        return this.cache.keySet();
    }

    /**
     * Removes all IBigQueue objects from the cache.
     * The cache will be empty after this operation.
     */
    public void removeAll() {
        this.cache.clear();
    }
}
