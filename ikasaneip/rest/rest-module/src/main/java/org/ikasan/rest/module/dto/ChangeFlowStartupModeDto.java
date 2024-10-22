package org.ikasan.rest.module.dto;

import java.io.Serializable;

public class ChangeFlowStartupModeDto implements Serializable
{
    private String moduleName;
    private String flowName;
    private String startupType;
    private String comment;
    private String username;

    public ChangeFlowStartupModeDto()
    {
    }

    public ChangeFlowStartupModeDto(String moduleName, String flowName, String startupType, String comment, String username)
    {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.startupType = startupType;
        this.comment = comment;
        this.username = username;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public String getFlowName()
    {
        return flowName;
    }

    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    public String getStartupType()
    {
        return startupType;
    }

    public void setStartupType(String startupType)
    {
        this.startupType = startupType;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
