package org.ikasan.spec.scheduled.event.model;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.SchedulerJobInstance;

public interface SchedulerJobInstanceStateChangeEvent extends StateChangeEvent {

    SchedulerJobInstance getSchedulerJobInstance();

    ContextInstance getContextInstance();
}
