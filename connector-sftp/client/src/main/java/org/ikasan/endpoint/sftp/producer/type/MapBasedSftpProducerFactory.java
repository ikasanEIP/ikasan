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
package org.ikasan.endpoint.sftp.producer.type;

import javax.resource.cci.ConnectionFactory;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.connector.sftp.outbound.SFTPConnectionSpec;
import org.ikasan.endpoint.sftp.producer.SftpProducerAlternateConfiguration;
import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.ikasan.spec.endpoint.EndpointFactory;
import org.ikasan.spec.endpoint.Producer;

/**
 * SFTP producer factory for creating sftpProducer endpoint implementations.
 * 
 * @author Ikasan Development Team
 */
public class MapBasedSftpProducerFactory implements EndpointFactory<Producer<?>,SftpProducerConfiguration>
{
    /** Connection factory */
    private ConnectionFactory connectionFactory;

    /**
     * Constructor
     * @param connectionFactory SFTP connection factory
     */
    public MapBasedSftpProducerFactory(ConnectionFactory connectionFactory)
    {
        this.connectionFactory = connectionFactory;
        if(connectionFactory == null)
        {
            throw new IllegalArgumentException("connectionFactory cannot be 'null'");
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.endpoint.EndpointFactory#createEndpoint(java.lang.Object)
     */
    public Producer<?> createEndpoint(final SftpProducerConfiguration sftpProducerConfiguration)
    {
        SFTPConnectionSpec spec = this.getConnectionSpec();
        spec.setClientID(sftpProducerConfiguration.getClientID());
        spec.setRemoteHostname(sftpProducerConfiguration.getRemoteHost());
        spec.setKnownHostsFilename(sftpProducerConfiguration.getKnownHostsFilename());
        spec.setMaxRetryAttempts(sftpProducerConfiguration.getMaxRetryAttempts());
        spec.setRemotePort(sftpProducerConfiguration.getRemotePort());
        spec.setPrivateKeyFilename(sftpProducerConfiguration.getPrivateKeyFilename());
        spec.setConnectionTimeout(sftpProducerConfiguration.getConnectionTimeout());
        spec.setUsername(sftpProducerConfiguration.getUsername());
        spec.setCleanupJournalOnComplete(sftpProducerConfiguration.getCleanupJournalOnComplete());

        SFTPConnectionSpec alternateSpec = null;
        if (sftpProducerConfiguration instanceof SftpProducerAlternateConfiguration)
        {
            SftpProducerAlternateConfiguration alternateConfig = (SftpProducerAlternateConfiguration)sftpProducerConfiguration;

            alternateSpec = this.getConnectionSpec();
            alternateSpec.setClientID(alternateConfig.getClientID());
            alternateSpec.setRemoteHostname(alternateConfig.getAlternateRemoteHost());
            alternateSpec.setKnownHostsFilename(alternateConfig.getAlternateKnownHostsFilename());
            alternateSpec.setMaxRetryAttempts(alternateConfig.getAlternateMaxRetryAttempts());
            alternateSpec.setRemotePort(alternateConfig.getAlternateRemotePort());
            alternateSpec.setPrivateKeyFilename(alternateConfig.getAlternatePrivateKeyFilename());
            alternateSpec.setConnectionTimeout(alternateConfig.getAlternateConnectionTimeout());
            alternateSpec.setUsername(alternateConfig.getAlternateUsername());
            alternateSpec.setCleanupJournalOnComplete(alternateConfig.getCleanupJournalOnComplete());
        }
        return this.getEndpoint(spec, alternateSpec, sftpProducerConfiguration);
    }

    /**
     * Internal endpoint creation method allows for easier overriding of the actual endpoint creation and simpler testing.
     * @param fileTransferConnectionTemplate
     * @param sftpProducerConfiguration
     * @return
     */
    protected Producer<?> getEndpoint(final SFTPConnectionSpec spec, final SFTPConnectionSpec alternateSpec, final SftpProducerConfiguration sftpProducerConfiguration)
    {
        MapBasedSftpProducer producer = new MapBasedSftpProducer(new FileTransferConnectionTemplate(this.connectionFactory, spec), sftpProducerConfiguration);
        if (alternateSpec != null)
        {
            producer.setAlternateFileTransferConnectionTemplate(new FileTransferConnectionTemplate(this.connectionFactory, alternateSpec));
        }
        return producer;
    }
    
    /**
     * Utility method to aid testing of this class
     * @return SFTPConnectionSpec
     */
    protected SFTPConnectionSpec getConnectionSpec()
    {
        return new SFTPConnectionSpec();
    }
  
}
