package org.ikasan.spec.scheduled.event.model;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.SchedulerJobInstance;

import java.io.Serializable;

public interface SchedulerJobInstanceStateChangeEvent extends StateChangeEvent, Serializable {

    SchedulerJobInstance getSchedulerJobInstance();

    ContextInstance getContextInstance();
}
