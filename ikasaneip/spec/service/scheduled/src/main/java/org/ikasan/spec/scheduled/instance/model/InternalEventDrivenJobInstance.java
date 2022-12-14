package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJob;

import java.io.Serializable;

public interface InternalEventDrivenJobInstance extends SchedulerJobInstance, InternalEventDrivenJob, Serializable {
}
