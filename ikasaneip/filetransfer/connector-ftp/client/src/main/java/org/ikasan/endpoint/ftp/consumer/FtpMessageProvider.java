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
package org.ikasan.endpoint.ftp.consumer;

import org.apache.log4j.Logger;
import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.connector.listener.TransactionCommitEvent;
import org.ikasan.filetransfer.Payload;
import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.connector.ftp.outbound.FTPConnectionSpec;
import org.ikasan.connector.listener.TransactionCommitFailureListener;
import org.ikasan.endpoint.ftp.util.FileBasedPasswordHelper;
import org.ikasan.framework.factory.DirectoryURLFactory;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.quartz.JobExecutionContext;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;
import javax.resource.spi.InvalidPropertyException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation on MessageProvider using JobExecutionContext as a message type.
 *
 * @author Ikasan Development Team
 */
public class FtpMessageProvider implements ManagedResource, MessageProvider<Payload>, TransactionCommitFailureListener
{
    private static Logger logger = Logger.getLogger(FtpMessageProvider.class);

    /**
     * Currently active connection template
     */
    protected FileTransferConnectionTemplate activeFileTransferConnectionTemplate;

    /**
     * A connection template
     */
    protected FileTransferConnectionTemplate fileTransferConnectionTemplate;

    /**
     * Alternate template to be used in cases of failure
     */
    protected FileTransferConnectionTemplate alternateFileTransferConnectionTemplate;

    /**
     * Configuration
     */
    protected FtpConsumerConfiguration configuration;

    /**
     * Connection factory
     */
    private final ConnectionFactory connectionFactory;

    private FileBasedPasswordHelper fileBasedPasswordHelper;

    /**
     * Directory URL factory
     */
    private DirectoryURLFactory directoryURLFactory;

    private ManagedResourceRecoveryManager managedResourceRecoveryManager;

    /**
     * Constructor
     *
     * @param connectionFactory FTP connection factory
     */
    public FtpMessageProvider(final ConnectionFactory connectionFactory,
            FileBasedPasswordHelper fileBasedPasswordHelper)
    {
        this.connectionFactory = connectionFactory;
        if (this.connectionFactory == null)
        {
            throw new IllegalArgumentException("connectionFactory cannot be 'null'");
        }
        this.fileBasedPasswordHelper = fileBasedPasswordHelper;
        if (this.fileBasedPasswordHelper == null)
        {
            throw new IllegalArgumentException("fileBasedPasswordHelper cannot be 'null'");
        }
    }

    @Override public Payload invoke(JobExecutionContext context)
    {
        Payload payload = null;
        List<String> sourceDirectories = this.getSourceDirectories();
        for (String sourceDirectory : sourceDirectories)
        {
            try
            {
                payload = this.activeFileTransferConnectionTemplate
                    .getDiscoveredFile(sourceDirectory, this.configuration.getFilenamePattern(),
                         this.configuration.getRenameOnSuccess().booleanValue(),
                         this.configuration.getRenameOnSuccessExtension(),
                         this.configuration.getMoveOnSuccess().booleanValue(),
                         this.configuration.getMoveOnSuccessNewPath(),
                         this.configuration.getChunking().booleanValue(),
                         this.configuration.getChunkSize().intValue(),
                         this.configuration.getChecksum().booleanValue(),
                         this.configuration.getMinAge().longValue(),
                         this.configuration.getDestructive().booleanValue(),
                         this.configuration.getFilterDuplicates().booleanValue(),
                         this.configuration.getFilterOnFilename().booleanValue(),
                         this.configuration.getFilterOnLastModifiedDate().booleanValue(),
                         this.configuration.getChronological().booleanValue());
                if (payload != null)
                {
                    return payload;
                }
            }
            catch (ResourceException e)
            {
                this.switchActiveConnection();
                throw new EndpointException(e);
            }
        }
        try
        {
            this.housekeep();
        }
        catch (ResourceException e)
        {
            throw new EndpointException(e);
        }
        return payload;
    }

    /**
     * Apply any configured housekeeping on this connection template.
     *
     * @throws ResourceException - Exception if the JCA connector fails
     */
    protected void housekeep() throws ResourceException
    {
        int maxRows = this.configuration.getMaxRows().intValue();
        int ageOfFiles = this.configuration.getAgeOfFiles().intValue();
        // If the values have been set then housekeep, else don't
        if (maxRows > -1 && ageOfFiles > -1)
        {
            this.fileTransferConnectionTemplate.housekeep(maxRows, ageOfFiles);
        }
    }

    /**
     * Return a list of src directories to be polled.
     *
     * @return List of src directories
     */
    protected List<String> getSourceDirectories()
    {
        List<String> dirs = new ArrayList<String>();
        // If we've been passed a factory it means there are multiple directories to
        // poll, starting from this.srcDirectory
        if (this.configuration.getSourceDirectoryURLFactory() != null)
        {
            dirs = this.configuration.getSourceDirectoryURLFactory()
                    .getDirectoriesURLs(this.configuration.getSourceDirectory());
        }
        else
        {
            dirs.add(this.configuration.getSourceDirectory());
        }
        return dirs;
    }

    /**
     * @param alternate the {@link FileTransferConnectionTemplate} alternate to use
     */
    public void setAlternateFileTransferConnectionTemplate(final FileTransferConnectionTemplate alternate)
    {
        this.alternateFileTransferConnectionTemplate = alternate;
    }

    /**
     * This method is only used for testing purposes
     *
     * @return the alternateFileTransferConnectionTemplate
     */
    FileTransferConnectionTemplate getAlternateFileTransferConnectionTemplate()
    {
        return this.alternateFileTransferConnectionTemplate;
    }

    /**
     * This method is only used for testing purposes
     *
     * @return the activeFileTransferConnectionTemplate
     */
    FileTransferConnectionTemplate getActiveFileTransferConnectionTemplate()
    {
        return this.activeFileTransferConnectionTemplate;
    }

    /**
     * Switch the active connection to the other connection template.
     */
    protected void switchActiveConnection()
    {
        logger.info("Switch Active Connection to " + alternateFileTransferConnectionTemplate);
        if (this.alternateFileTransferConnectionTemplate != null)
        {
            if (this.activeFileTransferConnectionTemplate == this.fileTransferConnectionTemplate)
            {
                this.activeFileTransferConnectionTemplate = this.alternateFileTransferConnectionTemplate;
            }
            else
            {
                this.activeFileTransferConnectionTemplate = this.fileTransferConnectionTemplate;
            }
        }
    }

    @Override public void startManagedResource()
    {
        try
        {
            configuration.validate();
            FTPConnectionSpec spec = createSpec(configuration);
            FTPConnectionSpec alternateSpec = createAlternateSpec(configuration);
            // Finally, update populated configuration with complex objects that cannot be specified by front end clients
            configuration.setSourceDirectoryURLFactory(this.directoryURLFactory);
            getEndpoint(spec, alternateSpec);
        }
        catch (InvalidPropertyException e)
        {
            throw new EndpointException(e);
        }
    }

    @Override public void stopManagedResource()
    {
    }

    @Override public void setManagedResourceRecoveryManager(
            ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        this.managedResourceRecoveryManager = managedResourceRecoveryManager;
    }

    @Override public boolean isCriticalOnStartup()
    {
        return false;
    }

    @Override public void setCriticalOnStartup(boolean criticalOnStartup)
    {
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointFactory#createEndpoint(java.lang.Object)
     */
    private FTPConnectionSpec createSpec(FtpConsumerConfiguration ftpConsumerConfiguration)
    {
        FTPConnectionSpec spec = this.getConnectionSpec();
        spec.setClientID(ftpConsumerConfiguration.getClientID());
        spec.setActive(ftpConsumerConfiguration.getActive());
        spec.setCleanupJournalOnComplete(ftpConsumerConfiguration.getCleanupJournalOnComplete());
        spec.setConnectionTimeout(ftpConsumerConfiguration.getConnectionTimeout());
        spec.setDataTimeout(ftpConsumerConfiguration.getDataTimeout());
        spec.setMaxRetryAttempts(ftpConsumerConfiguration.getMaxRetryAttempts());
        // We get the password from a file if it is so configured.
        if (ftpConsumerConfiguration.getPasswordFilePath() != null
                && ftpConsumerConfiguration.getPasswordFilePath().length() > 0)
        {
            try
            {
                spec.setPassword(
                        fileBasedPasswordHelper.getPasswordFromFile(ftpConsumerConfiguration.getPasswordFilePath()));
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            spec.setPassword(ftpConsumerConfiguration.getPassword());
        }
        spec.setRemoteHostname(ftpConsumerConfiguration.getRemoteHost());
        spec.setRemotePort(ftpConsumerConfiguration.getRemotePort());
        spec.setSocketTimeout(ftpConsumerConfiguration.getSocketTimeout());
        spec.setSystemKey(ftpConsumerConfiguration.getSystemKey());
        spec.setUsername(ftpConsumerConfiguration.getUsername());
        spec.setPassword(ftpConsumerConfiguration.getPassword());
        spec.setIsFTPS(ftpConsumerConfiguration.getIsFTPS());
        spec.setFtpsProtocol(ftpConsumerConfiguration.getFtpsProtocol());
        spec.setFtpsPort(ftpConsumerConfiguration.getFtpsPort());
        spec.setFtpsIsImplicit(ftpConsumerConfiguration.getFtpsIsImplicit());
        spec.setFtpsKeyStoreFilePath(ftpConsumerConfiguration.getFtpsKeyStoreFilePath());
        spec.setFtpsKeyStoreFilePassword(ftpConsumerConfiguration.getFtpsKeyStoreFilePassword());
        return spec;
    }

    private FTPConnectionSpec createAlternateSpec(FtpConsumerConfiguration ftpConsumerConfiguration)
    {
        FTPConnectionSpec alternateSpec = null;
        if (ftpConsumerConfiguration instanceof FtpConsumerAlternateConfiguration)
        {
            FtpConsumerAlternateConfiguration alternateConfig = (FtpConsumerAlternateConfiguration) ftpConsumerConfiguration;
            alternateSpec = this.getConnectionSpec();
            alternateSpec.setClientID(alternateConfig.getClientID());
            alternateSpec.setActive(alternateConfig.getAlternateActive());
            alternateSpec.setCleanupJournalOnComplete(alternateConfig.getCleanupJournalOnComplete());
            alternateSpec.setConnectionTimeout(alternateConfig.getAlternateConnectionTimeout());
            alternateSpec.setDataTimeout(alternateConfig.getAlternateDataTimeout());
            alternateSpec.setMaxRetryAttempts(alternateConfig.getAlternateMaxRetryAttempts());
            alternateSpec.setPassword(alternateConfig.getAlternatePassword());
            alternateSpec.setRemoteHostname(alternateConfig.getAlternateRemoteHost());
            alternateSpec.setRemotePort(alternateConfig.getAlternateRemotePort());
            alternateSpec.setSocketTimeout(alternateConfig.getAlternateSocketTimeout());
            alternateSpec.setSystemKey(alternateConfig.getAlternateSystemKey());
            alternateSpec.setUsername(alternateConfig.getAlternateUsername());
            alternateSpec.setIsFTPS(ftpConsumerConfiguration.getIsFTPS());
            alternateSpec.setFtpsProtocol(ftpConsumerConfiguration.getFtpsProtocol());
            alternateSpec.setFtpsPort(ftpConsumerConfiguration.getFtpsPort());
            alternateSpec.setFtpsIsImplicit(ftpConsumerConfiguration.getFtpsIsImplicit());
            alternateSpec.setFtpsKeyStoreFilePath(ftpConsumerConfiguration.getFtpsKeyStoreFilePath());
            alternateSpec.setFtpsKeyStoreFilePassword(ftpConsumerConfiguration.getFtpsKeyStoreFilePassword());
        }
        return alternateSpec;
    }

    /**
     * Internal endpoint creation method allows for easier overriding of the actual endpoint creation and simpler testing.
     *
     * @param spec
     * @param alternateSpec
     * @return
     */
    private void getEndpoint(final FTPConnectionSpec spec, final FTPConnectionSpec alternateSpec)
    {
        activeFileTransferConnectionTemplate = new FileTransferConnectionTemplate(this.connectionFactory, spec);
        activeFileTransferConnectionTemplate.addListener(this);
        if (alternateSpec != null)
        {
            alternateFileTransferConnectionTemplate = new FileTransferConnectionTemplate(this.connectionFactory, spec);
            alternateFileTransferConnectionTemplate.addListener(this);
        }
    }

    /**
     * Utility method to aid testing of this class
     *
     * @return
     */
    protected FTPConnectionSpec getConnectionSpec()
    {
        return new FTPConnectionSpec();
    }

    /**
     * @param directoryURLFactory the directoryURLFactory to set
     */
    public void setDirectoryURLFactory(DirectoryURLFactory directoryURLFactory)
    {
        this.directoryURLFactory = directoryURLFactory;
    }

    public void setConfiguration(FtpConsumerConfiguration configuration)
    {
        this.configuration = configuration;
    }

    @Override public void commitFailureOccurred(TransactionCommitEvent event)
    {
        logger.info("Logging error: " + event.getException().getMessage());
        this.managedResourceRecoveryManager.recover(event.getException());
    }
}
