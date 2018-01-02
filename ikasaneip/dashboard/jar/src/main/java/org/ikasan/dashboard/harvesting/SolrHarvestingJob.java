package org.ikasan.dashboard.harvesting;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.harvest.HarvestEvent;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.harvest.HarvestService;
import org.ikasan.spec.solr.SolrInitialisationService;
import org.ikasan.spec.solr.SolrService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ikasan Development Team on 09/08/2016.
 */
@DisallowConcurrentExecution
public class SolrHarvestingJob implements Job
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrHarvestingJob.class);

    public static final String HARVEST_BATCH_SIZE = "-harvestBatchSize";
    public static final String THREAD_COUNT = "-threadCount";
    public static final String CRON_EXPRESSION = "-cronExpression";
    public static final String ENABLED = "-enabled";


    public static final String DEFAULT_CRON_EXPRESSION = "0/10 * * * * ?";
    public static final Integer DEFAULT_BATCH_DELETE_SIZE = 200;
    public static final Integer DEFAULT_THREAD_COUNT = 1;


    private String jobName;
    private HarvestService harvestService;
    private SolrService solrService;
    private PlatformConfigurationService platformConfigurationService;
    private Integer harvestSize;
    private Integer threadCount;
    private String cronExpression;
    private Boolean enabled = true;
    private Boolean lastExecutionSuccessful = true;
    private String executionErrorMessage;
    private Boolean initialised = false;


    public SolrHarvestingJob(String jobName, HarvestService harvestService,
                             PlatformConfigurationService platformConfigurationService,
                             SolrService solrService)
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
        this.solrService = solrService;
        if(this.solrService == null)
        {
            throw new IllegalArgumentException("solrService cannot be null!");
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

            String threadCountString = this.platformConfigurationService.getConfigurationValue(this.jobName + THREAD_COUNT);
            if (threadCountString != null && threadCountString.length() > 0)
            {
                try
                {
                    this.threadCount = new Integer(threadCountString);
                }
                catch (NumberFormatException e)
                {
                    this.threadCount = DEFAULT_THREAD_COUNT;
                    this.platformConfigurationService.saveConfigurationValue(this.getJobName() + THREAD_COUNT, DEFAULT_THREAD_COUNT.toString());
                    logger.warn("The value configured for " + this.jobName + THREAD_COUNT
                            + " is not a number. Using default house keeping batch size: " + DEFAULT_THREAD_COUNT);
                }
            }
            else
            {
                this.threadCount = DEFAULT_THREAD_COUNT;
                this.platformConfigurationService.saveConfigurationValue(this.getJobName() + THREAD_COUNT, DEFAULT_THREAD_COUNT.toString());
                logger.warn("The value configured for " + this.jobName + THREAD_COUNT
                        + " is not a number. Using default house keeping batch size: " + DEFAULT_THREAD_COUNT);
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

        ExecutorService executor = Executors.newFixedThreadPool(this.threadCount);

        this.solrService.setSolrUsername(this.platformConfigurationService.getSolrUsername());
        this.solrService.setSolrPassword(this.platformConfigurationService.getSolrPassword());

        try
        {
            if (harvestService.harvestableRecordsExist())
            {
                List<HarvestEvent> events = this.harvestService.harvest(this.harvestSize);

                if(events.size() > 0)
                {
                    List<List<HarvestEvent>> partitionedEvents = Lists.partition(events, threadCount);

                    for (List<HarvestEvent> smallerEvents : partitionedEvents)
                    {
                        SaveRunnable saveRunnable = new SaveRunnable(smallerEvents);

                        executor.execute(saveRunnable);
                    }
                }
            }

            // This will make the executor accept no new threads
            // and finish all existing threads in the queue
            executor.shutdown();
            // Wait until all threads are finished
            executor.awaitTermination(300, TimeUnit.SECONDS);
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
                + SolrHarvestingJob.CRON_EXPRESSION, this.cronExpression);
        getPlatformConfigurationService().saveConfigurationValue(getJobName()
                + SolrHarvestingJob.HARVEST_BATCH_SIZE, this.harvestSize.toString());
        getPlatformConfigurationService().saveConfigurationValue(getJobName()
                + SolrHarvestingJob.THREAD_COUNT, this.threadCount.toString());
        getPlatformConfigurationService().saveConfigurationValue(getJobName()
                + SolrHarvestingJob.ENABLED, this.enabled.toString());

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

    public Integer getThreadCount()
    {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount)
    {
        this.threadCount = threadCount;
    }

    private class SaveRunnable implements Runnable
    {
        private List<HarvestEvent> events;

        public SaveRunnable(List<HarvestEvent> events)
        {
            this.events = events;
        }

        @Override
        public void run()
        {
            solrService.save(events);

            harvestService.updateAsHarvested(events);
        }
    }
}
