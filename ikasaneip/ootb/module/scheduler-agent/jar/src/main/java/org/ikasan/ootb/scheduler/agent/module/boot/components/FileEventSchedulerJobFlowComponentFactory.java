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
package org.ikasan.ootb.scheduler.agent.module.boot.components;

import org.ikasan.bigqueue.BigArrayImpl;
import org.ikasan.bigqueue.BigQueueImpl;
import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.filter.duplicate.IsDuplicateFilterRule;
import org.ikasan.filter.duplicate.service.DuplicateFilterService;
import org.ikasan.ootb.scheduler.agent.module.component.broker.CorrelatingFileMatcherBroker;
import org.ikasan.ootb.scheduler.agent.module.component.broker.MoveFileBroker;
import org.ikasan.ootb.scheduler.agent.module.component.converter.FileWatcherJobEventToContextualisedScheduledProcessEventConverter;
import org.ikasan.ootb.scheduler.agent.module.component.filter.FileAgeFilter;
import org.ikasan.ootb.scheduler.agent.module.component.filter.ScheduledProcessEventFilter;
import org.ikasan.ootb.scheduler.agent.module.component.filter.SchedulerFileFilter;
import org.ikasan.ootb.scheduler.agent.module.component.filter.SchedulerFilterEntryConverter;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.SchedulerFileFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.router.BlackoutRouter;
import org.ikasan.ootb.scheduler.agent.module.component.serialiser.FileWatcherJobEventToBigQueueMessageSerialiser;
import org.ikasan.ootb.scheduler.agent.module.component.serialiser.ScheduledProcessEventToBigQueueMessageSerialiser;
import org.ikasan.ootb.scheduler.agent.rest.cache.InternalFileWatcherJobQueueCache;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * File scheduler job event flow component factory.
 *
 * @author Ikasan Development Team
 */
@Configuration
public class FileEventSchedulerJobFlowComponentFactory  {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value( "${module.name}" )
    String moduleName;

    @Value( "${scheduler.file.filter.days.ttl:30}" )
    Integer filterTtl;

    @Value( "${big.queue.consumer.queueDir}" )
    private String queueDir;

    @Resource
    BuilderFactory builderFactory;

    @Resource
    IBigQueue outboundQueue;

    @Resource
    DuplicateFilterService duplicateFilterService;

    @Resource
    DryRunModeService dryRunModeService;

    @Value("${context.instance.recovery.active:true}")
    boolean agentRecoveryActive;

    @Value("${big.queue.page.size:"+ BigArrayImpl.DEFAULT_DATA_PAGE_SIZE + "}")
    private int bigQueuePageSize;

    /**
     * Creates a BigQueueConsumer component for consuming messages from a specified inbound queue.
     *
     * @param threadIdentifier the identifier for the consumer thread
     * @return a Consumer object representing the BigQueueConsumer component
     * @throws IOException if an I/O error occurs
     */
    public Consumer bigQueueConsumer(String threadIdentifier) throws IOException {
        String queueName = moduleName+"-"+threadIdentifier+"-file-watcher-inbound-queue";

        if(bigQueuePageSize < BigArrayImpl.MINIMUM_DATA_PAGE_SIZE) {
            logger.info("bigQueuePageSize[{}] is smaller than BigArrayImpl.MINIMUM_DATA_PAGE_SIZE[{}]. Setting to [{}]"
                , bigQueuePageSize, BigArrayImpl.MINIMUM_DATA_PAGE_SIZE, BigArrayImpl.MINIMUM_DATA_PAGE_SIZE);
            bigQueuePageSize = BigArrayImpl.MINIMUM_DATA_PAGE_SIZE;
        }

        IBigQueue inboundQueue = new BigQueueImpl(queueDir, queueName, bigQueuePageSize);

        // Add the inbound queue to the cache.
        InternalFileWatcherJobQueueCache.instance().put(queueName, inboundQueue);

        return builderFactory.getComponentBuilder().bigQueueConsumer()
            .setInboundQueue(inboundQueue)
            .setPutErrorsToBackOfQueue(false)
            .setSerialiser(new FileWatcherJobEventToBigQueueMessageSerialiser())
            .build();
    }

    /**
     * Retrieves a CorrelatingFileMatcherBroker instance that implements the Broker interface.
     * The CorrelatingFileMatcherBroker is responsible for matching files based on provided criteria and correlation identifier.
     *
     * @return a CorrelatingFileMatcherBroker object implementing the Broker interface for handling file correlation
     */
    public Broker correlatingFileMatcherBroker() {
        return new CorrelatingFileMatcherBroker();
    }

    /**
     * Returns a FileAgeFilter object that filters FileWatcherJobEvent based on specified criteria.
     *
     * @return FileAgeFilter object implementing the Filter interface for filtering FileWatcherJobEvent
     */
    public Filter getFileAgeFilter() {
        return new FileAgeFilter();
    }

    /**
     * Retrieves a duplicate message filter based on the given job name.
     *
     * @return a Filter object representing the duplicate message filter
     */
    public Filter getDuplicateMessageFilter() {
        SchedulerFilterEntryConverter converter
            = new SchedulerFilterEntryConverter("file-watcher-duplicate-message-filter", filterTtl);

        IsDuplicateFilterRule isDuplicateFilterRule
            = new IsDuplicateFilterRule(duplicateFilterService, converter);

        SchedulerFileFilterConfiguration configuration = new SchedulerFileFilterConfiguration();

        SchedulerFileFilter filter = new SchedulerFileFilter(isDuplicateFilterRule);
        filter.setConfiguration(configuration);

        return filter;
    }

    /**
     * Retrieves a MoveFileBroker instance that implements the Broker interface.
     * The MoveFileBroker is responsible for processing file moving operations with optional dry run mode.
     *
     * @return a MoveFileBroker object for handling file moving operations
     */
    public Broker getMoveFileBroker() {
        return new MoveFileBroker();
    }

    /**
     * Method to retrieve a Converter implementation that converts FileWatcherJobEvent objects to
     * ContextualisedScheduledProcessEvent objects.
     *
     * @return a Converter object capable of converting FileWatcherJobEvent to ContextualisedScheduledProcessEvent
     */
    public Converter getFileEventToScheduledProcessEventConverter() {
        return new FileWatcherJobEventToContextualisedScheduledProcessEventConverter(moduleName);
    }

    /**
     * Retrieves the BlackoutRouter instance used for determining whether a scheduled process event
     * has fired within a defined blackout period.
     *
     * @return a BlackoutRouter instance implementing the SingleRecipientRouter interface
     */
    public SingleRecipientRouter getBlackoutRouter() {
        return new BlackoutRouter();
    }

    /**
     * Retrieves a filter that ignores scheduled process events within the blackout period.
     *
     * @return a Filter object representing the ScheduledProcessEventFilter
     */
    public Filter getScheduledStatusFilter()
    {
        return new ScheduledProcessEventFilter();
    }

    /**
     * Retrieves a Producer component for distributing to an endpoint.
     * The Producer defines a contract for translation.
     *
     * @return a Producer object that can push payload to a protocol endpoint
     */
    public Producer getScheduledStatusProducer()
    {
        return builderFactory.getComponentBuilder().bigQueueProducer()
            .setOutboundQueue(this.outboundQueue)
            .setSerialiser(new ScheduledProcessEventToBigQueueMessageSerialiser())
            .build();
    }

}

