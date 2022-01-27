package org.ikasan.job.orchestration.model.job;

import org.ikasan.spec.scheduled.job.model.FileEventDrivenJob;

public class FileEventDrivenJobImpl extends QuartzScheduleDrivenJobImpl implements FileEventDrivenJob {

    private String filePath;

    @Override
    public String getFilePath() {
        return this.filePath;
    }

    @Override
    public void setFilePath(String path) {
        this.filePath = path;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FileEventDrivenJobImpl{");
        sb.append("filePath='").append(filePath).append('\'');
        sb.append(", cronExpression='").append(cronExpression).append('\'');
        sb.append(", jobGroup='").append(jobGroup).append('\'');
        sb.append(", timeZone='").append(timeZone).append('\'');
        sb.append(", jobIdentifier='").append(jobIdentifier).append('\'');
        sb.append(", agentName='").append(agentName).append('\'');
        sb.append(", jobName='").append(jobName).append('\'');
        sb.append(", contextId='").append(contextId).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
