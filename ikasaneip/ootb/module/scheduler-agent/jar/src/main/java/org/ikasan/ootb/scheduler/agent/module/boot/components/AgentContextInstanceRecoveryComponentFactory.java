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
package org.ikasan.ootb.scheduler.agent.module.boot.components;

import jakarta.annotation.Resource;
import org.ikasan.ootb.scheduler.agent.module.boot.recovery.AgentInstanceRecoveryManager;
import org.ikasan.spec.dashboard.ContextInstanceRestService;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.provision.ContextInstanceIdentifierProvisionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Agent context instances recovery component factory.
 *
 * @author Ikasan Development Team
 */
@Configuration
public class AgentContextInstanceRecoveryComponentFactory {

    @Value("${context.instance.recovery.minutes.to.retry:120}")
    private long minutesToKeepRetrying;

    @Value("${context.instance.recovery.active:true}")
    boolean agentRecoveryActive;

    @Value("${module.name}")
    private String moduleName;

    @Resource
    private ContextInstanceRestService contextInstanceRestService;

    @Resource
    private ContextInstanceIdentifierProvisionService contextInstanceIdentifierProvisionService;

    @Bean
    @DependsOn("moduleLoader")
    public AgentInstanceRecoveryManager agentInstanceRecoveryManager(ModuleService moduleService) {
        return new AgentInstanceRecoveryManager(this.contextInstanceRestService,
            this.contextInstanceIdentifierProvisionService,
            this.minutesToKeepRetrying, this.agentRecoveryActive, this.moduleName, moduleService);
    }
}
