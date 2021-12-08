package org.ikasan.spec.scheduled.job.model;

public interface FileEventDrivenJob extends SchedulerJob {
    String getCronExpression();

    void setCronExpression(String cronExpression);

    String getFilePath();

    void setFilePath(String path);
}
