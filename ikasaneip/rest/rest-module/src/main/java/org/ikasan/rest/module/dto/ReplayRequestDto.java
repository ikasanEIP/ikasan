package org.ikasan.rest.module.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.StringJoiner;

public class ReplayRequestDto implements Serializable
{
    private String moduleName;
    private String flowName;
    private byte[] event;
    private String userName;

    public ReplayRequestDto(){

    }

    public ReplayRequestDto(String moduleName, String flowName, byte[] event)
    {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.event = event;
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

    public byte[] getEvent()
    {
        return event;
    }

    public void setEvent(byte[] event)
    {
        this.event = event;
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
        return new StringJoiner(", ", ReplayRequestDto.class.getSimpleName() + "[", "]")
            .add("moduleName='" + moduleName + "'").add("flowName='" + flowName + "'")
            .add("event=" + Arrays.toString(event)).toString();
    }
}
