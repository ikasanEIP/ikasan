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
import org.ikasan.module.startup.StartupControlImpl;
import org.ikasan.module.startup.dao.StartupControlDao;
import org.ikasan.ootb.data.sharing.module.boot.components.WiretapEntityProducerFlowComponentFactory;
import org.ikasan.ootb.data.sharing.module.configuration.DataSharingConfiguredModuleConfiguration;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


/**
 * Factory class responsible for creating and configuring flows dedicated to processing wiretap entity
 * data and publishing it to specified target dashboards. This class is annotated as a Spring
 * {@code @Configuration} to allow for dependency injection and configuration management.
 *
 * The flow created by this factory is tailored for managing data pipelines involving
 * reading wiretap entity data from a source, processing it, and pushing the results
 * to a downstream dashboard. It employs a consumer-producer architecture and leverages components
 * built using an injected {@link BuilderFactory}.
 *
 * Dependencies used in this class include:
 * - {@link BuilderFactory}: For building the flow and its components.
 * - {@link WiretapEntityProducerFlowComponentFactory}: For creating the consumer components.
 * - {@link StartupControlDao}: For managing startup configuration data for flows.
 * - {@code DataSharingConfiguredModuleConfiguration}: For retrieving downstream dashboard configuration.
 * - {@link WiretapProducerFactory}: For getting the wiretap producer component for publishing data.
 *
 * This factory ensures that each flow is initialized with startup control set to 'AUTOMATIC',
 * making it ready for execution as soon as the application is deployed.
 */
@Configuration
public class WiretapEntityProducerFlowFactory {
    @Value( "${module.name}" )
    private String moduleName;

    @Autowired
    private BuilderFactory builderFactory;

    @Autowired
    WiretapEntityProducerFlowComponentFactory componentFactory;

    @Autowired
    StartupControlDao startupControlDao;

    @Autowired
    DataSharingConfiguredModuleConfiguration dataSharingConfiguredModuleConfiguration;

    @Autowired
    WiretapProducerFactory wiretapProducerFactory;

    /**
     * Creates and configures a new flow for processing wiretap entity data and publishing it
     * to the specified target dashboard.
     *
     * @param targetDashboardName the name of the target dashboard to which data will be published
     * @return the created and configured flow
     * @throws IOException if an I/O error occurs during flow creation
     */
    public Flow create(String targetDashboardName) throws IOException {
        StartupControl startupControl = new StartupControlImpl(moduleName, targetDashboardName);
        startupControl.setStartupType(StartupType.AUTOMATIC);
        this.startupControlDao.save(startupControl);

        String flowName = targetDashboardName + " Wiretap Entity Producer Flow";

        return builderFactory.getModuleBuilder(moduleName).getFlowBuilder(flowName)
            .withDescription("The [" + flowName +"] flow is responsible for determining if a file " +
                "reading wiretap entity data and publishing it to [" +
                this.dataSharingConfiguredModuleConfiguration.getDownStreamDashboards().get(targetDashboardName) + "]")
            .consumer("Wiretap Entity BigQueue Consumer",
                componentFactory.bigQueueConsumer(targetDashboardName))
            .producer("Wiretap Entity REST Dashboard Publishing Producer",
                wiretapProducerFactory.getWiretapProducer(targetDashboardName))
            .build();
    }
}


