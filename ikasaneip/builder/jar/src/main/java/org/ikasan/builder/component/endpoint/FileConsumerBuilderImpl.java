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
import org.ikasan.component.endpoint.filesystem.messageprovider.FileMessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;

import java.util.List;

/**
 * Ikasan provided file consumer builder default implementation.
 *
 * @author Ikasan Development Team
 */
public class FileConsumerBuilderImpl implements FileConsumerBuilder
{
    FileMessageProvider fileMessageProvider = new FileMessageProvider();

    @Override
    public FileConsumerBuilder setCriticalOnStartup(boolean criticalOnStartup)
    {
        this.fileMessageProvider.setCriticalOnStartup(criticalOnStartup);
        return this;
    }

    @Override
    public FileConsumerBuilder setConfiguredResourceId(String configuredResourceId)
    {
        return boo;
    }

    @Override
    public FileConsumerBuilder setConfiguration(FileConsumerConfiguration fileConsumerConfiguration)
    {
        this.fileMessageProvider.setConfiguration(fileConsumerConfiguration);
        return this;
    }

    @Override
    public FileConsumerBuilder setMessageProvider(MessageProvider messageProvider)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setManagedEventIdentifierService(ManagedEventIdentifierService managedEventIdentifierService)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setEventFactory(EventFactory eventFactory)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setCronExpression(String cronExpression)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setEager(boolean eager)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setIgnoreMisfire(boolean ignoreMisfire)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setTimezone(String timezone)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setScheduledJobGroupName(String scheduledJobGroupName)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setScheduledJobName(String scheduledJobName)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setFilenames(List<String> filenames)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setEncoding(String encoding)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setIncludeHeader(boolean includeHeader)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setIncludeTrailer(boolean includeTrailer)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setSortByModifiedDateTime(boolean sortByModifiedDateTime)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setSortAscending(boolean sortAscending)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setDirectoryDepth(int directoryDepth)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setLogMatchedFilenames(boolean logMatchedFilenames)
    {
        return this;
    }

    @Override
    public FileConsumerBuilder setIgnoreFileRenameWhilstScanning(boolean ignoreFileRenameWhilstScanning)
    {
        return this;
    }

    @Override
    public Consumer build()
    {
        return null;
    }
}

