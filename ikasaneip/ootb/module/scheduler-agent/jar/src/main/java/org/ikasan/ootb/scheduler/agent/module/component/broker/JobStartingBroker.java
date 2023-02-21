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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.JobStartingBrokerConfiguration;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.scheduled.event.model.Outcome;
import org.ikasan.ootb.scheduler.agent.module.model.EnrichedContextualisedScheduledProcessEvent;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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
    private static Logger logger = LoggerFactory.getLogger(JobStartingBroker.class);

    private String configuredResourceId;
    private JobStartingBrokerConfiguration configuration;
    
    private DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

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

        String[] commandLineArgs = getCommandLineArgs(scheduledProcessEvent.getInternalEventDrivenJob().getCommandLine(),
            scheduledProcessEvent.getInternalEventDrivenJob().getExecutionEnvironmentProperties());
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(commandLineArgs);


        // allow change of the new process working directory
        if(scheduledProcessEvent.getInternalEventDrivenJob().getWorkingDirectory() != null
            && scheduledProcessEvent.getInternalEventDrivenJob().getWorkingDirectory().length() > 0) {
            File workingDirectory = new File(scheduledProcessEvent.getInternalEventDrivenJob().getWorkingDirectory());
            processBuilder.directory(workingDirectory);
        }

        // We set up the std out and error log files and set them on the process builder.
        String formattedDate = formatter.format(LocalDateTime.now());
        File outputLog = new File(scheduledProcessEvent.getResultOutput());
        if(outputLog.exists()) {
            outputLog.renameTo(new File(scheduledProcessEvent.getResultOutput() + "." + formattedDate));
        }

        FileUtil.createMissingParentDirectories(outputLog);
        processBuilder.redirectOutput(outputLog);
        scheduledProcessEvent.setResultOutput(outputLog.getAbsolutePath());

        File errorLog = new File(scheduledProcessEvent.getResultError());
        if(errorLog.exists()) {
            errorLog.renameTo(new File(scheduledProcessEvent.getResultError() + "." + formattedDate));
        }

        FileUtil.createMissingParentDirectories(errorLog);
        processBuilder.redirectError(errorLog);
        scheduledProcessEvent.setResultError(errorLog.getAbsolutePath());

        // Some environments like cmd.exe will not persist an environment value if it is empty. This will identify
        // based on a list of environments provided in the broker configuration if a space should be added to the 
        // context parameter value if it empty 
        boolean addSpaceToEmptyContextParamValue = false;
        if (commandLineArgs.length > 0) {
            String targetEnvironment = commandLineArgs[0];
            if (configuration.getEnvironmentToAddSpaceForEmptyContextParam().contains(targetEnvironment)) {
                addSpaceToEmptyContextParamValue = true;
            }
        }
        
        // Add job context parameters to the process environment if there are any.
        if(scheduledProcessEvent.getContextParameters() != null
            && !scheduledProcessEvent.getContextParameters().isEmpty()) {
            Map<String, String> env = processBuilder.environment();

            final boolean finalAddSpaceToEmptyContextParamValue = addSpaceToEmptyContextParamValue; // required for lambda
            scheduledProcessEvent.getContextParameters()
                .forEach(contextParameter -> {
                    if(contextParameter.getValue() != null) {
                        env.put(contextParameter.getName(),
                            (("".equals(contextParameter.getValue()) && finalAddSpaceToEmptyContextParamValue) ? " " : contextParameter.getValue()));
                    }
                    else {
                        logger.warn("Context parameter[{}] could not be initialised on process as its value was NULL!"
                            , contextParameter.getName());
                    }
                });
        }

        try {
            // Start the process and enrich the payload.
            StringBuffer processStartString = new StringBuffer("\nExecuting Job -> Context Name[")
                .append(scheduledProcessEvent.getContextName())
                .append("] Job Name[")
                .append(scheduledProcessEvent.getInternalEventDrivenJob().getJobName())
                .append("]\n\n");

            processStartString.append("Job Parameters -> ").append("\n");

            if(scheduledProcessEvent.getContextParameters().isEmpty()) {
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


            StringBuffer commandString = new StringBuffer("Process Command -> ").append("\n");
            Arrays.stream(commandLineArgs).forEach(command -> commandString.append(command).append("\n"));

            processStartString.append("\n").append(commandString);

            logger.info(processStartString.toString());

            scheduledProcessEvent.setExecutionDetails(processStartString.toString());

            Process process = processBuilder.start();
            scheduledProcessEvent.setPid(process.pid());
            scheduledProcessEvent.setProcess(process);
        }
        catch (IOException e) {
            throw new EndpointException(e);
        }

        return scheduledProcessEvent;
    }

    String[] getCommandLineArgs(String commandLine, String executionEnvironmentProperties) {
        if(commandLine != null && commandLine.length() > 0) {
            if (executionEnvironmentProperties != null && executionEnvironmentProperties.length() > 0) {
                String[] processBuilderArgs = StringUtils.split(executionEnvironmentProperties, "|");
                if (processBuilderArgs == null || processBuilderArgs.length == 0) {
                    throw new EndpointException("Unable to split by | (pipe) [" + executionEnvironmentProperties + "] for executionEnvironmentProperties");
                }
                return ArrayUtils.add(processBuilderArgs, commandLine);
            } else {
                if (SystemUtils.OS_NAME.contains("Windows")) {
                    return new String[]{"cmd.exe", "/c", commandLine};
                } else {
                    // assume unix flavour
                    return new String[]{"/bin/bash", "-c", commandLine};
                }
            }
        }

        throw new EndpointException("Invalid commandLine [" + commandLine + "]");
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
