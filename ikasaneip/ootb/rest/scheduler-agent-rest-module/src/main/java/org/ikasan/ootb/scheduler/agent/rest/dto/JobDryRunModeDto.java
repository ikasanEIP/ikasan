package org.ikasan.ootb.scheduler.agent.rest.dto;

import org.ikasan.spec.scheduled.dryrun.JobDryRunMode;

public class JobDryRunModeDto implements JobDryRunMode {
    private boolean dryRun;
    private String jobName;

    @Override
    public boolean isDryRun() {
        return this.dryRun;
    }

    @Override
    public void setIsDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    @Override
    public String getJobName() {
        return this.jobName;
    }

    @Override
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
