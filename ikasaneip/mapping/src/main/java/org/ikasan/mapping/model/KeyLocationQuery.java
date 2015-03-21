/*
 * $Id: KeyLocationQuery.java 31896 2013-08-02 15:41:00Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/model/KeyLocationQuery.java $
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

import java.util.Date;

/**
 * @author CMI2 Development Team
 *
 */
public class KeyLocationQuery
{
    private Long id;

    private String value;

    private Long mappingConfigurationId;

    /** The data time stamp when an instance was first created */
    private Date createdDateTime;

    /** The data time stamp when an instance was last updated */
    private Date updatedDateTime;

    public KeyLocationQuery()
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
     * @param id the id to set
     */
    @SuppressWarnings("unused")
    private void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @return the value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value)
    {
        this.value = value;
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
        return "KeyLocationQuery [id=" + id + ", value=" + value + ", mappingConfigurationId=" + mappingConfigurationId
                + ", createdDateTime=" + createdDateTime + ", updatedDateTime=" + updatedDateTime + "]";
    }
}
