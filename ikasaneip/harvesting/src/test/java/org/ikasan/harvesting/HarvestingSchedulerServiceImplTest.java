package org.ikasan.harvesting;

import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.harvest.HarvestingJob;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.jupiter.api.Test;
import org.quartz.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

class HarvestingSchedulerServiceImplTest
{

    private Mockery mockery = new Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    HousekeepService housekeepService = mockery.mock(HousekeepService.class);
    Scheduler scheduler = mockery.mock(Scheduler.class);
    ScheduledJobFactory scheduledJobFactory =  mockery.mock(ScheduledJobFactory.class);
    JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class);
    HarvestingJob harvestingJob = mockery.mock(HarvestingJobImpl.class);
    JobDetail jobDetail = mockery.mock(JobDetail.class);
    JobKey jobKey = new JobKey("test");


    @Test
    void constructor()
    {
        List<HarvestingJob> harvestingJobs = Arrays.asList(harvestingJob);

        mockery.checking(new Expectations() {{


            atLeast(2).of(harvestingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(harvestingJob,HarvestingJob.class,"test","harvest");
            will(returnValue(jobDetail));

            oneOf(jobDetail).getKey();
            will(returnValue(jobKey));


        }});

        HarvestingSchedulerServiceImpl uut = new HarvestingSchedulerServiceImpl(scheduler,scheduledJobFactory,harvestingJobs);

        mockery.assertIsSatisfied();
    }

    @Test
    void registerJobs() throws SchedulerException
    {
        List<HarvestingJob> harvestingJobs = Arrays.asList(harvestingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(harvestingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(harvestingJob,HarvestingJob.class,"test","harvest");
            will(returnValue(jobDetail));

            atLeast(2).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // registerJobs expectations
            oneOf(harvestingJob).isInitialised();
            will(returnValue(true));

            oneOf(harvestingJob).isEnabled();
            will(returnValue(true));

            oneOf(harvestingJob).init();

            oneOf(scheduler).checkExists(jobKey);
            will(returnValue(false));

            atLeast(2).of(harvestingJob).getCronExpression();
            will(returnValue(HarvestingJob.DEFAULT_CRON_EXPRESSION));

            oneOf(scheduler).scheduleJob(with(any(JobDetail.class)),with(any(Trigger.class)));
            will(returnValue(new Date()));

        }});

        HarvestingSchedulerServiceImpl uut = new HarvestingSchedulerServiceImpl(scheduler,scheduledJobFactory,harvestingJobs);
        // test
        uut.registerJobs();

        mockery.assertIsSatisfied();
    }

    @Test
    void registerJobsWhenNoJobs() throws SchedulerException
    {
        List<HarvestingJob> harvestingJobs = Arrays.asList();

        mockery.checking(new Expectations() {{

            // Constructor expectations

        }});

        HarvestingSchedulerServiceImpl uut = new HarvestingSchedulerServiceImpl(scheduler,scheduledJobFactory,harvestingJobs);
        // test
        uut.registerJobs();

        mockery.assertIsSatisfied();
    }

    @Test
    void registerJobsWhenHousekeepJobIsDisabled() throws SchedulerException
    {
        List<HarvestingJob> harvestingJobs = Arrays.asList(harvestingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(harvestingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(harvestingJob,HarvestingJob.class,"test","harvest");
            will(returnValue(jobDetail));

            atLeast(2).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // registerJobs expectations
            oneOf(harvestingJob).isInitialised();
            will(returnValue(true));

            oneOf(harvestingJob).isEnabled();
            will(returnValue(false));

            oneOf(harvestingJob).init();

        }});

        HarvestingSchedulerServiceImpl uut = new HarvestingSchedulerServiceImpl(scheduler,scheduledJobFactory,harvestingJobs);
        // test
        uut.registerJobs();

        mockery.assertIsSatisfied();
    }

    @Test
    void registerJobsWhenShcedulerThrowsRuntimeException() throws SchedulerException
    {
        List<HarvestingJob> harvestingJobs = Arrays.asList(harvestingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(harvestingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(harvestingJob,HarvestingJob.class,"test","harvest");
            will(returnValue(jobDetail));

            atLeast(2).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // registerJobs expectations
            oneOf(harvestingJob).isInitialised();
            will(returnValue(true));

            oneOf(harvestingJob).isEnabled();
            will(returnValue(true));

            oneOf(harvestingJob).init();

            oneOf(scheduler).checkExists(jobKey);
            will(returnValue(false));

            oneOf(harvestingJob).getCronExpression();
            will(returnValue(HarvestingJob.DEFAULT_CRON_EXPRESSION));

            oneOf(scheduler).scheduleJob(with(any(JobDetail.class)),with(any(Trigger.class)));
            will(throwException(new RuntimeException("Scheduler is shutdown")));

        }});

        HarvestingSchedulerServiceImpl uut = new HarvestingSchedulerServiceImpl(scheduler,scheduledJobFactory,harvestingJobs);
        // test
        try
        {
            uut.registerJobs();
        }catch (RuntimeException e){
            mockery.assertIsSatisfied();
            return;
        }

        fail("Not expected to be reached!");

    }

    @Test
    void remove() throws SchedulerException
    {
        List<HarvestingJob> harvestingJobs = Arrays.asList(harvestingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(harvestingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(harvestingJob,HarvestingJob.class,"test","harvest");
            will(returnValue(jobDetail));

            atLeast(2).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // registerJobs expectations
            oneOf(scheduler).checkExists(jobKey);
            will(returnValue(true));

            oneOf(scheduler).deleteJob(jobKey);
            will(returnValue(true));

        }});

        HarvestingSchedulerServiceImpl uut = new HarvestingSchedulerServiceImpl(scheduler,scheduledJobFactory,harvestingJobs);
        // test
        uut.removeJob("test");

        mockery.assertIsSatisfied();
    }

    @Test
    void removeWhenJobDoesntExist() throws SchedulerException
    {
        List<HarvestingJob> harvestingJobs = Arrays.asList(harvestingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(harvestingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(harvestingJob,HarvestingJob.class,"test","harvest");
            will(returnValue(jobDetail));

            atLeast(2).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // registerJobs expectations
            oneOf(scheduler).checkExists(jobKey);
            will(returnValue(false));

        }});

        HarvestingSchedulerServiceImpl uut = new HarvestingSchedulerServiceImpl(scheduler,scheduledJobFactory,harvestingJobs);
        // test
        uut.removeJob("test");

        mockery.assertIsSatisfied();
    }

    @Test
    void add() throws SchedulerException
    {
        List<HarvestingJob> harvestingJobs = Arrays.asList(harvestingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(harvestingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(harvestingJob,HarvestingJob.class,"test","harvest");
            will(returnValue(jobDetail));

            atLeast(2).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // registerJobs expectations
            atLeast(2).of(scheduler).checkExists(jobKey);
            will(returnValue(false));

            atLeast(2).of(harvestingJob).getCronExpression();
            will(returnValue(HarvestingJob.DEFAULT_CRON_EXPRESSION));

            oneOf(scheduler).scheduleJob(with(any(JobDetail.class)),with(any(Trigger.class)));
            will(returnValue(new Date()));

        }});

        HarvestingSchedulerServiceImpl uut = new HarvestingSchedulerServiceImpl(scheduler,scheduledJobFactory,harvestingJobs);
        // test
        uut.addJob("test");

        mockery.assertIsSatisfied();
    }

    @Test
    void startScheduler() throws SchedulerException
    {
        List<HarvestingJob> harvestingJobs = Arrays.asList(harvestingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(harvestingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(harvestingJob,HarvestingJob.class,"test","harvest");
            will(returnValue(jobDetail));

            atLeast(2).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // startScheduler expectations
            oneOf(harvestingJob).isInitialised();
            will(returnValue(true));

            oneOf(harvestingJob).isEnabled();
            will(returnValue(false));

            oneOf(harvestingJob).init();

            oneOf(scheduler).start();
        }});

        HarvestingSchedulerServiceImpl uut = new HarvestingSchedulerServiceImpl(scheduler,scheduledJobFactory,harvestingJobs);
        // test
        uut.startScheduler();

        mockery.assertIsSatisfied();
    }

    @Test
    void shutdownScheduler() throws SchedulerException
    {
        List<HarvestingJob> harvestingJobs = Arrays.asList(harvestingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(harvestingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(harvestingJob,HarvestingJob.class,"test","harvest");
            will(returnValue(jobDetail));

            atLeast(1).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // shutdownScheduler expectations

            oneOf(scheduler).shutdown();
        }});

        HarvestingSchedulerServiceImpl uut = new HarvestingSchedulerServiceImpl(scheduler,scheduledJobFactory,harvestingJobs);
        // test
        uut.shutdownScheduler();

        mockery.assertIsSatisfied();
    }
}
