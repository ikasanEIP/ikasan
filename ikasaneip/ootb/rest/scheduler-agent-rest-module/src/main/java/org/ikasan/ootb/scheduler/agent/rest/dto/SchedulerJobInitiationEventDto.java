package org.ikasan.ootb.scheduler.agent.rest.dto;

import org.ikasan.spec.scheduled.SchedulerJobInitiationEvent;

import java.util.List;

public class SchedulerJobInitiationEventDto implements SchedulerJobInitiationEvent<ContextParameterDto, InternalEventDrivenJobDto> {

    @Override
    public String getAgentName() {
        return null;
    }

    @Override
    public String getJobName() {
        return null;
    }

    @Override
    public InternalEventDrivenJobDto getInternalEventDrivenJob() {
        return null;
    }

    @Override
    public void setAgentName(String agentName) {

    }

    @Override
    public void setJobName(String jobName) {

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
}
