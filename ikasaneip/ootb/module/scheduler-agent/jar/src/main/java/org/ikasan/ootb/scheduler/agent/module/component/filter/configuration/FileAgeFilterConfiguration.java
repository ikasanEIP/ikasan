package org.ikasan.ootb.scheduler.agent.module.component.filter.configuration;

public class FileAgeFilterConfiguration {
    private int fileAgeSeconds;
    private String jobName;

    public int getFileAgeSeconds() {
        return fileAgeSeconds;
    }

    public void setFileAgeSeconds(int fileAgeSeconds) {
        this.fileAgeSeconds = fileAgeSeconds;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
