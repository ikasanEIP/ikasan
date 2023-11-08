package org.ikasan.housekeeping;

import org.ikasan.spec.housekeeping.HousekeepService;
import org.ikasan.spec.housekeeping.HousekeepingJob;
import org.ikasan.spec.housekeeping.HousekeepingJobState;
import org.ikasan.spec.monitor.Monitor;
import org.ikasan.spec.monitor.MonitorSubject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * Created by Ikasan Development Team on 09/07/2019.
 */
@DisallowConcurrentExecution
public class HousekeepingJobImpl implements HousekeepingJob, MonitorSubject
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(HousekeepingJobImpl.class);

    private String jobName;
    private HousekeepService houseKeepService;
    private Environment environment;
    private Integer batchDeleteSize;
    private Integer transactionDeleteSize;
    private String cronExpression;
    private Boolean enabled = true;
    private Boolean lastExecutionSuccessful = true;
    private String executionErrorMessage;
    private Boolean initialised = false;
    private Monitor monitor;

    public HousekeepingJobImpl(String jobName, HousekeepService houseKeepService,
        Environment environment)
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
            String houseKeepingBatchSize = this.environment.getProperty(this.jobName + HOUSE_KEEPING_BATCH_SIZE);
            if (houseKeepingBatchSize != null && houseKeepingBatchSize.length() > 0)
            {
                try
                {
                    this.batchDeleteSize = Integer.valueOf(houseKeepingBatchSize);
                    this.houseKeepService.setHousekeepingBatchSize(this.batchDeleteSize);
                }
                catch (NumberFormatException e)
                {
                    this.batchDeleteSize = DEFAULT_BATCH_DELETE_SIZE;
                    this.houseKeepService.setHousekeepingBatchSize(DEFAULT_BATCH_DELETE_SIZE);
                    logger.debug("The value configured for " + this.jobName + HOUSE_KEEPING_BATCH_SIZE
                            + " is not a number. Using default house keeping batch size: " + DEFAULT_BATCH_DELETE_SIZE);
                }
            }
            else
            {
                this.batchDeleteSize = DEFAULT_BATCH_DELETE_SIZE;
                this.houseKeepService.setHousekeepingBatchSize(DEFAULT_BATCH_DELETE_SIZE);
                logger.debug("The value configured for " + this.jobName + HOUSE_KEEPING_BATCH_SIZE
                        + " is not available. Using default house keeping batch size: " + DEFAULT_BATCH_DELETE_SIZE);
            }

            String transactionBatchSize = this.environment.getProperty(this.jobName + TRANSACTION_BATCH_SIZE);
            if (transactionBatchSize != null && transactionBatchSize.length() > 0)
            {
                try
                {
                    this.transactionDeleteSize = Integer.valueOf(transactionBatchSize);
                    this.houseKeepService.setTransactionBatchSize(this.transactionDeleteSize);
                }
                catch (NumberFormatException e)
                {
                    this.transactionDeleteSize = DEFAULT_TRANSACTION_DELETE_SIZE;
                    this.houseKeepService.setTransactionBatchSize(DEFAULT_TRANSACTION_DELETE_SIZE);
                    logger.info("The value configured for " + this.jobName + TRANSACTION_BATCH_SIZE
                            + " is not a number. Using default house keeping transaction size: " + DEFAULT_TRANSACTION_DELETE_SIZE);
                }
            }
            else
            {
                this.transactionDeleteSize = DEFAULT_TRANSACTION_DELETE_SIZE;
                this.houseKeepService.setTransactionBatchSize(DEFAULT_TRANSACTION_DELETE_SIZE);
                logger.debug("The value configured for " + this.jobName + TRANSACTION_BATCH_SIZE
                        + " is not available. Using default house keeping transaction size: " + DEFAULT_TRANSACTION_DELETE_SIZE);
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
                    logger.debug("The value configured for " + this.jobName + ENABLED + " is not a boolean. Using default house keeping enabled: true");
                }
            }
            else
            {
                this.enabled = true;
                logger.debug("The value configured for " + this.jobName + ENABLED + " is not available. Using default house keeping enabled: true");
            }
        }
        catch(Exception e)
        {
            logger.error("Unable to initialise house keeping job: " + this.getJobName()
                    + ". This may be due to the database not yet having been created.", e);
        }

        this.initialised = true;
    }

    @Override public void save()
    {
        //Not Supported
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
    {
        logger.debug("Housekeeping job executing: " + this.getJobName()
                + " [transaction size: " + this.transactionDeleteSize + "][batch delete size: " + this.batchDeleteSize + "]");
        try
        {
            if (houseKeepService.housekeepablesExist())
            {
                this.houseKeepService.housekeep();
                if(this.monitor!=null)this.monitor.invoke(HousekeepingJobState.HEALTHY);
            }
        }
        catch(Exception e)
        {
            this.executionErrorMessage = e.getMessage();
            this.lastExecutionSuccessful = false;
            logger.error("Could not execute housekeeping job[%s]. Error message[%s].".formatted(this.jobName, this.executionErrorMessage));
            if(this.monitor!=null)this.monitor.invoke(HousekeepingJobState.ERROR);
        }

        this.lastExecutionSuccessful = true;
        logger.debug("Finished housekeeping job executing: " + this.getJobName());
    }

    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    @Override
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

    @Override
    public Boolean isEnabled()
    {
        return enabled;
    }

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

    public void setInitialised(Boolean initialised)
    {
        this.initialised = initialised;
    }
}
