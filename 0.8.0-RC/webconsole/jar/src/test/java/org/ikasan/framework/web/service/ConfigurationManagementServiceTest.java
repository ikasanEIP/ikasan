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
package org.ikasan.framework.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.ikasan.framework.configuration.ConfiguredResource;
import org.ikasan.framework.configuration.model.Configuration;
import org.ikasan.framework.configuration.service.ConfigurationService;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.flow.FlowComponent;
import org.ikasan.framework.flow.FlowElement;
import org.ikasan.framework.flow.VisitingInvokerFlow;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.service.ModuleService;
import org.ikasan.framework.systemevent.service.SystemEventService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.security.Authentication;
import org.springframework.webflow.execution.RequestContext;

/**
 * Test class for {@link ConfigurationManagementService}
 * 
 * @author Ikasan Development Team
 *
 */
public class ConfigurationManagementServiceTest
{
    Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** mock configurationService */
    final ConfigurationService configurationService = mockery.mock(ConfigurationService.class, "mockConfigurationService");
    
    /** mock systemEventService */
    final SystemEventService systemEventService = mockery.mock(SystemEventService.class, "mockSystemEventService");
    
    /** mock moduleService */
    final ModuleService moduleService = mockery.mock(ModuleService.class, "mockModuleService");
    
    /** mock configuredResource */
    final ConfiguredResource configuredResource = mockery.mock(ConfiguredResource.class, "mockConfiguredResource");
    
    /** mock configuration */
    final Configuration configuration = mockery.mock(Configuration.class, "mockConfiguration");
    
    /** mock RequestContext */
    final RequestContext requestContext = mockery.mock(RequestContext.class, "mockRequestContext");
    
    /** mock Module */
    final Module module = mockery.mock(Module.class, "mockModule");
    
    /** mock Flow */
    final VisitingInvokerFlow flow = mockery.mock(VisitingInvokerFlow.class, "mockVisitingInvokerFlow");
    
    /** mock FlowElement */
    final FlowElement flowElement = mockery.mock(FlowElement.class, "mockFlowElement");
    
    /** mock FlowComponent */
    final FlowComponent flowComponent = mockery.mock(FlowComponent.class, "mockFlowComponent");
    
    /** mock authentication */
    final Authentication authentication = mockery.mock(Authentication.class, "mockAuthentication");
    
    /** instance on test */
    final ConfigurationManagementService configurationManagementService = 
        new ConfigurationManagementService(configurationService, systemEventService, moduleService);

    /**
     * Test failed constructor due to null configurationService.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructorDueToNullConfigurationService()
    {
        new ConfigurationManagementService(null, null, null);
    }

    /**
     * Test failed constructor due to null systemEventService.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructorDueToNullSystemEventService()
    {
        new ConfigurationManagementService(configurationService, null, null);
    }

    /**
     * Test failed constructor due to null moduleService.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructorDueToNullModuleService()
    {
        new ConfigurationManagementService(configurationService, systemEventService, null);
    }

    /**
     * Test successful findConfiguration invocation.
     */
    @Test 
    public void test_successful_findConfiguration()
    {
        final Map<String,Flow> flows = new HashMap<String,Flow>();
        flows.put("flowName", flow);
        
        final List<FlowElement> flowElements = new ArrayList<FlowElement>();
        flowElements.add(flowElement);

        // dummy flow/configuredResource component for testing
        final TestComponent testComponent = new TestComponent();
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the module
                exactly(1).of(moduleService).getModule("moduleName");
                will(returnValue(module));
                
                // get the flow from the module
                one(module).getFlows();
                will(returnValue(flows));
                
                // get the flowElements from the flow
                one(flow).getFlowElements();
                will(returnValue(flowElements));

                // this is this the flowElement name we are looking for 
                one(flowElement).getComponentName();
                will(returnValue("flowElementName"));
                
                // return the component in this flowElement
                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(testComponent));
                
                // find the configuration for this component
                one(configurationService).getConfiguration(testComponent);
                will(returnValue(configuration));
            }
        });
        
        // run the test
        configurationManagementService.findConfiguration("moduleName", "flowName", "flowElementName", requestContext);
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed findConfiguration invocation due to component not being found.
     */
    @Test 
    public void test_failed_findConfiguration_due_to_componentNotFound()
    {
        final Map<String,Flow> flows = new HashMap<String,Flow>();
        flows.put("flowName", flow);
        
        final List<FlowElement> flowElements = new ArrayList<FlowElement>();
        flowElements.add(flowElement);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the module
                exactly(1).of(moduleService).getModule("moduleName");
                will(returnValue(module));
                
                // get the flow from the module
                one(module).getFlows();
                will(returnValue(flows));
                
                // get the flowElements from the flow
                one(flow).getFlowElements();
                will(returnValue(flowElements));

                // this is this the flowElement name we are looking for 
                one(flowElement).getComponentName();
                will(returnValue("unmatchedFlowElementName"));
                
                // return the component in this flowElement
                exactly(1).of(requestContext).getMessageContext();
            }
        });
        
        // run the test
        Assert.assertNull(configurationManagementService.findConfiguration("moduleName", "flowName", "flowElementName", requestContext));
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful create configuration.
     */
    @Test 
    public void test_successful_createConfiguration()
    {
        final Map<String,Flow> flows = new HashMap<String,Flow>();
        flows.put("flowName", flow);
        
        final List<FlowElement> flowElements = new ArrayList<FlowElement>();
        flowElements.add(flowElement);
        
        // dummy flow/configuredResource component for testing
        final TestComponent testComponent = new TestComponent();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the module
                exactly(1).of(moduleService).getModule("moduleName");
                will(returnValue(module));
                
                // get the flow from the module
                one(module).getFlows();
                will(returnValue(flows));
                
                // get the flowElements from the flow
                one(flow).getFlowElements();
                will(returnValue(flowElements));
                
                // this is this the flowElement name we are looking for 
                one(flowElement).getComponentName();
                will(returnValue("flowElementName"));
                
                // return the component in this flowElement
                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(testComponent));

                // create configuration via the configuration service
                exactly(1).of(configurationService).createConfiguration(testComponent);
                will(returnValue(configuration));
            }
        });
        
        // run the test
        configurationManagementService.createConfiguration("moduleName", "flowName", "flowElementName", requestContext);
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed create configuration.
     */
    @Test 
    public void test_failed_createConfiguration()
    {
        final Map<String,Flow> flows = new HashMap<String,Flow>();
        flows.put("flowName", flow);
        
        final List<FlowElement> flowElements = new ArrayList<FlowElement>();
        flowElements.add(flowElement);
        
        // dummy flow/configuredResource component for testing
        final TestComponent testComponent = new TestComponent();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the module
                exactly(1).of(moduleService).getModule("moduleName");
                will(returnValue(module));
                
                // get the flow from the module
                one(module).getFlows();
                will(returnValue(flows));
                
                // get the flowElements from the flow
                one(flow).getFlowElements();
                will(returnValue(flowElements));
                
                // this is this the flowElement name we are looking for 
                one(flowElement).getComponentName();
                will(returnValue("flowElementName"));
                
                // return the component in this flowElement
                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(testComponent));

                // create configuration via the configuration service
                exactly(1).of(configurationService).createConfiguration(testComponent);
                will(throwException(new RuntimeException("Failed for testing")));
                
                // return the component in this flowElement
                exactly(1).of(requestContext).getMessageContext();
            }
        });
        
        // run the test
        Assert.assertNull(configurationManagementService.createConfiguration("moduleName", "flowName", "flowElementName", requestContext));
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful insert configuration.
     */
    @Test 
    public void test_successful_insertConfiguration()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(authentication).getName();
                will(returnValue("prinicpalOfInvokingUser"));
                
                exactly(1).of(configuration).getConfigurationId();
                will(returnValue("configurationId"));
                
                exactly(1).of(systemEventService).logSystemEvent("configurationId", "Configuration created", "prinicpalOfInvokingUser");
                exactly(1).of(configurationService).saveConfiguration(configuration);
            }
        });
        
        // run the test
        ConfigurationManagementService testConfigurationManagementService = new TestConfigurationManagementService(configurationService, systemEventService, moduleService);
        testConfigurationManagementService.insertConfiguration(configuration);
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful update configuration.
     */
    @Test 
    public void test_successful_updateConfiguration()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(authentication).getName();
                will(returnValue("prinicpalOfInvokingUser"));
                
                exactly(1).of(configuration).getConfigurationId();
                will(returnValue("configurationId"));
                
                exactly(1).of(systemEventService).logSystemEvent("configurationId", "Configuration updated", "prinicpalOfInvokingUser");
                exactly(1).of(configurationService).saveConfiguration(configuration);
            }
        });
        
        // run the test
        ConfigurationManagementService testConfigurationManagementService = new TestConfigurationManagementService(configurationService, systemEventService, moduleService);
        testConfigurationManagementService.updateConfiguration(configuration);
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test successful delete configuration.
     */
    @Test 
    public void test_successful_deleteConfiguration()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(authentication).getName();
                will(returnValue("prinicpalOfInvokingUser"));
                
                exactly(1).of(configuration).getConfigurationId();
                will(returnValue("configurationId"));
                
                exactly(1).of(systemEventService).logSystemEvent("configurationId", "Configuration deleted", "prinicpalOfInvokingUser");
                exactly(1).of(configurationService).deleteConfiguration(configuration);
            }
        });
        
        // run the test
        ConfigurationManagementService testConfigurationManagementService = new TestConfigurationManagementService(configurationService, systemEventService, moduleService);
        testConfigurationManagementService.deleteConfiguration(configuration);
        mockery.assertIsSatisfied();
    }

    private class TestConfigurationManagementService extends ConfigurationManagementService
    {

        /**
         * @param configurationDao
         * @param systemEventService
         * @param moduleService
         */
        public TestConfigurationManagementService(
                ConfigurationService configurationService,
                SystemEventService systemEventService,
                ModuleService moduleService)
        {
            super(configurationService, systemEventService, moduleService);
        }
     
        @Override
        protected Authentication getAuthentication()
        {
            return authentication;
        }
    }
    
    private class TestComponent implements FlowComponent, ConfiguredResource<String>
    {

        /* (non-Javadoc)
         * @see org.ikasan.framework.configuration.ConfiguredResource#getConfiguration()
         */
        public String getConfiguration()
        {
            return "configuration";
        }

        /* (non-Javadoc)
         * @see org.ikasan.framework.configuration.ConfiguredResource#getConfiguredResourceId()
         */
        public String getConfiguredResourceId()
        {
            return "configuredResourceId";
        }

        /* (non-Javadoc)
         * @see org.ikasan.framework.configuration.ConfiguredResource#setConfiguration(java.lang.Object)
         */
        public void setConfiguration(String configuration)
        {
            // dont care - this is just for test
        }

        /* (non-Javadoc)
         * @see org.ikasan.framework.configuration.ConfiguredResource#setConfiguredResourceId(java.lang.String)
         */
        public void setConfiguredResourceId(String id)
        {
            // dont care - this is just for test
        }
        
    }
}
