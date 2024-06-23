package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.job.model.ContextStartJob;

import java.io.Serializable;

public interface ContextStartJobInstance extends SchedulerJobInstance, ContextStartJob, Serializable {
}
