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
package org.ikasan.topology.dao;

import java.util.List;

import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;


/**
 * Data Access interface for <code>User</code> instances
 * 
 * @author Ikasan Development Team
 *
 */
public interface TopologyDao
{
	/**
	 * Get all servers
	 * 
	 * @return
	 */
	public List<Server> getAllServers();

	/**
	 * Save a server. Will create a new record or update an existing.
	 * @param server
	 */
	public void save(Server server);

	/**
	 * Get all modules
	 * 
	 * @return
	 */
	public List<Module> getAllModules();

	/**
	 * Save a module. Will create a new record or update an existing.
	 * 
	 * @param module
	 */
	public void save(Module module);

	/**
	 * Get all flows.
	 * 
	 * @return
	 */
	public List<Flow> getAllFlows();

	/**
	 * Save a flow. Will create a new record or update an existing.
	 * 
	 * @param flow
	 */
	public void save(Flow flow);

	/**
	 * Get all BusinessStreams
	 * 
	 * @return
	 */
	public List<BusinessStream> getAllBusinessStreams();
	
	/**
	 * Save a BusinessStream. Will create a new record or update an existing.
	 * 
	 * @param businessStream
	 */
	public void saveBusinessStream(BusinessStream businessStream);

	/**
	 * Get all BusinessStreams associated with a given user.
	 * @param userId
	 * @return
	 */
	public List<BusinessStream> getBusinessStreamsByUserId(Long userId);
}
