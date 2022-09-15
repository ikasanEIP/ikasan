package org.ikasan.job.orchestration.model.job;


import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.util.List;

public class SchedulerJobImpl implements SchedulerJob {
    protected String jobIdentifier;
    protected String agentName;
    protected String jobName;
    protected String contextId;
    protected List<String> childContextIds;
    protected String description;
    protected String startupControlType = "AUTOMATIC";
    protected boolean skip;

    @Override
    public String getContextId() {
        return this.contextId;
    }

    @Override
    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    @Override
    public List<String> getChildContextIds() {
        return childContextIds;
    }

    @Override
    public void setChildContextIds(List<String> childContextIds) {
        this.childContextIds = childContextIds;
    }

    @Override
    public String getIdentifier() {
        return this.jobIdentifier;
    }

    @Override
    public void setIdentifier(String jobIdentifier) {
        this.jobIdentifier = jobIdentifier;
    }

    @Override
    public String getAgentName() {
        return this.agentName;
    }

    @Override
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public String getJobName() {
        return this.jobName;
    }

    @Override
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Override
    public String getJobDescription() {
        return this.description;
    }

    @Override
    public void setJobDescription(String jobDescription) {
        this.description = jobDescription;
    }

    @Override
    public String getStartupControlType() {
        return startupControlType;
    }

    @Override
    public void setStartupControlType(String startupControlType) {
        this.startupControlType = startupControlType;
    }

    @Override
    public boolean isSkip() {
        return skip;
    }

    @Override
    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SchedulerJobImpl{");
        sb.append("jobIdentifier='").append(jobIdentifier).append('\'');
        sb.append(", agentName='").append(agentName).append('\'');
        sb.append(", jobName='").append(jobName).append('\'');
        sb.append(", contextId='").append(contextId).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", startupControlType='").append(startupControlType).append('\'');
        sb.append(", skip='").append(skip).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
