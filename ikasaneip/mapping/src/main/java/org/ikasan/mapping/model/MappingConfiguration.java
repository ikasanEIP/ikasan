/*
 * $Id: MappingConfiguration.java 40152 2014-10-17 15:57:49Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/model/MappingConfiguration.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2010 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.mapping.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Model for representing the traded instrument.
 * 
 * @author CMI2 Development Team
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
     * It is a Hibernate requirement that all properties of a model object have getter and setter methods. However, the value of
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
