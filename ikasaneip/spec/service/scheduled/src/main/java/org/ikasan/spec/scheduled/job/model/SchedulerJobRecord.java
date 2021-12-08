package org.ikasan.spec.scheduled.job.model;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface SchedulerJobRecord<T extends SchedulerJob> {
    String getId();

    String getType();

    String getAgentName();

    String getJobName();

    String getContextId();

    T getJob() throws JsonProcessingException;

    long getTimestamp();
}
