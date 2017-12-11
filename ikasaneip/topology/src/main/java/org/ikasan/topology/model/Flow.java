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
package org.ikasan.topology.model;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 
 * @author Ikasan Development Team
 */
public class Flow
{
	private Long id;
    private String name;
    private String description;
    private String state;
    private Module module;
    private Set<Component> components = new HashSet<Component>();
    private int order;
    private boolean configurable = false;
    private String configurationId;

	/** The data time stamp when an instance was first created */
    private Date createdDateTime;

    /** The data time stamp when an instance was last updated */
    private Date updatedDateTime;

    /**
	 * Default constructor for Hibernate
	 */
    protected Flow(){}

    /**
	 * @param name
	 * @param description
	 * @param module
	 */
	public Flow(String name, String description, Module module)
	{
		super();
		this.name = name;
		this.description = description;
		this.module = module;
		
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
    public void setId(Long id)
    {
        this.id = id;
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
	 * @return the components
	 */
	public Set<Component> getComponents()
	{
		return components;
	}

	/**
	 * @param components the components to set
	 */
	public void setComponents(Set<Component> components)
	{
		this.components = components;
	}

	/**
	 * @param components the components to set
	 */
	public void addComponent(Component component)
	{
		if(this.components==null)
		{
			this.components = new LinkedHashSet<>();
		}
		components.add(component);

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
	 * @return the module
	 */
	public Module getModule()
	{
		return module;
	}

	/**
	 * @param module the module to set
	 */
	public void setModule(Module module)
	{
		this.module = module;
	}

	/**
	 * @return the state
	 */
	public String getState()
	{
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state)
	{
		this.state = state;
	}
	
	/**
	 * @return the order
	 */
	public int getOrder()
	{
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(int order)
	{
		this.order = order;
	}

	/**
	 * @return the isConfigurable
	 */
	public boolean isConfigurable() 
	{
		return configurable;
	}

	/**
	 * @param isConfigurable the isConfigurable to set
	 */
	public void setConfigurable(boolean isConfigurable) 
	{
		this.configurable = isConfigurable;
	}
	
	/**
	 * @return the configurationId
	 */
	public String getConfigurationId() 
	{
		return configurationId;
	}

	/**
	 * @param configurationId the configurationId to set
	 */
	public void setConfigurationId(String configurationId) 
	{
		this.configurationId = configurationId;
	}

    @Override public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Flow flow = (Flow) o;
        if (id != null ? !id.equals(flow.id) : flow.id != null)
            return false;
        return name != null ? name.equals(flow.name) : flow.name == null;
    }

    @Override public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		String returnString = "Flow [id=" + id + ", name=" + name + ", description="
				+ description + ", state=" + state;
		
		if(this.module != null)
		{
			returnString += ", module=" + module.getId();
		}
		else
		{
			returnString += ", module=NULL";
		}
		
		returnString += ", components=" + components + ", order=" + order
				+ ", createdDateTime=" + createdDateTime + ", updatedDateTime="
				+ updatedDateTime + "]";
		
		return returnString;
	}
}
