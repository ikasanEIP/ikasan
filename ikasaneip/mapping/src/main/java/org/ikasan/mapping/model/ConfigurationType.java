/*
 * $Id: ConfigurationType.java 40152 2014-10-17 15:57:49Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/model/ConfigurationType.java $
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
public class ConfigurationType
{
    private Long id;

    private String name;

    /** The data time stamp when an instance was first created */
    private Date createdDateTime;

    /** The data time stamp when an instance was last updated */
    private Date updatedDateTime;

    /**
     * Default constructor
     */
    public ConfigurationType()
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
    public void setId(Long id)
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
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
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
        return "ConfigurationType [id=" + id + ", name=" + name + ", createdDateTime=" + createdDateTime
                + ", updatedDateTime=" + updatedDateTime + "]";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((createdDateTime == null) ? 0 : createdDateTime.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((updatedDateTime == null) ? 0 : updatedDateTime.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ConfigurationType other = (ConfigurationType) obj;
        if (createdDateTime == null)
        {
            if (other.createdDateTime != null) return false;
        }
        else if (!createdDateTime.equals(other.createdDateTime)) return false;
        if (id == null)
        {
            if (other.id != null) return false;
        }
        else if (!id.equals(other.id)) return false;
        if (name == null)
        {
            if (other.name != null) return false;
        }
        else if (!name.equals(other.name)) return false;
        if (updatedDateTime == null)
        {
            if (other.updatedDateTime != null) return false;
        }
        else if (!updatedDateTime.equals(other.updatedDateTime)) return false;
        return true;
    }

}
