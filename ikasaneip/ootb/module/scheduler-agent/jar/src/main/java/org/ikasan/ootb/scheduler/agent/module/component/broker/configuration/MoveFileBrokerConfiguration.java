package org.ikasan.ootb.scheduler.agent.module.component.broker.configuration;

public class MoveFileBrokerConfiguration {

    private String moveDirectory;
    private String jobName;

    public String getMoveDirectory() {
        return moveDirectory;
    }

    public void setMoveDirectory(String moveDirectory) {
        this.moveDirectory = moveDirectory;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
