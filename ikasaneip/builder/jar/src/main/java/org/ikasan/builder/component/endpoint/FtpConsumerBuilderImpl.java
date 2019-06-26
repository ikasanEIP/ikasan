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
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.endpoint.ftp.consumer.FtpConsumerConfiguration;
import org.ikasan.endpoint.ftp.consumer.FtpMessageProvider;
import org.ikasan.endpoint.ftp.util.FileBasedPasswordHelper;
import org.ikasan.framework.factory.DirectoryURLFactory;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.quartz.Scheduler;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Ikasan provided FTP scheduled consumer default implementation.
 *
 * This implementation allows for proxying of the object to facilitate transaction pointing.
 *
 * @author Ikasan Development Team
 */
public class FtpConsumerBuilderImpl extends AbstractScheduledConsumerBuilderImpl<FtpConsumerBuilder>
        implements FtpConsumerBuilder
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
    Integer maxRetryAttempts;
    Integer remotePort;
    String username;
    String password;
    Integer connectionTimeout;
    Boolean isRecursive;
    String ftpsKeyStoreFilePassword;
    String ftpsKeyStoreFilePath;
    Boolean ftpsIsImplicit;
    String ftpsProtocol;
    Integer ftpsPort;
    Boolean FTPS;
    String passwordFilePath;
    String systemKey;
    Integer socketTimeout;
    Integer dataTimeout;
    Boolean active;

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
    public FtpConsumerBuilderImpl(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory,
                                  AopProxyProvider aopProxyProvider, JtaTransactionManager transactionManager,
                                  BaseFileTransferDao baseFileTransferDao, FileChunkDao fileChunkDao,
                                  TransactionalResourceCommandDAO transactionalResourceCommandDAO)
    {
        super(scheduler, scheduledJobFactory, aopProxyProvider);
        this.transactionManager = transactionManager;
        this.baseFileTransferDao = baseFileTransferDao;
        this.fileChunkDao = fileChunkDao;
        this.transactionalResourceCommandDAO = transactionalResourceCommandDAO;
    }

    public FtpConsumerBuilder setConfiguration(FtpConsumerConfiguration configuration)
    {
        this.configuration = configuration;
        return this;
    }

    @Override
    public FtpConsumerBuilder setSourceDirectory(String sourceDirectory)
    {
        this.sourceDirectory = sourceDirectory;
        return this;
    }

    @Override
    public FtpConsumerBuilder setFilenamePattern(String filenamePattern)
    {
        this.filenamePattern = filenamePattern;
        return this;
    }

    @Override
    public FtpConsumerBuilder setSourceDirectoryURLFactory(DirectoryURLFactory sourceDirectoryURLFactory)
    {
        this.sourceDirectoryURLFactory = sourceDirectoryURLFactory;
        return this;
    }

    @Override
    public FtpConsumerBuilder setFilterDuplicates(Boolean filterDuplicates)
    {
        this.filterDuplicates = filterDuplicates;
        return this;
    }

    @Override
    public FtpConsumerBuilder setFilterOnFilename(Boolean filterOnFilename)
    {
        this.filterOnFilename = filterOnFilename;
        return this;
    }

    @Override
    public FtpConsumerBuilder setFilterOnLastModifiedDate(Boolean filterOnLastModifiedDate)
    {
        this.filterOnLastModifiedDate = filterOnLastModifiedDate;
        return this;
    }

    @Override
    public FtpConsumerBuilder setRenameOnSuccess(Boolean renameOnSuccess)
    {
        this.renameOnSuccess = renameOnSuccess;
        return this;
    }

    @Override
    public FtpConsumerBuilder setRenameOnSuccessExtension(String renameOnSuccessExtension)
    {
        this.renameOnSuccessExtension = renameOnSuccessExtension;
        return this;
    }

    @Override
    public FtpConsumerBuilder setMoveOnSuccess(Boolean moveOnSuccess)
    {
        this.moveOnSuccess = moveOnSuccess;
        return this;
    }

    @Override
    public FtpConsumerBuilder setMoveOnSuccessNewPath(String moveOnSuccessNewPath)
    {
        this.moveOnSuccessNewPath = moveOnSuccessNewPath;
        return this;
    }

    @Override
    public FtpConsumerBuilder setChronological(Boolean chronological)
    {
        this.chronological = chronological;
        return this;
    }

    @Override
    public FtpConsumerBuilder setChunking(Boolean chunking)
    {
        this.chunking = chunking;
        return this;
    }

    @Override
    public FtpConsumerBuilder setChunkSize(Integer chunkSize)
    {
        this.chunkSize = chunkSize;
        return this;
    }

    @Override
    public FtpConsumerBuilder setChecksum(Boolean checksum)
    {
        this.checksum = checksum;
        return this;
    }

    @Override
    public FtpConsumerBuilder setMinAge(Long minAge)
    {
        this.minAge = minAge;
        return this;
    }

    @Override
    public FtpConsumerBuilder setDestructive(Boolean destructive)
    {
        this.destructive = destructive;
        return this;
    }

    @Override
    public FtpConsumerBuilder setMaxRows(Integer maxRows)
    {
        this.maxRows = maxRows;
        return this;
    }

    @Override
    public FtpConsumerBuilder setAgeOfFiles(Integer ageOfFiles)
    {
        this.ageOfFiles = ageOfFiles;
        return this;
    }

    @Override
    public FtpConsumerBuilder setClientID(String clientID)
    {
        this.clientID = clientID;
        return this;
    }

    @Override
    public FtpConsumerBuilder setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete)
    {
        this.cleanupJournalOnComplete = cleanupJournalOnComplete;
        return this;
    }

    @Override
    public FtpConsumerBuilder setRemoteHost(String remoteHost)
    {
        this.remoteHost = remoteHost;
        return this;
    }

    @Override
    public FtpConsumerBuilder setMaxRetryAttempts(Integer maxRetryAttempts)
    {
        this.maxRetryAttempts = maxRetryAttempts;
        return this;
    }

    @Override
    public FtpConsumerBuilder setRemotePort(Integer remotePort)
    {
        this.remotePort = remotePort;
        return this;
    }

    @Override
    public FtpConsumerBuilder setUsername(String username)
    {
        this.username = username;
        return this;
    }

    @Override
    public FtpConsumerBuilder setPassword(String password)
    {
        this.password = password;
        return this;
    }

    @Override
    public FtpConsumerBuilder setConnectionTimeout(Integer connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    @Override
    public FtpConsumerBuilder setIsRecursive(Boolean isRecursive)
    {
        this.isRecursive = isRecursive;
        return this;
    }

    @Override
    public FtpConsumerBuilder setFtpsKeyStoreFilePassword(String ftpsKeyStoreFilePassword)
    {
        this.ftpsKeyStoreFilePassword = ftpsKeyStoreFilePassword;
        return this;
    }

    @Override
    public FtpConsumerBuilder setFtpsKeyStoreFilePath(String ftpsKeyStoreFilePath)
    {
        this.ftpsKeyStoreFilePath = ftpsKeyStoreFilePath;
        return this;
    }

    @Override
    public FtpConsumerBuilder setFtpsIsImplicit(Boolean ftpsIsImplicit)
    {
        this.ftpsIsImplicit = ftpsIsImplicit;
        return this;
    }

    @Override
    public FtpConsumerBuilder setFtpsProtocol(String ftpsProtocol)
    {
        this.ftpsProtocol = ftpsProtocol;
        return this;
    }

    @Override
    public FtpConsumerBuilder setFtpsPort(Integer ftpsPort)
    {
        this.ftpsPort = ftpsPort;
        return this;
    }

    @Override
    public FtpConsumerBuilder setFTPS(Boolean FTPS)
    {
        this.FTPS = FTPS;
        return this;
    }

    @Override
    public FtpConsumerBuilder setPasswordFilePath(String passwordFilePath)
    {
        this.passwordFilePath = passwordFilePath;
        return this;
    }

    @Override
    public FtpConsumerBuilder setSystemKey(String systemKey)
    {
        this.systemKey = systemKey;
        return this;
    }

    @Override
    public FtpConsumerBuilder setSocketTimeout(Integer socketTimeout)
    {
        this.socketTimeout = socketTimeout;
        return this;
    }

    @Override
    public FtpConsumerBuilder setDataTimeout(Integer dataTimeout)
    {
        this.dataTimeout = dataTimeout;
        return this;
    }

    @Override
    public FtpConsumerBuilder setActive(Boolean active)
    {
        this.active = active;
        return this;
    }

    @Override
    protected FtpConsumerConfiguration createConfiguration()
    {
        return FtpConsumerBuilder.newConfiguration();
    }


    public ScheduledConsumer build()
    {
        ScheduledConsumer scheduledConsumer = super.build();
        FtpConsumerConfiguration configuration = (FtpConsumerConfiguration)scheduledConsumer.getConfiguration();

        if(messageProvider == null)
        {
            FtpMessageProvider ftpMessageProvider = new FtpMessageProvider(transactionManager,baseFileTransferDao,
                    fileChunkDao, transactionalResourceCommandDAO, new FileBasedPasswordHelper());
            scheduledConsumer.setMessageProvider(ftpMessageProvider);
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

        if(maxRetryAttempts != null)
        {
            configuration.setMaxRetryAttempts(maxRetryAttempts);
        }

        if(remotePort != null)
        {
            configuration.setRemotePort(remotePort);
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

        if(ftpsKeyStoreFilePassword != null)
        {
            configuration.setFtpsKeyStoreFilePassword(ftpsKeyStoreFilePassword);
        }

        if(ftpsKeyStoreFilePath != null)
        {
            configuration.setFtpsKeyStoreFilePath(ftpsKeyStoreFilePath);
        }

        if(ftpsIsImplicit != null)
        {
            configuration.setFtpsIsImplicit(ftpsIsImplicit);
        }

        if(ftpsProtocol != null)
        {
            configuration.setFtpsProtocol(ftpsProtocol);
        }

        if(ftpsPort != null)
        {
            configuration.setFtpsPort(ftpsPort);
        }

        if(FTPS != null)
        {
            configuration.setFTPS(FTPS);
        }

        if(passwordFilePath != null)
        {
            configuration.setPasswordFilePath(passwordFilePath);
        }

        if(systemKey != null)
        {
            configuration.setSystemKey(systemKey);
        }

        if(socketTimeout != null)
        {
            configuration.setSocketTimeout(socketTimeout);
        }

        if(dataTimeout != null)
        {
            configuration.setDataTimeout(dataTimeout);
        }

        if(active != null)
        {
            configuration.setActive(active);
        }

        return scheduledConsumer;
    }
}

