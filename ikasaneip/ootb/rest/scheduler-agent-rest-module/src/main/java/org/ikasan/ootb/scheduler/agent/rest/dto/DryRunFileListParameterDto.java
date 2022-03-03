package org.ikasan.ootb.scheduler.agent.rest.dto;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.ikasan.spec.scheduled.event.model.DryRunFileListParameter;

public class DryRunFileListParameterDto implements DryRunFileListParameter<DryRunFileListJobParameterDto> {

    private List<DryRunFileListJobParameterDto> fileList;

    @Override
    public List<DryRunFileListJobParameterDto> getFileList() {
        return fileList;
    }

    @Override
    public void setFileList(List<DryRunFileListJobParameterDto> fileList) {
        this.fileList = fileList;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
