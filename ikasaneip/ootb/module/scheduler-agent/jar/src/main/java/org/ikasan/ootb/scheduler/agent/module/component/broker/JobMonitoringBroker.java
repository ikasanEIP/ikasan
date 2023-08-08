/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.lang3.math.NumberUtils;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.JobMonitoringBrokerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.model.EnrichedContextualisedScheduledProcessEvent;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.scheduled.event.model.Outcome;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Job Monitoring Broker implementation for the execution of the command line process.
 *
 * @author Ikasan Development Team
 */
public class JobMonitoringBroker implements Broker<EnrichedContextualisedScheduledProcessEvent, EnrichedContextualisedScheduledProcessEvent>,
                                            ConfiguredResource<JobMonitoringBrokerConfiguration>
{
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobMonitoringBroker.class);
    public static final int DEFAULT_ERROR_RETURN_CODE = 1;

    private String configuredResourceId;
    private JobMonitoringBrokerConfiguration configuration;

    @Override
    public EnrichedContextualisedScheduledProcessEvent invoke(EnrichedContextualisedScheduledProcessEvent scheduledProcessEvent) throws EndpointException
    {
        scheduledProcessEvent.setJobStarting(false);

        // If the process is skipped we do what is necessary to the payload
        // and return it.
        if(scheduledProcessEvent.isSkipped()) {
            this.manageSkipped(scheduledProcessEvent);
            return scheduledProcessEvent;
        }

        // If the process running in dry run mode, we do what is necessary to the payload
        // and return it.
        if(scheduledProcessEvent.isDryRun()) {
            this.manageDryRun(scheduledProcessEvent);
            return scheduledProcessEvent;
        }

        // If any day of weeks are defined, we only run the job on the day of the
        // week that is defined.
        if(scheduledProcessEvent.getInternalEventDrivenJob().getDaysOfWeekToRun() != null
            && !scheduledProcessEvent.getInternalEventDrivenJob().getDaysOfWeekToRun().isEmpty()
            && !scheduledProcessEvent.getInternalEventDrivenJob().getDaysOfWeekToRun()
            .contains(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))) {
            this.manageDayOfWeekToRunIgnored(scheduledProcessEvent);
            return scheduledProcessEvent;
        }

        try {
            // Process detached i.e. after agent restart, was still running.
            LOGGER.info("Detached process" + scheduledProcessEvent.getDetachableProcess());

            if (scheduledProcessEvent.getDetachableProcess().isDetached()) {

                String executionDetails = scheduledProcessEvent.getExecutionDetails() != null ? scheduledProcessEvent.getExecutionDetails() : "";
                executionDetails += "\n\nThe process was detached, the processHandle and output file will be used to determine the return value.";
                if (!scheduledProcessEvent.getDetachableProcess().isDetachedAlreadyFinished()) {
                    ProcessHandle processHandle = scheduledProcessEvent.getDetachableProcess().getProcessHandle();
                    LOGGER.info("Waiting for detached process " + scheduledProcessEvent.getDetachableProcess().getPid() + " to complete");
                    try {
                        processHandle.onExit().get(configuration.getTimeout(), TimeUnit.MINUTES);
                    } catch (ExecutionException | TimeoutException ex) {
                        String errorMessage = "Detatched Process was killed due to not finishing in the allowed time, handle was ["+ processHandle + "]" +
                            "Job Name [" + scheduledProcessEvent.getJobName() + "]" +
                            "ContextInstanceId [" + scheduledProcessEvent.getContextInstanceId() + "] " +
                            "Timeout settings in minutes [" + configuration.getTimeout() + "]";
                        if (processHandle.destroy()) {
                            LOGGER.error(errorMessage + ". The process was zombied and may need to be manually terminated.", ex);
                        } else {
                            LOGGER.error(errorMessage, ex);
                        }
                        // Add to the execution details.
                        executionDetails = scheduledProcessEvent.getExecutionDetails();
                        executionDetails += "\n\nThe detached process did not complete in "+configuration.getTimeout()+" minutes. Killing the process. " +
                            "If more time is required, please raise this to the administrator to change the timeout setting. Note this process was detached so may not behave normally";
                    }
                }
                LOGGER.info("Pre-existing process completed [" + scheduledProcessEvent+ "]");
                // A detached process might have persisted its return value, look for it and set return code accordingly
                String statusReturnCode = scheduledProcessEvent.getDetachableProcess().getReturnCode();
                if (!NumberUtils.isParsable(statusReturnCode)) {
                    executionDetails += "\n\nWARNING : There were problems getting the return status from the detached process, it will be treated as an error, issue was " + statusReturnCode;
                }
                scheduledProcessEvent.setExecutionDetails(executionDetails);
                scheduledProcessEvent.setReturnCode(NumberUtils.toInt(statusReturnCode, DEFAULT_ERROR_RETURN_CODE));
            } else {
                Process process = scheduledProcessEvent.getDetachableProcess().getProcess();
                String executionDetails = scheduledProcessEvent.getExecutionDetails() != null ? scheduledProcessEvent.getExecutionDetails() : "";
                LOGGER.info("Waiting for new process " + process.pid() + " to complete");
                boolean completedWithinTimeout = process.waitFor(configuration.getTimeout(), TimeUnit.MINUTES);
                if (!completedWithinTimeout) {
                    process.destroy();
                    LOGGER.error("Process was killed due to not finishing in the allowed time [{}]. Job Name [{}], " +
                        "ContextInstanceId [{}], Timeout settings in minutes [{}] ", process,
                        scheduledProcessEvent.getJobName(), scheduledProcessEvent.getContextInstanceId(), configuration.getTimeout());

                    scheduledProcessEvent.setReturnCode(DEFAULT_ERROR_RETURN_CODE); // Indicate it wasn't successful
                    // Add to the execution details.
                    executionDetails += "\n\nProcess did not complete in "+configuration.getTimeout()+" minutes. Killing the process. " +
                        "If more time is required, please raise this to the administrator to change the timeout setting.";

                } else {
                    String statusReturnCode = scheduledProcessEvent.getDetachableProcess().getReturnCode();
                    if (!NumberUtils.isParsable(statusReturnCode)) {
                        executionDetails += "\n\nWARNING : There were problems getting the return status from the process, it will be treated as an error, issue was " + statusReturnCode;
                    }
                    scheduledProcessEvent.setReturnCode(NumberUtils.toInt(statusReturnCode, DEFAULT_ERROR_RETURN_CODE));
                }
                scheduledProcessEvent.setExecutionDetails(executionDetails);
                LOGGER.info("New process completed [" + scheduledProcessEvent + "]");
            }
            scheduledProcessEvent.setCompletionTime(System.currentTimeMillis());
            List<String> acceptableReturnCodes = scheduledProcessEvent.getInternalEventDrivenJob().getSuccessfulReturnCodes();
            if( (acceptableReturnCodes == null || acceptableReturnCodes.isEmpty())) {
                scheduledProcessEvent.setSuccessful(scheduledProcessEvent.getReturnCode() == 0);
            }
            else
            {
                scheduledProcessEvent.setSuccessful(false);
                for(String acceptableReturnCode:acceptableReturnCodes) {
                    if(Integer.parseInt(acceptableReturnCode) == scheduledProcessEvent.getReturnCode()) {
                        scheduledProcessEvent.setSuccessful(true);
                        break;
                    }
                }
            }

            // Only clean up persistent pid if we were not interrupted by InterruptedException i.e. module shutdown
            try {
                scheduledProcessEvent.getDetachableProcess().removePersistedProcessData();
            } catch (IOException ioe) {
                LOGGER.warn("Attempt to tidy process and results file for " + scheduledProcessEvent.getJobName() +
                    " with identity " + scheduledProcessEvent.getDetachableProcess().getIdentity() +
                    " failed, non fatal error but may require manual housekeeping of the agents pid directory", ioe);
            }
        }
        catch(InterruptedException e) {
            // If a job has failed we do not want to notify the orchestration that we have been successful.
            LOGGER.warn("process.waitFor interrupted, this could be due to the agent being stopped while processes are running, these should re-attached upon restart", e);
            throw new EndpointException(e);
        }
        scheduledProcessEvent.setDetailsFromProcess();

        return scheduledProcessEvent;
    }

    /**
     * Determine the outcome of the dry run based on the dry run parameters.
     *
     * @param scheduledProcessEvent
     */
    private void manageDryRun(ScheduledProcessEvent scheduledProcessEvent) {
        scheduledProcessEvent.setSuccessful(true);

        if(scheduledProcessEvent.getDryRunParameters().isError()) {
            // Specific executions can be configured to result in an error.
            scheduledProcessEvent.setSuccessful(false);
        }
        else {
            // otherwise determine error based on percentage probability.
            int percentUpperBound = (int) (100 / scheduledProcessEvent.getDryRunParameters().getJobErrorPercentage());

            int randomInt = new Random().nextInt(percentUpperBound);

            if(randomInt == 0) {
                scheduledProcessEvent.setSuccessful(false);
            }
        }

        long sleepTime;

        if(scheduledProcessEvent.getDryRunParameters().getFixedExecutionTimeMillis() > 0) {
            // Job execution time is configured as a fixed value.
            sleepTime = scheduledProcessEvent.getDryRunParameters().getFixedExecutionTimeMillis();
        }
        else {
            // Job execution time is configured as a random number within fixed boundaries.
            sleepTime = scheduledProcessEvent.getDryRunParameters().getMinExecutionTimeMillis()
                + (long) (Math.random() * (scheduledProcessEvent.getDryRunParameters().getMaxExecutionTimeMillis()
                - scheduledProcessEvent.getDryRunParameters().getMinExecutionTimeMillis()));
        }

        scheduledProcessEvent.setFireTime(System.currentTimeMillis());

        try {
            Thread.sleep(sleepTime);
        }
        catch (InterruptedException e) {
            // Not that much of a concern if we get an exception here.
            LOGGER.error("Error attempting to put thread to sleep when executing a dry run!", e);
        }

        scheduledProcessEvent.setCompletionTime(System.currentTimeMillis());
    }

    /**
     * Update the event with details of the job being skipped.
     *
     * @param scheduledProcessEvent
     */
    private void manageSkipped(ScheduledProcessEvent scheduledProcessEvent) {
        scheduledProcessEvent.setSuccessful(true);
        scheduledProcessEvent.setFireTime(System.currentTimeMillis());
        scheduledProcessEvent.setCompletionTime(System.currentTimeMillis());
    }

    /**
     * Update the event with details of the job being skipped.
     *
     * @param scheduledProcessEvent
     */
    private void manageDayOfWeekToRunIgnored(ScheduledProcessEvent scheduledProcessEvent) {
        scheduledProcessEvent.setOutcome(Outcome.EXECUTION_INVOKED_IGNORED_DAY_OF_WEEK);
        scheduledProcessEvent.setSuccessful(true);
        scheduledProcessEvent.setFireTime(System.currentTimeMillis());
        scheduledProcessEvent.setCompletionTime(System.currentTimeMillis());
    }

    @Override
    public String getConfiguredResourceId() {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId) {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public JobMonitoringBrokerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(JobMonitoringBrokerConfiguration configuration) {
        this.configuration = configuration;
    }
}
