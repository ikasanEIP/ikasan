package org.ikasan.spec.scheduled.event.model;

import org.ikasan.spec.scheduled.instance.model.InstanceStatus;

import java.io.Serializable;

public interface StateChangeEvent extends Serializable {

    InstanceStatus getPreviousStatus();

    InstanceStatus getNewStatus();
}
