package org.ikasan.spec.scheduled.job.service;

import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;

public interface JobInitiationService {

    /**
     * Raise a SchedulerJobInitiationEvent with the relevant agent.
     *
     * @param contextUrl
     * @param event
     */
    void raiseSchedulerJobInitiationEvent(String contextUrl, SchedulerJobInitiationEvent event);

    /**
     * Raise a quartz event scheduler job.
     *
     * @param contextUrl
     * @param agentName
     * @param jobName
     * @param correlationId used to uniquely identify the events raised e.g. for the dashboard this would be the contextInstanceId
     */
    void raiseQuartzSchedulerJob(String contextUrl, String agentName, String jobName, String correlationId);

    /**
     * Raise a file event scheduler job.
     *
     * @param contextUrl
     * @param agentName
     * @param jobName
     * @param correlationId used to uniquely identify the events raised e.g. for the dashboard this would be the contextInstanceId
     */
    void raiseFileEventSchedulerJob(String contextUrl, String agentName, String jobName, String correlationId);
}
