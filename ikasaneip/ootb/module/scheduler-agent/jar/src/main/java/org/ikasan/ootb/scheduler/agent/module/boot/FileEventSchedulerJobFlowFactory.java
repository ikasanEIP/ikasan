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
import org.ikasan.builder.OnException;
import org.ikasan.ootb.scheduler.agent.module.boot.components.FileEventSchedulerJobFlowComponentFactory;
import org.ikasan.ootb.scheduler.agent.module.component.filter.ContextInstanceFilterException;
import org.ikasan.spec.flow.Flow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

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


    public Flow create(String jobName)
    {
        return builderFactory.getModuleBuilder(moduleName).getFlowBuilder(jobName)
            .withDescription("The " + jobName + " File Event Flow is responsible for kicking off jobs when an expected file arrives.")
            .withExceptionResolver(
                builderFactory
                    .getExceptionResolverBuilder()
                    .addExceptionToAction(ContextInstanceFilterException.class, OnException.retry(agentRecoveryRetryDelay, agentRecoveryMaxRetries))
            )
            .consumer("File Consumer", componentFactory.getFileConsumer())
            .filter("Context Instance Active Filter", componentFactory.getContextInstanceFilter())
            .filter("File Age Filter", componentFactory.getFileAgeFilter())
            .filter("Duplicate Message Filter", componentFactory.getDuplicateMessageFilter(jobName))
            .broker("File Move Broker", componentFactory.getMoveFileBroker())
            .converter("JobExecution to ScheduledStatusEvent", componentFactory.getFileEventToScheduledProcessEventConverter(jobName))
            .producer("Scheduled Status Producer", componentFactory.getScheduledStatusProducer())
            .build();
    }
}


