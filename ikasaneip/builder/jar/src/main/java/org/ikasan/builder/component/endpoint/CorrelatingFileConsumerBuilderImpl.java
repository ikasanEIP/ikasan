package org.ikasan.builder.component.endpoint;

import org.ikasan.builder.AopProxyProvider;
import org.ikasan.builder.component.RequiresAopProxy;
import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileConsumerConfiguration;
import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatingFileMessageProvider;
import org.ikasan.component.endpoint.filesystem.messageprovider.FileMessageProvider;
import org.ikasan.component.endpoint.filesystem.messageprovider.MessageProviderPostProcessor;
import org.ikasan.component.endpoint.quartz.consumer.CorrelatingScheduledConsumer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.quartz.Scheduler;

import java.util.List;

public class CorrelatingFileConsumerBuilderImpl extends AbstractScheduledConsumerBuilderImpl<CorrelatingFileConsumerBuilder>
    implements CorrelatingFileConsumerBuilder, RequiresAopProxy {

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
    public CorrelatingFileConsumerBuilderImpl(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory,
                                   AopProxyProvider aopProxyProvider, CorrelatingFileMessageProvider messageProvider)
    {
        super(scheduler, scheduledJobFactory, aopProxyProvider);
        this.messageProvider = messageProvider;
        if(messageProvider == null)
        {
            throw new IllegalArgumentException("fileMessageProvider cannot be 'null'");
        }
    }

    @Override
    public CorrelatingFileConsumerBuilder setConfiguration(CorrelatedFileConsumerConfiguration configuration)
    {
        this.configuration = configuration;
        return this;
    }

    @Override
    protected CorrelatedFileConsumerConfiguration createConfiguration()
    {
        return CorrelatingFileConsumerBuilder.newConfiguration();
    }

    @Override
    public CorrelatingFileConsumerBuilder setFilenames(List<String> filenames)
    {
        this.filenames = filenames;
        return this;
    }

    @Override
    public CorrelatingFileConsumerBuilder setEncoding(String encoding)
    {
        this.encoding = encoding;
        return this;
    }

    @Override
    public CorrelatingFileConsumerBuilder setIncludeHeader(boolean includeHeader)
    {
        this.includeHeader = Boolean.valueOf(includeHeader);
        return this;
    }

    @Override
    public CorrelatingFileConsumerBuilder setIncludeTrailer(boolean includeTrailer)
    {
        this.includeTrailer = Boolean.valueOf(includeTrailer);
        return this;
    }

    @Override
    public CorrelatingFileConsumerBuilder setSortByModifiedDateTime(boolean sortByModifiedDateTime)
    {
        this.sortByModifiedDateTime = Boolean.valueOf(sortByModifiedDateTime);
        return this;
    }

    @Override
    public CorrelatingFileConsumerBuilder setSortAscending(boolean sortAscending)
    {
        this.sortAscending = Boolean.valueOf(sortAscending);
        return this;
    }

    @Override
    public CorrelatingFileConsumerBuilder setDirectoryDepth(int directoryDepth)
    {
        this.directoryDepth = directoryDepth;
        return this;
    }

    @Override
    public CorrelatingFileConsumerBuilder setLogMatchedFilenames(boolean logMatchedFilenames)
    {
        this.logMatchedFilenames = Boolean.valueOf(logMatchedFilenames);
        return this;
    }

    @Override
    public CorrelatingFileConsumerBuilder setIgnoreFileRenameWhilstScanning(boolean ignoreFileRenameWhilstScanning)
    {
        this.ignoreFileRenameWhilstScanning = Boolean.valueOf(ignoreFileRenameWhilstScanning);
        return this;
    }

    @Override
    public CorrelatingFileConsumerBuilder setMessageProviderPostProcessor(MessageProviderPostProcessor messageProviderPostProcessor)
    {
        ((FileMessageProvider)this.messageProvider).setMessageProviderPostProcessor(messageProviderPostProcessor);
        return this;
    }

    @Override
    protected ScheduledConsumer getScheduledConsumer() {
        return new CorrelatingScheduledConsumer(scheduler);
    }

    @Override
    public ScheduledConsumer build()
    {
        ScheduledConsumer scheduledConsumer = super.build();
        scheduledConsumer.setCriticalOnStartup(true);

        CorrelatedFileConsumerConfiguration configuration = (CorrelatedFileConsumerConfiguration)scheduledConsumer
            .getConfiguration();

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
