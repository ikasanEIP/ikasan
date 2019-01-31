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

import org.ikasan.flow.configuration.FlowElementPersistentConfiguration;
import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow.ManagedResourceRecoveryManagerFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.IsErrorReportingServiceAware;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.ForceTransactionRollbackException;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.*;
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

import java.util.*;

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
    private final FlowConfiguration flowConfiguration = mockery.mock(FlowConfiguration.class, "mockFlowConfiguration");

    /** Mock exclusionFlowConfiguration */
    private final ExclusionFlowConfiguration exclusionFlowConfiguration = mockery.mock(ExclusionFlowConfiguration.class, "mockExclusionFlowConfiguration");

    /** Mock flowElementInvoker */
    private final FlowElementInvoker flowElementInvoker = mockery.mock(FlowElementInvoker.class, "mockFlowElementInvoker");

    /** Mock flowEventListener */
    private final FlowEventListener flowEventListener = mockery.mock(FlowEventListener.class, "mockFlowEventListener");

    /** Mock recoveryManager */
    private final RecoveryManager recoveryManager = mockery.mock(RecoveryManager.class, "mockRecoveryManager");

    /** Mock recoveryManager */
    private final ErrorReportingService errorReportingService = mockery.mock(ErrorReportingService.class, "mockErrorReportingService");

    private final ReplayRecordService replayRecordService = mockery.mock(ReplayRecordService.class, "mockReplayRecordService");

    /** Mock managedResourceRecoveryManagerFactory */
    private final ManagedResourceRecoveryManagerFactory managedResourceRecoveryManagerFactory = mockery.mock(ManagedResourceRecoveryManagerFactory.class, "mockManagedResourceRecoveryManagerFactory");

    /** Mock managedResourceRecoveryManager */
    private final ManagedResourceRecoveryManager managedResourceRecoveryManager = mockery.mock(ManagedResourceRecoveryManager.class, "mockManagedResourceRecoveryManager");

    /** Mock managedResourceRecoveryManager */
    private final ManagedResourceRecoveryManager exclusionManagedResourceRecoveryManager = mockery.mock(ManagedResourceRecoveryManager.class, "mockExclusionManagedResourceRecoveryManager");

    /** Mock configured resource flow elements */
    private final FlowElement<ConfiguredResource> configuredResourceFlowElement
            = mockery.mock(FlowElement.class, "mockFlowElementConfiguredResource");

    /** Mock dynamic configured resource flow elements */
    private final FlowElement<ConfiguredResource> dynamicConfiguredResourceFlowElement
            = mockery.mock(FlowElement.class, "mockFlowElementDynamicConfiguredResource");

    /** Mock managed resource flow element 1 */
    private final FlowElement<ManagedResource> managedResourceFlowElement1
            = mockery.mock(FlowElement.class, "mockFlowElementManagedResource1");

    /** Mock errorReportingService aware resource flow element 1 */
    private final FlowElement<IsErrorReportingServiceAware> errorReportingServiceAwareFlowElement1
            = mockery.mock(FlowElement.class, "mockErrorReportingServiceAwareFlowElement1");

    /** Mock managed resource flow element 2 */
    private final FlowElement<ManagedResource> managedResourceFlowElement2
            = mockery.mock(FlowElement.class, "mockFlowElementManagedResource2");

    /** Mock managed resource flow element 3 */
    private final FlowElement<ManagedResource> managedResourceFlowElement3
            = mockery.mock(FlowElement.class, "mockFlowElementManagedResource3");

    /** Mock managed resource flow element 1 */
    private final FlowElement<ManagedResource> managedResourceFlowElementExclusion1
            = mockery.mock(FlowElement.class, "mockManagedResourceFlowElementExclusion1");

    /** mock managed resource */
    private final ManagedResource managedResource = mockery.mock(ManagedResource.class, "mockManagedResource");

    /** mock errorReportingService aware component */
    private final IsErrorReportingServiceAware errorReportingServiceAwareComponent = mockery.mock(IsErrorReportingServiceAware.class, "mockIsErrorReportingServiceAware");

    /** mock managed resource */
    private final ManagedResource exclusionManagedResource = mockery.mock(ManagedResource.class, "mockExclusionManagedResource");

    /** mock managed resource */
    private final ConfiguredResource configuredResource = mockery.mock(ConfiguredResource.class, "mockConfiguredResource");

    /** mock managed resource */
    private final ConfiguredResource dynamicConfiguredResource = mockery.mock(ConfiguredResource.class, "mockDynamicConfiguredResource");

    /** Mock consumer flowElement */
    private final FlowElement<Consumer<EventListener<FlowEvent<?,?>>,EventFactory>> consumerFlowElement
            = mockery.mock(FlowElement.class, "mockConsumerFlowElement");

    /** Mock consumer */
    private final Consumer<EventListener<FlowEvent<?,?>>,EventFactory> consumer = mockery.mock(Consumer.class, "mockConsumer");

    /** Mock flow event */
    private final FlowEvent flowEvent = mockery.mock(FlowEvent.class, "mockFlowEvent");

    /** Mock monitor */
    private final Monitor monitor = mockery.mock(Monitor.class, "mockMonitor");

    /** Mock flow invocation context */
    private final FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "mockFlowInvocationContext");

    /** Mock flow event factory */
    private final FlowEventFactory flowEventFactory = mockery.mock(FlowEventFactory.class, "mockFlowEventFactory");

    /** Mock exclusionService */
    private final ExclusionService exclusionService = mockery.mock(ExclusionService.class, "mockExclusionService");

    /** Mock serialiserFactory */
    private final SerialiserFactory serialiserFactory = mockery.mock(SerialiserFactory.class, "mockSerialiserFactory");

    private final FlowInvocationContextListener flowInvocationContextListener = mockery.mock(FlowInvocationContextListener.class, "flowInvocationContextListener");
    private final List<FlowInvocationContextListener> flowInvocationContextListeners = Collections.singletonList(flowInvocationContextListener);

    /** is recovering status */
    private final boolean isRecovering = true;
    private final boolean isNotRecovering = false;

    /** is running status */
    private final boolean isNotRunning = false;
    private final boolean isRunning = true;

    /** is unrecoverable status */
    private final boolean isUnrecoverable = true;
    private final boolean isNotUnrecoverable = false;


    @Before
    public void setup()
    {
        // nothing to setup
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

        final List<FlowElement<?>> configuredResourceInvokers = new ArrayList<FlowElement<?>>();

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();

        final List<FlowElement<?>> flowElements = new ArrayList<FlowElement<?>>();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the two flow element configured resources
                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));

                // get any dynamic resources
                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));

                // load monitor configuration and notifiers
                oneOf(monitor).getNotifiers();
                will(returnValue(notifiers));

                // load configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);

                // exclusion flow invoker configuration
                oneOf(exclusionFlowConfiguration).getFlowElementInvokerConfiguredResources();
                will(returnValue(configuredResourceInvokers));

                // flow invoker configuration
                oneOf(flowConfiguration).getFlowElementInvokerConfiguredResources();
                will(returnValue(configuredResourceInvokers));

                // get the two exclusion flow element configured resources
                oneOf(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));

                // load exclusion flow configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);

                oneOf(flowConfiguration).configure(flow);

                oneOf(flowConfiguration).getFlowElements();
                will(returnValue(flowElements));

                // inject errorReportingService to those needing it
                oneOf(flowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                oneOf(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                oneOf(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // inject errorReportingService to those needing it on the exclusion flow
                oneOf(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                oneOf(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                oneOf(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // get the three flow element managed resources
                oneOf(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceExclusionFlowElements));

                // get the three flow element managed resources
                oneOf(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // clear recovery manager states
                oneOf(recoveryManager).initialise();
                // pass any managed resources to the recovery manager
                oneOf(recoveryManager).setManagedResources(with(any(List.class)));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name3"));
                oneOf(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name3");
                will(returnValue(managedResourceRecoveryManager));
                oneOf(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name3"));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name2"));
                oneOf(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name2");
                will(returnValue(managedResourceRecoveryManager));
                oneOf(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name2"));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name1"));
                oneOf(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name1");
                will(returnValue(managedResourceRecoveryManager));
                oneOf(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name1"));

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // set listener for tech callbacks
                oneOf(consumer).setListener(flow);

                // check eventFactory for consumer
                oneOf(consumer).getEventFactory();
                will(returnValue(flowEventFactory));

                // start the consumer
                oneOf(consumer).start();
            }
        });

        // set the monitor and receive initial state callback
        setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

        // start will result in the monitor being updated to running
        setGetStateExpectations(isNotRecovering, isRunning, isNotUnrecoverable);
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

        final List<FlowElement<?>> configuredResourceInvokers = new ArrayList<FlowElement<?>>();

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();

        final List<FlowElement<?>> flowElements = new ArrayList<FlowElement<?>>();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the two flow element configured resources
                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));

                // load monitor configuration and notifiers
                oneOf(monitor).getNotifiers();
                will(returnValue(notifiers));

                // load configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);

                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));

                // get the two exclusion flow element configured resources
                oneOf(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));

                // exclusion flow invoker configuration
                oneOf(exclusionFlowConfiguration).getFlowElementInvokerConfiguredResources();
                will(returnValue(configuredResourceInvokers));

                // flow invoker configuration
                oneOf(flowConfiguration).getFlowElementInvokerConfiguredResources();
                will(returnValue(configuredResourceInvokers));

                // load exclusion flow configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);

                oneOf(flowConfiguration).configure(flow);

                oneOf(flowConfiguration).getFlowElements();
                will(returnValue(flowElements));

                // inject errorReportingService to those needing it
                oneOf(flowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                oneOf(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                oneOf(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // inject errorReportingService to those needing it on the exclusion flow
                oneOf(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                oneOf(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                oneOf(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // get the three flow element managed resources
                oneOf(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceExclusionFlowElements));

                // get the three flow element managed resources
                oneOf(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // clear recovery manager states
                oneOf(recoveryManager).initialise();
                // pass any managed resources to the recovery manager
                oneOf(recoveryManager).setManagedResources(with(any(List.class)));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                oneOf(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                oneOf(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                oneOf(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                oneOf(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                oneOf(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                oneOf(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // set listener for tech callbacks
                oneOf(consumer).setListener(flow);

                // check eventFactory for consumer
                oneOf(consumer).getEventFactory();
                will(returnValue(flowEventFactory));

                // start the consumer
                oneOf(consumer).start();
            }
        });

        // set the monitor and receive initial state callback
        setGetStateExpectations(isNotRecovering, isNotRunning, isUnrecoverable);
        setMonitorExpectations("stoppedInError");
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

        // check state before proceeding with start
        setGetStateExpectations(isNotRecovering, isNotRunning, isUnrecoverable);

        // start will result in the monitor being updated to running
        setGetStateExpectations(isNotRecovering, isRunning, isUnrecoverable);
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
        setGetStateExpectations(isNotRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // check state before proceeding with start
        setGetStateExpectations(isNotRecovering, isRunning, isUnrecoverable);

        // monitor state updated after start invoked
        setGetStateExpectations(isNotRecovering, isRunning, isUnrecoverable);
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
        setGetStateExpectations(isRecovering, isRunning, isNotUnrecoverable);
        setMonitorExpectations("recovering");
        flow.setMonitor(monitor);

        // check state before proceeding with start
        setGetStateExpectations(isRecovering, isRunning, isNotUnrecoverable);

        // monitor state updated after start invoked
        setGetStateExpectations(isRecovering, isRunning, isNotUnrecoverable);
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

        final List<FlowElement<?>> configuredResourceInvokers = new ArrayList<FlowElement<?>>();

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();

        final List<FlowElement<ConfiguredResource>> multipleConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        multipleConfiguredResourceFlowElements.add(configuredResourceFlowElement);
        multipleConfiguredResourceFlowElements.add(configuredResourceFlowElement);

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

                oneOf(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));

                // exclusion flow invoker configuration
                oneOf(exclusionFlowConfiguration).getFlowElementInvokerConfiguredResources();
                will(returnValue(configuredResourceInvokers));

                // get dynamic configured resources
                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));

                // flow invoker configuration
                oneOf(flowConfiguration).getFlowElementInvokerConfiguredResources();
                will(returnValue(configuredResourceInvokers));

                // get the flow element configured resources
                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));

                // load monitor configuration and notifiers
                oneOf(monitor).getNotifiers();
                will(returnValue(notifiers));

                // get the two exclusion flow element configured resources
                oneOf(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(multipleConfiguredResourceFlowElements));

                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(multipleConfiguredResourceFlowElements));

                // load exclusion flow configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);
                will(throwException(exception));
            }
        });

        // set the monitor and receive initial state callback
        setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

        // this start will result in the monitor being updated to unrecoverable
        setGetStateExpectations(isNotRecovering, isNotRunning, isUnrecoverable);
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

        final List<FlowElement<?>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);

        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements = new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();

        // set start invocation expectations
        setStartExpectations(flow,dynamicConfiguredResourceFlowElements,configuredResourceFlowElements, managedResourceFlowElements);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // handle managed resources on the normal flow
                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                oneOf(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                oneOf(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

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
            }
        });

         // state is stopped when setting monitor and on init of start()
        setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setErrorReportingService(errorReportingService);
        flow.setMonitor(monitor);
        setGetStateExpectations(1, isNotRecovering, isRunning, isNotUnrecoverable);
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

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // clear recovery manager states
                oneOf(recoveryManager).initialise();

                // get the two flow element configured resources
                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));

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
        setGetStateExpectations(isNotRecovering, isNotRunning, isUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // check state before proceeding with start
        setGetStateExpectations(isNotRecovering, isNotRunning, isUnrecoverable);

        // start will result in the monitor being updated to running
        setGetStateExpectations(isNotRecovering, isRunning, isUnrecoverable);
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

        // set the monitor and receive initial state callback
        setGetStateExpectations(isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

        // this start will result in the monitor being updated to unrecoverable
        setGetStateExpectations(2, isRecovering, isRunning, isNotUnrecoverable);
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

        final List<FlowElement<?>> configuredResourceInvokers = new ArrayList<FlowElement<?>>();

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();

        final List<FlowElement<?>> flowElements = new ArrayList<FlowElement<?>>();

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

                // exclusion flow invoker configuration
                oneOf(exclusionFlowConfiguration).getFlowElementInvokerConfiguredResources();
                will(returnValue(configuredResourceInvokers));

                // get the two exclusion flow element configured resources
                oneOf(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));

                // load exclusion flow configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);

                oneOf(flowConfiguration).configure(flow);

                oneOf(flowConfiguration).getFlowElements();
                will(returnValue(flowElements));

                // inject errorReportingService to those needing it
                oneOf(flowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                oneOf(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                oneOf(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // inject errorReportingService to those needing it on the exclusion flow
                oneOf(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                oneOf(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                oneOf(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // load monitor dao and notifiers
                oneOf(monitor).getNotifiers();
                will(returnValue(notifiers));

                // load dao
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);

                // flow invoker configuration
                oneOf(flowConfiguration).getFlowElementInvokerConfiguredResources();
                will(returnValue(configuredResourceInvokers));

                // get any dynamic resources
                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));

                // get the exclusion flow element managed resources
                oneOf(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceExclusionFlowElements));

                // get the three flow element managed resources
                oneOf(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));
                oneOf(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                oneOf(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement3).getComponentName();
                will(returnValue("component name"));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));
                oneOf(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                oneOf(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement2).getComponentName();
                will(returnValue("component name"));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                oneOf(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                oneOf(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // set listener for tech callbacks
                oneOf(consumer).setListener(flow);

                // check eventFactory for consumer
                oneOf(consumer).getEventFactory();
                will(returnValue(flowEventFactory));

                // start the consumer
                oneOf(consumer).start();
                will(throwException(exception));

                // recovery manager invocation
                oneOf(consumerFlowElement).getComponentName();
                will(returnValue("consumerName"));
                oneOf(recoveryManager).recover("consumerName", exception);
            }
        });

        // set the monitor and receive initial state callback
        setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

        // this start will result in the monitor being updated to unrecoverable
        setGetStateExpectations(isNotRecovering, isNotRunning, isUnrecoverable);
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

        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        final Sequence reverseOrder = mockery.sequence("flowElements in reverse order");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                oneOf(recoveryManager).cancelAll();

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // nullify the listener for tech callbacks
                oneOf(consumer).setListener(null);

                // stop the consumer
                oneOf(consumer).stop();

                // get the three flow element managed resources
                oneOf(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // stop each managed resource from left to right in flow order
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("componentName1"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("componentName1"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("componentName2"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("componentName2"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("componentName3"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("componentName3"));
                inSequence(reverseOrder);

                // get the three exclusion flow element managed resources
                oneOf(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // stop each managed resource from left to right in flow order
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("componentName1"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("componentName1"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("componentName2"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement2).getComponentName();
                will(returnValue("componentName2"));
                inSequence(reverseOrder);

                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("componentName3"));
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getFlowComponent();
                will(returnValue(managedResource));
                inSequence(reverseOrder);
                oneOf(managedResource).stopManagedResource();
                inSequence(reverseOrder);
                oneOf(managedResourceFlowElement3).getComponentName();
                will(returnValue("componentName3"));
                inSequence(reverseOrder);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory);

        // state is stopped when setting monitor and on init of start()
        setGetStateExpectations(1, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(isNotRecovering, isNotRunning, isNotUnrecoverable);
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
                oneOf(recoveryManager).cancelAll();

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // nullify the listener for tech callbacks
                oneOf(consumer).setListener(null);

                // stop the consumer
                oneOf(consumer).stop();

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
        setGetStateExpectations(isNotRecovering, isRunning, isUnrecoverable);
        setMonitorExpectations("running");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(isNotRecovering, isNotRunning, isNotUnrecoverable);
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
                oneOf(recoveryManager).cancelAll();

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // nullify the listener for tech callbacks
                oneOf(consumer).setListener(null);

                // stop the consumer
                oneOf(consumer).stop();

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
        setGetStateExpectations(isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(isNotRecovering, isNotRunning, isNotUnrecoverable);
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
                oneOf(recoveryManager).cancelAll();

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // nullify the listener for tech callbacks
                oneOf(consumer).setListener(null);

                // stop the consumer
                oneOf(consumer).stop();

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
        setGetStateExpectations(isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
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
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always get the original exceptionLifeIdentifier
                oneOf(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                oneOf(flowInvocationContext).startFlowInvocation();
                oneOf(flowInvocationContext).endFlowInvocation();

                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                oneOf(flowConfiguration).getReplayRecordService();
                will(returnValue(replayRecordService));

                exactly(2).of(flowConfiguration).configure(dynamicConfiguredResource);

                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

                oneOf(flowElementInvoker).setFlowInvocationContextListeners(null);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(flowElementInvoker).setInvokeContextListeners(false);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(flowElementInvoker).setIgnoreContextInvocation(true);

                oneOf(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, consumerFlowElement);
                will(returnValue(null));

                oneOf(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                exactly(2).of(flowConfiguration).update(dynamicConfiguredResource);

                // in this test we do not need to cancelAll recovery
                oneOf(recoveryManager).isRecovering();
                will(returnValue(false));

                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getComponentName();
                will(returnValue("consumer"));
                oneOf(flowInvocationContext).setLastComponentName("consumer");
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

        // set start invocation expectations
        setStartExpectations(flow,dynamicConfiguredResourceFlowElements,configuredResourceFlowElements);

        // state is stopped when setting monitor and on init of start()
        setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(2, isNotRecovering, isRunning, isNotUnrecoverable);
        setMonitorExpectations("running");
        flow.setFlowListener(flowEventListener);
        setMonitorExpectations("running");
        flow.start();
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
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always get the original exceptionLifeIdentifier
                oneOf(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                oneOf(flowInvocationContext).startFlowInvocation();
                oneOf(flowInvocationContext).endFlowInvocation();

                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                exactly(2).of(flowConfiguration).configure(dynamicConfiguredResource);

                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

                oneOf(flowElementInvoker).setFlowInvocationContextListeners(flowInvocationContextListeners);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(flowElementInvoker).setInvokeContextListeners(true);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

                FlowElementPersistentConfiguration configuration = new FlowElementPersistentConfiguration();
                configuration.setCaptureMetrics(true);
                configuration.setSnapEvent(true);

                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(configuration));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(configuration));
                oneOf(flowElementInvoker).setIgnoreContextInvocation(false);
                oneOf(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, consumerFlowElement);
                will(returnValue(null));

                oneOf(flowConfiguration).getReplayRecordService();
                will(returnValue(replayRecordService));

                oneOf(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                exactly(2).of(flowConfiguration).update(dynamicConfiguredResource);

                oneOf(flowInvocationContextListener).endFlow(flowInvocationContext);

                // in this test we do not need to cancelAll recovery
                oneOf(recoveryManager).isRecovering();
                will(returnValue(false));

                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getComponentName();
                will(returnValue("consumer"));
                oneOf(flowInvocationContext).setLastComponentName("consumer");
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);
        flow.getConfiguration().setInvokeContextListeners(true);
        flow.setFlowInvocationContextListeners(flowInvocationContextListeners);

        // set start invocation expectations
        setStartExpectations(flow,dynamicConfiguredResourceFlowElements,configuredResourceFlowElements);

        // state is stopped when setting monitor and on init of start()
        setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(2, isNotRecovering, isRunning, isNotUnrecoverable);
        setMonitorExpectations("running");
        flow.setFlowListener(flowEventListener);
        setMonitorExpectations("running");
        flow.start();
        flow.invoke(flowEvent);

        // test assertions
        mockery.assertIsSatisfied();
    }

    /**
     * Set all expectations for a standard start()
     */
    public void setStartExpectations(VisitingInvokerFlow flow,
                                     final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements,
                                     final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements)
    {
        final List<FlowElement<?>> managedResourceFlowElements = new ArrayList<FlowElement<?>>();
        final List<FlowElement<?>> managedResourceExclusionFlowElements = new ArrayList<FlowElement<?>>();
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements,
                managedResourceFlowElements, managedResourceExclusionFlowElements);
    }

    /**
     * Set all expectations for a standard start()
     */
    public void setStartExpectations(VisitingInvokerFlow flow,
                                     final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements,
                                     final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements,
                                     final List<FlowElement<?>> managedResourceFlowElements)
    {
        final List<FlowElement<?>> managedResourceExclusionFlowElements = new ArrayList<FlowElement<?>>();
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements,
                managedResourceFlowElements, managedResourceExclusionFlowElements);
    }

    /**
     * Set all expectations for a standard start()
     */
    public void setStartExpectations(VisitingInvokerFlow flow,
                                     final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements,
                                     final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements,
                                     final List<FlowElement<?>> managedResourceFlowElements,
                                     final List<FlowElement<?>> managedResourceExclusionFlowElements)
    {
        final List<Notifier> notifiers = new ArrayList<>();
        final List<FlowElement<?>> configuredResourceInvokers = new ArrayList<FlowElement<?>>();
        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements = new ArrayList<>();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // init recovery manager
                oneOf(recoveryManager).initialise();
                // load monitor configuration and notifiers
                oneOf(monitor).getNotifiers();
                will(returnValue(notifiers));
                // exclusion flow invoker configuration
                oneOf(exclusionFlowConfiguration).getFlowElementInvokerConfiguredResources();
                will(returnValue(configuredResourceInvokers));
                oneOf(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                exactly(1).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));
                exactly(1).of(configuredResource).getConfiguredResourceId();
                will(returnValue("id"));
                exactly(1).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));
                exactly(1).of(flowConfiguration).configure(configuredResource);
                oneOf(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));
                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));
                oneOf(flowConfiguration).getFlowElementInvokerConfiguredResources();
                will(returnValue(configuredResourceInvokers));
                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));
                exactly(2).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));
                exactly(1).of(configuredResource).getConfiguredResourceId();
                will(returnValue("id"));
                exactly(1).of(flowConfiguration).configure(configuredResource);
                exactly(1).of(flowConfiguration).getFlowElements();
                will(returnValue(configuredResourceFlowElements));
                exactly(1).of(configuredResourceFlowElement).getConfiguredResourceId();
                will(returnValue("id"));
                exactly(1).of(flowConfiguration).configure(configuredResourceFlowElement);
                exactly(1).of(flowConfiguration).configure(flow);
                oneOf(flowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));
                exactly(1).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));
                // exclusion flow element flow managed resource flow elements
                exactly(1).of(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceExclusionFlowElements));
                // normal flow element flow managed resource flow elements
                exactly(1).of(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));
                exactly(1).of(recoveryManager).setManagedResources(managedResourceFlowElements);
                exactly(1).of(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                exactly(1).of(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));
                exactly(1).of(consumer).setListener(flow);
                exactly(1).of(consumer).getEventFactory();
                will(returnValue(flowEventFactory));
                exactly(1).of(consumer).start();
            }
        });
    }

    /**
     * Test successful flow invoke with a flow event with a context listener that is disabled.
     */
    @Test
    public void test_successful_flow_invoke_with_flowEvent_with_contextListener_disabled()
    {
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always get the original exceptionLifeIdentifier
                oneOf(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                //
                // expectations from invoke
                oneOf(flowInvocationContext).startFlowInvocation();
                oneOf(flowInvocationContext).endFlowInvocation();

                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                exactly(2).of(flowConfiguration).configure(dynamicConfiguredResource);

                oneOf(flowConfiguration).getReplayRecordService();
                will(returnValue(replayRecordService));

                exactly(2).of(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

                oneOf(flowElementInvoker).setFlowInvocationContextListeners(flowInvocationContextListeners);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(flowElementInvoker).setInvokeContextListeners(false);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(flowElementInvoker).setIgnoreContextInvocation(true);
                oneOf(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, consumerFlowElement);
                will(returnValue(null));

                oneOf(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                exactly(2).of(flowConfiguration).update(dynamicConfiguredResource);

                // in this test we do not need to cancelAll recovery
                oneOf(recoveryManager).isRecovering();
                will(returnValue(false));

                oneOf(consumerFlowElement).getComponentName();
                will(returnValue("consumer"));
                oneOf(flowInvocationContext).setLastComponentName("consumer");
            }
        });

        // set start invocation expectations
        setStartExpectations(flow,dynamicConfiguredResourceFlowElements,configuredResourceFlowElements);

        flow.setFlowInvocationContextListeners(flowInvocationContextListeners);
        flow.stopContextListeners();

        // state is stopped when setting monitor and on init of start()
        setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(2, isNotRecovering, isRunning, isNotUnrecoverable);
        setMonitorExpectations("running");
        flow.setFlowListener(flowEventListener);
        setMonitorExpectations("running");
        flow.start();
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
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always get the original exceptionLifeIdentifier
                oneOf(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                oneOf(flowInvocationContext).startFlowInvocation();
                oneOf(flowInvocationContext).endFlowInvocation();

                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                oneOf(flowConfiguration).getReplayRecordService();
                will(returnValue(replayRecordService));

                exactly(2).of(flowConfiguration).configure(dynamicConfiguredResource);

                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

                oneOf(flowElementInvoker).setFlowInvocationContextListeners(flowInvocationContextListeners);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(flowElementInvoker).setInvokeContextListeners(true);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

                exactly(1).of(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));

                oneOf(consumerFlowElement).getConfiguration();
                will(returnValue(new FlowElementPersistentConfiguration()));
                oneOf(flowElementInvoker).setIgnoreContextInvocation(true);
                oneOf(flowElementInvoker).invoke(flowEventListener, "moduleName", "flowName", flowInvocationContext, flowEvent, consumerFlowElement);
                will(returnValue(null));

                oneOf(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                exactly(2).of(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                exactly(2).of(flowConfiguration).update(dynamicConfiguredResource);

                oneOf(flowInvocationContextListener).endFlow(flowInvocationContext);

                // in this test we do not need to cancelAll recovery
                oneOf(recoveryManager).isRecovering();
                will(returnValue(false));

                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getComponentName();
                will(returnValue("consumer"));
                oneOf(flowInvocationContext).setLastComponentName("consumer");
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);
        flow.getConfiguration().setInvokeContextListeners(true);
        flow.setFlowInvocationContextListeners(flowInvocationContextListeners);
        flow.stopContextListeners();
        flow.startContextListeners();

        // set start invocation expectations
        setStartExpectations(flow,dynamicConfiguredResourceFlowElements,configuredResourceFlowElements);

        setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(isNotRecovering, isRunning, isNotUnrecoverable);
        setMonitorExpectations("running");
        flow.setFlowListener(flowEventListener);
        flow.start();

        setGetStateExpectations(isNotRecovering, isRunning, isNotUnrecoverable);
        setMonitorExpectations("running");
        flow.invoke(flowEvent);

        // test assertions
        mockery.assertIsSatisfied();
    }


    /**
     * Test failed flow invoke with a flow event, but dynamic dao failing.
     */
    @Test(expected = ForceTransactionRollbackException.class)
    public void test_failed_flow_invoke_with_flowEvent_stoppingInError_due_to_dynamicConfiguration_failure()
    {
        final RuntimeException exception = new RuntimeException("test failed dynamic dao");
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

        // set start invocation expectations
        setStartExpectations(flow,dynamicConfiguredResourceFlowElements,configuredResourceFlowElements);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always get the original exceptionLifeIdentifier
                oneOf(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                oneOf(flowInvocationContext).startFlowInvocation();
                oneOf(flowInvocationContext).endFlowInvocation();

                oneOf(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                oneOf(flowConfiguration).configure(configuredResource);
                will(throwException(exception));

                oneOf(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                // pass the exception to the recovery manager
                oneOf(recoveryManager).recover(flowInvocationContext, exception, flowEvent, "identifier");
                will(throwException(new ForceTransactionRollbackException("")));

                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getComponentName();
                will(returnValue("consumer"));
                oneOf(flowInvocationContext).setLastComponentName("consumer");

            }
        });

        // set state as stopped when setting monitor
        setGetStateExpectations(1, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        try
        {
            // state on entry to start
            setGetStateExpectations(1, isNotRecovering, isNotRunning, isNotUnrecoverable);

            // state on exit from start
            setGetStateExpectations(1, isNotRecovering, isRunning, isNotUnrecoverable);
            setMonitorExpectations("running");
            flow.start();

            // run invoke
            setGetStateExpectations(isNotRecovering, isNotRunning, isUnrecoverable);
            setMonitorExpectations("stoppedInError");
            flow.invoke(flowEvent);
        }
        catch(ForceTransactionRollbackException e)
        {
            // test assertions
            mockery.assertIsSatisfied();
            throw e;
        }

    }

    /**
     * Test failed flow invoke with a flow event with the resulting outcome being stoppedInError.
     */
    @Test(expected = ForceTransactionRollbackException.class)
    public void test_failed_flow_invoke_with_flowEvent_resulting_in_stoppedInError()
    {
        final RuntimeException exception = new RuntimeException("test failed flow invocation");
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always get the original exceptionLifeIdentifier
                oneOf(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                oneOf(flowInvocationContext).startFlowInvocation();
                oneOf(flowInvocationContext).endFlowInvocation();

                oneOf(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                oneOf(flowConfiguration).configure(configuredResource);

                oneOf(flowConfiguration).getReplayRecordService();
                will(returnValue(replayRecordService));

                oneOf(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                // invoke the flow elements
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

                oneOf(flowElementInvoker).setFlowInvocationContextListeners(null);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(flowElementInvoker).setInvokeContextListeners(false);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

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
                will(throwException(new ForceTransactionRollbackException("")));

                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getComponentName();
                will(returnValue("consumer"));
                oneOf(flowInvocationContext).setLastComponentName("consumer");
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

        // set start invocation expectations
        setStartExpectations(flow,dynamicConfiguredResourceFlowElements,configuredResourceFlowElements);

        setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        setGetStateExpectations(1, isNotRecovering, isRunning, isNotUnrecoverable);
        setMonitorExpectations("running");
        flow.start();

        // run test
        setGetStateExpectations(isNotRecovering, isNotRunning, isUnrecoverable);
        setMonitorExpectations("stoppedInError");
        flow.setFlowListener(flowEventListener);

        try
        {
            flow.invoke(flowEvent);
        }
        catch(ForceTransactionRollbackException e)
        {
            // test assertions
            mockery.assertIsSatisfied();
            throw e;
        }

    }

    /**
     * Test failed flow invoke with a flow event with the resulting outcome being recovering.
     */
    @Test(expected = ForceTransactionRollbackException.class)
    public void test_failed_flow_invoke_with_flowEvent_resulting_in_recovery()
    {
        final RuntimeException exception = new RuntimeException("test failed flow invocation");
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always get the original exceptionLifeIdentifier
                oneOf(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                oneOf(flowInvocationContext).startFlowInvocation();
                oneOf(flowInvocationContext).endFlowInvocation();

                oneOf(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                oneOf(flowConfiguration).configure(configuredResource);

                oneOf(flowConfiguration).getReplayRecordService();
                will(returnValue(replayRecordService));

                oneOf(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                // invoke the flow elements
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

                oneOf(flowElementInvoker).setFlowInvocationContextListeners(null);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(flowElementInvoker).setInvokeContextListeners(false);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

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
                will(throwException(new ForceTransactionRollbackException("")));

                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getComponentName();
                will(returnValue("consumer"));
                oneOf(flowInvocationContext).setLastComponentName("consumer");
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

        // set start invocation expectations
        setStartExpectations(flow,dynamicConfiguredResourceFlowElements,configuredResourceFlowElements);

        try
        {
            // state is stopped when setting monitor and on init of start()
            setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
            setMonitorExpectations("stopped");
            flow.setMonitor(monitor);
            setGetStateExpectations(1, isNotRecovering, isRunning, isNotUnrecoverable);
            setMonitorExpectations("running");
            flow.start();

            // run test
            flow.setFlowListener(flowEventListener);
            setGetStateExpectations(isRecovering, isRunning, isNotUnrecoverable);
            setMonitorExpectations("recovering");
            flow.invoke(flowEvent);
        }
        catch(ForceTransactionRollbackException e)
        {
            // test assertions
            mockery.assertIsSatisfied();

            throw e;
        }

    }

    /**
     * Test failed flow invoke with a flow event with the resulting outcome being recovering.
     */
    @Test
    public void test_failed_flow_invoke_with_flowEvent_resulting_in_ignore()
    {
        final RuntimeException exception = new RuntimeException("test failed flow invocation");
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always get the original exceptionLifeIdentifier
                oneOf(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                oneOf(flowInvocationContext).startFlowInvocation();
                oneOf(flowInvocationContext).endFlowInvocation();

                oneOf(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                oneOf(flowConfiguration).configure(configuredResource);

                oneOf(flowConfiguration).getReplayRecordService();
                will(returnValue(replayRecordService));

                oneOf(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                // invoke the flow elements
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

                oneOf(flowElementInvoker).setFlowInvocationContextListeners(null);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(flowElementInvoker).setInvokeContextListeners(false);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

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
                // ignore doesnt throw exception

                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getComponentName();
                will(returnValue("consumer"));
                oneOf(flowInvocationContext).setLastComponentName("consumer");

                oneOf(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                oneOf(flowConfiguration).update(dynamicConfiguredResource);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

        // set start invocation expectations
        setStartExpectations(flow,dynamicConfiguredResourceFlowElements,configuredResourceFlowElements);

        // state is stopped when setting monitor and on init of start()
        setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(isNotRecovering, isRunning, isNotUnrecoverable);
        setMonitorExpectations("running");
        flow.setFlowListener(flowEventListener);
        flow.start();

        setGetStateExpectations(isNotRecovering, isRunning, isNotUnrecoverable);
        setMonitorExpectations("running");
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
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // always get the original exceptionLifeIdentifier
                oneOf(flowEvent).getIdentifier();
                will(returnValue("identifier"));

                oneOf(flowInvocationContext).startFlowInvocation();
                oneOf(flowInvocationContext).endFlowInvocation();

                oneOf(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                oneOf(flowConfiguration).configure(configuredResource);

                oneOf(flowConfiguration).getReplayRecordService();
                will(returnValue(replayRecordService));

                oneOf(exclusionService).isBlackListed("identifier");
                will(returnValue(false));

                // invoke the flow elements
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

                oneOf(flowElementInvoker).setFlowInvocationContextListeners(null);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));
                oneOf(flowElementInvoker).setInvokeContextListeners(false);
                oneOf(consumerFlowElement).getFlowElementInvoker();
                will(returnValue(flowElementInvoker));

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
                // no exceptions thrown to rollback

                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getComponentName();
                will(returnValue("consumer"));
                oneOf(flowInvocationContext).setLastComponentName("consumer");

                oneOf(dynamicConfiguredResourceFlowElement).getFlowComponent();
                will(returnValue(dynamicConfiguredResource));

                oneOf(flowConfiguration).update(dynamicConfiguredResource);
            }
        });

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName",
                flowConfiguration, recoveryManager, exclusionService);

        // set start invocation expectations
        setStartExpectations(flow,dynamicConfiguredResourceFlowElements,configuredResourceFlowElements);

        // state is stopped when setting monitor and on init of start()
        setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);
        setGetStateExpectations(1, isNotRecovering, isRunning, isNotUnrecoverable);
        setMonitorExpectations("running");
        flow.start();

        // run test
        setGetStateExpectations(isNotRecovering, isRunning, isUnrecoverable);
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
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

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

        // set start invocation expectations
        setStartExpectations(flow,dynamicConfiguredResourceFlowElements,configuredResourceFlowElements);

        // state is stopped when setting monitor and on init of start()
        setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(2, isNotRecovering, isRunning, isNotUnrecoverable);
        setMonitorExpectations("running");
        flow.setFlowListener(flowEventListener);
        setMonitorExpectations("running");
        flow.start();
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
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

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

        // set start invocation expectations
        setStartExpectations(flow,dynamicConfiguredResourceFlowElements,configuredResourceFlowElements);

        setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);
        setGetStateExpectations(1, isNotRecovering, isRunning, isNotUnrecoverable);
        setMonitorExpectations("running");
        flow.start();

        // run test
        setGetStateExpectations(isRecovering, isRunning, isNotUnrecoverable);
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
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

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

        // set start invocation expectations
        setStartExpectations(flow,dynamicConfiguredResourceFlowElements,configuredResourceFlowElements);

        // state is stopped when setting monitor and on init of start()
        setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);

        // run test
        setGetStateExpectations(1, isNotRecovering, isRunning, isNotUnrecoverable);
        setMonitorExpectations("running");
        flow.setFlowListener(flowEventListener);
        setGetStateExpectations(1, isNotRecovering, isNotRunning, isUnrecoverable);
        setMonitorExpectations("stoppedInError");
        flow.start();
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
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();

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

        final List<FlowElement<?>> managedResourceExclusionFlowElements = new ArrayList<>();
        managedResourceExclusionFlowElements.add(managedResourceFlowElementExclusion1);

        final List<FlowElement<?>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);

        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements = new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();

        // set start invocation expectations
        setStartExpectations(flow,dynamicConfiguredResourceFlowElements,configuredResourceFlowElements,
                managedResourceFlowElements, managedResourceExclusionFlowElements);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // handle managed resources on the exclusion flow
                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElementExclusion1).getFlowComponent();
                will(returnValue(managedResource));
                oneOf(managedResourceFlowElementExclusion1).getComponentName();
                will(returnValue("component name"));
                oneOf(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                oneOf(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElementExclusion1).getComponentName();
                will(returnValue("component name"));

                // handle managed resources on the normal flow
                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                oneOf(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                oneOf(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("managedResourceFlowElementName"));
            }
        });

        // set the monitor and receive initial state callback
        setGetStateExpectations(2, isNotRecovering, isNotRunning, isNotUnrecoverable);
        setMonitorExpectations("stopped");
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

        // start will result in the monitor being updated to running
        setGetStateExpectations(isNotRecovering, isRunning, isNotUnrecoverable);
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

        final List<FlowElement<?>> configuredResourceInvokers = new ArrayList<FlowElement<?>>();

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);

        final List<FlowElement<?>> flowElements = new ArrayList<FlowElement<?>>();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // load monitor configuration and notifiers
                oneOf(monitor).getNotifiers();
                will(returnValue(notifiers));

                // get the two flow element configured resources
                oneOf(flowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));

                // load flow configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);

                // get the two exclusion flow element configured resources
                oneOf(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
                will(returnValue(configuredResourceFlowElements));

                // load exclusion flow configuration
                exactly(4).of(configuredResourceFlowElement).getFlowComponent();
                will(returnValue(configuredResource));

                exactly(2).of(configuredResource).getConfiguredResourceId();
                will(returnValue("configuredResourceId"));

                exactly(2).of(flowConfiguration).configure(configuredResource);

                oneOf(flowConfiguration).configure(flow);

                // flow dynamic configuration
                oneOf(flowConfiguration).getDynamicConfiguredResourceFlowElements();
                will(returnValue(dynamicConfiguredResourceFlowElements));

                // flow invoker configuration
                oneOf(flowConfiguration).getFlowElementInvokerConfiguredResources();
                will(returnValue(configuredResourceInvokers));

                // exclusion flow invoker configuration
                oneOf(exclusionFlowConfiguration).getFlowElementInvokerConfiguredResources();
                will(returnValue(configuredResourceInvokers));

                oneOf(flowConfiguration).getFlowElements();
                will(returnValue(flowElements));

                // inject errorReportingService to those needing it on the business flow
                oneOf(flowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                oneOf(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                oneOf(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // inject errorReportingService to those needing it on the exclusion flow
                oneOf(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
                will(returnValue(errorReportingServiceAwareFlowElements));

                oneOf(errorReportingServiceAwareFlowElement1).getFlowComponent();
                will(returnValue(errorReportingServiceAwareComponent));

                oneOf(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

                // get the three flow element managed resources
                oneOf(exclusionFlowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceExclusionFlowElements));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElementExclusion1).getFlowComponent();
                will(returnValue(exclusionManagedResource));
                oneOf(managedResourceFlowElementExclusion1).getComponentName();
                will(returnValue("exclusion component name"));
                oneOf(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("exclusion component name");
                will(returnValue(exclusionManagedResourceRecoveryManager));
                oneOf(exclusionManagedResource).setManagedResourceRecoveryManager(exclusionManagedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(exclusionManagedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElementExclusion1).getComponentName();
                will(returnValue("exclusion component name"));


                // get the three flow element managed resources
                oneOf(flowConfiguration).getManagedResourceFlowElements();
                will(returnValue(managedResourceFlowElements));

                // clear recovery manager states
                oneOf(recoveryManager).initialise();
                // pass any managed resources to the recovery manager
                oneOf(recoveryManager).setManagedResources(with(any(List.class)));

                // set the managed resource recovery manager instance on each managed resource
                oneOf(managedResourceFlowElement1).getFlowComponent();
                will(returnValue(managedResource));
                oneOf(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));
                oneOf(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name");
                will(returnValue(managedResourceRecoveryManager));
                oneOf(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);

                // start each managed resource from right to left (reverse order) in flow order
                inSequence(reverseOrder);
                oneOf(managedResource).startManagedResource();
                exactly(2).of(managedResourceFlowElement1).getComponentName();
                will(returnValue("component name"));

                // get the consumer
                oneOf(flowConfiguration).getConsumerFlowElement();
                will(returnValue(consumerFlowElement));
                oneOf(consumerFlowElement).getFlowComponent();
                will(returnValue(consumer));

                // set listener for tech callbacks
                oneOf(consumer).setListener(flow);

                // check eventFactory for consumer
                oneOf(consumer).getEventFactory();
                will(returnValue(flowEventFactory));

                // start the consumer
                oneOf(consumer).start();
            }
        });

        // set the monitor and receive initial state callback
        setGetStateExpectations(isNotRecovering, isNotRunning, isUnrecoverable);
        setMonitorExpectations("stoppedInError");
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

        // check state before proceeding with start
        setGetStateExpectations(isNotRecovering, isNotRunning, isUnrecoverable);

        // start will result in the monitor being updated to running
        setGetStateExpectations(isNotRecovering, isRunning, isNotUnrecoverable);
        setMonitorExpectations("running");
        flow.start();

        // test assertions
        mockery.assertIsSatisfied();
    }

    private void setGetStateExpectations(final boolean recovering, final boolean running, final boolean unrecoverable)
    {
        setGetStateExpectations(1, recovering, running, unrecoverable);
    }

        /**
         * Set the getState expectations based on the incoming parameters.
         * @param recovering
         * @param running
         * @param unrecoverable
         */
    private void setGetStateExpectations(final int iteration, boolean recovering, boolean running, boolean unrecoverable)
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                for(int it = 0; it < iteration; it++)
                {
                    // set expectations for establishing state
                    oneOf(recoveryManager).isRecovering();
                    will(returnValue(recovering));

                    if(!recovering)
                    {
                        oneOf(flowConfiguration).getConsumerFlowElement();
                        will(returnValue(consumerFlowElement));
                        oneOf(consumerFlowElement).getFlowComponent();
                        will(returnValue(consumer));
                        oneOf(consumer).isRunning();
                        will(returnValue(running));
                    }

                    if(!recovering && !running)
                    {
                        oneOf(recoveryManager).isUnrecoverable();
                        will(returnValue(unrecoverable));
                    }
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
                oneOf(monitor).invoke(state);
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
                                           RecoveryManager<FlowEvent<?,?>, FlowInvocationContext, ?> recoveryManager,
                                           ExclusionService exclusionService)
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
