package org.ikasan.ootb.scheduler.agent.rest.cache;

import org.ikasan.component.endpoint.bigqueue.cache.AbstractBigQueueCache;

public class InternalFileWatcherJobQueueCache extends AbstractBigQueueCache {

    private volatile static InternalFileWatcherJobQueueCache INSTANCE;

    /**
     * Returns the singleton instance of InternalFileWatcherJobQueueCache.
     *
     * @return the singleton instance of InternalFileWatcherJobQueueCache
     */
    public static InternalFileWatcherJobQueueCache instance()
    {
        if(INSTANCE == null) {
            synchronized (InternalFileWatcherJobQueueCache.class) {
                if(INSTANCE == null) {
                    INSTANCE = new InternalFileWatcherJobQueueCache();
                }
            }
        }
        return INSTANCE;
    }
}
