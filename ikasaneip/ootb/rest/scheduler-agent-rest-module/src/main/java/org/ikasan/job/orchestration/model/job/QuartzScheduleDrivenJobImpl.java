package org.ikasan.job.orchestration.model.job;

import org.ikasan.spec.scheduled.job.model.QuartzScheduleDrivenJob;

import java.util.HashMap;
import java.util.Map;

public class QuartzScheduleDrivenJobImpl extends SchedulerJobImpl implements QuartzScheduleDrivenJob {

    protected String cronExpression;
    protected String jobGroup;
    protected String timeZone;

    /** whether to ignore a misfire - default true */
    private boolean ignoreMisfire = true;

    /** Determines whether consumer will be eagerly executing after successful run */
    private boolean eager = false;

    /** maximum number of consecutive eager scheduled callbacks before reverting to business schedule - default 0 = unlimited */
    private int maxEagerCallbacks;

    /** generic properties to be passed into the job at schedule time and subsequently passed back on schedule execution */
    private Map<String,String> passthroughProperties = new HashMap<String,String>();

    /** allow for persistent recovery of a schedule - default true */
    private boolean persistentRecovery = true;

    /** tolerance period in millis within which it makes sense to rerun a schedule if it was missed - default 30 minutes */
    private long recoveryTolerance = 30 * 60 * 1000;


    @Override
    public String getCronExpression() {
        return this.cronExpression;
    }

    @Override
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @Override
    public String getJobGroup() {
        return this.jobGroup;
    }

    @Override
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    @Override
    public String getTimeZone() {
        return this.timeZone;
    }

    @Override
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public boolean isIgnoreMisfire() {
        return ignoreMisfire;
    }

    @Override
    public void setIgnoreMisfire(boolean ignoreMisfire) {
        this.ignoreMisfire = ignoreMisfire;
    }

    @Override
    public boolean isEager() {
        return eager;
    }

    @Override
    public void setEager(boolean eager) {
        this.eager = eager;
    }

    @Override
    public int getMaxEagerCallbacks() {
        return maxEagerCallbacks;
    }

    @Override
    public void setMaxEagerCallbacks(int maxEagerCallbacks) {
        this.maxEagerCallbacks = maxEagerCallbacks;
    }

    @Override
    public Map<String, String> getPassthroughProperties() {
        return passthroughProperties;
    }

    @Override
    public void setPassthroughProperties(Map<String, String> passthroughProperties) {
        this.passthroughProperties = passthroughProperties;
    }

    @Override
    public boolean isPersistentRecovery() {
        return persistentRecovery;
    }

    @Override
    public void setPersistentRecovery(boolean persistentRecovery) {
        this.persistentRecovery = persistentRecovery;
    }

    @Override
    public long getRecoveryTolerance() {
        return recoveryTolerance;
    }

    @Override
    public void setRecoveryTolerance(long recoveryTolerance) {
        this.recoveryTolerance = recoveryTolerance;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("QuartzScheduleDrivenJobImpl{");
        sb.append("cronExpression='").append(cronExpression).append('\'');
        sb.append(", jobGroup='").append(jobGroup).append('\'');
        sb.append(", timeZone='").append(timeZone).append('\'');
        sb.append(", ignoreMisfire=").append(ignoreMisfire);
        sb.append(", eager=").append(eager);
        sb.append(", maxEagerCallbacks=").append(maxEagerCallbacks);
        sb.append(", passthroughProperties=").append(passthroughProperties);
        sb.append(", persistentRecovery=").append(persistentRecovery);
        sb.append(", recoveryTolerance=").append(recoveryTolerance);
        sb.append(", jobIdentifier='").append(jobIdentifier).append('\'');
        sb.append(", agentName='").append(agentName).append('\'');
        sb.append(", jobName='").append(jobName).append('\'');
        sb.append(", contextId='").append(contextName).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", startupControlType='").append(startupControlType).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
