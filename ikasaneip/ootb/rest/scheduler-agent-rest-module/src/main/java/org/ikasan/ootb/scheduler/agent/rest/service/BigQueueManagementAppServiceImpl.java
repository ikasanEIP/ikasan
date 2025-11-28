package org.ikasan.ootb.scheduler.agent.rest.service;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.component.endpoint.bigqueue.service.AbstractBigQueueManagementService;
import org.ikasan.ootb.scheduler.agent.rest.cache.InboundJobQueueCache;
import org.ikasan.ootb.scheduler.agent.rest.cache.InternalFileWatcherJobQueueCache;
import org.ikasan.spec.bigqueue.service.exception.BigQueueNotFoundException;

public class BigQueueManagementAppServiceImpl extends AbstractBigQueueManagementService {

    /**
     * Using the InboundJobQueueCache, find the queue so that we can manage the queue.
     * @param queueName Name of the queue
     * @return the related IBigQueue instance of that queueName from the InboundJobQueueCache
     */
    @Override
    public IBigQueue getBigQueue(String queueName) throws BigQueueNotFoundException {
        if (InboundJobQueueCache.instance().contains(queueName)) {
            return InboundJobQueueCache.instance().get(queueName);
        }
        else if(InternalFileWatcherJobQueueCache.instance().contains(queueName)) {
            return InternalFileWatcherJobQueueCache.instance().get(queueName);
        }

        throw new BigQueueNotFoundException("BigQueue queue not found: " + queueName);
    }

}
