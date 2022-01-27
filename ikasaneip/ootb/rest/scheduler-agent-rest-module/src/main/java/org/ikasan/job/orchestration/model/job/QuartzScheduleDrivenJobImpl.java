package org.ikasan.job.orchestration.model.job;

import org.ikasan.spec.scheduled.job.model.QuartzScheduleDrivenJob;

public class QuartzScheduleDrivenJobImpl extends SchedulerJobImpl implements QuartzScheduleDrivenJob {

    protected String cronExpression;
    protected String jobGroup;
    protected String timeZone;


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
    public String toString() {
        final StringBuffer sb = new StringBuffer("QuartzScheduleDrivenJobImpl{");
        sb.append("cronExpression='").append(cronExpression).append('\'');
        sb.append(", jobGroup='").append(jobGroup).append('\'');
        sb.append(", timeZone='").append(timeZone).append('\'');
        sb.append(", jobIdentifier='").append(jobIdentifier).append('\'');
        sb.append(", agentName='").append(agentName).append('\'');
        sb.append(", jobName='").append(jobName).append('\'');
        sb.append(", contextId='").append(contextId).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
