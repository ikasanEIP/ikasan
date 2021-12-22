package org.ikasan.spec.scheduled.instance.model;

public interface StatefulEntity {

    InstanceStatus getStatus();

    void setStatus(InstanceStatus status);
}
