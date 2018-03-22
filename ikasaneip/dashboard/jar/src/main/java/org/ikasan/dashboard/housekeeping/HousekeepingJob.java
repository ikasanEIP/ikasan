package org.ikasan.dashboard.housekeeping;

import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.ikasan.spec.solr.SolrService;
import org.ikasan.spec.solr.SolrServiceBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by Ikasan Development Team on 09/08/2016.
 */
@DisallowConcurrentExecution
public class HousekeepingJob implements Job
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(HousekeepingJob.class);

    public static final String HOUSE_KEEPING_BATCH_SIZE = "-houseKeepingBatchSize";
    public static final String TRANSACTION_BATCH_SIZE = "-transactionBatchSize";
    public static final String CRON_EXPRESSION = "-cronExpression";
    public static final String ENABLED = "-enabled";


    public static final String DEFAULT_CRON_EXPRESSION = "0 0/1 * * * ?";
    public static final Integer DEFAULT_BATCH_DELETE_SIZE = 200;
    public static final Integer DEFAULT_TRANSACTION_DELETE_SIZE = 2500;


    private String jobName;
    private HousekeepService houseKeepService;
    private PlatformConfigurationService platformConfigurationService;
    private Integer batchDeleteSize;
    private Integer transactionDeleteSize;
    private String cronExpression;
    private Boolean enabled = true;
    private Boolean lastExecutionSuccessful = true;
    private String executionErrorMessage;
    private Boolean initialised = false;


    public HousekeepingJob(String jobName, HousekeepService houseKeepService,
                           PlatformConfigurationService platformConfigurationService)
    {
        this.jobName = jobName;
        if(this.jobName == null)
        {
            throw new IllegalArgumentException("Housekeeping job name cannot be null!");
        }
        this.houseKeepService = houseKeepService;
        if(this.houseKeepService == null)
        {
            throw new IllegalArgumentException("houseKeepService cannot be null!");
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
            String houseKeepingBatchSize = this.platformConfigurationService.getConfigurationValue(this.jobName + HOUSE_KEEPING_BATCH_SIZE);
            if (houseKeepingBatchSize != null && houseKeepingBatchSize.length() > 0)
            {
                try
                {
                    this.batchDeleteSize = new Integer(houseKeepingBatchSize);
                    this.houseKeepService.setHousekeepingBatchSize(this.batchDeleteSize);
                }
                catch (NumberFormatException e)
                {
                    this.batchDeleteSize = DEFAULT_BATCH_DELETE_SIZE;
                    this.houseKeepService.setHousekeepingBatchSize(DEFAULT_BATCH_DELETE_SIZE);
                    this.platformConfigurationService.saveConfigurationValue(this.getJobName() + HOUSE_KEEPING_BATCH_SIZE, DEFAULT_BATCH_DELETE_SIZE.toString());
                    logger.warn("The value configured for " + this.jobName + HOUSE_KEEPING_BATCH_SIZE
                            + " is not a number. Using default house keeping batch size: " + DEFAULT_BATCH_DELETE_SIZE);
                }
            }
            else
            {
                this.batchDeleteSize = DEFAULT_BATCH_DELETE_SIZE;
                this.houseKeepService.setHousekeepingBatchSize(DEFAULT_BATCH_DELETE_SIZE);
                this.platformConfigurationService.saveConfigurationValue(this.getJobName() + HOUSE_KEEPING_BATCH_SIZE, DEFAULT_BATCH_DELETE_SIZE.toString());
                logger.warn("The value configured for " + this.jobName + HOUSE_KEEPING_BATCH_SIZE
                        + " is not available. Using default house keeping batch size: " + DEFAULT_BATCH_DELETE_SIZE);
            }

            String transactionBatchSize = this.platformConfigurationService.getConfigurationValue(this.jobName + TRANSACTION_BATCH_SIZE);
            if (transactionBatchSize != null && transactionBatchSize.length() > 0)
            {
                try
                {
                    this.transactionDeleteSize = new Integer(transactionBatchSize);
                    this.houseKeepService.setTransactionBatchSize(this.transactionDeleteSize);
                }
                catch (NumberFormatException e)
                {
                    this.transactionDeleteSize = DEFAULT_TRANSACTION_DELETE_SIZE;
                    this.houseKeepService.setTransactionBatchSize(DEFAULT_TRANSACTION_DELETE_SIZE);
                    this.platformConfigurationService.saveConfigurationValue(this.getJobName() + TRANSACTION_BATCH_SIZE, DEFAULT_TRANSACTION_DELETE_SIZE.toString());
                    logger.warn("The value configured for " + this.jobName + TRANSACTION_BATCH_SIZE
                            + " is not a number. Using default house keeping transaction size: " + DEFAULT_TRANSACTION_DELETE_SIZE);
                }
            }
            else
            {
                this.transactionDeleteSize = DEFAULT_TRANSACTION_DELETE_SIZE;
                this.houseKeepService.setTransactionBatchSize(DEFAULT_TRANSACTION_DELETE_SIZE);
                this.platformConfigurationService.saveConfigurationValue(this.getJobName() + TRANSACTION_BATCH_SIZE, DEFAULT_TRANSACTION_DELETE_SIZE.toString());
                logger.warn("The value configured for " + this.jobName + TRANSACTION_BATCH_SIZE
                        + " is not available. Using default house keeping transaction size: " + DEFAULT_TRANSACTION_DELETE_SIZE);
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
        logger.info("Housekeeping job executing: " + this.getJobName()
                + " [transaction size: " + this.transactionDeleteSize + "][batch delete size: " + this.batchDeleteSize + "]");
        try
        {
            if (houseKeepService.housekeepablesExist())
            {
                if(this.houseKeepService instanceof SolrGeneralServiceImpl)
                {
                    String username = this.platformConfigurationService.getSolrUsername();
                    String password = this.platformConfigurationService.getSolrPassword();

                    ((SolrGeneralServiceImpl)this.houseKeepService).setSolrUsername(username);
                    ((SolrGeneralServiceImpl)this.houseKeepService).setSolrPassword(password);
                }

                this.houseKeepService.housekeep();
            }
        }
        catch(Exception e)
        {
            this.executionErrorMessage = e.getMessage();
            this.lastExecutionSuccessful = false;
            throw new JobExecutionException("Could not execute housekeeping job: " + this.jobName, e);
        }

        this.lastExecutionSuccessful = true;
        logger.info("Finished housekeeping job executing: " + this.getJobName());
    }

    public void save()
    {
        getPlatformConfigurationService().saveConfigurationValue(getJobName()
                + HousekeepingJob.CRON_EXPRESSION, this.cronExpression);
        getPlatformConfigurationService().saveConfigurationValue(getJobName()
                + HousekeepingJob.HOUSE_KEEPING_BATCH_SIZE, this.batchDeleteSize.toString());
        getPlatformConfigurationService().saveConfigurationValue(getJobName()
                + HousekeepingJob.TRANSACTION_BATCH_SIZE, this.transactionDeleteSize.toString());
        getPlatformConfigurationService().saveConfigurationValue(getJobName()
                + HousekeepingJob.ENABLED, this.enabled.toString());

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

    public Integer getBatchDeleteSize()
    {
        return batchDeleteSize;
    }

    public Integer getTransactionDeleteSize()
    {
        return transactionDeleteSize;
    }

    public void setBatchDeleteSize(Integer batchDeleteSize)
    {
        this.batchDeleteSize = batchDeleteSize;
    }

    public void setTransactionDeleteSize(Integer transactionDeleteSize)
    {
        this.transactionDeleteSize = transactionDeleteSize;
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
