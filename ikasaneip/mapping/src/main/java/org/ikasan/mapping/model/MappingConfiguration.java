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
package org.ikasan.mapping.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Model for representing the traded instrument.
 * 
 * @author Ikasan Development Team
 *
 */
public class MappingConfiguration implements Serializable
{
    /** Auto generated serial id */
    private static final long serialVersionUID = 2490203288817051966L;

    protected Long id;

    protected ConfigurationContext sourceContext;

    protected ConfigurationContext targetContext;

    protected String description = "";

    protected Long numberOfParams = new Long(1);

    protected ConfigurationType configurationType;

    protected ConfigurationServiceClient configurationServiceClient;

    protected Set<SourceConfigurationValue> sourceConfigurationValues;

	protected boolean isManyToMany = false;

    /** The data time stamp when an instance was first created */
    private Date createdDateTime;

    /** The data time stamp when an instance was last updated */
    private Date updatedDateTime;

    /**
     * Default constructor
     */
    public MappingConfiguration()
    {
        long now = System.currentTimeMillis();
        this.createdDateTime = new Date(now);
        this.updatedDateTime = new Date(now);
        this.sourceConfigurationValues = new HashSet<SourceConfigurationValue>();
        this.configurationServiceClient = new ConfigurationServiceClient();
        this.sourceContext = new ConfigurationContext();
        this.targetContext = new ConfigurationContext();
    }

    /**
     * It is a Hibernate requirement that all properties of a window object have getter and setter methods. However, the value of
     * an Id is part of its primary key and must me immutable. Hence, setter method is private to prevent
     * client code from changing the value.
     * 
     * @param id to set
     */
    @SuppressWarnings("unused")
    private void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Instrument immutable id
     * @return id
     */
    public Long getId()
    {
        return this.id;
    }

    /**
     * @return the numberOfParams
     */
    public Long getNumberOfParams()
    {
        return numberOfParams;
    }

    /**
     * @param numberOfParams the numberOfParams to set
     */
    public void setNumberOfParams(Long numberOfParams)
    {
        this.numberOfParams = numberOfParams;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return the createdDateTime
     */
    public Date getCreatedDateTime()
    {
        return createdDateTime;
    }

    /**
     * @param createdDateTime the createdDateTime to set
     */
    @SuppressWarnings("unused")
    private void setCreatedDateTime(Date createdDateTime)
    {
        this.createdDateTime = createdDateTime;
    }

    /**
     * @return the updatedDateTime
     */
    public Date getUpdatedDateTime()
    {
        return updatedDateTime;
    }

    /**
     * @param updatedDateTime the updatedDateTime to set
     */
    public void setUpdatedDateTime(Date updatedDateTime)
    {
        this.updatedDateTime = updatedDateTime;
    }

	public ConfigurationContext getSourceContext() {
		return sourceContext;
	}

	public void setSourceContext(ConfigurationContext sourceContext) 
	{
		this.sourceContext = sourceContext;
	}

	public ConfigurationContext getTargetContext() {
		return targetContext;
	}

	public void setTargetContext(ConfigurationContext targetContext) 
	{
		this.targetContext = targetContext;
	}

	public ConfigurationType getConfigurationType() 
	{
		return configurationType;
	}

	public void setConfigurationType(ConfigurationType configurationType) 
	{
		this.configurationType = configurationType;
	}

	public ConfigurationServiceClient getConfigurationServiceClient() 
	{
		return configurationServiceClient;
	}

	public void setConfigurationServiceClient(
			ConfigurationServiceClient configurationServiceClient) 
	{
		this.configurationServiceClient = configurationServiceClient;
	}

	public Set<SourceConfigurationValue> getSourceConfigurationValues() 
	{
		return sourceConfigurationValues;
	}

	public void setSourceConfigurationValues(
			Set<SourceConfigurationValue> sourceConfigurationValues) 
	{
		this.sourceConfigurationValues = sourceConfigurationValues;
	}

	public boolean getIsManyToMany()
	{
		return isManyToMany;
	}

	public void setIsManyToMany(boolean manyToMany)
	{
		isManyToMany = manyToMany;
	}

	@Override
	public String toString() {
		return "MappingConfiguration [id=" + id + ", sourceContext="
				+ sourceContext + ", targetContext=" + targetContext
				+ ", description=" + description + ", numberOfParams="
				+ numberOfParams + ", configurationType=" + configurationType
				+ ", configurationServiceClient=" + configurationServiceClient
				+ ", sourceConfigurationValues=" + sourceConfigurationValues
				+ ", createdDateTime=" + createdDateTime + ", updatedDateTime="
				+ updatedDateTime + "]";
	}
	
	/**
	 * Light version of string representation
	 * 
	 * @return
	 */
	public String toStringLite() {
		return "MappingConfiguration Id =" + id + " " + configurationServiceClient.getName()
				+ " " + configurationType.getName() + " " + sourceContext.getName()  + " " + targetContext.getName(); 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((configurationServiceClient == null) ? 0
						: configurationServiceClient.hashCode());
		result = prime
				* result
				+ ((configurationType == null) ? 0 : configurationType
						.hashCode());
		result = prime * result
				+ ((createdDateTime == null) ? 0 : createdDateTime.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((numberOfParams == null) ? 0 : numberOfParams.hashCode());
		result = prime
				* result
				+ ((sourceConfigurationValues == null) ? 0
						: sourceConfigurationValues.hashCode());
		result = prime * result
				+ ((sourceContext == null) ? 0 : sourceContext.hashCode());
		result = prime * result
				+ ((targetContext == null) ? 0 : targetContext.hashCode());
		result = prime * result
				+ ((updatedDateTime == null) ? 0 : updatedDateTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MappingConfiguration other = (MappingConfiguration) obj;
		if (configurationServiceClient == null) {
			if (other.configurationServiceClient != null)
				return false;
		} else if (!configurationServiceClient
				.equals(other.configurationServiceClient))
			return false;
		if (configurationType == null) {
			if (other.configurationType != null)
				return false;
		} else if (!configurationType.equals(other.configurationType))
			return false;
		if (createdDateTime == null) {
			if (other.createdDateTime != null)
				return false;
		} else if (!createdDateTime.equals(other.createdDateTime))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (numberOfParams == null) {
			if (other.numberOfParams != null)
				return false;
		} else if (!numberOfParams.equals(other.numberOfParams))
			return false;
		if (sourceConfigurationValues == null) {
			if (other.sourceConfigurationValues != null)
				return false;
		} else if (!sourceConfigurationValues
				.equals(other.sourceConfigurationValues))
			return false;
		if (sourceContext == null) {
			if (other.sourceContext != null)
				return false;
		} else if (!sourceContext.equals(other.sourceContext))
			return false;
		if (targetContext == null) {
			if (other.targetContext != null)
				return false;
		} else if (!targetContext.equals(other.targetContext))
			return false;
		if (updatedDateTime == null) {
			if (other.updatedDateTime != null)
				return false;
		} else if (!updatedDateTime.equals(other.updatedDateTime))
			return false;
		return true;
	}    
}
