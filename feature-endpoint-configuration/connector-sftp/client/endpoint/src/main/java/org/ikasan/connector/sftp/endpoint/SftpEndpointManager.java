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
package org.ikasan.connector.sftp.endpoint;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.connector.sftp.configuration.SftpConfiguration;
import org.ikasan.connector.sftp.outbound.SFTPConnectionSpec;
import org.ikasan.spec.endpoint.EndpointManager;
import org.ikasan.spec.endpoint.EndpointActivator;
import org.ikasan.spec.endpoint.Producer;

/**
 * Endpoint manager for SFTP producer endpoint implementations based on an 
 * Sftp protocol and configuration.
 * @author Ikasan Development Team
 */
public class SftpEndpointManager implements EndpointManager<Producer<?>,SftpConfiguration>
{
    /** configuration */
    private SftpConfiguration sftpConfiguration;
    
    /** producer endpoint */
    private Producer<?> producer;

    /** connection factory */
    private ConnectionFactory connectionFactory;

    /**
     * Constructor
     * @param connectionFactory
     * @param sftpConfiguration
     */
    public SftpEndpointManager(ConnectionFactory connectionFactory, SftpConfiguration sftpConfiguration)
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
        this.producer = this.getProducer(spec);
        
        if(this.producer instanceof EndpointActivator)
        {
            ((EndpointActivator) this.producer).activate();
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
    protected Producer<?> getProducer(ConnectionSpec spec)
    {
        return new SftpMapProducer(new FileTransferConnectionTemplate(connectionFactory, spec), sftpConfiguration);
    }
    
    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#getEndpoint()
     */
    public Producer<?> getEndpoint()
    {
        return this.producer;
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#stop()
     */
    public void stop() throws ResourceException
    {
        if(this.producer != null && this.producer instanceof EndpointActivator)
        {
            try
            {
                ((EndpointActivator)producer).deactivate();
            }
            finally
            {
                this.producer = null;
            }
        }
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#setConfiguration(java.lang.Object)
     */
    public void setConfiguration(SftpConfiguration sftpConfiguration)
    {
        this.sftpConfiguration = sftpConfiguration;
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#getConfiguration()
     */
    public SftpConfiguration getConfiguration()
    {
        return this.sftpConfiguration;
    }

}
