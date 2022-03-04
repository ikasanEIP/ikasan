package org.ikasan.ootb.scheduler.agent.rest.dto;

import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;

import java.util.List;

public class SchedulerJobInitiationEventDto implements SchedulerJobInitiationEvent<ContextParameterDto, InternalEventDrivenJobDto, DryRunParametersDto> {

    private String agentName;
    private String agentUrl;
    private String jobName;
    private List<ContextParameterDto> contextParameters;
    private InternalEventDrivenJobDto internalEventDrivenJob;
    private String contextId;
    private List<String> childContextId;
    private String contextInstanceId;
    private boolean dryRun;
    private DryRunParametersDto dryRunParametersDto;
    private boolean skipped;

    @Override
    public InternalEventDrivenJobDto getInternalEventDrivenJob() {
        return this.internalEventDrivenJob;
    }

    @Override
    public void setInternalEventDrivenJob(InternalEventDrivenJobDto internalEventDrivenJob) {
        this.internalEventDrivenJob = internalEventDrivenJob;
    }

    @Override
    public void setContextParameters(List<ContextParameterDto> contextParameters) {
        this.contextParameters = contextParameters;
    }

    @Override
    public List<ContextParameterDto> getContextParameters() {
        return this.contextParameters;
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
    public String getAgentUrl() {
        return agentUrl;
    }

    @Override
    public void setAgentUrl(String agentUrl) {
        this.agentUrl = agentUrl;
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
    public String getContextId() {
        return this.contextId;
    }

    @Override
    public void setContextId(String contextId) {
        this.contextId = contextId;
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
    public List<String> getChildContextIds() {
        return childContextId;
    }

    @Override
    public void setChildContextIds(List<String> childContextId) {
        this.childContextId = childContextId;
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
    public void setDryRunParameters(DryRunParametersDto dryRunParametersDto) {
        this.dryRunParametersDto = dryRunParametersDto;
    }

    @Override
    public DryRunParametersDto getDryRunParameters() {
        return this.dryRunParametersDto;
    }

    @Override
    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    @Override
    public boolean isSkipped() {
        return this.skipped;
    }
}
