package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.util.Map;

public interface QuartzScheduleDrivenJobInstance extends SchedulerJobInstance {

    String getJobGroup();

    void setJobGroup(String jobGroup);

    String getCronExpression();

    void setCronExpression(String cronExpression);

    String getTimeZone();

    void setTimeZone(String timeZone);

    Map<String, String> getPassthroughProperties();

    void setPassthroughProperties(Map<String, String> passthroughProperties);

    boolean isEager();

    int getMaxEagerCallbacks();

    void setMaxEagerCallbacks(int maxEagerCallbacks);

    void setEager(boolean eager);

    void setIgnoreMisfire(boolean ignoreMisfire);

    boolean isIgnoreMisfire();

    long getRecoveryTolerance();

    void setRecoveryTolerance(long recoveryTolerance);

    boolean isPersistentRecovery();

    void setPersistentRecovery(boolean persistentRecovery);
}
