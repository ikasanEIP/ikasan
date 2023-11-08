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
package org.ikasan.module.service;

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.harvest.HarvestingSchedulerService;
import org.ikasan.spec.housekeeping.HousekeepingSchedulerService;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleActivator;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.monitor.FlowMonitor;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Before;
import org.junit.Test;
import org.quartz.Scheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test cases for ModuleInitialisationServiceImpl
 *
 * @author Ikasan Development Team
 */
public class ModuleInitialisationServiceImplTest {
    private Mockery mockery = new Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    /**
     * mocked container, service and dao
     */
    ModuleContainer moduleContainer = mockery.mock(ModuleContainer.class);
    ModuleActivator moduleActivator = mockery.mock(ModuleActivator.class);
    HousekeepingSchedulerService housekeepingSchedulerService = mockery.mock(HousekeepingSchedulerService.class);
    HarvestingSchedulerService harvestingSchedulerService = mockery.mock(HarvestingSchedulerService.class);
    ApplicationContext platformContext = mockery.mock(ApplicationContext.class);
    Flow flow1 = mockery.mock(Flow.class, "flow1");
    FlowConfiguration flow1Configuration = mockery.mock(FlowConfiguration.class, "flow1Configuration");

    Module module = mockery.mock(Module.class);

    private static final String MODULE_NAME = "moduleName";

    /**
     * Class under test
     */
    ModuleInitialisationServiceImpl uut;
    @Before
    public void setup(){
        uut = new ModuleInitialisationServiceImpl(moduleContainer, moduleActivator,
           housekeepingSchedulerService,harvestingSchedulerService);

        List<AbstractApplicationContext> innerContexts = new ArrayList<>();
        ReflectionTestUtils.setField(uut,"platformContext",platformContext);

    }
    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_moduleContainer() {
        new ModuleInitialisationServiceImpl(null, moduleActivator,
            housekeepingSchedulerService, harvestingSchedulerService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_module_activator() {
        new ModuleInitialisationServiceImpl(moduleContainer, null,
            housekeepingSchedulerService, harvestingSchedulerService);
    }


    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_housekeeping() {
        new ModuleInitialisationServiceImpl(moduleContainer, moduleActivator,
            null, harvestingSchedulerService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_harvestion() {
        new ModuleInitialisationServiceImpl(moduleContainer, moduleActivator,
            housekeepingSchedulerService, null);
    }


    @Test
    public void destroy() throws Exception {
        mockery.checking(new Expectations() {{
            oneOf(moduleContainer).getModules();
            will(returnValue(Arrays.asList(module)));

            oneOf(moduleActivator).deactivate(module);

            oneOf(module).getFlows();
            will(returnValue(List.of(flow1)));

            oneOf(flow1).getFlowConfiguration();
            will(returnValue(flow1Configuration));

            oneOf(flow1Configuration).getManagedServices();
            will(returnValue(List.of()));

            oneOf(module).getName();
            will(returnValue(MODULE_NAME));

            oneOf(moduleContainer).remove(MODULE_NAME);

            oneOf(platformContext).getBeansOfType(Scheduler.class);
            will(returnValue(null));

            oneOf(platformContext).getBeansOfType(FlowMonitor.class);
            will(returnValue(null));

        }});
        uut.destroy();
        mockery.assertIsSatisfied();
    }

    @Test
    public void register() throws Exception {
        mockery.checking(new Expectations() {{
            oneOf(moduleContainer).add(module);
            oneOf(moduleActivator).activate(module);
            oneOf(housekeepingSchedulerService).registerJobs();
            oneOf(harvestingSchedulerService).registerJobs();
        }});
        uut.register(module);
        mockery.assertIsSatisfied();
    }
}
