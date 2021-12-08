package org.ikasan.ootb.scheduler.agent.rest.dto;

import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;

import java.util.List;

public class SchedulerJobInitiationEventDto implements SchedulerJobInitiationEvent<ContextParameterDto, InternalEventDrivenJobDto> {

    private String agentName;
    private String jobName;

    @Override
    public InternalEventDrivenJobDto getInternalEventDrivenJob() {
        return null;
    }

    @Override
    public void setInternalEventDrivenJob(InternalEventDrivenJobDto internalEventDrivenJob) {

    }

    @Override
    public void setContextParameters(List<ContextParameterDto> contextParameters) {

    }

    @Override
    public List<ContextParameterDto> getContextParameters() {
        return null;
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
    public String getContextId() {
        return null;
    }

    @Override
    public void setContextId(String contextId) {

    }

    @Override
    public String getContextInstanceId() {
        return null;
    }

    @Override
    public void setContextInstanceId(String contextInstanceId) {

    }

    @Override
    public boolean isDryRun() {
        return false;
    }

    @Override
    public void setDryRun(boolean dryRun) {

    }
}
