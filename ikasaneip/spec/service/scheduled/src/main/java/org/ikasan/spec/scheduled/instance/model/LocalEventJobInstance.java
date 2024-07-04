package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.job.model.LocalEventJob;

import java.io.Serializable;

public interface LocalEventJobInstance extends SchedulerJobInstance, LocalEventJob, Serializable {
}
