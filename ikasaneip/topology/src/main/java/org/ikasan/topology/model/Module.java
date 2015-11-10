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
import java.util.Set;

/**
 * Implementation of <code>GrantedAuthority</code> adding a description field
 * and identity field suitable for ORM 
 * 
 * @author Ikasan Development Team
 *
 */
public class Module
{

    private Long id;
    private String name; 
    private String description;
    private String contextRoot;
    private String diagramUrl;
    private String version;
    private Server server;
    private Set<Flow> flows;

    /** The data time stamp when an instance was first created */
    private Date createdDateTime;

    /** The data time stamp when an instance was last updated */
    private Date updatedDateTime;

    /**
	 * Default constructor for Hibernate
	 */
    protected Module() {}

	/**
	 * Constructor 
	 * 
	 * @param name
	 * @param description
	 * @param server
	 */
	public Module(String name, String contextRoot, String description, String version, Server server, String diagramUrl)
	{
		super();
		this.name = name;
		this.contextRoot = contextRoot;
		this.description = description;
		this.version = version;
		this.server = server;
		this.diagramUrl = diagramUrl;
		
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
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
	 * @return the flows
	 */
	public Set<Flow> getFlows()
	{
		return flows;
	}



	/**
	 * @param flows the flows to set
	 */
	public void setFlows(Set<Flow> flows)
	{
		this.flows = flows;
	}



	/**
	 * @return the server
	 */
	public Server getServer()
	{
		return server;
	}



	/**
	 * @param server the server to set
	 */
	public void setServer(Server server)
	{
		this.server = server;
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
	 * @return the diagramUrl
	 */
	public String getDiagramUrl()
	{
		return diagramUrl;
	}

	/**
	 * @param diagramUrl the diagramUrl to set
	 */
	public void setDiagramUrl(String diagramUrl)
	{
		this.diagramUrl = diagramUrl;
	}

	/**
	 * @return the contextRoot
	 */
	public String getContextRoot()
	{
		return contextRoot;
	}

	/**
	 * @param contextRoot the contextRoot to set
	 */
	public void setContextRoot(String contextRoot)
	{
		this.contextRoot = contextRoot;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Module [id=" + id + ", name=" + name + ", description="
				+ description + ", contextRoot=" + contextRoot
				+ ", diagramUrl=" + diagramUrl + ", version=" + version
				+ ", server=" + server.getName() + ", flows=" + flows
				+ ", createdDateTime=" + createdDateTime + ", updatedDateTime="
				+ updatedDateTime + "]";
	}
    
}
