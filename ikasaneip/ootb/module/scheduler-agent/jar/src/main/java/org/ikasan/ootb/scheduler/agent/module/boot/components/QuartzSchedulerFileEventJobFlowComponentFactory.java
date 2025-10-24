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

import org.ikasan.builder.BuilderFactory;
import org.ikasan.component.endpoint.bigqueue.producer.BigQueueProducer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.converter.JobExecutionContextToFileWatcherJobConverter;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.FileWatcherJobConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.endpoint.producer.NoFileWatcherFlowsAvailableProducer;
import org.ikasan.ootb.scheduler.agent.module.component.filter.FileWatcherJobEventContextInstancesActiveFilter;
import org.ikasan.ootb.scheduler.agent.module.component.router.FileWatcherJobQueueSizeWeightedRouter;
import org.ikasan.ootb.scheduler.agent.module.component.serialiser.FileWatcherJobEventToBigQueueMessageSerialiser;
import org.ikasan.ootb.scheduler.agent.module.component.splitter.FileWatcherEventCorrelationIdentifierSplitter;
import org.ikasan.ootb.scheduler.agent.rest.cache.InternalFileWatcherJobQueueCache;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Quartz scheduler job event flow component factory.
 *
 * @author Ikasan Development Team
 */
@Configuration
public class QuartzSchedulerFileEventJobFlowComponentFactory
{
    @Value( "${module.name}" )
    String moduleName;

    @Resource
    BuilderFactory builderFactory;

    @Resource
    DryRunModeService dryRunModeService;

    @Value("${context.instance.recovery.active:true}")
    boolean agentRecoveryActive;


    /**
     * Retrieves a scheduled consumer with a predefined configuration for correlation purposes.
     *
     * @return Consumer implementing scheduling functionality with correlation configuration
     */
    public Consumer getScheduledConsumer() {
        ScheduledConsumerConfiguration consumerConfiguration = new ScheduledConsumerConfiguration();
        consumerConfiguration.setCronExpression("0 0 0 * * ?");

        return builderFactory.getComponentBuilder()
            .scheduledConsumer()
            .setConfiguration(consumerConfiguration)
            .build();
    }

    /**
     * Retrieves the active filter for context instances.
     *
     * @return Filter<FileWatcherJobEvent> representing the active filter for context instances
     */
    public Filter getContextInstancesActiveFilter() {
        return new FileWatcherJobEventContextInstancesActiveFilter(dryRunModeService);
    }


    /**
     * Retrieves a Splitter for correlating FileWatcherJobEvent based on ContextInstance correlation IDs.
     *
     * @return Splitter for correlating events based on ContextInstance correlation IDs
     */
    public Splitter getFileWatcherEventCorrelationIdentifierSplitter() {
        return new FileWatcherEventCorrelationIdentifierSplitter();
    }

    /**
     * Get the converter that converts messages from a JobExecution to a ScheduledProcessEvent.
     *
     * @return the converter
     */
    public Converter getFileWatcherJobConverter() {
        FileWatcherJobConverterConfiguration configuration = new FileWatcherJobConverterConfiguration();
        JobExecutionContextToFileWatcherJobConverter converter = new JobExecutionContextToFileWatcherJobConverter();
        converter.setConfiguration(configuration);

        return converter;
    }

    /**
     * Retrieves a weighted router based on the size of the job queues for File Watcher events.
     * The router determines the route that has the smallest queue size, i.e., the least number of messages, for processing.
     *
     * @return SingleRecipientRouter implementing the routing based on job queue size
     */
    public SingleRecipientRouter getFileWatcherJobQueueSizeWeightedRouter() {
        return new FileWatcherJobQueueSizeWeightedRouter();
    }


    /**
     * Retrieves a Producer for distributing to an endpoint based on the provided big queue name.
     *
     * @param bigQueueName The name of the big queue used for producing messages
     * @return Producer for distributing messages to an endpoint
     * @throws IllegalArgumentException if the provided big queue name is not found in the InternalFileWatcherJobQueueCache
     */
    public Producer getInternalFileWatcherEventBigQueueProducer(String bigQueueName) {
        if(!InternalFileWatcherJobQueueCache.instance().contains(bigQueueName)) {
            throw new IllegalArgumentException(("A BigQueue destination named [%s] not found in the " +
                "InternalFileWatcherJobQueueCache!").formatted(bigQueueName));
        }

        BigQueueProducer bigQueueProducer = new BigQueueProducer<>(InternalFileWatcherJobQueueCache.instance().get(bigQueueName));
        bigQueueProducer.setSerialiser(new FileWatcherJobEventToBigQueueMessageSerialiser());
        return bigQueueProducer;
    }

    /**
     * Retrieves a producer for distributing to an endpoint when no file watcher flows are available.
     *
     * @return Producer<FileWatcherJobEvent> representing the producer for when no flows available
     */
    public Producer getNoFileWatcherFlowsAvailableProducer() {
        return new NoFileWatcherFlowsAvailableProducer();
    }
}

