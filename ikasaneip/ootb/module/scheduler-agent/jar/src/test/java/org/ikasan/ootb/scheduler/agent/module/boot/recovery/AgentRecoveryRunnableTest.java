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
        AgentRecoveryRunnable runner = new AgentRecoveryRunnable(contextInstanceRestService, 1, "scheduler-agent", moduleService);

        String contextName = RandomStringUtils.randomAlphabetic(12);
        ContextInstance instance1 = new ContextInstanceImpl();
        String contextName1 = contextName + RandomStringUtils.randomNumeric(10);
        instance1.setName(contextName1);

        ContextInstance instance2 = new ContextInstanceImpl();
        String contextName2 = contextName + RandomStringUtils.randomNumeric(10);
        instance2.setName(contextName2);

        Map<String, String> contextFlowMap = Map.of(
          "jobName1", contextName1, "jobNName2", contextName1, "jobName3", contextName2, "jobNName4", contextName2
        );

        when(moduleService.getModule("scheduler-agent")).thenReturn(module);
        when(module.getConfiguration()).thenReturn(configureModule);
        when(configureModule.getFlowContextMap()).thenReturn(contextFlowMap);

        Map<String, ContextInstance> map1 = Map.of(contextName1, instance1);
        Map<String, ContextInstance> map2 = Map.of(contextName2, instance2);

        when(contextInstanceRestService.getByContextName(contextName1)).thenReturn(map1);
        when(contextInstanceRestService.getByContextName(contextName2)).thenReturn(map2);

        runner.run();

        Assert.assertNotNull(ContextInstanceCache.instance().getByContextName(contextName1));
        Assert.assertNotNull(ContextInstanceCache.instance().getByContextName(contextName2));
    }

    @Test(expected = EndpointException.class)
    @Ignore // takes at least 1 minute to run
    public void should_throw_exception_if_times_out_recovering() {
        AgentRecoveryRunnable runner = new AgentRecoveryRunnable(contextInstanceRestService, 1, "scheduler-agent", moduleService);

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

        when(contextInstanceRestService.getByContextName(contextName1)).thenThrow(new EndpointException("excepted exception"));

        runner.run();
    }
}