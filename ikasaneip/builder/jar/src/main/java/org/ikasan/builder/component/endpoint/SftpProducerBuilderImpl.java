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
import org.ikasan.endpoint.sftp.producer.SftpProducer;
import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Ikasan provided Sftp Producer Builder implementation.
 * This implemnetation allows for proxying of the object to facilitate transaction pointing.
 *
 * @author Ikasan Development Team
 */
public class SftpProducerBuilderImpl implements SftpProducerBuilder
{

    private TransactionalResourceCommandDAO transactionalResourceCommandDAO;

    private FileChunkDao fileChunkDao;

    private BaseFileTransferDao baseFileTransferDao;

    private JtaTransactionManager transactionManager;

    private SftpProducerConfiguration configuration;

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
    public SftpProducerBuilderImpl(JtaTransactionManager transactionManager,
            BaseFileTransferDao baseFileTransferDao, FileChunkDao fileChunkDao,
            TransactionalResourceCommandDAO transactionalResourceCommandDAO)
    {
        this.transactionManager = transactionManager;
        this.baseFileTransferDao = baseFileTransferDao;
        this.fileChunkDao = fileChunkDao;
        this.transactionalResourceCommandDAO = transactionalResourceCommandDAO;

    }

    @Override public SftpProducerBuilder setCriticalOnStartup(boolean criticalOnStartup)
    {
        this.criticalOnStartup = criticalOnStartup;
        return this;
    }

    /**
     * ConfigurationService identifier for this component configuration.
     * @param configuredResourceId
     * @return
     */
    public SftpProducerBuilder setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
        return this;
    }

    /**
     * Actual runtime configuration
     * @param sftpProducerConfiguration
     * @return
     */
    public SftpProducerBuilder setConfiguration(SftpProducerConfiguration sftpProducerConfiguration)
    {
        this.configuration = sftpProducerConfiguration;
        return this;
    }

    /**
     * Give the component a handle directly to the recovery manager
     * @param managedResourceRecoveryManager
     * @return
     */
    public SftpProducerBuilder setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        this.managedResourceRecoveryManager = managedResourceRecoveryManager;
        return this;
    }

    @Override
    public SftpProducerBuilder setOutputDirectory(String outputDirectory)
    {
        getConfiguration().setOutputDirectory(outputDirectory);
        return this;
    }

    @Override
    public SftpProducerBuilder setRenameExtension(String renameExtension)
    {
        getConfiguration().setRenameExtension(renameExtension);
        return this;
    }

    @Override
    public SftpProducerBuilder setTempFileName(String tempFileName)
    {
        getConfiguration().setTempFileName(tempFileName);
        return this;
    }

    @Override
    public SftpProducerBuilder setChecksumDelivered(Boolean checksumDelivered)
    {
        getConfiguration().setChecksumDelivered(checksumDelivered);
        return this;
    }

    @Override
    public SftpProducerBuilder setCleanUpChunks(Boolean cleanUpChunks)
    {
        getConfiguration().setCleanUpChunks(cleanUpChunks);
        return this;
    }

    @Override
    public SftpProducerBuilder setClientID(String clientID)
    {
        getConfiguration().setClientID(clientID);
        return this;
    }

    @Override
    public SftpProducerBuilder setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete)
    {
        getConfiguration().setCleanupJournalOnComplete(cleanupJournalOnComplete);
        return this;
    }

    @Override
    public SftpProducerBuilder setCreateParentDirectory(Boolean createParentDirectory)
    {
        getConfiguration().setCreateParentDirectory(createParentDirectory);
        return this;
    }

    @Override
    public SftpProducerBuilder setOverwrite(Boolean overwrite)
    {
        getConfiguration().setOverwrite(overwrite);
        return this;
    }

    @Override
    public SftpProducerBuilder setUnzip(Boolean unzip)
    {
        getConfiguration().setUnzip(unzip);
        return this;
    }

    @Override
    public SftpProducerBuilder setRemoteHost(String remoteHost)
    {
        getConfiguration().setRemoteHost(remoteHost);
        return this;
    }

    @Override
    public SftpProducerBuilder setPrivateKeyFilename(String privateKeyFilename)
    {
        getConfiguration().setPrivateKeyFilename(privateKeyFilename);
        return this;
    }

    @Override
    public SftpProducerBuilder setMaxRetryAttempts(Integer maxRetryAttempts)
    {
        getConfiguration().setMaxRetryAttempts(maxRetryAttempts);
        return this;
    }

    @Override
    public SftpProducerBuilder setRemotePort(Integer remotePort)
    {
        getConfiguration().setRemotePort(remotePort);
        return this;
    }

    @Override
    public SftpProducerBuilder setKnownHostsFilename(String knownHostsFilename)
    {
        getConfiguration().setKnownHostsFilename(knownHostsFilename);
        return this;
    }

    @Override
    public SftpProducerBuilder setUsername(String username)
    {
        getConfiguration().setUsername(username);
        return this;
    }

    @Override
    public SftpProducerBuilder setPassword(String password)
    {
        getConfiguration().setPassword(password);
        return this;
    }

    @Override
    public SftpProducerBuilder setConnectionTimeout(Integer connectionTimeout)
    {
        getConfiguration().setConnectionTimeout(connectionTimeout);
        return this;
    }

    @Override
    public SftpProducerBuilder setPreferredKeyExchangeAlgorithm(String preferredKeyExchangeAlgorithm)
    {
        getConfiguration().setPreferredKeyExchangeAlgorithm(preferredKeyExchangeAlgorithm);
        return this;
    }


    private SftpProducerConfiguration getConfiguration()
    {
        if(configuration == null)
        {
            configuration = new SftpProducerConfiguration();
        }

        return configuration;
    }

    /**
     * Configure the raw component based on the properties passed to the builder, configure it
     * ready for use and return the instance.
     * @return
     */
    public SftpProducer build()
    {
        SftpProducer sftpProducer = new SftpProducer(transactionManager, baseFileTransferDao, fileChunkDao,
                transactionalResourceCommandDAO);
        sftpProducer.setConfiguration(this.configuration);
        sftpProducer.setConfiguredResourceId(this.configuredResourceId);
        if(this.criticalOnStartup)
        {
            sftpProducer.setCriticalOnStartup(criticalOnStartup);
        }

        if(this.managedResourceRecoveryManager!=null)
        {
            sftpProducer.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        }
        return sftpProducer;
    }

}

