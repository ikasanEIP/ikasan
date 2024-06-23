package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.job.model.ContextTerminalJob;

import java.io.Serializable;

public interface ContextTerminalJobInstance extends SchedulerJobInstance, ContextTerminalJob, Serializable {
}
