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
package org.ikasan.builder.component.endpoint;

import org.ikasan.component.endpoint.filesystem.messageprovider.FileConsumerConfiguration;
import org.ikasan.component.endpoint.filesystem.messageprovider.MessageProviderPostProcessor;
import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;

import java.util.List;

/**
 * Contract for a default fileConsumerBuilder.
 *
 * @author Ikasan Development Team.
 */
public interface FileConsumerBuilder extends ScheduledConsumerBuilder
{
    public FileConsumerBuilder setCriticalOnStartup(boolean criticalOnStartup);

    public FileConsumerBuilder setConfiguredResourceId(String configuredResourceId);

    public FileConsumerBuilder setConfiguration(FileConsumerConfiguration scheduledConsumerConfiguration);

    public FileConsumerBuilder setMessageProvider(MessageProvider messageProvider);

    public FileConsumerBuilder setManagedEventIdentifierService(ManagedEventIdentifierService managedEventIdentifierService);

    public FileConsumerBuilder setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager);

    public FileConsumerBuilder setEventFactory(EventFactory eventFactory);

    public FileConsumerBuilder setCronExpression(String cronExpression);

    public FileConsumerBuilder setEager(boolean eager);

    public FileConsumerBuilder setIgnoreMisfire(boolean ignoreMisfire);

    public FileConsumerBuilder setTimezone(String timezone);

    public FileConsumerBuilder setScheduledJobGroupName(String scheduledJobGroupName);

    public FileConsumerBuilder setScheduledJobName(String scheduledJobName);

    public FileConsumerBuilder setFilenames(List<String> filenames);

    public FileConsumerBuilder setEncoding(String encoding);

    public FileConsumerBuilder setIncludeHeader(boolean includeHeader);

    public FileConsumerBuilder setIncludeTrailer(boolean includeTrailer);

    public FileConsumerBuilder setSortByModifiedDateTime(boolean sortByModifiedDateTime);

    public FileConsumerBuilder setSortAscending(boolean sortAscending);

    public FileConsumerBuilder setDirectoryDepth(int directoryDepth);

    public FileConsumerBuilder setLogMatchedFilenames(boolean logMatchedFilenames);

    public FileConsumerBuilder setIgnoreFileRenameWhilstScanning(boolean ignoreFileRenameWhilstScanning);

    public FileConsumerBuilder setMessageProviderPostProcessor(MessageProviderPostProcessor messageProviderPostProcessor);

}

