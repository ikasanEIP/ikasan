package org.ikasan.ootb.scheduler.agent.rest.client.model;

import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.DryRunParameters;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.ikasan.spec.scheduled.instance.model.InternalEventDrivenJobInstance;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class ContextualisedScheduledProcessEventImpl implements ContextualisedScheduledProcessEvent<String, DryRunParameters> {
    private Long id;
    private String agentName;
    private String agentHostname;
    private String jobName;
    private String jobGroup;
    private String jobDescription;
    private String commandLine;
    private int returnCode;
    private boolean successful;
    private String outcome;
    private String resultOutput;
    private String resultError;
    private long pid;
    private String user;
    private long fireTime;
    private long nextFireTime;
    private long completionTime;
    private boolean dryRun = false;
    private String contextName;
    private List<String> childContextNames;
    private String contextInstanceId;
    private boolean jobStarting = false;
    private DryRunParameters dryRunParameters;
    private boolean skipped;
    private InternalEventDrivenJobInstance internalEventDrivenJob;
    private boolean raisedDueToFailureResubmission;
    private String executionDetails;
    private ScheduledProcessEvent catalystEvent;
    private Map<String, String> jobExecutionOutputParameters;

    public Long getId()
    {
        return id;
    }

    private void setId(Long id)
    {
        this.id = id;
    }

    @Override
    public String getAgentName() {
        return agentName;
    }

    @Override
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public String getAgentHostname() {
        return agentHostname;
    }

    @Override
    public void setAgentHostname(String agentHostname) {
        this.agentHostname = agentHostname;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Override
    public String getJobGroup() {
        return jobGroup;
    }

    @Override
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
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
    public String getCommandLine() {
        return commandLine;
    }

    @Override
    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    public String getResultOutput() {
        return resultOutput;
    }

    @Override
    public void setResultOutput(String resultOutput) {
        this.resultOutput = resultOutput;
    }

    @Override
    public String getResultError() {
        return resultError;
    }

    @Override
    public void setResultError(String resultError) {
        this.resultError = resultError;
    }

    @Override
    public long getPid() {
        return pid;
    }

    @Override
    public void setPid(long pid) {
        this.pid = pid;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public long getFireTime() {
        return fireTime;
    }

    @Override
    public void setFireTime(long fireTime) {
        this.fireTime = fireTime;
    }

    @Override
    public long getNextFireTime() {
        return nextFireTime;
    }

    @Override
    public void setNextFireTime(long nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    @Override
    public int getReturnCode()
    {
        return returnCode;
    }

    @Override
    public void setReturnCode(int returnCode)
    {
        this.returnCode = returnCode;
    }

    @Override
    public boolean isSuccessful()
    {
        return successful;
    }

    @Override
    public void setSuccessful(boolean successful)
    {
        this.successful = successful;
    }

    @Override
    public String getOutcome()
    {
        return outcome;
    }

    @Override
    public void setOutcome(String outcome)
    {
        this.outcome = outcome;
    }

    @Override
    public long getCompletionTime()
    {
        return completionTime;
    }

    @Override
    public void setCompletionTime(long completionTime)
    {
        this.completionTime = completionTime;
    }

    @Override
    public boolean isDryRun() {
        return this.dryRun;
    }

    @Override
    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

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
    public String getContextInstanceId() {
        return this.contextInstanceId;
    }

    @Override
    public void setContextInstanceId(String contextInstanceId) {
        this.contextInstanceId = contextInstanceId;
    }

    @Override
    public boolean isJobStarting() {
        return this.jobStarting;
    }

    @Override
    public void setJobStarting(boolean jobStarting) {
        this.jobStarting = jobStarting;
    }

    @Override
    public void setDryRunParameters(DryRunParameters dryRunParameters) {
        this.dryRunParameters = dryRunParameters;
    }

    @Override
    public DryRunParameters getDryRunParameters() {
        return this.dryRunParameters;
    }

    @Override
    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    @Override
    public boolean isSkipped() {
        return skipped;
    }

    @Override
    public void setInternalEventDrivenJob(InternalEventDrivenJobInstance internalEventDrivenJob) {
        this.internalEventDrivenJob = internalEventDrivenJob;
    }

    @Override
    public InternalEventDrivenJobInstance getInternalEventDrivenJob() {
        return this.internalEventDrivenJob;
    }

    @Override
    public boolean isRaisedDueToFailureResubmission() {
        return raisedDueToFailureResubmission;
    }

    @Override
    public void setRaisedDueToFailureResubmission(boolean raisedDueToFailureResubmission) {
        this.raisedDueToFailureResubmission = raisedDueToFailureResubmission;
    }

    @Override
    public String getExecutionDetails() {
        return executionDetails;
    }

    @Override
    public void setExecutionDetails(String executionDetails) {
        this.executionDetails = executionDetails;
    }

    @Override
    public ScheduledProcessEvent getCatalystEvent() {
        return catalystEvent;
    }

    @Override
    public void setCatalystEvent(ScheduledProcessEvent catalystEvent) {
        this.catalystEvent = catalystEvent;
    }

    @Override
    public Map<String, String> getJobExecutionOutputParameters() {
        return jobExecutionOutputParameters;
    }

    @Override
    public void setJobExecutionOutputParameters(Map<String, String> jobExecutionOutputParameters) {
        this.jobExecutionOutputParameters = jobExecutionOutputParameters;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ContextualisedScheduledProcessEventImpl.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("agentName='" + agentName + "'")
            .add("agentHostname='" + agentHostname + "'")
            .add("jobName='" + jobName + "'")
            .add("jobGroup='" + jobGroup + "'")
            .add("jobDescription='" + jobDescription + "'")
            .add("commandLine='" + commandLine + "'")
            .add("returnCode=" + returnCode)
            .add("successful=" + successful)
            .add("outcome='" + outcome + "'")
            .add("resultOutput='" + resultOutput + "'")
            .add("resultError='" + resultError + "'")
            .add("pid=" + pid)
            .add("user='" + user + "'")
            .add("fireTime=" + fireTime)
            .add("nextFireTime=" + nextFireTime)
            .add("completionTime=" + completionTime)
            .add("dryRun=" + dryRun)
            .add("contextName='" + contextName + "'")
            .add("childContextNames=" + childContextNames)
            .add("contextInstanceId='" + contextInstanceId + "'")
            .add("jobStarting=" + jobStarting)
            .add("dryRunParameters=" + dryRunParameters)
            .add("skipped=" + skipped)
            .add("internalEventDrivenJob=" + internalEventDrivenJob)
            .add("raisedDueToFailureResubmission=" + raisedDueToFailureResubmission)
            .add("executionDetails='" + executionDetails + "'")
            .add("catalystEvent=" + catalystEvent)
            .add("jobExecutionOutputParameters=" + jobExecutionOutputParameters)
            .toString();
    }
}
