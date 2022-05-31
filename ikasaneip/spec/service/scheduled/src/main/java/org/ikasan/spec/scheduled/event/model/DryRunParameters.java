package org.ikasan.spec.scheduled.event.model;

import java.io.Serializable;

public interface DryRunParameters extends Serializable {

    /**
     * Get the minimum time the execution thread will sleep for.
     *
     * @return
     */
    public long getMinExecutionTimeMillis();

    /**
     * Set the minimum time the execution thread will sleep for.
     *
     * @param minExecutionTimeMillis
     */
    public void setMinExecutionTimeMillis(long minExecutionTimeMillis);

    /**
     * Get the maximum time the execution thread will sleep for.
     *
     * @return
     */
    public long getMaxExecutionTimeMillis();

    /**
     * Set the maximum time the execution thread will sleep for.
     *
     * @param maxExecutionTimeMillis
     */
    public void setMaxExecutionTimeMillis(long maxExecutionTimeMillis);

    /**
     * Set the fixed time the execution thread will sleep for.
     *
     * @return
     */
    public long getFixedExecutionTimeMillis();

    /**
     * Set the fixed time the execution thread will sleep for.
     *
     * @param fixedExecutionTimeMillis
     */
    public void setFixedExecutionTimeMillis(long fixedExecutionTimeMillis);

    /**
     * Get the double that represents the percentage probability that a job will fail.
     *
     * @return
     */
    public double getJobErrorPercentage();

    /**
     * Set the double that represents the percentage probability that a job will fail.
     *
     * @param jobErrorPercentage
     */
    public void setJobErrorPercentage(double jobErrorPercentage);

    /**
     * Flag to force a job to fail.
     *
     * @return
     */
    boolean isError();

    /**
     * Set the flag to indicate that the job will fail.
     *
     * @param dryRun
     */
    void setError(boolean dryRun);
}
