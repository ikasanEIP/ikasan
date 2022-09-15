package org.ikasan.spec.scheduled.instance.model;

public enum InstanceStatus {
    RUNNING,
    SKIPPED_RUNNING,
    COMPLETE,
    SKIPPED_COMPLETE,
    WAITING,
    ERROR,
    SKIPPED,
    ON_HOLD,
    RELEASED,
    ENDED;
}
