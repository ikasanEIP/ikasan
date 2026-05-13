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

import org.ikasan.bigqueue.IBigQueue;
import org.ikasan.builder.AopProxyProvider;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.component.endpoint.bigqueue.producer.BigQueueProducer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.converter.JobExecutionToContextualisedScheduledProcessEventConverter;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.ContextualisedConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.ScheduledContextEventContextInstancesActiveFilter;
import org.ikasan.ootb.scheduler.agent.module.component.filter.ScheduledProcessEventFilter;
import org.ikasan.ootb.scheduler.agent.module.component.router.BlackoutRouter;
import org.ikasan.ootb.scheduler.agent.module.component.serialiser.ScheduledProcessEventToBigQueueMessageSerialiser;
import org.ikasan.ootb.scheduler.agent.module.component.splitter.ScheduledProcessEventEventCorrelationIdentifierSplitter;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Quartz scheduler job event flow component factory.
 *
 * @author Ikasan Development Team
 */
@Configuration
public class QuartzSchedulerJobEventFlowComponentFactory
{
    @Value( "${module.name}" )
    String moduleName;

    @Autowired
    BuilderFactory builderFactory;

    @Autowired
    IBigQueue outboundQueue;

    @Autowired
    DryRunModeService dryRunModeService;

    @Value("${context.instance.recovery.active:true}")
    boolean agentRecoveryActive;

    @Autowired
    Scheduler scheduler;

    @Autowired
    ScheduledJobFactory scheduledJobFactory;

    @Autowired
    AopProxyProvider aopProxyProvider;

    public Consumer getScheduledConsumer() {
        ScheduledConsumerConfiguration consumerConfiguration = new ScheduledConsumerConfiguration();
        consumerConfiguration.setCronExpression("0 0 0 * * ?");

        return builderFactory.getComponentBuilder()
            .scheduledConsumer()
            .setConfiguration(consumerConfiguration)
            .build();
    }

    /**
     * Get the context instance filter.
     *
     * @return
     */
    public Filter getContextInstancesActiveFilter() {
        return new ScheduledContextEventContextInstancesActiveFilter(dryRunModeService);
    }

    /**
     * Get the converter that converts messages from a JobExecution to a ScheduledProcessEvent.
     *
     * @return the converter
     */
    public Converter getJobExecutionConverter() {
        ContextualisedConverterConfiguration configuration = new ContextualisedConverterConfiguration();
        JobExecutionToContextualisedScheduledProcessEventConverter converter
            = new JobExecutionToContextualisedScheduledProcessEventConverter(moduleName);
        converter.setConfiguration(configuration);

        return converter;
    }

    /**
     * Get the Splitter instance responsible for splitting FileWatcherJobEvent instances based on correlation identifiers.
     *
     * @return the FileWatcherEventCorrelationIdentifierSplitter instance
     */
    public Splitter getScheduledProcessEventEventCorrelationIdentifierSplitter() {
        return new ScheduledProcessEventEventCorrelationIdentifierSplitter  ();
    }

    /**
     * Get the router responsible for determining if a job has been run in a blackout window.
     *
     * @return
     */
    public SingleRecipientRouter getBlackoutRouter()
    {
        return new BlackoutRouter();
    }

    /**
     * Get the filter that drops ScheduledProcessEvents that should not be published back to the dashboard.
     *
     * @return
     */
    public Filter getScheduledStatusFilter() {
        return new ScheduledProcessEventFilter();
    }

    /**
     * Get the producer that publishes ScheduledProcessEvents.
     *
     * @return
     */
    public Producer getScheduledStatusProducer()
    {
        BigQueueProducer producer = new BigQueueProducer(this.outboundQueue);
        producer.setSerialiser(new ScheduledProcessEventToBigQueueMessageSerialiser());

        return producer;
    }
}

