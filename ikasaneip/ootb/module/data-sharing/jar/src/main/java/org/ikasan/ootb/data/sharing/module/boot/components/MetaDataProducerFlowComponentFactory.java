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
import org.ikasan.component.router.multirecipient.RecipientListRouter;
import org.ikasan.dashboard.DashboardRestServiceImpl;
import org.ikasan.ootb.data.sharing.module.cache.EntityBigQueueCache;
import org.ikasan.ootb.data.sharing.module.component.endpoint.producer.ConfigurationMetaDataRestProducer;
import org.ikasan.ootb.data.sharing.module.component.endpoint.producer.ModuleMetaDataRestProducer;
import org.ikasan.ootb.data.sharing.module.component.splitter.ConfigurationMetaDataSplitter;
import org.ikasan.ootb.data.sharing.module.component.splitter.ModuleMetaDataSplitter;
import org.ikasan.ootb.data.sharing.module.configuration.DataSharingConfiguredModuleConfiguration;
import org.ikasan.ootb.data.sharing.module.model.ModuleAndConfigMetaData;
import org.ikasan.ootb.data.sharing.module.serialiser.ModuleAndConfigMetaDataToBigQueueMessageSerialiser;
import org.ikasan.ootb.data.sharing.module.util.BigQueueNameHelper;
import org.ikasan.ootb.data.sharing.module.util.DataSharingEnvironment;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.dashboard.DataSharingRestService;
import org.ikasan.spec.metadata.model.ConfigurationMetaData;
import org.ikasan.spec.metadata.model.ModuleMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.IOException;
import java.util.List;

import static org.ikasan.dashboard.DashboardClientAutoConfiguration.CONFIGURATION_METADATA_PATH;
import static org.ikasan.dashboard.DashboardClientAutoConfiguration.METADATA_PATH;
import static org.ikasan.ootb.data.sharing.module.DataSharingEntity.MODULE_METADATA_ENTITY;
import static org.ikasan.spec.dashboard.DashboardRestService.*;
import static org.ikasan.spec.dashboard.DashboardRestService.DASHBOARD_PASSWORD_PROPERTY;

/**
 * A factory class responsible for creating and configuring consumer and producer components
 * for module metadata processing. This class acts as a configuration point for scheduling
 * consumer operations and managing message publishing via big queues.
 *
 * The factory utilizes the {@link BuilderFactory} to construct components and relies on
 * {@link DataSharingRestService} for metadata queries.
 *
 * The provided methods enable the creation of:
 * 1. A scheduled consumer configured with a cron expression and a data-provider based on
 *    error data sharing functionality.
 * 2. A big queue producer that publishes messages to a queue linked with a specific target
 *    dashboard and entity type.
 *
 * This factory is suitable for scenarios involving scheduled data querying and message flow
 * between metadata extraction and dashboard modules.
 */
@Configuration
public class MetaDataProducerFlowComponentFactory {

    @Autowired
    BuilderFactory builderFactory;

    @Autowired
    DataSharingConfiguredModuleConfiguration dataSharingConfiguredModuleConfiguration;

    /**
     * Creates and returns a {@link Producer} instance specifically configured for module metadata sharing
     * with a downstream dashboard.
     *
     * @param targetDashboardName the name of the target dashboard for which the module metadata producer
     *                            will be created. This is used to fetch the corresponding dashboard URL,
     *                            username, and password from the configuration.
     * @return a {@link Producer} instance configured to communicate with the specified target dashboard.
     */
    public Producer getModuleMetaDataProducer(String targetDashboardName) {
        DataSharingEnvironment environment = new DataSharingEnvironment();
        environment.setProperty(DASHBOARD_EXTRACT_ENABLED_PROPERTY, "true");
        environment.setProperty(DASHBOARD_BASE_URL_PROPERTY
            , dataSharingConfiguredModuleConfiguration.getDownStreamDashboards().get(targetDashboardName));
        environment.setProperty(DASHBOARD_USERNAME_PROPERTY
            , dataSharingConfiguredModuleConfiguration.getDownStreamDashboardUsernames().get(targetDashboardName));
        environment.setProperty(DASHBOARD_PASSWORD_PROPERTY
            , dataSharingConfiguredModuleConfiguration.getDownStreamDashboardPasswords().get(targetDashboardName));

        DashboardRestService moduleMetaDataDashboardRestService
            = new DashboardRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory(), METADATA_PATH);

        return new ModuleMetaDataRestProducer(moduleMetaDataDashboardRestService);
    }

    /**
     * Creates and returns a {@link Producer} instance specifically configured for configuration metadata sharing
     * with a downstream dashboard.
     *
     * @param targetDashboardName the name of the target dashboard for which the configuration metadata producer
     *                            will be created. This is used to fetch the corresponding dashboard URL,
     *                            username, and password from the configuration.
     * @return a {@link Producer} instance configured to communicate with the specified target dashboard.
     */
    public Producer getConfigurationMetaDataProducer(String targetDashboardName) {
        DataSharingEnvironment environment = new DataSharingEnvironment();
        environment.setProperty(DASHBOARD_EXTRACT_ENABLED_PROPERTY, "true");
        environment.setProperty(DASHBOARD_BASE_URL_PROPERTY
            , dataSharingConfiguredModuleConfiguration.getDownStreamDashboards().get(targetDashboardName));
        environment.setProperty(DASHBOARD_USERNAME_PROPERTY
            , dataSharingConfiguredModuleConfiguration.getDownStreamDashboardUsernames().get(targetDashboardName));
        environment.setProperty(DASHBOARD_PASSWORD_PROPERTY
            , dataSharingConfiguredModuleConfiguration.getDownStreamDashboardPasswords().get(targetDashboardName));

        DashboardRestService configurationMetaDataDashboardRestService
            = new DashboardRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory(), CONFIGURATION_METADATA_PATH);

        return new ConfigurationMetaDataRestProducer(configurationMetaDataDashboardRestService);
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
            .get(BigQueueNameHelper.getQueueName(targetDashboardName, MODULE_METADATA_ENTITY));

        return builderFactory.getComponentBuilder().bigQueueConsumer()
            .setInboundQueue(dashboardEntityQueue)
            .setPutErrorsToBackOfQueue(false)
            .setSerialiser(new ModuleAndConfigMetaDataToBigQueueMessageSerialiser())
            .build();
    }

    public MultiRecipientRouter<ModuleAndConfigMetaData> getMetadataRouter() {
        return new RecipientListRouter<>(
            List.of("modules", "configurations"));
    }

    public Splitter<ModuleAndConfigMetaData, ModuleMetaData> getModuleSplitter()  {
        return new ModuleMetaDataSplitter();
    }

    public Splitter<ModuleAndConfigMetaData, List<ConfigurationMetaData>> getConfigurationSplitter()  {
        return new ConfigurationMetaDataSplitter();
    }
}
