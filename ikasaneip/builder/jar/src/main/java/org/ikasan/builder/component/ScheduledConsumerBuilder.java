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
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;

/**
 * Contract for a default scheduledConsumerBuilder.
 *
 * @author Ikasan Development Team.
 */
public interface ScheduledConsumerBuilder extends Builder<Consumer>
{
    public ScheduledConsumerBuilder setCriticalOnStartup(boolean criticalOnStartup);

    public ScheduledConsumerBuilder setConfiguredResourceId(String configuredResourceId);

    public ScheduledConsumerBuilder setConfiguration(ScheduledConsumerConfiguration scheduledConsumerConfiguration);

    public ScheduledConsumerBuilder setMessageProvider(MessageProvider messageProvider);

    public ScheduledConsumerBuilder setManagedEventIdentifierService(ManagedEventIdentifierService managedEventIdentifierService);

    public ScheduledConsumerBuilder setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager);

    public ScheduledConsumerBuilder setEventFactory(EventFactory eventFactory);

    public ScheduledConsumerBuilder setCronExpression(String cronExpression);

    public ScheduledConsumerBuilder setEager(boolean eager);

    public ScheduledConsumerBuilder setIgnoreMisfire(boolean ignoreMisfire);

    public ScheduledConsumerBuilder setTimezone(String timezone);

    public ScheduledConsumerBuilder setScheduledJobGroupName(String scheduledJobGroupName);

    public ScheduledConsumerBuilder setScheduledJobName(String scheduledJobName);

}

