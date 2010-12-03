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
package org.ikasan.spec.endpoint;

import javax.resource.ResourceException;

import org.ikasan.spec.endpoint.EndpointFactory;
import org.ikasan.spec.endpoint.EndpointManager;
import org.ikasan.spec.endpoint.EndpointActivator;

/**
 * Default endpoint manager for general endpoint implementations.
 * @author Ikasan Development Team
 */
public class DefaultEndpointManager<E,C> implements EndpointManager<E,C>
{
    /** endpoint factory */
    protected EndpointFactory<E,C> endpointFactory;
    
    /** configuration */
    protected C configuration;

    /** endpoint */
    protected E endpoint;
    
    /**
     * Constructor
     * @param endpointFactory - endpoint factory
     * @param configuration - default configuration
     */
    public DefaultEndpointManager(EndpointFactory<E,C> endpointFactory, C configuration)
    {
        this.endpointFactory = endpointFactory;
        if(endpointFactory == null)
        {
            throw new IllegalArgumentException("endpointFactory cannot be 'null'");
        }

        this.configuration = configuration;
        if(configuration == null)
        {
            throw new IllegalArgumentException("configuration cannot be 'null'");
        }
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#start()
     */
    public void start() throws ResourceException
    {
        this.endpoint = this.endpointFactory.createEndpoint(configuration);
        if(this.endpoint instanceof EndpointActivator)
        {
            ((EndpointActivator) this.endpoint).activate();
        }
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#getEndpoint()
     */
    public E getEndpoint()
    {
        return this.endpoint;
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#stop()
     */
    public void stop() throws ResourceException
    {
        if(this.endpoint != null && this.endpoint instanceof EndpointActivator)
        {
            try
            {
                ((EndpointActivator)endpoint).deactivate();
            }
            finally
            {
                this.endpoint = null;
            }
        }
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#setConfiguration(java.lang.Object)
     */
    public void setConfiguration(C configuration)
    {
        this.configuration = configuration;
    }

    /* (non-Jsdoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#getConfiguration()
     */
    public C getConfiguration()
    {
        return this.configuration;
    }
}
