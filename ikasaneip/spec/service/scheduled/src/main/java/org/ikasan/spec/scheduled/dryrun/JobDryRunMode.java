package org.ikasan.spec.scheduled.dryrun;

public interface JobDryRunMode {

    boolean isDryRun();

    void setIsDryRun(boolean dryRun);

    String getJobName();

    void setJobName(String jobName);
}
