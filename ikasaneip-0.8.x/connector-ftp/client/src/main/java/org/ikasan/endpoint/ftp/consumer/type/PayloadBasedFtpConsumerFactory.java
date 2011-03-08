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
package org.ikasan.endpoint.ftp.consumer.type;

import javax.resource.cci.ConnectionFactory;
import javax.resource.spi.InvalidPropertyException;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.connector.ftp.outbound.FTPConnectionSpec;
import org.ikasan.endpoint.ftp.consumer.FtpConsumerAlternateConfiguration;
import org.ikasan.endpoint.ftp.consumer.FtpConsumerConfiguration;
import org.ikasan.framework.factory.DirectoryURLFactory;
import org.ikasan.spec.endpoint.Consumer;
import org.ikasan.spec.endpoint.EndpointFactory;

/**
 * FTP consumer factory for creating SFTP Consumer endpoint implementations.
 * 
 * @author Ikasan Development Team
 */
public class PayloadBasedFtpConsumerFactory implements EndpointFactory<Consumer<?>,FtpConsumerConfiguration>
{
    /** Connection factory */
    private final ConnectionFactory connectionFactory;

    /** Directory URL factory */
    private DirectoryURLFactory directoryURLFactory;

    /**
     * Constructor
     * @param connectionFactory FTP connection factory
     */
    public PayloadBasedFtpConsumerFactory(final ConnectionFactory connectionFactory)
    {
        this.connectionFactory = connectionFactory;
        if(this.connectionFactory == null)
        {
            throw new IllegalArgumentException("connectionFactory cannot be 'null'");
        }
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointFactory#createEndpoint(java.lang.Object)
     */
    public Consumer<?> createEndpoint(FtpConsumerConfiguration ftpConsumerConfiguration) throws InvalidPropertyException
    {
        ftpConsumerConfiguration.validate();
        FTPConnectionSpec spec = this.getConnectionSpec();
        spec.setClientID(ftpConsumerConfiguration.getClientID());
        spec.setActive(ftpConsumerConfiguration.getActive());
        spec.setCleanupJournalOnComplete(ftpConsumerConfiguration.getCleanupJournalOnComplete());
        spec.setConnectionTimeout(ftpConsumerConfiguration.getConnectionTimeout());
        spec.setDataTimeout(ftpConsumerConfiguration.getDataTimeout());
        spec.setMaxRetryAttempts(ftpConsumerConfiguration.getMaxRetryAttempts());
        spec.setPassword(ftpConsumerConfiguration.getPassword());
        spec.setRemoteHostname(ftpConsumerConfiguration.getRemoteHost());
        spec.setRemotePort(ftpConsumerConfiguration.getRemotePort());
        spec.setSocketTimeout(ftpConsumerConfiguration.getSocketTimeout());
        spec.setSystemKey(ftpConsumerConfiguration.getSystemKey());
        spec.setUsername(ftpConsumerConfiguration.getUsername());

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
        }

        // Finally, update populated configuration with complex objects that cannot be specified by front end clients
        ftpConsumerConfiguration.setSourceDirectoryURLFactory(this.directoryURLFactory);

        return getEndpoint(spec, alternateSpec, ftpConsumerConfiguration);
    }

    /**
     * Internal endpoint creation method allows for easier overriding of the actual endpoint creation and simpler testing.
     * @param fileTransferConnectionTemplate
     * @param ftpConsumerConfiguration
     * @return
     */
    protected Consumer<?> getEndpoint(final FTPConnectionSpec spec, final FTPConnectionSpec alternateSpec, final FtpConsumerConfiguration ftpConsumerConfiguration)
    {
        PayloadBasedFtpConsumer consumer = new PayloadBasedFtpConsumer(new FileTransferConnectionTemplate(this.connectionFactory, spec), ftpConsumerConfiguration);
        if (alternateSpec != null)
        {
            consumer.setAlternateFileTransferConnectionTemplate(new FileTransferConnectionTemplate(this.connectionFactory, alternateSpec));
        }
        return consumer;
    }
    
    /**
     * Utility method to aid testing of this class
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
}
