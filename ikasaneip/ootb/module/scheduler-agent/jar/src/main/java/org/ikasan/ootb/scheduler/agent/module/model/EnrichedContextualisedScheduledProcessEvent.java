package org.ikasan.ootb.scheduler.agent.module.model;

import org.ikasan.ootb.scheduled.model.ContextualisedScheduledProcessEventImpl;

public class EnrichedContextualisedScheduledProcessEvent extends ContextualisedScheduledProcessEventImpl {

    private Process process;

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
