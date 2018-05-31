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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Filter;
import org.ikasan.topology.model.FilterComponent;
import org.ikasan.topology.model.FilterComponentKey;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Notification;
import org.ikasan.topology.model.RoleFilter;
import org.ikasan.topology.model.RoleFilterKey;
import org.ikasan.topology.model.Server;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataIntegrityViolationException;
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
        "/topology-conf.xml", "/h2db-config.xml",
        "/substitute-components.xml"
})
public class HibernateTopologyDaoTest
{

	@Resource
	private TopologyDao xaTopologyDao;
	
	/**
     * Before each test case, inject a mock {@link HibernateTemplate} to dao implementation
     * being tested
     */
    @Before public void setup()
    {
    	Server server = new Server("esb01", "This is the first esb server", "svc-esb01", 60000);    	
    	this.xaTopologyDao.save(server);
    	
    	Module module = new Module("Module 1", "contextRoot", "I am module 1","version", server, "diagram");
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
    	
    	module = new Module("Module 2", "contextRoot", "I am module 2","version", server, "diagram");
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
     	
     	module = new Module("Module 3", "contextRoot", "I am module 3", "version",server, "diagram");
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
     	
     	module = new Module("Module 4", "contextRoot", "I am module 4","version", server, "diagram");
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
    	
    	Component component = new Component();
    	component.setName("name");
    	component.setDescription("description");
    	component.setOrder(0);
    	component.setConfigurable(false);
    	component.setFlow(flow);
    	
    	flow.getComponents().add(component);
    	
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
	public void testGetFlowsByServerIdModuleIdAndFlowname()
	{
		List<Server> servers = this.xaTopologyDao.getAllServers();
		Assert.assertTrue(servers.size() == 1);
		
		Module module = this.xaTopologyDao.getModuleByName("Module 1");
		
		
		Flow flow =  this.xaTopologyDao.getFlowByServerIdModuleIdAndFlowname(servers.get(0).getId(), module.getId(), "Flow 1");
		
		Assert.assertTrue(flow != null);
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
	
	@Test
	@DirtiesContext
	public void testSaveFilter()
	{
		Filter filter = new Filter("testFilter", "testFilterDescription", "me");
		
		List<Flow> flows = this.xaTopologyDao.getAllFlows();
		
		this.xaTopologyDao.saveFilter(filter);
		
		Set<FilterComponent> components = new HashSet<FilterComponent>();
		
		for(Flow flow: flows)
		{
			for(Component component: flow.getComponents())
			{
				FilterComponent fc = new FilterComponent(new FilterComponentKey(filter.getId(), component.getId()));
				fc.setComponent(component);
				
				components.add(fc);
			}
		}
		
		filter.setComponents(components);
		
		this.xaTopologyDao.saveFilter(filter);
		
		
		Assert.assertTrue(this.xaTopologyDao.getAllFilters().size() == 1);
		
		Assert.assertTrue(this.xaTopologyDao.getAllFilters().get(0).getComponents().size() == 1);
	}
	
	@Test
	@DirtiesContext
	public void testGetFilterByName()
	{
		Filter filter = new Filter("testFilter", "testFilterDescription", "me");
		
		List<Flow> flows = this.xaTopologyDao.getAllFlows();
		
		this.xaTopologyDao.saveFilter(filter);
		
		Set<FilterComponent> components = new HashSet<FilterComponent>();
		
		for(Flow flow: flows)
		{
			for(Component component: flow.getComponents())
			{
				FilterComponent fc = new FilterComponent(new FilterComponentKey(filter.getId(), component.getId()));
				fc.setComponent(component);
				
				components.add(fc);
			}
		}
		
		filter.setComponents(components);
		
		this.xaTopologyDao.saveFilter(filter);
		
		
		Assert.assertTrue(this.xaTopologyDao.getFilterByName("testFilter") != null);
	}
	
	/**
	 * Test method for {@link org.ikasan.security.dao.HibernateAuthorityDao#getAuthorities()}.
	 */
	@Test
	@DirtiesContext
	public void testGetAllFilters()
	{
		Filter filter = new Filter("testFilter", "testFilterDescription", "me");
		
		List<Flow> flows = this.xaTopologyDao.getAllFlows();
		
		this.xaTopologyDao.saveFilter(filter);
		
		Set<FilterComponent> components = new HashSet<FilterComponent>();
		
		for(Flow flow: flows)
		{
			for(Component component: flow.getComponents())
			{
				FilterComponent fc = new FilterComponent(new FilterComponentKey(filter.getId(), component.getId()));
				fc.setComponent(component);
				
				components.add(fc);
			}
		}
		
		filter.setComponents(components);
		
		this.xaTopologyDao.saveFilter(filter);
		
		filter = new Filter("testFilter2", "testFilterDescription", "me");
		
		flows = this.xaTopologyDao.getAllFlows();
		
		this.xaTopologyDao.saveFilter(filter);
		
		components = new HashSet<FilterComponent>();
		
		for(Flow flow: flows)
		{
			for(Component component: flow.getComponents())
			{
				FilterComponent fc = new FilterComponent(new FilterComponentKey(filter.getId(), component.getId()));
				fc.setComponent(component);
				
				components.add(fc);
			}
		}
		
		filter.setComponents(components);
		
		this.xaTopologyDao.saveFilter(filter);
		
		
		Assert.assertTrue(this.xaTopologyDao.getAllFilters().size() == 2);
		
		Assert.assertTrue(this.xaTopologyDao.getAllFilters().get(0).getComponents().size() == 1);
		Assert.assertTrue(this.xaTopologyDao.getAllFilters().get(1).getComponents().size() == 1);
	}
	
	@Test
	@DirtiesContext	
	public void testSaveRoleFilter()
	{
		Filter filter = new Filter("testFilter", "testFilterDescription", "me");
		
		List<Flow> flows = this.xaTopologyDao.getAllFlows();
		
		this.xaTopologyDao.saveFilter(filter);
		
		RoleFilter rf = new RoleFilter(new RoleFilterKey(new Long(1), filter.getId()));
		
		this.xaTopologyDao.saveRoleFilter(rf);
		
		List<Long> roleIds = new ArrayList<Long>();
		roleIds.add(new Long(1));
		
		Assert.assertTrue(this.xaTopologyDao.getRoleFiltersByRoleId(roleIds) != null);
		
		Assert.assertTrue(this.xaTopologyDao.getRoleFiltersByRoleId(roleIds).get(0).getFilter().getName().equals("testFilter"));
	}
	
	@Test
	@DirtiesContext
	public void testSaveNotification()
	{
		Filter filter = new Filter("testFilter", "testFilterDescription", "me");
		
		List<Flow> flows = this.xaTopologyDao.getAllFlows();
		
		this.xaTopologyDao.saveFilter(filter);
		
		Notification notification = new Notification();
		notification.setName("testNotification");
		notification.setContext("context");
		notification.setFilter(filter);
		
		this.xaTopologyDao.save(notification);
		
		Notification found = this.xaTopologyDao.getNotificationByName("testNotification");
		
		Assert.assertTrue(found != null);
		
		Assert.assertTrue(found.getName().equals(notification.getName()));
		
	}
	
	@Test
	@DirtiesContext
	public void testSaveNotification_duplicate_filter()
	{
		Filter filter = new Filter("testFilter", "testFilterDescription", "me");
		
		List<Flow> flows = this.xaTopologyDao.getAllFlows();
		
		this.xaTopologyDao.saveFilter(filter);
		
		
		Notification notification = new Notification();
		notification.setName("testNotification");
		notification.setContext("context");
		notification.setFilter(filter);
		
		this.xaTopologyDao.save(notification);
		
		notification = new Notification();
		notification.setName("testNotification2");
		notification.setContext("context");
		notification.setFilter(filter);
		
		this.xaTopologyDao.save(notification);
		
		Notification found = this.xaTopologyDao.getNotificationByName("testNotification");
		
		Assert.assertTrue(found != null);	
		
		Assert.assertTrue(this.xaTopologyDao.getAllNotifications().size() == 2);	
	}
	
	@Test (expected = DataIntegrityViolationException.class)
	@DirtiesContext
	public void testSaveNotification_exception_duplicate_name()
	{
		Filter filter = new Filter("testFilter", "testFilterDescription", "me");
		
		List<Flow> flows = this.xaTopologyDao.getAllFlows();
		
		this.xaTopologyDao.saveFilter(filter);
		
		
		Notification notification = new Notification();
		notification.setName("testNotification");
		notification.setContext("context");
		notification.setFilter(filter);
		
		this.xaTopologyDao.save(notification);
		
		notification = new Notification();
		notification.setName("testNotification");
		notification.setContext("context");
		notification.setFilter(filter);
		
		this.xaTopologyDao.save(notification);
		
		Notification found = this.xaTopologyDao.getNotificationByName("testNotification");
		
		Assert.assertTrue(found != null);
		
		Assert.assertTrue(found.getName().equals(notification.getName()));
		
	}

	@Test
    @DirtiesContext
    public void test_save_with_invoker_config()
    {
        Server server = new Server("esb02", "This is the first esb server", "svc-esb02", 60000);
        this.xaTopologyDao.save(server);

        Module module = new Module("Module 1 with invoker config", "contextRoot", "I am module 1","version", server, "diagram");
        this.xaTopologyDao.save(module);

        Flow flow = new Flow("Flow 1", "I am flow 1", module);
        this.xaTopologyDao.save(flow);

        Component component = new Component();
        component.setFlow(flow);
        component.setName("name");
        component.setDescription("description");
        component.setOrder(0);
        component.setConfigurable(false);
        component.setInvokerConfigurable(true);
        component.setInvokerConfigurationId("invokerConfigurationId");

        this.xaTopologyDao.save(component);

        List<Component> components = this.xaTopologyDao.getAllComponents();

        Assert.assertEquals("Is configurable", components.get(1).isInvokerConfigurable(), true);
        Assert.assertEquals("Is configurable", components.get(1).getInvokerConfigurationId(), "invokerConfigurationId");
    }
}
