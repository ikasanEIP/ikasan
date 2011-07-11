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

import javax.resource.ResourceException;

import org.ikasan.spec.endpoint.Consumer;
import org.ikasan.spec.endpoint.DefaultEndpointManager;
import org.ikasan.spec.endpoint.EndpointActivator;
import org.ikasan.spec.endpoint.EndpointFactory;
import org.ikasan.spec.endpoint.EndpointManager;

/**
 * Implementation of {@link EndpointManager} for consuming files via FTP connection
 * 
 * @author Ikasan Development Team
 * @deprecated Use the general implementation {@link DefaultEndpointManager}
 *
 */
@Deprecated
public class FtpConsumerEndpointManager implements EndpointManager<Consumer<?>, FtpConsumerConfiguration>
{
    private FtpConsumerConfiguration configuration;
    private Consumer<?> consumer;
    private final EndpointFactory<Consumer<?>, FtpConsumerConfiguration> endpointFactory;

    /**
     * Constructor
     * @param factory {@link EndpointFactory} implementation for creating the endpoint
     * @param configuration Runtime configuration of endpoint
     */
    public FtpConsumerEndpointManager(final EndpointFactory<Consumer<?>, FtpConsumerConfiguration> factory, final FtpConsumerConfiguration configuration)
    {
        this.endpointFactory = factory;
        if (this.endpointFactory == null)
        {
            throw new IllegalArgumentException("EndpointFactory cannot be null.");
        }
        
        this.configuration = configuration;
        if (this.configuration == null)
        {
            throw new IllegalArgumentException("configuration cannot be null.");
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#setConfiguration(java.lang.Object)
     */
    public void setConfiguration(FtpConsumerConfiguration configuration)
    {
        this.configuration = configuration;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#getConfiguration()
     */
    public FtpConsumerConfiguration getConfiguration()
    {
        return this.configuration;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#getEndpoint()
     */
    public Consumer<?> getEndpoint()
    {
        return this.consumer;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#start()
     */
    public void start() throws ResourceException
    {
        this.configuration.validate();
        this.consumer = this.endpointFactory.createEndpoint(this.configuration);
        if (this.consumer instanceof EndpointActivator)
        {
            ((EndpointActivator) this.consumer).activate();
        }
        // TODO what if factory returned null?
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#stop()
     */
    public void stop() throws ResourceException
    {
        try
        {
            if (this.consumer instanceof EndpointActivator)
            {
                ((EndpointActivator) this.consumer).deactivate();
            }
        }
        finally
        {
            this.consumer = null;
        }
    }

}
