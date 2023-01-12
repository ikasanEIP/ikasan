package org.ikasan.ootb.scheduler.agent.module.boot.recovery;

import static org.mockito.Mockito.when;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.module.ConfiguredModuleImpl;
import org.ikasan.ootb.scheduler.agent.module.configuration.SchedulerAgentConfiguredModuleConfiguration;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.dashboard.ContextInstanceRestService;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AgentRecoveryRunnableTest {
    private static final String AGENT = "scheduler-agent";
    @Mock
    private ContextInstanceRestService contextInstanceRestService;

    @Mock
    private ModuleService moduleService;

    @Mock
    private ConfiguredModuleImpl module;

    @Mock
    private SchedulerAgentConfiguredModuleConfiguration configureModule;


    @Test
    public void should_successfully_recover_instances() {
        AgentRecoveryRunnable runner = new AgentRecoveryRunnable(contextInstanceRestService, 1, AGENT, moduleService);

        ContextInstance instance1 = new ContextInstanceImpl();
        String rootPlan1CorrelationId = RandomStringUtils.randomNumeric(10);
        String rootPlan1Name = "RootPlanName1";
        instance1.setName(rootPlan1Name);
        instance1.setId(rootPlan1CorrelationId);

        ContextInstance instance2 = new ContextInstanceImpl();
        String rootPlan2CorrelationId = RandomStringUtils.randomNumeric(10);
        String rootPlan2Name = "RootPlanName2";
        instance2.setName(rootPlan2Name);
        instance2.setId(rootPlan2CorrelationId);

        // This one is not in the flow map i.e. not recovered therefore we cant deal with it.
        ContextInstance instance3 = new ContextInstanceImpl();
        String rootPlan3CorrelationId = RandomStringUtils.randomNumeric(10);
        String rootPlan3Name = "RootPlanName3";
        instance3.setName(rootPlan3Name);
        instance3.setId(rootPlan3CorrelationId);

        Map<String, String> contextFlowMap = Map.of(
          "jobName1", rootPlan1Name, "jobNName2", rootPlan1Name, "jobName3", rootPlan2Name, "jobNName4", rootPlan2Name
        );

        when(moduleService.getModule("scheduler-agent")).thenReturn(module);
        when(module.getConfiguration()).thenReturn(configureModule);
        when(configureModule.getFlowContextMap()).thenReturn(contextFlowMap);

        Map<String, ContextInstance> map = Map.of(rootPlan1CorrelationId, instance1, rootPlan2CorrelationId, instance2, rootPlan3CorrelationId, instance3);

        when(contextInstanceRestService.getAllInstancesDashboardThinksAgentShouldHandle(AGENT)).thenReturn(map);

        runner.run();

        Assert.assertNotNull(ContextInstanceCache.instance().getByCorrelationId(rootPlan1CorrelationId));
        Assert.assertNotNull(ContextInstanceCache.instance().getByCorrelationId(rootPlan2CorrelationId));
        // Plan3 was not in the flow map i.e. not recovered by Ikasan so we are not expecting it.
        Assert.assertNull(ContextInstanceCache.instance().getByCorrelationId(rootPlan3CorrelationId));
    }

    @Test(expected = EndpointException.class)
    @Ignore // takes at least 1 minute to run
    public void should_throw_exception_if_times_out_recovering() {
        AgentRecoveryRunnable runner = new AgentRecoveryRunnable(contextInstanceRestService, 1, AGENT, moduleService);

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
    }
}