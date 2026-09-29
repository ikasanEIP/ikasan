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
package org.ikasan.ootb.data.sharing.module.boot;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.flow.configuration.FlowComponentInvokerConfiguration;
import org.ikasan.flow.visitorPattern.invoker.InvokerConfiguration;
import org.ikasan.module.startup.StartupControlImpl;
import org.ikasan.module.startup.dao.StartupControlDao;
import org.ikasan.ootb.data.sharing.module.boot.components.WiretapEntityConsumerFlowComponentFactory;
import org.ikasan.ootb.data.sharing.module.configuration.DataSharingConfiguredModuleConfiguration;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Factory class responsible for creating a Flow to handle the consumption of wiretap entities
 * and publishing them to an internal Big Queue for a specific target dashboard.
 *
 * This class uses the builder pattern, facilitated by the BuilderFactory, to construct
 * flows dynamically. The flows are configured to:
 * - Consume wiretap entities from an upstream dashboard.
 * - Publish the consumed entities to an internal Big Queue.
 *
 * Dependencies:
 * - {@link BuilderFactory}: Used to create and manage module and flow builders.
 * - {@link WiretapEntityConsumerFlowComponentFactory}: Provides components for the flow's consumer and producer.
 * - {@link StartupControlDao}: Manages the startup control state of the flow.
 * - {@link DataSharingConfiguredModuleConfiguration}: Provides configuration details such as upstream dashboard names.
 *
 * Configuration:
 * - The module name is injected via the property "module.name".
 *
 * Responsibilities:
 * - Defines and manages startup behavior for the flow, such as setting it to automatic startup.
 * - Creates invoker configurations for dynamic behavior in the flow.
 * - Builds and returns a fully configured Flow instance.
 */
@Configuration
public class WiretapEntityConsumerFlowFactory {
    @Value( "${module.name}" )
    private String moduleName;

    @Autowired
    private BuilderFactory builderFactory;

    @Autowired
    WiretapEntityConsumerFlowComponentFactory componentFactory;

    @Autowired
    StartupControlDao startupControlDao;

    @Autowired
    DataSharingConfiguredModuleConfiguration dataSharingConfiguredModuleConfiguration;

    /**
     * Creates a Flow for handling wiretap entity consumption and publishing them
     * to an internal Big Queue for the specified target dashboard.
     *
     * @param targetDashboardName the name of the target dashboard for which the flow is created
     * @return the constructed Flow configured with the consumer and producer components
     * @throws IOException if an I/O error occurs during the creation of the flow
     */
    public Flow create(String targetDashboardName) throws IOException {
        StartupControl startupControl = new StartupControlImpl(moduleName, targetDashboardName);
        startupControl.setStartupType(StartupType.AUTOMATIC);
        this.startupControlDao.save(startupControl);

        String flowName = targetDashboardName + " Wiretap Entity Consumer Flow";

        InvokerConfiguration scheduledConsumerInvokerConfiguration = new InvokerConfiguration();
        scheduledConsumerInvokerConfiguration.setDynamicConfiguration(true);

        return builderFactory.getModuleBuilder(moduleName).getFlowBuilder(flowName)
            .withDescription("The [" + flowName +"] flow is responsible for consuming " +
                "wiretap entities from [" + dataSharingConfiguredModuleConfiguration.getUpstreamDashboard() + "] " +
                "and publishing them to an internal Big Queue.")
            .consumer("Wiretap Entity Scheduled Consumer",
                componentFactory.getScheduledConsumer(targetDashboardName)
                , scheduledConsumerInvokerConfiguration)
            .producer("Wiretap Entity Big Queue Producer",
                componentFactory.getBigQueueProducer(targetDashboardName))
            .build();
    }
}


