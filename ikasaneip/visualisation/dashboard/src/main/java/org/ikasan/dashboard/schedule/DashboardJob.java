package org.ikasan.dashboard.schedule;

public interface DashboardJob {

    /**
     * Get the job name.
     *
     * @return
     */
    public String getJobName();

    /**
     * Get the cron expression.
     *
     * @return
     */
    public String getCronExpression();
}
