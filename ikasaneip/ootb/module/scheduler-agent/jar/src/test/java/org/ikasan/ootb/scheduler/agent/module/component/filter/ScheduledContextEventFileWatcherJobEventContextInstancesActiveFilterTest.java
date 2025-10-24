package org.ikasan.ootb.scheduler.agent.module.component.filter;

import org.apache.commons.lang3.RandomStringUtils;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.job.orchestration.model.context.ContextualisedScheduledProcessEventImpl;
import org.ikasan.ootb.scheduler.agent.module.component.router.AgentRecoveryNotCompleteException;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledContextEventFileWatcherJobEventContextInstancesActiveFilterTest {

    @Mock
    private DryRunModeService dryRunModeService;
    /** Mock jobExecutionContext **/

    private static final String contextInstanceId = RandomStringUtils.randomAlphabetic(12);

    @Before
    public void setup() {
        ContextInstance instance = new ContextInstanceImpl();
        instance.setName("ContextInstanceName1");
        instance.setId(contextInstanceId);
        ContextInstanceCache.instance().put(instance.getId(), instance);
        ContextInstanceCache.instance().setInitialisationComplete(true);
    }

    @Test(expected = AgentRecoveryNotCompleteException.class)
    public void should_throw_exception_if_context_not_in_cache() {
        ContextInstanceCache.instance().setInitialisationComplete(false);

        ScheduledContextEventContextInstancesActiveFilter filter = new ScheduledContextEventContextInstancesActiveFilter(dryRunModeService);
        filter.filter(new ContextualisedScheduledProcessEventImpl());
    }

    @Test
    public void should_pass_though_event_if_correlationId_in_cache() {
        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);
        ContextualisedScheduledProcessEvent event = new ContextualisedScheduledProcessEventImpl();
        event.setContextName("ContextInstanceName1");

        ScheduledContextEventContextInstancesActiveFilter filter = new ScheduledContextEventContextInstancesActiveFilter(dryRunModeService);
        Assert.assertNotNull(filter.filter(event));
    }

    @Test
    public void should_filter_event_if_correlationId_null() {
        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);
        ContextualisedScheduledProcessEvent event = new ContextualisedScheduledProcessEventImpl();
        event.setContextName("BAD_CONTEXT_NAME");

        ScheduledContextEventContextInstancesActiveFilter filter = new ScheduledContextEventContextInstancesActiveFilter(dryRunModeService);
        Assert.assertNull(filter.filter(event));
    }

    @Test
    public void should_filter_event_if_correlationId_empty_string() {
        when(this.dryRunModeService.getDryRunMode()).thenReturn(false);
        ContextualisedScheduledProcessEvent event = new ContextualisedScheduledProcessEventImpl();
        event.setContextName("BAD_CONTEXT_NAME");

        ScheduledContextEventContextInstancesActiveFilter filter = new ScheduledContextEventContextInstancesActiveFilter(dryRunModeService);
        Assert.assertNull(filter.filter(event));
    }

    @Test
    public void should_passthrough_event_if_dry_run_mode() {
        when(this.dryRunModeService.getDryRunMode()).thenReturn(true);
        ContextualisedScheduledProcessEvent event = new ContextualisedScheduledProcessEventImpl();

        ScheduledContextEventContextInstancesActiveFilter filter = new ScheduledContextEventContextInstancesActiveFilter(dryRunModeService);
        Assert.assertNotNull(filter.filter(event));
    }
}