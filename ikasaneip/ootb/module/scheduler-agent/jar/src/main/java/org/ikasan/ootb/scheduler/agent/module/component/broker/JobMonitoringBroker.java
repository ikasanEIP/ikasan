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

import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.JobMonitoringBrokerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.model.EnrichedContextualisedScheduledProcessEvent;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Job Monitoring Broker implementation for the execution of the command line process.
 *
 * @author Ikasan Development Team
 */
public class JobMonitoringBroker implements Broker<EnrichedContextualisedScheduledProcessEvent, EnrichedContextualisedScheduledProcessEvent>,
                                            ConfiguredResource<JobMonitoringBrokerConfiguration>
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(JobMonitoringBroker.class);

    private String configuredResourceId;
    private JobMonitoringBrokerConfiguration configuration;

    @Override
    public EnrichedContextualisedScheduledProcessEvent invoke(EnrichedContextualisedScheduledProcessEvent scheduledProcessEvent) throws EndpointException
    {
        scheduledProcessEvent.setJobStarting(false);

        // if it is skipped or dryRun, the event is already sent to Dashboard
        // so we can stop the flow here for them
        if(scheduledProcessEvent.isSkipped() || scheduledProcessEvent.isDryRun()) {
            return null;
        }

        try {
            Process process = scheduledProcessEvent.getProcess();

            try {
                // We wait indefinitely until the process is finished.
                process.waitFor(configuration.getTimeout(), TimeUnit.MINUTES);

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

        }
        catch (Exception e) {
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
    public JobMonitoringBrokerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(JobMonitoringBrokerConfiguration configuration) {
        this.configuration = configuration;
    }
}
