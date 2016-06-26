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
package org.ikasan.flow.visitorPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javassist.bytecode.Descriptor.Iterator;

import org.ikasan.flow.configuration.FlowElementPersistentConfiguration;
import org.ikasan.flow.configuration.FlowPersistentConfiguration;
import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow.ManagedResourceRecoveryManagerFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.DynamicConfiguredResource;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.IsErrorReportingServiceAware;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowConfiguration;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowElementInvoker;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowEventListener;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.flow.FlowInvocationContextListener;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.Notifier;
import org.ikasan.spec.recovery.RecoveryManager;
import org.ikasan.spec.replay.ReplayRecordService;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>VisitingInvokerFlow</code> class.
 *
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
public class VisitingInvokerFlowTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
        }};

    /** Mock flowConfiguration */
    final FlowConfiguration flowConfiguration = mockery.mock(FlowConfiguration.class, "mockFlowConfiguration");

    /** Mock exclusionFlowConfiguration */
    final ExclusionFlowConfiguration exclusionFlowConfiguration = mockery.mock(ExclusionFlowConfiguration.class, "mockExclusionFlowConfiguration");

    /** Mock flowElementInvoker */
    final FlowElementInvoker flowElementInvoker = mockery.mock(FlowElementInvoker.class, "mockFlowElementInvoker");

    /** Mock flowEventListener */
    final FlowEventListener flowEventListener = mockery.mock(FlowEventListener.class, "mockFlowEventListener");

    /** Mock recoveryManager */
    final RecoveryManager recoveryManager = mockery.mock(RecoveryManager.class, "mockRecoveryManager");

    /** Mock recoveryManager */
    final ErrorReportingService errorReportingService = mockery.mock(ErrorReportingService.class, "mockErrorReportingService");
    
    final ReplayRecordService replayRecordService = mockery.mock(ReplayRecordService.class, "mockReplayRecordService");

    /** Mock managedResourceRecoveryManagerFactory */
    final ManagedResourceRecoveryManagerFactory managedResourceRecoveryManagerFactory = mockery.mock(ManagedResourceRecoveryManagerFactory.class, "mockManagedResourceRecoveryManagerFactory");

    /** Mock managedResourceRecoveryManager */
    final ManagedResourceRecoveryManager managedResourceRecoveryManager = mockery.mock(ManagedResourceRecoveryManager.class, "mockManagedResourceRecoveryManager");

    /** Mock managedResourceRecoveryManager */
    final ManagedResourceRecoveryManager exclusionManagedResourceRecoveryManager = mockery.mock(ManagedResourceRecoveryManager.class, "mockExclusionManagedResourceRecoveryManager");


    /** Mock list of configured resource flow elements */
    final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements
            = mockery.mock(List.class, "mockFlowElementConfiguredResources");
    
    /** Mock list of configured resource flow elements */
    final List<FlowElement<?>> flowElements
            = mockery.mock(List.class, "mockFlowElements");

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

    /** Mock errorReportingService aware resource flow element 1 */
    final FlowElement<IsErrorReportingServiceAware> errorReportingServiceAwareFlowElement1
            = mockery.mock(FlowElement.class, "mockErrorReportingServiceAwareFlowElement1");

    /** Mock managed resource flow element 2 */
    final FlowElement<ManagedResource> managedResourceFlowElement2
            = mockery.mock(FlowElement.class, "mockFlowElementManagedResource2");

    /** Mock managed resource flow element 3 */
    final FlowElement<ManagedResource> managedResourceFlowElement3
            = mockery.mock(FlowElement.class, "mockFlowElementManagedResource3");

    /** Mock managed resource flow element 1 */
    final FlowElement<ManagedResource> managedResourceFlowElementExclusion1
            = mockery.mock(FlowElement.class, "mockManagedResourceFlowElementExclusion1");

    /** mock managed resource */
    final ManagedResource managedResource = mockery.mock(ManagedResource.class, "mockManagedResource");

    /** mock errorReportingService aware component */
    final IsErrorReportingServiceAware errorReportingServiceAwareComponent = mockery.mock(IsErrorReportingServiceAware.class, "mockIsErrorReportingServiceAware");

    /** mock managed resource */
    final ManagedResource exclusionManagedResource = mockery.mock(ManagedResource.class, "mockExclusionManagedResource");

    /** mock managed resource */
    final ConfiguredResource configuredResource = mockery.mock(ConfiguredResource.class, "mockConfiguredResource");

    /** mock managed resource */
    final DynamicConfiguredResource dynamicConfiguredResource = mockery.mock(DynamicConfiguredResource.class, "mockDynamicConfiguredResource");

    /** Mock consumer flowElement */
    final FlowElement<Consumer<EventListener<FlowEvent<?,?>>,EventFactory>> consumerFlowElement
            = mockery.mock(FlowElement.class, "mockConsumerFlowElement");

    /** Mock consumer */
    final Consumer<EventListener<FlowEvent<?,?>>,EventFactory> consumer = mockery.mock(Consumer.class, "mockConsumer");

    /** Mock flow event */
    final FlowEvent flowEvent = mockery.mock(FlowEvent.class, "mockFlowEvent");

    /** Mock monitor */
    final Monitor monitor = mockery.mock(Monitor.class, "mockMonitor");

    /** Mock flow invocation context */
    final FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "mockFlowInvocationContext");

    /** Mock flow event factory */
    final FlowEventFactory flowEventFactory = mockery.mock(FlowEventFactory.class, "mockFlowEventFactory");

    /** Mock exclusionService */
    final ExclusionService exclusionService = mockery.mock(ExclusionService.class, "mockExclusionService");

    /** Mock serialiserFactory */
    final SerialiserFactory serialiserFactory = mockery.mock(SerialiserFactory.class, "mockSerialiserFactory");

    final FlowInvocationContextListener flowInvocationContextListener = mockery.mock(FlowInvocationContextListener.class, "flowInvocationContextListener");
    final List<FlowInvocationContextListener> flowInvocationContextListeners = Collections.singletonList(flowInvocationContextListener);

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
        new VisitingInvokerFlow(null, null, null, null, null, null, null);
    }

    /**
     * Test failed constructor due to null module name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullModuleName()
    {
        new VisitingInvokerFlow("flowName", null, null, null, null, null, null);
    }

    /**
     * Test failed constructor due to null flow configuration.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullFlowConfiguration()
    {
        new VisitingInvokerFlow("flowName", "moduleName", null, null, null, null, null);
    }

    /**
     * Test failed constructor due to null flow element invoker.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullExclusionFlowConfigurationInvoker()
    {
        new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration, null, null, null, null);
    }

    /**
     * Test failed constructor due to null flow recovery manager.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullFlowRecoveryManager()
    {
        new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration, exclusionFlowConfiguration, null, null, null);
    }

    /**
     * Test failed constructor due to null flow recovery manager.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullFlowExclusionService()
    {
        new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration, exclusionFlowConfiguration, recoveryManager, null, null);
    }

    /**
     * Test successful visiting flow invoker instantiation.
     */
    @Test
    public void test_successful_VisitingInvokerFlow_instantiation()
    {
        Flow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);
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
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements = new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final List<FlowElement<ManagedResource>> managedResourceExclusionFlowElements = new ArrayList<>();

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);

        final List<Notifier> notifiers = new ArrayList<>();

        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the two flow element configured resources
                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load monitor configuration and notifiers
                exactly(1).of(monitor).getNotifiers();
                will(returnValue(notifiers));

                // load configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);

                // get the two exclusion flow element configured resources
                oneOf(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load exclusion flow configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);
                
                exactly(1).of(flowConfiguration).configure(flow);
                
                exactly(1).of(flowConfiguration).getFlowElements();
                will(returnValue(flowElements));
                
                exactly(1).of(flowElements).iterator();     

                // inject errorReportingService to those needing it
                exactly(1).of(flowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                exactly(1).of(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                exactly(1).of(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // inject errorReportingService to those needing it on the exclusion flow
                exactly(1).of(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                exactly(1).of(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                exactly(1).of(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // get the three flow element managed resources
                exactly(1).of(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceExclusionFlowElements));

                // get the three flow element managed resources
                exactly(1).of(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // clear recovery manager states
                oneOf(recoveryManager).initialise();
                // pass any managed resources to the recovery manager
                oneOf(recoveryManager).setManagedResources(with(any(List.class)));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name3"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name3");
                will(returnValue(managedResourceRecoveryManager));
                exactly(1).of(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name3"));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name2"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name2");
                will(returnValue(managedResourceRecoveryManager));
                exactly(1).of(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name2"));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name1"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name1");
                will(returnValue(managedResourceRecoveryManager));
                exactly(1).of(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name1"));

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // set listener for tech callbacks
                exactly(1).of(consumer).setListener(flow);

                // check eventFactory for consumer
                exactly(1).of(consumer).getEventFactory();
                will(returnValue(flowEventFactory));

                // start the consumer
                exactly(1).of(consumer).start();
            }
        });

        // set the monitor and receive initial state callback
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

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
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements = new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final List<FlowElement<ManagedResource>> managedResourceExclusionFlowElements = new ArrayList<>();

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);

        final List<Notifier> notifiers = new ArrayList<>();

        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the two flow element configured resources
                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load monitor configuration and notifiers
                exactly(1).of(monitor).getNotifiers();
                will(returnValue(notifiers));

                // load configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);

                // get the two exclusion flow element configured resources
                oneOf(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load exclusion flow configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);
                
                exactly(1).of(flowConfiguration).configure(flow);
                
                exactly(1).of(flowConfiguration).getFlowElements();
                will(returnValue(flowElements));
                
                exactly(1).of(flowElements).iterator();     

                // inject errorReportingService to those needing it
                exactly(1).of(flowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                exactly(1).of(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                exactly(1).of(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // inject errorReportingService to those needing it on the exclusion flow
                exactly(1).of(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                exactly(1).of(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                exactly(1).of(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // get the three flow element managed resources
                exactly(1).of(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceExclusionFlowElements));

                // get the three flow element managed resources
                exactly(1).of(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // clear recovery manager states
                oneOf(recoveryManager).initialise();
                // pass any managed resources to the recovery manager
                oneOf(recoveryManager).setManagedResources(with(any(List.class)));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                exactly(1).of(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                exactly(1).of(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                exactly(1).of(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // set listener for tech callbacks
                exactly(1).of(consumer).setListener(flow);

                // check eventFactory for consumer
                exactly(1).of(consumer).getEventFactory();
                will(returnValue(flowEventFactory));

                // start the consumer
                exactly(1).of(consumer).start();
            }
        });

        // set the monitor and receive initial state callback
        isUnrecoverable = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stoppedInError");
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

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
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        // set the monitor and receive initial state callback
        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // check state before proceeding with start
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);

        // monitor state updated after start invoked
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");

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
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);

        // set the monitor and receive initial state callback
        isRecovering = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("recovering");
        flow.setMonitor(monitor);

        // check state before proceeding with start
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);

        // monitor state updated after start invoked
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("recovering");

        // start will bail out due to flow already running in recovery
        flow.start();

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed flow start due to configuration failure.
     */
    @Test(expected = RuntimeException.class)
    public void test_failed_flow_start_due_to_configuration_failure()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);


        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements = new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final RuntimeException exception = new RuntimeException("test configuration failing");

        final List<Notifier> notifiers = new ArrayList<>();

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // clear recovery manager states
                oneOf(recoveryManager).initialise();
                // pass any managed resources to the recovery manager
                oneOf(recoveryManager).setManagedResources(with(any(List.class)));
                oneOf(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // get consumer flow element
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));

                // get the flow element configured resources
                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement));

                // load monitor configuration and notifiers
                exactly(1).of(monitor).getNotifiers();
                will(returnValue(notifiers));

                // get the two exclusion flow element configured resources
                oneOf(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load exclusion flow configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);
                
                // inject errorReportingService to those needing it on the exclusion flow
                exactly(1).of(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                exactly(1).of(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                exactly(1).of(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // load configuration
                exactly(2).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(1).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(1).of(flowConfiguration).configure(configuredResource);
                will(throwException(exception));
            }
        });

        // set the monitor and receive initial state callback
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

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
    public void test_success_flow_start_even_with_managedResource_start_failure_non_critical_resource()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);

        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements = new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final List<FlowElement<ManagedResource>> managedResourceExclusionFlowElements = new ArrayList<>();

        final List<Notifier> notifiers = new ArrayList<>();

        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // clear recovery manager states
                oneOf(recoveryManager).initialise();
                // pass any managed resources to the recovery manager
                oneOf(recoveryManager).setManagedResources(with(any(List.class)));

                // load monitor configuration and notifiers
                exactly(1).of(monitor).getNotifiers();
                will(returnValue(notifiers));

                // get the two flow element configured resources
                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));
                // configure business flow
                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));
                exactly(2).of(flowConfiguration).configure(configuredResource);

                // load configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                // get the two exclusion flow element configured resources
                oneOf(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load exclusion flow configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);
                
                exactly(1).of(flowConfiguration).configure(flow);
                
                exactly(1).of(flowConfiguration).getFlowElements();
                will(returnValue(flowElements));
                
                exactly(1).of(flowElements).iterator();     

                // inject errorReportingService to those needing it
                exactly(1).of(flowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                exactly(1).of(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                exactly(1).of(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // inject errorReportingService to those needing it on the exclusion flow
                exactly(1).of(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                exactly(1).of(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                exactly(1).of(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // get the the flow element managed resource
                exactly(1).of(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceExclusionFlowElements));

                // get the the flow element managed resource
                exactly(1).of(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // set the managed resource recovery manager instance on each managed resource
                exactly(1).of(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                exactly(1).of(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                will(throwException(new RuntimeException("test managed resource start failure")));
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                oneOf(managedResource).isCriticalOnStartup();
                will(returnValue(false));
                exactly(2).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("managedResourceFlowElementName"));
                inSequence(reverseOrder);

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // set listener for tech callbacks
                exactly(1).of(consumer).setListener(flow);

                // check eventFactory for consumer
                exactly(1).of(consumer).getEventFactory();
                will(returnValue(flowEventFactory));

                // start the consumer
                exactly(1).of(consumer).start();
            }
        });

        // set the monitor and receive initial state callback
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

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
     * Test successful flow start regardless of a managed resource successfully
     * starting. We don't ignore managed resource start failures if they are marked as critical.
     */
    @Test(expected = RuntimeException.class)
    public void test_success_flow_start_even_with_managedResource_start_failure_critical_resource()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);

        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // clear recovery manager states
                oneOf(recoveryManager).initialise();

                // get the two flow element configured resources
                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load dao
                exactly(2).of(flowConfiguration).configure(configuredResource);

                // get the the flow element managed resource
                oneOf(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // start each managed resource from right to left (reverse order) in flow order
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                will(throwException(new RuntimeException("test managed resource start failure")));
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                oneOf((FlowElement) managedResource).getFlowComponent();
                will(returnValue("name"));
                oneOf(managedResource).isCriticalOnStartup();
                will(returnValue(true));

                // stop each managed resource
                oneOf(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
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
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements = new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final List<FlowElement<ManagedResource>> managedResourceExclusionFlowElements = new ArrayList<>();

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);

        final RuntimeException exception = new RuntimeException("test consumer failing to start");

        final List<Notifier> notifiers = new ArrayList<>();

        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // clear recovery manager states
                oneOf(recoveryManager).initialise();
                // pass any managed resources to the recovery manager
                oneOf(recoveryManager).setManagedResources(with(any(List.class)));

                // get the two flow element configured resources
                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load monitor dao and notifiers
                exactly(1).of(monitor).getNotifiers();
                will(returnValue(notifiers));

                // load dao
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);

                // get the two exclusion flow element configured resources
                oneOf(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load exclusion flow configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);
                
                exactly(1).of(flowConfiguration).configure(flow);
                
                exactly(1).of(flowConfiguration).getFlowElements();
                will(returnValue(flowElements));
                
                exactly(1).of(flowElements).iterator();     


                // inject errorReportingService to those needing it
                exactly(1).of(flowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                exactly(1).of(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                exactly(1).of(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // inject errorReportingService to those needing it on the exclusion flow
                exactly(1).of(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                exactly(1).of(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                exactly(1).of(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // get the exclusion flow element managed resources
                exactly(1).of(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceExclusionFlowElements));

                // get the three flow element managed resources
                exactly(1).of(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                exactly(1).of(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                exactly(1).of(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                exactly(1).of(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // set listener for tech callbacks
                exactly(1).of(consumer).setListener(flow);

                // check eventFactory for consumer
                exactly(1).of(consumer).getEventFactory();
                will(returnValue(flowEventFactory));

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
        flow.setErrorReportingService(errorReportingService);

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
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements = new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final List<FlowElement<ManagedResource>> managedResourceExclusionFlowElements = new ArrayList<>();

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);

        final RuntimeException exception = new RuntimeException("test consumer failing to start");

        final List<Notifier> notifiers = new ArrayList<>();

        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // clear recovery manager states
                oneOf(recoveryManager).initialise();
                // pass any managed resources to the recovery manager
                oneOf(recoveryManager).setManagedResources(with(any(List.class)));

                // get the two flow element configured resources
                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // get the two exclusion flow element configured resources
                oneOf(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load exclusion flow configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);
                
                exactly(1).of(flowConfiguration).configure(flow);
                
                exactly(1).of(flowConfiguration).getFlowElements();
                will(returnValue(flowElements));
                
                exactly(1).of(flowElements).iterator();     

                // inject errorReportingService to those needing it
                exactly(1).of(flowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                exactly(1).of(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                exactly(1).of(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // inject errorReportingService to those needing it on the exclusion flow
                exactly(1).of(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                exactly(1).of(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                exactly(1).of(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // load monitor dao and notifiers
                exactly(1).of(monitor).getNotifiers();
                will(returnValue(notifiers));

                // load dao
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);

                // get the exclusion flow element managed resources
                exactly(1).of(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceExclusionFlowElements));

                // get the three flow element managed resources
                exactly(1).of(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                exactly(1).of(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                exactly(1).of(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                exactly(1).of(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // set listener for tech callbacks
                exactly(1).of(consumer).setListener(flow);

                // check eventFactory for consumer
                exactly(1).of(consumer).getEventFactory();
                will(returnValue(flowEventFactory));

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
        flow.setErrorReportingService(errorReportingService);

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
        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
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
                oneOf(recoveryManager).isRecovering();
                will(returnValue(false));

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // nullify the listener for tech callbacks
                exactly(1).of(consumer).setListener(null);

                // stop the consumer
                exactly(1).of(consumer).stop();

                // get the three flow element managed resources
                oneOf(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // stop each managed resource from left to right in flow order
                exactly(1).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("componentName1"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                exactly(1).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("componentName1"));
                inSequence(reverseOrder);

                exactly(1).of(managedResourceFlowElement2).getComponentName();
                will(returnValue("componentName2"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                exactly(1).of(managedResourceFlowElement2).getComponentName();
                will(returnValue("componentName2"));
                inSequence(reverseOrder);

                exactly(1).of(managedResourceFlowElement3).getComponentName();
                will(returnValue("componentName3"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                exactly(1).of(managedResourceFlowElement3).getComponentName();
                will(returnValue("componentName3"));
                inSequence(reverseOrder);

                // get the three exclusion flow element managed resources
                oneOf(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // stop each managed resource from left to right in flow order
                exactly(1).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("componentName1"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                exactly(1).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("componentName1"));
                inSequence(reverseOrder);

                exactly(1).of(managedResourceFlowElement2).getComponentName();
                will(returnValue("componentName2"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                exactly(1).of(managedResourceFlowElement2).getComponentName();
                will(returnValue("componentName2"));
                inSequence(reverseOrder);

                exactly(1).of(managedResourceFlowElement3).getComponentName();
                will(returnValue("componentName3"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                exactly(1).of(managedResourceFlowElement3).getComponentName();
                will(returnValue("componentName3"));
                inSequence(reverseOrder);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);

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
        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
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
                oneOf(recoveryManager).isRecovering();
                will(returnValue(false));

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // nullify the listener for tech callbacks
                exactly(1).of(consumer).setListener(null);

                // stop the consumer
                exactly(1).of(consumer).stop();

                // get the three flow element managed resources
                oneOf(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // stop each managed resource from left to right in flow order
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);

                // get the three exclusion flow element managed resources
                oneOf(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // stop each managed resource from left to right in flow order
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);

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
        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
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
                oneOf(recoveryManager).isRecovering();
                will(returnValue(false));

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // nullify the listener for tech callbacks
                exactly(1).of(consumer).setListener(null);

                // stop the consumer
                exactly(1).of(consumer).stop();

                // get the three flow element managed resources
                oneOf(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // stop each managed resource from left to right in flow order
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);

                // get the three exclusion flow element managed resources
                oneOf(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // stop each managed resource from left to right in flow order
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);

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
        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
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
                oneOf(recoveryManager).isRecovering();
                will(returnValue(false));

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // nullify the listener for tech callbacks
                exactly(1).of(consumer).setListener(null);

                // stop the consumer
                exactly(1).of(consumer).stop();

                // get the three flow element managed resources
                oneOf(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // stop each managed resource from left to right in flow order
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);

                // get the three exclusion flow element managed resources
                oneOf(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // stop each managed resource from left to right in flow order
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                inSequence(reverseOrder);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);

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
                // always get the original exceptionLifeIdentifier
                exactly(1).of(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                exactly(1).of(flowInvocationContext).startFlowInvocation();
                exactly(1).of(flowInvocationContext).endFlowInvocation();


                // reload any marked dynamic dao
                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                oneOf(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement, dynamicConfiguredResourceFlowElement));

                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                oneOf(flowConfiguration).getReplayRecordService();
                will(returnValue(replayRecordService));

                exactly(2).of(flowConfiguration).configure(dynamicConfiguredResource);

                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(flowElementInvoker).setIgnoreContextInvocation(true);
                
                oneOf(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, consumerFlowElement);
                will(returnValue(null));

                exactly(1).of(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                oneOf(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement, dynamicConfiguredResourceFlowElement));

                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                exactly(2).of(flowConfiguration).update(dynamicConfiguredResource);

                // in this test we do not need to cancel recovery
                oneOf(recoveryManager).isRecovering();
                will(returnValue(false));
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setFlowListener(flowEventListener);
        flow.invoke(flowEvent);

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow invoke with a flow event with a context listener.
     */
    @Test
    public void test_successful_flow_invoke_with_flowEvent_with_contextListener()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always get the original exceptionLifeIdentifier
                exactly(1).of(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                exactly(1).of(flowInvocationContext).startFlowInvocation();
                exactly(1).of(flowInvocationContext).endFlowInvocation();


                // reload any marked dynamic dao
                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                oneOf(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement, dynamicConfiguredResourceFlowElement));

                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                exactly(2).of(flowConfiguration).configure(dynamicConfiguredResource);

                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(flowElementInvoker).setIgnoreContextInvocation(true);
                oneOf(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, consumerFlowElement);
                will(returnValue(null));
                
                oneOf(flowConfiguration).getReplayRecordService();
                will(returnValue(replayRecordService));

                exactly(1).of(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                oneOf(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement, dynamicConfiguredResourceFlowElement));

                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                exactly(2).of(flowConfiguration).update(dynamicConfiguredResource);

                exactly(1).of(flowInvocationContextListener).endFlow(flowInvocationContext);

                // in this test we do not need to cancel recovery
                oneOf(recoveryManager).isRecovering();
                will(returnValue(false));
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);
        ((FlowPersistentConfiguration)flow.getConfiguration()).setInvokeContextListeners(true);
        flow.setFlowInvocationContextListeners(flowInvocationContextListeners);

        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setFlowListener(flowEventListener);
        flow.invoke(flowEvent);

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow invoke with a flow event with a context listener that is disabled.
     */
    @Test
    public void test_successful_flow_invoke_with_flowEvent_with_contextListener_disabled()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always get the original exceptionLifeIdentifier
                exactly(1).of(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                exactly(1).of(flowInvocationContext).startFlowInvocation();
                exactly(1).of(flowInvocationContext).endFlowInvocation();
                // reload any marked dynamic dao
                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                oneOf(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement, dynamicConfiguredResourceFlowElement));
                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                exactly(2).of(flowConfiguration).configure(dynamicConfiguredResource);

                oneOf(flowConfiguration).getReplayRecordService();
                will(returnValue(replayRecordService));

                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(flowElementInvoker).setIgnoreContextInvocation(true);
                oneOf(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, consumerFlowElement);
                will(returnValue(null));

                exactly(1).of(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                oneOf(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement, dynamicConfiguredResourceFlowElement));

                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                exactly(2).of(flowConfiguration).update(dynamicConfiguredResource);

                // in this test we do not need to cancel recovery
                oneOf(recoveryManager).isRecovering();
                will(returnValue(false));
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);
        flow.setFlowInvocationContextListeners(flowInvocationContextListeners);
        flow.stopContextListeners();

        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setFlowListener(flowEventListener);
        flow.invoke(flowEvent);

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow invoke with a flow event with a context listener that is specifically enabled.
     */
    @Test
    public void test_successful_flow_invoke_with_flowEvent_with_contextListener_enabled()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always get the original exceptionLifeIdentifier
                exactly(1).of(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                exactly(1).of(flowInvocationContext).startFlowInvocation();
                exactly(1).of(flowInvocationContext).endFlowInvocation();


                // reload any marked dynamic dao
                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                oneOf(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement, dynamicConfiguredResourceFlowElement));

                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                oneOf(flowConfiguration).getReplayRecordService();
                will(returnValue(replayRecordService));

                exactly(2).of(flowConfiguration).configure(dynamicConfiguredResource);

                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(flowElementInvoker).setIgnoreContextInvocation(true);
                oneOf(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, consumerFlowElement);
                will(returnValue(null));

                exactly(1).of(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                oneOf(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement, dynamicConfiguredResourceFlowElement));

                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                exactly(2).of(flowConfiguration).update(dynamicConfiguredResource);

                exactly(1).of(flowInvocationContextListener).endFlow(flowInvocationContext);

                // in this test we do not need to cancel recovery
                oneOf(recoveryManager).isRecovering();
                will(returnValue(false));
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);
        ((FlowPersistentConfiguration)flow.getConfiguration()).setInvokeContextListeners(true);
        flow.setFlowInvocationContextListeners(flowInvocationContextListeners);
        flow.stopContextListeners();
        flow.startContextListeners();

        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setFlowListener(flowEventListener);
        flow.invoke(flowEvent);

        // test assertions
        mockery.assertIsSatisfied();
    }


    /**
     * Test failed flow invoke with a flow event, but dynamic dao failing.
     */
    @Test
    public void test_failed_flow_invoke_with_flowEvent_stoppingInError_due_to_dynamicConfiguration_failure()
    {
        final RuntimeException exception = new RuntimeException("test failed dynamic dao");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always get the original exceptionLifeIdentifier
                exactly(1).of(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                exactly(1).of(flowInvocationContext).startFlowInvocation();
                exactly(1).of(flowInvocationContext).endFlowInvocation();

                // reload any marked dynamic dao
                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                oneOf(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement));

                exactly(1).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(1).of(flowConfiguration).configure(configuredResource);
                will(throwException(exception));

                exactly(1).of(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                // pass the exception to the recovery manager
                oneOf(recoveryManager).recover(flowInvocationContext, exception, flowEvent, "identifier");
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

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
                // always get the original exceptionLifeIdentifier
                exactly(1).of(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                exactly(1).of(flowInvocationContext).startFlowInvocation();
                exactly(1).of(flowInvocationContext).endFlowInvocation();

                // reload any marked dynamic dao
                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                oneOf(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement));

                exactly(1).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(1).of(flowConfiguration).configure(configuredResource);
                
                oneOf(flowConfiguration).getReplayRecordService();
                will(returnValue(replayRecordService));

                exactly(1).of(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                // invoke the flow elements
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(flowElementInvoker).setIgnoreContextInvocation(true);

                oneOf(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, consumerFlowElement);
                will(throwException(exception));

                // pass the exception to the recovery manager
                oneOf(recoveryManager).recover(flowInvocationContext, exception, flowEvent, "identifier");
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        isUnrecoverable = true;
        isRunning = false;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stoppedInError");
        flow.setFlowListener(flowEventListener);
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
                // always get the original exceptionLifeIdentifier
                exactly(1).of(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                exactly(1).of(flowInvocationContext).startFlowInvocation();
                exactly(1).of(flowInvocationContext).endFlowInvocation();

                // reload any marked dynamic dao
                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                oneOf(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement));

                exactly(1).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(1).of(flowConfiguration).configure(configuredResource);
                
                oneOf(flowConfiguration).getReplayRecordService();
                will(returnValue(replayRecordService));

                exactly(1).of(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                // invoke the flow elements
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(flowElementInvoker).setIgnoreContextInvocation(true);

                oneOf(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, consumerFlowElement);
                will(throwException(exception));

                // pass the exception to the recovery manager
                oneOf(recoveryManager).recover(flowInvocationContext, exception, flowEvent, "identifier");
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        isRecovering = true;
        isRunning = false;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("recovering");
        flow.setFlowListener(flowEventListener);
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
                // always get the original exceptionLifeIdentifier
                exactly(1).of(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                exactly(1).of(flowInvocationContext).startFlowInvocation();
                exactly(1).of(flowInvocationContext).endFlowInvocation();

                // reload any marked dynamic dao
                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                oneOf(dynamicConfiguredResourceFlowElements).iterator();
                will(returnIterator(dynamicConfiguredResourceFlowElement));

                exactly(1).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(1).of(flowConfiguration).configure(configuredResource);
                
                oneOf(flowConfiguration).getReplayRecordService();
                will(returnValue(replayRecordService));

                exactly(1).of(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                // invoke the flow elements
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(flowElementInvoker).setIgnoreContextInvocation(true);

                oneOf(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, consumerFlowElement);
                will(throwException(exception));

                // pass the exception to the recovery manager
                oneOf(recoveryManager).recover(flowInvocationContext, exception, flowEvent, "identifier");
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        isRunning = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setFlowListener(flowEventListener);
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
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));

                // pass the exception to the recovery manager
                oneOf(recoveryManager).recover("consumerName", exception);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

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
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));

                // pass the exception to the recovery manager
                oneOf(recoveryManager).recover("consumerName", exception);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

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
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));

                // pass the exception to the recovery manager
                oneOf(recoveryManager).recover("consumerName", exception);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

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
     * Test getter for named flowElements
     */
    @Test
    public void test_accessor_for_named_flow_elements()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get all flow elements
                oneOf(flowConfiguration).getFlowElements();
                will(returnValue(configuredResourceFlowElements));
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

        Assert.assertNotNull("there should be one flow elements on this flow", flow.getFlowElements());

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful flow start from a stopped state.
     */
    @Test
    public void test_successful_flow_start_from_stopped_with_exclusionFlow_having_manageResources()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        final List<FlowElement<ManagedResource>> managedResourceExclusionFlowElements = new ArrayList<>();
        managedResourceExclusionFlowElements.add(managedResourceFlowElementExclusion1);

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);

        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements = new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final List<Notifier> notifiers = new ArrayList<>();

        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the two flow element configured resources
                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load monitor configuration and notifiers
                exactly(1).of(monitor).getNotifiers();
                will(returnValue(notifiers));

                // load configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);

                // get the two exclusion flow element configured resources
                oneOf(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load exclusion flow configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);
                
                exactly(1).of(flowConfiguration).configure(flow);
                
                exactly(1).of(flowConfiguration).getFlowElements();
                will(returnValue(flowElements));
                
                exactly(1).of(flowElements).iterator();     

                // inject errorReportingService to those needing it
                exactly(1).of(flowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                exactly(1).of(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                exactly(1).of(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // inject errorReportingService to those needing it on the exclusion flow
                exactly(1).of(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                exactly(1).of(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                exactly(1).of(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // get the three flow element managed resources
                exactly(1).of(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceExclusionFlowElements));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElementExclusion1).getFlowComponent();
                will(returnValue(exclusionManagedResource));
                exactly(3).of(managedResourceFlowElementExclusion1).getComponentName();
                will(returnValue("exclusion component name1"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("exclusion component name1");
                will(returnValue(exclusionManagedResourceRecoveryManager));
                exactly(1).of(exclusionManagedResource).setManagedResourceRecoveryManager(exclusionManagedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                exactly(1).of(exclusionManagedResource).startManagedResource();

                // get the three flow element managed resources
                exactly(1).of(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // clear recovery manager states
                oneOf(recoveryManager).initialise();
                // pass any managed resources to the recovery manager
                oneOf(recoveryManager).setManagedResources(with(any(List.class)));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name2"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name2");
                will(returnValue(managedResourceRecoveryManager));
                exactly(1).of(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name2"));

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // set listener for tech callbacks
                exactly(1).of(consumer).setListener(flow);

                // check eventFactory for consumer
                exactly(1).of(consumer).getEventFactory();
                will(returnValue(flowEventFactory));

                // start the consumer
                exactly(1).of(consumer).start();
            }
        });

        // set the monitor and receive initial state callback
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

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
    public void test_successful_flow_start_from_stoppedInError_with_exclusionFlow_having_managedResource()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        final List<FlowElement<ManagedResource>> managedResourceExclusionFlowElements = new ArrayList<>();
        managedResourceExclusionFlowElements.add(managedResourceFlowElementExclusion1);

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);

        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements = new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final List<Notifier> notifiers = new ArrayList<>();

        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // load monitor configuration and notifiers
                exactly(1).of(monitor).getNotifiers();
                will(returnValue(notifiers));

                // get the two flow element configured resources
                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load flow configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);
                

                // get the two exclusion flow element configured resources
                oneOf(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                oneOf(configuredResourceFlowElements).iterator();
                will(returnIterator(configuredResourceFlowElement, configuredResourceFlowElement));

                // load exclusion flow configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);
                
                exactly(1).of(flowConfiguration).configure(flow);
                
                exactly(1).of(flowConfiguration).getFlowElements();
                will(returnValue(flowElements));
                
                exactly(1).of(flowElements).iterator();                
                
                // inject errorReportingService to those needing it on the business flow
                exactly(1).of(flowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                exactly(1).of(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                exactly(1).of(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // inject errorReportingService to those needing it on the exclusion flow
                exactly(1).of(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                exactly(1).of(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                exactly(1).of(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // get the three flow element managed resources
                exactly(1).of(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceExclusionFlowElements));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElementExclusion1).getFlowComponent();
                will(returnValue(exclusionManagedResource));
                exactly(1).of(managedResourceFlowElementExclusion1).getComponentName();
                will(returnValue("exclusion component name"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("exclusion component name");
                will(returnValue(exclusionManagedResourceRecoveryManager));
                exactly(1).of(exclusionManagedResource).setManagedResourceRecoveryManager(exclusionManagedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(exclusionManagedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElementExclusion1).getComponentName();
                will(returnValue("exclusion component name"));


                // get the three flow element managed resources
                exactly(1).of(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // clear recovery manager states
                oneOf(recoveryManager).initialise();
                // pass any managed resources to the recovery manager
                oneOf(recoveryManager).setManagedResources(with(any(List.class)));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                exactly(1).of(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                exactly(1).of(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // set listener for tech callbacks
                exactly(1).of(consumer).setListener(flow);

                // check eventFactory for consumer
                exactly(1).of(consumer).getEventFactory();
                will(returnValue(flowEventFactory));

                // start the consumer
                exactly(1).of(consumer).start();
            }
        });

        // set the monitor and receive initial state callback
        isUnrecoverable = true;
        setGetStateExpectations(isRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("stoppedInError");
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

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
                oneOf(recoveryManager).isRecovering();
                will(returnValue(isRecovering));

                if(!isRecovering)
                {
                    oneOf(flowConfiguration).getConsumerFlowElement();
                    will(returnValue(consumerFlowElement));
                    oneOf(consumerFlowElement).getFlowComponent();
                    will(returnValue(consumer));
                    oneOf(consumer).isRunning();
                    will(returnValue(isRunning));
                }

                if(!isRecovering && !isRunning)
                {
                    oneOf(recoveryManager).isUnrecoverable();
                    will(returnValue(isUnrecoverable));
                }
            }
        });
    }

    /**
     * Set the getState expectations based on the incoming parameters.
     * @param isRecovering
     * @param isRunning
     */
    private void setGetStateExpectations(final boolean isRecovering, final boolean isRunning)
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set expectations for establishing state
                oneOf(recoveryManager).isRecovering();
                will(returnValue(isRecovering));

                if(!isRecovering)
                {
                    oneOf(flowConfiguration).getConsumerFlowElement();
                    will(returnValue(consumerFlowElement));
                    oneOf(consumerFlowElement).getFlowComponent();
                    will(returnValue(consumer));
                    oneOf(consumer).isRunning();
                    will(returnValue(isRunning));
                }
            }
        });
    }

    /**
     * Convenience method for setting monitor expectations.
     * @param state the state
     */
    private void setMonitorExpectations(final String state)
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set expectation string on monitor notification
                exactly(1).of(monitor).invoke(state);
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

        public ExtendedVisitingInvokerFlow(String name, String moduleName, FlowConfiguration flowConfiguration,
                                           RecoveryManager<FlowEvent<?,?>, FlowInvocationContext> recoveryManager, ExclusionService exclusionService)
        {
            super(name, moduleName, flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);
        }

        @Override
        protected FlowInvocationContext createFlowInvocationContext()
        {
            return flowInvocationContext;
        }
    }
}
