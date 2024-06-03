package org.ikasan.job.orchestration.model.job;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.ikasan.spec.scheduled.context.model.ContextParameter;
import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJob;

import java.util.ArrayList;
import java.util.List;

public class InternalEventDrivenJobImpl extends SchedulerJobImpl implements InternalEventDrivenJob {

    private List<String> successfulReturnCodes;
    private String workingDirectory;
    private String commandLine;
    private long minExecutionTime;
    private long maxExecutionTime;
    private List<ContextParameter> contextParameters = new ArrayList<>();
    private List<Integer> daysOfWeekToRun;
    private boolean targetResidingContextOnly;
    private boolean participatesInLock;
    private String executionEnvironmentProperties;
    private boolean repeatable;
    private boolean killed;

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
        return minExecutionTime;
    }

    @Override
    public void setMinExecutionTime(long minExecutionTime) {
        this.minExecutionTime = minExecutionTime;
    }

    @Override
    public long getMaxExecutionTime() {
        return maxExecutionTime;
    }

    @Override
    public void setMaxExecutionTime(long maxExecutionTime) {
        this.maxExecutionTime = maxExecutionTime;
    }

    @Override
    public List<ContextParameter> getContextParameters() {
        return contextParameters;
    }

    @Override
    public void setContextParameters(List<ContextParameter> contextParameters) {
        this.contextParameters = contextParameters;
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
    public String getExecutionEnvironmentProperties() {
        return executionEnvironmentProperties;
    }

    @Override
    public void setExecutionEnvironmentProperties(String executionEnvironmentProperties) {
        this.executionEnvironmentProperties = executionEnvironmentProperties;
    }

    @Override
    public void setJobRepeatable(boolean jobRepeatable) {
        this.repeatable = jobRepeatable;
    }

    @Override
    public boolean isJobRepeatable() {
        return this.repeatable;
    }

    @Override
    public boolean isKilled() {
        return killed;
    }

    @Override
    public void setKilled(boolean killed) {
        this.killed = killed;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
