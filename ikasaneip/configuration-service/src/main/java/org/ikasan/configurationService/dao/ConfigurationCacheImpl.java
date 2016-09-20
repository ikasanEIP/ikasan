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
package org.ikasan.configurationService.dao;

import java.util.List;
import java.util.HashMap;

import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.Configuration;

/**
 * Implementation of the ConfigurationDao interface providing
 * the cache persistence for configuration instances.
 * 
 * @author Ikasan Development Team
 */
public class ConfigurationCacheImpl 
    implements ConfigurationDao<List<ConfigurationParameter>>
{
    private HashMap<String,Configuration> configurations = new HashMap<String,Configuration>();
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.dao.ConfigurationDao#findConfiguration(java.lang.String)
     */
    public Configuration findByConfigurationId(String configurationId)
    {
        return this.configurations.get(configurationId);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.dao.ConfigurationDao#saveConfiguration(org.ikasan.framework.configuration.window.Configuration)
     */
    public void save(Configuration<List<ConfigurationParameter>> configuration)
    {
        this.configurations.put(configuration.getConfigurationId(), configuration);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.configuration.dao.ConfigurationDao#deleteConfiguration(org.ikasan.framework.configuration.window.Configuration)
     */
    public void delete(Configuration<List<ConfigurationParameter>> configuration)
    {
        this.configurations.remove(configuration.getConfigurationId());
    }
}
