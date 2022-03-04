package org.ikasan.ootb.scheduler.agent.rest.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.ikasan.spec.scheduled.dryrun.DryRunFileListJobParameter;

public class DryRunFileListJobParameterDto implements DryRunFileListJobParameter {
    private String jobName;
    private String fileName;

    @Override
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
            }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
