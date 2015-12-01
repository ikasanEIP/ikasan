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
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Filter;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.RoleFilter;
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
	 * Get all flows by server id and module id.
	 * 
	 * @return
	 */
	public List<Flow> getFlowsByServerIdAndModuleId(Long serverId, Long moduleId);

	/**
	 * Get a flow by server id, module id and flow name.
	 * 
	 * @return
	 */
	public Flow getFlowByServerIdModuleIdAndFlowname(Long serverId, Long moduleId, String flowName);
	
	/**
	 * Get a components by server id, module id and flow name and component name not in.
	 * 
	 * @return
	 */
	public List<Component> getComponentsByServerIdModuleIdAndFlownameAndComponentNameNotIn(Long serverId, Long moduleId, String flowName, List<String> componentNames);
	
	/**
	 * Get a orphaned flow by server id, module id and flow names.
	 * 
	 * @return
	 */
	public List<Flow> getFlowsByServerIdModuleIdAndNotInFlownames(Long serverId, Long moduleId, List<String> flowName);

	
	/**
	 * Save a flow. Will create a new record or update an existing.
	 * 
	 * @param flow
	 */
	public void save(Flow flow);
	
	/**
	 * Delete a flow.
	 * 
	 * @param flow
	 */
	public void delete(Flow flow);
	
	/**
	 * Save a component. Will create a new record or update an existing.
	 * 
	 * @param flow
	 */
	public void save(Component component);

	/**
	 * Delete a component.
	 * 
	 * @param component
	 */
	public void delete(Component component);
	

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
	 * 
	 * @param userId
	 * @return
	 */
	public List<BusinessStream> getBusinessStreamsByUserId(Long userId);
	
	/**
	 * Get all BusinessStreams by a list of ids.
	 * 
	 * @param ids
	 * @return
	 */
	public List<BusinessStream> getBusinessStreamsByUserId(List<Long> ids);

	/**
	 * Delete a business stream flow
	 * 
	 * @param businessStreamFlow
	 */
	public void deleteBusinessStreamFlow(BusinessStreamFlow businessStreamFlow);
	
	/**
	 * Delete a business stream 
	 * 
	 * @param businessStream
	 */
	public void deleteBusinessStream(BusinessStream businessStream);
	
	/**
	 * Method to get a Module by its name. 
	 * @param name
	 * @return
	 */
	public Module getModuleByName(String name);
	
	/**
	 * 
	 * @param filter
	 */
	public void saveFilter(Filter filter);
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Filter getFilterByName(String name);
	
	/**
	 * 
	 * @return
	 */
	public List<Filter> getAllFilters();
	
	/**
	 * 
	 * @param roleFilter
	 */
	public void saveRoleFilter(RoleFilter roleFilter);
	
	/**
	 * 
	 * @param roleId
	 * @return
	 */
	public List<RoleFilter> getRoleFiltersByRoleId(List<Long> roleIds);

	
	/**
	 * 
	 * @param roleId
	 * @return
	 */
	public RoleFilter getRoleFilterByFilterId(Long roleId);
	
	/**
	 * 
	 * @param filter
	 */
	public void deleteFilter(Filter filter);
	
	/**
	 * 
	 * @param filter
	 */
	public void deleteRoleFilter(RoleFilter roleFilter);
	
	/**
	 * 
	 * @param filterId
	 */
	public void deleteFilterComponents(Long filterId);
}
