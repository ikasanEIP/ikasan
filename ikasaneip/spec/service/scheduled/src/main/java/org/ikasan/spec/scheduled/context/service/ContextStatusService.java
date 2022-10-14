package org.ikasan.spec.scheduled.context.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ContextStatusService {

    /**
     * Get the status of a Context
     *
     * @param instanceName the context instance
     * @param contextName the context to get the status for
     * @return the InstanceStatus of the context
     */
    String getContextStatus(String instanceName, String contextName);

    /**
     * Get the status of a Scheduler Job
     *
     * @param instanceName the context instance
     * @param contextName the context to get the status for
     * @param jobIdentifier the scheduler job identifier
     * @return the InstanceStatus of the job
     */
    String getContextStatusForJob(String instanceName, String contextName, String jobIdentifier);

    /**
     * Get the status of a Context in Json
     *
     * @param instanceName the context instance
     * @param contextName the context to get the status for
     * @return the Json representation of the context status
     */
    String getJsonContextStatus(String instanceName, String contextName) throws JsonProcessingException;

    /**
     * Get the status of the Scheduler Job in Json
     *
     * @param instanceName the context instance
     * @param contextName the context to get the status for
     * @param jobIdentifier the scheduler job identifier
     * @return the Json representation of the job status
     */
    String getJsonContextStatusForJob(String instanceName, String contextName, String jobIdentifier) throws JsonProcessingException;

}
