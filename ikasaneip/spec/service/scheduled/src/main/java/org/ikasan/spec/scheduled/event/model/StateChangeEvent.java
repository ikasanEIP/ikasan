package org.ikasan.spec.scheduled.event.model;

import org.ikasan.spec.scheduled.instance.model.InstanceStatus;

public interface StateChangeEvent {

    InstanceStatus getPreviousStatus();

    InstanceStatus getNewStatus();
}
