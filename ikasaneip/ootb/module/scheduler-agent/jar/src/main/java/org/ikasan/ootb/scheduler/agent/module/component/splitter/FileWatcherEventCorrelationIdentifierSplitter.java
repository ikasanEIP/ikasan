package org.ikasan.ootb.scheduler.agent.module.component.splitter;

import org.ikasan.ootb.scheduler.agent.module.component.serialiser.ScheduledProcessEventToBigQueueMessageSerialiser;
import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.splitting.SplitterException;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

public class FileWatcherEventCorrelationIdentifierSplitter implements Splitter<FileWatcherJobEvent, FileWatcherJobEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatcherEventCorrelationIdentifierSplitter.class);

    @Override
    public List<FileWatcherJobEvent> split(FileWatcherJobEvent event) throws SplitterException {
        List<FileWatcherJobEvent> fileWatcherJobEvents = new ArrayList<>();

        if(event.getCorrelationIdentifier() != null && !event.getCorrelationIdentifier().isEmpty()) {
            fileWatcherJobEvents.add(event);
            return fileWatcherJobEvents;
        }

        ContextInstanceCache.getCorrelationIds().forEach(id -> {
            ContextInstance contextInstance = ContextInstanceCache.instance().getByCorrelationId(id);
            if(contextInstance != null && contextInstance.getName().equals(event.getContextName())) {
                FileWatcherJobEvent fileWatcherJobEvent = SerializationUtils.clone(event);
                fileWatcherJobEvent.setCorrelationIdentifier(contextInstance.getId());
                fileWatcherJobEvents.add(fileWatcherJobEvent);
            }
        });

        LOGGER.info(String.format("Sending [%s] split events for job[%s]", fileWatcherJobEvents.size(), event.getJobName()));
        return fileWatcherJobEvents;
    }

}
