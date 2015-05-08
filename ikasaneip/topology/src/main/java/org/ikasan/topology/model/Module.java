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
    private Server server;
    private Set<Flow> flows;

    /**
     * No args constructor required by ORM
     *
    private Module(){}



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
    
    
}
