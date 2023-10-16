package org.ikasan.ootb.scheduler.agent.module.service;

import org.ikasan.component.endpoint.filesystem.messageprovider.CorrelatedFileConsumerConfiguration;
import org.ikasan.component.endpoint.quartz.consumer.CorrelatedScheduledConsumerConfiguration;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.module.ConfiguredModuleImpl;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.InstanceStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.ikasan.ootb.scheduler.agent.module.service.ContextInstanceIdentifierProvisionServiceImpl.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContextInstanceIdentifierProvisionServiceImplTest {
    private static final String SCHEDULE_CONSUMER_FLOW="Schedule Consumer Flow";
    private static final String FILE_CONSUMER_FLOW="File Consumer Flow";
    private static final String DUMMY_FLOW="Dummy Flow";

    @Value( "${module.name}" )
    private String moduleName;
    @Mock
    private ModuleService moduleService;

    @Mock
    ConfigurationService configurationService;
    @Mock
    private Flow scheduleConsumerFlow;
    @Mock
    private Flow fileConsumerFlow;

    @Mock
    FlowElement schuleConsumerFlowElement;
    @Mock
    FlowElement fileConsumerFlowElement;
    @Mock
    private ConfiguredModuleImpl module;

    @Mock
    ConfiguredResource<CorrelatedScheduledConsumerConfiguration> scheduleConsumerConfiguredResource;

    @Mock
    ConfiguredResource<CorrelatedFileConsumerConfiguration> fileConsumerConfiguredResource;

    @Mock
    CorrelatedScheduledConsumerConfiguration correlatedScheduledConsumerConfiguration;

    @Mock
    CorrelatedFileConsumerConfiguration correlatedFileConsumerConfiguration;

    @InjectMocks
    private ContextInstanceIdentifierProvisionServiceImpl service;

    @Mock
    private SchedulerAgentConfiguredModuleConfiguration configureModule;

    private static final String ROOT_PLAN_NAME = "plan1";
    private static final String ROOT_PLAN_CONTEXT_INTANCE_ID = "instance123";
    private ContextInstance contextInstance;
    private static final Map<String, String> CONTEXT_FLOW_MAP = Map.of(
        FILE_CONSUMER_FLOW, ROOT_PLAN_NAME, SCHEDULE_CONSUMER_FLOW, ROOT_PLAN_NAME, DUMMY_FLOW, ROOT_PLAN_NAME
    );
    private static final Map<String, String> PROFILE_FLOW_MAP = Map.of(
        FILE_CONSUMER_FLOW, FILE_CONSUMER_PROFILE, SCHEDULE_CONSUMER_FLOW, SCHEDULED_CONSUMER_PROFILE, DUMMY_FLOW, "DUMMY"
    );
    @Before
    public void setup() {
        contextInstance = new ContextInstanceImpl();
        contextInstance.setName(ROOT_PLAN_NAME);
        contextInstance.setId(ROOT_PLAN_CONTEXT_INTANCE_ID);
        contextInstance.setStatus(InstanceStatus.RUNNING);
    }

    @Test
    public void test_provision_instance_refresh_when_current_plan_has_no_correlation_ids() {
        List<String> scheduledConsumerCorrelationIdList = new ArrayList<>();
        List<String> fileConsumerCorrelationIdList = new ArrayList<>();

        setupWhen(scheduledConsumerCorrelationIdList, fileConsumerCorrelationIdList);

        service.provision(contextInstance);

        // Verify we DO stop/start the module
        verify(scheduleConsumerFlow).getFlowElement(SCHEDULED_CONSUMER);
        verify(scheduleConsumerFlow).stop();
        verify(scheduleConsumerFlow).start();
        verify(fileConsumerFlow).getFlowElement(FILE_CONSUMER);
        verify(fileConsumerFlow).stop();
        verify(fileConsumerFlow).start();
        verifyNoMoreInteractions(scheduleConsumerFlow);
        verifyNoMoreInteractions(fileConsumerFlow);

        assertEquals(1, scheduledConsumerCorrelationIdList.size());
        assertEquals(ROOT_PLAN_CONTEXT_INTANCE_ID, scheduledConsumerCorrelationIdList.get(0));
        assertEquals(1, fileConsumerCorrelationIdList.size());
        assertEquals(ROOT_PLAN_CONTEXT_INTANCE_ID, fileConsumerCorrelationIdList.get(0));
    }

    @Test
    public void test_provision_refresh_when_another_instance_is_already_present() {
        String existingCorrelationId = "CorrelationId0";
        List<String> scheduledConsumerCorrelationIdList = new ArrayList<>(Arrays.asList(existingCorrelationId));
        List<String> fileConsumerCorrelationIdList = new ArrayList<>(Arrays.asList(existingCorrelationId));

        setupWhen(scheduledConsumerCorrelationIdList, fileConsumerCorrelationIdList);

        service.provision(contextInstance);

        // Verify we DO stop/start the module
        verify(scheduleConsumerFlow).getFlowElement(SCHEDULED_CONSUMER);
        verify(scheduleConsumerFlow).stop();
        verify(scheduleConsumerFlow).start();
        verify(fileConsumerFlow).getFlowElement(FILE_CONSUMER);
        verify(fileConsumerFlow).stop();
        verify(fileConsumerFlow).start();
        verifyNoMoreInteractions(scheduleConsumerFlow);
        verifyNoMoreInteractions(fileConsumerFlow);

        assertEquals(2, scheduledConsumerCorrelationIdList.size());
        assertEquals(existingCorrelationId, scheduledConsumerCorrelationIdList.get(0));
        assertEquals(ROOT_PLAN_CONTEXT_INTANCE_ID, scheduledConsumerCorrelationIdList.get(1));
        assertEquals(2, fileConsumerCorrelationIdList.size());
        assertEquals(existingCorrelationId, fileConsumerCorrelationIdList.get(0));
        assertEquals(ROOT_PLAN_CONTEXT_INTANCE_ID, fileConsumerCorrelationIdList.get(1));
    }

    @Test
    public void test_provision_refresh_when_same_correlation_id_presented_twice() {
        String existingCorrelationId = ROOT_PLAN_CONTEXT_INTANCE_ID;
        List<String> scheduledConsumerCorrelationIdList = new ArrayList<>(Arrays.asList(existingCorrelationId));
        List<String> fileConsumerCorrelationIdList = new ArrayList<>(Arrays.asList(existingCorrelationId));

        setupWhen(scheduledConsumerCorrelationIdList, fileConsumerCorrelationIdList);

        service.provision(contextInstance);

        // Verify we DON'T stop/start the module
        verify(scheduleConsumerFlow).getFlowElement(SCHEDULED_CONSUMER);
        verify(fileConsumerFlow).getFlowElement(FILE_CONSUMER);
        verifyNoMoreInteractions(scheduleConsumerFlow);
        verifyNoMoreInteractions(fileConsumerFlow);

        assertEquals(1, scheduledConsumerCorrelationIdList.size());
        assertEquals(ROOT_PLAN_CONTEXT_INTANCE_ID, scheduledConsumerCorrelationIdList.get(0));
        assertEquals(1, fileConsumerCorrelationIdList.size());
        assertEquals(ROOT_PLAN_CONTEXT_INTANCE_ID, fileConsumerCorrelationIdList.get(0));
    }

    @Test
    public void test_remove_all() {
        List<String> scheduledConsumerCorrelationIdList = new ArrayList<>();
        List<String> fileConsumerCorrelationIdList = new ArrayList<>();

        setupWhen(scheduledConsumerCorrelationIdList, fileConsumerCorrelationIdList);

        service.removeAll();

        // Verify we DO stop/start the module
        verify(scheduleConsumerFlow).getFlowElement(SCHEDULED_CONSUMER);
        verify(scheduleConsumerFlow).stop();
        verify(scheduleConsumerFlow).start();
        verify(fileConsumerFlow).getFlowElement(FILE_CONSUMER);
        verify(fileConsumerFlow).stop();
        verify(fileConsumerFlow).start();
        verifyNoMoreInteractions(scheduleConsumerFlow);
        verifyNoMoreInteractions(fileConsumerFlow);

        assertEquals(0, scheduledConsumerCorrelationIdList.size());
        assertEquals(0, fileConsumerCorrelationIdList.size());
    }

    @Test
    public void test_reset_success() {
        String existingCorrelationId = "CorrelationId0";
        List<String> scheduledConsumerCorrelationIdList = new ArrayList<>(Arrays.asList(existingCorrelationId));
        List<String> fileConsumerCorrelationIdList = new ArrayList<>(Arrays.asList(existingCorrelationId));

        setupWhen(scheduledConsumerCorrelationIdList, fileConsumerCorrelationIdList);

        service.reset(Map.of(contextInstance.getId(), contextInstance));

        // Verify we DO stop/start the module
        verify(scheduleConsumerFlow, times(2)).getFlowElement(SCHEDULED_CONSUMER);
        verify(scheduleConsumerFlow, times(2)).stop();
        verify(scheduleConsumerFlow, times(2)).start();
        verify(fileConsumerFlow, times(2)).getFlowElement(FILE_CONSUMER);
        verify(fileConsumerFlow, times(2)).stop();
        verify(fileConsumerFlow, times(2)).start();
        verifyNoMoreInteractions(scheduleConsumerFlow);
        verifyNoMoreInteractions(fileConsumerFlow);

        assertEquals(1, scheduledConsumerCorrelationIdList.size());
        assertEquals("instance123", scheduledConsumerCorrelationIdList.get(0));
    }

    @Test
    public void test_remove_success() {
        String existingCorrelationId1 = "CorrelationId0";
        String existingCorrelationId2 = "CorrelationId1";
        List<String> scheduledConsumerCorrelationIdList = new ArrayList<>(Arrays.asList(existingCorrelationId1, existingCorrelationId2));
        List<String> fileConsumerCorrelationIdList = new ArrayList<>(Arrays.asList(existingCorrelationId1, existingCorrelationId2));

        setupWhen(scheduledConsumerCorrelationIdList, fileConsumerCorrelationIdList);

        service.remove("CorrelationId0");

        // Verify we DO stop/start the module
        verify(scheduleConsumerFlow, times(2)).getFlowElement(SCHEDULED_CONSUMER);
        verify(scheduleConsumerFlow).stop();
        verify(scheduleConsumerFlow).start();
        verify(fileConsumerFlow, times(2)).getFlowElement(FILE_CONSUMER);
        verify(fileConsumerFlow).stop();
        verify(fileConsumerFlow).start();
        verifyNoMoreInteractions(scheduleConsumerFlow);
        verifyNoMoreInteractions(fileConsumerFlow);

        assertEquals(1, scheduledConsumerCorrelationIdList.size());
        assertEquals(existingCorrelationId2, scheduledConsumerCorrelationIdList.get(0));
    }

    private void setupWhen(List scheduledConsumerCorrelationIdList, List fileConsumerCorrelationIdList) {

        when(moduleService.getModule(moduleName)).thenReturn(module);
        when(module.getConfiguration()).thenReturn(configureModule);
        when(configureModule.getFlowContextMap()).thenReturn(CONTEXT_FLOW_MAP);
        when(configureModule.getFlowDefinitionProfiles()).thenReturn(PROFILE_FLOW_MAP);

        when(module.getFlow(SCHEDULE_CONSUMER_FLOW)).thenReturn(scheduleConsumerFlow);
        when(module.getFlow(FILE_CONSUMER_FLOW)).thenReturn(fileConsumerFlow);

        when(scheduleConsumerFlow.getFlowElement(SCHEDULED_CONSUMER)).thenReturn(schuleConsumerFlowElement);
        when(schuleConsumerFlowElement.getFlowComponent()).thenReturn(scheduleConsumerConfiguredResource);
        when(fileConsumerFlow.getFlowElement(FILE_CONSUMER)).thenReturn(fileConsumerFlowElement);
        when(fileConsumerFlowElement.getFlowComponent()).thenReturn(fileConsumerConfiguredResource);

        when(scheduleConsumerConfiguredResource.getConfiguration()).thenReturn(correlatedScheduledConsumerConfiguration);
        when(fileConsumerConfiguredResource.getConfiguration()).thenReturn(correlatedFileConsumerConfiguration);

        when(correlatedScheduledConsumerConfiguration.getCorrelatingIdentifiers()).thenReturn(scheduledConsumerCorrelationIdList);
        when(correlatedFileConsumerConfiguration.getCorrelatingIdentifiers()).thenReturn(fileConsumerCorrelationIdList);
    }
}