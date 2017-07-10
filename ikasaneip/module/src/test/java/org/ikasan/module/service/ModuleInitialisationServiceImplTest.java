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

import org.ikasan.security.service.UserService;
import org.ikasan.spec.module.*;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.topology.service.TopologyService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
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
 * Test cases for ModuleServiceImpl
 *
 * @author Ikasan Development Team
 */
public class ModuleInitialisationServiceImplTest {
    private Mockery mockery = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    /**
     * mocked container, service and dao
     */
    ModuleContainer moduleContainer = mockery.mock(ModuleContainer.class);
    ModuleActivator moduleActivator = mockery.mock(ModuleActivator.class);
    UserService userService = mockery.mock(UserService.class);
    TopologyService topologyService = mockery.mock(TopologyService.class);
    ApplicationContext platformContext = mockery.mock(ApplicationContext.class);

    Module module = mockery.mock(Module.class);

    private static final String MODULE_NAME = "moduleName";
    private static final String FLOW_NAME = "flowName";
    private static final String ACTOR = "actor";

    /**
     * Class under test
     */
    ModuleInitialisationServiceImpl uut;
    @Before
    public void setup(){
        uut = new ModuleInitialisationServiceImpl(moduleContainer, moduleActivator, userService, topologyService);

        List<AbstractApplicationContext> innerContexts = new ArrayList<>();
        ReflectionTestUtils.setField(uut,"platformContext",platformContext);
        ReflectionTestUtils.setField(uut,"innerContexts",innerContexts);

    }
    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_moduleContainer() {
        new ModuleInitialisationServiceImpl(null, moduleActivator, userService, topologyService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_systemEventService() {
        new ModuleInitialisationServiceImpl(moduleContainer, null, userService, topologyService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_startupControlDao() {
        new ModuleInitialisationServiceImpl(moduleContainer, moduleActivator, null,topologyService);
    }

    @Test
    public void destroy() throws Exception {
        mockery.checking(new Expectations() {{
            oneOf(moduleContainer).getModules();
            will(returnValue(Arrays.asList(module)));

            oneOf(moduleActivator).deactivate(module);

            oneOf(module).getName();
            will(returnValue(MODULE_NAME));

            oneOf(moduleContainer).remove(MODULE_NAME);

            oneOf(platformContext).getBeansOfType(Scheduler.class);
            will(returnValue(null));

            oneOf(platformContext).getBeansOfType(Monitor.class);
            will(returnValue(null));

        }});
        uut.destroy();
        mockery.assertIsSatisfied();
    }


}
