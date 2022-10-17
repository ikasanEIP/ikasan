package org.ikasan.spec.scheduled.job.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface QuartzScheduleDrivenJob extends SchedulerJob, Serializable {

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

    List<String> getBlackoutWindowCronExpressions();

    void setBlackoutWindowCronExpressions(List<String> blackoutWindowCronExpressions);

    Map<String,String> getBlackoutWindowDateTimeRanges();

    void setBlackoutWindowDateTimeRanges(Map<String,String> blackoutWindowDateTimeRanges);

    boolean isDropEventOnBlackout();

    void setDropEventOnBlackout(boolean isDropEventOnBlackout);
}
