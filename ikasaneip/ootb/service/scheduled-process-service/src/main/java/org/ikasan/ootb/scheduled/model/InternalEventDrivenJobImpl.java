package org.ikasan.ootb.scheduled.model;

import org.ikasan.spec.scheduled.context.model.ContextParameter;
import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJob;

import java.util.List;

public class InternalEventDrivenJobImpl implements InternalEventDrivenJob {

    private List<String> successfulReturnCodes;
    private String workingDirectory;
    private String commandLine;
    private long minExecutionTime;
    private long maxExecutionTime;
    private List<ContextParameter> contextParameters;
    private String contextId;
    protected List<String> childContextIds;
    private String identifier;
    private String agentName;
    private String jobName;
    private String jobDescription;
    private String startupControlType;

    @Override
    public List<String> getSuccessfulReturnCodes() {
        return this.successfulReturnCodes;
    }

    @Override
    public void setSuccessfulReturnCodes(List<String> successfulReturnCodes) {
        this.successfulReturnCodes = successfulReturnCodes;
    }

    @Override
    public String getWorkingDirectory() {
        return this.workingDirectory;
    }

    @Override
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public String getCommandLine() {
        return this.commandLine;
    }

    @Override
    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    public long getMinExecutionTime() {
        return this.minExecutionTime;
    }

    @Override
    public void setMinExecutionTime(long minExecutionTime) {
        this.minExecutionTime = minExecutionTime;
    }

    @Override
    public long getMaxExecutionTime() {
        return this.maxExecutionTime;
    }

    @Override
    public void setMaxExecutionTime(long maxExecutionTime) {
        this.maxExecutionTime = maxExecutionTime;
    }

    @Override
    public List<ContextParameter> getContextParameters() {
        return this.contextParameters;
    }

    @Override
    public void setContextParameters(List<ContextParameter> contextParameters) {
        this.contextParameters = contextParameters;
    }

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
        return this.identifier;
    }

    @Override
    public void setIdentifier(String jobIdentifier) {
        this.identifier = identifier;
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
        return this.jobDescription;
    }

    @Override
    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    @Override
    public String getStartupControlType() {
        return this.startupControlType;
    }

    @Override
    public void setStartupControlType(String startupControlType) {
        this.startupControlType = startupControlType;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("InternalEventDrivenJobImpl{");
        sb.append("successfulReturnCodes=").append(successfulReturnCodes);
        sb.append(", workingDirectory='").append(workingDirectory).append('\'');
        sb.append(", commandLine='").append(commandLine).append('\'');
        sb.append(", minExecutionTime=").append(minExecutionTime);
        sb.append(", maxExecutionTime=").append(maxExecutionTime);
        sb.append(", contextParameters=").append(contextParameters);
        sb.append(", contextId='").append(contextId).append('\'');
        if(childContextIds != null) {
            sb.append(", childContextIds=[ ");
            childContextIds.forEach(id -> sb.append("[").append(id).append("] "));
        }
        else {
            sb.append(", childContextIds='").append(this.childContextIds).append('\'');
        }
        sb.append("], identifier='").append(identifier).append('\'');
        sb.append(", agentName='").append(agentName).append('\'');
        sb.append(", jobName='").append(jobName).append('\'');
        sb.append(", jobDescription='").append(jobDescription).append('\'');
        sb.append(", startupControlType='").append(startupControlType).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
