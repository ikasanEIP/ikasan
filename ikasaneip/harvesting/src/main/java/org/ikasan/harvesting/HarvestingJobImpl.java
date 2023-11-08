package org.ikasan.harvesting;

import org.ikasan.harvest.HarvestEvent;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.ikasan.spec.harvest.HarvestJobState;
import org.ikasan.spec.harvest.HarvestService;
import org.ikasan.spec.harvest.HarvestingJob;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.MonitorSubject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * Created by Ikasan Development Team on 09/08/2016.
 */
@DisallowConcurrentExecution
public class HarvestingJobImpl implements HarvestingJob, MonitorSubject
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(HarvestingJobImpl.class);

    private String jobName;
    private HarvestService harvestService;
    private DashboardRestService dashboardRestService;
    private Environment environment;
    private Integer harvestSize;
    private String cronExpression;
    private Boolean enabled = true;
    private Boolean lastExecutionSuccessful = true;
    private String executionErrorMessage;
    private Boolean initialised = false;

    private Monitor monitor;

    public HarvestingJobImpl(String jobName, HarvestService harvestService,
        Environment environment,
        DashboardRestService solrService)
    {
        this.jobName = jobName;
        if(this.jobName == null)
        {
            throw new IllegalArgumentException("Harvesting job name cannot be null!");
        }
        this.harvestService = harvestService;
        if(this.harvestService == null)
        {
            throw new IllegalArgumentException("harvestService cannot be null!");
        }
        this.dashboardRestService = solrService;
        if(this.dashboardRestService == null)
        {
            throw new IllegalArgumentException("dashboardRestService cannot be null!");
        }
        this.environment = environment;
        if(this.environment == null)
        {
            throw new IllegalArgumentException("environment cannot be null!");
        }


    }

    @Override
    public void init()
    {
        try
        {
            String houseKeepingBatchSize = this.environment.getProperty(this.jobName + HARVEST_BATCH_SIZE);
            if (houseKeepingBatchSize != null && houseKeepingBatchSize.length() > 0)
            {
                try
                {
                    this.harvestSize = Integer.valueOf(houseKeepingBatchSize);
                }
                catch (NumberFormatException e)
                {
                    this.harvestSize = DEFAULT_BATCH_DELETE_SIZE;
                    logger.info("The value configured for " + this.jobName + HARVEST_BATCH_SIZE
                        + " is not a number. Using default house keeping batch size: " + DEFAULT_BATCH_DELETE_SIZE);
                }
            }
            else
            {
                this.harvestSize = DEFAULT_BATCH_DELETE_SIZE;
                logger.info("The value configured for " + this.jobName + HARVEST_BATCH_SIZE
                    + " is not available. Using default house keeping batch size: " + DEFAULT_BATCH_DELETE_SIZE);
            }


            String enabled = this.environment.getProperty(this.jobName + ENABLED);
            if (enabled != null && enabled.length() > 0)
            {
                try
                {
                    this.enabled = Boolean.valueOf(enabled);
                } catch (Exception e)
                {
                    this.enabled = true;
                    logger.info("The value configured for " + this.jobName + ENABLED + " is not a boolean. Using default house keeping enabled: true");
                }
            }
            else
            {
                this.enabled = true;
                logger.info("The value configured for " + this.jobName + ENABLED + " is not available. Using default house keeping enabled: true");
            }
        }
        catch(Exception e)
        {
            logger.error("Unable to initialise house keeping job: " + this.getJobName()
                + ". This may be due to the database not yet having been created.", e);
        }

        this.initialised = true;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
    {
        logger.debug("Harvesting job executing: " + this.getJobName()
            + " [batch delete size: " + this.harvestSize + "]");

        try
        {
            if (harvestService.harvestableRecordsExist())
            {
                List<HarvestEvent> events = this.harvestService.harvest(this.harvestSize);

                if(events.size() > 0)
                {
                    if(dashboardRestService.publish(events))
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
        logger.debug("Finished harvesting job executing: " + this.getJobName());
    }

    @Override
    public void save()
    {
    }


    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    public String getCronExpression()
    {
        cronExpression = this.environment.getProperty(this.getJobName() + CRON_EXPRESSION);

        if(cronExpression == null || cronExpression.isEmpty())
        {
            cronExpression = DEFAULT_CRON_EXPRESSION;
        }

        return cronExpression;
    }

    @Override
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public Environment getEnvironment()
    {
        return environment;
    }
    @Override
    public String getJobName()
    {
        return jobName;
    }
    @Override
    public Integer getHarvestSize()
    {
        return harvestSize;
    }
    @Override
    public void setHarvestSize(Integer harvestSize)
    {
        this.harvestSize = harvestSize;
    }
    @Override
    public Boolean isEnabled()
    {
        return enabled;
    }
    @Override
    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }
    @Override
    public Boolean getLastExecutionSuccessful()
    {
        return lastExecutionSuccessful;
    }
    @Override
    public String getExecutionErrorMessage()
    {
        return executionErrorMessage;
    }
    @Override
    public Boolean isInitialised()
    {
        return initialised;
    }
    @Override
    public void setInitialised(Boolean initialised)
    {
        this.initialised = initialised;
    }

    @Override public Integer getThreadCount()
    {
        return null;
    }

    @Override public void setThreadCount(Integer threadCount)
    {
    }
}
