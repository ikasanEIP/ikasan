package org.ikasan.housekeeping;

import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HousekeepingSchedulerServiceImplTest
{

    private Mockery mockery = new Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    HousekeepService housekeepService = mockery.mock(HousekeepService.class);
    Scheduler scheduler = mockery.mock(Scheduler.class);
    ScheduledJobFactory scheduledJobFactory =  mockery.mock(ScheduledJobFactory.class);
    JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class);
    HousekeepingJob housekeepingJob = mockery.mock(HousekeepingJobImpl.class);
    JobDetail jobDetail = mockery.mock(JobDetail.class);
    JobKey jobKey = new JobKey("test");


    @Test
    public void constructor()
    {
        List<HousekeepingJob> houseKeepingJobs = Arrays.asList(housekeepingJob);

        mockery.checking(new Expectations() {{


            atLeast(2).of(housekeepingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(housekeepingJob,HousekeepingJobImpl.class,"test","housekeeping");
            will(returnValue(jobDetail));

            oneOf(jobDetail).getKey();
            will(returnValue(jobKey));


        }});

        HousekeepingSchedulerServiceImpl uut = new HousekeepingSchedulerServiceImpl(scheduler,scheduledJobFactory,houseKeepingJobs);

        mockery.assertIsSatisfied();
    }

    @Test
    public void registerJobs() throws SchedulerException
    {
        List<HousekeepingJob> houseKeepingJobs = Arrays.asList(housekeepingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(housekeepingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(housekeepingJob,HousekeepingJobImpl.class,"test","housekeeping");
            will(returnValue(jobDetail));

            atLeast(2).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // registerJobs expectations
            oneOf(housekeepingJob).isInitialised();
            will(returnValue(true));

            oneOf(housekeepingJob).isEnabled();
            will(returnValue(true));

            oneOf(housekeepingJob).init();

            oneOf(scheduler).checkExists(jobKey);
            will(returnValue(false));

            oneOf(housekeepingJob).getCronExpression();
            will(returnValue(HousekeepingJob.DEFAULT_CRON_EXPRESSION));

            oneOf(scheduler).scheduleJob(with(any(JobDetail.class)),with(any(Trigger.class)));
            will(returnValue(new Date()));

        }});

        HousekeepingSchedulerServiceImpl uut = new HousekeepingSchedulerServiceImpl(scheduler,scheduledJobFactory,houseKeepingJobs);
        // test
        uut.registerJobs();

        mockery.assertIsSatisfied();
    }

    @Test
    public void registerJobsWhenNoJobs() throws SchedulerException
    {
        List<HousekeepingJob> houseKeepingJobs = Arrays.asList();

        mockery.checking(new Expectations() {{

            // Constructor expectations

        }});

        HousekeepingSchedulerServiceImpl uut = new HousekeepingSchedulerServiceImpl(scheduler,scheduledJobFactory,houseKeepingJobs);
        // test
        uut.registerJobs();

        mockery.assertIsSatisfied();
    }

    @Test
    public void registerJobsWhenHousekeepJobIsDisabled() throws SchedulerException
    {
        List<HousekeepingJob> houseKeepingJobs = Arrays.asList(housekeepingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(housekeepingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(housekeepingJob,HousekeepingJobImpl.class,"test","housekeeping");
            will(returnValue(jobDetail));

            atLeast(2).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // registerJobs expectations
            oneOf(housekeepingJob).isInitialised();
            will(returnValue(true));

            oneOf(housekeepingJob).isEnabled();
            will(returnValue(false));

            oneOf(housekeepingJob).init();

        }});

        HousekeepingSchedulerServiceImpl uut = new HousekeepingSchedulerServiceImpl(scheduler,scheduledJobFactory,houseKeepingJobs);
        // test
        uut.registerJobs();

        mockery.assertIsSatisfied();
    }

    @Test

    public void registerJobsWhenShcedulerThrowsRuntimeException() throws SchedulerException
    {
        List<HousekeepingJob> houseKeepingJobs = Arrays.asList(housekeepingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(housekeepingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(housekeepingJob,HousekeepingJobImpl.class,"test","housekeeping");
            will(returnValue(jobDetail));

            atLeast(2).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // registerJobs expectations
            oneOf(housekeepingJob).isInitialised();
            will(returnValue(true));

            oneOf(housekeepingJob).isEnabled();
            will(returnValue(true));

            oneOf(housekeepingJob).init();

            oneOf(scheduler).checkExists(jobKey);
            will(returnValue(false));

            oneOf(housekeepingJob).getCronExpression();
            will(returnValue(HousekeepingJob.DEFAULT_CRON_EXPRESSION));

            oneOf(scheduler).scheduleJob(with(any(JobDetail.class)),with(any(Trigger.class)));
            will(throwException(new RuntimeException("Scheduler is shutdown")));

        }});

        HousekeepingSchedulerServiceImpl uut = new HousekeepingSchedulerServiceImpl(scheduler,scheduledJobFactory,houseKeepingJobs);
        // test
        try
        {
            uut.registerJobs();
        }catch (RuntimeException e){
            mockery.assertIsSatisfied();
            return;
        }

        Assert.fail("Not expected to be reached!");

    }

    @Test
    public void remove() throws SchedulerException
    {
        List<HousekeepingJob> houseKeepingJobs = Arrays.asList(housekeepingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(housekeepingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(housekeepingJob,HousekeepingJobImpl.class,"test","housekeeping");
            will(returnValue(jobDetail));

            atLeast(2).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // registerJobs expectations
            oneOf(scheduler).checkExists(jobKey);
            will(returnValue(true));

            oneOf(scheduler).deleteJob(jobKey);
            will(returnValue(true));

        }});

        HousekeepingSchedulerServiceImpl uut = new HousekeepingSchedulerServiceImpl(scheduler,scheduledJobFactory,houseKeepingJobs);
        // test
        uut.removeJob("test");

        mockery.assertIsSatisfied();
    }

    @Test
    public void removeWhenJobDoesntExist() throws SchedulerException
    {
        List<HousekeepingJob> houseKeepingJobs = Arrays.asList(housekeepingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(housekeepingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(housekeepingJob,HousekeepingJobImpl.class,"test","housekeeping");
            will(returnValue(jobDetail));

            atLeast(2).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // registerJobs expectations
            oneOf(scheduler).checkExists(jobKey);
            will(returnValue(false));

        }});

        HousekeepingSchedulerServiceImpl uut = new HousekeepingSchedulerServiceImpl(scheduler,scheduledJobFactory,houseKeepingJobs);
        // test
        uut.removeJob("test");

        mockery.assertIsSatisfied();
    }

    @Test
    public void add() throws SchedulerException
    {
        List<HousekeepingJob> houseKeepingJobs = Arrays.asList(housekeepingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(housekeepingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(housekeepingJob,HousekeepingJobImpl.class,"test","housekeeping");
            will(returnValue(jobDetail));

            atLeast(2).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // registerJobs expectations
            atLeast(2).of(scheduler).checkExists(jobKey);
            will(returnValue(false));

            atLeast(2).of(housekeepingJob).getCronExpression();
            will(returnValue(HousekeepingJob.DEFAULT_CRON_EXPRESSION));

            oneOf(scheduler).scheduleJob(with(any(JobDetail.class)),with(any(Trigger.class)));
            will(returnValue(new Date()));

        }});

        HousekeepingSchedulerServiceImpl uut = new HousekeepingSchedulerServiceImpl(scheduler,scheduledJobFactory,houseKeepingJobs);
        // test
        uut.addJob("test");

        mockery.assertIsSatisfied();
    }

    @Test
    public void startScheduler() throws SchedulerException
    {
        List<HousekeepingJob> houseKeepingJobs = Arrays.asList(housekeepingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(housekeepingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(housekeepingJob,HousekeepingJobImpl.class,"test","housekeeping");
            will(returnValue(jobDetail));

            atLeast(2).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // startScheduler expectations
            oneOf(housekeepingJob).isInitialised();
            will(returnValue(true));

            oneOf(housekeepingJob).isEnabled();
            will(returnValue(false));

            oneOf(housekeepingJob).init();

            oneOf(scheduler).start();
        }});

        HousekeepingSchedulerServiceImpl uut = new HousekeepingSchedulerServiceImpl(scheduler,scheduledJobFactory,houseKeepingJobs);
        // test
        uut.startScheduler();

        mockery.assertIsSatisfied();
    }

    @Test
    public void shutdownScheduler() throws SchedulerException
    {
        List<HousekeepingJob> houseKeepingJobs = Arrays.asList(housekeepingJob);

        mockery.checking(new Expectations() {{

            // Constructor expectations
            atLeast(2).of(housekeepingJob).getJobName();
            will(returnValue("test"));


            oneOf(scheduledJobFactory).createJobDetail(housekeepingJob,HousekeepingJobImpl.class,"test","housekeeping");
            will(returnValue(jobDetail));

            atLeast(1).of(jobDetail).getKey();
            will(returnValue(jobKey));

            // shutdownScheduler expectations

            oneOf(scheduler).shutdown();
        }});

        HousekeepingSchedulerServiceImpl uut = new HousekeepingSchedulerServiceImpl(scheduler,scheduledJobFactory,houseKeepingJobs);
        // test
        uut.shutdownScheduler();

        mockery.assertIsSatisfied();
    }
}
