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

import org.ikasan.module.ConfiguredModuleConfiguration;
import org.ikasan.module.FlowFactoryCapable;
import org.ikasan.module.startup.dao.StartupControlDao;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowFactory;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleActivator;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.spec.module.StartupType;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test cases for ModuleActivatorImpl
 *
 * @author Ikasan Development Team
 */
public class ModuleActivatorDefaultImplTest
{
    private Mockery mockery = new Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    /**
     * mocked container, service and dao
     */
    ConfigurationService configurationService = mockery.mock(ConfigurationService.class);
    DashboardRestService dashboardRestService = mockery.mock(DashboardRestService.class);
    StartupControlDao startupControlDao = mockery.mock(StartupControlDao.class);

    Flow flow1 = mockery.mock(Flow.class, "flow1");

    Module module = mockery.mock(Module.class);
    FlowFactoryCapable flowFactoryCapable = mockery.mock(FlowFactoryCapable.class);
    FlowFactory flowFactory = mockery.mock(FlowFactory.class);
    StartupControl flowStartupControl = mockery.mock(StartupControl.class);

    ConfiguredResource<ConfiguredModuleConfiguration> configuredResource = mockery.mock(ConfiguredResource.class);
    ConfiguredResource<ConfiguredModuleConfiguration> configuredModuleConfiguration = new ExtendedConfiguredResource();
    private BulkStartupTypeSetupService bulkStartupTypeSetupService = mockery.mock(BulkStartupTypeSetupService.class);
    private WiretapTriggerSetupService wiretapTriggerSetupService = mockery.mock(WiretapTriggerSetupService.class);

    /**
     * Class under test
     */
    ModuleActivator moduleActivator;

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_configurationService()
    {
        new ModuleActivatorDefaultImpl(null, startupControlDao, dashboardRestService,
            dashboardRestService, bulkStartupTypeSetupService, wiretapTriggerSetupService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_startupControlDao()
    {
        new ModuleActivatorDefaultImpl(configurationService, null, dashboardRestService,
            dashboardRestService, bulkStartupTypeSetupService, wiretapTriggerSetupService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_moduleDashboardRestService()
    {
        new ModuleActivatorDefaultImpl(configurationService, startupControlDao, null,
            dashboardRestService, bulkStartupTypeSetupService, wiretapTriggerSetupService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_configurationDashboardRestService()
    {
        new ModuleActivatorDefaultImpl(configurationService, startupControlDao, dashboardRestService,
            null, bulkStartupTypeSetupService, wiretapTriggerSetupService);
    }

    @Test
    public void test_successful_activate_existing_startupControl()
    {
        List flowsEmpty = new ArrayList();

        final List flowsPopulated = new ArrayList();
        flowsPopulated.add(flow1);

        final List startupControls = new ArrayList();
        startupControls.add(flowStartupControl);

        moduleActivator =
            new ExtendedModuleActivator(
                true, configurationService, startupControlDao, dashboardRestService,
                dashboardRestService,bulkStartupTypeSetupService, wiretapTriggerSetupService);

        String moduleName = "moduleName";

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(module).getName();
                will(returnValue(moduleName));
                exactly(1).of(wiretapTriggerSetupService).setup("moduleName");
                exactly(1).of(module).getName();
                will(returnValue(moduleName));
                exactly(1).of(bulkStartupTypeSetupService).deleteAllOnlyIfConfigured("moduleName");
                exactly(1).of(startupControlDao).getStartupControls(moduleName);
                will(returnValue(startupControls));
                exactly(1).of(bulkStartupTypeSetupService).setup(flowStartupControl);
                will(returnValue(flowStartupControl));

                exactly(1).of(flowStartupControl).getFlowName();
                will(returnValue("flowname1"));

                oneOf(configurationService).configure(configuredModuleConfiguration);
                ignoring(configuredResource);

                oneOf(flowFactoryCapable).getFlowFactory();
                will(returnValue(flowFactory));

                oneOf(flowFactory).create("flowname1", "profile");
                will(returnValue(List.of(flow1)));

                exactly(1).of(module).getFlows();
                will(returnValue(flowsEmpty));

                // start flows
                exactly(1).of(module).getFlows();
                will(returnValue(flowsPopulated));

                exactly(1).of(module).getName();
                will(returnValue(moduleName));

                exactly(1).of(flow1).getName();
                will(returnValue("flowname1"));

                exactly(1).of(flowStartupControl).getStartupType();
                will(returnValue(StartupType.AUTOMATIC));

                exactly(1).of(flow1).getName();
                will(returnValue("flowname1"));

                oneOf(flow1).start();

                exactly(2).of(dashboardRestService).publish(module);
            }});

        moduleActivator.activate(module);
        Assert.assertTrue("module isActivated should return true", moduleActivator.isActivated(module) );
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_activate_no_existing_startupControl()
    {
        List flowsEmpty = new ArrayList();

        final List flowsPopulated = new ArrayList();
        flowsPopulated.add(flow1);

        final List startupControls = new ArrayList();

        moduleActivator =
            new ExtendedModuleActivator(true, configurationService, startupControlDao,
                dashboardRestService, dashboardRestService, bulkStartupTypeSetupService, wiretapTriggerSetupService);

        String moduleName = "moduleName";

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(module).getName();
                will(returnValue(moduleName));
                exactly(1).of(wiretapTriggerSetupService).setup("moduleName");
                exactly(1).of(module).getName();
                will(returnValue(moduleName));
                exactly(1).of(bulkStartupTypeSetupService).deleteAllOnlyIfConfigured("moduleName");
                exactly(1).of(startupControlDao).getStartupControls(moduleName);
                will(returnValue(startupControls));
                exactly(1).of(bulkStartupTypeSetupService).setup(flowStartupControl);
                will(returnValue(flowStartupControl));

                oneOf(configurationService).configure(configuredModuleConfiguration);
                ignoring(configuredResource);

                oneOf(module).getName();
                will(returnValue(moduleName));

                oneOf(startupControlDao).getStartupControl(moduleName, "flowname1");
                will(returnValue(flowStartupControl));

                oneOf(flowStartupControl).setStartupType(StartupType.AUTOMATIC);

                oneOf(flowFactoryCapable).getFlowFactory();
                will(returnValue(flowFactory));

                oneOf(flowFactory).create("flowname1", "profile");
                will(returnValue(List.of(flow1)));

                exactly(1).of(module).getFlows();
                will(returnValue(flowsEmpty));

                // start flows
                exactly(1).of(module).getFlows();
                will(returnValue(flowsPopulated));

                exactly(1).of(module).getName();
                will(returnValue(moduleName));

                exactly(1).of(flow1).getName();
                will(returnValue("flowname1"));

                oneOf(flowStartupControl).getStartupType();
                will(returnValue(StartupType.AUTOMATIC));

                exactly(1).of(flow1).getName();
                will(returnValue("flowname1"));

                oneOf(flow1).start();

                exactly(2).of(dashboardRestService).publish(module);
            }});

        moduleActivator.activate(module);
        Assert.assertTrue("module isActivated should return true", moduleActivator.isActivated(module) );
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_activate_no_existing_startupControl_not_configured_resource()
    {
        List flowsEmpty = new ArrayList();

        final List flowsPopulated = new ArrayList();
        flowsPopulated.add(flow1);

        final List startupControls = new ArrayList();

        moduleActivator =
            new ExtendedModuleActivator(false, configurationService, startupControlDao,
                dashboardRestService, dashboardRestService,
                bulkStartupTypeSetupService, wiretapTriggerSetupService);

        String moduleName = "moduleName";

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(module).getName();
                will(returnValue(moduleName));
                exactly(1).of(wiretapTriggerSetupService).setup("moduleName");
                exactly(1).of(module).getName();
                will(returnValue(moduleName));
                exactly(1).of(bulkStartupTypeSetupService).deleteAllOnlyIfConfigured("moduleName");
                exactly(1).of(startupControlDao).getStartupControls(moduleName);
                will(returnValue(startupControls));
                exactly(1).of(bulkStartupTypeSetupService).setup(flowStartupControl);
                will(returnValue(flowStartupControl));

                oneOf(module).getName();
                will(returnValue(moduleName));

                // start flows
                exactly(1).of(module).getFlows();
                will(returnValue(flowsPopulated));

                exactly(1).of(module).getName();
                will(returnValue(moduleName));

                exactly(2).of(flow1).getName();
                will(returnValue("flowname1"));

                oneOf(startupControlDao).getStartupControl(moduleName, "flowname1");
                will(returnValue(flowStartupControl));

                oneOf(flowStartupControl).getStartupType();
                will(returnValue(StartupType.AUTOMATIC));

                exactly(1).of(flow1).getName();
                will(returnValue("flowname1"));

                oneOf(flow1).start();

                exactly(2).of(dashboardRestService).publish(module);
            }});

        moduleActivator.activate(module);
        Assert.assertTrue("module isActivated should return true", moduleActivator.isActivated(module) );
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_deactivate_activate_with_stopped_flow()
    {
        List flowsEmpty = new ArrayList();

        final List flowsPopulated = new ArrayList();
        flowsPopulated.add(flow1);

        final List startupControls = new ArrayList();

        moduleActivator =
            new ExtendedModuleActivator(false, configurationService, startupControlDao,
                dashboardRestService, dashboardRestService,
                bulkStartupTypeSetupService, wiretapTriggerSetupService);

        String moduleName = "moduleName";

        mockery.checking(new Expectations()
        {
            {
                //
                // deactivate

                // stop flows
                exactly(1).of(module).getFlows();
                will(returnValue(flowsPopulated));

                exactly(1).of(flow1).isRunning();
                will(returnValue(false));

                exactly(1).of(flow1).getName();
                will(returnValue("flowname1"));

                oneOf(flow1).stop();

                //
                // activate

                exactly(1).of(module).getName();
                will(returnValue(moduleName));
                exactly(1).of(wiretapTriggerSetupService).setup("moduleName");
                exactly(1).of(module).getName();
                will(returnValue(moduleName));
                exactly(1).of(bulkStartupTypeSetupService).deleteAllOnlyIfConfigured("moduleName");
                exactly(1).of(startupControlDao).getStartupControls(moduleName);
                will(returnValue(startupControls));
                exactly(1).of(bulkStartupTypeSetupService).setup(flowStartupControl);
                will(returnValue(flowStartupControl));
                oneOf(module).getName();
                will(returnValue(moduleName));

                // start flows
                exactly(1).of(module).getFlows();
                will(returnValue(flowsPopulated));

                exactly(1).of(module).getName();
                will(returnValue(moduleName));

                exactly(2).of(flow1).getName();
                will(returnValue("flowname1"));

                oneOf(startupControlDao).getStartupControl(moduleName, "flowname1");
                will(returnValue(flowStartupControl));

                oneOf(flowStartupControl).getStartupType();
                will(returnValue(StartupType.AUTOMATIC));

                exactly(1).of(flow1).getName();
                will(returnValue("flowname1"));

                exactly(1).of(module).getName();
                will(returnValue(moduleName));

                exactly(1).of(flow1).getName();
                will(returnValue("flowname1"));

                exactly(2).of(dashboardRestService).publish(module);

            }
        });

        moduleActivator.deactivate(module);
        moduleActivator.activate(module);
        Assert.assertTrue("module isActivated should return true", moduleActivator.isActivated(module) );
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_deactivate_activate_with_running_flow()
    {
        List flowsEmpty = new ArrayList();

        final List flowsPopulated = new ArrayList();
        flowsPopulated.add(flow1);

        final List startupControls = new ArrayList();

        moduleActivator =
            new ExtendedModuleActivator(false, configurationService, startupControlDao,
                dashboardRestService, dashboardRestService, bulkStartupTypeSetupService, wiretapTriggerSetupService);

        String moduleName = "moduleName";

        mockery.checking(new Expectations()
        {
            {
                //
                // deactivate

                // stop flows
                exactly(1).of(module).getFlows();
                will(returnValue(flowsPopulated));

                exactly(1).of(flow1).isRunning();
                will(returnValue(true));

                oneOf(flow1).stop();

                //
                // activate

                exactly(1).of(module).getName();
                will(returnValue(moduleName));
                exactly(1).of(wiretapTriggerSetupService).setup("moduleName");
                exactly(1).of(module).getName();
                will(returnValue(moduleName));
                exactly(1).of(bulkStartupTypeSetupService).deleteAllOnlyIfConfigured("moduleName");
                exactly(1).of(startupControlDao).getStartupControls(moduleName);
                will(returnValue(startupControls));
                exactly(1).of(bulkStartupTypeSetupService).setup(flowStartupControl);
                will(returnValue(flowStartupControl));

                oneOf(module).getName();
                will(returnValue(moduleName));

                // start flows
                exactly(1).of(module).getFlows();
                will(returnValue(flowsPopulated));

                exactly(1).of(module).getName();
                will(returnValue(moduleName));

                exactly(2).of(flow1).getName();
                will(returnValue("flowname1"));

                oneOf(startupControlDao).getStartupControl(moduleName, "flowname1");
                will(returnValue(flowStartupControl));

                oneOf(flowStartupControl).getStartupType();
                will(returnValue(StartupType.AUTOMATIC));

                exactly(1).of(flow1).getName();
                will(returnValue("flowname1"));

                exactly(1).of(flow1).start();

                exactly(2).of(dashboardRestService).publish(module);

            }
        });

        moduleActivator.deactivate(module);
        moduleActivator.activate(module);
        Assert.assertTrue("module isActivated should return true", moduleActivator.isActivated(module) );
        mockery.assertIsSatisfied();
    }

    private class ExtendedModuleActivator extends ModuleActivatorDefaultImpl
    {
        boolean configuredModule;

        /**
         * Constructor
         *
         * @param configurationService
         * @param startupControlDao
         * @param moduleMetadataDashboardRestService
         * @param configurationMetadataDashboardRestService
         */
        public ExtendedModuleActivator(boolean configuredModule, ConfigurationService configurationService, StartupControlDao startupControlDao,
                                       DashboardRestService moduleMetadataDashboardRestService,
                                       DashboardRestService configurationMetadataDashboardRestService,
                                       BulkStartupTypeSetupService bulkStartupTypeSetupService,
                                       WiretapTriggerSetupService wiretapTriggerSetupService)
        {
            super(configurationService, startupControlDao, moduleMetadataDashboardRestService,
                configurationMetadataDashboardRestService,bulkStartupTypeSetupService, wiretapTriggerSetupService );
            this.configuredModule = configuredModule;
        }

        @Override
        protected boolean isConfiguredResource(Module<Flow> module)
        {
            return configuredModule;
        }

        @Override
        protected boolean isFlowFactoryCapable(Module<Flow> module)
        {
            return true;
        }

        @Override
        protected ConfiguredResource<ConfiguredModuleConfiguration> getConfiguredResource(Module<Flow> module)
        {
            return configuredModuleConfiguration;
        }

        @Override
        protected FlowFactoryCapable getFlowFactoryCapable(Module<Flow> module)
        {
            return flowFactoryCapable;
        }
    }

    class ExtendedConfiguredResource implements ConfiguredResource<ConfiguredModuleConfiguration>
    {
        Map<String,String> flowDefinitions = new HashMap<String,String>();
        Map<String,String> flowDefinitionProfiles = new HashMap();
        ConfiguredModuleConfiguration configuredModuleConfiguration = new ConfiguredModuleConfiguration();

        String configuredResourceId = "configuredResourceId";

        @Override
        public ConfiguredModuleConfiguration getConfiguration()
        {
            flowDefinitions.put("flowname1", StartupType.AUTOMATIC.name());
            flowDefinitionProfiles.put("flowname1", "profile");

            configuredModuleConfiguration.setFlowDefinitions(this.flowDefinitions);
            configuredModuleConfiguration.setFlowDefinitionProfiles(this.flowDefinitionProfiles);
            return configuredModuleConfiguration;
        }

        @Override
        public void setConfiguration(ConfiguredModuleConfiguration configuration)
        {
            this.configuredModuleConfiguration = configuration;
        }

        @Override
        public String getConfiguredResourceId()
        {
            return this.configuredResourceId;
        }

        @Override
        public void setConfiguredResourceId(String id)
        {
            this.configuredResourceId = id;
        }
    }

}
