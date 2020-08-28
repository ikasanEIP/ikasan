package org.ikasan.rest.client.dto;

import java.io.Serializable;

public class FlowStartupTypeDto implements Serializable
{
    private String moduleName;
    private String flowName;
    private String startupType;
    private String comment;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getStartupType() {
        return startupType;
    }

    public void setStartupType(String startupType) {
        this.startupType = startupType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FlowStartupTypeDto{");
        sb.append("moduleName='").append(moduleName).append('\'');
        sb.append(", flowName='").append(flowName).append('\'');
        sb.append(", startupType='").append(startupType).append('\'');
        sb.append(", comment='").append(comment).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
