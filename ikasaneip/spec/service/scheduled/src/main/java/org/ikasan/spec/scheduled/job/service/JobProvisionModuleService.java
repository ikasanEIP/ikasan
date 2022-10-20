package org.ikasan.spec.scheduled.job.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.ikasan.spec.scheduled.job.model.SchedulerJobWrapper;

public interface JobProvisionModuleService {

    /**
     * This method it responsible for provisioning jobs on an agent.
     *
     * @param contextUrl
     * @param jobs
     * @throws JsonProcessingException
     */
    void provisionJobs(String contextUrl, SchedulerJobWrapper jobs);


    /**
     * Remove all jobs on an agent for a given context.
     *
     * @param contextUrl
     * @param contextName
     */
    void removeJobsForContext(String contextUrl, String contextName);
}
