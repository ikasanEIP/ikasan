package org.ikasan.rest.module.dto;

import java.io.Serializable;
import java.util.List;

public class ChangeFlowStateDto implements Serializable
{
    private String moduleName;
    private String flowName;
    private String action;
    private String username;

    public ChangeFlowStateDto(){

    }

    public ChangeFlowStateDto(String moduleName, String flowName, String action, String username)
    {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.action = action;
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

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
