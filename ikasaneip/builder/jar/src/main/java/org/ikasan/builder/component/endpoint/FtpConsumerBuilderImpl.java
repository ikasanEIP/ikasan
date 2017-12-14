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
import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.endpoint.ftp.consumer.FtpConsumerConfiguration;
import org.ikasan.endpoint.ftp.consumer.FtpMessageProvider;
import org.ikasan.endpoint.ftp.util.FileBasedPasswordHelper;
import org.ikasan.endpoint.sftp.consumer.SftpMessageProvider;
import org.ikasan.framework.factory.DirectoryURLFactory;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.quartz.Job;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Ikasan provided scheduled consumer default implementation.
 * This implemnetation allows for proxying of the object to facilitate transaction pointing.
 *
 * @author Ikasan Development Team
 */
public class FtpConsumerBuilderImpl extends ScheduledConsumerBuilderImpl implements FtpConsumerBuilder
{

    private TransactionalResourceCommandDAO transactionalResourceCommandDAO;

    private FileChunkDao fileChunkDao;

    private BaseFileTransferDao baseFileTransferDao;

    private JtaTransactionManager transactionManager;

    private MessageProvider messageProvider;
    /**
     * Constructor
     * @param scheduledConsumer
     */
    public FtpConsumerBuilderImpl(ScheduledConsumer scheduledConsumer, ScheduledJobFactory scheduledJobFactory,
            AopProxyProvider aopProxyProvider, JtaTransactionManager transactionManager,
            BaseFileTransferDao baseFileTransferDao, FileChunkDao fileChunkDao,
            TransactionalResourceCommandDAO transactionalResourceCommandDAO)
    {
        super(scheduledConsumer,scheduledJobFactory,aopProxyProvider);
        this.transactionManager = transactionManager;
        this.baseFileTransferDao = baseFileTransferDao;
        this.fileChunkDao = fileChunkDao;
        this.transactionalResourceCommandDAO = transactionalResourceCommandDAO;

    }

    /**
     * Is this successful start of this component critical on flow start.
     * If it can recover post flow start up then its not crititcal.
     * @param criticalOnStartup
     * @return
     */
    @Override
    public FtpConsumerBuilder setCriticalOnStartup(boolean criticalOnStartup)
    {
        return (FtpConsumerBuilder) super.setCriticalOnStartup(criticalOnStartup);

    }

    /**
     * ConfigurationService identifier for this component configuration.
     * @param configuredResourceId
     * @return
     */
    public FtpConsumerBuilder setConfiguredResourceId(String configuredResourceId)
    {
        return (FtpConsumerBuilder) super.setConfiguredResourceId(configuredResourceId);
    }

    /**
     * Actual runtime configuration
     * @param scheduledConsumerConfiguration
     * @return
     */
    public FtpConsumerBuilder setConfiguration(ScheduledConsumerConfiguration scheduledConsumerConfiguration)
    {
        return (FtpConsumerBuilder) super.setConfiguration(scheduledConsumerConfiguration);

    }

    /**
     * Underlying tech providing the message event
     * @param messageProvider
     * @return
     */
    public FtpConsumerBuilder setMessageProvider(MessageProvider messageProvider)
    {
        this.messageProvider = messageProvider;
        return this;
    }

    /**
     * Implementation of the managed event identifier service - sets the life identifier based on the incoming event.
     * @param managedEventIdentifierService
     * @return
     */
    public FtpConsumerBuilder setManagedEventIdentifierService(ManagedEventIdentifierService managedEventIdentifierService)
    {
        return (FtpConsumerBuilder) super.setManagedEventIdentifierService(managedEventIdentifierService);
    }

    /**
     * Give the component a handle directly to the recovery manager
     * @param managedResourceRecoveryManager
     * @return
     */
    public FtpConsumerBuilder setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        return (FtpConsumerBuilder) super.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
    }

    /**
     * Override default event factory
     * @param eventFactory
     * @return
     */
    public FtpConsumerBuilder setEventFactory(EventFactory eventFactory) {
        return (FtpConsumerBuilder) super.setEventFactory(eventFactory);
    }

    /**
     * Scheduled consumer cron expression
     * @param cronExpression
     * @return
     */
    @Override
    public FtpConsumerBuilder setCronExpression(String cronExpression)
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
    @Override
    public FtpConsumerBuilder setEager(boolean eager) {
        getConfiguration().setEager(eager);
        return this;
    }

    /**
     * Whether to ignore call back failures.
     * @param ignoreMisfire
     * @return
     */
    @Override
    public FtpConsumerBuilder setIgnoreMisfire(boolean ignoreMisfire) {
        getConfiguration().setIgnoreMisfire(ignoreMisfire);
        return this;
    }

    /**
     * Specifically set the timezone of the scheduled callback.
     * @param timezone
     * @return
     */
    @Override
    public FtpConsumerBuilder setTimezone(String timezone) {
        getConfiguration().setTimezone(timezone);
        return this;
    }

    @Override
    public FtpConsumerBuilder setSourceDirectory(String sourceDirectory)
    {
        getConfiguration().setSourceDirectory(sourceDirectory);
        return this;
    }

    @Override
    public FtpConsumerBuilder setFilenamePattern(String filenamePattern)
    {
        getConfiguration().setFilenamePattern(filenamePattern);
        return this;
    }

    @Override
    public FtpConsumerBuilder setSourceDirectoryURLFactory(DirectoryURLFactory sourceDirectoryURLFactory)
    {
        getConfiguration().setSourceDirectoryURLFactory(sourceDirectoryURLFactory);
        return this;
    }

    @Override
    public FtpConsumerBuilder setFilterDuplicates(Boolean filterDuplicates)
    {
        getConfiguration().setFilterDuplicates(filterDuplicates);
        return this;
    }

    @Override
    public FtpConsumerBuilder setFilterOnFilename(Boolean filterOnFilename)
    {
        getConfiguration().setFilterOnFilename(filterOnFilename);
        return this;
    }

    @Override
    public FtpConsumerBuilder setFilterOnLastModifiedDate(Boolean filterOnLastModifiedDate)
    {
        getConfiguration().setFilterOnLastModifiedDate(filterOnLastModifiedDate);
        return this;
    }

    @Override
    public FtpConsumerBuilder setRenameOnSuccess(Boolean renameOnSuccess)
    {
        getConfiguration().setRenameOnSuccess(renameOnSuccess);
        return this;
    }

    @Override
    public FtpConsumerBuilder setRenameOnSuccessExtension(String renameOnSuccessExtension)
    {
        getConfiguration().setRenameOnSuccessExtension(renameOnSuccessExtension);
        return this;
    }

    @Override
    public FtpConsumerBuilder setMoveOnSuccess(Boolean moveOnSuccess)
    {
        getConfiguration().setMoveOnSuccess(moveOnSuccess);
        return this;
    }

    @Override
    public FtpConsumerBuilder setMoveOnSuccessNewPath(String moveOnSuccessNewPath)
    {
        getConfiguration().setMoveOnSuccessNewPath(moveOnSuccessNewPath);
        return this;
    }

    @Override
    public FtpConsumerBuilder setChronological(Boolean chronological)
    {
        getConfiguration().setChronological(chronological);
        return this;
    }

    @Override
    public FtpConsumerBuilder setChunking(Boolean chunking)
    {
        getConfiguration().setChunking(chunking);
        return this;
    }

    @Override
    public FtpConsumerBuilder setChunkSize(Integer chunkSize)
    {
        getConfiguration().setChunkSize(chunkSize);
        return this;
    }

    @Override
    public FtpConsumerBuilder setChecksum(Boolean checksum)
    {
        getConfiguration().setChecksum(checksum);
        return this;
    }

    @Override
    public FtpConsumerBuilder setMinAge(Long minAge)
    {
        getConfiguration().setMinAge(minAge);
        return this;
    }

    @Override
    public FtpConsumerBuilder setDestructive(Boolean destructive)
    {
        getConfiguration().setDestructive(destructive);
        return this;
    }

    @Override
    public FtpConsumerBuilder setMaxRows(Integer maxRows)
    {
        getConfiguration().setMaxRows(maxRows);
        return this;
    }

    @Override
    public FtpConsumerBuilder setAgeOfFiles(Integer ageOfFiles)
    {
        getConfiguration().setAgeOfFiles(ageOfFiles);
        return this;
    }

    @Override
    public FtpConsumerBuilder setClientID(String clientID)
    {
        getConfiguration().setClientID(clientID);
        return this;
    }

    @Override
    public FtpConsumerBuilder setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete)
    {
        getConfiguration().setCleanupJournalOnComplete(cleanupJournalOnComplete);
        return this;
    }

    @Override
    public FtpConsumerBuilder setRemoteHost(String remoteHost)
    {
        getConfiguration().setRemoteHost(remoteHost);
        return this;
    }

    @Override
    public FtpConsumerBuilder setMaxRetryAttempts(Integer maxRetryAttempts)
    {
        getConfiguration().setMaxRetryAttempts(maxRetryAttempts);
        return this;
    }

    @Override
    public FtpConsumerBuilder setRemotePort(Integer remotePort)
    {
        getConfiguration().setRemotePort(remotePort);
        return this;
    }

    @Override
    public FtpConsumerBuilder setUsername(String username)
    {
        getConfiguration().setUsername(username);
        return this;
    }

    @Override
    public FtpConsumerBuilder setPassword(String password)
    {
        getConfiguration().setPassword(password);
        return this;
    }

    @Override
    public FtpConsumerBuilder setConnectionTimeout(Integer connectionTimeout)
    {
        getConfiguration().setConnectionTimeout(connectionTimeout);
        return this;
    }

    @Override
    public FtpConsumerBuilder setIsRecursive(Boolean isRecursive)
    {
        getConfiguration().setIsRecursive(isRecursive);
        return this;
    }

    @Override
    public FtpConsumerBuilder setFtpsKeyStoreFilePassword(String ftpsKeyStoreFilePassword)
    {
        getConfiguration().setFtpsKeyStoreFilePassword(ftpsKeyStoreFilePassword);
        return this;
    }

    @Override
    public FtpConsumerBuilder setFtpsKeyStoreFilePath(String ftpsKeyStoreFilePath)
    {
        getConfiguration().setFtpsKeyStoreFilePath(ftpsKeyStoreFilePath);
        return this;
    }

    @Override
    public FtpConsumerBuilder setFtpsIsImplicit(Boolean ftpsIsImplicit)
    {
        getConfiguration().setFtpsIsImplicit(ftpsIsImplicit);
        return this;
    }

    @Override
    public FtpConsumerBuilder setFtpsProtocol(String ftpsProtocol)
    {
        getConfiguration().setFtpsProtocol(ftpsProtocol);
        return this;
    }

    @Override
    public FtpConsumerBuilder setFtpsPort(Integer ftpsPort)
    {
        getConfiguration().setFtpsPort(ftpsPort);
        return this;
    }

    @Override
    public FtpConsumerBuilder setIsFTPS(Boolean isFTPS)
    {
        getConfiguration().setIsFTPS(isFTPS);
        return this;
    }

    @Override
    public FtpConsumerBuilder setPasswordFilePath(String passwordFilePath)
    {
        getConfiguration().setPasswordFilePath(passwordFilePath);
        return this;
    }

    @Override
    public FtpConsumerBuilder setSystemKey(String systemKey)
    {
        getConfiguration().setSystemKey(systemKey);
        return this;
    }

    @Override
    public FtpConsumerBuilder setSocketTimeout(Integer socketTimeout)
    {
        getConfiguration().setSocketTimeout(socketTimeout);
        return this;
    }

    @Override
    public FtpConsumerBuilder setDataTimeout(Integer dataTimeout)
    {
        getConfiguration().setDataTimeout(dataTimeout);
        return this;
    }

    @Override
    public FtpConsumerBuilder setActive(Boolean active)
    {
        getConfiguration().setActive(active);
        return this;
    }

    @Override
    public FtpConsumerBuilder setScheduledJobGroupName(String scheduledJobGroupName) {
        this.scheduledJobGroupName = scheduledJobGroupName;
        return this;
    }

    @Override
    public FtpConsumerBuilder setScheduledJobName(String scheduledJobName) {
        return (FtpConsumerBuilder) super.setScheduledJobName(scheduledJobName);
    }


    private FtpConsumerConfiguration getConfiguration()
    {
        FtpConsumerConfiguration FtpConsumerConfiguration = (FtpConsumerConfiguration) this.scheduledConsumer.getConfiguration();
        if(FtpConsumerConfiguration == null)
        {
            FtpConsumerConfiguration = new FtpConsumerConfiguration();
            this.scheduledConsumer.setConfiguration(FtpConsumerConfiguration);
        }

        return FtpConsumerConfiguration;
    }

    /**
     * Configure the raw component based on the properties passed to the builder, configure it
     * ready for use and return the instance.
     * @return
     */
    public ScheduledConsumer build() {
        if (this.scheduledConsumer.getConfiguration() == null) {
            this.scheduledConsumer.setConfiguration(new FtpConsumerConfiguration());
        }

        validateBuilderConfiguration();

        if(messageProvider != null)
        {
            this.scheduledConsumer.setMessageProvider(messageProvider);
        }
        else
        {
            FtpMessageProvider sftpMessageProvider = new FtpMessageProvider(transactionManager,baseFileTransferDao,
                    fileChunkDao, transactionalResourceCommandDAO, new FileBasedPasswordHelper());
            sftpMessageProvider.setConfiguration(getConfiguration());
            this.scheduledConsumer.setMessageProvider(sftpMessageProvider);
        }

        if(this.aopProxyProvider == null)
        {
            scheduledConsumer.setJobDetail( scheduledJobFactory.createJobDetail(scheduledConsumer, ScheduledConsumer.class, this.scheduledJobName, this.scheduledJobGroupName) );
        }
        else
        {
            Job pointcutJob = this.aopProxyProvider.applyPointcut(this.scheduledJobName, scheduledConsumer);
            scheduledConsumer.setJobDetail( scheduledJobFactory.createJobDetail(pointcutJob, ScheduledConsumer.class, this.scheduledJobName, this.scheduledJobGroupName) );
        }

        return this.scheduledConsumer;
    }

    protected void validateBuilderConfiguration()
    {
        if(this.scheduledJobName == null)
        {
            throw new IllegalArgumentException("scheduledJobName is a required property for the scheduledConsumer and cannot be 'null'");
        }

        if(this.scheduledJobGroupName == null)
        {
            throw new IllegalArgumentException("scheduledJobGroupName is a required property for the scheduledConsumer and cannot be 'null'");
        }
    }

    @Override
    public void setAopProxyProvider(AopProxyProvider aopProxyProvider) {
        this.aopProxyProvider = aopProxyProvider;
    }
}

