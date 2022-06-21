package org.ikasan.spec.scheduled.job.service;

import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;

public interface JobInitiationService {

    /**
     * Raise a SchedulerJobInitiationEvent with the relevant agent.
     *
     * @param contextUrl
     * @param event
     * @return
     */
    void raiseSchedulerJobInitiationEvent(String contextUrl, SchedulerJobInitiationEvent event);

    /**
     * Raise a quartz event scheduler job.
     *
     * @param contextUrl
     * @param agentName
     * @param jobName
     */
    void raiseQuartzSchedulerJob(String contextUrl, String agentName, String jobName);

    /**
     * Raise a file event scheduler job.
     *
     * @param contextUrl
     * @param agentName
     * @param jobName
     */
    void raiseFileEventSchedulerJob(String contextUrl, String agentName, String jobName);
}
