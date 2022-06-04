package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ikasan.spec.scheduled.event.model.ContextualisedSchedulerJobInitiationEvent;
import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;

public interface JobLockHolder extends Serializable {

    String getLockName();

    void setLockName(String lockName);

    long getLockCount();

    void setLockCount(long lockCount);

    Map<String, List<SchedulerJob>> getSchedulerJobs();

    void addSchedulerJobs(String contextName, List<SchedulerJob> jobs);

    Set<String> getLockHolders();

    void addLockHolder(String jobIdentifier);

    boolean removeLockHolder(String jobIdentifier);

    ContextualisedSchedulerJobInitiationEvent pollSchedulerJobInitiationEventWaitQueue();

    void addQueuedSchedulerJobInitiationEvent(ContextualisedSchedulerJobInitiationEvent event);
}
