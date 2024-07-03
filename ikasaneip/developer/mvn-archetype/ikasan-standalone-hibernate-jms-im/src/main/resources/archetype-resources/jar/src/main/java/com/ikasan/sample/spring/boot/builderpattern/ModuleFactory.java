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

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.flow.visitorPattern.invoker.FilterInvokerConfiguration;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class ModuleFactory
{
    @Resource
    private BuilderFactory builderFactory;

    @Resource
    private ComponentFactory componentFactory;

    @Bean
    public Module getModule()
    {

        // get the builders
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("${artifactId}");

        Flow sourceFlow = moduleBuilder.getFlowBuilder("${sourceFlowName}")
            .withDescription("Sample DB to JMS flow")
            .withExceptionResolver( componentFactory.getSourceFlowExceptionResolver() )
            .consumer("DB Consumer", componentFactory.getDBConsumer())
            .filter("My Filter", componentFactory.getFilter(), new FilterInvokerConfiguration())
            .splitter("Split list", componentFactory.getListSplitter())
            .converter("Person to XML", componentFactory.getObjectToXmlStringConverter())
            .producer("JMS Producer", componentFactory.getJmsProducer()).build();

        Flow targetFlow = moduleBuilder.getFlowBuilder("${targetFlowName}")
            .withDescription("Sample JMS to DB flow")
            .consumer("JMS Consumer", componentFactory.getJmsConsumer())
            .converter("XML to Person", componentFactory.getXmlToObjectConverter())
            .producer("DB Producer", componentFactory.getDBProducer()).build();

        Module module = moduleBuilder.withDescription("Sample DB consumer / producer module.")
            .addFlow(sourceFlow)
            .addFlow(targetFlow)
            .build();

        return module;
    }
}
