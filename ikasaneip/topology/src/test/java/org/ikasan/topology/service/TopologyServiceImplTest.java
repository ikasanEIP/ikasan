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

import org.ikasan.topology.exception.DiscoveryException;
import org.ikasan.topology.dao.TopologyDao;
import org.ikasan.topology.model.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate4.HibernateTemplate;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class TopologyServiceImplTest
{

	private Mockery mockery = new Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
		setThreadingPolicy(new Synchroniser());
	}};
	private TopologyDao topologyDao = mockery.mock(TopologyDao.class);

	private TopologyService uut;
	
	/**
     * Before each test case, inject a mock {@link HibernateTemplate} to dao implementation
     * being tested
     */
    @Before
	public void setup()
    {

		uut = new TopologyServiceImpl(topologyDao);
    }

	@Test
	public void discoveryWhenServerIsNullAndModuleHasComponentsAndModuleIsNew() throws DiscoveryException
	{

		Module module = new Module("Module Test", "/contextRoot", "I am module 2","version", null, "diagram");

		Flow flow = new Flow("Flow Test", "I am flow Test", module);

		Component component = new Component();
		component.setName("testComponentName1");
		component.setDescription("description1");
		component.setOrder(0);
		component.setConfigurable(false);
		component.setFlow(flow);
		flow.addComponent(component);

		mockery.checking(new Expectations() {{

			exactly(1).of(topologyDao).getFlowByServerIdModuleIdAndFlowname(null, null,"Flow Test" );
			will(returnValue(null));

			exactly(2).of(topologyDao).save(flow);

			exactly(1).of(topologyDao).save(component);

			exactly(1).of(topologyDao).getComponentsByServerIdModuleIdAndFlownameAndComponentNameNotIn(null, null,"Flow Test", Arrays.asList("testComponentName1") );
			will(returnValue(Arrays.asList()));

			exactly(1).of(topologyDao).save(with(any(Module.class)));

			exactly(1).of(topologyDao).getFlowsByServerIdAndModuleId(null, null );
			will(returnValue(Arrays.asList()));

		}});

		//do test
		uut.discover(null,module, Arrays.asList(flow));

		//assert dao calls
		mockery.assertIsSatisfied();

	}

	@Test
	public void discoveryWhenServerIsNullAndNewModuleAndOlderModuleVersionIsInDBHaveDifferentComponents() throws DiscoveryException
	{

		Module module = new Module("Module Test", "/contextRoot", "I am module 2","version", null, "diagram");
		module.setId(1l);

		Component component = new Component();
		component.setName("testComponentName1");
		component.setDescription("description1");
		component.setOrder(0);
		component.setConfigurable(false);

		Flow flow = new Flow("Flow Test", "I am Test flow", null);
		component.setFlow(flow);
		flow.addComponent(component);

		Flow oldFlow = new Flow("Flow Test", "I am old Test flow", module);
		oldFlow.setId(2l);
		module.addFlow(oldFlow);

		Component oldComponent = new Component();
		oldComponent.setId(3l);
		oldComponent.setName("oldTestComponentName1");
		oldComponent.setDescription("Olddescription1");
		oldComponent.setOrder(0);
		oldComponent.setConfigurable(false);
		oldComponent.setFlow(oldFlow);
		oldFlow.addComponent(oldComponent);

		mockery.checking(new Expectations() {{

			exactly(1).of(topologyDao).getFlowByServerIdModuleIdAndFlowname(null, 1l,"Flow Test" );
			will(returnValue(oldFlow));

			exactly(2).of(topologyDao).save(oldFlow);

			exactly(1).of(topologyDao).save(component);

			exactly(1).of(topologyDao).getComponentsByServerIdModuleIdAndFlownameAndComponentNameNotIn(null, 1l,"Flow Test", Arrays.asList("testComponentName1") );
			will(returnValue(Arrays.asList(oldComponent)));

			exactly(1).of(topologyDao).deleteFilterComponentsByComponentId( 3l );

			exactly(1).of(topologyDao).delete(oldComponent);

			exactly(1).of(topologyDao).save(with(any(Module.class)));

			exactly(1).of(topologyDao).getFlowsByServerIdAndModuleId(null, 1l );
			will(returnValue(Arrays.asList(oldFlow)));

		}});


		//do test
		uut.discover(null,module, Arrays.asList(flow));

		//assert dao calls
		mockery.assertIsSatisfied();

	}

	@Test
	public void discoveryWhenServerIsNullAndNewModuleAndOlderModuleVersionIsInDBHaveDifferentFlows() throws DiscoveryException
	{

		Module module = new Module("Module Test", "/contextRoot", "I am module 2","version", null, "diagram");
		module.setId(1l);

		Flow flow = new Flow("Flow Test", "I am Test flow", null);

		Component component = new Component();
		component.setName("testComponentName1");
		component.setDescription("description1");
		component.setOrder(0);
		component.setConfigurable(false);
		component.setFlow(flow);
		flow.addComponent(component);


		Flow oldFlow = new Flow("Old Test Flow", "I am old Test flow", module);
		oldFlow.setId(2l);
		module.addFlow(oldFlow);

		Component oldComponent = new Component();
		oldComponent.setId(3l);
		oldComponent.setName("oldTestComponentName1");
		oldComponent.setDescription("Olddescription1");
		oldComponent.setOrder(0);
		oldComponent.setConfigurable(false);
		oldComponent.setFlow(oldFlow);
		oldFlow.addComponent(oldComponent);

		mockery.checking(new Expectations() {{

			exactly(1).of(topologyDao).getFlowByServerIdModuleIdAndFlowname(null, 1l,"Flow Test" );
			will(returnValue(null));

			exactly(2).of(topologyDao).save(flow);

			exactly(1).of(topologyDao).save(component);

			exactly(1).of(topologyDao).getComponentsByServerIdModuleIdAndFlownameAndComponentNameNotIn(null, 1l,"Flow Test", Arrays.asList("testComponentName1") );
			will(returnValue(Arrays.asList()));


			exactly(1).of(topologyDao).save(with(any(Module.class)));

			// flow clean up
			exactly(1).of(topologyDao).getFlowsByServerIdAndModuleId(null, 1l );
			will(returnValue(Arrays.asList(oldFlow)));

			exactly(1).of(topologyDao).delete(oldComponent);

			exactly(1).of(topologyDao).deleteBusinessStreamFlowByFlowId( 2l );

			exactly(1).of(topologyDao).delete(oldFlow);


		}});


		//do test
		uut.discover(null,module, Arrays.asList(flow));

		//assert dao calls
		mockery.assertIsSatisfied();

	}


}
