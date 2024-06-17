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
package org.ikasan.ootb.scheduler.agent;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.ootb.scheduler.agent.module.SchedulerAgentFlowFactory;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;

@org.springframework.boot.test.context.TestConfiguration
public class TestConfiguration {

    @Value( "${module.name}" )
    String moduleName;

    @Resource
    BuilderFactory builderFactory;

    @Resource
    SchedulerAgentFlowFactory schedulerAgentFlowFactory;


    @Bean(name = "scheduler-agent-module")
    @Primary
    public Module createTestBeanModule()
    {
        SchedulerAgentConfiguredModuleConfiguration configuration = new SchedulerAgentConfiguredModuleConfiguration();
        configuration.getFlowDefinitions().put("Scheduler Flow 1", "MANUAL");
        configuration.getFlowDefinitionProfiles().put("Scheduler Flow 1", "SCHEDULER_JOB");
        configuration.getFlowDefinitions().put("Scheduler Flow 2", "MANUAL");
        configuration.getFlowDefinitionProfiles().put("Scheduler Flow 2", "FILE");
        configuration.getFlowDefinitions().put("Scheduler Flow 4", "MANUAL");
        configuration.getFlowDefinitionProfiles().put("Scheduler Flow 4", "QUARTZ");
        configuration.getFlowDefinitions().put("Scheduled Process Event Outbound Flow", "MANUAL");
        configuration.getFlowDefinitionProfiles().put("Scheduled Process Event Outbound Flow", "OUTBOUND");
        configuration.getFlowDefinitions().put("Housekeep Log Files Flow", "MANUAL");
        configuration.getFlowDefinitionProfiles().put("Housekeep Log Files Flow", "HOUSEKEEP_LOG");

        configuration.setDryRunMode(false);

        // get the module builder
        return builderFactory.getModuleBuilder(moduleName)
            .withDescription("Scheduler Agent Integration Module.")
            .withType(ModuleType.SCHEDULER_AGENT)
            .withFlowFactory(schedulerAgentFlowFactory)
            .setConfiguration(configuration)
            .build();
    }
}


