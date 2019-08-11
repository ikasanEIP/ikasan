package org.ikasan.dashboard.housekeeping;

import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Ikasan Development Team on 09/08/2016.
 */
@DisallowConcurrentExecution
public class HousekeepingJobImpl implements HousekeepingJob
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(HousekeepingJobImpl.class);

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

    public HousekeepingJobImpl(String jobName, HousekeepService houseKeepService,
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

    @Override
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
            logger.error(String.format("Could not execute housekeeping job[%s]. Error message[%s].", this.jobName, this.executionErrorMessage));
        }

        this.lastExecutionSuccessful = true;
        logger.info("Finished housekeeping job executing: " + this.getJobName());
    }

    @Override
    public void save()
    {
        getPlatformConfigurationService().saveConfigurationValue(getJobName()
                + HousekeepingJobImpl.CRON_EXPRESSION, this.cronExpression);
        getPlatformConfigurationService().saveConfigurationValue(getJobName()
                + HousekeepingJobImpl.HOUSE_KEEPING_BATCH_SIZE, this.batchDeleteSize.toString());
        getPlatformConfigurationService().saveConfigurationValue(getJobName()
                + HousekeepingJobImpl.TRANSACTION_BATCH_SIZE, this.transactionDeleteSize.toString());
        getPlatformConfigurationService().saveConfigurationValue(getJobName()
                + HousekeepingJobImpl.ENABLED, this.enabled.toString());

    }

    @Override
    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    @Override
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

    @Override
    public String getJobName()
    {
        return jobName;
    }

    @Override
    public Integer getBatchDeleteSize()
    {
        return batchDeleteSize;
    }

    @Override
    public Integer getTransactionDeleteSize()
    {
        return transactionDeleteSize;
    }

    @Override
    public void setBatchDeleteSize(Integer batchDeleteSize)
    {
        this.batchDeleteSize = batchDeleteSize;
    }

    @Override
    public void setTransactionDeleteSize(Integer transactionDeleteSize)
    {
        this.transactionDeleteSize = transactionDeleteSize;
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

    public Boolean getLastExecutionSuccessful()
    {
        return lastExecutionSuccessful;
    }

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
}
