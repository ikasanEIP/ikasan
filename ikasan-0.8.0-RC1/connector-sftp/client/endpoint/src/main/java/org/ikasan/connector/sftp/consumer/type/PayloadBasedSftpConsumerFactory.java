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
package org.ikasan.connector.sftp.consumer.type;

import org.ikasan.connector.base.outbound.EISConnectionFactory;
import org.ikasan.connector.sftp.consumer.SftpConsumerConfiguration;
import org.ikasan.connector.sftp.outbound.SFTPConnectionSpec;
import org.ikasan.framework.payload.service.FileTransferPayloadProvider;
import org.ikasan.spec.endpoint.Consumer;
import org.ikasan.spec.endpoint.EndpointFactory;

/**
 * SFTP consumer factory for creating sftpConsumer endpoint implementations.
 * @author Ikasan Development Team
 */
public class PayloadBasedSftpConsumerFactory implements EndpointFactory<Consumer<?>,SftpConsumerConfiguration>
{
    /** connection factory */
    private EISConnectionFactory connectionFactory;

    /**
     * Constructor
     * @param connectionFactory
     */
    public PayloadBasedSftpConsumerFactory(EISConnectionFactory connectionFactory)
    {
        this.connectionFactory = connectionFactory;
        if(connectionFactory == null)
        {
            throw new IllegalArgumentException("connectionFactory cannot be 'null'");
        }
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointFactory#createEndpoint(java.lang.Object)
     */
    public Consumer<?> createEndpoint(SftpConsumerConfiguration sftpConsumerConfiguration)
    {
        SFTPConnectionSpec spec = this.getConnectionSpec();
        spec.setClientID(sftpConsumerConfiguration.getClientID());
        spec.setRemoteHostname(sftpConsumerConfiguration.getRemoteHost());
        spec.setKnownHostsFilename(sftpConsumerConfiguration.getKnownHostsFilename());
        spec.setMaxRetryAttempts(sftpConsumerConfiguration.getMaxRetryAttempts());
        spec.setRemotePort(sftpConsumerConfiguration.getRemotePort());
        spec.setPrivateKeyFilename(sftpConsumerConfiguration.getPrivateKeyFilename());
        spec.setConnectionTimeout(sftpConsumerConfiguration.getConnectionTimeout());
        spec.setUsername(sftpConsumerConfiguration.getUsername());
        spec.setCleanupJournalOnComplete(sftpConsumerConfiguration.getCleanupJournalOnComplete());
        
        return new PayloadBasedSftpConsumer(new FileTransferPayloadProvider(sftpConsumerConfiguration.getSourceDirectory(), 
                sftpConsumerConfiguration.getFilenamePattern(), connectionFactory, spec), sftpConsumerConfiguration);
    }

    /**
     * Utility method to aid testing of this class
     * @return
     */
    protected SFTPConnectionSpec getConnectionSpec()
    {
        return new SFTPConnectionSpec();
    }
    
}
