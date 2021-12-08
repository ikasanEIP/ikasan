package org.ikasan.ootb.scheduled.model;

import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJob;
import org.ikasan.spec.scheduled.event.model.SchedulerJobInitiationEvent;

import java.util.List;

public class SchedulerJobInitiationEventImpl implements SchedulerJobInitiationEvent {
    @Override
    public InternalEventDrivenJob getInternalEventDrivenJob() {
        return null;
    }

    @Override
    public void setInternalEventDrivenJob(InternalEventDrivenJob internalEventDrivenJob) {

    }

    @Override
    public void setContextParameters(List contextParameters) {

    }

    @Override
    public List getContextParameters() {
        return null;
    }

    @Override
    public String getAgentName() {
        return null;
    }

    @Override
    public void setAgentName(String agentName) {

    }

    @Override
    public String getJobName() {
        return null;
    }

    @Override
    public void setJobName(String jobName) {

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
