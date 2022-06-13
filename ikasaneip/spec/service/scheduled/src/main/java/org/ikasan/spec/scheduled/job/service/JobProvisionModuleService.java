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
    public void provisionJobs(String contextUrl, SchedulerJobWrapper jobs);
}
