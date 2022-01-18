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
import org.ikasan.ootb.scheduled.model.Outcome;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Contextualised Process Execution Broker implementation for the execution of the command line process.
 *
 * @author Ikasan Development Team
 */
public class ContextualisedProcessExecutionBroker implements Broker<ContextualisedScheduledProcessEvent, ContextualisedScheduledProcessEvent>
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(ContextualisedProcessExecutionBroker.class);

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public ContextualisedScheduledProcessEvent invoke(ContextualisedScheduledProcessEvent scheduledProcessEvent) throws EndpointException
    {
        scheduledProcessEvent.setOutcome(Outcome.EXECUTION_INVOKED);
        scheduledProcessEvent.setJobStarting(false);

        // Skipping a job is as simple as marking the job as successful
        if(scheduledProcessEvent.isSkipped()) {
            this.manageSkipped(scheduledProcessEvent);
            return scheduledProcessEvent;
        }

        if(scheduledProcessEvent.isDryRun()) {
            this.manageDryRun(scheduledProcessEvent);
            return scheduledProcessEvent;
        }

        String[] commandLineArgs = getCommandLineArgs(scheduledProcessEvent.getInternalEventDrivenJob().getCommandLine());
        ProcessBuilder processBuilder = new ProcessBuilder(commandLineArgs);

        // allow change of the new process working directory
        if(scheduledProcessEvent.getInternalEventDrivenJob().getWorkingDirectory() != null
            && scheduledProcessEvent.getInternalEventDrivenJob().getWorkingDirectory().length() > 0) {
            File workingDirectory = new File(scheduledProcessEvent.getInternalEventDrivenJob().getWorkingDirectory());
            processBuilder.directory(workingDirectory);
        }

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

        try {
            Process process = processBuilder.start();

            try {
                // We wait indefinitely until the process is finished.
                process.waitFor();

                scheduledProcessEvent.setCompletionTime(System.currentTimeMillis());
                scheduledProcessEvent.setReturnCode(process.exitValue());
                if( (scheduledProcessEvent.getInternalEventDrivenJob().getSuccessfulReturnCodes() == null
                    || scheduledProcessEvent.getInternalEventDrivenJob().getSuccessfulReturnCodes().size() == 0)) {
                    if(scheduledProcessEvent.getReturnCode() == 0) {
                        scheduledProcessEvent.setSuccessful(true);
                    }
                    else {
                        scheduledProcessEvent.setSuccessful(false);
                    }
                }
                else
                {
                    scheduledProcessEvent.setSuccessful(false);
                    for(String returnCode:scheduledProcessEvent.getInternalEventDrivenJob().getSuccessfulReturnCodes()) {
                        if(Integer.parseInt(returnCode) == scheduledProcessEvent.getReturnCode()) {
                            scheduledProcessEvent.setSuccessful(true);
                            break;
                        }
                    }
                }
            }
            catch(InterruptedException e) {
                // need to think about what we do here. If a job has failed
                // we do not want to notify the orchestration that we have
                // been successful.
                logger.debug("process.waitFor interrupted", e);
            }

            ProcessHandle.Info info = process.info();
            if(info != null && !info.user().isEmpty()) {
                scheduledProcessEvent.setUser( info.user().get() );
            }

            if(info != null && !info.commandLine().isEmpty()) {
                scheduledProcessEvent.setCommandLine( info.commandLine().get() );
            }
            else {
                scheduledProcessEvent.setCommandLine(scheduledProcessEvent.getInternalEventDrivenJob().getCommandLine());
            }

            scheduledProcessEvent.setPid( process.pid() );
        }
        catch (IOException e) {
            throw new EndpointException(e);
        }

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
            logger.error("Error attempting to put thread to sleep when executing a dry run!", e);
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

    String[] getCommandLineArgs(String commandLine)
    {
        if(commandLine != null && commandLine.length() > 0) {
            return commandLine.split(" ");
        }

        throw new EndpointException("Invalid commandLine [" + commandLine + "]");
    }
}
