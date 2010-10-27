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
package org.ikasan.connector.sftp.consumer;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionSpec;

import org.ikasan.connector.base.outbound.EISConnectionFactory;
import org.ikasan.connector.sftp.outbound.SFTPConnectionSpec;
import org.ikasan.framework.payload.service.FileTransferPayloadProvider;
import org.ikasan.spec.endpoint.Consumer;
import org.ikasan.spec.endpoint.EndpointManager;
import org.ikasan.spec.endpoint.EndpointActivator;

/**
 * Endpoint manager for SFTP consumer endpoint implementations based on an 
 * Sftp protocol and configuration.
 * @author Ikasan Development Team
 */
public class SftpConsumerEndpointManager implements EndpointManager<Consumer<?>,SftpConsumerConfiguration>
{
    /** configuration */
    private SftpConsumerConfiguration sftpConfiguration;
    
    /** consumer endpoint */
    private Consumer<?> consumer;

    /** connection factory */
    private EISConnectionFactory connectionFactory;
    
    /**
     * Constructor
     * @param connectionFactory
     * @param sftpConfiguration
     */
    public SftpConsumerEndpointManager(EISConnectionFactory connectionFactory, SftpConsumerConfiguration sftpConfiguration)
    {
        this.connectionFactory = connectionFactory;
        if(connectionFactory == null)
        {
            throw new IllegalArgumentException("connectionFactory cannot be 'null'");
        }

        this.sftpConfiguration = sftpConfiguration;
        if(sftpConfiguration == null)
        {
            throw new IllegalArgumentException("sftpConfiguration cannot be 'null'");
        }
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#start()
     */
    public void start() throws ResourceException
    {
        SFTPConnectionSpec spec = this.getConnectionSpec();
        spec.setClientID(sftpConfiguration.getClientID());
        spec.setRemoteHostname(sftpConfiguration.getRemoteHost());
        spec.setKnownHostsFilename(sftpConfiguration.getKnownHostsFilename());
        spec.setMaxRetryAttempts(sftpConfiguration.getMaxRetryAttempts());
        spec.setRemotePort(sftpConfiguration.getRemotePort());
        spec.setPrivateKeyFilename(sftpConfiguration.getPrivateKeyFilename());
        spec.setConnectionTimeout(sftpConfiguration.getConnectionTimeout());
        spec.setUsername(sftpConfiguration.getUsername());
        spec.setCleanupJournalOnComplete(sftpConfiguration.getCleanupJournalOnComplete());
        this.consumer = this.getConsumer(spec);
        
        if(this.consumer instanceof EndpointActivator)
        {
            ((EndpointActivator) this.consumer).activate();
        }
    }

    /**
     * Utility method to aid testing of this class
     * @return
     */
    protected SFTPConnectionSpec getConnectionSpec()
    {
        return new SFTPConnectionSpec();
    }
    
    /**
     * Utility method to aid testing of this class
     * @param spec
     * @return
     */
    protected Consumer<?> getConsumer(ConnectionSpec spec)
    {
        return new SftpMapConsumer(new FileTransferPayloadProvider(sftpConfiguration.getSourceDirectory(), 
            sftpConfiguration.getFilenamePattern(), connectionFactory, spec), sftpConfiguration);
    }
    
    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#getEndpoint()
     */
    public Consumer<?> getEndpoint()
    {
        return this.consumer;
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#stop()
     */
    public void stop() throws ResourceException
    {
        if(this.consumer != null && this.consumer instanceof EndpointActivator)
        {
            try
            {
                ((EndpointActivator)consumer).deactivate();
            }
            finally
            {
                this.consumer = null;
            }
        }
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#setConfiguration(java.lang.Object)
     */
    public void setConfiguration(SftpConsumerConfiguration sftpConfiguration)
    {
        this.sftpConfiguration = sftpConfiguration;
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#getConfiguration()
     */
    public SftpConsumerConfiguration getConfiguration()
    {
        return this.sftpConfiguration;
    }

}
