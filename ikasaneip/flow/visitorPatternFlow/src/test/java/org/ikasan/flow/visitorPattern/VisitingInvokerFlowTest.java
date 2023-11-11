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

import org.ikasan.flow.event.FlowEventFactory;
import org.ikasan.flow.visitorPattern.VisitingInvokerFlow.ManagedResourceRecoveryManagerFactory;
import org.ikasan.flow.visitorPattern.invoker.InvokerConfiguration;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This test class supports the <code>VisitingInvokerFlow</code> class.
 *
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
public class VisitingInvokerFlowTest
{

    /**
     * Mock flowConfiguration
     */
    private final FlowConfiguration flowConfiguration = Mockito.spy(FlowConfiguration.class);

    /**
     * Mock exclusionFlowConfiguration
     */
    private final ExclusionFlowConfiguration exclusionFlowConfiguration = Mockito
        .mock(ExclusionFlowConfiguration.class, "mockExclusionFlowConfiguration");

    /**
     * Mock flowElementInvoker
     */
    private final FlowElementInvoker flowElementInvoker = Mockito
        .mock(FlowElementInvoker.class, "mockFlowElementInvoker");

    /**
     * Mock flowEventListener
     */
    private final FlowEventListener flowEventListener = Mockito.mock(FlowEventListener.class, "mockFlowEventListener");

    /**
     * Mock recoveryManager
     */
    private final RecoveryManager recoveryManager = Mockito.spy(RecoveryManager.class);

    /**
     * Mock recoveryManager
     */
    private final ErrorReportingService errorReportingService = Mockito
        .mock(ErrorReportingService.class, "mockErrorReportingService");

    private final ReplayRecordService replayRecordService = Mockito
        .mock(ReplayRecordService.class, "mockReplayRecordService");

    /**
     * Mock managedResourceRecoveryManagerFactory
     */
    private final ManagedResourceRecoveryManagerFactory managedResourceRecoveryManagerFactory = Mockito
        .mock(ManagedResourceRecoveryManagerFactory.class, "mockManagedResourceRecoveryManagerFactory");

    /**
     * Mock managedResourceRecoveryManager
     */
    private final ManagedResourceRecoveryManager managedResourceRecoveryManager = Mockito
        .mock(ManagedResourceRecoveryManager.class, "mockManagedResourceRecoveryManager");

    /**
     * Mock managedResourceRecoveryManager
     */
    private final ManagedResourceRecoveryManager exclusionManagedResourceRecoveryManager = Mockito
        .mock(ManagedResourceRecoveryManager.class, "mockExclusionManagedResourceRecoveryManager");

    /**
     * Mock configured resource flow elements
     */
    private final FlowElement<ConfiguredResource> configuredResourceFlowElement = Mockito
        .mock(FlowElement.class, "mockFlowElementConfiguredResource");

    /**
     * Mock dynamic configured resource flow elements
     */
    private final FlowElement<ConfiguredResource> dynamicConfiguredResourceFlowElement = Mockito
        .mock(FlowElement.class, "mockFlowElementDynamicConfiguredResource");

    /**
     * Mock managed resource flow element 1
     */
    private final FlowElement<ManagedResource> managedResourceFlowElement1 = Mockito
        .mock(FlowElement.class, "mockFlowElementManagedResource1");

    /**
     * Mock errorReportingService aware resource flow element 1
     */
    private final FlowElement<IsErrorReportingServiceAware> errorReportingServiceAwareFlowElement1 = Mockito
        .mock(FlowElement.class, "mockErrorReportingServiceAwareFlowElement1");

    /**
     * Mock managed resource flow element 2
     */
    private final FlowElement<ManagedResource> managedResourceFlowElement2 = Mockito
        .mock(FlowElement.class, "mockFlowElementManagedResource2");

    /**
     * Mock managed resource flow element 3
     */
    private final FlowElement<ManagedResource> managedResourceFlowElement3 = Mockito
        .mock(FlowElement.class, "mockFlowElementManagedResource3");

    /**
     * Mock managed resource flow element 1
     */
    private final FlowElement<ManagedResource> managedResourceFlowElementExclusion1 = Mockito
        .mock(FlowElement.class, "mockManagedResourceFlowElementExclusion1");

    /**
     * mock managed resource
     */
    private final ManagedResource managedResource = Mockito.mock(ManagedResource.class, "mockManagedResource");

    /**
     * mock errorReportingService aware component
     */
    private final IsErrorReportingServiceAware errorReportingServiceAwareComponent = Mockito
        .mock(IsErrorReportingServiceAware.class, "mockIsErrorReportingServiceAware");

    /**
     * mock managed resource
     */
    private final ManagedResource exclusionManagedResource = Mockito
        .mock(ManagedResource.class, "mockExclusionManagedResource");

    /**
     * mock managed resource
     */
    private final ConfiguredResource configuredResource = Mockito
        .mock(ConfiguredResource.class, "mockConfiguredResource");

    /**
     * mock managed resource
     */
    private final ConfiguredResource dynamicConfiguredResource = Mockito
        .mock(ConfiguredResource.class, "mockDynamicConfiguredResource");

    /**
     * Mock consumer flowElement
     */
    private final FlowElement<Consumer> consumerFlowElement = Mockito
        .mock(FlowElement.class, "mockConsumerFlowElement");

    /**
     * Mock consumer
     */
    private final Consumer<EventListener<FlowEvent<?, ?>>, EventFactory> consumer = Mockito
        .mock(Consumer.class, "mockConsumer");

    /**
     * Mock flow event
     */
    private final FlowEvent flowEvent = Mockito.mock(FlowEvent.class, "mockFlowEvent");

    /**
     * Mock monitor
     */
    private final Monitor monitor = Mockito.spy(Monitor.class);

    /**
     * Mock flow invocation context
     */
    private final FlowInvocationContext flowInvocationContext = Mockito
        .mock(FlowInvocationContext.class, "mockFlowInvocationContext");

    /**
     * Mock flow event factory
     */
    private final FlowEventFactory flowEventFactory = Mockito.mock(FlowEventFactory.class, "mockFlowEventFactory");

    /**
     * Mock exclusionService
     */
    private final ExclusionService exclusionService = Mockito.mock(ExclusionService.class, "mockExclusionService");

    /**
     * Mock serialiserFactory
     */
    private final SerialiserFactory serialiserFactory = Mockito.mock(SerialiserFactory.class, "mockSerialiserFactory");

    private final FlowInvocationContextListener flowInvocationContextListener = Mockito
        .mock(FlowInvocationContextListener.class, "flowInvocationContextListener");

    private final List<FlowInvocationContextListener> flowInvocationContextListeners = Collections
        .singletonList(flowInvocationContextListener);

    /**
     * is recovering status
     */
    private final boolean isRecovering = true;

    private final boolean isNotRecovering = false;

    /**
     * is running status
     */
    private final boolean isNotRunning = false;

    private final boolean isRunning = true;

    /**
     * is unrecoverable status
     */
    private final boolean isUnrecoverable = true;

    private final boolean isNotUnrecoverable = false;

    private List<FlowEventListener> flowEventListeners = new ArrayList<FlowEventListener>();

    @BeforeEach
    void setup()
    {
        // nothing to setup
    }

    /**
     * Test failed constructor due to null flow name.
     */
    @Test
    void test_failed_constructorDueToNullName()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new VisitingInvokerFlow(null, null, null, null, null, null, null);
        });
    }

    /**
     * Test failed constructor due to null module name.
     */
    @Test
    void test_failed_constructorDueToNullModuleName()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new VisitingInvokerFlow("flowName", null, null, null, null, null, null);
        });
    }

    /**
     * Test failed constructor due to null flow configuration.
     */
    @Test
    void test_failed_constructorDueToNullFlowConfiguration()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new VisitingInvokerFlow("flowName", "moduleName", null, null, null, null, null);
        });
    }

    /**
     * Test failed constructor due to null flow element invoker.
     */
    @Test
    void test_failed_constructorDueToNullExclusionFlowConfigurationInvoker()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration, null, null, null, null);
        });
    }

    /**
     * Test failed constructor due to null flow recovery manager.
     */
    @Test
    void test_failed_constructorDueToNullFlowRecoveryManager()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration, exclusionFlowConfiguration, null, null,
                null
            );
        });
    }

    /**
     * Test failed constructor due to null flow recovery manager.
     */
    @Test
    void test_failed_constructorDueToNullFlowExclusionService()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration, exclusionFlowConfiguration,
                recoveryManager, null, null
            );
        });
    }

    /**
     * Test successful visiting flow invoker instantiation.
     */
    @Test
    void test_successful_VisitingInvokerFlow_instantiation()
    {
        Flow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration, exclusionFlowConfiguration,
            recoveryManager, exclusionService, serialiserFactory
        );
        assertEquals("flowName", flow.getName(), "flowName setter failed");
        assertEquals("moduleName", flow.getModuleName(), "moduleName setter failed");
    }

    /**
     * Test successful flow start from a stopped state.
     */
    @Test
    void test_successful_flow_start_from_stopped()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory
        );
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements =
            new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final List<FlowElement<ManagedResource>> managedResourceExclusionFlowElements = new ArrayList<>();

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);

        final List<Notifier> notifiers = new ArrayList<>();

        final List<FlowElement<?>> configuredResourceInvokers = new ArrayList<FlowElement<?>>();

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();

        final List<FlowElement<?>> flowElements = new ArrayList<>();

        // expectations
        // get the two flow element configured resources
        Mockito.when(flowConfiguration.getConfiguredResourceFlowElements()).thenReturn(configuredResourceFlowElements);

        // get any dynamic resources
        Mockito.when(flowConfiguration.getDynamicConfiguredResourceFlowElements())
               .thenReturn(dynamicConfiguredResourceFlowElements);

        // load monitor configuration and notifiers
        Mockito.when(monitor.getNotifiers()).thenReturn(notifiers);

        // load configuration
        Mockito.when(configuredResourceFlowElement.getFlowComponent()).thenReturn(configuredResource);

        Mockito.when(configuredResource.getConfiguredResourceId()).thenReturn("configuredResourceId");

        Mockito.spy(flowConfiguration).configure(configuredResource);

        // exclusion flow invoker configuration
        Mockito.when(exclusionFlowConfiguration.getFlowElementInvokerConfiguredResources())
               .thenReturn(configuredResourceInvokers);

        // flow invoker configuration
        Mockito.when(flowConfiguration.getFlowElementInvokerConfiguredResources())
               .thenReturn(configuredResourceInvokers);

        // get the two exclusion flow element configured resources
        Mockito.when(exclusionFlowConfiguration.getConfiguredResourceFlowElements())
               .thenReturn(configuredResourceFlowElements);

        // load exclusion flow configuration
        Mockito.when(configuredResourceFlowElement.getFlowComponent()).thenReturn(configuredResource);

        Mockito.when(configuredResource.getConfiguredResourceId()).thenReturn("configuredResourceId");

        Mockito.spy(flowConfiguration).configure(configuredResource);

        Mockito.spy(flowConfiguration).configure(flow);

        Mockito.when(flowConfiguration.getFlowElements()).thenReturn(flowElements);

        // inject errorReportingService to those needing it
        Mockito.when(flowConfiguration.getErrorReportingServiceAwareFlowElements())
               .thenReturn(errorReportingServiceAwareFlowElements);

        Mockito.when(errorReportingServiceAwareFlowElement1.getFlowComponent())
               .thenReturn(errorReportingServiceAwareComponent);

        Mockito.spy(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

        // inject errorReportingService to those needing it on the exclusion flow
        Mockito.when(exclusionFlowConfiguration.getErrorReportingServiceAwareFlowElements())
               .thenReturn(errorReportingServiceAwareFlowElements);

        // get the three flow element managed resources
        Mockito.when(exclusionFlowConfiguration.getManagedResourceFlowElements())
               .thenReturn(managedResourceExclusionFlowElements);

        // get the three flow element managed resources
        Mockito.when(flowConfiguration.getManagedResourceFlowElements()).thenReturn(managedResourceFlowElements);

        // set the managed resource recovery manager instance on each managed resource
        Mockito.when(managedResourceFlowElement3.getFlowComponent()).thenReturn(managedResource);
        Mockito.when(managedResourceFlowElement3.getComponentName()).thenReturn("component name3");
        Mockito.when(managedResourceRecoveryManagerFactory.getManagedResourceRecoveryManager("component name3"))
               .thenReturn(managedResourceRecoveryManager);

        Mockito.when(managedResourceFlowElement3.getComponentName()).thenReturn("component name3");

        // set the managed resource recovery manager instance on each managed resource
        Mockito.when(managedResourceFlowElement2.getFlowComponent()).thenReturn(managedResource);
        Mockito.when(managedResourceFlowElement2.getComponentName()).thenReturn("component name2");
        Mockito.when(managedResourceRecoveryManagerFactory.getManagedResourceRecoveryManager("component name2"))
               .thenReturn(managedResourceRecoveryManager);

        Mockito.when(managedResourceFlowElement2.getComponentName()).thenReturn("component name2");

        // set the managed resource recovery manager instance on each managed resource
        Mockito.when(managedResourceFlowElement1.getFlowComponent()).thenReturn(managedResource);
        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("component name1");
        Mockito.when(managedResourceRecoveryManagerFactory.getManagedResourceRecoveryManager("component name1"))
               .thenReturn(managedResourceRecoveryManager);

        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("component name1");

        // get the consumer
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // check eventFactory for consumer
        Mockito.when(consumer.getEventFactory()).thenReturn(flowEventFactory);

        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false);

        // start will result in the monitor being updated to running
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

        // DO TEST
        flow.start();

        // test assertions

        Mockito.verify(recoveryManager, Mockito.times(3)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(managedResource, Mockito.times(3))
               .setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        Mockito.verify(managedResource, Mockito.times(3)).startManagedResource();
        Mockito.verifyNoMoreInteractions(managedResource);

        Mockito.verify(managedResourceFlowElement3).getFlowComponent();
        Mockito.verify(managedResourceFlowElement3, Mockito.times(3)).getComponentName();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement3);

        Mockito.verify(managedResourceFlowElement2).getFlowComponent();
        Mockito.verify(managedResourceFlowElement2, Mockito.times(3)).getComponentName();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement2);

        Mockito.verify(managedResourceFlowElement1).getFlowComponent();
        Mockito.verify(managedResourceFlowElement1, Mockito.times(3)).getComponentName();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement1);

        Mockito.verify(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name3");
        Mockito.verify(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name2");
        Mockito.verify(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name1");
        Mockito.verifyNoMoreInteractions(managedResourceRecoveryManagerFactory);

        Mockito.verify(consumer, Mockito.times(3)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).getEventFactory();

        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();

        // verify monitor invoke is called from set method as stop
        Mockito.verify(monitor).invoke("stopped");
        // verify monitor invoke is called from _start
        Mockito.verify(monitor).invoke("running");
        Mockito.verifyNoMoreInteractions(monitor);

    }

    /**
     * Test successful flow start from a stoppedInError state.
     */
    @Test
    void test_successful_flow_start_from_stoppedInError()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory
        );
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements =
            new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final List<FlowElement<ManagedResource>> managedResourceExclusionFlowElements = new ArrayList<>();

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);

        final List<Notifier> notifiers = new ArrayList<>();

        final List<FlowElement<?>> configuredResourceInvokers = new ArrayList<>();

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<>();

        final List<FlowElement<?>> flowElements = new ArrayList<>();

        // expectations
        // get the two flow element configured resources
        Mockito.when(flowConfiguration.getConfiguredResourceFlowElements()).thenReturn(configuredResourceFlowElements);

        // load monitor configuration and notifiers
        Mockito.when(monitor.getNotifiers()).thenReturn(notifiers);

        // load configuration
        Mockito.when(configuredResourceFlowElement.getFlowComponent()).thenReturn(configuredResource);

        Mockito.when(configuredResource.getConfiguredResourceId()).thenReturn("configuredResourceId");

        Mockito.spy(flowConfiguration).configure(configuredResource);

        // get the two exclusion flow element configured resources
        Mockito.when(exclusionFlowConfiguration.getFlowElementInvokerConfiguredResources())
               .thenReturn(configuredResourceInvokers);

        // flow invoker configuration
        Mockito.when(flowConfiguration.getFlowElementInvokerConfiguredResources())
               .thenReturn(configuredResourceInvokers);

        // load exclusion flow configuration
        Mockito.when(configuredResourceFlowElement.getFlowComponent()).thenReturn(configuredResource);

        Mockito.when(configuredResource.getConfiguredResourceId()).thenReturn("configuredResourceId");

        Mockito.spy(flowConfiguration).configure(configuredResource);

        Mockito.spy(flowConfiguration).configure(flow);

        Mockito.when(flowConfiguration.getFlowElements()).thenReturn(flowElements);

        // inject errorReportingService to those needing it
        Mockito.when(flowConfiguration.getErrorReportingServiceAwareFlowElements())
               .thenReturn(errorReportingServiceAwareFlowElements);

        Mockito.when(errorReportingServiceAwareFlowElement1.getFlowComponent())
               .thenReturn(errorReportingServiceAwareComponent);

        Mockito.spy(errorReportingServiceAwareComponent).setErrorReportingService(errorReportingService);

        // inject errorReportingService to those needing it on the exclusion flow
        Mockito.when(exclusionFlowConfiguration.getErrorReportingServiceAwareFlowElements())
               .thenReturn(errorReportingServiceAwareFlowElements);

        // get the three flow element managed resources
        Mockito.when(exclusionFlowConfiguration.getManagedResourceFlowElements())
               .thenReturn(managedResourceExclusionFlowElements);

        // get the three flow element managed resources
        Mockito.when(flowConfiguration.getManagedResourceFlowElements()).thenReturn(managedResourceFlowElements);

        // set the managed resource recovery manager instance on each managed resource
        Mockito.when(managedResourceFlowElement3.getFlowComponent()).thenReturn(managedResource);
        Mockito.when(managedResourceFlowElement3.getComponentName()).thenReturn("component name3");
        Mockito.when(managedResourceRecoveryManagerFactory.getManagedResourceRecoveryManager("component name3"))
               .thenReturn(managedResourceRecoveryManager);

        Mockito.when(managedResourceFlowElement3.getComponentName()).thenReturn("component name3");

        // set the managed resource recovery manager instance on each managed resource
        Mockito.when(managedResourceFlowElement2.getFlowComponent()).thenReturn(managedResource);
        Mockito.when(managedResourceFlowElement2.getComponentName()).thenReturn("component name2");
        Mockito.when(managedResourceRecoveryManagerFactory.getManagedResourceRecoveryManager("component name2"))
               .thenReturn(managedResourceRecoveryManager);

        Mockito.when(managedResourceFlowElement2.getComponentName()).thenReturn("component name2");

        // set the managed resource recovery manager instance on each managed resource
        Mockito.when(managedResourceFlowElement1.getFlowComponent()).thenReturn(managedResource);
        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("component name1");
        Mockito.when(managedResourceRecoveryManagerFactory.getManagedResourceRecoveryManager("component name1"))
               .thenReturn(managedResourceRecoveryManager);

        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("component name1");

        // get the consumer
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // check eventFactory for consumer
        Mockito.when(consumer.getEventFactory()).thenReturn(flowEventFactory);

        // set the monitor and receive initial state callback
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(true);

        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

        // DO TEST
        flow.start();

        // test assertions

        Mockito.verify(recoveryManager, Mockito.times(3)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(managedResource, Mockito.times(3))
               .setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        Mockito.verify(managedResource, Mockito.times(3)).startManagedResource();
        Mockito.verifyNoMoreInteractions(managedResource);

        Mockito.verify(managedResourceFlowElement3).getFlowComponent();
        Mockito.verify(managedResourceFlowElement3, Mockito.times(3)).getComponentName();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement3);

        Mockito.verify(managedResourceFlowElement2).getFlowComponent();
        Mockito.verify(managedResourceFlowElement2, Mockito.times(3)).getComponentName();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement2);

        Mockito.verify(managedResourceFlowElement1).getFlowComponent();
        Mockito.verify(managedResourceFlowElement1, Mockito.times(3)).getComponentName();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement1);

        Mockito.verify(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name3");
        Mockito.verify(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name2");
        Mockito.verify(managedResourceRecoveryManagerFactory).getManagedResourceRecoveryManager("component name1");
        Mockito.verifyNoMoreInteractions(managedResourceRecoveryManagerFactory);

        Mockito.verify(consumer, Mockito.times(3)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).getEventFactory();
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
        // verify monitor invoke is called from set method as stop
        Mockito.verify(monitor).invoke("stoppedInError");
        // verify monitor invoke is called from _start
        Mockito.verify(monitor).invoke("running");
        Mockito.verifyNoMoreInteractions(monitor);

    }

    /**
     * Test failed flow start due to consumer already running.
     */
    @Test
    void test_failed_flow_start_due_to_consumer_already_running()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory
        );
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        // set the monitor and receive initial state callback

        // get the consumer
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // Set Flow state to not in error
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false);

        // set the monitor and receive initial state callback
        Mockito.when(consumer.isRunning()).thenReturn(true).thenReturn(true);

        flow.setMonitor(monitor);

        // start will bail out due to flow already running

        //DO TEST
        flow.start();

        // test assertions

        Mockito.verify(recoveryManager, Mockito.times(3)).isRecovering();
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(consumer, Mockito.times(3)).isRunning();
        Mockito.verifyNoMoreInteractions(consumer);

        // verify monitor invoke is called from _start
        Mockito.verify(monitor, Mockito.times(2)).invoke("running");
        Mockito.verifyNoMoreInteractions(monitor);

        Mockito.verify(flowConfiguration, Mockito.times(3)).getConsumerFlowElement();
        Mockito.verifyNoMoreInteractions(flowConfiguration);

    }

    /**
     * Test failed flow start due to consumer already running in recovery.
     */
    @Test
    void test_failed_flow_start_due_to_consumer_already_running_in_recovery()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory
        );

        // get the consumer
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // Set Flow state to not in error
        Mockito.when(recoveryManager.isRecovering()).thenReturn(true);

        flow.setMonitor(monitor);

        // start will bail out due to flow already running in recovery

        //DO TEST
        flow.start();

        // test assertions

        Mockito.verify(recoveryManager, Mockito.times(3)).isRecovering();
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verifyNoMoreInteractions(consumer);

        // verify monitor invoke is called from _start
        Mockito.verify(monitor, Mockito.times(2)).invoke("recovering");
        Mockito.verifyNoMoreInteractions(monitor);

        Mockito.verifyNoMoreInteractions(flowConfiguration);

    }

    /**
     * Test failed flow start due to configuration failure.
     */
    @Test
    void test_failed_flow_start_due_to_configuration_failure()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory
        );

        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements =
            new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final RuntimeException exception = new RuntimeException("test configuration failing");

        final List<Notifier> notifiers = new ArrayList<>();

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();

        final List<FlowElement<?>> configuredResourceInvokers = new ArrayList<FlowElement<?>>();

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<>();

        final List<FlowElement<ConfiguredResource>> multipleConfiguredResourceFlowElements = new ArrayList<>();
        multipleConfiguredResourceFlowElements.add(configuredResourceFlowElement);
        multipleConfiguredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations

        // expectations
        // get the two flow element configured resources
        Mockito.when(flowConfiguration.getManagedResourceFlowElements()).thenReturn(managedResourceFlowElements);

        // get consumer flow element
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);

        Mockito.when(exclusionFlowConfiguration.getConfiguredResourceFlowElements())
               .thenReturn(configuredResourceFlowElements);

        // exclusion flow invoker configuration
        Mockito.when(exclusionFlowConfiguration.getFlowElementInvokerConfiguredResources())
               .thenReturn(configuredResourceInvokers);

        // get dynamic configured resources
        Mockito.when(flowConfiguration.getDynamicConfiguredResourceFlowElements())
               .thenReturn(dynamicConfiguredResourceFlowElements);

        // flow invoker configuration
        Mockito.when(flowConfiguration.getFlowElementInvokerConfiguredResources())
               .thenReturn(configuredResourceInvokers);

        // get the flow element configured resources
        Mockito.when(flowConfiguration.getConfiguredResourceFlowElements()).thenReturn(configuredResourceFlowElements);

        // load monitor configuration and notifiers
        Mockito.when(monitor.getNotifiers()).thenReturn(notifiers);

        // get the two exclusion flow element configured resources
        Mockito.when(exclusionFlowConfiguration.getConfiguredResourceFlowElements())
               .thenReturn(multipleConfiguredResourceFlowElements);

        Mockito.when(flowConfiguration.getConfiguredResourceFlowElements())
               .thenReturn(multipleConfiguredResourceFlowElements);

        Mockito.doThrow(exception).when(flowConfiguration).configure(configuredResource);

        // load exclusion flow configuration
        Mockito.when(configuredResourceFlowElement.getFlowComponent()).thenReturn(configuredResource);

        Mockito.when(configuredResource.getConfiguredResourceId()).thenReturn("configuredResourceId");

        // get the consumer
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // set the monitor and receive initial state callback
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(true);

        // set the monitor and receive initial state callback
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

        // DO TEST
        try
        {
            flow.start();
        }
        catch (Throwable throwable)
        {
            if ( !(throwable instanceof RuntimeException) )
            {
                fail("Runtime exception Not thrown");

            }
        }
        // test assertions
        Mockito.verify(flowConfiguration).configure(configuredResource);

        Mockito.verify(recoveryManager, Mockito.times(3)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
        Mockito.verify(exclusionFlowConfiguration).getFlowElementInvokerConfiguredResources();
        Mockito.verifyNoMoreInteractions(exclusionFlowConfiguration);

        Mockito.verifyNoMoreInteractions(managedResource);
        Mockito.verifyNoMoreInteractions(managedResourceRecoveryManagerFactory);

        Mockito.verify(consumer, Mockito.times(3)).isRunning();
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
        Mockito.verify(monitor, Mockito.times(2)).invoke("stoppedInError");
        Mockito.verifyNoMoreInteractions(monitor);

        Mockito.verify(flowConfiguration, Mockito.times(3)).getConsumerFlowElement();
        Mockito.verifyNoMoreInteractions(flowConfiguration);

    }

    /**
     * Test successful flow start regardless of a managed resource successfully
     * starting. We ignore managed resource start failures as these could
     * resolve themselves later in the flow invocation.
     */
    @Test
    void test_success_flow_start_even_with_managedResource_start_failure_non_critical_resource()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory
        );
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);

        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements =
            new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<>();

        // set start invocation expectations
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements,
            managedResourceFlowElements
                            );
        // handle managed resources on the normal flow
        // set the managed resource recovery manager instance on each managed resource
        Mockito.when(managedResourceFlowElement1.getFlowComponent()).thenReturn(managedResource);
        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("component name");
        Mockito.when(managedResourceRecoveryManagerFactory.getManagedResourceRecoveryManager("component name"))
               .thenReturn(managedResourceRecoveryManager);

        //Mockito.spy(managedResource).setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        Mockito.doThrow(new RuntimeException("test managed resource start failure")).when(managedResource)
               .startManagedResource();

        Mockito.when(managedResourceFlowElement1.getFlowComponent()).thenReturn(managedResource);
        Mockito.when(managedResource.isCriticalOnStartup()).thenReturn(false);
        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("managedResourceFlowElementName");

        // set the monitor and receive initial state callback
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false).thenReturn(false);

        flow.setErrorReportingService(errorReportingService);
        flow.setMonitor(monitor);

        // DO TEST
        flow.start();

        // test assertions
        Mockito.verify(flowConfiguration, Mockito.times(3)).configure(Mockito.any());
        Mockito.verify(flowConfiguration, Mockito.times(4)).getConsumerFlowElement();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
        Mockito.verifyNoMoreInteractions(flowConfiguration);

        Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
        Mockito.verify(recoveryManager, Mockito.times(3)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(exclusionFlowConfiguration).getManagedResourceFlowElements();
        Mockito.verify(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
        Mockito.verify(exclusionFlowConfiguration).getFlowElementInvokerConfiguredResources();
        Mockito.verify(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
        Mockito.verifyNoMoreInteractions(exclusionFlowConfiguration);

        Mockito.verify(managedResource).isCriticalOnStartup();
        Mockito.verify(managedResource).setManagedResourceRecoveryManager(Mockito.any());
        Mockito.verify(managedResource).startManagedResource();
        Mockito.verifyNoMoreInteractions(managedResource);

        Mockito.verify(managedResourceFlowElement1, Mockito.times(2)).getFlowComponent();
        Mockito.verify(managedResourceFlowElement1, Mockito.times(3)).getComponentName();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement1);

        Mockito.verify(configuredResourceFlowElement, Mockito.times(4)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(configuredResourceFlowElement);

        Mockito.verify(managedResourceRecoveryManagerFactory)
               .getManagedResourceRecoveryManager("managedResourceFlowElementName");
        Mockito.verifyNoMoreInteractions(managedResourceRecoveryManagerFactory);

        Mockito.verify(consumer, Mockito.times(3)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).getEventFactory();
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
        Mockito.verify(monitor).invoke("stopped");
        Mockito.verify(monitor).invoke("running");
        Mockito.verifyNoMoreInteractions(monitor);

    }

    /**
     * Test successful flow start regardless of a managed resource successfully
     * starting. We don't ignore managed resource start failures if they are marked as critical.
     */
    @Test
    void test_success_flow_start_even_with_managedResource_start_failure_critical_resource()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory
        );

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<>();

        // expectations

        // get the two flow element configured resources
        Mockito.when(flowConfiguration.getConfiguredResourceFlowElements()).thenReturn(configuredResourceFlowElements);

        // get the the flow element managed resource
        Mockito.when(flowConfiguration.getManagedResourceFlowElements()).thenReturn(managedResourceFlowElements);

        // start each managed resource from right to left (reverse order) in flow order
        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("managed component");
        Mockito.when(managedResourceFlowElement1.getFlowComponent()).thenReturn(managedResource);

        Mockito.doThrow(new RuntimeException("test managed resource start failure")).when(managedResource)
               .startManagedResource();

        Mockito.when(managedResourceFlowElement1.getFlowComponent()).thenReturn(managedResource);

        //             Mockito.when((FlowElement) managedResource.getFlowComponent();
        //                    .thenReturn("name"));
        Mockito.when(managedResource.isCriticalOnStartup()).thenReturn(true);

        // stop each managed resource
        Mockito.when(flowConfiguration.getManagedResourceFlowElements()).thenReturn(managedResourceFlowElements);

        Mockito.when(managedResourceFlowElement1.getFlowComponent()).thenReturn(managedResource);

        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // check state before proceeding with start
        // set the monitor and receive initial state callback
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false).thenReturn(false);

        flow.setErrorReportingService(errorReportingService);
        flow.setMonitor(monitor);

        // DO TEST
        try
        {
            flow.start();
        }
        catch (Throwable t)
        {
            if ( !(t instanceof RuntimeException) )
            {
                fail("No Runtime Exception");
            }
        }

        // test assertions

        Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
        Mockito.verify(recoveryManager, Mockito.times(3)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(flowConfiguration, Mockito.times(1)).configure(Mockito.any());
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(3)).getConsumerFlowElement();
        Mockito.verify(flowConfiguration, Mockito.times(2)).getManagedResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
        Mockito.verifyNoMoreInteractions(flowConfiguration);

        Mockito.verify(managedResource).isCriticalOnStartup();
        Mockito.verify(managedResource).setManagedResourceRecoveryManager(Mockito.any());
        Mockito.verify(managedResource).startManagedResource();
        Mockito.verify(managedResource).stopManagedResource();
        Mockito.verifyNoMoreInteractions(managedResource);

        Mockito.verify(managedResourceFlowElement1, Mockito.times(3)).getFlowComponent();
        Mockito.verify(managedResourceFlowElement1, Mockito.times(5)).getComponentName();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement1);

        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(consumer, Mockito.times(3)).isRunning();
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
        Mockito.verify(monitor).invoke("stopped");
        Mockito.verify(monitor).invoke("running");
        Mockito.verifyNoMoreInteractions(monitor);

    }

    /**
     * Test failed flow start due to consumer failing to start, but activating recovery.
     */
    @Test
    void test_failed_flow_start_due_to_recoverable_consumer_failure()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory
        );
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        // set the monitor and receive initial state callback

        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // check state before proceeding with start
        // set the monitor and receive initial state callback
        Mockito.doThrow(new RuntimeException("Fail consumer start")).when(consumer).start();
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false);
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false).thenReturn(false).thenReturn(true);

        flow.setErrorReportingService(errorReportingService);
        flow.setMonitor(monitor);

        // this start will result in the monitor being updated to unrecoverable
        flow.start();

        // test assertions

        Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
        Mockito.verify(recoveryManager, Mockito.times(3)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verify(recoveryManager).recover(Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(consumer, Mockito.times(2)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).setEventFactory(Mockito.any());
        Mockito.verify(consumer).getEventFactory();
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
        Mockito.verify(monitor).invoke("stopped");
        Mockito.verify(monitor).invoke("recovering");
        Mockito.verifyNoMoreInteractions(monitor);
    }

    /**
     * Test failed flow start due to consumer failing to start.
     */
    @Test
    void test_failed_flow_start_due_to_unrecoverable_consumer_failure()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory
        );
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        // set the monitor and receive initial state callback
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // check state before proceeding with start
        // set the monitor and receive initial state callback
        Mockito.doThrow(new RuntimeException("Fail consumer start")).when(consumer).start();
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false).thenReturn(false).thenReturn(true);
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false).thenReturn(false).thenReturn(false);

        flow.setErrorReportingService(errorReportingService);
        flow.setMonitor(monitor);

        // this start will result in the monitor being updated to unrecoverable
        flow.start();

        // test assertions
        Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
        Mockito.verify(recoveryManager, Mockito.times(3)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(3)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verify(recoveryManager).recover(Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(consumer, Mockito.times(3)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).setEventFactory(Mockito.any());
        Mockito.verify(consumer).getEventFactory();
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
        Mockito.verify(monitor).invoke("stopped");
        Mockito.verify(monitor).invoke("stoppedInError");
        Mockito.verifyNoMoreInteractions(monitor);
    }

    /**
     * Test successful flow stop on flow which is running.
     */
    @Test
    void test_successful_flow_stop_whilst_running()
    {
        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);

        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations

        // get the consumer
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        Mockito.when(consumer.isRunning()).thenReturn(true).thenReturn(false);

        // get the three flow element managed resources
        Mockito.when(flowConfiguration.getManagedResourceFlowElements()).thenReturn(managedResourceFlowElements);

        // stop each managed resource from left to right in flow order
        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("componentName1");

        Mockito.when(managedResourceFlowElement1.getFlowComponent()).thenReturn(managedResource);

        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("componentName1");

        Mockito.when(managedResourceFlowElement2.getComponentName()).thenReturn("componentName2");

        Mockito.when(managedResourceFlowElement2.getFlowComponent()).thenReturn(managedResource);

        Mockito.when(managedResourceFlowElement2.getComponentName()).thenReturn("componentName2");

        Mockito.when(managedResourceFlowElement3.getComponentName()).thenReturn("componentName3");

        Mockito.when(managedResourceFlowElement3.getFlowComponent()).thenReturn(managedResource);

        Mockito.when(managedResourceFlowElement3.getComponentName()).thenReturn("componentName3");

        // get the three exclusion flow element managed resources
        Mockito.when(exclusionFlowConfiguration.getManagedResourceFlowElements())
               .thenReturn(managedResourceFlowElements);

        // stop each managed resource from left to right in flow order
        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("componentName1");

        Mockito.when(managedResourceFlowElement1.getFlowComponent()).thenReturn(managedResource);

        Mockito.when(managedResourceFlowElement2.getComponentName()).thenReturn("componentName2");

        Mockito.when(managedResourceFlowElement2.getFlowComponent()).thenReturn(managedResource);

        Mockito.when(managedResourceFlowElement3.getComponentName()).thenReturn("componentName3");
        Mockito.when(managedResourceFlowElement3.getFlowComponent()).thenReturn(managedResource);

        // container for the complete flow
        VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory
        );

        // state is stopped when setting monitor and on init of start()
        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false);
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false).thenReturn(false);

        flow.setMonitor(monitor);

        // run test
        flow.stop();

        // test assertions
        Mockito.verify(recoveryManager, Mockito.times(2)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(1)).isUnrecoverable();
        Mockito.verify(recoveryManager).cancelAll();
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(managedResourceFlowElement3, Mockito.times(4)).getComponentName();
        Mockito.verify(managedResourceFlowElement3, Mockito.times(2)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement3);
        Mockito.verify(managedResourceFlowElement2, Mockito.times(4)).getComponentName();
        Mockito.verify(managedResourceFlowElement2, Mockito.times(2)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement2);
        Mockito.verify(managedResourceFlowElement1, Mockito.times(4)).getComponentName();
        Mockito.verify(managedResourceFlowElement1, Mockito.times(2)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement1);

        Mockito.verify(consumer, Mockito.times(2)).isRunning();
        Mockito.verify(consumer).stop();
        Mockito.verify(consumer).setListener(null);
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor).invoke("running");
        Mockito.verify(monitor).invoke("stopped");
        Mockito.verifyNoMoreInteractions(monitor);

    }

    /**
     * Test successful flow stop on flow which is in recovery.
     */
    @Test
    void test_successful_flow_stop_whilst_recovering()
    {
        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);

        // expectations

        // get the consumer
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // get the three flow element managed resources
        Mockito.when(flowConfiguration.getManagedResourceFlowElements()).thenReturn(managedResourceFlowElements);

        // stop each managed resource from left to right in flow order
        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("component name");

        Mockito.when(managedResourceFlowElement1.getFlowComponent()).thenReturn(managedResource);

        Mockito.when(managedResourceFlowElement2.getComponentName()).thenReturn("component name");

        Mockito.when(managedResourceFlowElement2.getFlowComponent()).thenReturn(managedResource);

        Mockito.when(managedResourceFlowElement3.getComponentName()).thenReturn("component name");

        Mockito.when(managedResourceFlowElement3.getFlowComponent()).thenReturn(managedResource);

        // get the three exclusion flow element managed resources
        Mockito.when(exclusionFlowConfiguration.getManagedResourceFlowElements())
               .thenReturn(managedResourceFlowElements);

        // stop each managed resource from left to right in flow order

        // container for the complete flow
        VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory
        );

        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);
        Mockito.when(consumer.isRunning()).thenReturn(false);

        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false);
        Mockito.when(recoveryManager.isRecovering()).thenReturn(true).thenReturn(false);

        flow.setMonitor(monitor);

        // run test
        flow.stop();

        // test assertions
        Mockito.verify(recoveryManager, Mockito.times(2)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(1)).isUnrecoverable();
        Mockito.verify(recoveryManager).cancelAll();
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(managedResourceFlowElement3, Mockito.times(4)).getComponentName();
        Mockito.verify(managedResourceFlowElement3, Mockito.times(2)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement3);
        Mockito.verify(managedResourceFlowElement2, Mockito.times(4)).getComponentName();
        Mockito.verify(managedResourceFlowElement2, Mockito.times(2)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement2);
        Mockito.verify(managedResourceFlowElement1, Mockito.times(4)).getComponentName();
        Mockito.verify(managedResourceFlowElement1, Mockito.times(2)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement1);

        Mockito.verify(consumer, Mockito.times(1)).isRunning();
        Mockito.verify(consumer).stop();
        Mockito.verify(consumer).setListener(null);
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor).invoke("recovering");
        Mockito.verify(monitor).invoke("stopped");
        Mockito.verifyNoMoreInteractions(monitor);
    }

    /**
     * Test successful flow stop on flow which is already stopped.
     * This should go through the motions as there is no issue in
     * stopping a stopped flow.
     */
    @Test
    void test_successful_flow_stop_whilst_stopped()
    {
        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);

        // expectations

        // get the consumer
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // get the three flow element managed resources
        Mockito.when(flowConfiguration.getManagedResourceFlowElements()).thenReturn(managedResourceFlowElements);

        // stop each managed resource from left to right in flow order
        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("component name");

        Mockito.when(managedResourceFlowElement1.getFlowComponent()).thenReturn(managedResource);

        Mockito.when(managedResourceFlowElement2.getComponentName()).thenReturn("component name");

        Mockito.when(managedResourceFlowElement2.getFlowComponent()).thenReturn(managedResource);

        Mockito.when(managedResourceFlowElement3.getComponentName()).thenReturn("component name");

        Mockito.when(managedResourceFlowElement3.getFlowComponent()).thenReturn(managedResource);

        // get the three exclusion flow element managed resources
        Mockito.when(exclusionFlowConfiguration.getManagedResourceFlowElements())
               .thenReturn(managedResourceFlowElements);

        // container for the complete flow
        VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory
        );

        // set the monitor
        Mockito.when(consumer.isRunning()).thenReturn(false);
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false);
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false);

        flow.setMonitor(monitor);

        // run test
        flow.stop();

        // test assertions
        Mockito.verify(recoveryManager, Mockito.times(2)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).cancelAll();
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(managedResourceFlowElement3, Mockito.times(4)).getComponentName();
        Mockito.verify(managedResourceFlowElement3, Mockito.times(2)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement3);
        Mockito.verify(managedResourceFlowElement2, Mockito.times(4)).getComponentName();
        Mockito.verify(managedResourceFlowElement2, Mockito.times(2)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement2);
        Mockito.verify(managedResourceFlowElement1, Mockito.times(4)).getComponentName();
        Mockito.verify(managedResourceFlowElement1, Mockito.times(2)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement1);

        Mockito.verify(consumer, Mockito.times(2)).isRunning();
        Mockito.verify(consumer).stop();
        Mockito.verify(consumer).setListener(null);
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor, Mockito.times(2)).invoke("stopped");
        Mockito.verifyNoMoreInteractions(monitor);
    }

    /**
     * Test successful flow stop on flow which is already stoppedInError.
     * This should go through the motions as there is no issue in
     * stopping a stoppedInError flow.
     */
    @Test
    void test_successful_flow_stop_whilst_stoppedInError()
    {
        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);
        managedResourceFlowElements.add(managedResourceFlowElement2);
        managedResourceFlowElements.add(managedResourceFlowElement3);

        // expectations
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // get the three flow element managed resources
        Mockito.when(flowConfiguration.getManagedResourceFlowElements()).thenReturn(managedResourceFlowElements);

        // stop each managed resource from left to right in flow order
        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("component name");

        Mockito.when(managedResourceFlowElement1.getFlowComponent()).thenReturn(managedResource);

        Mockito.when(managedResourceFlowElement2.getComponentName()).thenReturn("component name");

        Mockito.when(managedResourceFlowElement2.getFlowComponent()).thenReturn(managedResource);

        Mockito.when(managedResourceFlowElement3.getComponentName()).thenReturn("component name");

        Mockito.when(managedResourceFlowElement3.getFlowComponent()).thenReturn(managedResource);

        // get the three exclusion flow element managed resources
        Mockito.when(exclusionFlowConfiguration.getManagedResourceFlowElements())
               .thenReturn(managedResourceFlowElements);

        // container for the complete flow
        VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory
        );

        // set the monitor
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false);
        Mockito.when(consumer.isRunning()).thenReturn(false);
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(true).thenReturn(false);

        flow.setMonitor(monitor);

        // run test
        flow.stop();

        // test assertions
        // test assertions
        Mockito.verify(recoveryManager, Mockito.times(2)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).cancelAll();
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(managedResourceFlowElement3, Mockito.times(4)).getComponentName();
        Mockito.verify(managedResourceFlowElement3, Mockito.times(2)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement3);
        Mockito.verify(managedResourceFlowElement2, Mockito.times(4)).getComponentName();
        Mockito.verify(managedResourceFlowElement2, Mockito.times(2)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement2);
        Mockito.verify(managedResourceFlowElement1, Mockito.times(4)).getComponentName();
        Mockito.verify(managedResourceFlowElement1, Mockito.times(2)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement1);

        Mockito.verify(consumer, Mockito.times(2)).isRunning();
        Mockito.verify(consumer).stop();
        Mockito.verify(consumer).setListener(null);
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor).invoke("stoppedInError");
        Mockito.verify(monitor).invoke("stopped");
        Mockito.verifyNoMoreInteractions(monitor);
    }

    /**
     * Test successful flow invoke with a flow event.
     */
    @Test
    void test_successful_flow_invoke_with_flowEvent()
    {
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations

        // always get the original exceptionLifeIdentifier
        Mockito.when(flowEvent.getIdentifier()).thenReturn("identifier");

        Mockito.when(dynamicConfiguredResourceFlowElement.getFlowComponent()).thenReturn(dynamicConfiguredResource);

        Mockito.when(flowConfiguration.getReplayRecordService()).thenReturn(replayRecordService);

        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);

        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        Mockito.when(consumerFlowElement.getFlowElementInvoker()).thenReturn(flowElementInvoker);

        Mockito.when(consumerFlowElement.getFlowElementInvoker()).thenReturn(flowElementInvoker);

        Mockito.when(consumerFlowElement.getFlowElementInvoker()).thenReturn(flowElementInvoker);

//        Mockito.when(consumerFlowElement.getConfiguration()).thenReturn(new FlowElementPersistentConfiguration());
        Mockito.when(consumerFlowElement.getFlowElementInvoker()).thenReturn(flowElementInvoker);
//        Mockito.when(consumerFlowElement.getConfiguration()).thenReturn(new FlowElementPersistentConfiguration());

        Mockito.when(flowElementInvoker
            .invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent,
                consumerFlowElement
                   )).thenReturn(null);

        Mockito.when(exclusionService.isBlackListed("identifier")).thenReturn(false);

        Mockito.when(dynamicConfiguredResourceFlowElement.getFlowComponent()).thenReturn(dynamicConfiguredResource);

        // in this test we do not need to cancelAll recovery
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false);

        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getComponentName()).thenReturn("consumer");

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            recoveryManager, exclusionService
        );

        // set start invocation expectations
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements);

        // state is stopped when setting monitor and on init of start()
        // set the monitor
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false);
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true);
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false).thenReturn(false);

        flow.setMonitor(monitor);

        // run test
        flow.setFlowListener(flowEventListener);

        flow.start();
        // DO TEST
        flow.invoke(flowEvent);

        // test assertions

        Mockito.verify(recoveryManager, Mockito.times(5)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager, Mockito.times(1)).initialise();
        Mockito.verify(recoveryManager, Mockito.times(1)).setManagedResources(Mockito.any());
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(flowConfiguration, Mockito.times(5)).configure(Mockito.any());
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(7)).getConsumerFlowElement();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getReplayRecordService();
        Mockito.verify(flowConfiguration, Mockito.times(2)).update(Mockito.any());
        Mockito.verifyNoMoreInteractions(flowConfiguration);

        Mockito.verify(flowConfiguration, Mockito.times(5)).configure(Mockito.any());

        //        Mockito.verifyNoMoreInteractions(consumerFlowElement);
        Mockito.verify(flowElementInvoker, Mockito.times(1)).setIgnoreContextInvocation(true);
        Mockito.verify(flowElementInvoker, Mockito.times(1)).setInvokeContextListeners(false);
        Mockito.verify(flowElementInvoker, Mockito.times(1)).setFlowInvocationContextListeners(null);
        Mockito.verify(flowElementInvoker, Mockito.times(1))
               .invoke(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(flowElementInvoker, Mockito.times(1)).getConfiguration();

        Mockito.verifyNoMoreInteractions(flowElementInvoker);

        Mockito.verify(consumer, Mockito.times(4)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).getEventFactory();
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor, Mockito.times(2)).invoke("running");
        Mockito.verify(monitor).invoke("stopped");
        Mockito.verify(monitor).getNotifiers();
        Mockito.verifyNoMoreInteractions(monitor);

    }

    /**
     * Test successful flow invoke with a flow event with a context listener.
     */
    @Test
    void test_successful_flow_invoke_with_flowEvent_with_contextListener()
    {
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations
        // always get the original exceptionLifeIdentifier
        Mockito.when(flowEvent.getIdentifier()).thenReturn("identifier");

        Mockito.when(dynamicConfiguredResourceFlowElement.getFlowComponent()).thenReturn(dynamicConfiguredResource);

        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);

        Mockito.when(consumerFlowElement.getFlowElementInvoker()).thenReturn(flowElementInvoker);

        InvokerConfiguration configuration = new InvokerConfiguration();
        configuration.setCaptureMetrics(true);
        configuration.setSnapEvent(true);

//        Mockito.when(consumerFlowElement.getConfiguration()).thenReturn(configuration);
        Mockito.when(consumerFlowElement.getFlowElementInvoker()).thenReturn(flowElementInvoker);
        Mockito.when(flowElementInvoker.getConfiguration()).thenReturn(configuration);
//        Mockito.when(consumerFlowElement.getConfiguration()).thenReturn(configuration);
        Mockito.when(flowElementInvoker
            .invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent,
                consumerFlowElement
                   )).thenReturn(null);

        Mockito.when(flowConfiguration.getReplayRecordService()).thenReturn(replayRecordService);

        Mockito.when(exclusionService.isBlackListed("identifier")).thenReturn(false);

        Mockito.when(dynamicConfiguredResourceFlowElement.getFlowComponent()).thenReturn(dynamicConfiguredResource);

        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getComponentName()).thenReturn("consumer");

        // in this test we do not need to cancelAll recovery
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false);
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true);
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false).thenReturn(false);

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            recoveryManager, exclusionService
        );
        flow.getConfiguration().setInvokeContextListeners(true);
        flow.setFlowInvocationContextListeners(flowInvocationContextListeners);

        // set start invocation expectations
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements);

        flow.setMonitor(monitor);
        flow.setFlowListener(flowEventListener);

        // run test

        flow.start();
        flow.invoke(flowEvent);

        // test assertions

        Mockito.verify(recoveryManager, Mockito.times(5)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager, Mockito.times(1)).initialise();
        Mockito.verify(recoveryManager, Mockito.times(1)).setManagedResources(Mockito.any());
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(flowConfiguration, Mockito.times(5)).configure(Mockito.any());
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(7)).getConsumerFlowElement();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getReplayRecordService();
        Mockito.verify(flowConfiguration, Mockito.times(2)).update(Mockito.any());
        Mockito.verifyNoMoreInteractions(flowConfiguration);

        Mockito.verify(flowConfiguration, Mockito.times(5)).configure(Mockito.any());

        //        Mockito.verifyNoMoreInteractions(consumerFlowElement);
        Mockito.verify(flowElementInvoker, Mockito.times(1)).setIgnoreContextInvocation(false);
        Mockito.verify(flowElementInvoker, Mockito.times(1)).setInvokeContextListeners(true);
        Mockito.verify(flowElementInvoker, Mockito.times(1)).setFlowInvocationContextListeners(Mockito.anyList());
        Mockito.verify(flowElementInvoker, Mockito.times(1))
               .invoke(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(flowElementInvoker, Mockito.times(1)).getConfiguration();

        Mockito.verifyNoMoreInteractions(flowElementInvoker);

        Mockito.verify(consumer, Mockito.times(4)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).getEventFactory();
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verifyNoMoreInteractions(flowEventListener);

        Mockito.verify(flowInvocationContext).startFlowInvocation();
        Mockito.verify(flowInvocationContext).setLastComponentName("consumer");
        Mockito.verify(flowInvocationContext).endFlowInvocation();
        Mockito.verifyNoMoreInteractions(flowInvocationContext);

        Mockito.verify(flowInvocationContextListener).endFlow(flowInvocationContext);
        Mockito.verifyNoMoreInteractions(flowInvocationContextListener);

        Mockito.verify(monitor, Mockito.times(2)).invoke("running");
        Mockito.verify(monitor).invoke("stopped");
        Mockito.verify(monitor).getNotifiers();
        Mockito.verifyNoMoreInteractions(monitor);
    }

    /**
     * Set all expectations for a standard start()
     */
    public void setStartExpectations(VisitingInvokerFlow flow,
                                     final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements,
                                     final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements)
    {
        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        final List<FlowElement<ManagedResource>> managedResourceExclusionFlowElements = new ArrayList<>();
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements,
            managedResourceFlowElements, managedResourceExclusionFlowElements
                            );
    }

    /**
     * Set all expectations for a standard start()
     */
    public void setStartExpectations(VisitingInvokerFlow flow,
                                     final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements,
                                     final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements,
                                     final List<FlowElement<ManagedResource>> managedResourceFlowElements)
    {
        final List<FlowElement<ManagedResource>> managedResourceExclusionFlowElements = new ArrayList<>();
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements,
            managedResourceFlowElements, managedResourceExclusionFlowElements
                            );
    }

    /**
     * Set all expectations for a standard start()
     */
    public void setStartExpectations(VisitingInvokerFlow flow,
                                     final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements,
                                     final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements,
                                     final List<FlowElement<ManagedResource>> managedResourceFlowElements,
                                     final List<FlowElement<ManagedResource>> managedResourceExclusionFlowElements)
    {
        final List<Notifier> notifiers = new ArrayList<>();
        final List<FlowElement<?>> configuredResourceInvokers = new ArrayList<FlowElement<?>>();
        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements =
            new ArrayList<>();

        // expectations

        // load monitor configuration and notifiers
        Mockito.when(monitor.getNotifiers()).thenReturn(notifiers);

        // exclusion flow invoker configuration
        Mockito.when(exclusionFlowConfiguration.getFlowElementInvokerConfiguredResources())
               .thenReturn(configuredResourceInvokers);
        Mockito.when(exclusionFlowConfiguration.getConfiguredResourceFlowElements())
               .thenReturn(configuredResourceFlowElements);

        Mockito.when(configuredResourceFlowElement.getFlowComponent()).thenReturn(configuredResource);

        Mockito.when(configuredResource.getConfiguredResourceId()).thenReturn("id");

        Mockito.when(configuredResourceFlowElement.getFlowComponent()).thenReturn(configuredResource);

        //        Mockito.spy(flowConfiguration).configure(configuredResource);

        Mockito.when(exclusionFlowConfiguration.getErrorReportingServiceAwareFlowElements())
               .thenReturn(errorReportingServiceAwareFlowElements);

        Mockito.when(flowConfiguration.getDynamicConfiguredResourceFlowElements())
               .thenReturn(dynamicConfiguredResourceFlowElements);

        Mockito.when(flowConfiguration.getFlowElementInvokerConfiguredResources())
               .thenReturn(configuredResourceInvokers);
        Mockito.when(flowConfiguration.getConfiguredResourceFlowElements()).thenReturn(configuredResourceFlowElements);
        Mockito.when(configuredResourceFlowElement.getFlowComponent()).thenReturn(configuredResource);
        Mockito.when(configuredResource.getConfiguredResourceId()).thenReturn("id");
        //        Mockito.spy(flowConfiguration).configure(configuredResource);

//        Mockito.when(configuredResourceFlowElement.getConfiguredResourceId()).thenReturn("id");

        //Mockito.spy(flowConfiguration).configure(configuredResourceFlowElement);

        //        Mockito.spy(flowConfiguration).configure(flow);
        Mockito.when(flowConfiguration.getErrorReportingServiceAwareFlowElements())
               .thenReturn(errorReportingServiceAwareFlowElements);
        Mockito.when(configuredResourceFlowElement.getFlowComponent()).thenReturn(configuredResource);
        // exclusion flow element flow managed resource flow elements
        Mockito.when(exclusionFlowConfiguration.getManagedResourceFlowElements())
               .thenReturn(managedResourceExclusionFlowElements);

        // normal flow element flow managed resource flow elements
        Mockito.when(flowConfiguration.getManagedResourceFlowElements()).thenReturn(managedResourceFlowElements);

        // get the consumer
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // check eventFactory for consumer
        Mockito.when(consumer.getEventFactory()).thenReturn(flowEventFactory);

    }

    /**
     * Test successful flow invoke with a flow event with a context listener that is disabled.
     */
    @Test
    void test_successful_flow_invoke_with_flowEvent_with_contextListener_disabled()
    {
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            recoveryManager, exclusionService
        );

        // expectations
        // always get the original exceptionLifeIdentifier
        Mockito.when(flowEvent.getIdentifier()).thenReturn("identifier");

        // expectations from invoke
        Mockito.when(dynamicConfiguredResourceFlowElement.getFlowComponent()).thenReturn(dynamicConfiguredResource);

        Mockito.when(flowConfiguration.getReplayRecordService()).thenReturn(replayRecordService);

        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowElementInvoker()).thenReturn(flowElementInvoker);

//        Mockito.when(consumerFlowElement.getConfiguration()).thenReturn(new FlowElementPersistentConfiguration());
//
//        Mockito.when(consumerFlowElement.getConfiguration()).thenReturn(new FlowElementPersistentConfiguration());

        Mockito.when(flowElementInvoker
            .invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent,
                consumerFlowElement
                   )).thenReturn(null);

        Mockito.when(exclusionService.isBlackListed("identifier")).thenReturn(false);

        Mockito.when(dynamicConfiguredResourceFlowElement.getFlowComponent()).thenReturn(dynamicConfiguredResource);

        // in this test we do not need to cancelAll recovery
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false);

        Mockito.when(consumerFlowElement.getComponentName()).thenReturn("consumer");

        // set start invocation expectations
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements);

        flow.setFlowInvocationContextListeners(flowInvocationContextListeners);
        flow.stopContextListeners();

        // state is stopped when setting monitor and on init of start()
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false).thenReturn(false);

        flow.setMonitor(monitor);

        // run test
        //setGetStateExpectations(2, isNotRecovering, isRunning, isNotUnrecoverable);
        flow.setFlowListener(flowEventListener);
        flow.start();

        // DO TEST
        flow.invoke(flowEvent);

        // test assertions
        Mockito.verify(flowConfiguration, Mockito.times(5)).configure(Mockito.any());
        Mockito.verify(flowConfiguration, Mockito.times(2)).update(Mockito.any());
        Mockito.verify(flowConfiguration, Mockito.times(7)).getConsumerFlowElement();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getReplayRecordService();
        Mockito.verifyNoMoreInteractions(flowConfiguration);

        Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
        Mockito.verify(recoveryManager, Mockito.times(5)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(exclusionFlowConfiguration).getFlowElementInvokerConfiguredResources();
        Mockito.verify(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
        Mockito.verify(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
        Mockito.verify(exclusionFlowConfiguration).getManagedResourceFlowElements();
        Mockito.verifyNoMoreInteractions(exclusionFlowConfiguration);

        Mockito.verifyNoMoreInteractions(managedResource);

        Mockito.verifyNoMoreInteractions(managedResourceFlowElement1);

        Mockito.verify(configuredResourceFlowElement, Mockito.times(4)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(configuredResourceFlowElement);

        Mockito.verifyNoMoreInteractions(managedResourceRecoveryManagerFactory);

        Mockito.verify(consumer, Mockito.times(4)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).getEventFactory();
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(flowInvocationContext).startFlowInvocation();
        Mockito.verify(flowInvocationContext).setLastComponentName("consumer");
        Mockito.verify(flowInvocationContext).endFlowInvocation();
        Mockito.verifyNoMoreInteractions(flowInvocationContext);

        Mockito.verifyNoMoreInteractions(flowInvocationContextListener);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
        Mockito.verify(monitor).invoke("stopped");
        Mockito.verify(monitor, Mockito.times(2)).invoke("running");
        Mockito.verifyNoMoreInteractions(monitor);

    }

    /**
     * Test successful flow invoke with a flow event with a context listener that is specifically enabled.
     */
    @Test
    void test_successful_flow_invoke_with_flowEvent_with_contextListener_enabled()
    {
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations
        // always get the original exceptionLifeIdentifier
        Mockito.when(flowEvent.getIdentifier()).thenReturn("identifier");

        Mockito.when(dynamicConfiguredResourceFlowElement.getFlowComponent()).thenReturn(dynamicConfiguredResource);

        Mockito.when(flowConfiguration.getReplayRecordService()).thenReturn(replayRecordService);

        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowElementInvoker()).thenReturn(flowElementInvoker);

//        Mockito.when(consumerFlowElement.getConfiguration()).thenReturn(new FlowElementPersistentConfiguration());

        Mockito.when(flowElementInvoker
            .invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent,
                consumerFlowElement
                   )).thenReturn(null);

        Mockito.when(exclusionService.isBlackListed("identifier")).thenReturn(false);

        Mockito.when(dynamicConfiguredResourceFlowElement.getFlowComponent()).thenReturn(dynamicConfiguredResource);

        // in this test we do not need to cancelAll recovery
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false);

        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getComponentName()).thenReturn("consumer");
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // state is stopped when setting monitor and on init of start()
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false).thenReturn(false);

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            recoveryManager, exclusionService
        );
        flow.getConfiguration().setInvokeContextListeners(true);
        flow.setFlowInvocationContextListeners(flowInvocationContextListeners);
        flow.stopContextListeners();
        flow.startContextListeners();

        // set start invocation expectations
        flow.setMonitor(monitor);

        // run test
        flow.setFlowListener(flowEventListener);
        flow.start();

        flow.invoke(flowEvent);

        // test assertions
        Mockito.verify(flowConfiguration, Mockito.times(1)).configure(Mockito.any());
        //Mockito.verify(flowConfiguration, Mockito.times(2)).update(Mockito.any());
        Mockito.verify(flowConfiguration, Mockito.times(7)).getConsumerFlowElement();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getReplayRecordService();
        Mockito.verifyNoMoreInteractions(flowConfiguration);

        Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
        Mockito.verify(recoveryManager, Mockito.times(5)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(exclusionFlowConfiguration).getFlowElementInvokerConfiguredResources();
        Mockito.verify(exclusionFlowConfiguration).getConfiguredResourceFlowElements();
        Mockito.verify(exclusionFlowConfiguration).getErrorReportingServiceAwareFlowElements();
        Mockito.verify(exclusionFlowConfiguration).getManagedResourceFlowElements();
        Mockito.verifyNoMoreInteractions(exclusionFlowConfiguration);

        Mockito.verifyNoMoreInteractions(managedResource);

        Mockito.verifyNoMoreInteractions(managedResourceFlowElement1);

        Mockito.verifyNoMoreInteractions(configuredResourceFlowElement);

        Mockito.verifyNoMoreInteractions(managedResourceRecoveryManagerFactory);

        Mockito.verify(consumer, Mockito.times(4)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).getEventFactory();
        Mockito.verify(consumer).setEventFactory(Mockito.any());
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(exclusionService).isBlackListed("identifier");
        Mockito.verifyNoMoreInteractions(exclusionService);

        Mockito.verify(flowInvocationContextListener).endFlow(flowInvocationContext);
        Mockito.verifyNoMoreInteractions(flowInvocationContextListener);

        Mockito.verify(flowInvocationContext).setLastComponentName("consumer");
        Mockito.verify(flowInvocationContext).startFlowInvocation();
        Mockito.verify(flowInvocationContext).endFlowInvocation();
        Mockito.verifyNoMoreInteractions(flowInvocationContext);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
        Mockito.verify(monitor).invoke("stopped");
        Mockito.verify(monitor, Mockito.times(2)).invoke("running");
        Mockito.verifyNoMoreInteractions(monitor);

    }

    /**
     * Test failed flow invoke with a flow event, but dynamic dao failing.
     */
    @Test
    void test_failed_flow_invoke_with_flowEvent_stoppingInError_due_to_dynamicConfiguration_failure()
    {
        final RuntimeException exception = new RuntimeException("test failed dynamic dao");
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            recoveryManager, exclusionService
        );

        // set start invocation expectations
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements);

        // expectations
        // always get the original exceptionLifeIdentifier
        Mockito.when(flowEvent.getIdentifier()).thenReturn("identifier");

        Mockito.when(dynamicConfiguredResourceFlowElement.getFlowComponent()).thenReturn(configuredResource);

        Mockito.doNothing().doNothing()

               .doThrow(exception).when(flowConfiguration).configure(configuredResource);

        Mockito.when(exclusionService.isBlackListed("identifier")).thenReturn(false);

        // pass the exception to the recovery manager
        Mockito.doThrow(new ForceTransactionRollbackException("")).when(recoveryManager)
               .recover(flowInvocationContext, exception, flowEvent, "identifier");

        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getComponentName()).thenReturn("consumer");

        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // state is stopped when setting monitor and on init of start()
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true).thenReturn(false);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false).thenReturn(false).thenReturn(true);

        flow.setMonitor(monitor);

        try
        {

            flow.start();

            // run invoke
            //  setGetStateExpectations(isNotRecovering, isNotRunning, isUnrecoverable);
            // setMonitorExpectations("stoppedInError");
            flow.invoke(flowEvent);
        }
        catch (ForceTransactionRollbackException e)
        {
            // test assertions
            Mockito.verify(flowConfiguration, Mockito.times(4)).configure(Mockito.any());
            Mockito.verify(flowConfiguration, Mockito.times(6)).getConsumerFlowElement();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
            Mockito.verifyNoMoreInteractions(flowConfiguration);

            Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
            Mockito.verify(recoveryManager, Mockito.times(4)).isRecovering();
            Mockito.verify(recoveryManager, Mockito.times(3)).isUnrecoverable();
            Mockito.verify(recoveryManager).initialise();
            Mockito.verify(recoveryManager).recover(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
            Mockito.verifyNoMoreInteractions(recoveryManager);

            Mockito.verify(flowInvocationContext).setLastComponentName("consumer");
            Mockito.verify(flowInvocationContext).startFlowInvocation();
            Mockito.verify(flowInvocationContext).endFlowInvocation();
            Mockito.verifyNoMoreInteractions(flowInvocationContext);

            Mockito.verify(consumer, Mockito.times(4)).isRunning();
            Mockito.verify(consumer).start();
            Mockito.verify(consumer).setListener(Mockito.any());
            Mockito.verify(consumer).getEventFactory();
            Mockito.verifyNoMoreInteractions(consumer);

            Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
            Mockito.verify(monitor).invoke("stopped");
            Mockito.verify(monitor).invoke("stoppedInError");
            Mockito.verify(monitor, Mockito.times(1)).invoke("running");
            Mockito.verifyNoMoreInteractions(monitor);

            //throw e;
        }

    }

    /**
     * Test failed flow invoke with a flow event with the resulting outcome being stoppedInError.
     */
    @Test
    void test_failed_flow_invoke_with_flowEvent_resulting_in_stoppedInError()
    {
        final RuntimeException exception = new RuntimeException("test failed flow invocation");
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations

        // always get the original exceptionLifeIdentifier
        Mockito.when(flowEvent.getIdentifier()).thenReturn("identifier");

        Mockito.when(dynamicConfiguredResourceFlowElement.getFlowComponent()).thenReturn(configuredResource);

        Mockito.when(flowConfiguration.getReplayRecordService()).thenReturn(replayRecordService);

        Mockito.when(exclusionService.isBlackListed("identifier")).thenReturn(false);

        // invoke the flow elements
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowElementInvoker()).thenReturn(flowElementInvoker);

//        Mockito.when(consumerFlowElement.getConfiguration()).thenReturn(new FlowElementPersistentConfiguration());

        Mockito.when(flowElementInvoker
            .invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent,
                consumerFlowElement
                   )).thenThrow(exception);

        // pass the exception to the recovery manager
        Mockito.doThrow(new ForceTransactionRollbackException("")).when(recoveryManager)
               .recover(flowInvocationContext, exception, flowEvent, "identifier");
        ;

        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getComponentName()).thenReturn("consumer");
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // state is stopped when setting monitor and on init of start()
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true).thenReturn(false);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false).thenReturn(false).thenReturn(true);

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            recoveryManager, exclusionService
        );

        // set start invocation expectations
        flow.setMonitor(monitor);
        flow.setFlowListener(flowEventListener);

        flow.start();

        // run test

        try
        {
            flow.invoke(flowEvent);
        }
        catch (ForceTransactionRollbackException e)
        {

            // test assertions
            Mockito.verify(flowConfiguration, Mockito.times(1)).configure(Mockito.any());
            Mockito.verify(flowConfiguration, Mockito.times(7)).getConsumerFlowElement();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getReplayRecordService();
            Mockito.verifyNoMoreInteractions(flowConfiguration);

            Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
            Mockito.verify(recoveryManager, Mockito.times(4)).isRecovering();
            Mockito.verify(recoveryManager, Mockito.times(3)).isUnrecoverable();
            Mockito.verify(recoveryManager).initialise();
            Mockito.verify(recoveryManager).recover(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
            Mockito.verifyNoMoreInteractions(recoveryManager);

            Mockito.verify(flowInvocationContext).setLastComponentName("consumer");
            Mockito.verify(flowInvocationContext).startFlowInvocation();
            Mockito.verify(flowInvocationContext).endFlowInvocation();
            Mockito.verifyNoMoreInteractions(flowInvocationContext);

            Mockito.verify(consumer, Mockito.times(4)).isRunning();
            Mockito.verify(consumer).start();
            Mockito.verify(consumer).setListener(Mockito.any());
            Mockito.verify(consumer).getEventFactory();
            Mockito.verify(consumer).setEventFactory(Mockito.any());
            Mockito.verifyNoMoreInteractions(consumer);

            Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
            Mockito.verify(monitor).invoke("stopped");
            Mockito.verify(monitor).invoke("stoppedInError");
            Mockito.verify(monitor, Mockito.times(1)).invoke("running");
            Mockito.verifyNoMoreInteractions(monitor);

        }

    }

    /**
     * Test failed flow invoke with a flow event with the resulting outcome being recovering.
     */
    @Test
    void test_failed_flow_invoke_with_flowEvent_resulting_in_recovery()
    {
        final RuntimeException exception = new RuntimeException("test failed flow invocation");
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations
        // always get the original exceptionLifeIdentifier
        Mockito.when(flowEvent.getIdentifier()).thenReturn("identifier");

        Mockito.when(dynamicConfiguredResourceFlowElement.getFlowComponent()).thenReturn(configuredResource);

        Mockito.when(flowConfiguration.getReplayRecordService()).thenReturn(replayRecordService);

        Mockito.when(exclusionService.isBlackListed("identifier")).thenReturn(false);

        // invoke the flow elements
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowElementInvoker()).thenReturn(flowElementInvoker);

//        Mockito.when(consumerFlowElement.getConfiguration()).thenReturn(new FlowElementPersistentConfiguration());

        Mockito.when(flowElementInvoker
            .invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent,
                consumerFlowElement
                   )).thenThrow(exception);

        // pass the exception to the recovery manager
        Mockito.
                   doThrow(new ForceTransactionRollbackException("")).when(recoveryManager)
               .recover(flowInvocationContext, exception, flowEvent, "identifier");

        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getComponentName()).thenReturn("consumer");

        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // state is stopped when setting monitor and on init of start()
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true).thenReturn(false);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false).thenReturn(false);
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false).thenReturn(false).thenReturn(false)
               .thenReturn(true);

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            recoveryManager, exclusionService
        );

        // set start invocation expectations
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements);

        try
        {
            // state is stopped when setting monitor and on init of start()
            flow.setMonitor(monitor);

            flow.start();

            // run test
            //            setMonitorExpectations("recovering");
            flow.invoke(flowEvent);
        }
        catch (ForceTransactionRollbackException e)
        {
            // test assertions
            Mockito.verify(flowConfiguration, Mockito.times(4)).configure(Mockito.any());
            Mockito.verify(flowConfiguration, Mockito.times(6)).getConsumerFlowElement();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
            Mockito.verify(flowConfiguration, Mockito.times(1)).getReplayRecordService();
            Mockito.verifyNoMoreInteractions(flowConfiguration);

            Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
            Mockito.verify(recoveryManager, Mockito.times(4)).isRecovering();
            Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
            Mockito.verify(recoveryManager).initialise();
            Mockito.verify(recoveryManager).recover(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
            Mockito.verifyNoMoreInteractions(recoveryManager);

            Mockito.verify(flowInvocationContext).setLastComponentName("consumer");
            Mockito.verify(flowInvocationContext).startFlowInvocation();
            Mockito.verify(flowInvocationContext).endFlowInvocation();
            Mockito.verifyNoMoreInteractions(flowInvocationContext);

            Mockito.verify(consumer, Mockito.times(3)).isRunning();
            Mockito.verify(consumer).start();
            Mockito.verify(consumer).setListener(Mockito.any());
            Mockito.verify(consumer).getEventFactory();
            //            Mockito.verify(consumer).setEventFactory(Mockito.any());
            Mockito.verifyNoMoreInteractions(consumer);

            Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
            Mockito.verify(monitor).invoke("stopped");
            Mockito.verify(monitor).invoke("recovering");
            Mockito.verify(monitor, Mockito.times(1)).invoke("running");
            Mockito.verifyNoMoreInteractions(monitor);

        }

    }

    /**
     * Test failed flow invoke with a flow event with the resulting outcome being recovering.
     */
    @Test
    void test_failed_flow_invoke_with_flowEvent_resulting_in_ignore()
    {
        final RuntimeException exception = new RuntimeException("test failed flow invocation");
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // always get the original exceptionLifeIdentifier
        Mockito.when(flowEvent.getIdentifier()).thenReturn("identifier");

        Mockito.when(dynamicConfiguredResourceFlowElement.getFlowComponent()).thenReturn(configuredResource);

        Mockito.when(flowConfiguration.getReplayRecordService()).thenReturn(replayRecordService);

        Mockito.when(exclusionService.isBlackListed("identifier")).thenReturn(false);

        // invoke the flow elements
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowElementInvoker()).thenReturn(flowElementInvoker);
//        Mockito.when(consumerFlowElement.getConfiguration()).thenReturn(new FlowElementPersistentConfiguration());

        Mockito.when(flowElementInvoker
            .invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent,
                consumerFlowElement
                   )).thenThrow(exception);

        // pass the exception to the recovery manager
        // ignore doesnt throw exception
        Mockito.when(dynamicConfiguredResourceFlowElement.getFlowComponent()).thenReturn(dynamicConfiguredResource);

        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getComponentName()).thenReturn("consumer");

        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // state is stopped when setting monitor and on init of start()
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true).thenReturn(true);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false);
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false).thenReturn(false).thenReturn(false)
               .thenReturn(false);

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            recoveryManager, exclusionService
        );

        // set start invocation expectations
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements);

        // state is stopped when setting monitor and on init of start()
        flow.setMonitor(monitor);

        // run test

        flow.setFlowListener(flowEventListener);
        flow.start();

        flow.invoke(flowEvent);

        // test assertions
        Mockito.verify(flowConfiguration, Mockito.times(4)).configure(Mockito.any());
        Mockito.verify(flowConfiguration, Mockito.times(7)).getConsumerFlowElement();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getReplayRecordService();
        Mockito.verify(flowConfiguration, Mockito.times(1)).update(dynamicConfiguredResource);
        Mockito.verifyNoMoreInteractions(flowConfiguration);

        Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
        Mockito.verify(recoveryManager, Mockito.times(4)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verify(recoveryManager).recover(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(flowInvocationContext).setLastComponentName("consumer");
        Mockito.verify(flowInvocationContext).startFlowInvocation();
        Mockito.verify(flowInvocationContext).endFlowInvocation();
        Mockito.verifyNoMoreInteractions(flowInvocationContext);

        Mockito.verify(flowElementInvoker).setIgnoreContextInvocation(true);
        Mockito.verify(flowElementInvoker).setFlowInvocationContextListeners(null);
        Mockito.verify(flowElementInvoker).setInvokeContextListeners(false);
        Mockito.verify(flowElementInvoker)
               .invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent,
                   consumerFlowElement
                      );
        Mockito.verify(flowElementInvoker).getConfiguration();
        Mockito.verifyNoMoreInteractions(flowElementInvoker);

        Mockito.verify(consumer, Mockito.times(4)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).getEventFactory();
        //            Mockito.verify(consumer).setEventFactory(Mockito.any());
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(flowEvent).getIdentifier();
        Mockito.verifyNoMoreInteractions(flowEvent);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
        Mockito.verify(monitor).invoke("stopped");
        Mockito.verify(monitor, Mockito.times(2)).invoke("running");
        Mockito.verifyNoMoreInteractions(monitor);

    }

    /**
     * Test failed flow invoke with a flow event with the resulting outcome
     * being continue to run.
     */
    @Test
    void test_failed_flow_invoke_with_flowEvent_resulting_in_continuing_to_run()
    {
        final RuntimeException exception = new RuntimeException("test failed flow invocation");
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations

        // always get the original exceptionLifeIdentifier
        Mockito.when(flowEvent.getIdentifier()).thenReturn("identifier");

        Mockito.when(dynamicConfiguredResourceFlowElement.getFlowComponent()).thenReturn(configuredResource);

        Mockito.when(flowConfiguration.getReplayRecordService()).thenReturn(replayRecordService);

        Mockito.when(exclusionService.isBlackListed("identifier")).thenReturn(false);

        // invoke the flow elements
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowElementInvoker()).thenReturn(flowElementInvoker);

//        Mockito.when(consumerFlowElement.getConfiguration()).thenReturn(new FlowElementPersistentConfiguration());

        Mockito.when(flowElementInvoker
            .invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent,
                consumerFlowElement
                   )).thenThrow(exception);

        // no exceptions thrown to rollback

        Mockito.when(dynamicConfiguredResourceFlowElement.getFlowComponent()).thenReturn(dynamicConfiguredResource);

        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getComponentName()).thenReturn("consumer");

        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // state is stopped when setting monitor and on init of start()
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true).thenReturn(true);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false);
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false).thenReturn(false).thenReturn(false)
               .thenReturn(false);

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            recoveryManager, exclusionService
        );

        // set start invocation expectations
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements);

        // state is stopped when setting monitor and on init of start()
        flow.setMonitor(monitor);
        flow.setFlowListener(flowEventListener);

        flow.start();

        // run test
        flow.invoke(flowEvent);

        // test assertions
        Mockito.verify(flowConfiguration, Mockito.times(4)).configure(Mockito.any());
        Mockito.verify(flowConfiguration, Mockito.times(7)).getConsumerFlowElement();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getReplayRecordService();
        Mockito.verify(flowConfiguration, Mockito.times(1)).update(dynamicConfiguredResource);
        Mockito.verifyNoMoreInteractions(flowConfiguration);

        Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
        Mockito.verify(recoveryManager, Mockito.times(4)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verify(recoveryManager).recover(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(flowInvocationContext).setLastComponentName("consumer");
        Mockito.verify(flowInvocationContext).startFlowInvocation();
        Mockito.verify(flowInvocationContext).endFlowInvocation();
        Mockito.verifyNoMoreInteractions(flowInvocationContext);

        Mockito.verify(flowElementInvoker).setIgnoreContextInvocation(true);
        Mockito.verify(flowElementInvoker).setFlowInvocationContextListeners(null);
        Mockito.verify(flowElementInvoker).setInvokeContextListeners(false);
        Mockito.verify(flowElementInvoker)
               .invoke(flowEventListeners, "moduleName", "flowName", flowInvocationContext, flowEvent,
                   consumerFlowElement
                      );
        Mockito.verify(flowElementInvoker).getConfiguration();
        Mockito.verifyNoMoreInteractions(flowElementInvoker);

        Mockito.verify(consumer, Mockito.times(4)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).getEventFactory();
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(flowEvent).getIdentifier();
        Mockito.verifyNoMoreInteractions(flowEvent);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
        Mockito.verify(monitor, Mockito.times(2)).invoke("running");
        Mockito.verify(monitor, Mockito.times(1)).invoke("stopped");
        Mockito.verifyNoMoreInteractions(monitor);
    }

    /**
     * Test failed flow invoke with an exception resulting outcome
     * being continue to run.
     */
    @Test
    void test_failed_flow_invoke_with_exception_resulting_in_continuing_to_run()
    {
        final RuntimeException exception = new RuntimeException("invoked with exception test");
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations
        // get the context
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getComponentName()).thenReturn("consumerName");

        // pass the exception to the recovery manager
        //   Mockito.when(recoveryManager).recover("consumerName", exception);

        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // state is stopped when setting monitor and on init of start()
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true).thenReturn(true);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false);
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false).thenReturn(false).thenReturn(false)
               .thenReturn(false);

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            recoveryManager, exclusionService
        );

        // set start invocation expectations
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements);

        // state is stopped when setting monitor and on init of start()
        flow.setMonitor(monitor);

        // run test
        flow.setFlowListener(flowEventListener);
        flow.start();
        flow.invoke(exception);

        // test assertions
        Mockito.verify(flowConfiguration, Mockito.times(3)).configure(Mockito.any());
        Mockito.verify(flowConfiguration, Mockito.times(6)).getConsumerFlowElement();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
        Mockito.verifyNoMoreInteractions(flowConfiguration);

        Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
        Mockito.verify(recoveryManager, Mockito.times(4)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verify(recoveryManager).recover("consumerName", exception);
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verifyNoMoreInteractions(flowInvocationContext);
        Mockito.verifyNoMoreInteractions(flowElementInvoker);

        Mockito.verify(consumer, Mockito.times(4)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).getEventFactory();
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
        Mockito.verify(monitor, Mockito.times(2)).invoke("running");
        Mockito.verify(monitor, Mockito.times(1)).invoke("stopped");
        Mockito.verifyNoMoreInteractions(monitor);

    }

    /**
     * Test failed flow invoke with an exception resulting outcome
     * being recovery.
     */
    @Test
    void test_failed_flow_invoke_with_exception_resulting_in_recovery()
    {
        final RuntimeException exception = new RuntimeException("invoked with exception test");
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations
        // get the context
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getComponentName()).thenReturn("consumerName");

        // pass the exception to the recovery manager

        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // state is stopped when setting monitor and on init of start()
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true).thenReturn(false);
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false);
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false).thenReturn(false).thenReturn(true)
               .thenReturn(false);

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            recoveryManager, exclusionService
        );

        // set start invocation expectations
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements);

        flow.setMonitor(monitor);

        flow.start();

        // run test
        flow.invoke(exception);

        // test assertions
        Mockito.verify(flowConfiguration, Mockito.times(3)).configure(Mockito.any());
        Mockito.verify(flowConfiguration, Mockito.times(5)).getConsumerFlowElement();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
        Mockito.verifyNoMoreInteractions(flowConfiguration);

        Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
        Mockito.verify(recoveryManager, Mockito.times(4)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verify(recoveryManager).recover("consumerName", exception);
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verifyNoMoreInteractions(flowInvocationContext);
        Mockito.verifyNoMoreInteractions(flowElementInvoker);

        Mockito.verify(consumer, Mockito.times(3)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).getEventFactory();
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
        Mockito.verify(monitor, Mockito.times(1)).invoke("running");
        Mockito.verify(monitor, Mockito.times(1)).invoke("recovering");
        Mockito.verify(monitor, Mockito.times(1)).invoke("stopped");
        Mockito.verifyNoMoreInteractions(monitor);

    }

    /**
     * Test failed flow invoke with an exception resulting outcome
     * being stoppedInError.
     */
    @Test
    void test_failed_flow_invoke_with_exception_resulting_in_stoppedInError()
    {
        final RuntimeException exception = new RuntimeException("invoked with exception test");
        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        // expectations

        // get the context
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getComponentName()).thenReturn("consumerName");

        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // state is stopped when setting monitor and on init of start()
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false).thenReturn(false).thenReturn(false);
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true).thenReturn(false);
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false).thenReturn(false).thenReturn(true);

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            recoveryManager, exclusionService
        );

        // set start invocation expectations
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements);

        // state is stopped when setting monitor and on init of start()
        flow.setMonitor(monitor);

        // run test
        flow.setFlowListener(flowEventListener);

        flow.start();
        flow.invoke(exception);

        // test assertions
        Mockito.verify(flowConfiguration, Mockito.times(3)).configure(Mockito.any());
        Mockito.verify(flowConfiguration, Mockito.times(6)).getConsumerFlowElement();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
        Mockito.verifyNoMoreInteractions(flowConfiguration);

        Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
        Mockito.verify(recoveryManager, Mockito.times(4)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(3)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verify(recoveryManager).recover("consumerName", exception);
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verifyNoMoreInteractions(flowInvocationContext);
        Mockito.verifyNoMoreInteractions(flowElementInvoker);

        Mockito.verify(consumer, Mockito.times(4)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).getEventFactory();
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
        Mockito.verify(monitor, Mockito.times(1)).invoke("running");
        Mockito.verify(monitor, Mockito.times(1)).invoke("stoppedInError");
        Mockito.verify(monitor, Mockito.times(1)).invoke("stopped");
        Mockito.verifyNoMoreInteractions(monitor);
    }

    /**
     * Test getter for named flowElements
     */
    @Test
    void test_accessor_for_named_flow_elements()
    {
        final List<FlowElement<?>> configuredResourceFlowElements = new ArrayList<>();

        // expectations
        // get all flow elements
        Mockito.when(flowConfiguration.getFlowElements()).thenReturn(configuredResourceFlowElements);

        // container for the complete flow
        VisitingInvokerFlow flow = new ExtendedVisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            recoveryManager, exclusionService
        );

        assertNotNull(flow.getFlowElements(), "there should be one flow elements on this flow");

        // test assertions

        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
        Mockito.verifyNoMoreInteractions(flowConfiguration);

    }

    /**
     * Test successful flow start from a stopped state.
     */
    @Test
    void test_successful_flow_start_from_stopped_with_exclusionFlow_having_manageResources()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory
        );
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        final List<FlowElement<ManagedResource>> managedResourceExclusionFlowElements = new ArrayList<>();
        managedResourceExclusionFlowElements.add(managedResourceFlowElementExclusion1);

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);

        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements =
            new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements = new ArrayList<>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements = new ArrayList<>();

        // set start invocation expectations
        setStartExpectations(flow, dynamicConfiguredResourceFlowElements, configuredResourceFlowElements,
            managedResourceFlowElements, managedResourceExclusionFlowElements
                            );

        // expectations
        // handle managed resources on the exclusion flow
        // set the managed resource recovery manager instance on each managed resource
        Mockito.when(managedResourceFlowElementExclusion1.getFlowComponent()).thenReturn(managedResource);
        Mockito.when(managedResourceFlowElementExclusion1.getComponentName()).thenReturn("component name");
        Mockito.when(managedResourceRecoveryManagerFactory.getManagedResourceRecoveryManager("component name"))
               .thenReturn(managedResourceRecoveryManager);
        //                    Mockito.when(managedResource.setManagedResourceRecoveryManager
        //                    (managedResourceRecoveryManager);

        // start each managed resource from right to left (reverse order) in flow order
        //                    Mockito.when(managedResource).startManagedResource();
        Mockito.when(managedResourceFlowElementExclusion1.getComponentName()).thenReturn("component name");

        // handle managed resources on the normal flow
        // set the managed resource recovery manager instance on each managed resource
        Mockito.when(managedResourceFlowElement1.getFlowComponent()).thenReturn(managedResource);
        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("component name");
        Mockito.when(managedResourceRecoveryManagerFactory.getManagedResourceRecoveryManager("component name"))
               .thenReturn(managedResourceRecoveryManager);
        //                    Mockito.when(managedResource).setManagedResourceRecoveryManager
        //                    (managedResourceRecoveryManager);

        // start each managed resource from right to left (reverse order) in flow order
        //                    Mockito.when(managedResource).startManagedResource();
        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("managedResourceFlowElementName");

        // state is stopped when setting monitor and on init of start()
        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(false);
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false).thenReturn(false).thenReturn(false)
               .thenReturn(false);

        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

        // start will result in the monitor being updated to running

        flow.start();

        // test assertions
        Mockito.verify(flowConfiguration, Mockito.times(3)).configure(Mockito.any());
        Mockito.verify(flowConfiguration, Mockito.times(4)).getConsumerFlowElement();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
        Mockito.verifyNoMoreInteractions(flowConfiguration);

        Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
        Mockito.verify(recoveryManager, Mockito.times(3)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(managedResourceFlowElement1, Mockito.times(3)).getComponentName();
        Mockito.verify(managedResourceFlowElement1, Mockito.times(1)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement1);
        Mockito.verify(managedResourceFlowElementExclusion1, Mockito.times(3)).getComponentName();
        Mockito.verify(managedResourceFlowElementExclusion1, Mockito.times(1)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElementExclusion1);

        Mockito.verify(configuredResourceFlowElement, Mockito.times(4)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(configuredResourceFlowElement);

        Mockito.verifyNoMoreInteractions(flowInvocationContext);
        Mockito.verifyNoMoreInteractions(flowElementInvoker);

        Mockito.verify(consumer, Mockito.times(3)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).getEventFactory();
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
        Mockito.verify(monitor, Mockito.times(1)).invoke("running");
        Mockito.verify(monitor, Mockito.times(1)).invoke("stopped");
        Mockito.verifyNoMoreInteractions(monitor);

    }

    /**
     * Test successful flow start from a stoppedInError state.
     */
    @Test
    void test_successful_flow_start_from_stoppedInError_with_exclusionFlow_having_managedResource()
    {
        // container for the complete flow
        final VisitingInvokerFlow flow = new VisitingInvokerFlow("flowName", "moduleName", flowConfiguration,
            exclusionFlowConfiguration, recoveryManager, exclusionService, serialiserFactory
        );
        flow.setManagedResourceRecoveryManagerFactory(managedResourceRecoveryManagerFactory);

        final List<FlowElement<ManagedResource>> managedResourceExclusionFlowElements = new ArrayList<>();
        managedResourceExclusionFlowElements.add(managedResourceFlowElementExclusion1);

        final List<FlowElement<ManagedResource>> managedResourceFlowElements = new ArrayList<>();
        managedResourceFlowElements.add(managedResourceFlowElement1);

        final List<FlowElement<IsErrorReportingServiceAware>> errorReportingServiceAwareFlowElements =
            new ArrayList<>();
        errorReportingServiceAwareFlowElements.add(errorReportingServiceAwareFlowElement1);

        final List<Notifier> notifiers = new ArrayList<>();

        final List<FlowElement<?>> configuredResourceInvokers = new ArrayList<FlowElement<?>>();

        final List<FlowElement<ConfiguredResource>> configuredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        configuredResourceFlowElements.add(configuredResourceFlowElement);
        configuredResourceFlowElements.add(configuredResourceFlowElement);

        final List<FlowElement<ConfiguredResource>> dynamicConfiguredResourceFlowElements =
            new ArrayList<FlowElement<ConfiguredResource>>();
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);
        dynamicConfiguredResourceFlowElements.add(dynamicConfiguredResourceFlowElement);

        final List<FlowElement<?>> flowElements = new ArrayList<FlowElement<?>>();

        // expectations
        // load monitor configuration and notifiers
        Mockito.when(monitor.getNotifiers()).thenReturn(notifiers);

        // get the two flow element configured resources
        Mockito.when(flowConfiguration.getConfiguredResourceFlowElements()).thenReturn(configuredResourceFlowElements);

        // load flow configuration
        Mockito.when(configuredResourceFlowElement.getFlowComponent()).thenReturn(configuredResource);

        Mockito.when(configuredResource.getConfiguredResourceId()).thenReturn("configuredResourceId");

        // get the two exclusion flow element configured resources
        Mockito.when(exclusionFlowConfiguration.getConfiguredResourceFlowElements())
               .thenReturn(configuredResourceFlowElements);

        Mockito.when(configuredResource.getConfiguredResourceId()).thenReturn("configuredResourceId");

        // flow dynamic configuration
        Mockito.when(flowConfiguration.getDynamicConfiguredResourceFlowElements())
               .thenReturn(dynamicConfiguredResourceFlowElements);

        // flow invoker configuration
        Mockito.when(flowConfiguration.getFlowElementInvokerConfiguredResources())
               .thenReturn(configuredResourceInvokers);

        // exclusion flow invoker configuration
        Mockito.when(exclusionFlowConfiguration.getFlowElementInvokerConfiguredResources())
               .thenReturn(configuredResourceInvokers);

        Mockito.when(flowConfiguration.getFlowElements()).thenReturn(flowElements);

        // inject errorReportingService to those needing it on the business flow
        Mockito.when(flowConfiguration.getErrorReportingServiceAwareFlowElements())
               .thenReturn(errorReportingServiceAwareFlowElements);

        Mockito.when(errorReportingServiceAwareFlowElement1.getFlowComponent())
               .thenReturn(errorReportingServiceAwareComponent);

        // inject errorReportingService to those needing it on the exclusion flow
        Mockito.when(exclusionFlowConfiguration.getErrorReportingServiceAwareFlowElements())
               .thenReturn(errorReportingServiceAwareFlowElements);

        Mockito.when(errorReportingServiceAwareFlowElement1.getFlowComponent())
               .thenReturn(errorReportingServiceAwareComponent);

        // get the three flow element managed resources
        Mockito.when(exclusionFlowConfiguration.getManagedResourceFlowElements())
               .thenReturn(managedResourceExclusionFlowElements);

        // set the managed resource recovery manager instance on each managed resource
        Mockito.when(managedResourceFlowElementExclusion1.getFlowComponent()).thenReturn(exclusionManagedResource);
        Mockito.when(managedResourceFlowElementExclusion1.getComponentName()).thenReturn("exclusion component name");
        Mockito
            .when(managedResourceRecoveryManagerFactory.getManagedResourceRecoveryManager("exclusion component name"))
            .thenReturn(exclusionManagedResourceRecoveryManager);

        // start each managed resource from right to left (reverse order) in flow order

        Mockito.when(managedResourceFlowElementExclusion1.getComponentName()).thenReturn("exclusion component name");

        // get the three flow element managed resources
        Mockito.when(flowConfiguration.getManagedResourceFlowElements()).thenReturn(managedResourceFlowElements);

        // clear recovery manager states
        // pass any managed resources to the recovery manager

        // set the managed resource recovery manager instance on each managed resource
        Mockito.when(managedResourceFlowElement1.getFlowComponent()).thenReturn(managedResource);
        Mockito.when(managedResourceFlowElement1.getComponentName()).thenReturn("component name");
        Mockito.when(managedResourceRecoveryManagerFactory.getManagedResourceRecoveryManager("component name"))
               .thenReturn(managedResourceRecoveryManager);

        // get the consumer
        Mockito.when(flowConfiguration.getConsumerFlowElement()).thenReturn(consumerFlowElement);
        Mockito.when(consumerFlowElement.getFlowComponent()).thenReturn(consumer);

        // check eventFactory for consumer
        Mockito.when(consumer.getEventFactory()).thenReturn(flowEventFactory);

        Mockito.when(consumer.isRunning()).thenReturn(false).thenReturn(false).thenReturn(true);

        // Set Flow state to StoppedInError
        Mockito.when(recoveryManager.isUnrecoverable()).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(recoveryManager.isRecovering()).thenReturn(false).thenReturn(false).thenReturn(false)
               .thenReturn(false);

        // set the monitor and receive initial state callback
        //            setGetStateExpectations(isNotRecovering, isNotRunning, isUnrecoverable);
        //            setMonitorExpectations("stoppedInError");
        flow.setMonitor(monitor);
        flow.setErrorReportingService(errorReportingService);

        // check state before proceeding with start
        //            setGetStateExpectations(isNotRecovering, isNotRunning, isUnrecoverable);

        // start will result in the monitor being updated to running
        //            setGetStateExpectations(isNotRecovering, isRunning, isNotUnrecoverable);
        //            setMonitorExpectations("running");
        flow.start();

        // test assertions
        Mockito.verify(flowConfiguration, Mockito.times(5)).configure(Mockito.any());
        Mockito.verify(flowConfiguration, Mockito.times(4)).getConsumerFlowElement();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getDynamicConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getFlowElements();
        Mockito.verify(flowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
        Mockito.verifyNoMoreInteractions(flowConfiguration);

        Mockito.verify(exclusionFlowConfiguration, Mockito.times(1)).getManagedResourceFlowElements();
        Mockito.verify(exclusionFlowConfiguration, Mockito.times(1)).getFlowElementInvokerConfiguredResources();
        Mockito.verify(exclusionFlowConfiguration, Mockito.times(1)).getConfiguredResourceFlowElements();
        Mockito.verify(exclusionFlowConfiguration, Mockito.times(1)).getErrorReportingServiceAwareFlowElements();
        Mockito.verifyNoMoreInteractions(exclusionFlowConfiguration);

        Mockito.verify(recoveryManager).setManagedResources(Mockito.any(List.class));
        Mockito.verify(recoveryManager, Mockito.times(3)).isRecovering();
        Mockito.verify(recoveryManager, Mockito.times(2)).isUnrecoverable();
        Mockito.verify(recoveryManager).initialise();
        Mockito.verifyNoMoreInteractions(recoveryManager);

        Mockito.verify(managedResourceFlowElement1, Mockito.times(3)).getComponentName();
        Mockito.verify(managedResourceFlowElement1, Mockito.times(1)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElement1);
        Mockito.verify(managedResourceFlowElementExclusion1, Mockito.times(3)).getComponentName();
        Mockito.verify(managedResourceFlowElementExclusion1, Mockito.times(1)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(managedResourceFlowElementExclusion1);

        Mockito.verify(configuredResourceFlowElement, Mockito.times(8)).getFlowComponent();
        Mockito.verifyNoMoreInteractions(configuredResourceFlowElement);

        Mockito.verifyNoMoreInteractions(flowInvocationContext);
        Mockito.verifyNoMoreInteractions(flowElementInvoker);

        Mockito.verify(consumer, Mockito.times(3)).isRunning();
        Mockito.verify(consumer).start();
        Mockito.verify(consumer).setListener(Mockito.any());
        Mockito.verify(consumer).getEventFactory();
        Mockito.verifyNoMoreInteractions(consumer);

        Mockito.verify(monitor, Mockito.times(1)).getNotifiers();
        Mockito.verify(monitor, Mockito.times(1)).invoke("running");
        Mockito.verify(monitor, Mockito.times(1)).invoke("stoppedInError");
        Mockito.verifyNoMoreInteractions(monitor);
    }


    /**
     * Extended test class allowing return of a mocked flowInvocationContext.
     *
     * @author Ikasan Developer Team
     */
    private class ExtendedVisitingInvokerFlow extends VisitingInvokerFlow
    {
        public ExtendedVisitingInvokerFlow(String name, String moduleName, FlowConfiguration flowConfiguration,
                                           RecoveryManager<FlowEvent<?, ?>, FlowInvocationContext, ?> recoveryManager,
                                           ExclusionService exclusionService)
        {
            super(name, moduleName, flowConfiguration, exclusionFlowConfiguration, recoveryManager, exclusionService,
                serialiserFactory
                 );
        }

        @Override
        protected FlowInvocationContext createFlowInvocationContext()
        {
            return flowInvocationContext;
        }

        @Override
        protected List<FlowEventListener> getFlowEventListeners()
        {
            return flowEventListeners;
        }
    }
}
