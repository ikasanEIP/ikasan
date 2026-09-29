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
package org.ikasan.ootb.data.sharing.module;

import org.ikasan.bigqueue.BigArrayImpl;
import org.ikasan.bigqueue.BigQueueImpl;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.ootb.data.sharing.module.boot.*;
import org.ikasan.ootb.data.sharing.module.cache.EntityBigQueueCache;
import org.ikasan.ootb.data.sharing.module.configuration.DataSharingConfiguredModuleConfiguration;
import org.ikasan.ootb.data.sharing.module.util.BigQueueNameHelper;
import org.ikasan.ootb.data.sharing.module.util.StringHelper;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Flow factory implementation.
 *
 * @author Ikasan Development Team
 */
@Configuration
public class DataSharingFlowFactory implements FlowFactory
{
    private static Logger logger = LoggerFactory.getLogger(DataSharingFlowFactory.class);

    @Value( "${big.queue.consumer.queueDir}" )
    private String queueDir;

    @Value("${big.queue.page.size:"+ BigArrayImpl.DEFAULT_DATA_PAGE_SIZE + "}")
    private int bigQueuePageSize;

    @Autowired
    WiretapEntityProducerFlowFactory wiretapEntityProducerFlowFactory;

    @Autowired
    WiretapEntityConsumerFlowFactory wiretapEntityConsumerFlowFactory;

    @Autowired
    DataSharingConfiguredModuleConfiguration dataSharingConfiguredModuleConfiguration;

    @Override
    public List<Flow> create(String downstreamDashboard, String profile)
    {
        try {
            logger.info("Initiating data sharing flows for dashboard: " + downstreamDashboard);

            List<String> supportedEntities = StringHelper.commaSeparatedStringToList(this.dataSharingConfiguredModuleConfiguration
                .getDownStreamDashboardEntities().get(downstreamDashboard));

            List<Flow> flows = new ArrayList<>();
            for (String entity : supportedEntities) {
                logger.info("Creating data sharing flows for downstream dashboard: " + downstreamDashboard + " for entity " + entity);
                switch (entity) {
                    case DataSharingEntity.WIRETAP_ENTITY: {
                        this.initialiseDashboardEntityBigQueue(downstreamDashboard, DataSharingEntity.WIRETAP_ENTITY);
                        flows.add(this.createWiretapEntityConsumerFlow(downstreamDashboard));
                        flows.add(this.createWiretapEntityProducerFlow(downstreamDashboard));
                        continue;
                    }
                    case DataSharingEntity.ERROR_ENTITY: {
                        continue;
                    }
                    case DataSharingEntity.EXCLUSION_ENTITY: {
                        continue;
                    }
                    case DataSharingEntity.REPLAY_ENTITY: {
                        continue;
                    }
                    default: {
                        throw new RuntimeException(String.format("Unknown entity[%s] encountered in flow factory!", entity));
                    }
                }
            }

            return flows;
        }
        catch (IOException e) {
            throw new RuntimeException("An exception has occurred creating " +
                "data sharing flows for["+downstreamDashboard+"]!", e);
        }
    }

    /**
     * Initializes a BigQueue instance for a given dashboard and entity, associates it with
     * a derived queue name, and stores the BigQueue in the EntityBigQueueCache. This method
     * ensures that the BigQueue page size meets the minimum requirements.
     *
     * @param dashboardName the name of the dashboard for which the BigQueue is being initialized.
     *                      Must not be null or empty.
     * @param entity the entity associated with the dashboard. Must not be null or empty.
     * @throws IOException if an I/O error occurs during the creation of the BigQueue instance.
     */
    private void initialiseDashboardEntityBigQueue(String dashboardName, String entity) throws IOException {
        String queueName = BigQueueNameHelper.getQueueName(dashboardName, entity);

        if(bigQueuePageSize < BigArrayImpl.MINIMUM_DATA_PAGE_SIZE) {
            logger.info("bigQueuePageSize[{}] is smaller than BigArrayImpl.MINIMUM_DATA_PAGE_SIZE[{}]. Setting to [{}]"
                , bigQueuePageSize, BigArrayImpl.MINIMUM_DATA_PAGE_SIZE, BigArrayImpl.MINIMUM_DATA_PAGE_SIZE);
            bigQueuePageSize = BigArrayImpl.MINIMUM_DATA_PAGE_SIZE;
        }

        IBigQueue dashboardEntityBigQueue = new BigQueueImpl(queueDir, queueName, bigQueuePageSize);

        // Add the inbound queue to the cache.
        EntityBigQueueCache.instance().put(queueName, dashboardEntityBigQueue);
    }

    /**
     * Creates a consumer flow for processing wiretap entity data, configured with a specified
     * downstream dashboard name.
     *
     * @param downstreamDashboardName the name of the downstream dashboard to be used for configuring
     *                                the consumer flow. Must not be null or empty.
     * @return a Flow instance representing the configured wiretap entity consumer flow.
     * @throws IOException if an error occurs while creating the consumer flow.
     */
    private Flow createWiretapEntityConsumerFlow(String downstreamDashboardName) throws IOException {
        return this.wiretapEntityConsumerFlowFactory.create(downstreamDashboardName);
    }

    /**
     * Creates a producer flow for handling wiretap entity data, configured with the specified
     * downstream dashboard name.
     *
     * @param downstreamDashboardName the name of the downstream dashboard to be used for configuring
     *                                the producer flow. Must not be null or empty.
     * @return a Flow instance representing the configured wiretap entity producer flow.
     * @throws IOException if an error occurs while creating the producer flow.
     */
    private Flow createWiretapEntityProducerFlow(String downstreamDashboardName) throws IOException {
        return this.wiretapEntityProducerFlowFactory.create(downstreamDashboardName);
    }
}


