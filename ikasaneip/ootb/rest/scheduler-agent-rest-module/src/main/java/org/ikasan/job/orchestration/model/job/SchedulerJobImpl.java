package org.ikasan.job.orchestration.model.job;


import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.util.List;
import java.util.Map;

public class SchedulerJobImpl implements SchedulerJob {
    protected String jobIdentifier;
    protected String agentName;
    protected String jobName;
    protected String contextName;
    protected List<String> childContextNames;
    protected String description;
    protected String startupControlType = "AUTOMATIC";
    protected Map<String, Boolean> skippedContexts;
    protected Map<String, Boolean> heldContexts;
    private int ordinal = -1;

    @Override
    public String getContextName() {
        return this.contextName;
    }

    @Override
    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    @Override
    public List<String> getChildContextNames() {
        return childContextNames;
    }

    @Override
    public void setChildContextNames(List<String> childContextNames) {
        this.childContextNames = childContextNames;
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
    public Map<String, Boolean> getSkippedContexts() {
        return skippedContexts;
    }

    @Override
    public void setSkippedContexts(Map<String, Boolean> skippedContexts) {
        this.skippedContexts = skippedContexts;
    }

    @Override
    public Map<String, Boolean> getHeldContexts() {
        return heldContexts;
    }

    @Override
    public void setHeldContexts(Map<String, Boolean> heldContexts) {
        this.heldContexts = heldContexts;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    @Override
    public String toString() {
        return "SchedulerJobImpl{" +
            "jobIdentifier='" + jobIdentifier + '\'' +
            ", agentName='" + agentName + '\'' +
            ", jobName='" + jobName + '\'' +
            ", contextId='" + contextName + '\'' +
            ", childContextIds=" + childContextNames +
            ", description='" + description + '\'' +
            ", startupControlType='" + startupControlType + '\'' +
            ", skippedContexts=" + skippedContexts +
            ", heldContexts=" + heldContexts +
            '}';
    }
}
