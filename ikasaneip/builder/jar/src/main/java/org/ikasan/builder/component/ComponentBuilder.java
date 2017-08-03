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
package org.ikasan.builder.component;

import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A simple Component builder.
 * 
 * @author Ikasan Development Team
 */
public class ComponentBuilder
{
    String moduleName;

    String flowName;

    /** the scheduler */
    @Autowired
    Scheduler scheduler;

    /** the scheduler */
    @Autowired
    ScheduledJobFactory scheduledJobFactory;

    public ComponentBuilder(String flowName, String moduleName)
    {
        this.flowName = flowName;
        this.moduleName = moduleName;
    }

    public ScheduledConsumerBuilder scheduledConsumer()
    {
        ScheduledConsumer scheduledConsumer = new org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer(scheduler);
        scheduledConsumer.setJobDetail( scheduledJobFactory.createJobDetail(scheduledConsumer, ScheduledConsumer.class, "scheduledConsumer", this.flowName + this.moduleName) ); //FIXME - dont hard code scheduledConsumer name
        return new ScheduledConsumerBuilderImpl(scheduledConsumer);
    }

    class ScheduledConsumerBuilderImpl implements ScheduledConsumerBuilder
    {
        ScheduledConsumer scheduledConsumer;

        public ScheduledConsumerBuilderImpl(ScheduledConsumer scheduledConsumer)
        {
            this.scheduledConsumer = scheduledConsumer;
        }

        public ScheduledConsumerBuilder setCriticalOnStartup(boolean criticalOnStartup)
        {
            this.scheduledConsumer.setCriticalOnStartup(criticalOnStartup);
            return this;
        }

        public ScheduledConsumerBuilder setConfiguredResourceId(String configuredResourceId)
        {
            this.scheduledConsumer.setConfiguredResourceId(configuredResourceId);
            return this;
        }

        public ScheduledConsumerBuilder setConfiguration(ScheduledConsumerConfiguration scheduledConsumerConfiguration)
        {
            this.scheduledConsumer.setConfiguration(scheduledConsumerConfiguration);
            return this;
        }

        public ScheduledConsumerBuilder setMessageProvider(MessageProvider messageProvider)
        {
            this.scheduledConsumer.setMessageProvider(messageProvider);
            return this;
        }

        public ScheduledConsumerBuilder setManagedEventIdentifierService(ManagedEventIdentifierService managedEventIdentifierService)
        {
            this.scheduledConsumer.setManagedEventIdentifierService(managedEventIdentifierService);
            return this;
        }

        public ScheduledConsumerBuilder setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
        {
            this.scheduledConsumer.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
            return this;
        }

        public ScheduledConsumerBuilder setEventFactory(EventFactory eventFactory) {
            this.scheduledConsumer.setEventFactory(eventFactory);
            return this;
        }

        public ScheduledConsumerBuilder setCronExpression(String cronExpression)
        {
            getConfiguration().setCronExpression(cronExpression);
            return this;
        }

        public ScheduledConsumerBuilder setEager(boolean eager) {
            getConfiguration().setEager(eager);
            return this;
        }

        public ScheduledConsumerBuilder setIgnoreMisfire(boolean ignoreMisfire) {
            getConfiguration().setIgnoreMisfire(ignoreMisfire);
            return this;
        }

        public ScheduledConsumerBuilder setTimezone(String timezone) {
            getConfiguration().setTimezone(timezone);
            return this;
        }

        private ScheduledConsumerConfiguration getConfiguration()
        {
            ScheduledConsumerConfiguration scheduledConsumerConfiguration = this.scheduledConsumer.getConfiguration();
            if(scheduledConsumerConfiguration == null)
            {
                scheduledConsumerConfiguration = new ScheduledConsumerConfiguration();
                this.scheduledConsumer.setConfiguration(scheduledConsumerConfiguration);
            }

            return scheduledConsumerConfiguration;
        }

        public ScheduledConsumer getInstance()
        {
            if(this.scheduledConsumer.getConfiguration() == null)
            {
                this.scheduledConsumer.setConfiguration( new ScheduledConsumerConfiguration() );
            }

            if(this.scheduledConsumer.getConfiguredResourceId() == null)
            {
                this.scheduledConsumer.setConfiguredResourceId("scheduledConsumer" + flowName + moduleName);
            }

            return this.scheduledConsumer;
        }
    }

}




