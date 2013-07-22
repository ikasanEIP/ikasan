 /* 
 * $Id: VisitingInvokerFlowTest.java 3183 2010-09-16 06:28:36Z mitcje $
 * $URL: https://open.jira.com/svn/IKASAN/trunk/ikasaneip/framework/src/test/java/org/ikasan/framework/flow/VisitingInvokerFlowTest.java $
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
package org.ikasan.framework.flow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.configuration.ConfiguredResource;
import org.ikasan.framework.configuration.DynamicConfiguredResource;
import org.ikasan.framework.configuration.model.Configuration;
import org.ikasan.framework.configuration.service.ConfigurationException;
import org.ikasan.framework.configuration.service.ConfigurationService;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.flow.invoker.FlowElementInvoker;
import org.ikasan.framework.flow.invoker.FlowInvocationContext;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ikasan Development Team
 *
 */
public class VisitingInvokerFlowTest
{
    /**
     * Mockery for interfaces
     */
//    private Mockery mockery = new Mockery();
    /**
     * Mockery for classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    /**
     * Mocked Event object
     */
    Event event = mockery.mock(Event.class);
    
    /**
     * Mocked invoker
     */
    FlowElementInvoker flowElementInvoker = mockery.mock(FlowElementInvoker.class);
    
    /**
     * Mocked configuration service
     */
    @SuppressWarnings("unchecked")
    ConfigurationService configurationService = mockery.mock(ConfigurationService.class);
    
    /**
     * Mocked element for the head
     */
    FlowElement flowElement = mockery.mock(FlowElement.class);
    
    /**
     * Name of this flow
     */
    String flowName = "flowName";
    
    String moduleName = "moduleName";
    
    FlowInvocationContext flowInvocationContext = new FlowInvocationContext();
    

    /**
     * Class under test
     */
    private VisitingInvokerFlow visitingInvokerFlow = new VisitingInvokerFlow(flowName, moduleName, flowElement, flowElementInvoker);
    
    /**
     * Mocked Exeption Action
     */
    IkasanExceptionAction ikasanExceptionAction = mockery.mock(IkasanExceptionAction.class);
    
    
    
    /**
     * Test method for {@link org.ikasan.framework.flow.VisitingInvokerFlow#invoke(org.ikasan.framework.component.Event)}.
     */
    @Test
    public void testInvoke()
    {
        mockery.checking(new Expectations()
        {
            {
                one(flowElementInvoker).invoke(flowInvocationContext, event, moduleName, flowName, flowElement);
            }
        });
        
        
        visitingInvokerFlow.invoke(flowInvocationContext, event);
    }
    
    /**
     * Test method for {@link org.ikasan.framework.flow.VisitingInvokerFlow#start()}.
     */
    @Test
    public void test_start_with_ManagedResources()
    {
        final Map<String,FlowElement> transitions = new HashMap<String,FlowElement>();
        transitions.put("key", flowElement);
        
        final TestManagedResourceFlowComponent testManagedFlowComponent = mockery.mock(TestManagedResourceFlowComponent.class, "mockedManagedResourceFlowComponent");
        
        mockery.checking(new Expectations()
        {
            {
                one(flowElement).getTransitions();
                will(returnValue(transitions));
                
                one(flowElement).getFlowComponent();
                will(returnValue(testManagedFlowComponent));
                
                one(testManagedFlowComponent).startManagedResource();
            }
        });
        
        visitingInvokerFlow.start();
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test method for {@link org.ikasan.framework.flow.VisitingInvokerFlow#start()}.
     */
    @Test
    public void test_start_with_ConfiguredResources()
    {
        final Map<String,FlowElement> transitions = new HashMap<String,FlowElement>();
        transitions.put("key", flowElement);
        
        final TestConfiguredResourceFlowComponent testConfiguredFlowComponent = mockery.mock(TestConfiguredResourceFlowComponent.class, "mockedConfiguredResourceFlowComponent");
        
        final Configuration configuration = mockery.mock(Configuration.class, "mockedConfiguration");
        
        mockery.checking(new Expectations()
        {
            {
                one(flowElement).getTransitions();
                will(returnValue(transitions));
                
                one(flowElement).getFlowComponent();
                will(returnValue(testConfiguredFlowComponent));
                
                one(configurationService).configure(testConfiguredFlowComponent);
            }
        });
        
        visitingInvokerFlow.setConfigurationService(configurationService);
        visitingInvokerFlow.start();
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test method for {@link org.ikasan.framework.flow.VisitingInvokerFlow#start()}.
     */
    @Test
    public void test_start_with_DynamicConfiguredResources()
    {
        final Map<String,FlowElement> transitions = new HashMap<String,FlowElement>();
        transitions.put("key", flowElement);
        
        final TestDynamicConfiguredResourceFlowComponent testDynamicConfiguredFlowComponent = 
            mockery.mock(TestDynamicConfiguredResourceFlowComponent.class, "mockedDynamicConfiguredResourceFlowComponent");
        
        final Configuration configuration = mockery.mock(Configuration.class, "mockedConfiguration");
        
        mockery.checking(new Expectations()
        {
            {
                one(flowElement).getTransitions();
                will(returnValue(transitions));
                
                one(flowElement).getFlowComponent();
                will(returnValue(testDynamicConfiguredFlowComponent));
                
                one(configurationService).configure(testDynamicConfiguredFlowComponent);
            }
        });
        
        visitingInvokerFlow.setConfigurationService(configurationService);
        visitingInvokerFlow.start();
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test method for {@link org.ikasan.framework.flow.VisitingInvokerFlow#start()}.
     */
    @Test(expected = ConfigurationException.class)
    public void test_failed_start_with_ConfiguredResources_without_a_ConfigurationService()
    {
        final Map<String,FlowElement> transitions = new HashMap<String,FlowElement>();
        transitions.put("key", flowElement);
        
        final TestConfiguredResourceFlowComponent testConfiguredFlowComponent = mockery.mock(TestConfiguredResourceFlowComponent.class, "mockedConfiguredResourceFlowComponent");
        
        mockery.checking(new Expectations()
        {
            {
                one(flowElement).getTransitions();
                will(returnValue(transitions));
                
                one(flowElement).getFlowComponent();
                will(returnValue(testConfiguredFlowComponent));

                one(flowElement).getComponentName();
                will(returnValue("componentName"));
            }
        });
        
        visitingInvokerFlow.start();
        mockery.assertIsSatisfied();
    }
    
    /**
     * Creates a sufficiently complex graph of flow elements and tests that the getFlowElements
     * method returns a listing of all the elements discovered in a breadth first search
     */
    @Test
    public void testGetFlowElements(){
    	final FlowElement flowElementA = mockery.mock(FlowElement.class, "flowElementA");
    	final FlowElement flowElementB = mockery.mock(FlowElement.class, "flowElementB");
    	final FlowElement flowElementC = mockery.mock(FlowElement.class, "flowElementC");
    	final FlowElement flowElementD = mockery.mock(FlowElement.class, "flowElementD");
    	final FlowElement flowElementE = mockery.mock(FlowElement.class, "flowElementE");
    	final FlowElement flowElementF = mockery.mock(FlowElement.class, "flowElementF");
    	
    	final Map<String, FlowElement> flowElementATransitions = new HashMap<String, FlowElement>();
    	flowElementATransitions.put(FlowElement.DEFAULT_TRANSITION_NAME,flowElementB );
    	
    	final Map<String, FlowElement> flowElementBTransitions = new HashMap<String, FlowElement>();
    	flowElementBTransitions.put("transitionToC",flowElementC );
    	flowElementBTransitions.put("transitionToD",flowElementD );

    	final Map<String, FlowElement> flowElementCTransitions = new HashMap<String, FlowElement>();
    	flowElementCTransitions.put(FlowElement.DEFAULT_TRANSITION_NAME,flowElementE );

    	final Map<String, FlowElement> flowElementDTransitions = new HashMap<String, FlowElement>();
    	flowElementDTransitions.put(FlowElement.DEFAULT_TRANSITION_NAME,flowElementF );

    	final Map<String, FlowElement> flowElementETransitions = new HashMap<String, FlowElement>();

    	final Map<String, FlowElement> flowElementFTransitions = new HashMap<String, FlowElement>();
    	flowElementFTransitions.put(FlowElement.DEFAULT_TRANSITION_NAME,flowElementB );
    	
        mockery.checking(new Expectations()
        {
            {
                one(flowElementA).getTransitions();
                will(returnValue(flowElementATransitions));
                
                one(flowElementB).getTransitions();
                will(returnValue(flowElementBTransitions));
                
                one(flowElementC).getTransitions();
                will(returnValue(flowElementCTransitions));
                
                one(flowElementD).getTransitions();
                will(returnValue(flowElementDTransitions));
                
                one(flowElementE).getTransitions();
                will(returnValue(flowElementETransitions));
                
                one(flowElementF).getTransitions();
                will(returnValue(flowElementFTransitions));
            }
        });
    	
        VisitingInvokerFlow visitingInvokerFlow = new VisitingInvokerFlow("flowname", "moduleName", flowElementA, null);
    	
        List<FlowElement> flowElements = visitingInvokerFlow.getFlowElements();
        
        Assert.assertEquals("each flowElement existing in the listing once only", 6, flowElements.size());
    	
        Assert.assertEquals("first Element is A", flowElementA, flowElements.get(0));
        Assert.assertEquals("first Element is B", flowElementB, flowElements.get(1));
        Assert.assertEquals("first Element is C", flowElementC, flowElements.get(2));
        Assert.assertEquals("first Element is D", flowElementD, flowElements.get(3));
        Assert.assertEquals("first Element is E", flowElementE, flowElements.get(4));
        Assert.assertEquals("first Element is F", flowElementF, flowElements.get(5));
        
    }
    
    /**
     * Test component which implements the ManagedResource contract.
     * @author Ikasan Development Team
     *
     */
    private class TestManagedResourceFlowComponent implements FlowComponent, ManagedResource
    {

        public void startManagedResource()
        {
            // test purposes only - ignore implementation
        }

        public void stopManagedResource()
        {
            // test purposes only - ignore implementation
        }
        
    }

    /**
     * Test component which implements the ConfiguredResource contract.
     * @author Ikasan Development Team
     *
     */
    private class TestConfiguredResourceFlowComponent implements FlowComponent, ConfiguredResource<Configuration>
    {
        private String configuredResourceId;
        
        public String getConfiguredResourceId()
        {
            // test purposes only - ignore implementation
            return configuredResourceId;
        }
        
        public void setConfiguration(Configuration configuration)
        {
            // test purposes only - ignore implementation
        }

        public Configuration getConfiguration()
        {
            // test purposes only - ignore implementation
            return null;
        }

        public void setConfiguredResourceId(String configuredResourceId)
        {
            // test purposes only - ignore implementation
            this.configuredResourceId = configuredResourceId;
        }
        
    }

    /**
     * Test component which implements the ConfiguredResource contract.
     * @author Ikasan Development Team
     *
     */
    private class TestDynamicConfiguredResourceFlowComponent implements FlowComponent, DynamicConfiguredResource<Configuration>
    {
        private String configuredResourceId;
        
        public String getConfiguredResourceId()
        {
            // test purposes only - ignore implementation
            return configuredResourceId;
        }
        
        public void setConfiguration(Configuration configuration)
        {
            // test purposes only - ignore implementation
        }

        public Configuration getConfiguration()
        {
            // test purposes only - ignore implementation
            return null;
        }

        public void setConfiguredResourceId(String configuredResourceId)
        {
            // test purposes only - ignore implementation
            this.configuredResourceId = configuredResourceId;
        }
        
    }
}
