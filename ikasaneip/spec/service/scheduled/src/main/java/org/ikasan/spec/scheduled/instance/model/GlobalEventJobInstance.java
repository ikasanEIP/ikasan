package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.job.model.GlobalEventJob;

import java.io.Serializable;

public interface GlobalEventJobInstance extends SchedulerJobInstance, GlobalEventJob, Serializable {
}
