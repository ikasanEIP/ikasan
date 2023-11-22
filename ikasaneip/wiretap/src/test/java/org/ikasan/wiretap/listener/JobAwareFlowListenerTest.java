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
package org.ikasan.wiretap.listener;

import org.ikasan.WiretapAutoConfiguration;
import org.ikasan.WiretapTestAutoConfiguration;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.trigger.Trigger;
import org.ikasan.spec.trigger.TriggerRelationship;
import org.ikasan.trigger.dao.TriggerDao;
import org.ikasan.trigger.model.TriggerImpl;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WiretapAutoConfiguration.class, WiretapTestAutoConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JobAwareFlowListenerTest
{

    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };

    ModuleService moduleService = mockery.mock(ModuleService.class);
    DashboardRestService dashboardRestService = mockery.mock(DashboardRestService.class);

    @Resource
    private TriggerDao triggerDao;


	private JobAwareFlowEventListener uut;

	private static final String MODULE_1 = "module1";
	private static final String MODULE_2= "module2";
	private static final String FLOW_1= "test Flow 1";
	private static final String FLOW_2= "test Flow 2";

	@Before
	public void setup()
	{
	    //String moduleName, String flowName, String relationshipDescription, String jobName

        Trigger trigger1Before = new TriggerImpl(MODULE_1,FLOW_1, TriggerRelationship.BEFORE.getDescription(),"trigger1Before");
        Trigger trigger1After = new TriggerImpl(MODULE_1,FLOW_1, TriggerRelationship.AFTER.getDescription(),"trigger1After");
        Trigger trigger2Before = new TriggerImpl(MODULE_1,FLOW_2, TriggerRelationship.BEFORE.getDescription(),"trigger2Before");
        Trigger trigger2After = new TriggerImpl(MODULE_1,FLOW_2, TriggerRelationship.AFTER.getDescription(),"trigger2After","Test");
        Trigger trigger2After2 = new TriggerImpl(MODULE_1,FLOW_2, TriggerRelationship.AFTER.getDescription(),"trigger2AfterLog","Test");

        this.triggerDao.save(trigger1Before);
        this.triggerDao.save(trigger1After);
        this.triggerDao.save(trigger2Before);
        this.triggerDao.save(trigger2After);
        this.triggerDao.save(trigger2After2);

        uut = new JobAwareFlowEventListener(null, triggerDao,moduleService,dashboardRestService);

	}

	@Test
	public void test_getTriggersForGivenFlow()
	{
	    Map<String,List<Trigger>> triggers = uut.getTriggers(MODULE_1,FLOW_1);

		assertEquals( 2, triggers.size());

		List<Trigger> result = triggers.get(TriggerRelationship.BEFORE.getDescription());
        assertEquals(1, result.size());
        assertEquals(MODULE_1, result.get(0).getModuleName());
        assertEquals("trigger1Before", result.get(0).getJobName());

    }

    @Test
    public void test_getTriggersForGivenFlowWhenComponentHasTwoTriggers()
    {
        Map<String,List<Trigger>> triggers = uut.getTriggers(MODULE_1,FLOW_2);

        assertEquals( 2, triggers.size());

        List<Trigger> result = triggers.get("afterTest");
        assertEquals(2, result.size());
        assertEquals(MODULE_1, result.get(0).getModuleName());
        assertEquals(MODULE_1, result.get(1).getModuleName());
        assertEquals("trigger2After", result.get(0).getJobName());
        assertEquals("trigger2AfterLog", result.get(1).getJobName());

    }

    @Test
    public void addDynamicTrigger()
    {

        Module m = mockery.mock(Module.class);
        mockery.checking(new Expectations(){{
            oneOf(moduleService).getModule("moduleTest");
            will(returnValue(m));
            oneOf(dashboardRestService).publish(m);
        }});

        uut.addDynamicTrigger(new TriggerImpl("moduleTest","flowTest","AFTER","testTrigger"));

        mockery.assertIsSatisfied();
    }


}
