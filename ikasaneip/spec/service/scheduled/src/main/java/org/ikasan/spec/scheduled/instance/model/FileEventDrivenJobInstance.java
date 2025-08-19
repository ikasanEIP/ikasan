package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.job.model.FileEventDrivenJob;

import java.io.Serializable;

public interface FileEventDrivenJobInstance extends QuartzScheduleDrivenJobInstance, FileEventDrivenJob, Serializable {
}
