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
package org.ikasan.topology.service;

import java.util.List;
import java.util.Set;

import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Filter;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Notification;
import org.ikasan.topology.model.RoleFilter;
import org.ikasan.topology.model.Server;
import org.ikasan.topology.exception.DiscoveryException;

/**
 * @author Ikasan Development Team
 */
public interface TopologyService
{
    /**
     * Get all servers
     *
     * @return
     */
    List<Server> getAllServers();

    /**
     * Save a server. Will create a new record or update an existing.
     *
     * @param server
     */
    void save(Server server);

    /**
     * Get all modules
     *
     * @return
     */
    List<Module> getAllModules();

    /**
     * Save a module. Will create a new record or update an existing.
     *
     * @param module
     */
    void save(Module module);

    /**
     * Get all flows.
     *
     * @return
     */
    List<Flow> getAllFlows();

    /**
     * Save a flow. Will create a new record or update an existing.
     *
     * @param flow
     */
    void save(Flow flow);

    /**
     * Save a component. Will create a new record or update an existing.
     *
     * @param component
     */
    void save(Component component);

    /**
     * Get all BusinessStreams
     *
     * @return
     */
    List<BusinessStream> getAllBusinessStreams();

    /**
     * Save a BusinessStream. Will create a new record or update an existing.
     *
     * @param businessStream
     */
    void saveBusinessStream(BusinessStream businessStream);

    /**
     * Get all BusinessStreams associated with a given user.
     *
     * @param userId
     * @return
     */
    List<BusinessStream> getBusinessStreamsByUserId(Long userId);

    /**
     * Get all flows by server id and module id.
     *
     * @return
     */
    List<Flow> getFlowsByServerIdAndModuleId(Long serverId, Long moduleId);

    /**
     * Delete a business stream flow
     *
     * @param businessStreamFlow
     */
    void deleteBusinessStreamFlow(BusinessStreamFlow businessStreamFlow);

    /**
     * Method to get a Module by its name.
     *
     * @param name
     * @return
     */
    Module getModuleByName(String name);

    /**
     * Get all BusinessStreams by a list of ids.
     *
     * @param ids
     * @return
     */
    List<BusinessStream> getBusinessStreamsByUserId(List<Long> ids);

    /**
     * Performs module discovery aka updates given module topology view with new latest flows and its components.
     *
     * @param server server wher given module resides
     * @param module module to be updated
     * @param flows list of flows dto containing high level information about flow
     * @throws DiscoveryException exce
     */
    void discover(Server server, Module module, List<Flow> flows) throws DiscoveryException;

    /**
     * Delete a business stream
     *
     * @param businessStream
     */
    void deleteBusinessStream(BusinessStream businessStream);

    /**
     * Method to discovery and populate Ikasan topology
     */
    void discover(IkasanAuthentication authentication) throws DiscoveryException;

    /**
     * @param name
     * @param description
     * @param flows
     */
    Filter createFilter(String name, String description, String createdBy, Set<Component> components);

    /**
     * @return
     */
    List<Filter> getAllFilters();

    /**
     * @param filter
     */
    void saveFilter(Filter filter);

    /**
     * @param filter
     */
    void deleteFilter(Filter filter);

    /**
     * @param roleFilter
     */
    void saveRoleFilter(RoleFilter roleFilter);

    /**
     * @param roleId
     * @return
     */
    List<RoleFilter> getRoleFilters(List<Long> roleIds);

    /**
     * @param roleId
     * @return
     */
    RoleFilter getRoleFilterByFilterId(Long filterId);

    /**
     * @param roleFilter
     */
    void deleteRoleFilter(RoleFilter roleFilter);

    /**
     * @param filterId
     */
    void deleteFilterComponents(Long filterId);

    /**
     * @param name
     * @return
     */
    Filter getFilterByName(String name);

    /**
     * @param notification
     */
    void save(Notification notification);

    /**
     * @param notification
     */
    void delete(Notification notification);

    /**
     * @param name
     * @return
     */
    Notification getNotificationByName(String name);

    /**
     * @param name
     * @return
     */
    List<Notification> getAllNotifications();
}
