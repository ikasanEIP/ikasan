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
package org.ikasan.ootb.data.sharing.module.boot.components;

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.dashboard.DashboardRestServiceImpl;
import org.ikasan.filter.duplicate.service.DuplicateFilterService;
import org.ikasan.ootb.data.sharing.module.cache.EntityBigQueueCache;
import org.ikasan.ootb.data.sharing.module.component.endpoint.producer.ExclusionEventRestProducer;
import org.ikasan.ootb.data.sharing.module.configuration.DataSharingConfiguredModuleConfiguration;
import org.ikasan.ootb.data.sharing.module.serialiser.ExclusionEventToBigQueueMessageSerialiser;
import org.ikasan.ootb.data.sharing.module.util.BigQueueNameHelper;
import org.ikasan.ootb.data.sharing.module.util.DataSharingEnvironment;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.IOException;

import static org.ikasan.dashboard.DashboardClientAutoConfiguration.EXCLUSION_PATH;
import static org.ikasan.ootb.data.sharing.module.DataSharingEntity.EXCLUSION_ENTITY;
import static org.ikasan.spec.dashboard.DashboardRestService.*;
import static org.ikasan.spec.dashboard.DashboardRestService.DASHBOARD_PASSWORD_PROPERTY;


/**
 * Factory class for creating flow components related to the Exclusion Entity Producer.
 * This class is used to configure and provide components such as message consumers
 * and producers for handling exclusion entity interactions.
 *
 * This factory leverages dependencies like the {@link BuilderFactory},
 * {@link DuplicateFilterService}, and {@link DashboardRestService} to construct
 * the required components.
 *
 * This class is annotated with {@code @Configuration}, indicating that it provides
 * Spring-managed beans for application configuration.
 */
@Configuration
public class ExclusionEntityProducerFlowComponentFactory {

    @Autowired
    BuilderFactory builderFactory;

    @Autowired
    DataSharingConfiguredModuleConfiguration dataSharingConfiguredModuleConfiguration;

    /**
     * Creates and returns a {@link Producer} instance specifically configured for exclusion data sharing
     * with a downstream dashboard.
     *
     * @param targetDashboardName the name of the target dashboard for which the exclusion producer
     *                            will be created. This is used to fetch the corresponding dashboard URL,
     *                            username, and password from the module configuration.
     * @return a {@link Producer} instance configured to communicate with the specified target dashboard.
     */
    public Producer getExclusionProducer(String targetDashboardName) {
        DataSharingEnvironment environment = new DataSharingEnvironment();
        environment.setProperty(DASHBOARD_EXTRACT_ENABLED_PROPERTY, "true");
        environment.setProperty(DASHBOARD_BASE_URL_PROPERTY
            , dataSharingConfiguredModuleConfiguration.getDownStreamDashboards().get(targetDashboardName));
        environment.setProperty(DASHBOARD_USERNAME_PROPERTY
            , dataSharingConfiguredModuleConfiguration.getDownStreamDashboardUsernames().get(targetDashboardName));
        environment.setProperty(DASHBOARD_PASSWORD_PROPERTY
            , dataSharingConfiguredModuleConfiguration.getDownStreamDashboardPasswords().get(targetDashboardName));

        DashboardRestService exclusionDashboardRestService
            = new DashboardRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory(), EXCLUSION_PATH);

        return new ExclusionEventRestProducer(exclusionDashboardRestService);
    }

    /**
     * Creates a BigQueueConsumer component for consuming messages from a specified inbound queue.
     *
     * @param targetDashboardName the identifier for the consumer thread
     * @return a Consumer object representing the BigQueueConsumer component
     * @throws IOException if an I/O error occurs
     */
    public Consumer bigQueueConsumer(String targetDashboardName) throws IOException {
        IBigQueue dashboardEntityQueue = EntityBigQueueCache.instance()
            .get(BigQueueNameHelper.getQueueName(targetDashboardName, EXCLUSION_ENTITY));

        return builderFactory.getComponentBuilder().bigQueueConsumer()
            .setInboundQueue(dashboardEntityQueue)
            .setPutErrorsToBackOfQueue(false)
            .setSerialiser(new ExclusionEventToBigQueueMessageSerialiser())
            .build();
    }

}

