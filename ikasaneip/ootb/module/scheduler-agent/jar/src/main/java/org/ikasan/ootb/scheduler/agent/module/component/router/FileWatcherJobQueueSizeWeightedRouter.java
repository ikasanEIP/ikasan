package org.ikasan.ootb.scheduler.agent.module.component.router;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.ootb.scheduler.agent.module.component.splitter.FileWatcherEventCorrelationIdentifierSplitter;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.ootb.scheduler.agent.rest.cache.InternalFileWatcherJobQueueCache;
import org.ikasan.spec.component.routing.RouterException;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class FileWatcherJobQueueSizeWeightedRouter implements SingleRecipientRouter<FileWatcherJobEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatcherEventCorrelationIdentifierSplitter.class);

    @Override
    public String route(FileWatcherJobEvent messageToRoute) throws RouterException {
        AtomicLong queueSize = new AtomicLong(Long.MAX_VALUE);
        AtomicReference<String> routeName = new AtomicReference<>("INVALID_ROUTE");

        // We are going to route to the flow that publishes to the big queue
        // that is smallest in size (least messages on it to be processed).
        InternalFileWatcherJobQueueCache.instance().keys().forEach(key -> {
            IBigQueue queue = InternalFileWatcherJobQueueCache.instance().get(key);
            LOGGER.info(String.format("Routing job name[%s] to queue[%s] size[%s]!", messageToRoute.getJobName(), key, queue.size()));
            if(queue.size() < queueSize.get()) {
                queueSize.set(queue.size());
                routeName.set(key);
            }
        });


        LOGGER.info(String.format("Routing job name[%s] to route[%s]!", messageToRoute.getJobName(), routeName.get()));
        return routeName.get();
    }
}
