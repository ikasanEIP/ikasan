package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.job.model.BridgingJob;

import java.io.Serializable;

public interface BridgingJobInstance extends SchedulerJobInstance, BridgingJob, Serializable {
}
