package org.ikasan.rest.module.dto;

import java.io.Serializable;
import java.util.StringJoiner;

public class TriggerDto implements Serializable
{
    private Long id;
    private String moduleName;
    private String flowName;
    private String flowElementName;
    private String relationship;
    private String jobType;
    private String timeToLive;
    private String userName;

    public TriggerDto()
    {
    }

    public TriggerDto(String moduleName, String flowName, String flowElementName, String relationship, String jobType,
                      String timeToLive)
    {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.flowElementName = flowElementName;
        this.relationship = relationship;
        this.jobType = jobType;
        this.timeToLive = timeToLive;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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

    public String getFlowElementName()
    {
        return flowElementName;
    }

    public void setFlowElementName(String flowElementName)
    {
        this.flowElementName = flowElementName;
    }

    public String getRelationship()
    {
        return relationship;
    }

    public void setRelationship(String relationship)
    {
        this.relationship = relationship;
    }

    public String getJobType()
    {
        return jobType;
    }

    public void setJobType(String jobType)
    {
        this.jobType = jobType;
    }

    public String getTimeToLive()
    {
        return timeToLive;
    }

    public void setTimeToLive(String timeToLive)
    {
        this.timeToLive = timeToLive;
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
        return new StringJoiner(", ", TriggerDto.class.getSimpleName() + "[", "]")
            .add("moduleName='" + moduleName + "'").add("flowName='" + flowName + "'")
            .add("flowElementName='" + flowElementName + "'").add("relationship='" + relationship + "'")
            .add("jobType='" + jobType + "'").add("timeToLive='" + timeToLive + "'").toString();
    }
}
