package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.job.model.FileEventDrivenJob;
import org.ikasan.spec.scheduled.job.model.QuartzScheduleDrivenJob;

import java.io.Serializable;
import java.util.List;

public interface FileEventDrivenJobInstance extends QuartzScheduleDrivenJobInstance, FileEventDrivenJob, Serializable {
}
