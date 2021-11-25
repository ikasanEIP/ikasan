package org.ikasan.spec.scheduled;

import java.util.List;

public interface SchedulerJobInitiationEvent<CONTEXT_PARAM extends ContextParameter, JOB extends InternalEventDrivenJob> {

    public String getAgentName();

    public String getJobName();

    public JOB getInternalEventDrivenJob();

    public void setAgentName(String agentName);

    public void setJobName(String jobName);

    public void setInternalEventDrivenJob(JOB internalEventDrivenJob);

    public void setContextParameters(List<CONTEXT_PARAM> contextParameters);

    public List<CONTEXT_PARAM> getContextParameters();
}
