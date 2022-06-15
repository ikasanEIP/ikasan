package org.ikasan.ootb.scheduler.agent.module.boot.recovery;

import static org.mockito.Mockito.when;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.dashboard.ContextInstanceRestService;
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

    @Test
    public void should_successfully_recover_instances() {
        AgentRecoveryRunnable runner = new AgentRecoveryRunnable(contextInstanceRestService, 1);

        String contextName = RandomStringUtils.randomAlphabetic(12);
        ContextInstance instance1 = new ContextInstanceImpl();
        String contextName1 = contextName + RandomStringUtils.randomNumeric(10);
        instance1.setName(contextName1);

        ContextInstance instance2 = new ContextInstanceImpl();
        String contextName2 = contextName + RandomStringUtils.randomNumeric(10);
        instance2.setName(contextName2);

        Map<String, ContextInstance> map = Map.of(contextName1, instance1, contextName2, instance2);

        when(contextInstanceRestService.getAll()).thenReturn(map);

        runner.run();

        Assert.assertNotNull(ContextInstanceCache.instance().getByContextName(contextName1));
        Assert.assertNotNull(ContextInstanceCache.instance().getByContextName(contextName2));
    }

    @Test(expected = EndpointException.class)
    @Ignore // takes at least 1 minute to run
    public void should_throw_exception_if_times_out_recovering() {
        AgentRecoveryRunnable runner = new AgentRecoveryRunnable(contextInstanceRestService, 1);

        when(contextInstanceRestService.getAll()).thenThrow(new EndpointException("excepted exception"));

        runner.run();
    }
}