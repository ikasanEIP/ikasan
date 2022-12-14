package org.ikasan.ootb.scheduler.agent.module.component.endpoint;

import org.ikasan.component.endpoint.quartz.consumer.CorrelatedScheduledConsumerConfiguration;
import org.ikasan.component.endpoint.quartz.consumer.CorrelatingScheduledConsumer;
import org.ikasan.component.endpoint.quartz.recovery.service.ScheduledJobRecoveryService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CorrelatingScheduledConsumerTest {
    /**
     * Mockery for mocking concrete classes
     */
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
        }
    };

    /** Mock scheduler */
    private final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");

    /** Mock job detail */
    private final JobDetail mockJobDetail = mockery.mock(JobDetail.class, "mockJobDetail");

    /** Mock consumerConfiguration */
    private final CorrelatedScheduledConsumerConfiguration consumerConfiguration =
        mockery.mock(CorrelatedScheduledConsumerConfiguration.class, "mockScheduledConsumerConfigurationEnhanced");

    private final ScheduledJobRecoveryService scheduledJobRecoveryService = mockery.mock(ScheduledJobRecoveryService.class, "mockScheduledJobRecoveryService");

    /**
     * Test failed constructor for scheduled consumer due to null scheduler.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_failure_NullScheduler()
    {
        new CorrelatingScheduledConsumer(null);
    }

    /**
     * Test failed constructor for scheduled consumer due to null scheduledJobRecoveryService.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_failure_NullScheduledJobRecoveryService()
    {
        new CorrelatingScheduledConsumer(scheduler, null);
    }

    /**
     * Test successful consumer start.
     * @throws SchedulerException problems with scheduleJob
     */
    @Test
    public void test_start_no_persisted_recovery() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("flowName", "moduleName");
        final List<String> expressions = new ArrayList<>();
        expressions.add("0/1 * * * * ?");
        expressions.add("0/2 * * * * ?");
        expressions.add("0/3 * * * * ?");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // get configuration job name
                exactly(2).of(consumerConfiguration).getJobName();
                will(returnValue("jobName"));

                // get configuration job group name
                exactly(2).of(consumerConfiguration).getJobGroupName();
                will(returnValue("jobGroupName"));

                // access configuration for details
                exactly(1).of(consumerConfiguration).getConsolidatedCronExpressions();
                will(returnValue(expressions));

                // check if persistent recovery
                exactly(6).of(consumerConfiguration).isPersistentRecovery();
                will(returnValue(false));

                // create new trigger with description from configuration
                exactly(6).of(consumerConfiguration).getDescription();
                will(returnValue("configuration description"));

                // access configuration for details
                exactly(6).of(consumerConfiguration).isIgnoreMisfire();
                will(returnValue(true));

                // access configuration for details
                exactly(18).of(consumerConfiguration).getTimezone();
                will(returnValue("UTC"));

                // get configuration scheduler correlation ID's for the Root (top level) Job Plans
                exactly(1).of(consumerConfiguration).getCorrelatingIdentifiers();
                will(returnValue(Arrays.asList("cor1", "cor2")));

                // get configuration scheduler pass-through properties
                exactly(1).of(consumerConfiguration).getPassthroughProperties();
                will(returnValue(null));

                // schedule the job triggers
                exactly(1).of(scheduler).scheduleJob(with(any(JobDetail.class)), with(any(Set.class)), with(any(Boolean.class)));
            }
        });

        CorrelatingScheduledConsumer CorrelatingScheduledConsumer = new StubbedCorrelatingScheduledConsumer(scheduler, scheduledJobRecoveryService);
        CorrelatingScheduledConsumer.setConfiguration(consumerConfiguration);
        CorrelatingScheduledConsumer.setJobDetail(mockJobDetail);
        CorrelatingScheduledConsumer.start();
        Assert.assertEquals("Expected number of triggers not met", ((StubbedCorrelatingScheduledConsumer) CorrelatingScheduledConsumer).getTriggers().size(), 6);
        Assert.assertTrue("Expected replacement of triggers", ((StubbedCorrelatingScheduledConsumer) CorrelatingScheduledConsumer).isReplace());

        mockery.assertIsSatisfied();
    }


    /**
     * Extended ScheduledRecoveryManagerJobFactory for testing with replacement mocks.
     * @author Ikasan Development Team
     *
     */
    private class StubbedCorrelatingScheduledConsumer extends CorrelatingScheduledConsumer
    {
        Set<Trigger> triggers;
        boolean replace;

        protected StubbedCorrelatingScheduledConsumer(Scheduler scheduler)
        {
            super(scheduler);
        }

        protected StubbedCorrelatingScheduledConsumer(Scheduler scheduler, ScheduledJobRecoveryService scheduledJobRecoveryService)
        {
            super(scheduler, scheduledJobRecoveryService);
        }

        @Override
        public void scheduleJobTriggers(JobDetail jobDetail, Set triggers, boolean replace) throws SchedulerException
        {
            this.triggers = triggers;
            this.replace = replace;
            super.scheduleJobTriggers(jobDetail, triggers, replace);
        }

        public Set<Trigger> getTriggers()
        {
            return triggers;
        }

        public boolean isReplace()
        {
            return replace;
        }
    }

}
