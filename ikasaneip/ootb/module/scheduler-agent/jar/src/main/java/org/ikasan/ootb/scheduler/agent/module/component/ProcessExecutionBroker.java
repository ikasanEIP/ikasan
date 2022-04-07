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
package org.ikasan.ootb.scheduler.agent.module.component;

import ch.qos.logback.core.util.FileUtil;
import org.ikasan.ootb.scheduled.model.Outcome;
import org.ikasan.spec.scheduled.ScheduledProcessEvent;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Process Execution Broker implementation for the execution of the command line process.
 *
 * @author Ikasan Development Team
 */
public class ProcessExecutionBroker implements Broker<ScheduledProcessEvent, ScheduledProcessEvent>,
    ConfiguredResource<ProcessExecutionBrokerConfiguration>
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(ProcessExecutionBroker.class);

    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    String configuredResourceId;
    ProcessExecutionBrokerConfiguration configuration = new ProcessExecutionBrokerConfiguration();

    String hostname;

    public ProcessExecutionBroker(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public ScheduledProcessEvent invoke(ScheduledProcessEvent scheduledProcessEvent) throws EndpointException
    {
        scheduledProcessEvent.setOutcome(Outcome.EXECUTION_INVOKED);
        scheduledProcessEvent.setAgentHostname(this.hostname);

        String[] commandLineArgs = getCommandLineArgs(configuration.getCommandLine());
        ProcessBuilder processBuilder = new ProcessBuilder(commandLineArgs);

        // allow change of the new process working directory
        if(configuration.getWorkingDirectory() != null && configuration.getWorkingDirectory().length() > 0)
        {
            File workingDirectory = new File(configuration.getWorkingDirectory());
            processBuilder.directory(workingDirectory);
        }

        String formattedDate = formatter.format(LocalDateTime.now());
        if(configuration.getStdOut() != null && configuration.getStdOut().length() > 0)
        {
            File outputLog = new File(configuration.getStdOut());
            if(outputLog.exists())
            {
                outputLog.renameTo(new File(configuration.getStdOut() + "." + formattedDate));
            }

            FileUtil.createMissingParentDirectories(outputLog);
            processBuilder.redirectOutput(outputLog);
            scheduledProcessEvent.setResultOutput(outputLog.getAbsolutePath());

            if(configuration.getStdOut().equals(configuration.getStdErr()))
            {
                processBuilder.redirectError(outputLog);
                scheduledProcessEvent.setResultError(outputLog.getAbsolutePath());
            }
        }
        else
        {
            processBuilder.redirectOutput();
        }

        if(configuration.getStdErr() != null && configuration.getStdErr().length() > 0)
        {
            if(configuration.getStdErr() != configuration.getStdOut())
            {
                File errorLog = new File(configuration.getStdErr());
                if(errorLog.exists())
                {
                    errorLog.renameTo(new File(configuration.getStdErr() + "." + formattedDate));
                }

                FileUtil.createMissingParentDirectories(errorLog);
                processBuilder.redirectError(errorLog);
                scheduledProcessEvent.setResultError(errorLog.getAbsolutePath());
            }
        }
        else
        {
            processBuilder.redirectError();
        }

        try
        {
            Process process = processBuilder.start();

            try
            {
                boolean processFinished = process.waitFor(configuration.getSecondsToWaitForProcessStart(), TimeUnit.SECONDS);
                if(processFinished)
                {
                    scheduledProcessEvent.setCompletionTime(System.currentTimeMillis());
                    scheduledProcessEvent.setReturnCode(process.exitValue());
                    if( (configuration.getSuccessfulReturnCodes() == null || configuration.getSuccessfulReturnCodes().size() == 0))
                    {
                        if(scheduledProcessEvent.getReturnCode() == 0)
                        {
                            scheduledProcessEvent.setSuccessful(true);
                        }
                        else
                        {
                            scheduledProcessEvent.setSuccessful(false);
                        }
                    }
                    else
                    {
                        scheduledProcessEvent.setSuccessful(false);
                        for(String returnCode:configuration.getSuccessfulReturnCodes())
                        {
                            if(Integer.parseInt(returnCode) == scheduledProcessEvent.getReturnCode())
                            {
                                scheduledProcessEvent.setSuccessful(true);
                                break;
                            }
                        }
                    }
                }
                else
                {
                    scheduledProcessEvent.setSuccessful(false);
                    if(process.isAlive())
                    {
                        scheduledProcessEvent.setSuccessful(true);
                    }
                }
            }
            catch(InterruptedException e)
            {
                logger.debug("process.waitFor interrupted", e);
            }

            ProcessHandle.Info info = process.info();
            if(info != null && !info.user().isEmpty())
            {
                scheduledProcessEvent.setUser( info.user().get() );
            }

            if(info != null && !info.commandLine().isEmpty())
            {
                scheduledProcessEvent.setCommandLine( info.commandLine().get() );
            }
            else
            {
                scheduledProcessEvent.setCommandLine( configuration.getCommandLine() );
            }

            scheduledProcessEvent.setPid( process.pid() );
        }
        catch (IOException e)
        {
            throw new EndpointException(e);
        }

        return scheduledProcessEvent;
    }

    String[] getCommandLineArgs(String commandLine)
    {
        if(commandLine != null && commandLine.length() > 0)
        {
            return commandLine.split(" ");
        }

        throw new EndpointException("Invalid commandLine [" + commandLine + "]");
    }

    @Override
    public String getConfiguredResourceId()
    {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public ProcessExecutionBrokerConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(ProcessExecutionBrokerConfiguration configuration)
    {
        this.configuration = configuration;
    }

}
