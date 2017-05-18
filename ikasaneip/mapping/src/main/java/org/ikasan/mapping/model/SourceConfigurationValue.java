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
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class SourceConfigurationValue implements Serializable, Comparable<SourceConfigurationValue>
{
    private static final long serialVersionUID = 7464033893694959176L;

    private Long id;

    private String sourceSystemValue;

    private String name = "";

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
     * It is a Hibernate requirement that all properties of a window object have getter and setter methods. However, the value of
     * an Id is part of its primary key and must me immutable. Hence, setter method is private to prevent
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
        if(sourceSystemValue != null)
        {
            this.sourceSystemValue = sourceSystemValue.trim();
        }
        else
        {
            this.sourceSystemValue = sourceSystemValue;
        }
    }

    /**
     * @return the configurationContextId
     */
    public Long getMappingConfigurationId()
    {
        return mappingConfigurationId;
    }

    /**
     * @param mappingConfigurationId the mappingConfigurationId to set
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
     *
     * @return the name
     */
    public String getName()
    {
        if(name == null)
        {
            return "";
        }

        return name;
    }

    /**
     *
     * @param name the name to set
     */
    public void setName(String name)
    {
        if(name != null)
        {
            this.name = name.trim();
        }
        else
        {
            this.name = name;
        }
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

    @Override
    public int compareTo(SourceConfigurationValue value)
    {
        int result = this.sourceSystemValue.compareTo(value.getSourceSystemValue());

        if(value.getName() != null && this.getName() != null)
        {
            result += this.name.compareTo(value.getName());
        }

        return result;
    }
}
