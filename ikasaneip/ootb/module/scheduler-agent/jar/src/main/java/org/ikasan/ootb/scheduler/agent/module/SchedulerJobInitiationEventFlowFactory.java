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

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.OnException;
import org.ikasan.component.endpoint.util.producer.DevNull;
import org.ikasan.ootb.scheduler.agent.module.ComponentFactory;
import org.ikasan.ootb.scheduler.agent.module.component.BlackoutRouter;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Flow factory implementation.
 *
 * @author Ikasan Development Team
 */
@Configuration
public class SchedulerJobInitiationEventFlowFactory
{
    @Value( "${module.name}" )
    private String moduleName;

    @Resource
    private BuilderFactory builderFactory;

    @Resource
    private Consumer bigQueueConsumer;

    @Bean
    public Flow schedulerJobInitiationEventFlow()
    {
        return builderFactory.getModuleBuilder(moduleName).getFlowBuilder("Scheduler Job Initiation Event Flow")
            .withDescription("Scheduler Job Initiation Event Flow")
            .withExceptionResolver( builderFactory.getExceptionResolverBuilder().addExceptionToAction(Exception.class, OnException.retryIndefinitely()))
            .consumer("Scheduler Job Initiation Event Consumer", this.bigQueueConsumer)
            .producer("Dev Null", new DevNull<>())
            .build();
    }
}


