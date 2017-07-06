package org.ikasan.dashboard.harvesting;

import org.apache.log4j.Logger;
import org.ikasan.harvest.HarvestService;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.wiretap.model.WiretapFlowEvent;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

/**
 * Created by Ikasan Development Team on 09/08/2016.
 */
@DisallowConcurrentExecution
public class WiretapSolrHarvestingJob implements Job
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(WiretapSolrHarvestingJob.class);

    public static final String HARVEST_BATCH_SIZE = "-harvestBatchSize";
    public static final String TRANSACTION_BATCH_SIZE = "-transactionBatchSize";
    public static final String CRON_EXPRESSION = "-cronExpression";
    public static final String ENABLED = "-enabled";


    public static final String DEFAULT_CRON_EXPRESSION = "0 0/1 * * * ?";
    public static final Integer DEFAULT_BATCH_DELETE_SIZE = 200;
    public static final Integer DEFAULT_TRANSACTION_DELETE_SIZE = 2500;


    private String jobName;
    private HarvestService<WiretapEvent> harvestService;
    private WiretapService solrWiretapService;
    private WiretapService wiretapService;
    private PlatformConfigurationService platformConfigurationService;
    private Integer harvestSize;
    private String cronExpression;
    private Boolean enabled = true;
    private Boolean lastExecutionSuccessful = true;
    private String executionErrorMessage;
    private Boolean initialised = false;


    public WiretapSolrHarvestingJob(String jobName, HarvestService<WiretapEvent> harvestService,
                                    PlatformConfigurationService platformConfigurationService,
                                    WiretapService solrWiretapService, WiretapService wiretapService)
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
        this.solrWiretapService = solrWiretapService;
        if(this.solrWiretapService == null)
        {
            throw new IllegalArgumentException("solrWiretapService cannot be null!");
        }
        this.wiretapService = wiretapService;
        if(this.wiretapService == null)
        {
            throw new IllegalArgumentException("wiretapService cannot be null!");
        }
        this.platformConfigurationService = platformConfigurationService;
        if(this.platformConfigurationService == null)
        {
            throw new IllegalArgumentException("platformConfigurationService cannot be null!");
        }
    }

    public void init()
    {
        try
        {
            String houseKeepingBatchSize = this.platformConfigurationService.getConfigurationValue(this.jobName + HARVEST_BATCH_SIZE);
            if (houseKeepingBatchSize != null && houseKeepingBatchSize.length() > 0)
            {
                try
                {
                    this.harvestSize = new Integer(houseKeepingBatchSize);
                }
                catch (NumberFormatException e)
                {
                    this.harvestSize = DEFAULT_BATCH_DELETE_SIZE;
                    this.platformConfigurationService.saveConfigurationValue(this.getJobName() + HARVEST_BATCH_SIZE, DEFAULT_BATCH_DELETE_SIZE.toString());
                    logger.warn("The value configured for " + this.jobName + HARVEST_BATCH_SIZE
                            + " is not a number. Using default house keeping batch size: " + DEFAULT_BATCH_DELETE_SIZE);
                }
            }
            else
            {
                this.harvestSize = DEFAULT_BATCH_DELETE_SIZE;
                this.platformConfigurationService.saveConfigurationValue(this.getJobName() + HARVEST_BATCH_SIZE, DEFAULT_BATCH_DELETE_SIZE.toString());
                logger.warn("The value configured for " + this.jobName + HARVEST_BATCH_SIZE
                        + " is not available. Using default house keeping batch size: " + DEFAULT_BATCH_DELETE_SIZE);
            }

            String enabled = this.platformConfigurationService.getConfigurationValue(this.jobName + ENABLED);
            if (enabled != null && enabled.length() > 0)
            {
                try
                {
                    this.enabled = new Boolean(enabled);
                } catch (Exception e)
                {
                    this.enabled = true;
                    this.platformConfigurationService.saveConfigurationValue(this.getJobName() + ENABLED, this.enabled.toString());
                    logger.warn("The value configured for " + this.jobName + ENABLED + " is not a boolean. Using default house keeping enabled: true");
                }
            }
            else
            {
                this.enabled = true;
                this.platformConfigurationService.saveConfigurationValue(this.getJobName() + ENABLED, this.enabled.toString());
                logger.warn("The value configured for " + this.jobName + ENABLED + " is not available. Using default house keeping enabled: true");
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
        logger.info("Harvesting job executing: " + this.getJobName()
                + " [batch delete size: " + this.harvestSize + "]");
        try
        {
            if (harvestService.harvestableRecordsExist())
            {
                List<WiretapEvent> events = this.harvestService.harvest(this.harvestSize);

                for(WiretapEvent event: events)
                {
                    this.solrWiretapService.save(event);

                    ((WiretapFlowEvent)event).setHarvested(true);

                    this.wiretapService.save(event);
                }
            }
        }
        catch(Exception e)
        {
            this.executionErrorMessage = e.getMessage();
            this.lastExecutionSuccessful = false;
            throw new JobExecutionException("Could not execute housekeeping job: " + this.jobName, e);
        }

        this.lastExecutionSuccessful = true;
        logger.info("Finished harvesting job executing: " + this.getJobName());
    }

    public void save()
    {
        getPlatformConfigurationService().saveConfigurationValue(getJobName()
                + WiretapSolrHarvestingJob.CRON_EXPRESSION, this.cronExpression);
        getPlatformConfigurationService().saveConfigurationValue(getJobName()
                + WiretapSolrHarvestingJob.HARVEST_BATCH_SIZE, this.harvestSize.toString());
        getPlatformConfigurationService().saveConfigurationValue(getJobName()
                + WiretapSolrHarvestingJob.ENABLED, this.enabled.toString());

    }


    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    public String getCronExpression()
    {
        cronExpression = this.platformConfigurationService.getConfigurationValue(this.getJobName() + CRON_EXPRESSION);

        if(cronExpression == null || cronExpression.isEmpty())
        {
            cronExpression = DEFAULT_CRON_EXPRESSION;
            this.platformConfigurationService.saveConfigurationValue(this.getJobName() + CRON_EXPRESSION, cronExpression);
        }

        return cronExpression;
    }

    public PlatformConfigurationService getPlatformConfigurationService()
    {
        return platformConfigurationService;
    }

    public String getJobName()
    {
        return jobName;
    }

    public Integer getHarvestSize()
    {
        return harvestSize;
    }

    public void setHarvestSize(Integer harvestSize)
    {
        this.harvestSize = harvestSize;
    }

    public Boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }

    public Boolean getLastExecutionSuccessful()
    {
        return lastExecutionSuccessful;
    }

    public String getExecutionErrorMessage()
    {
        return executionErrorMessage;
    }

    public Boolean isInitialised()
    {
        return initialised;
    }

    public void setInitialised(Boolean initialised)
    {
        this.initialised = initialised;
    }
}
