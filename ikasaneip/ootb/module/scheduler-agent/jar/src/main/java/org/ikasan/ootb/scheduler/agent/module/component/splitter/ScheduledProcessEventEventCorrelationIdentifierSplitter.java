package org.ikasan.ootb.scheduler.agent.module.component.splitter;

import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.splitting.SplitterException;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.springframework.util.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

public class ScheduledProcessEventEventCorrelationIdentifierSplitter implements Splitter<ContextualisedScheduledProcessEvent, ContextualisedScheduledProcessEvent> {

    @Override
    public List<ContextualisedScheduledProcessEvent> split(ContextualisedScheduledProcessEvent event) throws SplitterException {
        List<ContextualisedScheduledProcessEvent> contextualisedScheduledProcessEvents = new ArrayList<>();

        if(event.getContextInstanceId() != null && !event.getContextInstanceId().isEmpty()) {
            contextualisedScheduledProcessEvents.add(event);
            return contextualisedScheduledProcessEvents;
        }

        ContextInstanceCache.getCorrelationIds().forEach(id -> {
            ContextInstance contextInstance = ContextInstanceCache.instance().getByCorrelationId(id);
            if(contextInstance != null && contextInstance.getName().equals(event.getContextName())) {
                ContextualisedScheduledProcessEvent contextualisedScheduledProcessEvent = SerializationUtils.clone(event);
                contextualisedScheduledProcessEvent.setContextInstanceId(contextInstance.getId());
                contextualisedScheduledProcessEvents.add(contextualisedScheduledProcessEvent);
            }
        });

        return contextualisedScheduledProcessEvents;
    }

}
