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
package org.ikasan.framework.configuration.model;

import java.io.Serializable;

/**
 * Individual configuration parameter.
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("serial")
public class ConfigurationParameter implements Serializable
{
    /** configuration name */
    protected String name;
    
    /** configuration value */
    protected String value;
    
    /** configuration description */
    protected String description;
    
    /**
     * Constructor
     * @param name
     * @param value
     */
    public ConfigurationParameter(String name, String value)
    {
        this(name, value, null);
    }

    /**
     * Constructor
     * @param name
     * @param value
     * @param description
     */
    public ConfigurationParameter(String name, String value, String description)
    {
        this.name = name;
        if(name == null)
        {
            throw new IllegalArgumentException("name cannot be 'null'");
        }
        
        this.value = value;
        this.description = description;
    }

    /**
     * Constructor
     */
    protected ConfigurationParameter()
    {
        // required by ORM
    }

    public String getName()
    {
        return name;
    }

    protected void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
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
        if(object == null || !(object instanceof ConfigurationParameter))
        {
            return false;
        }

        // is same object type
        ConfigurationParameter configurationParameter = (ConfigurationParameter) object;
        if( this.name.equals(configurationParameter.getName()) && 
            equalsOrNull(this.value, configurationParameter.getValue()) &&
            equalsOrNull(this.description, configurationParameter.getDescription()) )
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
        hash = hash * 31 + this.name.hashCode();
        hash = hash * 31 + (this.value == null ? 0 : this.value.hashCode());
        hash = hash * 31 + (this.description == null ? 0 : this.description.hashCode());
        return hash;
    }
    
}
