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
import org.ikasan.ootb.scheduler.agent.module.boot.components.JobProcessingFlowComponentFactory;
import org.ikasan.ootb.scheduler.agent.module.component.router.BlackoutRouter;
import org.ikasan.spec.flow.Flow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Flow factory implementation.
 *
 * @author Ikasan Development Team
 */
@Configuration
public class JobProcessingFlowFactory
{
    @Value( "${module.name}" )
    String moduleName;

    @Resource
    BuilderFactory builderFactory;

    @Resource
    JobProcessingFlowComponentFactory componentFactory;


    public Flow create(String jobName) throws IOException {
        return builderFactory.getModuleBuilder(moduleName).getFlowBuilder(jobName)
            .withDescription(jobName +" Job Processing Flow")
            .consumer("Job Consumer", componentFactory.bigQueueConsumer(jobName))
            .converter("JobInitiationEvent to ScheduledStatusEvent", componentFactory.getJobInitiationEventConverter())
            .broker("Job Started Broker", this.componentFactory.getScheduledProcessEventJobStartBroker())
            .broker("Process Execution Broker", componentFactory.getProcessExecutionBroker())
            .producer("Scheduled Status Producer", componentFactory.getScheduledStatusProducer())
            .build();
    }
}


