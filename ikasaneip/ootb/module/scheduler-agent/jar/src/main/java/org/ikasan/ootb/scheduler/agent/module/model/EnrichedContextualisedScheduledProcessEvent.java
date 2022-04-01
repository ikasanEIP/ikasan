package org.ikasan.ootb.scheduler.agent.module.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ikasan.ootb.scheduled.model.ContextualisedScheduledProcessEventImpl;

public class EnrichedContextualisedScheduledProcessEvent extends ContextualisedScheduledProcessEventImpl {

    @JsonIgnore
    private Process process;

    @JsonIgnore
    public Process getProcess() {
        return process;
    }

    @JsonIgnore
    public void setProcess(Process process) {
        this.process = process;
    }
}
