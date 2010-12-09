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
package org.ikasan.spec.configuration.service;

import org.ikasan.spec.configuration.ConfiguredResource;

/**
 * ConfigurationService defines the operational contract of any configuration
 * service in Ikasan.
 * 
 * @author Ikasan Development Team
 * 
 * @param <RESOURCE> Resource being configured
 * @param <MODEL> Configuration object
 */
public interface ConfigurationService<RESOURCE,MODEL>
{
    /**
     * Configure the given resource.
     * @param configuredResource resource to configure
     */
    public void configure(ConfiguredResource<RESOURCE> configuredResource);

    /**
     * Create a configuration instance for the given configured resource.
     * @param configuredResource resource to configure
     * @return configuration object for that resource
     */
    public MODEL getConfiguration(ConfiguredResource<RESOURCE> configuredResource);

    /**
     * Create a configuration instance for the given configured resource.
     * @param configuredResource resource to configure
     * @return configuration object
     */
    public MODEL createConfiguration(ConfiguredResource<RESOURCE>  configuredResource);

    /**
     * Save the given configuration.
     * @param configuration configuration to add
     */
    public void saveConfiguration(final MODEL configuration);

    /**
     * Delete the given configuration.
     * @param configuration configuration to remove
     */
    public void deleteConfiguration(MODEL configuration);
}
