package org.ikasan.spec.harvest;

import org.quartz.Job;

/**
 * Created by Ikasan Development Team on 09/08/2016.
 */
public interface HarvestingJob extends Job
{
    String HARVEST_BATCH_SIZE = "-harvestBatchSize";
    String THREAD_COUNT = "-threadCount";
    String CRON_EXPRESSION = "-cronExpression";
    String ENABLED = "-enabled";
    String DEFAULT_CRON_EXPRESSION = "0/10 * * * * ?";
    Integer DEFAULT_BATCH_DELETE_SIZE = 200;
    Integer DEFAULT_THREAD_COUNT = 1;

    void init();

    void save();

    void setCronExpression(String cronExpression);

    String getCronExpression();

    String getJobName();

    Integer getHarvestSize();

    void setHarvestSize(Integer harvestSize);

    Boolean isEnabled();

    void setEnabled(Boolean enabled);

    Boolean getLastExecutionSuccessful();

    String getExecutionErrorMessage();

    Boolean isInitialised();

    void setInitialised(Boolean initialised);

    Integer getThreadCount();

    void setThreadCount(Integer threadCount);
}
