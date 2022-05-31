package org.ikasan.spec.scheduled.instance.model;

import java.io.Serializable;

public interface StatefulEntity extends Serializable {

    InstanceStatus getStatus();

    void setStatus(InstanceStatus status);
}
