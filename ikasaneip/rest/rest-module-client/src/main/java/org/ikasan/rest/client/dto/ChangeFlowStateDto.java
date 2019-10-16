package org.ikasan.rest.client.dto;

import java.io.Serializable;

public class ChangeFlowStateDto implements Serializable
{
    private String moduleName;
    private String flowName;
    private String action;

    public ChangeFlowStateDto(){

    }

    public ChangeFlowStateDto(String moduleName, String flowName, String action)
    {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.action = action;
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


}
