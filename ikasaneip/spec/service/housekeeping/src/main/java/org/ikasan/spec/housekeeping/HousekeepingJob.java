package org.ikasan.spec.housekeeping;

import org.quartz.Job;

public interface HousekeepingJob extends Job
{
    String HOUSE_KEEPING_BATCH_SIZE = "-houseKeepingBatchSize";
    String TRANSACTION_BATCH_SIZE = "-transactionBatchSize";
    String CRON_EXPRESSION = "-cronExpression";
    String ENABLED = "-enabled";

    String DEFAULT_CRON_EXPRESSION = "0 0/1 * * * ?";
    Integer DEFAULT_BATCH_DELETE_SIZE = 200;
    Integer DEFAULT_TRANSACTION_DELETE_SIZE = 2500;

    void init();

    void save();

    void setCronExpression(String cronExpression);

    String getCronExpression();

    String getJobName();

    Integer getBatchDeleteSize();

    Integer getTransactionDeleteSize();

    void setBatchDeleteSize(Integer batchDeleteSize);

    void setTransactionDeleteSize(Integer transactionDeleteSize);

    Boolean isEnabled();

    void setEnabled(Boolean enabled);

    Boolean getLastExecutionSuccessful();

    String getExecutionErrorMessage();

    Boolean isInitialised();

    void setInitialised(Boolean initialised);
}
