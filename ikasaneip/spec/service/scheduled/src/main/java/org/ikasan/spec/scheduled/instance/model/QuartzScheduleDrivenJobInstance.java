package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.job.model.QuartzScheduleDrivenJob;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.io.Serializable;
import java.util.Map;

public interface QuartzScheduleDrivenJobInstance extends SchedulerJobInstance, QuartzScheduleDrivenJob, Serializable {
}
