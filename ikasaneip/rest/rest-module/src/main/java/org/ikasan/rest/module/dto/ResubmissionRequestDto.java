package org.ikasan.rest.module.dto;

import java.io.Serializable;
import java.util.StringJoiner;

public class ResubmissionRequestDto implements Serializable
{
    private String moduleName;
    private String flowName;
    private String errorUri;
    private String action;
    private String userName;

    public ResubmissionRequestDto(){

    }

    public ResubmissionRequestDto(String moduleName, String flowName, String errorUri, String action)
    {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.errorUri = errorUri;
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

    public String getErrorUri()
    {
        return errorUri;
    }

    public void setErrorUri(String errorUri)
    {
        this.errorUri = errorUri;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    @Override
    public String toString()
    {
        return new StringJoiner(", ", ResubmissionRequestDto.class.getSimpleName() + "[", "]")
            .add("moduleName='" + moduleName + "'").add("flowName='" + flowName + "'")
            .add("errorUri='" + errorUri + "'").add("action='" + action + "'").toString();
    }
}
