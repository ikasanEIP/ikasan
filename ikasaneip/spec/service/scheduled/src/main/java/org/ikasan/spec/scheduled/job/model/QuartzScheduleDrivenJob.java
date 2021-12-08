package org.ikasan.spec.scheduled.job.model;

public interface QuartzScheduleDrivenJob extends SchedulerJob {

     String getCronExpression();

     void setCronExpression(String cronExpression);
}
