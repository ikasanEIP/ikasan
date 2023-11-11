package org.ikasan.component.endpoint.quartz.consumer;

import org.ikasan.component.endpoint.quartz.recovery.service.ScheduledJobRecoveryService;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.jupiter.api.Test;
import org.quartz.*;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CorrelatingScheduledConsumerTest {
    /**
     * Mockery for mocking concrete classes
     */
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
        }
    };

    private final Scheduler mockScheduler = mockery.mock(Scheduler.class, "mockScheduler");
    private final JobDetail mockJobDetail = mockery.mock(JobDetail.class, "mockJobDetail");
    private final CorrelatedScheduledConsumerConfiguration mockConsumerConfiguration =
        mockery.mock(CorrelatedScheduledConsumerConfiguration.class, "mockScheduledConsumerConfigurationEnhanced");
    private final ScheduledJobRecoveryService mockScheduledJobRecoveryService =
        mockery.mock(ScheduledJobRecoveryService.class, "mockScheduledJobRecoveryService");
    private final ManagedResourceRecoveryManager mockManagedResourceRecoveryManager =
        mockery.mock(ManagedResourceRecoveryManager.class);
    private final Trigger mockTrigger = mockery.mock(Trigger.class, "mockTrigger");
    private static final String PASSTHROUGH_KEY = "passthroughKey";
    private static final Map<String, String> PASS_THROUGH_PROPERTIES = Map.of("passthroughKey", "propValue");

    /**
     * Test failed constructor for scheduled consumer due to null scheduler.
     */
    @Test
    void test_constructor_failure_NullScheduler()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new CorrelatingScheduledConsumer(null);
        });
    }

    /**
     * Test failed constructor for scheduled consumer due to null scheduledJobRecoveryService.
     */
    @Test
    void test_constructor_failure_NullScheduledJobRecoveryService()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new CorrelatingScheduledConsumer(mockScheduler, null);
        });
    }

    // For some use cases (e.g. scheduler) we don't initially have the correlating identifiers
    @Test
    void test_start_no_persisted_recovery_without_correlations_or_passthrough() throws SchedulerException {
        createStandardExpectations();
        mockery.checking(new Expectations()
        {
            {
                // get configuration scheduler correlation ID's for the Root (top level) Job Plans
                exactly(1).of(mockConsumerConfiguration).getCorrelatingIdentifiers();
                will(returnValue(new ArrayList<>()));

                exactly(4).of(mockConsumerConfiguration).getPassthroughProperties();
                will(returnValue(new HashMap<String,String>()));

                exactly(3).of(mockConsumerConfiguration).getDescription();
                will(returnValue("configuration description"));

                exactly(3).of(mockConsumerConfiguration).isPersistentRecovery();
                will(returnValue(false));

                exactly(3).of(mockConsumerConfiguration).isIgnoreMisfire();
                will(returnValue(true));

                exactly(9).of(mockConsumerConfiguration).getTimezone();
                will(returnValue("UTC"));
            }
        });

        CorrelatingScheduledConsumer correlatingScheduledConsumer = new StubbedCorrelatingScheduledConsumer(mockScheduler, mockScheduledJobRecoveryService);
        correlatingScheduledConsumer.setConfiguration(mockConsumerConfiguration);
        correlatingScheduledConsumer.setJobDetail(mockJobDetail);
        correlatingScheduledConsumer.start();

        StubbedCorrelatingScheduledConsumer stubbedCorrelatingScheduledConsumer = (StubbedCorrelatingScheduledConsumer) correlatingScheduledConsumer;
        // No correlating identifiers means no triggers i.e. the component is passive until correlating identifiers are added
        assertEquals(3, stubbedCorrelatingScheduledConsumer.getTriggers().size(), "Expected number of triggers not met");
        assertTrue(stubbedCorrelatingScheduledConsumer.isReplace(), "Expected replacement of triggers");
        mockery.assertIsSatisfied();
    }

    @Test
    void test_start_no_persisted_recovery_with_correlations() throws SchedulerException {
        createStandardExpectations();
        mockery.checking(new Expectations()
        {
            {
                // get configuration scheduler correlation ID's for the Root (top level) Job Plans
                exactly(1).of(mockConsumerConfiguration).getCorrelatingIdentifiers();
                will(returnValue(Arrays.asList("cor1", "cor2")));

                exactly(7).of(mockConsumerConfiguration).getPassthroughProperties();
                will(returnValue(PASS_THROUGH_PROPERTIES));

                exactly(6).of(mockConsumerConfiguration).isPersistentRecovery();
                will(returnValue(false));

                exactly(6).of(mockConsumerConfiguration).getDescription();
                will(returnValue("configuration description"));

                exactly(6).of(mockConsumerConfiguration).isIgnoreMisfire();
                will(returnValue(true));

                exactly(18).of(mockConsumerConfiguration).getTimezone();
                will(returnValue("UTC"));
            }
        });

        CorrelatingScheduledConsumer correlatingScheduledConsumer = new StubbedCorrelatingScheduledConsumer(mockScheduler, mockScheduledJobRecoveryService);
        correlatingScheduledConsumer.setConfiguration(mockConsumerConfiguration);
        correlatingScheduledConsumer.setJobDetail(mockJobDetail);
        correlatingScheduledConsumer.start();

        StubbedCorrelatingScheduledConsumer stubbedCorrelatingScheduledConsumer = (StubbedCorrelatingScheduledConsumer) correlatingScheduledConsumer;
        assertEquals(6, stubbedCorrelatingScheduledConsumer.getTriggers().size(), "Expected number of triggers not met");
        assertTrue(stubbedCorrelatingScheduledConsumer.isReplace(), "Expected replacement of triggers");

        List<Trigger> sortedTriggers = (List<Trigger>)stubbedCorrelatingScheduledConsumer.getTriggers().stream()
            .sorted()
            .collect(Collectors.toList());
        assertEquals(
            """
            [\
            Trigger 'jobGroupName.jobName_cor1_-165197414':  triggerClass: 'org.quartz.impl.triggers.CronTriggerImpl calendar: 'null' misfireInstruction: 2 nextFireTime: null, \
            Trigger 'jobGroupName.jobName_cor1_-1962148773':  triggerClass: 'org.quartz.impl.triggers.CronTriggerImpl calendar: 'null' misfireInstruction: 2 nextFireTime: null, \
            Trigger 'jobGroupName.jobName_cor1_1631753945':  triggerClass: 'org.quartz.impl.triggers.CronTriggerImpl calendar: 'null' misfireInstruction: 2 nextFireTime: null, \
            Trigger 'jobGroupName.jobName_cor2_-165197414':  triggerClass: 'org.quartz.impl.triggers.CronTriggerImpl calendar: 'null' misfireInstruction: 2 nextFireTime: null, \
            Trigger 'jobGroupName.jobName_cor2_-1962148773':  triggerClass: 'org.quartz.impl.triggers.CronTriggerImpl calendar: 'null' misfireInstruction: 2 nextFireTime: null, \
            Trigger 'jobGroupName.jobName_cor2_1631753945':  triggerClass: 'org.quartz.impl.triggers.CronTriggerImpl calendar: 'null' misfireInstruction: 2 nextFireTime: null]\
            """,
                sortedTriggers.toString());
        // Sizes of the map data
        assertEquals("[3, 3, 3, 3, 3, 3]",
            sortedTriggers.stream().map(t-> t.getJobDataMap().size()).collect(Collectors.toList()).toString()
        );
        assertEquals("[cor1, cor1, cor1, cor2, cor2, cor2]",
            sortedTriggers.stream().map(t->t.getJobDataMap().get(CorrelatingScheduledConsumer.CORRELATION_ID)).collect(Collectors.toList()).toString()
        );
        assertEquals("[0/2 * * * * ?, 0/3 * * * * ?, 0/1 * * * * ?, 0/2 * * * * ?, 0/3 * * * * ?, 0/1 * * * * ?]",
            sortedTriggers.stream().map(t->t.getJobDataMap().get(CorrelatingScheduledConsumer.CRON_EXPRESSION)).collect(Collectors.toList()).toString()
        );
        assertEquals("[propValue, propValue, propValue, propValue, propValue, propValue]",
            sortedTriggers.stream().map(t->t.getJobDataMap().get(PASSTHROUGH_KEY)).collect(Collectors.toList()).toString()
        );

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful consumer start.
     */
    @Test
    void test_start_with_persisted_recovery_outside_tolerance() throws SchedulerException
    {
        createStandardExpectations();
        mockery.checking(new Expectations()
        {
            {
                // get configuration scheduler correlation ID's for the Root (top level) Job Plans
                exactly(1).of(mockConsumerConfiguration).getCorrelatingIdentifiers();
                will(returnValue(Arrays.asList("cor1")));

                exactly(3).of(mockConsumerConfiguration).isPersistentRecovery();
                will(returnValue(true));

                exactly(1).of(mockScheduledJobRecoveryService).isRecoveryRequired("jobName_cor1_1631753945", "jobGroupName", 0L);
                will(returnValue(false));
                exactly(1).of(mockScheduledJobRecoveryService).isRecoveryRequired("jobName_cor1_-165197414", "jobGroupName", 0L);
                will(returnValue(false));
                exactly(1).of(mockScheduledJobRecoveryService).isRecoveryRequired("jobName_cor1_-1962148773", "jobGroupName", 0L);
                will(returnValue(false));

                exactly(3).of(mockConsumerConfiguration).getRecoveryTolerance();
                will(returnValue(0L));

                exactly(3).of(mockConsumerConfiguration).getDescription();
                will(returnValue("configuration description"));

                exactly(3).of(mockConsumerConfiguration).isIgnoreMisfire();
                will(returnValue(true));

                exactly(9).of(mockConsumerConfiguration).getTimezone();
                will(returnValue("UTC"));

                exactly(1).of(mockConsumerConfiguration).getPassthroughProperties();
                will(returnValue(null));
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedCorrelatingScheduledConsumer(mockScheduler, mockScheduledJobRecoveryService);
        scheduledConsumer.setConfiguration(mockConsumerConfiguration);
        scheduledConsumer.setJobDetail(mockJobDetail);
        scheduledConsumer.setManagedResourceRecoveryManager(mockManagedResourceRecoveryManager);
        scheduledConsumer.start();
        assertEquals(3, ((StubbedCorrelatingScheduledConsumer)scheduledConsumer).getTriggers().size(), "Expected number of triggers not met - should have 2 business and 1 recovery trigger ");
        assertTrue(((StubbedCorrelatingScheduledConsumer)scheduledConsumer).isReplace(), "Expected replacement of triggers");

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful consumer start.
     */
    @Test
    void test_start_with_persisted_recovery_inside_tolerance() throws SchedulerException
    {
        createStandardExpectations();
        mockery.checking(new Expectations()
        {
            {
                // get configuration scheduler correlation ID's for the Root (top level) Job Plans
                exactly(1).of(mockConsumerConfiguration).getCorrelatingIdentifiers();
                will(returnValue(Arrays.asList("cor1")));

                // check if persistent recovery
                exactly(3).of(mockConsumerConfiguration).isPersistentRecovery();
                will(returnValue(true));

                exactly(1).of(mockScheduledJobRecoveryService).isRecoveryRequired("jobName_cor1_1631753945", "jobGroupName", 1000L);
                will(returnValue(false));
                exactly(1).of(mockScheduledJobRecoveryService).isRecoveryRequired("jobName_cor1_-165197414", "jobGroupName", 1000L);
                will(returnValue(true));
                exactly(1).of(mockScheduledJobRecoveryService).isRecoveryRequired("jobName_cor1_-1962148773", "jobGroupName", 1000L);
                will(returnValue(false));


                exactly(3).of(mockConsumerConfiguration).getRecoveryTolerance();
                will(returnValue(1000L));

                exactly(4).of(mockConsumerConfiguration).getDescription();
                will(returnValue("configuration description"));

                exactly(2).of(mockConsumerConfiguration).isIgnoreMisfire();
                will(returnValue(true));

                exactly(6).of(mockConsumerConfiguration).getTimezone();
                will(returnValue("UTC"));

                exactly(1).of(mockConsumerConfiguration).getPassthroughProperties();
                will(returnValue(null));
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedCorrelatingScheduledConsumer(mockScheduler, mockScheduledJobRecoveryService);
        scheduledConsumer.setConfiguration(mockConsumerConfiguration);
        scheduledConsumer.setJobDetail(mockJobDetail);
        scheduledConsumer.setManagedResourceRecoveryManager(mockManagedResourceRecoveryManager);
        scheduledConsumer.start();
        assertEquals(3, ((StubbedCorrelatingScheduledConsumer)scheduledConsumer).getTriggers().size(), "Expected number of triggers not met - should have 2 business and 1 recovery trigger ");
        assertTrue(((StubbedCorrelatingScheduledConsumer)scheduledConsumer).isReplace(), "Expected replacement of triggers");

        mockery.assertIsSatisfied();
    }

    /**
     * Test failed consumer start.
     */
    @Test
    void test_start_failure_due_to_schedulerException() throws SchedulerException
    {
        assertThrows(RuntimeException.class, () -> {
            final JobKey jobKey = new JobKey("flowName", "moduleName");

            // expectations
            mockery.checking(new Expectations() {
                {
                    exactly(1).of(mockJobDetail).getKey();
                    will(returnValue(jobKey));

                    exactly(1).of(mockConsumerConfiguration).getCronExpression();
                    will(returnValue("* * * * ? ?"));

                    // schedule the job
                    exactly(1).of(mockScheduler).scheduleJob(mockJobDetail, mockTrigger);
                    will(throwException(new SchedulerException()));
                }
            });

            ScheduledConsumer scheduledConsumer = new StubbedCorrelatingScheduledConsumer(mockScheduler);
            scheduledConsumer.setConfiguration(mockConsumerConfiguration);
            scheduledConsumer.start();
            mockery.assertIsSatisfied();
        });
    }

    /**
     * Test failed consumer start.
     */
    @Test
    void test_start_failure_due_to_parserException() throws SchedulerException
    {
        assertThrows(RuntimeException.class, () -> {
            final JobKey jobKey = new JobKey("flowName", "moduleName");

            // expectations
            mockery.checking(new Expectations()
            {
                {
                    exactly(1).of(mockJobDetail).getKey();
                    will(returnValue(jobKey));

                    exactly(1).of(mockConsumerConfiguration).getCronExpression();
                    will(returnValue("* * * * ? ?"));

                    // schedule the job
                    exactly(1).of(mockScheduler).scheduleJob(mockJobDetail, mockTrigger);
                    will(throwException(new ParseException("test", 0)));
                }
            });

            ScheduledConsumer scheduledConsumer = new StubbedCorrelatingScheduledConsumer(mockScheduler);
            scheduledConsumer.setConfiguration(mockConsumerConfiguration);
            scheduledConsumer.start();
            mockery.assertIsSatisfied();
        });
    }

    private void createStandardExpectations() throws SchedulerException {
        final JobKey jobKey = new JobKey("flowName", "moduleName");
        final List<String> expressions = new ArrayList<>();
        expressions.add("0/1 * * * * ?");
        expressions.add("0/2 * * * * ?");
        expressions.add("0/3 * * * * ?");

        mockery.checking( new Expectations()
        {
            {
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                exactly(2).of(mockConsumerConfiguration).getJobName();
                will(returnValue("jobName"));

                exactly(2).of(mockConsumerConfiguration).getJobGroupName();
                will(returnValue("jobGroupName"));

                exactly(1).of(mockConsumerConfiguration).getConsolidatedCronExpressions();
                will(returnValue(expressions));

                // schedule the job triggers
                exactly(1).of(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Set.class)), with(any(Boolean.class)));
            }
        });
    }

    /**
     * Extended ScheduledRecoveryManagerJobFactory for testing with replacement mocks.
     * @author Ikasan Development Team
     *
     */
    private class StubbedCorrelatingScheduledConsumer<T> extends CorrelatingScheduledConsumer<T>
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
        protected void scheduleJobTriggers(JobDetail jobDetail, Set<Trigger> triggers, boolean replace) throws SchedulerException
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
