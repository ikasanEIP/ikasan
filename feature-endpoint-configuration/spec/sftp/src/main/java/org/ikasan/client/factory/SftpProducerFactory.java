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
package org.ikasan.client.factory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.resource.cci.ConnectionFactory;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.client.configuration.SftpConfiguration;
import org.ikasan.client.endpoint.Producer;
import org.ikasan.client.endpoint.ProducerFactory;
import org.ikasan.client.endpoint.SftpFileProducer;
import org.ikasan.connector.sftp.outbound.SFTPConnectionSpec;

/**
 * @author Ikasan Development Team
 */
public class SftpProducerFactory implements ProducerFactory<SftpConfiguration>
{
    /** requires connection factory for the wrapped resource */
    private ConnectionFactory connectionFactory;

    /** maintained map of all active producers keyed by their configuration */
    private Map<SftpConfiguration,Producer<?>> producers = new ConcurrentHashMap<SftpConfiguration,Producer<?>>();

    /**
     * Constructor
     * @param connectionFactory
     */
    public SftpProducerFactory(ConnectionFactory connectionFactory)
    {
        this.connectionFactory = connectionFactory;
        if(connectionFactory == null)
        {
            throw new IllegalArgumentException("connectionFactory cannot be 'null'");
        }
    }

    /* (non-Jsdoc)
     * @see org.ikasan.client.endpoint.ProducerFactory#getEndpoint(java.lang.Object)
     */
    public Producer<?> getProducer(SftpConfiguration sftpConfiguration)
    {
        Producer<?> producer = producers.get(sftpConfiguration);
        if(producer == null)
        {
            SFTPConnectionSpec spec = new SFTPConnectionSpec();
            spec.setClientID(sftpConfiguration.getClientID());
            spec.setRemoteHostname(sftpConfiguration.getRemoteHost());
            spec.setKnownHostsFilename(sftpConfiguration.getKnownHostsFilename());
            spec.setMaxRetryAttempts(sftpConfiguration.getMaxRetryAttempts());
            spec.setRemotePort(sftpConfiguration.getRemotePort());
            spec.setPrivateKeyFilename(sftpConfiguration.getPrivateKeyFilename());
            spec.setConnectionTimeout(sftpConfiguration.getConnectionTimeout());
            spec.setUsername(sftpConfiguration.getUsername());
            spec.setCleanupJournalOnComplete(sftpConfiguration.getCleanupJournalOnComplete());

            producer = new SftpFileProducer(new FileTransferConnectionTemplate(connectionFactory, spec), sftpConfiguration);
            this.producers.put(sftpConfiguration, producer);
        }

        return producer;
    }

    /* (non-Jsdoc)
     * @see org.ikasan.client.endpoint.ProducerFactory#destroyProducers()
     */
    public void destroyProducers()
    {
        for (Iterator<Producer<?>> it = producers.values().iterator(); it.hasNext();)
        {
            Producer<?> producer = it.next();
            producer.deactivate();
        }
        
        producers.clear();
    }
}
