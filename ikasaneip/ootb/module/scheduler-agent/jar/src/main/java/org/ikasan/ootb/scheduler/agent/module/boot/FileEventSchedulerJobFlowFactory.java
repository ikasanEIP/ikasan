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
package org.ikasan.ootb.scheduler.agent.module.boot;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.module.startup.StartupControlImpl;
import org.ikasan.module.startup.dao.StartupControlDao;
import org.ikasan.ootb.scheduler.agent.module.boot.components.FileEventSchedulerJobFlowComponentFactory;
import org.ikasan.ootb.scheduler.agent.module.component.router.BlackoutRouter;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * File event scheduler job flow factory.
 *
 * @author Ikasan Development Team
 */
@Configuration
public class FileEventSchedulerJobFlowFactory
{
    @Value( "${module.name}" )
    private String moduleName;

    @Value("${agent.recovery.instance.exception.retry.delay.millis:5000}")
    private long agentRecoveryRetryDelay;

    // -1 means retry indefinitely
    @Value("${agent.recovery.instance.exception.max.retries:-1}")
    private int agentRecoveryMaxRetries;

    @Resource
    private BuilderFactory builderFactory;

    @Resource
    FileEventSchedulerJobFlowComponentFactory componentFactory;

    @Resource
    StartupControlDao startupControlDao;


    public Flow create(String jobName) throws IOException {
        StartupControl startupControl = new StartupControlImpl(moduleName, jobName);
        startupControl.setStartupType(StartupType.AUTOMATIC);
        this.startupControlDao.save(startupControl);

        return builderFactory.getModuleBuilder(moduleName).getFlowBuilder(jobName)
            .withDescription("The [" + jobName +"] flow is responsible for determining if a file exists and raising an event if it does.")
            .consumer("File Event BigQueue Consumer", componentFactory.bigQueueConsumer(jobName.toLowerCase().replace(" ", "-")))
            .broker("File Matching Broker", componentFactory.correlatingFileMatcherBroker())
            .filter("File Age Filter", componentFactory.getFileAgeFilter())
            .filter("Duplicate Message Filter", componentFactory.getDuplicateMessageFilter())
            .broker("File Move Broker", componentFactory.getMoveFileBroker())
            .singleRecipientRouter("Blackout Router", componentFactory.getBlackoutRouter())
            .when(BlackoutRouter.OUTSIDE_BLACKOUT_PERIOD, builderFactory.getRouteBuilder()
                .converter("FileWatcherJobEvent to ScheduledStatusEvent", componentFactory.getFileEventToScheduledProcessEventConverter())
                .producer("Scheduled Status Producer", componentFactory.getScheduledStatusProducer()))
            .otherwise(builderFactory.getRouteBuilder()
                .converter("FileWatcherJobEvent to ScheduledStatusEvent", componentFactory.getFileEventToScheduledProcessEventConverter())
                .filter("Publish Scheduled Status", componentFactory.getScheduledStatusFilter())
                .producer("Blackout Scheduled Status Producer", componentFactory.getScheduledStatusProducer()));
    }
}


