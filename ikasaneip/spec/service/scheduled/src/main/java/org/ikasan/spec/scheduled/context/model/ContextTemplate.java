package org.ikasan.spec.scheduled.context.model;

import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.io.Serializable;

public interface ContextTemplate extends Context<ContextTemplate, ContextParameter, SchedulerJob, JobLock>, Serializable {

}
