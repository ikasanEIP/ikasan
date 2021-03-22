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
import org.ikasan.component.endpoint.quartz.consumer.CallBackMessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.endpoint.sftp.consumer.SftpConsumer;
import org.ikasan.endpoint.sftp.consumer.SftpConsumerConfiguration;
import org.ikasan.endpoint.sftp.consumer.SftpMessageProvider;
import org.ikasan.framework.factory.DirectoryURLFactory;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.quartz.Scheduler;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Ikasan provided scheduled consumer default implementation.
 * This implemnetation allows for proxying of the object to facilitate transaction pointing.
 *
 * @author Ikasan Development Team
 */
public class SftpConsumerBuilderImpl
        extends AbstractScheduledConsumerBuilderImpl<SftpConsumerBuilder>
        implements SftpConsumerBuilder, RequiresAopProxy
{
    String sourceDirectory;
    String filenamePattern;
    DirectoryURLFactory sourceDirectoryURLFactory;
    Boolean filterDuplicates;
    Boolean filterOnFilename;
    Boolean filterOnLastModifiedDate;
    Boolean renameOnSuccess;
    String renameOnSuccessExtension;
    Boolean moveOnSuccess;
    String moveOnSuccessNewPath;
    Boolean chronological;
    Boolean chunking;
    Integer chunkSize;
    Boolean checksum;
    Long minAge;
    Boolean destructive;
    Integer maxRows;
    Integer ageOfFiles;
    String clientID;
    Boolean cleanupJournalOnComplete;
    String remoteHost;
    String privateKeyFilename;
    Integer maxRetryAttempts;
    Integer remotePort;
    String knownHostsFilename;
    String username;
    String password;
    Integer connectionTimeout;
    Boolean isRecursive;
    String preferredKeyExchangeAlgorithm;

    private TransactionalResourceCommandDAO transactionalResourceCommandDAO;

    private FileChunkDao fileChunkDao;

    private BaseFileTransferDao baseFileTransferDao;

    private JtaTransactionManager transactionManager;

    /**
     * Constructor
     * @param scheduler
     * @param scheduledJobFactory
     * @param aopProxyProvider
     * @param transactionManager
     * @param baseFileTransferDao
     * @param fileChunkDao
     * @param transactionalResourceCommandDAO
     */
    public SftpConsumerBuilderImpl(Scheduler scheduler,
                                   ScheduledJobFactory scheduledJobFactory,
                                   AopProxyProvider aopProxyProvider, JtaTransactionManager transactionManager,
                                   BaseFileTransferDao baseFileTransferDao, FileChunkDao fileChunkDao,
                                   TransactionalResourceCommandDAO transactionalResourceCommandDAO)
    {
        super(scheduler,scheduledJobFactory,aopProxyProvider);
        this.transactionManager = transactionManager;
        this.baseFileTransferDao = baseFileTransferDao;
        this.fileChunkDao = fileChunkDao;
        this.transactionalResourceCommandDAO = transactionalResourceCommandDAO;
    }

    @Override
    public SftpConsumerBuilder setConfiguration(SftpConsumerConfiguration configuration)
    {
        this.configuration = configuration;
        return this;
    }

    /**
     * Sets Transaction Manager which is different than default transaction manager set through constructor.
     *
     * @param transactionManager
     * @return
     */
    @Override
    public SftpConsumerBuilder setTransactionManager(JtaTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
        return this;
    }

    @Override
    public SftpConsumerBuilder setSourceDirectory(String sourceDirectory)
    {
        this.sourceDirectory = sourceDirectory;
        return this;
    }

    @Override
    public SftpConsumerBuilder setFilenamePattern(String filenamePattern)
    {
        this.filenamePattern = filenamePattern;
        return this;
    }

    @Override
    public SftpConsumerBuilder setSourceDirectoryURLFactory(DirectoryURLFactory sourceDirectoryURLFactory)
    {
        this.sourceDirectoryURLFactory = sourceDirectoryURLFactory;
        return this;
    }

    @Override
    public SftpConsumerBuilder setFilterDuplicates(Boolean filterDuplicates)
    {
        this.filterDuplicates = filterDuplicates;
        return this;
    }

    @Override
    public SftpConsumerBuilder setFilterOnFilename(Boolean filterOnFilename)
    {
        this.filterOnFilename = filterOnFilename;
        return this;
    }

    @Override
    public SftpConsumerBuilder setFilterOnLastModifiedDate(Boolean filterOnLastModifiedDate)
    {
        this.filterOnLastModifiedDate = filterOnLastModifiedDate;
        return this;
    }

    @Override
    public SftpConsumerBuilder setRenameOnSuccess(Boolean renameOnSuccess)
    {
        this.renameOnSuccess = renameOnSuccess;
        return this;
    }

    @Override
    public SftpConsumerBuilder setRenameOnSuccessExtension(String renameOnSuccessExtension)
    {
        this.renameOnSuccessExtension = renameOnSuccessExtension;
        return this;
    }

    @Override
    public SftpConsumerBuilder setMoveOnSuccess(Boolean moveOnSuccess)
    {
        this.moveOnSuccess = moveOnSuccess;
        return this;
    }

    @Override
    public SftpConsumerBuilder setMoveOnSuccessNewPath(String moveOnSuccessNewPath)
    {
        this.moveOnSuccessNewPath = moveOnSuccessNewPath;
        return this;
    }

    @Override
    public SftpConsumerBuilder setChronological(Boolean chronological)
    {
        this.chronological = chronological;
        return this;
    }

    @Override
    public SftpConsumerBuilder setChunking(Boolean chunking)
    {
        this.chunking = chunking;
        return this;
    }

    @Override
    public SftpConsumerBuilder setChunkSize(Integer chunkSize)
    {
        this.chunkSize = chunkSize;
        return this;
    }

    @Override
    public SftpConsumerBuilder setChecksum(Boolean checksum)
    {
        this.checksum = checksum;
        return this;
    }

    @Override
    public SftpConsumerBuilder setMinAge(Long minAge)
    {
        this.minAge = minAge;
        return this;
    }

    @Override
    public SftpConsumerBuilder setDestructive(Boolean destructive)
    {
        this.destructive = destructive;
        return this;
    }

    @Override
    public SftpConsumerBuilder setMaxRows(Integer maxRows)
    {
        this.maxRows = maxRows;
        return this;
    }

    @Override
    public SftpConsumerBuilder setAgeOfFiles(Integer ageOfFiles)
    {
        this.ageOfFiles = ageOfFiles;
        return this;
    }

    @Override
    public SftpConsumerBuilder setClientID(String clientID)
    {
        this.clientID = clientID;
        return this;
    }

    @Override
    public SftpConsumerBuilder setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete)
    {
        this.cleanupJournalOnComplete = cleanupJournalOnComplete;
        return this;
    }

    @Override
    public SftpConsumerBuilder setRemoteHost(String remoteHost)
    {
        this.remoteHost = remoteHost;
        return this;
    }

    @Override
    public SftpConsumerBuilder setPrivateKeyFilename(String privateKeyFilename)
    {
        this.privateKeyFilename = privateKeyFilename;
        return this;
    }

    @Override
    public SftpConsumerBuilder setMaxRetryAttempts(Integer maxRetryAttempts)
    {
        this.maxRetryAttempts = maxRetryAttempts;
        return this;
    }

    @Override
    public SftpConsumerBuilder setRemotePort(Integer remotePort)
    {
        this.remotePort = remotePort;
        return this;
    }

    @Override
    public SftpConsumerBuilder setKnownHostsFilename(String knownHostsFilename)
    {
        this.knownHostsFilename = knownHostsFilename;
        return this;
    }

    @Override
    public SftpConsumerBuilder setUsername(String username)
    {
        this.username = username;
        return this;
    }

    @Override
    public SftpConsumerBuilder setPassword(String password)
    {
        this.password = password;
        return this;
    }

    @Override
    public SftpConsumerBuilder setConnectionTimeout(Integer connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    @Override
    public SftpConsumerBuilder setIsRecursive(Boolean isRecursive)
    {
        this.isRecursive = isRecursive;
        return this;
    }

    @Override
    public SftpConsumerBuilder setPreferredKeyExchangeAlgorithm(String preferredKeyExchangeAlgorithm)
    {
        this.preferredKeyExchangeAlgorithm = preferredKeyExchangeAlgorithm;
        return this;
    }

    @Override
    public SftpConsumerBuilder setScheduledJobGroupName(String scheduledJobGroupName)
    {
        this.scheduledJobGroupName = scheduledJobGroupName;
        return this;
    }

    @Override
    public SftpConsumerBuilder setScheduledJobName(String scheduledJobName)
    {
        this.scheduledJobName = scheduledJobName;
        return this;
    }

    @Override
    protected SftpConsumerConfiguration createConfiguration()
    {
        return SftpConsumerBuilder.newConfiguration();
    }

    /**
     * WARNING this is used to override the consumer used for testing purposes only - BEWARE of USE
     * @return
     */
    protected SftpConsumer getScheduledConsumer()
    {
        return new SftpConsumer(scheduler);
    }

    protected Class<? extends ScheduledConsumer> getScheduledConsumerClass(){
        return SftpConsumer.class;
    }


    protected SftpConsumer _build()
    {
        SftpConsumer sftpConsumer;
        sftpConsumer = getScheduledConsumer();
        if (messageProvider != null)
        {
            sftpConsumer.setMessageProvider(messageProvider);
        }
        return sftpConsumer;
    }

    /**
     * Configure the raw component based on the properties passed to the builder, configure it
     * ready for use and return the instance.
     * @return
     */
    public SftpConsumer build()
    {
        SftpConsumer sftpConsumer = (SftpConsumer)super.build();
        SftpConsumerConfiguration configuration = (SftpConsumerConfiguration)sftpConsumer.getConfiguration();

        if(messageProvider == null)
        {
            SftpMessageProvider sftpMessageProvider = new SftpMessageProvider(transactionManager,baseFileTransferDao,
                    fileChunkDao, transactionalResourceCommandDAO);
            sftpConsumer.setMessageProvider(sftpMessageProvider);
        }

        if(sourceDirectory != null)
        {
            configuration.setSourceDirectory(sourceDirectory);
        }

        if(filenamePattern != null)
        {
            configuration.setFilenamePattern(filenamePattern);
        }

        if(sourceDirectoryURLFactory != null)
        {
            configuration.setSourceDirectoryURLFactory(sourceDirectoryURLFactory);
        }

        if(filterDuplicates != null)
        {
            configuration.setFilterDuplicates(filterDuplicates);
        }

        if(filterOnFilename != null)
        {
            configuration.setFilterOnFilename(filterOnFilename);
        }

        if(filterOnLastModifiedDate != null)
        {
            configuration.setFilterOnLastModifiedDate(filterOnLastModifiedDate);
        }

        if(renameOnSuccess != null)
        {
            configuration.setRenameOnSuccess(renameOnSuccess);
        }

        if(renameOnSuccessExtension != null)
        {
            configuration.setRenameOnSuccessExtension(renameOnSuccessExtension);
        }

        if(moveOnSuccess != null)
        {
            configuration.setMoveOnSuccess(moveOnSuccess);
        }

        if(moveOnSuccessNewPath != null)
        {
            configuration.setMoveOnSuccessNewPath(moveOnSuccessNewPath);
        }

        if(chronological != null)
        {
            configuration.setChronological(chronological);
        }

        if(chunking != null)
        {
            configuration.setChunking(chunking);
        }

        if(chunkSize != null)
        {
            configuration.setChunkSize(chunkSize);
        }

        if(checksum != null)
        {
            configuration.setChecksum(checksum);
        }

        if(minAge != null)
        {
            configuration.setMinAge(minAge);
        }

        if(destructive != null)
        {
            configuration.setDestructive(destructive);
        }

        if(maxRows != null)
        {
            configuration.setMaxRows(maxRows);
        }

        if(ageOfFiles != null)
        {
            configuration.setAgeOfFiles(ageOfFiles);
        }

        if(clientID != null)
        {
            configuration.setClientID(clientID);
        }

        if(cleanupJournalOnComplete != null)
        {
            configuration.setCleanupJournalOnComplete(cleanupJournalOnComplete);
        }

        if(remoteHost != null)
        {
            configuration.setRemoteHost(remoteHost);
        }

        if(privateKeyFilename != null)
        {
            configuration.setPrivateKeyFilename(privateKeyFilename);
        }

        if(maxRetryAttempts != null)
        {
            configuration.setMaxRetryAttempts(maxRetryAttempts);
        }

        if(remotePort != null)
        {
            configuration.setRemotePort(remotePort);
        }

        if(knownHostsFilename != null)
        {
            configuration.setKnownHostsFilename(knownHostsFilename);
        }

        if(username != null)
        {
            configuration.setUsername(username);
        }

        if(password != null)
        {
            configuration.setPassword(password);
        }

        if(connectionTimeout != null)
        {
            configuration.setConnectionTimeout(connectionTimeout);
        }

        if(isRecursive != null)
        {
            configuration.setIsRecursive(isRecursive);
        }

        if(preferredKeyExchangeAlgorithm != null)
        {
            configuration.setPreferredKeyExchangeAlgorithm(preferredKeyExchangeAlgorithm);
        }

        return sftpConsumer;
    }
}

