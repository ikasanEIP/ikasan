package org.ikasan.spec.scheduled.context.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.ikasan.spec.scheduled.instance.model.InstanceStatus;

import java.util.Map;

public interface ContextStatusService<ContextMachine> {

    /**
     * Get that status of all instances from the Context machine
     * @param includePrepared - true to include prepared and not active context instance
     * @return list of ContextMachineStatus in Json
     * {
     * 	"contextMachineStatusList" : [ {
     * 		"contextName" : "CONTEXT-1436221681",
     * 		"contextInstanceId" : "3e774777-8ee0-4354-b390-f2ad0712ca63",
     * 		"instanceStatus" : "WAITING"
     *    } ]
     * }
     * @throws JsonProcessingException if issue transforming to Json
     */
    String getJsonContextMachineStatus(boolean includePrepared) throws JsonProcessingException;

    /**
     * Get the status of all jobs in the Context Machine
     * @param instanceStatus - optional value to search for a particular instance status. Set to null to return everything
     * @param mapAllContextMachine - option map of context machine to found the status on. Set to null or empty to return bring back all active instances
     * @return list of ContextJobInstanceStatus in JSON
     * {
     *   "jobPlans" : [ {
     *     "contextName" : "CONTEXT-1436221681",
     *     "contextInstanceId" : "3e774777-8ee0-4354-b390-f2ad0712ca63",
     *     "instanceStatus" : "WAITING",
     *     "jobDetails" : [ {
     *       "jobName" : "JOB-12144s212",
     *       "childContextName" : [ "CONTEXT-1436221211" ],
     *       "instanceStatus" : "WAITING",
     *       "targetResidingContextOnly" : false,
     *       "startTime" : 1694138400008,
     *       "endTime" : 1694138400008
     *     } ]
     *   } ]
     * }
     * @throws JsonProcessingException if issue transforming to Json
     */
    String getJsonContextJobStatus(InstanceStatus instanceStatus, Map<String, ContextMachine> mapAllContextMachine) throws JsonProcessingException;

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
     * @return Mapped json representation of the context status
     */
    String getJsonContextStatus(String instanceName, String contextName) throws JsonProcessingException;

    /**
     * Get the status of the Scheduler Job in Json
     *
     * @param instanceName the context instance
     * @param contextName the context to get the status for
     * @param jobIdentifier the scheduler job identifier
     * @return Mapped json representation of the job status
     */
    String getJsonContextStatusForJob(String instanceName, String contextName, String jobIdentifier) throws JsonProcessingException;

}
