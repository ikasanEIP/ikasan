package org.ikasan.ootb.scheduler.agent.rest.cache;

public class InboundJobQueueCache extends AbstractBigQueueCache {

    private static InboundJobQueueCache INSTANCE;

    /**
     * Returns the singleton instance of InboundJobQueueCache.
     *
     * @return the singleton instance of InboundJobQueueCache
     */
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
}
