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
package org.ikasan.setup.persistence.service;

import org.ikasan.setup.persistence.dao.ProviderDAO;

import java.util.Map;
import java.util.Set;

/**
 * PersistenceServiceFactory implementation
 *
 * @auther Ikasan Development Team
 */
public class PersistenceServiceFactoryImpl implements PersistenceServiceFactory<String>
{
    /**
     * Providers and their associated DAO
     */
    private Map<String,ProviderDAO> providerDAOs;

    /**
     * Constructor
     * @param providerDAOs
     */
    public PersistenceServiceFactoryImpl(Map<String, ProviderDAO> providerDAOs)
    {
        this.providerDAOs = providerDAOs;
        if(providerDAOs == null)
        {
            throw new IllegalArgumentException("providerDAOs cannot be 'null");
        }
    }

    /**
     * Return a set of current providers.
     * @return
     */
    public Set<String> getProviders()
    {
        return providerDAOs.keySet();
    }

    /**
     * Create a persiwtence service instance base don the given provider
     * @param provider
     * @return
     * @throws UnsupportedProviderException
     */
    public PersistenceService getPersistenceService(String provider) throws UnsupportedProviderException
    {
        ProviderDAO providerDAO = providerDAOs.get(provider);
        if(providerDAO == null)
        {
            throw new UnsupportedProviderException(provider + " is not currently supported!");
        }

        return new PersistenceServiceImpl(providerDAO);
    }
}
