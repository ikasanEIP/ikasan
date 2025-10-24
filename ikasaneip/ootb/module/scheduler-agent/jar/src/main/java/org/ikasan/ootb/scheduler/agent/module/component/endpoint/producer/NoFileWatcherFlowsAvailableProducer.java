package org.ikasan.ootb.scheduler.agent.module.component.endpoint.producer;

import org.ikasan.ootb.scheduler.agent.module.model.FileWatcherJobEvent;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;

public class NoFileWatcherFlowsAvailableProducer implements Producer<FileWatcherJobEvent> {
    @Override
    public void invoke(FileWatcherJobEvent payload) throws EndpointException {
        throw new EndpointException(String.format("A FileWatcherJobEvent was received for job[%s] and job plan[%s] however there " +
            "were no downstream flows available to process the event!", payload.getJobName(), payload.getContextName()));
    }
}
