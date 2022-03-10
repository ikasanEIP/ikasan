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
package org.ikasan.ootb.scheduler.agent.module;

import org.ikasan.ootb.scheduler.agent.module.boot.*;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * Flow factory implementation.
 *
 * @author Ikasan Development Team
 */
@Configuration
public class SchedulerAgentFlowFactory implements FlowFactory
{
    private static Logger logger = LoggerFactory.getLogger(SchedulerAgentFlowFactory.class);

    @Value( "${module.name}" )
    String moduleName;

    @Resource
    JobProcessingFlowFactory schedulerJobProcessingFlowFactory;

    @Resource
    QuartzSchedulerJobEventFlowFactory quartzSchedulerJobEventFlowFactory;

    @Resource
    FileEventSchedulerJobFlowFactory fileEventSchedulerJobFlowFactory;

    @Resource
    LegacyJobProcessingFlowFactory legacyJobProcessingFlowFactory;

    @Resource
    ScheduledProcessEventOutboundFlowFactory scheduledProcessEventOutboundFlowFactory;

    @Resource
    HousekeepLogFilesFlowFactory housekeepLogFilesFlowFactory;

    @Override
    public List<Flow> create(String jobName, String profile)
    {
        try {
            logger.info("Loading job: " + jobName + " with profile " + profile);
            if(profile == null) {
                profile = "LEGACY";
                logger.info("Jobs with NULL profile are reverted to the legacy LEGACY profile");
            }
            switch (profile) {
                case "FILE": {
                    return this.createFileEventFlows(jobName);
                }
                case "SCHEDULER_JOB": {
                    return this.createSchedulerJobFlows(jobName);
                }
                case "QUARTZ": {
                    return this.createQuartzFlows(jobName);
                }
                case "LEGACY": {
                    return this.createLegacyEventFlows(jobName);
                }
                case "OUTBOUND": {
                    return this.createOutboundScheduledEventFlows();
                }
                case "HOUSEKEEP_LOG": {
                    return this.createHousekeepLogFilesFlows();
                }
                default: {
                    throw new RuntimeException(String.format("Unknown profile[%s] encountered in flow factory!", profile));
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException("An exception has occurred creating scheduler agent jobs!", e);
        }
    }

    /**
     * Helper method to create the quartz driven job flows.
     *
     * @param jobName
     * @return
     */
    private List<Flow> createQuartzFlows(String jobName) throws IOException {
        return List.of(this.quartzSchedulerJobEventFlowFactory.create(jobName));
    }

    /**
     * Helper method to create the file event flows.
     *
     * @param jobName
     * @return
     */
    private List<Flow> createFileEventFlows(String jobName) {
        return List.of(this.fileEventSchedulerJobFlowFactory.create(jobName));
    }

    /**
     * Helper method to create internal event flows.
     *
     * @param jobName
     * @return
     */
    private List<Flow> createSchedulerJobFlows(String jobName) throws IOException {
        return List.of(this.schedulerJobProcessingFlowFactory.create(jobName));
    }

    /**
     * Helper method to create legacy event flows.
     *
     * @param jobName
     * @return
     */
    private List<Flow> createLegacyEventFlows(String jobName) {
        return List.of(this.legacyJobProcessingFlowFactory.create(jobName));
    }

    /**
     * Helper method that creates the outbound flows to publish scheduled events to the dashboard.
     *
     * @return
     * @throws IOException
     */
    private List<Flow> createOutboundScheduledEventFlows() throws IOException {
        return List.of(this.scheduledProcessEventOutboundFlowFactory.create());
    }

    private List<Flow> createHousekeepLogFilesFlows() throws IOException {
        return List.of(this.housekeepLogFilesFlowFactory.create());
    }
}


