/*
 * $Id: TargetConfigurationValue.java 31896 2013-08-02 15:41:00Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/model/TargetConfigurationValue.java $
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

/**
 * Model for representing the traded instrument.
 * 
 * @author CMI2 Development Team
 *
 */
public class TargetConfigurationValue implements Serializable
{
    private static final long serialVersionUID = -1264606304216999864L;

    private Long id;

    private String targetSystemValue;

    /** The data time stamp when an instance was first created */
    private Date createdDateTime;

    /** The data time stamp when an instance was last updated */
    private Date updatedDateTime;

    /**
     * Default constructor
     */
    public TargetConfigurationValue()
    {
        long now = System.currentTimeMillis();
        this.createdDateTime = new Date(now);
        this.updatedDateTime = new Date(now);
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
     * @return the targetSystemValue
     */
    public String getTargetSystemValue()
    {
        return targetSystemValue;
    }

    /**
     * @param targetSystemValue the targetSystemValue to set
     */
    public void setTargetSystemValue(String targetSystemValue)
    {
        this.targetSystemValue = targetSystemValue;
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "TargetConfigurationValue [id=" + id + ", targetSystemValue=" + targetSystemValue + ", createdDateTime="
                + createdDateTime + ", updatedDateTime=" + updatedDateTime + "]";
    }

}
