package org.ikasan.spec.scheduled.dryrun;

import java.util.List;

public interface DryRunModeService<T> {

    void setDryRunMode(boolean dryRunMode);

    boolean getDryRunMode();

    void addDryRunFileList(List<T> dryRunFileList);

    String getJobFileName(String jobFileName);

    void setJobDryRun(String jobName, boolean isDryRun);

    boolean isJobDryRun(String jobName);
}
