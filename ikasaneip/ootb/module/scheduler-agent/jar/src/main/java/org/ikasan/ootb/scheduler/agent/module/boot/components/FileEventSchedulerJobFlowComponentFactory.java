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
import org.ikasan.builder.BuilderFactory;
import org.ikasan.component.endpoint.filesystem.messageprovider.FileConsumerConfiguration;
import org.ikasan.filter.duplicate.IsDuplicateFilterRule;
import org.ikasan.filter.duplicate.service.DuplicateFilterService;
import org.ikasan.ootb.scheduler.agent.module.component.broker.MoveFileBroker;
import org.ikasan.ootb.scheduler.agent.module.component.broker.configuration.MoveFileBrokerConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.converter.FileListToContextualisedScheduledProcessEventConverter;
import org.ikasan.ootb.scheduler.agent.module.component.converter.configuration.ContextualisedConverterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.endpoint.ScheduledProcessEventToBigQueueMessageSerialiser;
import org.ikasan.ootb.scheduler.agent.module.component.filter.*;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.ContextInstanceFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.FileAgeFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.filter.configuration.SchedulerFileFilterConfiguration;
import org.ikasan.ootb.scheduler.agent.module.component.router.BlackoutRouter;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;

/**
 * File scheduler job event flow component factory.
 *
 * @author Ikasan Development Team
 */
@Configuration
public class FileEventSchedulerJobFlowComponentFactory
{
    @Value( "${module.name}" )
    String moduleName;

    @Value( "${scheduler.file.filter.days.ttl:30}" )
    Integer filterTtl;

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

    /**
     * Return an instance of a configured file consumer
     *
     * @return
     */
    public Consumer getFileConsumer()
    {
        FileConsumerConfiguration fileConsumerConfiguration = new FileConsumerConfiguration();
        fileConsumerConfiguration.setFilenames(List.of("set me"));
        fileConsumerConfiguration.setCronExpression("0 0 0 * * ?");
        return builderFactory.getComponentBuilder().fileConsumer()
            .setConfiguration(fileConsumerConfiguration)
            .build();
    }

    /**
     * Get the context instance filter.
     *
     * @return
     */
    public Filter getContextInstanceFilter() {
        ContextInstanceFilterConfiguration configuration = new ContextInstanceFilterConfiguration();
        ContextInstanceFilter contextInstanceFilter = new ContextInstanceFilter(dryRunModeService, agentRecoveryActive);

        contextInstanceFilter.setConfiguration(configuration);

        return contextInstanceFilter;
    }

    /**
     * Get the file age filter.
     *
     * @return
     */
    public Filter getFileAgeFilter() {
        FileAgeFilterConfiguration configuration = new FileAgeFilterConfiguration();
        FileAgeFilter fileAgeFilter = new FileAgeFilter(this.dryRunModeService);

        fileAgeFilter.setConfiguration(configuration);

        return fileAgeFilter;
    }

    /**
     * Get the duplicate message filter.
     *
     * @param jobName
     * @return
     */
    public Filter getDuplicateMessageFilter(String jobName) {
        SchedulerFilterEntryConverter converter
            = new SchedulerFilterEntryConverter("duplicate-message-filter-"+jobName, filterTtl);

        IsDuplicateFilterRule isDuplicateFilterRule
            = new IsDuplicateFilterRule(duplicateFilterService, converter);

        SchedulerFileFilterConfiguration configuration = new SchedulerFileFilterConfiguration();

        SchedulerFileFilter filter = new SchedulerFileFilter(isDuplicateFilterRule, dryRunModeService);
        filter.setConfiguration(configuration);

        return filter;
    }

    /**
     * Get the broker that moves files if the job is configured to do so.
     *
     * @return
     */
    public Broker getMoveFileBroker() {
        MoveFileBrokerConfiguration moveFileBrokerConfiguration = new MoveFileBrokerConfiguration();
        MoveFileBroker broker = new MoveFileBroker(this.dryRunModeService);
        broker.setConfiguration(moveFileBrokerConfiguration);

        return broker;
    }

    /**
     * Get the converter that converts messages from a JobExecution to a ScheduledProcessEvent.
     *
     * @return the converter
     */
    public Converter getFileEventToScheduledProcessEventConverter(String jobName) {
        ContextualisedConverterConfiguration configuration = new ContextualisedConverterConfiguration();
        FileListToContextualisedScheduledProcessEventConverter converter
            = new FileListToContextualisedScheduledProcessEventConverter(moduleName, jobName);
        converter.setConfiguration(configuration);

        return converter;
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
    public Filter getScheduledStatusFilter()
    {
        return new ScheduledProcessEventFilter();
    }

    /**
     * Get the producer that publishes ScheduledProcessEvents.
     *
     * @return
     */
    public Producer getScheduledStatusProducer()
    {
        return builderFactory.getComponentBuilder().bigQueueProducer()
            .setOutboundQueue(this.outboundQueue)
            .setSerialiser(new ScheduledProcessEventToBigQueueMessageSerialiser())
            .build();
    }

}

