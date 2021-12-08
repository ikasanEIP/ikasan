package org.ikasan.spec.scheduled.event.model;

import org.ikasan.spec.scheduled.context.model.ContextParameterInstance;
import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJob;

import java.util.List;

public interface SchedulerJobInitiationEvent<CONTEXT_PARAM extends ContextParameterInstance, JOB extends InternalEventDrivenJob> {

    JOB getInternalEventDrivenJob();

    void setInternalEventDrivenJob(JOB internalEventDrivenJob);

    void setContextParameters(List<CONTEXT_PARAM> contextParameters);

    List<CONTEXT_PARAM> getContextParameters();

    String getAgentName();

    void setAgentName(String agentName);

    String getJobName();

    void setJobName(String jobName);

    String getContextId();

    void setContextId(String contextId);

    String getContextInstanceId();

    void setContextInstanceId(String contextInstanceId);

    boolean isDryRun();

    void setDryRun(boolean dryRun);
}
