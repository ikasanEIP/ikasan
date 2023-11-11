package org.ikasan.ootb.scheduler.agent.module.boot.recovery;

import org.apache.commons.lang3.RandomStringUtils;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.module.ConfiguredModuleImpl;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.dashboard.ContextInstanceRestService;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.provision.ContextInstanceIdentifierProvisionService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentRecoveryRunnableTest {
    private static final String AGENT = "scheduler-agent";
    @Mock
    private ContextInstanceRestService contextInstanceRestService;

    @Mock
    private ModuleService moduleService;

    @Mock
    private ConfiguredModuleImpl module;

    @Mock
    private SchedulerAgentConfiguredModuleConfiguration configureModule;

    @Mock
    ContextInstanceIdentifierProvisionService contextInstanceIdentifierProvisionService;

    @Test
    void should_successfully_recover_instances() {
        AgentRecoveryRunnable runner = new AgentRecoveryRunnable(
            contextInstanceRestService, contextInstanceIdentifierProvisionService, 1, AGENT, moduleService);

        ContextInstance instance1 = new ContextInstanceImpl();
        String rootPlan1CorrelationId = RandomStringUtils.randomNumeric(10);
        String rootPlan1Name = "RootPlanName1";
        instance1.setName(rootPlan1Name);
        instance1.setId(rootPlan1CorrelationId);

        Map<String, String> contextFlowMap = Map.of("jobName1", rootPlan1Name);

        when(moduleService.getModule("scheduler-agent")).thenReturn(module);
        when(module.getConfiguration()).thenReturn(configureModule);
        when(configureModule.getFlowContextMap()).thenReturn(contextFlowMap);

        Map<String, ContextInstance> liveContextInstances = Map.of(rootPlan1CorrelationId, instance1);

        when(contextInstanceRestService.getAllInstancesDashboardThinksAgentShouldHandle(AGENT)).thenReturn(liveContextInstances);
        runner.run();
    }

    // takes at least 1 minute to run
    @Test
    @Disabled
    void should_throw_exception_if_times_out_recovering() {
        assertThrows(EndpointException.class, () -> {
            AgentRecoveryRunnable runner = new AgentRecoveryRunnable(
                contextInstanceRestService, contextInstanceIdentifierProvisionService, 1, AGENT, moduleService);

            String contextName = RandomStringUtils.randomAlphabetic(12);
            ContextInstance instance1 = new ContextInstanceImpl();
            String contextName1 = contextName + RandomStringUtils.randomNumeric(10);
            instance1.setName(contextName1);

            Map<String, String> contextFlowMap = Map.of(
                "jobName1", contextName1, "jobNName2", contextName1
            );

            when(moduleService.getModule("scheduler-agent")).thenReturn(module);
            when(module.getConfiguration()).thenReturn(configureModule);
            when(configureModule.getFlowContextMap()).thenReturn(contextFlowMap);

            when(contextInstanceRestService.getAllInstancesDashboardThinksAgentShouldHandle(AGENT)).thenThrow(new EndpointException("excepted exception"));

            runner.run();
        });
    }
}