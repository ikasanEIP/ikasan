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

import javax.annotation.Resource;

import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "/topology-conf.xml",
        "/hsqldb-config.xml",
        "/substitute-components.xml",
        "/mock-components.xml"
})
public class HibernateTopologyDaoTest
{

	@Resource
	private TopologyDao xaTopologyDao;
	
	/**
     * Before each test case, inject a mock {@link HibernateTemplate} to dao implementation
     * being tested
     * @throws SecurityDaoException 
     */
    @Before public void setup()
    {
    	Server server = new Server("esb01", "This is the first esb server", "svc-esb01", 60000);    	
    	this.xaTopologyDao.save(server);
    	
    	Module module = new Module("Module 1", "contextRoot", "I am module 1", server, "diagram");
    	this.xaTopologyDao.save(module);
    	
    	Flow flow = new Flow("Flow 1", "I am flow 1", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 2", "I am flow 2", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 3", "I am flow 3", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 4", "I am flow 4", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 5", "I am flow 5", module);
    	this.xaTopologyDao.save(flow);
    	
    	module = new Module("Module 2", "contextRoot", "I am module 2", server, "diagram");
     	this.xaTopologyDao.save(module);
     	flow = new Flow("Flow 1", "I am flow 1", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 2", "I am flow 2", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 3", "I am flow 3", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 4", "I am flow 4", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 5", "I am flow 5", module);
    	this.xaTopologyDao.save(flow);
     	
     	module = new Module("Module 3", "contextRoot", "I am module 3", server, "diagram");
     	this.xaTopologyDao.save(module);
     	flow = new Flow("Flow 1", "I am flow 1", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 2", "I am flow 2", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 3", "I am flow 3", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 4", "I am flow 4", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 5", "I am flow 5", module);
    	this.xaTopologyDao.save(flow);
     	
     	module = new Module("Module 4", "contextRoot", "I am module 4", server, "diagram");
     	this.xaTopologyDao.save(module);
     	flow = new Flow("Flow 1", "I am flow 1", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 2", "I am flow 2", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 3", "I am flow 3", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 4", "I am flow 4", module);
    	this.xaTopologyDao.save(flow);
    	flow = new Flow("Flow 5", "I am flow 5", module);
    	this.xaTopologyDao.save(flow);
     	
     	
    }
    
	/**
	 * Test method for {@link org.ikasan.security.dao.HibernateAuthorityDao#getAuthorities()}.
	 */
	@Test
	@DirtiesContext
	public void testGetAllServers()
	{
		List<Server> servers = this.xaTopologyDao.getAllServers();
		Assert.assertTrue(servers.size() == 1);
		
		Assert.assertTrue(servers.get(0).getModules().size() == 4);
	}
	
	/**
	 * Test method for {@link org.ikasan.security.dao.HibernateAuthorityDao#getAuthorities()}.
	 */
	@Test
	@DirtiesContext
	public void testFlowsByServerId()
	{
		List<Server> servers = this.xaTopologyDao.getAllServers();
		Assert.assertTrue(servers.size() == 1);
		
		List<Flow> flows =  this.xaTopologyDao.getFlowsByServerIdAndModuleId(servers.get(0).getId(), null);
		
		Assert.assertTrue(flows.size() == 20);
	}

	/**
	 * Test method for {@link org.ikasan.security.dao.HibernateAuthorityDao#getAuthorities()}.
	 */
	@Test
	@DirtiesContext
	public void testFlowsByModuleId()
	{
		List<Module> modules = this.xaTopologyDao.getAllModules();
		
		List<Flow> flows =  this.xaTopologyDao.getFlowsByServerIdAndModuleId(null, modules.get(1).getId());
		
		Assert.assertTrue(flows.size() == 5);
	}
	
	/**
	 * Test method for {@link org.ikasan.security.dao.HibernateAuthorityDao#getAuthorities()}.
	 */
	@Test
	@DirtiesContext
	public void testFlowsByServerIdAndModuleId()
	{
		List<Server> servers = this.xaTopologyDao.getAllServers();
		Assert.assertTrue(servers.size() == 1);
		
		List<Module> modules = this.xaTopologyDao.getAllModules();
		
		List<Flow> flows =  this.xaTopologyDao.getFlowsByServerIdAndModuleId(servers.get(0).getId(), modules.get(0).getId());
		
		Assert.assertTrue(flows.size() == 5);
	}
}
