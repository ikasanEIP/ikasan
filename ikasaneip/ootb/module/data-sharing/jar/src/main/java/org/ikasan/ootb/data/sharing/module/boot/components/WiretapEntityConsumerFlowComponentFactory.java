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
import org.ikasan.component.endpoint.bigqueue.producer.BigQueueProducer;
import org.ikasan.ootb.data.sharing.module.DataSharingEntity;
import org.ikasan.ootb.data.sharing.module.cache.EntityBigQueueCache;
import org.ikasan.ootb.data.sharing.module.component.endpoint.consumer.WiretapMessageProvider;
import org.ikasan.ootb.data.sharing.module.component.endpoint.consumer.configuration.WiretapConsumerConfiguration;
import org.ikasan.ootb.data.sharing.module.configuration.DataSharingConfiguredModuleConfiguration;
import org.ikasan.ootb.data.sharing.module.serialiser.WiretapEventsToBigQueueMessageSerialiser;
import org.ikasan.ootb.data.sharing.module.util.BigQueueNameHelper;
import org.ikasan.ootb.data.sharing.module.util.StringHelper;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.dashboard.DataSharingRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.resource.ResourceTransformer;

import java.io.IOException;

import static org.ikasan.ootb.data.sharing.module.DataSharingEntity.WIRETAP_ENTITY;


/**
 * A factory class responsible for creating and configuring consumer and producer components
 * for wiretap entity data processing. This class acts as a configuration point for scheduling
 * consumer operations and managing message publishing via big queues.
 *
 * The factory utilizes the {@link BuilderFactory} to construct components and relies on
 * {@link DataSharingRestService} for wiretap data interactions.
 *
 * The provided methods enable the creation of:
 * 1. A scheduled consumer configured with a cron expression and a data-provider based on
 *    wiretap data sharing functionality.
 * 2. A big queue producer that publishes messages to a queue linked with a specific target
 *    dashboard and entity type.
 *
 * This factory is suitable for scenarios involving scheduled data querying and message flow
 * between wiretap systems and dashboard modules.
 */
@Configuration
public class WiretapEntityConsumerFlowComponentFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BuilderFactory builderFactory;

    @Autowired
    DataSharingRestService wiretapDataSharingRestService;

    @Autowired
    DataSharingConfiguredModuleConfiguration dataSharingConfiguredModuleConfiguration;
    @Autowired
    private ResourceTransformer resourceTransformer;

    /**
     * Creates and returns a scheduled {@link Consumer} instance configured with a
     * {@link WiretapConsumerConfiguration} and a {@link WiretapMessageProvider}.
     * The configuration sets a cron expression for scheduled execution.
     *
     * The {@link WiretapMessageProvider} uses the {@code DataSharingRestService} for
     * querying wiretap data, while the {@link WiretapConsumerConfiguration} governs
     * consumer behavior such as scheduling and data retrieval parameters.
     *
     * @return a fully configured {@link Consumer} instance ready for scheduled operations
     */
    public Consumer getScheduledConsumer(String targetDashboardName) {
        WiretapConsumerConfiguration wiretapConsumerConfiguration = new WiretapConsumerConfiguration();
        wiretapConsumerConfiguration.setEager(true);
        wiretapConsumerConfiguration.setModuleNames(StringHelper.commaSeparatedStringToList(this.dataSharingConfiguredModuleConfiguration
            .getDownStreamDashboardModules().get(targetDashboardName)));
        wiretapConsumerConfiguration.setCronExpression(this.dataSharingConfiguredModuleConfiguration
            .getDownStreamDashboardEntityCronExpressions().get(targetDashboardName + "-" + DataSharingEntity.WIRETAP_ENTITY));
        return this.builderFactory.getComponentBuilder()
            .scheduledConsumer()
            .setMessageProvider(new WiretapMessageProvider(wiretapDataSharingRestService))
            .setConfiguration(wiretapConsumerConfiguration)
            .build();
    }

    /**
     * Creates and returns a {@link Producer} instance configured to use a big queue associated
     * with the specified target dashboard name for outbound message publishing.
     *
     * The big queue is retrieved from an {@link EntityBigQueueCache} instance that maintains
     * queue mappings derived from the provided target dashboard name and a predefined entity
     * type. The retrieved queue is then used to configure the {@link Producer}.
     *
     * @param targetDashboardName the name of the target dashboard for which the big queue is required
     * @return a {@link Producer} instance configured with the appropriate big queue
     * @throws IOException if an error occurs while accessing the big queue
     */
    public Producer getBigQueueProducer(String targetDashboardName) throws IOException {
        IBigQueue dashboardEntityQueue = EntityBigQueueCache.instance()
            .get(BigQueueNameHelper.getQueueName(targetDashboardName, WIRETAP_ENTITY));

        BigQueueProducer bigQueueProducer = new BigQueueProducer(dashboardEntityQueue);
        bigQueueProducer.setSerialiser(new WiretapEventsToBigQueueMessageSerialiser());

        return bigQueueProducer;
//        return builderFactory.getComponentBuilder()
//            .bigQueueProducer()
//            .setSerialiser(new WiretapEventsToBigQueueMessageSerialiser())
//            .setOutboundQueue(dashboardEntityQueue)
//            .build();
    }

}

