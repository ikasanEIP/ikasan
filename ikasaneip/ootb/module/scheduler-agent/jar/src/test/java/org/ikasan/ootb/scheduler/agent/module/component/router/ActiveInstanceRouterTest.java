package org.ikasan.ootb.scheduler.agent.module.component.router;

import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduler.agent.module.boot.recovery.AgentInstanceRecoveryManager;
import org.ikasan.ootb.scheduler.agent.module.model.EnrichedContextualisedScheduledProcessEvent;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActiveInstanceRouterTest {

    @Test
    public void test_instance_active_route() {
        ContextInstanceCache.instance().setInitialisationComplete(true);
        ContextInstanceCache.instance().put("contextInstanceId", new ContextInstanceImpl());

        EnrichedContextualisedScheduledProcessEvent event = new EnrichedContextualisedScheduledProcessEvent();
        event.setContextInstanceId("contextInstanceId");
        ActiveInstanceRouter activeInstanceRouter = new ActiveInstanceRouter();
        String result = activeInstanceRouter.route(event);

        Assert.assertEquals(ActiveInstanceRouter.ACTIVE_INSTANCE_ID, result);
    }

    @Test
    public void test_instance_inactive_route() {
        ContextInstanceCache.instance().setInitialisationComplete(true);
        ContextInstanceCache.instance().removeAll();

        EnrichedContextualisedScheduledProcessEvent event = new EnrichedContextualisedScheduledProcessEvent();
        event.setContextInstanceId("contextInstanceId");
        ActiveInstanceRouter activeInstanceRouter = new ActiveInstanceRouter();
        String result = activeInstanceRouter.route(event);

        Assert.assertEquals(ActiveInstanceRouter.INACTIVE_INSTANCE_ID, result);
    }

    @Test(expected = AgentRecoveryNotCompleteException.class)
    public void test_agent_not_fully_recovered_exception() {
        ContextInstanceCache.instance().setInitialisationComplete(false);
        ContextInstanceCache.instance().removeAll();

        EnrichedContextualisedScheduledProcessEvent event = new EnrichedContextualisedScheduledProcessEvent();
        event.setContextInstanceId("contextInstanceId");
        ActiveInstanceRouter activeInstanceRouter = new ActiveInstanceRouter();
        String result = activeInstanceRouter.route(event);
    }
}
