/*
 * $Id: SourceConfigurationValue.java 40152 2014-10-17 15:57:49Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/model/SourceConfigurationValue.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.mapping.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author CMI2 Development Team
 *
 */
public class SourceConfigurationValue implements Serializable
{
    private static final long serialVersionUID = 7464033893694959176L;

    private Long id;

    private String sourceSystemValue;

    private Long mappingConfigurationId;

    private Long sourceConfigGroupId;

    private TargetConfigurationValue targetConfigurationValue;

    /** The data time stamp when an instance was first created */
    private Date createdDateTime;

    /** The data time stamp when an instance was last updated */
    private Date updatedDateTime;

    /**
     * Default constructor
     */
    public SourceConfigurationValue()
    {
        long now = System.currentTimeMillis();
        this.createdDateTime = new Date(now);
        this.updatedDateTime = new Date(now);
    }

    /**
     * @return the id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * It is a Hibernate requirement that all properties of a model object have getter and setter methods. However, the value of
     * an {@link Id} is part of its primary key and must me immutable. Hence, setter method is private to prevent 
     * client code from changing the value.
     */
    @SuppressWarnings("unused")
    private void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @return the sourceSystemValue
     */
    public String getSourceSystemValue()
    {
        return sourceSystemValue;
    }

    /**
     * @param sourceSystemValue the sourceSystemValue to set
     */
    public void setSourceSystemValue(String sourceSystemValue)
    {
        this.sourceSystemValue = sourceSystemValue;
    }

    /**
     * @return the configurationContextId
     */
    public Long getMappingConfigurationId()
    {
        return mappingConfigurationId;
    }

    /**
     * @param configurationContextId the configurationContextId to set
     */
    public void setMappingConfigurationId(Long mappingConfigurationId)
    {
        this.mappingConfigurationId = mappingConfigurationId;
    }

    
    /**
     * 
     * @return the targetConfigurationValue
     */
    public TargetConfigurationValue getTargetConfigurationValue() 
    {
		return targetConfigurationValue;
	}

	/**
	 * 
	 * @param targetConfigurationValue the targetConfigurationValue to set
	 */
    public void setTargetConfigurationValue(
			TargetConfigurationValue targetConfigurationValue) 
	{
		this.targetConfigurationValue = targetConfigurationValue;
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
    public void setCreatedDateTime(Date createdDateTime)
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

	/**
     * @return the sourceConfigGroupId
     */
    public Long getSourceConfigGroupId()
    {
        return sourceConfigGroupId;
    }

    /**
     * @param sourceConfigGroupId the sourceConfigGroupId to set
     */
    public void setSourceConfigGroupId(Long sourceConfigGroupId)
    {
        this.sourceConfigGroupId = sourceConfigGroupId;
    }

    @Override
	public String toString() {
		return "SourceConfigurationValue [id=" + id + ", sourceSystemValue="
				+ sourceSystemValue + ", mappingConfigurationId="
				+ mappingConfigurationId + ", targetConfigurationValue="
				+ targetConfigurationValue + ", createdDateTime="
				+ createdDateTime + ", updatedDateTime=" + updatedDateTime
				+ "]";
	}
}
