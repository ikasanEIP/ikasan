package org.ikasan.ootb.scheduler.agent.rest.cache;

import org.ikasan.bigqueue.IBigQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InboundJobQueueCache
{
    private Logger logger = LoggerFactory.getLogger(InboundJobQueueCache.class);

    private static InboundJobQueueCache INSTANCE;

    public static InboundJobQueueCache instance()
    {
        if(INSTANCE == null) {
            synchronized (InboundJobQueueCache.class) {
                if(INSTANCE == null) {
                    INSTANCE = new InboundJobQueueCache();
                }
            }
        }
        return INSTANCE;
    }

    private ConcurrentHashMap<String, IBigQueue> cache;

    private InboundJobQueueCache() {
        cache = new ConcurrentHashMap<>();
    }

    public void put(String contextName, IBigQueue contextMachine)
    {
        logger.debug(String.format("%s attempting to put key[%s]", this, contextName));

        this.cache.put(contextName, contextMachine);
    }


    public IBigQueue get(String contextName)
    {
        logger.debug(String.format("%s attempting to get context[%s]"
            , this, contextName));

        return this.cache.get(contextName);
    }

    public boolean contains(String contextName)
    {
        logger.debug(String.format("%s check contains[%s] - result [%s]",this
            , contextName, this.cache.containsKey(contextName)));
        return this.cache.containsKey(contextName);
    }

    public Set keys() {
        return this.cache.keySet();
    }
}
