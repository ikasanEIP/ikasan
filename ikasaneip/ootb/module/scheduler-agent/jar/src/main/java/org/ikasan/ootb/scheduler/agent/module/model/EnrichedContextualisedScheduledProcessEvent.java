package org.ikasan.ootb.scheduler.agent.module.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ikasan.ootb.scheduled.model.ContextualisedScheduledProcessEventImpl;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;

import java.util.List;

public class EnrichedContextualisedScheduledProcessEvent extends ContextualisedScheduledProcessEventImpl {

    @JsonIgnore
    private Process process;
    @JsonIgnore
    private List<ContextParameterInstance> contextParameters;

    /**
     * Get the process
     *
     * @return
     */
    @JsonIgnore
    public Process getProcess() {
        return process;
    }

    /**
     * Set the process
     *
     * @param process
     */
    @JsonIgnore
    public void setProcess(Process process) {
        this.process = process;
    }

    /**
     * Set the context parameters.
     *
     * @param contextParameters
     */
    @JsonIgnore
    public void setContextParameters(List<ContextParameterInstance> contextParameters) {
        this.contextParameters = contextParameters;
    }

    /**
     * Set the context parameters.
     *
     * @return
     */
    @JsonIgnore
    public List<ContextParameterInstance> getContextParameters() {
        return this.contextParameters;
    }
}
