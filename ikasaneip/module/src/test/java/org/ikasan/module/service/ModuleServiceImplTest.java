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

import org.ikasan.module.startup.dao.StartupControlDao;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;
import org.ikasan.systemevent.service.SystemEventService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test cases for ModuleServiceImpl
 *
 * @author Ikasan Development Team
 */
public class ModuleServiceImplTest
{
    private Mockery mockery = new Mockery()
    {{
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
    }};

    /** mocked container, service and dao */
    ModuleContainer moduleContainer = mockery.mock(ModuleContainer.class);
    SystemEventService systemEventService = mockery.mock(SystemEventService.class);
    StartupControlDao startupControlDao = mockery.mock(StartupControlDao.class);

    Module module = mockery.mock(Module.class);
    Flow flow = mockery.mock(Flow.class);
    StartupControl startupControl = mockery.mock(StartupControl.class);

    private static final String MODULE_NAME = "moduleName";
    private static final String FLOW_NAME = "flowName";
    private static final String ACTOR = "actor";

    /** Class under test */
    ModuleServiceImpl moduleService = new ModuleServiceImpl(moduleContainer, systemEventService, startupControlDao);

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_moduleContainer()
    {
        new ModuleServiceImpl(null, systemEventService, startupControlDao);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_systemEventService()
    {
        new ModuleServiceImpl(moduleContainer, null, startupControlDao);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_startupControlDao()
    {
        new ModuleServiceImpl(moduleContainer, systemEventService, null);
    }

    @Test
    public void test_startFlow_not_disabled()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(systemEventService).logSystemEvent(MODULE_NAME + "." + FLOW_NAME, ModuleServiceImpl.INITIATOR_START_REQUEST_SYSTEM_EVENT_ACTION, ACTOR);
                oneOf(moduleContainer).getModule(MODULE_NAME);
                will(returnValue(module));
                oneOf(module).getFlow(FLOW_NAME);
                will(returnValue(flow));
                oneOf(startupControlDao).getStartupControl(MODULE_NAME, FLOW_NAME);
                will(returnValue(startupControl));
                oneOf(startupControl).getStartupType();
                will(returnValue(StartupType.AUTOMATIC));
                oneOf(flow).start();

            }});
        moduleService.startFlow(MODULE_NAME, FLOW_NAME, ACTOR);
        mockery.assertIsSatisfied();
    }

    @Test(expected = IllegalStateException.class)
    public void test_startFlow_disabled()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(systemEventService).logSystemEvent(MODULE_NAME + "." + FLOW_NAME, ModuleServiceImpl.INITIATOR_START_REQUEST_SYSTEM_EVENT_ACTION, ACTOR);
                oneOf(moduleContainer).getModule(MODULE_NAME);
                will(returnValue(module));
                oneOf(module).getFlow(FLOW_NAME);
                will(returnValue(flow));
                oneOf(startupControlDao).getStartupControl(MODULE_NAME, FLOW_NAME);
                will(returnValue(startupControl));
                oneOf(startupControl).getStartupType();
                will(returnValue(StartupType.DISABLED));
            }});
        moduleService.startFlow(MODULE_NAME, FLOW_NAME, ACTOR);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_resumeFlow_not_disabled()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(systemEventService).logSystemEvent(MODULE_NAME + "." + FLOW_NAME, ModuleServiceImpl.INITIATOR_RESUME_REQUEST_SYSTEM_EVENT_ACTION, ACTOR);
                oneOf(moduleContainer).getModule(MODULE_NAME);
                will(returnValue(module));
                oneOf(module).getFlow(FLOW_NAME);
                will(returnValue(flow));
                oneOf(startupControlDao).getStartupControl(MODULE_NAME, FLOW_NAME);
                will(returnValue(startupControl));
                oneOf(startupControl).getStartupType();
                will(returnValue(StartupType.AUTOMATIC));
                oneOf(flow).resume();

            }});
        moduleService.resumeFlow(MODULE_NAME, FLOW_NAME, ACTOR);
        mockery.assertIsSatisfied();
    }

    @Test(expected = IllegalStateException.class)
    public void test_resumeFlow_disabled()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(systemEventService).logSystemEvent(MODULE_NAME + "." + FLOW_NAME, ModuleServiceImpl.INITIATOR_RESUME_REQUEST_SYSTEM_EVENT_ACTION, ACTOR);
                oneOf(moduleContainer).getModule(MODULE_NAME);
                will(returnValue(module));
                oneOf(module).getFlow(FLOW_NAME);
                will(returnValue(flow));
                oneOf(startupControlDao).getStartupControl(MODULE_NAME, FLOW_NAME);
                will(returnValue(startupControl));
                oneOf(startupControl).getStartupType();
                will(returnValue(StartupType.DISABLED));
            }});
        moduleService.resumeFlow(MODULE_NAME, FLOW_NAME, ACTOR);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_startPauseFlow()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(systemEventService).logSystemEvent(MODULE_NAME + "." + FLOW_NAME, ModuleServiceImpl.INITIATOR_START_PAUSE_REQUEST_SYSTEM_EVENT_ACTION, ACTOR);
                oneOf(moduleContainer).getModule(MODULE_NAME);
                will(returnValue(module));
                oneOf(module).getFlow(FLOW_NAME);
                will(returnValue(flow));
                oneOf(flow).startPause();

            }});
        moduleService.startPauseFlow(MODULE_NAME, FLOW_NAME, ACTOR);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_pauseFlow()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(systemEventService).logSystemEvent(MODULE_NAME + "." + FLOW_NAME, ModuleServiceImpl.INITIATOR_PAUSE_REQUEST_SYSTEM_EVENT_ACTION, ACTOR);
                oneOf(moduleContainer).getModule(MODULE_NAME);
                will(returnValue(module));
                oneOf(module).getFlow(FLOW_NAME);
                will(returnValue(flow));
                oneOf(flow).pause();

            }});
        moduleService.pauseFlow(MODULE_NAME, FLOW_NAME, ACTOR);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_stopFlow()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(systemEventService).logSystemEvent(MODULE_NAME + "." + FLOW_NAME, ModuleServiceImpl.INITIATOR_STOP_REQUEST_SYSTEM_EVENT_ACTION, ACTOR);
                oneOf(moduleContainer).getModule(MODULE_NAME);
                will(returnValue(module));
                oneOf(module).getFlow(FLOW_NAME);
                will(returnValue(flow));
                oneOf(flow).stop();

            }});
        moduleService.stopFlow(MODULE_NAME, FLOW_NAME, ACTOR);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_setStartupType_automatic()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(startupControlDao).getStartupControl(MODULE_NAME, FLOW_NAME);
                will(returnValue(startupControl));
                oneOf(startupControl).setComment("comment");
                oneOf(startupControl).setStartupType(StartupType.AUTOMATIC);
                oneOf(startupControlDao).save(startupControl);
                oneOf(systemEventService).logSystemEvent(MODULE_NAME + "." + FLOW_NAME, ModuleServiceImpl.INITIATOR_SET_STARTUP_TYPE_EVENT_ACTION + StartupType.AUTOMATIC.name(), ACTOR);
                oneOf(startupControl).getStartupType();
                will(returnValue(StartupType.AUTOMATIC));
            }});
        moduleService.setStartupType(MODULE_NAME, FLOW_NAME, StartupType.AUTOMATIC, "comment", ACTOR);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_setStartupType_manual()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(startupControlDao).getStartupControl(MODULE_NAME, FLOW_NAME);
                will(returnValue(startupControl));
                oneOf(startupControl).setComment("comment");
                oneOf(startupControl).setStartupType(StartupType.MANUAL);
                oneOf(startupControlDao).save(startupControl);
                oneOf(systemEventService).logSystemEvent(MODULE_NAME + "." + FLOW_NAME, ModuleServiceImpl.INITIATOR_SET_STARTUP_TYPE_EVENT_ACTION + StartupType.MANUAL.name(), ACTOR);
                oneOf(startupControl).getStartupType();
                will(returnValue(StartupType.MANUAL));
            }});
        moduleService.setStartupType(MODULE_NAME, FLOW_NAME, StartupType.MANUAL, "comment", ACTOR);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_setStartupType_disabled()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(startupControlDao).getStartupControl(MODULE_NAME, FLOW_NAME);
                will(returnValue(startupControl));
                oneOf(startupControl).setComment("comment");
                oneOf(startupControl).setStartupType(StartupType.DISABLED);
                oneOf(startupControlDao).save(startupControl);
                oneOf(systemEventService).logSystemEvent(MODULE_NAME + "." + FLOW_NAME, ModuleServiceImpl.INITIATOR_SET_STARTUP_TYPE_EVENT_ACTION + StartupType.DISABLED.name(), ACTOR);
                oneOf(startupControl).getStartupType();
                will(returnValue(StartupType.DISABLED));
            }});
        moduleService.setStartupType(MODULE_NAME, FLOW_NAME, StartupType.DISABLED, "comment", ACTOR);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_startContextListeners()
    {
        mockery.checking(new Expectations()
        {{
                oneOf(systemEventService).logSystemEvent(MODULE_NAME + "." + FLOW_NAME, ModuleServiceImpl.START_CONTEXT_LISTENERS_ACTION, ACTOR);
                oneOf(moduleContainer).getModule(MODULE_NAME);
                will(returnValue(module));
                oneOf(module).getFlow(FLOW_NAME);
                will(returnValue(flow));
                oneOf(flow).startContextListeners();
            }});
        moduleService.startContextListeners(MODULE_NAME, FLOW_NAME, ACTOR);
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_stopContextListeners()
    {
        mockery.checking(new Expectations(){{
            oneOf(systemEventService).logSystemEvent(MODULE_NAME+"."+FLOW_NAME, ModuleServiceImpl.STOP_CONTEXT_LISTENERS_ACTION, ACTOR);
            oneOf(moduleContainer).getModule(MODULE_NAME);
            will(returnValue(module));
            oneOf(module).getFlow(FLOW_NAME);
            will(returnValue(flow));
            oneOf(flow).stopContextListeners();
        }});
        moduleService.stopContextListeners(MODULE_NAME, FLOW_NAME, ACTOR);
        mockery.assertIsSatisfied();
    }
}
