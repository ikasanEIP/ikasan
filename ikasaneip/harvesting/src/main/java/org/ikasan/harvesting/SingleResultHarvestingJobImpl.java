package org.ikasan.harvesting;

import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.harvest.HarvestJobState;
import org.ikasan.spec.harvest.HarvestService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.List;

public class SingleResultHarvestingJobImpl extends HarvestingJobImpl {
    private static Logger logger = LoggerFactory.getLogger(SingleResultHarvestingJobImpl.class);

    public SingleResultHarvestingJobImpl(String jobName, HarvestService harvestService, Environment environment, DashboardRestService solrService) {
        super(jobName, harvestService, environment, solrService);
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.debug("Single Result Harvesting job executing: " + this.getJobName());

        try
        {
            if (harvestService.harvestableRecordsExist())
            {
                List events = this.harvestService.harvest(this.harvestSize);

                if(events.size() > 0)
                {
                    if(events.size() > 1) {
                        logger.warn(String.format("Single Result Harvesting job[%s] expected only one result to publish" +
                                ", however [%s] results encountered. Only the first will be published!", this.getJobName(), events.size()));
                    }
                    Object event = events.get(0);
                    if(dashboardRestService.publish(event))
                    {
                        harvestService.updateAsHarvested(events);
                        if(this.monitor!=null)this.monitor.invoke(HarvestJobState.HEALTHY);
                    }
                    else if(this.monitor!=null)
                    {
                        this.monitor.invoke(HarvestJobState.ERROR);
                    }
                }
            }
        }
        catch(Exception e)
        {
            this.executionErrorMessage = e.getMessage();
            this.lastExecutionSuccessful = false;
            if(this.monitor!=null)this.monitor.invoke(HarvestJobState.ERROR);
            throw new JobExecutionException("Could not execute housekeeping job: " + this.jobName, e);
        }

        this.lastExecutionSuccessful = true;
        logger.debug("Finished single result harvesting job executing: " + this.getJobName());
    }
}
