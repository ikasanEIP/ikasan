/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 *
 * Copyright (c) 2000-20010 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.flow.visitorPattern;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.DynamicConfiguredResource;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.recovery.RecoveryManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>VisitingInvokerFlow</code> class.
 * 
 * @author Ikasan Development Team
 */
public class VisitingInvokerFlowTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery();

    /** Mock flowConfiguration */
    final FlowConfiguration flowConfiguration = mockery.mock(FlowConfiguration.class, "mockFlowConfiguration");

    /** Mock flowElementInvoker */
    final FlowElementInvoker flowElementInvoker = mockery.mock(FlowElementInvoker.class, "mockFlowElementInvoker");

    /** Mock recoveryManager */
    final RecoveryManager recoveryManager = mockery.mock(RecoveryManager.class, "mockRecoveryManager");

    /** Mock list of configured resource flow elements */
    final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements
        = mockery.mock(List.class, "mockFlowElementConfiguredResources");

    /** Mock configured resource flow elements */
    final FlowElement<ConfiguredResource> configuredResourceFlowElement
        = mockery.mock(FlowElement.class, "mockFlowElementConfiguredResource");

    /** Mock list of dynamic configured resource flow elements */
    final List<FlowElement<DynamicConfiguredResource>> dynamicConfiguredResourceFlowElements
        = mockery.mock(List.class, "mockFlowElementDynamicConfiguredResources");

    /** Mock dynamic configured resource flow elements */
    final FlowElement<DynamicConfiguredResource> dynamicConfiguredResourceFlowElement
        = mockery.mock(FlowElement.class, "mockFlowElementDynamicConfiguredResource");

    /** Mock managed resource flow element 1 */
    final FlowElement<ManagedResource> managedResourceFlowElement1
        = mockery.mock(FlowElement.class, "mockFlowElementManagedResource1");
    
    /** Mock managed resource flow element 2 */
    final FlowElement<ManagedResource> managedResourceFlowElement2
    = mockery.mock(FlowElement.class, "mockFlowElementManagedResource2");

    /** Mock managed resource flow element 3 */
    final FlowElement<ManagedResource> managedResourceFlowElement3
    = mockery.mock(FlowElement.class, "mockFlowElementManagedResource3");

    /** Mock lead flow element */
    final FlowElement leadFlowElement = mockery.mock(FlowElement.class, "mockLeadFlowElement");

    /** mock managed resource */
    final ManagedResource managedResource = mockery.mock(ManagedResource.class, "mockManagedResource");

    /** Mock consumer flowElement */
    final FlowElement<Consumer<EventListener<FlowEvent<?,?>>>> consumerFlowElement 
        = mockery.mock(FlowElement.class, "mockFlowElement");

    /** Mock consumer */
    final Consumer<EventListener<FlowEvent<?,?>>> consumer = mockery.mock(Consumer.class, "mockConsumer");
    
    /** Mock flow event */
    final FlowEvent flowEvent = mockery.mock(FlowEvent.class, "mockFlowEvent");

    /** Mock monitor */
    final Monitor monitor = mockery.mock(Monitor.class, "mockMonitor");
    
    /** Mock flow invocation context */
    final FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "mockFlowInvocationContext");

    /** is recovering status */
    boolean isRecovering = false;

    /** is running status */
    boolean isRunning = false;
    
    /** is unrecoverable status */
    boolean isUnrecoverable = false;

    @Before
    public void setup()
    {
        isRecovering = false;
        isRunning = false;
        isUnrecoverable = false;
    }
    
	/**
     * Test failed constructor due to null flow name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullName()
    {
        new VisitingInvokerFlow(null, null, null, null, null);
    }

    /**
     * Test failed constructor due to null module name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullModuleName()
    {
        new VisitingInvokerFlow("flowName", null, null, null, null);
    }

    /**
     * Test failed constructor due to null flow configuration.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullFlowConfiguration()
    {
        new VisitingInvokerFlow("flowName", "moduleName", null, null, null);
    }

    /**
     * Test failed constructor due to null flow element invoker.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullFlowElementInvoker()
    {
        new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration, null, null);
    }

    /**
     * Test failed constructor due to null flow recovery manager.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullFlowRecoveryManager()
    {
        new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration, flowElementInvoker, null);
    }

    /**
     * Test successful visiting flow invoker instantiation.
     */
    @Test
    public void test_successful_VisitingInvokerFlow_instantiation()
    {
        Flow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration, flowElementInvoker, recoveryManager);
        Assert.assertEquals("flowName setter failed", "flowName", flow.getName());
        Assert.assertEquals("moduleName setter failed", "moduleName", flow.getModuleName());
    }

    /**
     * Test successful flow start from a stopped state.
     */
    @Test
    public void test_successful_flow_start_from_stopped()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<FlowElement<ManagedResource>>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);
        
        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the two flow element configured resources
                one(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                one(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load configuration
                exactly(2).of(flowConfiguration).configureFlowElement(configuredResourceFlowElement);
                
                // get the three flow element managed resources
                one(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));
                
                // start each managed resource from right to left (reverse order) in flow order
                one(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).startManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).startManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).startManagedResource();
                inSequence(reverseOrder);
                
                // get the consumer
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));
                
                // set listener for tech callbacks
                exactly(1).of(consumer).setListener(flow);
                
                // start the consumer
                exactly(1).of(consumer).start();
            }
        });

        // set the monitor and receive initial state callback
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // check state before proceeding with start
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);

        // start will result in the monitor being updated to running
        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.start();

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow start from a stoppedInError state.
     */
    @Test
    public void test_successful_flow_start_from_stoppedInError()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<FlowElement<ManagedResource>>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);
        
        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the two flow element configured resources
                one(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                one(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load configuration
                exactly(2).of(flowConfiguration).configureFlowElement(configuredResourceFlowElement);
                
                // get the three flow element managed resources
                one(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));
                
                // start each managed resource from right to left (reverse order) in flow order
                one(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).startManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).startManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).startManagedResource();
                inSequence(reverseOrder);
                
                // get the consumer
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));
                
                // set listener for tech callbacks
                exactly(1).of(consumer).setListener(flow);
                
                // start the consumer
                exactly(1).of(consumer).start();
            }
        });

        // set the monitor and receive initial state callback
        isUnrecoverable = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stoppedInError");
        flow.setMonitor(monitor);

        // check state before proceeding with start
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);

        // start will result in the monitor being updated to running
        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.start();

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed flow start due to consumer already running.
     */
    @Test
    public void test_failed_flow_start_due_to_consumer_already_running()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<FlowElement<ManagedResource>>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);

        final RuntimeException exception = new RuntimeException("test consumer failing to start");
        
        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the consumer flow element for logging its name
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));
            }
        });

        // set the monitor and receive initial state callback
        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // check state before proceeding with start
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);

        // start will bail out due to flow already running
        flow.start();

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed flow start due to consumer already running in recovery.
     */
    @Test
    public void test_failed_flow_start_due_to_consumer_already_running_in_recovery()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<FlowElement<ManagedResource>>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);

        final RuntimeException exception = new RuntimeException("test consumer failing to start");
        
        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the consumer flow element for logging its name
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));
            }
        });

        // set the monitor and receive initial state callback
        isRecovering = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("recovering");
        flow.setMonitor(monitor);

        // check state before proceeding with start
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);

        // start will bail out due to flow already running in recovery
        flow.start();

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed flow start due to configuration failure.
     */
    @Test
    public void test_failed_flow_start_due_to_configuration_failure()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        final RuntimeException exception = new RuntimeException("test configuration failing");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get consumer flow element
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                
                // get the flow element configured resources
                one(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                one(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement));

                // load configuration
                exactly(1).of(flowConfiguration).configureFlowElement(configuredResourceFlowElement);
                will(throwException(exception));
            }
        });

        // set the monitor and receive initial state callback
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // check state before proceeding with start
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);

        // this start will result in the monitor being updated to unrecoverable
        setGetStateExpectations(isRecovering, isRunning);
        setMonitorExpectations("stoppedInError");
        flow.start();

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow start regardless of a managed resource successfully
     * starting. We ignore managed resource start failures as these could 
     * resolve themselves later in the flow invocation.
     */
    @Test
    public void test_success_flow_start_even_with_managedResource_start_failure()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<FlowElement<ManagedResource>>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        
        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the two flow element configured resources
                one(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                one(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load configuration
                exactly(2).of(flowConfiguration).configureFlowElement(configuredResourceFlowElement);
                
                // get the the flow element managed resource
                one(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));
                
                // start each managed resource from right to left (reverse order) in flow order
                one(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).startManagedResource();
                will(throwException(new RuntimeException("test managed resource start failure")));
                one(managedResourceFlowElement1).getComponentName();
                will(returnValue("managedResourceFlowElementName"));
                inSequence(reverseOrder);
                
                // get the consumer
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));
                
                // set listener for tech callbacks
                exactly(1).of(consumer).setListener(flow);
                
                // start the consumer
                exactly(1).of(consumer).start();
            }
        });

        // set the monitor and receive initial state callback
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // check state before proceeding with start
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);

        // start will result in the monitor being updated to running
        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.start();

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed flow start due to consumer failing to start, but activating recovery.
     */
    @Test
    public void test_failed_flow_start_due_to_recoverable_consumer_failure()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<FlowElement<ManagedResource>>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);

        final RuntimeException exception = new RuntimeException("test consumer failing to start");
        
        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the two flow element configured resources
                one(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                one(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load configuration
                exactly(2).of(flowConfiguration).configureFlowElement(configuredResourceFlowElement);
                
                // get the three flow element managed resources
                one(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));
                
                // start each managed resource from right to left in flow order
                one(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).startManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).startManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).startManagedResource();
                inSequence(reverseOrder);
                
                // get the consumer
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));
                
                // set listener for tech callbacks
                exactly(1).of(consumer).setListener(flow);
                
                // start the consumer
                exactly(1).of(consumer).start();
                will(throwException(exception));

                // recovery manager invocation
                exactly(1).of(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));
                exactly(1).of(recoveryManager).recover("consumerName", exception);
            }
        });

        // set the monitor and receive initial state callback
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // check state before proceeding with start
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);

        // this start will result in the monitor being updated to unrecoverable
        isRecovering = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("recovering");
        flow.start();

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed flow start due to consumer failing to start.
     */
    @Test
    public void test_failed_flow_start_due_to_unrecoverable_consumer_failure()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<FlowElement<ManagedResource>>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);

        final RuntimeException exception = new RuntimeException("test consumer failing to start");
        
        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the two flow element configured resources
                one(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                one(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load configuration
                exactly(2).of(flowConfiguration).configureFlowElement(configuredResourceFlowElement);
                
                // get the three flow element managed resources
                one(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));
                
                // start each managed resource from right to left in flow order
                one(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).startManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).startManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).startManagedResource();
                inSequence(reverseOrder);
                
                // get the consumer
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));
                
                // set listener for tech callbacks
                exactly(1).of(consumer).setListener(flow);
                
                // start the consumer
                exactly(1).of(consumer).start();
                will(throwException(exception));

                // recovery manager invocation
                exactly(1).of(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));
                exactly(1).of(recoveryManager).recover("consumerName", exception);
            }
        });

        // set the monitor and receive initial state callback
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // check state before proceeding with start
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);

        // this start will result in the monitor being updated to unrecoverable
        isUnrecoverable = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stoppedInError");
        flow.start();

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow stop on flow which is running.
     */
    @Test
    public void test_successful_flow_stop_whilst_running()
    {
        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<FlowElement<ManagedResource>>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);
        
        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always check to see if recovery is in progress
                // in this test case it isn't
                one(recoveryManager).isRecovering();
                will(returnValue(false));
                
                // get the consumer
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));
                
                // nullify the listener for tech callbacks
                exactly(1).of(consumer).setListener(null);
                
                // stop the consumer
                exactly(1).of(consumer).stop();
                
                // get the three flow element managed resources
                one(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));
                
                // stop each managed resource from left to right in flow order
                one(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).stopManagedResource();
                inSequence(reverseOrder);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        // set the monitor
        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        isRunning = false;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stopped");
        flow.stop();

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow stop on flow which is in recovery.
     */
    @Test
    public void test_successful_flow_stop_whilst_recovering()
    {
        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<FlowElement<ManagedResource>>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);
        
        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always check to see if recovery is in progress
                // in this test case it isn't
                one(recoveryManager).isRecovering();
                will(returnValue(false));
                
                // get the consumer
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));
                
                // nullify the listener for tech callbacks
                exactly(1).of(consumer).setListener(null);
                
                // stop the consumer
                exactly(1).of(consumer).stop();
                
                // get the three flow element managed resources
                one(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));
                
                // stop each managed resource from left to right in flow order
                one(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).stopManagedResource();
                inSequence(reverseOrder);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        // set the monitor
        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        isRunning = false;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stopped");
        flow.stop();

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow stop on flow which is already stopped. 
     * This should go through the motions as there is no issue in 
     * stopping a stopped flow.
     */
    @Test
    public void test_successful_flow_stop_whilst_stopped()
    {
        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<FlowElement<ManagedResource>>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);
        
        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always check to see if recovery is in progress
                // in this test case it isn't
                one(recoveryManager).isRecovering();
                will(returnValue(false));
                
                // get the consumer
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));
                
                // nullify the listener for tech callbacks
                exactly(1).of(consumer).setListener(null);
                
                // stop the consumer
                exactly(1).of(consumer).stop();
                
                // get the three flow element managed resources
                one(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));
                
                // stop each managed resource from left to right in flow order
                one(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).stopManagedResource();
                inSequence(reverseOrder);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        // set the monitor
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stopped");
        flow.stop();

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow stop on flow which is already stoppedInError. 
     * This should go through the motions as there is no issue in 
     * stopping a stoppedInError flow.
     */
    @Test
    public void test_successful_flow_stop_whilst_stoppedInError()
    {
        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<FlowElement<ManagedResource>>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);
        
        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always check to see if recovery is in progress
                // in this test case it isn't
                one(recoveryManager).isRecovering();
                will(returnValue(false));
                
                // get the consumer
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));
                
                // nullify the listener for tech callbacks
                exactly(1).of(consumer).setListener(null);
                
                // stop the consumer
                exactly(1).of(consumer).stop();
                
                // get the three flow element managed resources
                one(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));
                
                // stop each managed resource from left to right in flow order
                one(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                one(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                one(managedResource).stopManagedResource();
                inSequence(reverseOrder);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        // set the monitor
        isUnrecoverable = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stoppedInError");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stoppedInError");
        flow.stop();

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow invoke with a flow event.
     */
    @Test
    public void test_successful_flow_invoke_with_flowEvent()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // create context
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                one(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));
                one(flowInvocationContext).addInvokedComponentName("consumerName");
                
                // reload any marked dynamic configuration
                one(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                one(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement, dynamicConfiguredResourceFlowElement));
                exactly(2).of(flowConfiguration).configureFlowElement(dynamicConfiguredResourceFlowElement);

                one(flowConfiguration).getLeadFlowElement();
                will(returnValue(leadFlowElement));
                one(flowElementInvoker).invoke(flowInvocationContext, flowEvent, leadFlowElement);
                
                // in this test we do not need to cancel recovery
                one(recoveryManager).isRecovering();
                will(returnValue(false));
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.invoke(flowEvent);

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed flow invoke with a flow event, but dynamic configuration failing.
     */
    @Test
    public void test_failed_flow_invoke_with_flowEvent_stoppingInError_due_to_dynamicConfiguration_failure()
    {
        final RuntimeException exception = new RuntimeException("test failed dynamic configuration");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // create context
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                one(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));
                one(flowInvocationContext).addInvokedComponentName("consumerName");
                
                // reload any marked dynamic configuration
                one(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                one(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement));
                exactly(1).of(flowConfiguration).configureFlowElement(dynamicConfiguredResourceFlowElement);
                will(throwException(exception));
                
                // add failed flow element name to the context
                one(dynamicConfiguredResourceFlowElement).getComponentName();
                will(returnValue("dynamicComponentName"));
                one(flowInvocationContext).addInvokedComponentName("dynamicComponentName");
                
                // pass the exception to the recovery manager
                one(flowInvocationContext).getLastComponentName();
                will(returnValue("dynamicComponentName"));
                one(recoveryManager).recover("dynamicComponentName", exception, flowEvent);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        isUnrecoverable = true;
        isRunning = false;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stoppedInError");
        flow.invoke(flowEvent);

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed flow invoke with a flow event with the resulting outcome being stoppedInError.
     */
    @Test
    public void test_failed_flow_invoke_with_flowEvent_resulting_in_stoppedInError()
    {
        final RuntimeException exception = new RuntimeException("test failed flow invocation");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // create context
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                one(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));
                one(flowInvocationContext).addInvokedComponentName("consumerName");

                // reload any marked dynamic configuration
                one(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                one(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement));
                exactly(1).of(flowConfiguration).configureFlowElement(dynamicConfiguredResourceFlowElement);

                // invoke the flow elements
                one(flowConfiguration).getLeadFlowElement();
                will(returnValue(leadFlowElement));
                one(flowElementInvoker).invoke(flowInvocationContext, flowEvent, leadFlowElement);
                will(throwException(exception));
                
                // pass the exception to the recovery manager
                one(flowInvocationContext).getLastComponentName();
                will(returnValue("componentName"));
                one(recoveryManager).recover("componentName", exception, flowEvent);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        isUnrecoverable = true;
        isRunning = false;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stoppedInError");
        flow.invoke(flowEvent);

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed flow invoke with a flow event with the resulting outcome being recovering.
     */
    @Test
    public void test_failed_flow_invoke_with_flowEvent_resulting_in_recovery()
    {
        final RuntimeException exception = new RuntimeException("test failed flow invocation");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // create context
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                one(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));
                one(flowInvocationContext).addInvokedComponentName("consumerName");

                // reload any marked dynamic configuration
                one(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                one(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement));
                exactly(1).of(flowConfiguration).configureFlowElement(dynamicConfiguredResourceFlowElement);

                // invoke the flow elements
                one(flowConfiguration).getLeadFlowElement();
                will(returnValue(leadFlowElement));
                one(flowElementInvoker).invoke(flowInvocationContext, flowEvent, leadFlowElement);
                will(throwException(exception));
                
                // pass the exception to the recovery manager
                one(flowInvocationContext).getLastComponentName();
                will(returnValue("componentName"));
                one(recoveryManager).recover("componentName", exception, flowEvent);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        isRecovering = true;
        isRunning = false;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("recovering");
        flow.invoke(flowEvent);

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed flow invoke with a flow event with the resulting outcome 
     * being continue to run.
     */
    @Test
    public void test_failed_flow_invoke_with_flowEvent_resulting_in_continuing_to_run()
    {
        final RuntimeException exception = new RuntimeException("test failed flow invocation");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // create context
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                one(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));
                one(flowInvocationContext).addInvokedComponentName("consumerName");

                // reload any marked dynamic configuration
                one(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                one(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement));
                exactly(1).of(flowConfiguration).configureFlowElement(dynamicConfiguredResourceFlowElement);

                // invoke the flow elements
                one(flowConfiguration).getLeadFlowElement();
                will(returnValue(leadFlowElement));
                one(flowElementInvoker).invoke(flowInvocationContext, flowEvent, leadFlowElement);
                will(throwException(exception));
                
                // pass the exception to the recovery manager
                one(flowInvocationContext).getLastComponentName();
                will(returnValue("componentName"));
                one(recoveryManager).recover("componentName", exception, flowEvent);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.invoke(flowEvent);

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed flow invoke with an exception resulting outcome 
     * being continue to run.
     */
    @Test
    public void test_failed_flow_invoke_with_exception_resulting_in_continuing_to_run()
    {
        final RuntimeException exception = new RuntimeException("invoked with exception test");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the context
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                one(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));
                one(flowInvocationContext).addInvokedComponentName("consumerName");
                exactly(1).of(flowInvocationContext).getLastComponentName();
                will(returnValue("consumerName"));

                // pass the exception to the recovery manager
                one(recoveryManager).recover("consumerName", exception);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.invoke(exception);

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed flow invoke with an exception resulting outcome 
     * being recovery.
     */
    @Test
    public void test_failed_flow_invoke_with_exception_resulting_in_recovery()
    {
        final RuntimeException exception = new RuntimeException("invoked with exception test");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the context
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                one(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));
                one(flowInvocationContext).addInvokedComponentName("consumerName");
                exactly(1).of(flowInvocationContext).getLastComponentName();
                will(returnValue("consumerName"));

                // pass the exception to the recovery manager
                one(recoveryManager).recover("consumerName", exception);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        isRecovering = true;
        isRunning = false;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("recovering");
        flow.invoke(exception);

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed flow invoke with an exception resulting outcome 
     * being stoppedInError.
     */
    @Test
    public void test_failed_flow_invoke_with_exception_resulting_in_stoppedInError()
    {
        final RuntimeException exception = new RuntimeException("invoked with exception test");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the context
                one(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                one(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));
                one(flowInvocationContext).addInvokedComponentName("consumerName");
                exactly(1).of(flowInvocationContext).getLastComponentName();
                will(returnValue("consumerName"));

                // pass the exception to the recovery manager
                one(recoveryManager).recover("consumerName", exception);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", 
            flowConfiguration, flowElementInvoker, recoveryManager);

        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        isUnrecoverable = true;
        isRunning = false;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stoppedInError");
        flow.invoke(exception);

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Set the getState expectations based on the incoming parameters.
     * @param isRecovering
     * @param isRunning
     * @param isUnrecoverable
     */
    private void setGetStateExpectations(final boolean isRecovering, final boolean isRunning, final boolean isUnrecoverable)
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set expectations for establishing state
                one(recoveryManager).isRecovering();
                will(returnValue(isRecovering));
                
                if(!isRecovering)
                {
                    one(flowConfiguration).getConsumerFlowElement();
                    will(returnValue(consumerFlowElement));
                    one(consumerFlowElement).getFlowComponent();
                    will(returnValue(consumer));
                    one(consumer).isRunning();
                    will(returnValue(isRunning));
                }
                
                if(!isRecovering && !isRunning)
                {
                    one(recoveryManager).isUnrecoverable();
                    will(returnValue(isUnrecoverable));
                }
            }
        });
    }
    
    /**
     * Set the getState expectations based on the incoming parameters.
     * @param isRecovering
     * @param isRunning
     * @param isUnrecoverable
     */
    private void setGetStateExpectations(final boolean isRecovering, final boolean isRunning)
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set expectations for establishing state
                one(recoveryManager).isRecovering();
                will(returnValue(isRecovering));
                
                if(!isRecovering)
                {
                    one(flowConfiguration).getConsumerFlowElement();
                    will(returnValue(consumerFlowElement));
                    one(consumerFlowElement).getFlowComponent();
                    will(returnValue(consumer));
                    one(consumer).isRunning();
                    will(returnValue(isRunning));
                }
            }
        });
    }
    
    /**
     * Convenience method for setting monitor expectations.
     * @param String state
     */
    private void setMonitorExpectations(final String state)
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set expectation string on monitor notification
                exactly(1).of(monitor).notifyMonitor(state);
            }
        });
    }
    
    /**
     * Extended test class allowing return of a mocked flowInvocationContext.
     * @author Ikasan Developer Team
     *
     */
    private class ExtendedVisitingInvokerFlow extends VisitingInvokerFlow
    {

        public ExtendedVisitingInvokerFlow(String name, String moduleName, FlowConfiguration flowConfiguration, FlowElementInvoker flowElementInvoker,
                RecoveryManager<FlowEvent<?,?>> recoveryManager)
        {
            super(name, moduleName, flowConfiguration, flowElementInvoker, recoveryManager);
        }
     
        @Override
        protected FlowInvocationContext createFlowInvocationContext()
        {
            return flowInvocationContext;
        }
    }
}
