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

import org.apache.log4j.Logger;
import org.ikasan.topology.dao.TopologyDao;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class TopologyServiceImpl implements TopologyService
{
	private static Logger logger = Logger.getLogger(TopologyServiceImpl.class);
	
	private TopologyDao topologyDao;

	/**
	 * Constructor
	 * 
	 * @param topologyDao
	 */
	public TopologyServiceImpl(TopologyDao topologyDao)
	{
		this.topologyDao = topologyDao;
		if(this.topologyDao == null)
		{
			throw new IllegalArgumentException("topologyDao cannot be null!");
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getAllServers()
	 */
	@Override
	public List<Server> getAllServers()
	{
		return this.topologyDao.getAllServers();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#save(org.ikasan.topology.model.Server)
	 */
	@Override
	public void save(Server server)
	{
		this.topologyDao.save(server);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getAllModules()
	 */
	@Override
	public List<Module> getAllModules()
	{
		return this.topologyDao.getAllModules();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#save(org.ikasan.topology.model.Module)
	 */
	@Override
	public void save(Module module)
	{
		this.topologyDao.save(module);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getAllFlows()
	 */
	@Override
	public List<Flow> getAllFlows()
	{
		return this.topologyDao.getAllFlows();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#save(org.ikasan.topology.model.Flow)
	 */
	@Override
	public void save(Flow flow)
	{
		this.topologyDao.save(flow);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getAllBusinessStreams()
	 */
	@Override
	public List<BusinessStream> getAllBusinessStreams()
	{
		return this.topologyDao.getAllBusinessStreams();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#saveBusinessStream(org.ikasan.topology.model.BusinessStream)
	 */
	@Override
	public void saveBusinessStream(BusinessStream businessStream)
	{
		this.topologyDao.saveBusinessStream(businessStream);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getBusinessStreamsByUserId(java.lang.Long)
	 */
	@Override
	public List<BusinessStream> getBusinessStreamsByUserId(Long userId)
	{
		return this.topologyDao.getBusinessStreamsByUserId(userId);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#getFlowsByServerIdAndModuleId(java.lang.Long, java.lang.Long)
	 */
	@Override
	public List<Flow> getFlowsByServerIdAndModuleId(Long serverId, Long moduleId)
	{
		return this.topologyDao.getFlowsByServerIdAndModuleId(serverId, moduleId);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.topology.service.TopologyService#deleteBusinessStreamFlow(org.ikasan.topology.model.BusinessStreamFlow)
	 */
	@Override
	public void deleteBusinessStreamFlow(BusinessStreamFlow businessStreamFlow)
	{
		this.topologyDao.deleteBusinessStreamFlow(businessStreamFlow);
	}
	
}
