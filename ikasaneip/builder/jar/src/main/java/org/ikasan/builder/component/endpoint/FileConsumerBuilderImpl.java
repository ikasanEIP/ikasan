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

import org.ikasan.builder.AopProxyProvider;
import org.ikasan.builder.component.RequiresAopProxy;
import org.ikasan.component.endpoint.filesystem.messageprovider.FileConsumerConfiguration;
import org.ikasan.component.endpoint.filesystem.messageprovider.FileMessageProvider;
import org.ikasan.component.endpoint.filesystem.messageprovider.MessageProviderPostProcessor;
import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;

import java.util.List;

/**
 * Ikasan provided local file consumer default implementation.
 *
 * This implemnetation allows for proxying of the object to facilitate transaction pointing.
 *
 * @author Ikasan Development Team
 */
public class FileConsumerBuilderImpl extends ScheduledConsumerBuilderImpl
        implements FileConsumerBuilder, RequiresAopProxy
{
    FileMessageProvider fileMessageProvider;

    /**
     * Constructor
     * @param scheduledConsumer
     */
    public FileConsumerBuilderImpl(ScheduledConsumer scheduledConsumer, ScheduledJobFactory scheduledJobFactory,
                                   AopProxyProvider aopProxyProvider, FileMessageProvider fileMessageProvider)
    {
        super(scheduledConsumer, scheduledJobFactory, aopProxyProvider);
        this.fileMessageProvider = fileMessageProvider;
        if(fileMessageProvider == null)
        {
            throw new IllegalArgumentException("fileMessageProvider cannot be 'null'");
        }

    }

    /**
     * Is this successful start of this component critical on flow start.
     * If it can recover post flow start up then its not crititcal.
     * @param criticalOnStartup
     * @return
     */
    public FileConsumerBuilder setCriticalOnStartup(boolean criticalOnStartup)
    {
        this.scheduledConsumer.setCriticalOnStartup(criticalOnStartup);
        return this;
    }

    /**
     * ConfigurationService identifier for this component configuration.
     * @param configuredResourceId
     * @return
     */
    public FileConsumerBuilder setConfiguredResourceId(String configuredResourceId)
    {
        this.scheduledConsumer.setConfiguredResourceId(configuredResourceId);
        return this;
    }

    @Override
    public FileConsumerBuilder setConfiguration(FileConsumerConfiguration fileConsumerConfiguration)
    {
        this.scheduledConsumer.setConfiguration(fileConsumerConfiguration);
        return this;
    }

    /**
     * Underlying tech providing the message event
     * @param messageProvider
     * @return
     */
    public FileConsumerBuilder setMessageProvider(MessageProvider messageProvider)
    {
        this.scheduledConsumer.setMessageProvider(messageProvider);
        return this;
    }

    /**
     * Implementation of the managed event identifier service - sets the life identifier based on the incoming event.
     * @param managedEventIdentifierService
     * @return
     */
    public FileConsumerBuilder setManagedEventIdentifierService(ManagedEventIdentifierService managedEventIdentifierService)
    {
        this.scheduledConsumer.setManagedEventIdentifierService(managedEventIdentifierService);
        return this;
    }

    /**
     * Give the component a handle directly to the recovery manager
     * @param managedResourceRecoveryManager
     * @return
     */
    public FileConsumerBuilder setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        this.scheduledConsumer.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        return this;
    }

    /**
     * Override default event factory
     * @param eventFactory
     * @return
     */
    public FileConsumerBuilder setEventFactory(EventFactory eventFactory) {
        this.scheduledConsumer.setEventFactory(eventFactory);
        return this;
    }

    /**
     * Scheduled consumer cron expression
     * @param cronExpression
     * @return
     */
    public FileConsumerBuilder setCronExpression(String cronExpression)
    {
        getConfiguration().setCronExpression(cronExpression);
        return this;
    }

    /**
     * When true the scheduled consumer is immediately called back on completion of flow execution.
     * If false the scheduled consumers cron expression determines the callback.
     * @param eager
     * @return
     */
    public FileConsumerBuilder setEager(boolean eager)
    {
        getConfiguration().setEager(eager);
        return this;
    }

    /**
     * Whether to ignore call back failures.
     * @param ignoreMisfire
     * @return
     */
    public FileConsumerBuilder setIgnoreMisfire(boolean ignoreMisfire) {
        getConfiguration().setIgnoreMisfire(ignoreMisfire);
        return this;
    }

    /**
     * Specifically set the timezone of the scheduled callback.
     * @param timezone
     * @return
     */
    public FileConsumerBuilder setTimezone(String timezone) {
        getConfiguration().setTimezone(timezone);
        return this;
    }

    @Override
    public FileConsumerBuilder setScheduledJobGroupName(String scheduledJobGroupName) {
        this.scheduledJobGroupName = scheduledJobGroupName;
        return this;
    }

    @Override
    public FileConsumerBuilder setScheduledJobName(String scheduledJobName) {
        this.scheduledJobName = scheduledJobName;
        return this;
    }

    @Override
    public FileConsumerBuilder setFilenames(List<String> filenames)
    {
        getConfiguration().setFilenames(filenames);
        return this;
    }

    @Override
    public FileConsumerBuilder setEncoding(String encoding)
    {
        getConfiguration().setEncoding(encoding);
        return this;
    }

    @Override
    public FileConsumerBuilder setIncludeHeader(boolean includeHeader)
    {
        getConfiguration().setIncludeHeader(includeHeader);
        return this;
    }

    @Override
    public FileConsumerBuilder setIncludeTrailer(boolean includeTrailer)
    {
        getConfiguration().setIncludeTrailer(includeTrailer);
        return this;
    }

    @Override
    public FileConsumerBuilder setSortByModifiedDateTime(boolean sortByModifiedDateTime)
    {
        getConfiguration().setSortByModifiedDateTime(sortByModifiedDateTime);
        return this;
    }

    @Override
    public FileConsumerBuilder setSortAscending(boolean sortAscending)
    {
        getConfiguration().setSortAscending(sortAscending);
        return this;
    }

    @Override
    public FileConsumerBuilder setDirectoryDepth(int directoryDepth)
    {
        getConfiguration().setDirectoryDepth(directoryDepth);
        return this;
    }

    @Override
    public FileConsumerBuilder setLogMatchedFilenames(boolean logMatchedFilenames)
    {
        getConfiguration().setLogMatchedFilenames(logMatchedFilenames);
        return this;
    }

    @Override
    public FileConsumerBuilder setIgnoreFileRenameWhilstScanning(boolean ignoreFileRenameWhilstScanning)
    {
        getConfiguration().setIgnoreFileRenameWhilstScanning(ignoreFileRenameWhilstScanning);
        return this;
    }

    @Override
    public FileConsumerBuilder setMessageProviderPostProcessor(MessageProviderPostProcessor messageProviderPostProcessor)
    {
        this.fileMessageProvider.setMessageProviderPostProcessor(messageProviderPostProcessor);
        return this;
    }

    private FileConsumerConfiguration getConfiguration()
    {
        FileConsumerConfiguration scheduledFileConsumerConfiguration = (FileConsumerConfiguration)this.scheduledConsumer.getConfiguration();
        if(scheduledFileConsumerConfiguration == null)
        {
            scheduledFileConsumerConfiguration = new FileConsumerConfiguration();
            this.scheduledConsumer.setConfiguration(scheduledFileConsumerConfiguration);
        }

        return scheduledFileConsumerConfiguration;
    }

    /**
     * Configure the raw component based on the properties passed to the builder, configure it
     * ready for use and return the instance.
     * @return
     */
    public ScheduledConsumer build()
    {
        this.scheduledConsumer.setMessageProvider(this.fileMessageProvider);

        // it maybe no configuration properties are set by the developer, so call getConfiguration to ensure one exists
        getConfiguration();

        return super.build();
    }

}

