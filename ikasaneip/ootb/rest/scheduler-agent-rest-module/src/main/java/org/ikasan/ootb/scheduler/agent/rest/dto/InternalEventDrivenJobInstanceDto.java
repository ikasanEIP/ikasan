package org.ikasan.ootb.scheduler.agent.rest.dto;

import org.ikasan.spec.scheduled.context.model.ContextParameter;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.ikasan.spec.scheduled.instance.model.InstanceStatus;
import org.ikasan.spec.scheduled.instance.model.InternalEventDrivenJobInstance;

import java.util.List;
import java.util.Map;

public class InternalEventDrivenJobInstanceDto implements InternalEventDrivenJobInstance {

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
    private List<Integer> daysOfWeekToRun;
    private String contextInstanceId;
    private String childContextName;
    private boolean held = false;
    private boolean skip = false;
    private boolean initiationEventRaised = false;
    private InstanceStatus status;
    private ScheduledProcessEvent scheduledProcessEvent;
    private boolean targetResidingContextOnly;
    private boolean participatesInLock;
    private String executionEnvironmentProperties;

    private Map<String, Boolean> skippedContexts;

    private Map<String, Boolean> heldContexts;

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
    public String getContextName() {
        return this.contextId;
    }

    @Override
    public void setContextName(String contextName) {
        this.contextId = contextName;
    }

    @Override
    public List<String> getChildContextNames() {
        return childContextIds;
    }

    @Override
    public void setChildContextNames(List<String> childContextIds) {
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
        return startupControlType;
    }

    @Override
    public void setStartupControlType(String startupControlType) {
        this.startupControlType = startupControlType;
    }

    @Override
    public List<Integer> getDaysOfWeekToRun() {
        return this.daysOfWeekToRun;
    }

    @Override
    public void setDaysOfWeekToRun(List<Integer> daysOfWeekToRun) {
        this.daysOfWeekToRun = daysOfWeekToRun;
    }

    @Override
    public String getContextInstanceId() {
        return contextInstanceId;
    }

    @Override
    public void setContextInstanceId(String contextInstanceId) {
        this.contextInstanceId = contextInstanceId;
    }

    @Override
    public String getChildContextName() {
        return childContextName;
    }

    @Override
    public void setChildContextName(String childContextName) {
        this.childContextName = childContextName;
    }

    @Override
    public boolean isHeld() {
        return held;
    }

    @Override
    public void setHeld(boolean held) {
        this.held = held;
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
    public boolean isInitiationEventRaised() {
        return initiationEventRaised;
    }

    @Override
    public void setInitiationEventRaised(boolean initiationEventRaised) {
        this.initiationEventRaised = initiationEventRaised;
    }

    @Override
    public InstanceStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(InstanceStatus status) {
        this.status = status;
    }

    @Override
    public ScheduledProcessEvent getScheduledProcessEvent() {
        return scheduledProcessEvent;
    }

    @Override
    public void setScheduledProcessEvent(ScheduledProcessEvent scheduledProcessEvent) {
        this.scheduledProcessEvent = scheduledProcessEvent;
    }

    @Override
    public boolean isTargetResidingContextOnly() {
        return targetResidingContextOnly;
    }

    @Override
    public void setTargetResidingContextOnly(boolean targetResidingContextOnly) {
        this.targetResidingContextOnly = targetResidingContextOnly;
    }

    @Override
    public boolean isParticipatesInLock() {
        return participatesInLock;
    }

    @Override
    public void setParticipatesInLock(boolean participatesInLock) {
        this.participatesInLock = participatesInLock;
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
    public String getExecutionEnvironmentProperties() {
        return executionEnvironmentProperties;
    }

    @Override
    public void setExecutionEnvironmentProperties(String executionEnvironmentProperties) {
        this.executionEnvironmentProperties = executionEnvironmentProperties;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("InternalEventDrivenJobDto{");
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
        sb.append(", targetResidingContextOnly='").append(targetResidingContextOnly).append('\'');
        sb.append(", executionEnvironmentProperties='").append(executionEnvironmentProperties).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
