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

import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.ikasan.endpoint.ftp.producer.FtpProducer;
import org.ikasan.endpoint.ftp.producer.FtpProducerConfiguration;
import org.ikasan.endpoint.ftp.util.FileBasedPasswordHelper;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Ikasan provided Ftp Producer Builder implementation.
 * This implementation allows for proxying of the object to facilitate transaction pointing.
 *
 * @author Ikasan Development Team
 */
public class FtpProducerBuilderImpl implements FtpProducerBuilder
{

    private TransactionalResourceCommandDAO transactionalResourceCommandDAO;

    private FileChunkDao fileChunkDao;

    private BaseFileTransferDao baseFileTransferDao;

    private JtaTransactionManager transactionManager;

    private FileBasedPasswordHelper fileBasedPasswordHelper;

    private FtpProducerConfiguration configuration;

    private ManagedResourceRecoveryManager managedResourceRecoveryManager;

    private String configuredResourceId;

    private boolean criticalOnStartup;

    /**
     *
     * @param transactionManager
     * @param baseFileTransferDao
     * @param fileChunkDao
     * @param transactionalResourceCommandDAO
     */
    public FtpProducerBuilderImpl(JtaTransactionManager transactionManager,
            BaseFileTransferDao baseFileTransferDao, FileChunkDao fileChunkDao,
            TransactionalResourceCommandDAO transactionalResourceCommandDAO)
    {
        this.transactionManager = transactionManager;
        this.baseFileTransferDao = baseFileTransferDao;
        this.fileChunkDao = fileChunkDao;
        this.transactionalResourceCommandDAO = transactionalResourceCommandDAO;

    }

    @Override public FtpProducerBuilder setCriticalOnStartup(boolean criticalOnStartup)
    {
        this.criticalOnStartup = criticalOnStartup;
        return this;
    }

    /**
     * ConfigurationService identifier for this component configuration.
     * @param configuredResourceId
     * @return
     */
    public FtpProducerBuilder setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
        return this;
    }

    /**
     * Actual runtime configuration
     * @param ftpProducerConfiguration
     * @return
     */
    public FtpProducerBuilder setConfiguration(FtpProducerConfiguration ftpProducerConfiguration)
    {
        this.configuration = ftpProducerConfiguration;
        return this;
    }

    /**
     * Give the component a handle directly to the recovery manager
     * @param managedResourceRecoveryManager
     * @return
     */
    public FtpProducerBuilder setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        this.managedResourceRecoveryManager = managedResourceRecoveryManager;
        return this;
    }

    /**
     * Give the component a FileBasedPasswordHelper
     * @param fileBasedPasswordHelper
     * @return
     */
    public FtpProducerBuilder setFileBasedPasswordHelper(FileBasedPasswordHelper fileBasedPasswordHelper)
    {
        this.fileBasedPasswordHelper = fileBasedPasswordHelper;
        return this;
    }


    @Override
    public FtpProducerBuilder setOutputDirectory(String outputDirectory)
    {
        getConfiguration().setOutputDirectory(outputDirectory);
        return this;
    }

    @Override
    public FtpProducerBuilder setRenameExtension(String renameExtension)
    {
        getConfiguration().setRenameExtension(renameExtension);
        return this;
    }

    @Override
    public FtpProducerBuilder setTempFileName(String tempFileName)
    {
        getConfiguration().setTempFileName(tempFileName);
        return this;
    }

    @Override
    public FtpProducerBuilder setChecksumDelivered(Boolean checksumDelivered)
    {
        getConfiguration().setChecksumDelivered(checksumDelivered);
        return this;
    }

    @Override
    public FtpProducerBuilder setClientID(String clientID)
    {
        getConfiguration().setClientID(clientID);
        return this;
    }

    @Override
    public FtpProducerBuilder setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete)
    {
        getConfiguration().setCleanupJournalOnComplete(cleanupJournalOnComplete);
        return this;
    }

    @Override
    public FtpProducerBuilder setCreateParentDirectory(Boolean createParentDirectory)
    {
        getConfiguration().setCreateParentDirectory(createParentDirectory);
        return this;
    }

    @Override
    public FtpProducerBuilder setOverwrite(Boolean overwrite)
    {
        getConfiguration().setOverwrite(overwrite);
        return this;
    }

    @Override
    public FtpProducerBuilder setActive(Boolean active)
    {
        getConfiguration().setActive(active);
        return this;
    }

    @Override
    public FtpProducerBuilder setUnzip(Boolean unzip)
    {
        getConfiguration().setUnzip(unzip);
        return this;
    }

    @Override
    public FtpProducerBuilder setRemoteHost(String remoteHost)
    {
        getConfiguration().setRemoteHost(remoteHost);
        return this;
    }

    @Override
    public FtpProducerBuilder setMaxRetryAttempts(Integer maxRetryAttempts)
    {
        getConfiguration().setMaxRetryAttempts(maxRetryAttempts);
        return this;
    }

    @Override
    public FtpProducerBuilder setRemotePort(Integer remotePort)
    {
        getConfiguration().setRemotePort(remotePort);
        return this;
    }

    @Override
    public FtpProducerBuilder setUsername(String username)
    {
        getConfiguration().setUsername(username);
        return this;
    }

    @Override
    public FtpProducerBuilder setPassword(String password)
    {
        getConfiguration().setPassword(password);
        return this;
    }

    @Override
    public FtpProducerBuilder setConnectionTimeout(Integer connectionTimeout)
    {
        getConfiguration().setConnectionTimeout(connectionTimeout);
        return this;
    }

    @Override
    public FtpProducerBuilder setSystemKey(String systemKey)
    {
        getConfiguration().setSystemKey(systemKey);
        return this;
    }

    @Override
    public FtpProducerBuilder setSocketTimeout(Integer socketTimeout)
    {
        getConfiguration().setSocketTimeout(socketTimeout);
        return this;
    }

    @Override
    public FtpProducerBuilder setDataTimeout(Integer dataTimeout)
    {
        getConfiguration().setDataTimeout(dataTimeout);
        return this;
    }

    @Override
    public FtpProducerBuilder setIsFTPS(Boolean isFTPS)
    {
        getConfiguration().setFTPS(isFTPS);
        return this;
    }

    @Override
    public FtpProducerBuilder setFtpsPort(Integer ftpsPort)
    {
        getConfiguration().setFtpsPort(ftpsPort);
        return this;
    }

    @Override
    public FtpProducerBuilder setFtpsProtocol(String ftpsProtocol)
    {
        getConfiguration().setFtpsProtocol(ftpsProtocol);
        return this;
    }

    @Override
    public FtpProducerBuilder setFtpsIsImplicit(Boolean ftpsIsImplicit)
    {
        getConfiguration().setFtpsIsImplicit(ftpsIsImplicit);
        return this;
    }

    @Override
    public FtpProducerBuilder setFtpsKeyStoreFilePath(String ftpsKeyStoreFilePath)
    {
        getConfiguration().setFtpsKeyStoreFilePath(ftpsKeyStoreFilePath);
        return this;
    }

    @Override
    public FtpProducerBuilder setFtpsKeyStoreFilePassword(String ftpsKeyStoreFilePassword)
    {
        getConfiguration().setFtpsKeyStoreFilePassword(ftpsKeyStoreFilePassword);
        return this;
    }

    private FtpProducerConfiguration getConfiguration()
    {
        if(configuration == null)
        {
            configuration = new FtpProducerConfiguration();
        }

        return configuration;
    }

    /**
     * Configure the raw component based on the properties passed to the builder, configure it
     * ready for use and return the instance.
     * @return
     */
    public FtpProducer build()
    {
        if(this.fileBasedPasswordHelper == null){
            this.fileBasedPasswordHelper = new FileBasedPasswordHelper();
        }
        FtpProducer ftpProducer = new FtpProducer(transactionManager, baseFileTransferDao, fileChunkDao,
                transactionalResourceCommandDAO, fileBasedPasswordHelper);
        ftpProducer.setConfiguration(this.configuration);
        ftpProducer.setConfiguredResourceId(this.configuredResourceId);
        if(this.criticalOnStartup)
        {
            ftpProducer.setCriticalOnStartup(criticalOnStartup);
        }

        if(this.managedResourceRecoveryManager!=null)
        {
            ftpProducer.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        }
        return ftpProducer;
    }

}

