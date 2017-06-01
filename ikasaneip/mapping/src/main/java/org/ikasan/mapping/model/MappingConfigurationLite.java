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

import javax.persistence.Id;

/**
 * Model for representing the traded instrument.
 * 
 * @author Ikasan Development Team
 *
 */
public class MappingConfigurationLite implements Serializable
{
    /** Auto generated serial id */
    private static final long serialVersionUID = 2490203288817051966L;

    protected Long id;
    protected ConfigurationContext sourceContext;
    protected ConfigurationContext targetContext;
    protected String description = "";
    protected int numberOfParams = 1;
    protected ConfigurationType configurationType;
    protected ConfigurationServiceClient configurationServiceClient;
	protected String lastUpdatedBy = "";
	protected int numberOfMappings = 0;


    /** The data time stamp when an instance was first created */
    private Date createdDateTime;

    /** The data time stamp when an instance was last updated */
    private Date updatedDateTime;

    /**
     * Default constructor
     */
    public MappingConfigurationLite()
    {
        long now = System.currentTimeMillis();
        this.createdDateTime = new Date(now);
        this.updatedDateTime = new Date(now);
        this.configurationServiceClient = new ConfigurationServiceClient();
        this.sourceContext = new ConfigurationContext();
        this.targetContext = new ConfigurationContext();
    }

    /**
     * It is a Hibernate requirement that all properties of a window object have getter and setter methods. However, the value of
     * an {@link Id} is part of its primary key and must me immutable. Hence, setter method is private to prevent 
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
    public int getNumberOfParams()
    {
        return numberOfParams;
    }

    /**
     * @param numberOfParams the numberOfParams to set
     */
    public void setNumberOfParams(int numberOfParams)
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

	public String getLastUpdatedBy()
	{
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy)
	{
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public int getNumberOfMappings()
	{
		return numberOfMappings;
	}

	public void setNumberOfMappings(int numberOfMappings)
	{
		this.numberOfMappings = numberOfMappings;
	}

	@Override
	public String toString() {
		return "MappingConfiguration [id=" + id + ", sourceContext="
				+ sourceContext + ", targetContext=" + targetContext
				+ ", description=" + description + ", numberOfParams="
				+ numberOfParams + ", configurationType=" + configurationType
				+ ", configurationServiceClient=" + configurationServiceClient
				+ ", createdDateTime=" + createdDateTime + ", updatedDateTime="
				+ updatedDateTime + "]";
	}
	
	/**
	 * Light version of string representation
	 * @return
	 */
	public String toStringLite() {
		return "MappingConfiguration Id =" + id + " " + configurationServiceClient.getName()
				+ " " + configurationType.getName() + " " + sourceContext.getName()  + " " + targetContext.getName(); 
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MappingConfigurationLite that = (MappingConfigurationLite) o;

		if (numberOfParams != that.numberOfParams) return false;
		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (sourceContext != null ? !sourceContext.equals(that.sourceContext) : that.sourceContext != null)
			return false;
		if (targetContext != null ? !targetContext.equals(that.targetContext) : that.targetContext != null)
			return false;
		if (description != null ? !description.equals(that.description) : that.description != null) return false;
		if (configurationType != null ? !configurationType.equals(that.configurationType) : that.configurationType != null)
			return false;
		if (configurationServiceClient != null ? !configurationServiceClient.equals(that.configurationServiceClient) : that.configurationServiceClient != null)
			return false;
		if (createdDateTime != null ? !createdDateTime.equals(that.createdDateTime) : that.createdDateTime != null)
			return false;
		return updatedDateTime != null ? updatedDateTime.equals(that.updatedDateTime) : that.updatedDateTime == null;

	}

	@Override
	public int hashCode()
	{
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (sourceContext != null ? sourceContext.hashCode() : 0);
		result = 31 * result + (targetContext != null ? targetContext.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + numberOfParams;
		result = 31 * result + (configurationType != null ? configurationType.hashCode() : 0);
		result = 31 * result + (configurationServiceClient != null ? configurationServiceClient.hashCode() : 0);
		result = 31 * result + (createdDateTime != null ? createdDateTime.hashCode() : 0);
		result = 31 * result + (updatedDateTime != null ? updatedDateTime.hashCode() : 0);
		return result;
	}
}
