package org.ikasan.job.orchestration.model.context;

import java.util.List;
import java.util.Objects;

import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunParametersDto;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;
import org.ikasan.spec.scheduled.instance.model.InternalEventDrivenJobInstance;

public class SchedulerJobInitiationEventImpl implements SchedulerJobInitiationEvent<ContextParameterInstanceImpl, InternalEventDrivenJobInstance, DryRunParametersDto> {
    private String agentName;
    private String agentUrl;
    private String jobName;
    private InternalEventDrivenJobInstance internalEventDrivenJob;
    private String contextId;
    private List<String> childContextIds;
    private String contextInstanceId;
    private List<ContextParameterInstanceImpl> contextParameters;
    private boolean dryRun = false;
    private DryRunParametersDto dryRunParameters;
    private boolean skipped = false;
    private ScheduledProcessEvent catalystEvent;

    @Override
    public String getAgentName() {
        return agentName;
    }

    @Override
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public String getAgentUrl() {
        return agentUrl;
    }

    @Override
    public void setAgentUrl(String agentUrl) {
        this.agentUrl = agentUrl;
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
    public String getContextName() {
        return contextId;
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
    public String getContextInstanceId() {
        return contextInstanceId;
    }

    @Override
    public void setContextInstanceId(String contextInstanceId) {
        this.contextInstanceId = contextInstanceId;
    }

    @Override
    public InternalEventDrivenJobInstance getInternalEventDrivenJob() {
        return internalEventDrivenJob;
    }

    @Override
    public void setInternalEventDrivenJob(InternalEventDrivenJobInstance internalEventDrivenJob) {
        this.internalEventDrivenJob = internalEventDrivenJob;
    }

    @Override
    public void setContextParameters(List<ContextParameterInstanceImpl> contextParameters) {
        this.contextParameters = contextParameters;
    }

    @Override
    public List<ContextParameterInstanceImpl> getContextParameters() {
        return this.contextParameters;
    }

    @Override
    public boolean isDryRun() {
        return dryRun;
    }

    @Override
    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    @Override
    public void setDryRunParameters(DryRunParametersDto dryRunParameters) {
        this.dryRunParameters = dryRunParameters;
    }

    @Override
    public DryRunParametersDto getDryRunParameters() {
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
    public ScheduledProcessEvent getCatalystEvent() {
        return catalystEvent;
    }

    @Override
    public void setCatalystEvent(ScheduledProcessEvent catalystEvent) {
        this.catalystEvent = catalystEvent;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SchedulerJobInitiationEventImpl{");
        sb.append("agentName='").append(agentName).append('\'');
        sb.append(", agentUrl='").append(agentUrl).append('\'');
        sb.append(", jobName='").append(jobName).append('\'');
        sb.append(", internalEventDrivenJob=").append(internalEventDrivenJob);
        sb.append(", contextId='").append(contextId).append('\'');
        if(childContextIds != null) {
            sb.append(", childContextIds=[ ");
            childContextIds.forEach(id -> sb.append("[").append(id).append("] "));
        }
        else {
            sb.append(", childContextIds='").append(this.childContextIds).append('\'');
        }
        sb.append("], contextInstanceId='").append(contextInstanceId).append('\'');
        sb.append(", contextParameters=").append(contextParameters);
        sb.append(", dryRun=").append(dryRun);
        sb.append(", dryRunParameters=").append(dryRunParameters);
        sb.append(", skipped=").append(skipped);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchedulerJobInitiationEventImpl that = (SchedulerJobInitiationEventImpl) o;
        return dryRun == that.dryRun &&
            skipped == that.skipped &&
            Objects.equals(agentName, that.agentName) &&
            Objects.equals(agentUrl, that.agentUrl) &&
            Objects.equals(jobName, that.jobName) &&
            Objects.equals(internalEventDrivenJob, that.internalEventDrivenJob) &&
            Objects.equals(contextId, that.contextId) &&
            Objects.equals(childContextIds, that.childContextIds) &&
            Objects.equals(contextInstanceId, that.contextInstanceId) &&
            Objects.equals(contextParameters, that.contextParameters) &&
            Objects.equals(dryRunParameters, that.dryRunParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentName, agentUrl, jobName, internalEventDrivenJob, contextId, childContextIds
            , contextInstanceId, contextParameters, dryRun, dryRunParameters, skipped);
    }
}
