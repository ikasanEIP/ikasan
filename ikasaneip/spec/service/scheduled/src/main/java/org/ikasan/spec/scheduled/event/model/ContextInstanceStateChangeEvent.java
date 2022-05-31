package org.ikasan.spec.scheduled.event.model;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.InstanceStatus;

import java.io.Serializable;

public interface ContextInstanceStateChangeEvent extends StateChangeEvent, Serializable {

    ContextInstance getContextInstance();

}
