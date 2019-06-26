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
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.quartz.Scheduler;

import java.util.List;

/**
 * Ikasan provided local file consumer default implementation.
 *
 * This implemnetation allows for proxying of the object to facilitate transaction pointing.
 *
 * @author Ikasan Development Team
 */
public class FileConsumerBuilderImpl extends AbstractScheduledConsumerBuilderImpl<FileConsumerBuilder>
        implements FileConsumerBuilder, RequiresAopProxy
{
    /** filenames to match on */
    List<String> filenames;

    /** file encoding */
    String encoding;

    /** optionally include file header */
    Boolean includeHeader;

    /** optionally include file trailer */
    Boolean includeTrailer;

    /** sort criteria for file selection */
    Boolean sortByModifiedDateTime;

    /** sort criteria for file selection */
    Boolean sortAscending;

    /** how many directory levels to explore for matching files */
    Integer directoryDepth;

    /** log the matcher results */
    Boolean logMatchedFilenames;

    /** ignore in flight renames of files whilst running a scan */
    Boolean ignoreFileRenameWhilstScanning;

    /**
     * Constructor
     * @param scheduler
     * @param scheduledJobFactory
     * @param aopProxyProvider
     * @param messageProvider
     */
    public FileConsumerBuilderImpl(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory,
                                   AopProxyProvider aopProxyProvider, FileMessageProvider messageProvider)
    {
        super(scheduler, scheduledJobFactory, aopProxyProvider);
        this.messageProvider = messageProvider;
        if(messageProvider == null)
        {
            throw new IllegalArgumentException("fileMessageProvider cannot be 'null'");
        }
    }

    @Override
    public FileConsumerBuilder setConfiguration(FileConsumerConfiguration configuration)
    {
        this.configuration = configuration;
        return this;
    }

    @Override
    protected FileConsumerConfiguration createConfiguration()
    {
    return FileConsumerBuilder.newConfiguration();
    }

    @Override
    public FileConsumerBuilder setFilenames(List<String> filenames)
    {
        this.filenames = filenames;
        return this;
    }

    @Override
    public FileConsumerBuilder setEncoding(String encoding)
    {
        this.encoding = encoding;
        return this;
    }

    @Override
    public FileConsumerBuilder setIncludeHeader(boolean includeHeader)
    {
        this.includeHeader = Boolean.valueOf(includeHeader);
        return this;
    }

    @Override
    public FileConsumerBuilder setIncludeTrailer(boolean includeTrailer)
    {
        this.includeTrailer = Boolean.valueOf(includeTrailer);
        return this;
    }

    @Override
    public FileConsumerBuilder setSortByModifiedDateTime(boolean sortByModifiedDateTime)
    {
        this.sortByModifiedDateTime = Boolean.valueOf(sortByModifiedDateTime);
        return this;
    }

    @Override
    public FileConsumerBuilder setSortAscending(boolean sortAscending)
    {
        this.sortAscending = Boolean.valueOf(sortAscending);
        return this;
    }

    @Override
    public FileConsumerBuilder setDirectoryDepth(int directoryDepth)
    {
        this.directoryDepth = directoryDepth;
        return this;
    }

    @Override
    public FileConsumerBuilder setLogMatchedFilenames(boolean logMatchedFilenames)
    {
        this.logMatchedFilenames = Boolean.valueOf(logMatchedFilenames);
        return this;
    }

    @Override
    public FileConsumerBuilder setIgnoreFileRenameWhilstScanning(boolean ignoreFileRenameWhilstScanning)
    {
        this.ignoreFileRenameWhilstScanning = Boolean.valueOf(ignoreFileRenameWhilstScanning);
        return this;
    }

    @Override
    public FileConsumerBuilder setMessageProviderPostProcessor(MessageProviderPostProcessor messageProviderPostProcessor)
    {
        ((FileMessageProvider)this.messageProvider).setMessageProviderPostProcessor(messageProviderPostProcessor);
        return this;
    }

    @Override
    public ScheduledConsumer build()
    {
        ScheduledConsumer scheduledConsumer = super.build();

        FileConsumerConfiguration configuration = (FileConsumerConfiguration)scheduledConsumer.getConfiguration();

        if(filenames != null)
        {
            configuration.setFilenames(this.filenames);
        }

        if(encoding != null)
        {
            configuration.setEncoding(this.encoding);
        }

        if(includeHeader != null)
        {
            configuration.setIncludeHeader(this.includeHeader.booleanValue());
        }

        if(includeTrailer != null)
        {
            configuration.setIncludeTrailer(this.includeTrailer.booleanValue());
        }

        if(sortByModifiedDateTime != null)
        {
            configuration.setSortByModifiedDateTime(this.sortByModifiedDateTime.booleanValue());
        }

        if(sortAscending != null)
        {
            configuration.setSortAscending(this.sortAscending.booleanValue());
        }

        if(directoryDepth != null)
        {
            configuration.setDirectoryDepth(this.directoryDepth.intValue());
        }

        if(logMatchedFilenames != null)
        {
            configuration.setLogMatchedFilenames(this.logMatchedFilenames.booleanValue());
        }

        if(ignoreFileRenameWhilstScanning != null)
        {
            configuration.setIgnoreFileRenameWhilstScanning(this.ignoreFileRenameWhilstScanning.booleanValue());
        }

        return scheduledConsumer;
    }
}

