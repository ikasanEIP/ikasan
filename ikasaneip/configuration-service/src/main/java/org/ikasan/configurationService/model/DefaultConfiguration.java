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
package org.ikasan.configurationService.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationParameter;

/**
 * Configuration data window supporting the runtime attributes
 * of any resource marked as a ConfiguredResource.
 * 
 * @author Ikasan Development Team
 */
@SuppressWarnings("serial")
public class DefaultConfiguration implements Configuration<List<ConfigurationParameter>>, Serializable
{
    /** runtime configuration identifier */
    protected String configurationId;
    
    /** runtime configuration description */
    protected String description;
    
    /** configuration parameters within this configuration */
    protected List<ConfigurationParameter> parameters;
    
    /**
     * Constructor
     * @param configurationId
     */
    public DefaultConfiguration(String configurationId)
    {
        this(configurationId, null, new ArrayList<ConfigurationParameter>());
    }

    /**
     * Constructor
     * @param configurationId
     * @param parameters
     */
    public DefaultConfiguration(String configurationId, List<ConfigurationParameter> parameters)
    {
        this(configurationId, null, parameters);
    }

    /**
     * Constructor
     * @param configurationId
     * @param description
     * @param parameters
     */
    public DefaultConfiguration(String configurationId, String description, List<ConfigurationParameter> parameters)
    {
        this.configurationId = configurationId;
        if(configurationId == null)
        {
            throw new IllegalArgumentException("configurationId cannot be 'null'");
        }
        
        this.description = description;
        
        this.parameters = parameters;
        if(parameters == null)
        {
            throw new IllegalArgumentException("parameters cannot be 'null'");
        }
    }

    /**
     * Constructor
     */
    protected DefaultConfiguration()
    {
        // required by ORM
    }

    protected void setConfigurationId(String configurationId)
    {
        this.configurationId = configurationId;
    }

    public String getConfigurationId()
    {
        return configurationId;
    }

    public List<ConfigurationParameter> getParameters()
    {
        return parameters;
    }

    public void setParameters(List<ConfigurationParameter> parameters)
    {
        this.parameters = parameters;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object)
    {
        // is same instance
        if(this == object)
        {
            return true;
        }

        // is an instanceof
        if(object == null || !(object instanceof DefaultConfiguration))
        {
            return false;
        }

        // is same object type
        DefaultConfiguration configuration = (DefaultConfiguration) object;
        if(this.configurationId.equals(configuration.getConfigurationId()) &&
           equalsOrNull(this.description, configuration.getDescription()) &&
           this.parameters.size() == configuration.getParameters().size() &&
           this.parameters.containsAll(configuration.getParameters()))
        {
            return true;
        }

        // nothing equal
        return false;
    }

    /**
     * Utility method for object comparison
     * @param object1
     * @param object2
     * @return
     */
    private boolean equalsOrNull(Object object1, Object object2)
    {
        if(object1 != null && object1.equals(object2))
        {
            return true;
        }
        else if(object1 == null && object2 == null)
        {
            return true;
        }
        
        return false;
    }
    
    /**
     * HashCode default implementation
     * 
     * @return int hashcode
     */
    @Override
    public int hashCode()
    {
        int hash = 1;
        hash = hash * 31 + this.configurationId.hashCode();
        hash = hash * 31 + (this.description == null ? 0 : this.description.hashCode());
        for(ConfigurationParameter configurationParameter:this.parameters)
        {
            hash = hash * 31 + (configurationParameter == null ? 0 : configurationParameter.hashCode());
        }
        return hash;
    }
        
}
