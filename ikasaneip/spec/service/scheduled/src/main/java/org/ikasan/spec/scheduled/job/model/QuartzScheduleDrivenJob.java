package org.ikasan.spec.scheduled.job.model;

public interface QuartzScheduleDrivenJob extends SchedulerJob {

    String getJobGroup();

    void setJobGroup(String jobGroup);

    String getCronExpression();

    void setCronExpression(String cronExpression);

    String getTimeZone();

    void setTimeZone(String timeZone);
}
