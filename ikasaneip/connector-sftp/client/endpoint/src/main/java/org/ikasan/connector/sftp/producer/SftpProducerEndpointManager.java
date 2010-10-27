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
package org.ikasan.connector.sftp.producer;

import javax.resource.ResourceException;

import org.ikasan.spec.endpoint.EndpointManager;
import org.ikasan.spec.endpoint.EndpointActivator;
import org.ikasan.spec.endpoint.Producer;
import org.ikasan.spec.endpoint.ProducerFactory;

/**
 * Endpoint manager for SFTP producer endpoint implementations based on an 
 * Sftp protocol and configuration.
 * @author Ikasan Development Team
 */
public class SftpProducerEndpointManager implements EndpointManager<Producer<?>,SftpProducerConfiguration>
{
    /** producer factory */
    @SuppressWarnings("rawtypes")
    private ProducerFactory producerFactory;
    
    /** configuration */
    private SftpProducerConfiguration sftpConfiguration;
    
    /** producer endpoint */
    private Producer<?> producer;

    /**
     * Constructor
     * @param producerFactory
     * @param sftpConfiguration
     */
    public SftpProducerEndpointManager(@SuppressWarnings("rawtypes") ProducerFactory producerFactory, SftpProducerConfiguration sftpConfiguration)
    {
        this.producerFactory = producerFactory;
        if(producerFactory == null)
        {
            throw new IllegalArgumentException("producerFactory cannot be 'null'");
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
    @SuppressWarnings("unchecked")
    public void start() throws ResourceException
    {
        this.producer = this.producerFactory.createProducer(sftpConfiguration);
        if(this.producer instanceof EndpointActivator)
        {
            ((EndpointActivator) this.producer).activate();
        }
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
    public void setConfiguration(SftpProducerConfiguration sftpConfiguration)
    {
        this.sftpConfiguration = sftpConfiguration;
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#getConfiguration()
     */
    public SftpProducerConfiguration getConfiguration()
    {
        return this.sftpConfiguration;
    }

}
