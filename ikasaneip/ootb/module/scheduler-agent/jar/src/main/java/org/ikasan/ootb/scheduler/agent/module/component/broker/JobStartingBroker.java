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

import ch.qos.logback.core.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.JobStartingBrokerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.DetachableProcess;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.DetachableProcessBuilder;
import org.ikasan.ootb.scheduler.agent.module.service.processtracker.service.SchedulerPersistenceService;
import org.ikasan.ootb.scheduler.agent.module.model.EnrichedContextualisedScheduledProcessEvent;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.scheduled.event.model.Outcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Map;

/**
 * Job Starting Broker implementation for the execution of the command line process.
 *
 * @author Ikasan Development Team
 */
public class JobStartingBroker implements Broker<EnrichedContextualisedScheduledProcessEvent, EnrichedContextualisedScheduledProcessEvent>,
                                          ConfiguredResource<JobStartingBrokerConfiguration>
{
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobStartingBroker.class);

    public static final String LOG_FILE_PATH = "LOG_FILE_PATH";
    public static final String ERROR_LOG_FILE_PATH = "ERROR_LOG_FILE_PATH";

    private String configuredResourceId;
    private JobStartingBrokerConfiguration configuration;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private final SchedulerPersistenceService schedulerPersistenceService;
    public JobStartingBroker(SchedulerPersistenceService schedulerPersistenceService) {
        this.schedulerPersistenceService = schedulerPersistenceService;
    }

    @Override
    public EnrichedContextualisedScheduledProcessEvent invoke(EnrichedContextualisedScheduledProcessEvent scheduledProcessEvent) throws EndpointException
    {
        scheduledProcessEvent.setJobStarting(true);
        scheduledProcessEvent.setOutcome(Outcome.EXECUTION_INVOKED);

        // Skipping a job is as simple as marking the job as successful.
        if(scheduledProcessEvent.isSkipped() || scheduledProcessEvent.isDryRun() ) {
            return scheduledProcessEvent;
        }

        // If any day of weeks are defined, we only run the job on the day of the
        // week that is defined.
        if(scheduledProcessEvent.getInternalEventDrivenJob().getDaysOfWeekToRun() != null
            && !scheduledProcessEvent.getInternalEventDrivenJob().getDaysOfWeekToRun().isEmpty()
            && !scheduledProcessEvent.getInternalEventDrivenJob().getDaysOfWeekToRun()
            .contains(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))) {
            return scheduledProcessEvent;
        }

        DetachableProcessBuilder detachableProcessBuilder =
            new DetachableProcessBuilder(
                schedulerPersistenceService,
                new ProcessBuilder(),
                StringUtils.split(scheduledProcessEvent.getInternalEventDrivenJob().getExecutionEnvironmentProperties(), "|"),
                scheduledProcessEvent.getProcessIdentity()
            );
        scheduledProcessEvent.setDetachableProcess(detachableProcessBuilder.getDetachableProcess());
        detachableProcessBuilder.command(scheduledProcessEvent.getInternalEventDrivenJob().getCommandLine());

        File outputLog;
        File errorLog;

        if( ! scheduledProcessEvent.getDetachableProcess().isDetached()) {
            // This is a new process, setup appropriately
            if(scheduledProcessEvent.getInternalEventDrivenJob().getWorkingDirectory() != null
                && scheduledProcessEvent.getInternalEventDrivenJob().getWorkingDirectory().length() > 0) {
                File workingDirectory = new File(scheduledProcessEvent.getInternalEventDrivenJob().getWorkingDirectory());
                detachableProcessBuilder.directory(workingDirectory);
            }
            outputLog = new File(scheduledProcessEvent.getResultOutput());
            FileUtil.createMissingParentDirectories(outputLog);
            detachableProcessBuilder.setInitialResultOutput(outputLog.getAbsolutePath());
            String formattedDate = formatter.format(LocalDateTime.now());
            if(outputLog.exists()) {
                String newFileName = scheduledProcessEvent.getResultOutput() + "." + formattedDate;
                if (!outputLog.renameTo(new File(newFileName))) {
                    LOGGER.warn("Rename of output file to " + newFileName + " failed.");
                }
            }
            detachableProcessBuilder.redirectOutput(outputLog);

            errorLog = new File(scheduledProcessEvent.getResultError());
            FileUtil.createMissingParentDirectories(errorLog);
            detachableProcessBuilder.setInitialErrorOutput(errorLog.getAbsolutePath());
            if(errorLog.exists()) {
                String newFileName = scheduledProcessEvent.getResultError() + "." + formattedDate;
                if (!errorLog.renameTo(new File(newFileName))) {
                    LOGGER.warn("Rename of error file to " + newFileName + " failed.");
                }
            }
            detachableProcessBuilder.redirectError(errorLog);

            // Some environments like cmd.exe will not persist an environment value if it is empty. This will identify
            // based on a list of environments provided in the broker configuration if a space should be added to the
            // context parameter value if it is empty
            boolean addSpaceToEmptyContextParamValue =
                configuration.getEnvironmentToAddSpaceForEmptyContextParam().contains(
                    detachableProcessBuilder.getDetachableProcess().getCommandProcessor().getName());

            Map<String, String> env = detachableProcessBuilder.environment();
            env.put(LOG_FILE_PATH, scheduledProcessEvent.getResultOutput());
            env.put(ERROR_LOG_FILE_PATH, scheduledProcessEvent.getResultError());

            // Add job context parameters to the process environment if there are any.
            if(scheduledProcessEvent.getContextParameters() != null
                && !scheduledProcessEvent.getContextParameters().isEmpty()) {

                final boolean finalAddSpaceToEmptyContextParamValue = addSpaceToEmptyContextParamValue; // required for lambda
                scheduledProcessEvent.getContextParameters()
                    .forEach(contextParameter -> {
                        if(contextParameter.getValue() != null) {
                            env.put(contextParameter.getName(),
                                (("".equals(contextParameter.getValue()) && finalAddSpaceToEmptyContextParamValue) ? " " : contextParameter.getValue()));
                        }
                        else {
                            LOGGER.warn("Context parameter[{}] could not be initialised on process as its value was NULL!"
                                , contextParameter.getName());
                        }
                    });
            }

        } else {
            // Once detached, configuration on the process must not change, most will have been deserialized already.
            outputLog = new File(detachableProcessBuilder.getInitialResultOutput());
            errorLog = new File(detachableProcessBuilder.getInitialErrorOutput());
        }

        // These came from the event but may have been updated by a deserialized detached process, or fully qualified name.
        scheduledProcessEvent.setResultOutput(outputLog.getAbsolutePath());
        scheduledProcessEvent.setResultError(errorLog.getAbsolutePath());

        try {
            // Start the process and enrich the payload.
            StringBuffer processStartString = new StringBuffer("\nExecuting Job -> Context Name[")
                .append(scheduledProcessEvent.getContextName())
                .append("] Job Name[")
                .append(scheduledProcessEvent.getInternalEventDrivenJob().getJobName())
                .append("]\n\n");

            processStartString.append("Job Parameters -> ").append("\n");

            if(scheduledProcessEvent.getContextParameters() == null || scheduledProcessEvent.getContextParameters().isEmpty()) {
                processStartString.append("There are no parameters set on this job.\n");
            }
            else {
                scheduledProcessEvent.getContextParameters()
                    .forEach(contextParameter -> processStartString.append("Name[")
                        .append(contextParameter.getName())
                        .append("] Value[")
                        .append(contextParameter.getValue())
                        .append("]")
                        .append("\n"));
            }

            processStartString.append("Name[")
                .append(LOG_FILE_PATH)
                .append("] Value[")
                .append(scheduledProcessEvent.getResultOutput())
                .append("]")
                .append("\n");

            processStartString.append("Name[")
                .append(ERROR_LOG_FILE_PATH)
                .append("] Value[")
                .append(scheduledProcessEvent.getResultError())
                .append("]")
                .append("\n");

            StringBuffer commandString = new StringBuffer("Process Command -> ").append("\n");
            detachableProcessBuilder.command().stream().forEach(command -> commandString.append(command).append("\n\n"));
            commandString.append("\n");

            commandString.append(detachableProcessBuilder.getScriptFilePath()).append(" ->").append("\n");
            commandString.append(scheduledProcessEvent.getInternalEventDrivenJob().getCommandLine()).append("\n");

            processStartString.append(commandString);

            LOGGER.info(processStartString.toString());

            scheduledProcessEvent.setExecutionDetails(processStartString.toString());

            DetachableProcess detachableProcess = detachableProcessBuilder.start();
            scheduledProcessEvent.setPid(detachableProcess.getPid());
        }
        catch (IOException e) {
            throw new EndpointException(e);
        }

        return scheduledProcessEvent;
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
    public JobStartingBrokerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(JobStartingBrokerConfiguration configuration) {
        this.configuration = configuration;
    }
}
