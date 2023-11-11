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
package com.ikasan.sample.spring.boot.builderpattern;

import jakarta.annotation.Resource;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.builder.OnException;
import org.ikasan.builder.invoker.Configuration;
import org.ikasan.component.endpoint.consumer.api.spec.EndpointEventProvider;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ikasan Development Team
 */
@org.springframework.context.annotation.Configuration
public class ModuleConfig
{
    @Resource
    private BuilderFactory builderFactory;
    @Resource
    private ComponentFactory componentFactory;

    public static int  EVENT_GENERATOR_COUNT = 10000;

    @Bean
    public Module getModule()
    {
        // get the builders
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("Component Stress Test Module");

        // event generating flow
        Flow splitterPerformanceFlow = moduleBuilder
                .getFlowBuilder("splitter stress flow")
                .withDescription("Stress test splitter.")
                .consumer("Event Generating Consumer", builderFactory.getComponentBuilder()
                        .eventGeneratingConsumer().setEndpointEventProvider( new TechEndpointEventProvider()))
                .splitter("splitter", builderFactory.getComponentBuilder().listSplitter(), Configuration.splitterInvoker().withSplitAsIndividualEvents())
                .producer("Logging Producer", builderFactory.getComponentBuilder().logProducer()).build();

        Module module = moduleBuilder.withDescription("Component Stress Test Flows.")
                .addFlow(splitterPerformanceFlow)
            .build();

        return module;
    }

    class TechEndpointEventProvider implements EndpointEventProvider<List<String>>
    {
        long count;
        List<String> event = new ArrayList<String>();
        boolean rollback;

        @Override
        public List<String> getEvent()
        {
            if(rollback)
            {
                rollback = false;
                return event;
            }

            event.clear();
            for(int i=0; i<5 && count < EVENT_GENERATOR_COUNT; i++)
            {
                event.add("Test Message " + ++count);
            }

            if(event.size() > 0)
            {
                return event;
            }

            return null;
        }

        @Override
        public void rollback()
        {
            this.rollback = true;
        }
    }
}
