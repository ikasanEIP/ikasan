package org.ikasan.spec.scheduled.event.model;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.InstanceStatus;

public interface ContextInstanceStateChangeEvent extends StateChangeEvent {

    ContextInstance getContextInstance();

}
