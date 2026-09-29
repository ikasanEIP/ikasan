package org.ikasan.ootb.data.sharing.module.cache;

import org.ikasan.component.endpoint.bigqueue.cache.AbstractBigQueueCache;

public class EntityBigQueueCache extends AbstractBigQueueCache {

    private static EntityBigQueueCache INSTANCE;


    /**
     * Provides a singleton instance of {@code EntityBigQueueCache}.
     *
     * This method ensures that only one instance of {@code EntityBigQueueCache} exists throughout
     * the application's lifecycle. It uses a double-checked locking mechanism to ensure thread safety
     * during lazy initialization of the instance.
     *
     * @return the singleton instance of {@code EntityBigQueueCache}
     */
    public static EntityBigQueueCache instance()
    {
        if(INSTANCE == null) {
            synchronized (EntityBigQueueCache.class) {
                if(INSTANCE == null) {
                    INSTANCE = new EntityBigQueueCache();
                }
            }
        }
        return INSTANCE;
    }
}
