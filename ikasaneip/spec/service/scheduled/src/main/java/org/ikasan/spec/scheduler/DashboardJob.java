package org.ikasan.spec.scheduler;

import org.quartz.Job;

public interface DashboardJob extends Job {

    /**
     * Get the job name.
     *
     * @return
     */
    String getJobName();

    /**
     * Get the cron expression.
     *
     * @return
     */
    String getCronExpression();
}
