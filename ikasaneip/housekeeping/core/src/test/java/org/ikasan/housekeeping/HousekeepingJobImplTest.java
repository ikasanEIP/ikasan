package org.ikasan.housekeeping;

import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Test;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.env.Environment;

public class HousekeepingJobImplTest
{

    private Mockery mockery = new Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    HousekeepService housekeepService = mockery.mock(HousekeepService.class);
    Environment environment = mockery.mock(Environment.class);
    JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class);

    @Test
    public void initWhenValuesAreNotInEnvironments()
    {

        HousekeepingJobImpl uut = new HousekeepingJobImpl("test",housekeepService,environment);


        mockery.checking(new Expectations() {{
            oneOf(environment).getProperty("test"+HousekeepingJob.HOUSE_KEEPING_BATCH_SIZE);
            will(returnValue(null));

            oneOf(housekeepService).setHousekeepingBatchSize(HousekeepingJob.DEFAULT_BATCH_DELETE_SIZE);

            oneOf(environment).getProperty("test"+HousekeepingJob.TRANSACTION_BATCH_SIZE);
            will(returnValue(null));

            oneOf(housekeepService).setTransactionBatchSize(HousekeepingJob.DEFAULT_TRANSACTION_DELETE_SIZE);

            oneOf(environment).getProperty("test"+HousekeepingJob.ENABLED);
            will(returnValue(null));

        }});
        uut.init();

        mockery.assertIsSatisfied();
    }

    @Test
    public void initWhenValuesInEnvironmentAreOfWrongType()
    {

        HousekeepingJobImpl uut = new HousekeepingJobImpl("test",housekeepService,environment);


        mockery.checking(new Expectations() {{
            oneOf(environment).getProperty("test"+HousekeepingJob.HOUSE_KEEPING_BATCH_SIZE);
            will(returnValue("test"));

            oneOf(housekeepService).setHousekeepingBatchSize(HousekeepingJob.DEFAULT_BATCH_DELETE_SIZE);

            oneOf(environment).getProperty("test"+HousekeepingJob.TRANSACTION_BATCH_SIZE);
            will(returnValue("test"));

            oneOf(housekeepService).setTransactionBatchSize(HousekeepingJob.DEFAULT_TRANSACTION_DELETE_SIZE);

            oneOf(environment).getProperty("test"+HousekeepingJob.ENABLED);
            will(returnValue("test"));

        }});
        uut.init();

        mockery.assertIsSatisfied();
    }

    @Test
    public void initWhenValuesInEnvironments()
    {

        HousekeepingJobImpl uut = new HousekeepingJobImpl("test",housekeepService,environment);

        mockery.checking(new Expectations() {{
            oneOf(environment).getProperty("test"+HousekeepingJob.HOUSE_KEEPING_BATCH_SIZE);
            will(returnValue("100"));

            oneOf(housekeepService).setHousekeepingBatchSize(100);

            oneOf(environment).getProperty("test"+HousekeepingJob.TRANSACTION_BATCH_SIZE);
            will(returnValue("2000"));

            oneOf(housekeepService).setTransactionBatchSize(2000);

            oneOf(environment).getProperty("test"+HousekeepingJob.ENABLED);
            will(returnValue("false"));

        }});
        uut.init();

        mockery.assertIsSatisfied();
    }

    @Test
    public void executeWhenHousekeepablesExistIsFalse() throws JobExecutionException
    {

        HousekeepingJobImpl uut = new HousekeepingJobImpl("test",housekeepService,environment);

        mockery.checking(new Expectations() {{

            oneOf(housekeepService).housekeepablesExist();
            will(returnValue(false));

        }});
        uut.execute(jobExecutionContext);

        mockery.assertIsSatisfied();
    }
    @Test
    public void executeWhenHousekeepablesExistIsTrue() throws JobExecutionException
    {

        HousekeepingJobImpl uut = new HousekeepingJobImpl("test",housekeepService,environment);

        mockery.checking(new Expectations() {{

            oneOf(housekeepService).housekeepablesExist();
            will(returnValue(true));

            oneOf(housekeepService).housekeep();

        }});
        uut.execute(jobExecutionContext);

        mockery.assertIsSatisfied();
    }

}
