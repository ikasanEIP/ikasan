package org.ikasan.harvesting;

import org.ikasan.harvest.HarvestEvent;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.harvest.HarvestService;
import org.ikasan.spec.harvest.HarvestingJob;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Test;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

public class HarvestingJobImplTest
{

    private Mockery mockery = new Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    HarvestService harvestService = mockery.mock(HarvestService.class);
    DashboardRestService dashboardRestService = mockery.mock(DashboardRestService.class);
    Environment environment = mockery.mock(Environment.class);
    JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class);

    @Test
    public void initWhenValuesAreNotInEnvironments()
    {

        HarvestingJobImpl uut = new HarvestingJobImpl("test", harvestService, environment, dashboardRestService);

        mockery.checking(new Expectations() {{
            oneOf(environment).getProperty("test"+ HarvestingJob.HARVEST_BATCH_SIZE);
            will(returnValue(null));

            oneOf(environment).getProperty("test"+HarvestingJob.ENABLED);
            will(returnValue(null));

        }});
        uut.init();

        mockery.assertIsSatisfied();
    }

    @Test
    public void initWhenValuesInEnvironmentAreOfWrongType()
    {

        HarvestingJobImpl uut = new HarvestingJobImpl("test", harvestService, environment, dashboardRestService);

        mockery.checking(new Expectations() {{
            oneOf(environment).getProperty("test"+HarvestingJob.HARVEST_BATCH_SIZE);
            will(returnValue("test"));

            oneOf(environment).getProperty("test"+HarvestingJob.ENABLED);
            will(returnValue("test"));

        }});
        uut.init();

        mockery.assertIsSatisfied();
    }

    @Test
    public void initWhenValuesInEnvironments()
    {

        HarvestingJobImpl uut = new HarvestingJobImpl("test", harvestService, environment, dashboardRestService);

        mockery.checking(new Expectations() {{
            oneOf(environment).getProperty("test"+HarvestingJob.HARVEST_BATCH_SIZE);
            will(returnValue("100"));

            oneOf(environment).getProperty("test"+HarvestingJob.ENABLED);
            will(returnValue("false"));

        }});
        uut.init();

        mockery.assertIsSatisfied();
    }

    @Test
    public void executeWhenHarvestableRecordsExistIsFalse() throws JobExecutionException
    {

        HarvestingJobImpl uut = new HarvestingJobImpl("test", harvestService, environment, dashboardRestService);

        mockery.checking(new Expectations() {{

            oneOf(harvestService).harvestableRecordsExist();
            will(returnValue(false));

        }});
        uut.execute(jobExecutionContext);

        mockery.assertIsSatisfied();
    }

    @Test
    public void executeWhenHarvestableRecordsExistIsTrueButNothingToHarvest() throws JobExecutionException
    {

        HarvestingJobImpl uut = new HarvestingJobImpl("test", harvestService, environment, dashboardRestService);

        mockery.checking(new Expectations() {{

            oneOf(environment).getProperty("test"+HarvestingJob.HARVEST_BATCH_SIZE);
            will(returnValue("100"));

            oneOf(environment).getProperty("test"+HarvestingJob.ENABLED);
            will(returnValue("true"));

            oneOf(harvestService).harvestableRecordsExist();
            will(returnValue(true));

            oneOf(harvestService).harvest(100);

        }});

        uut.init();
        uut.execute(jobExecutionContext);

        mockery.assertIsSatisfied();
    }

    @Test
    public void executeWhenHarvestableRecordsExistIsTrueAndHarvestList() throws JobExecutionException
    {

        HarvestingJobImpl uut = new HarvestingJobImpl("test", harvestService, environment, dashboardRestService);

        HarvestEvent harvestEvent = new HarvestEvent()
        {
            @Override public void setHarvested(boolean harvested)
            {
            }
        };
        List<HarvestEvent> list = Arrays.asList(harvestEvent);

        mockery.checking(new Expectations() {{

            oneOf(environment).getProperty("test"+HarvestingJob.HARVEST_BATCH_SIZE);
            will(returnValue("100"));

            oneOf(environment).getProperty("test"+HarvestingJob.ENABLED);
            will(returnValue("true"));

            oneOf(harvestService).harvestableRecordsExist();
            will(returnValue(true));

            oneOf(harvestService).harvest(100);
            will(returnValue(list));

            oneOf(dashboardRestService).publish(list);
            will(returnValue(true));

            oneOf(harvestService).updateAsHarvested(list);

        }});

        uut.init();
        uut.execute(jobExecutionContext);

        mockery.assertIsSatisfied();
    }

}
