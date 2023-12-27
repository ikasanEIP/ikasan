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

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * Long based configuration parameter.
 * 
 * @author Ikasan Development Team
 *
 */
@Entity(name = "ConfParamLong")
@PrimaryKeyJoinColumn(name = "Id")
public class ConfigurationParameterLongImpl extends AbstractComponentParameter<Long> implements Serializable
{
    /** configuration name */
    @Column(name="Name", nullable = false)
    protected String name;

    /** configuration description */
    @Column(name="Description")
    protected String description;

    @Column(name="Value")
    protected Long value;

    /**
     * Constructor
     * @param name
     * @param value
     */
    public ConfigurationParameterLongImpl(String name, Long value)
    {
        this(name, value, null);
    }

    /**
     * Constructor
     * @param name
     * @param value
     * @param description
     */
    public ConfigurationParameterLongImpl(String name, Long value, String description)
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
    protected ConfigurationParameterLongImpl()
    {
        // required by ORM
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public void setValue(Long value) {
        this.value = value;
    }

    /**
     * Utility method for object comparison
     * @param object1
     * @param object2
     * @return
     */
    protected boolean equalsOrNull(Object object1, Object object2)
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
        if(object == null || !(object instanceof ConfigurationParameterLongImpl))
        {
            return false;
        }

        // is same object type
        ConfigurationParameterLongImpl configurationParameter = (ConfigurationParameterLongImpl) object;
        if( this.name.equals(configurationParameter.getName()) &&
            equalsOrNull(this.getValue(), configurationParameter.getValue()) &&
            equalsOrNull(this.description, configurationParameter.getDescription()) )
        {
            return true;
        }

        // nothing equal
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
        hash = hash * 31 + (this.getValue() == null ? 0 : this.getValue().hashCode());
        hash = hash * 31 + (this.description == null ? 0 : this.description.hashCode());
        return hash;
    }
}
