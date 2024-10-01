package org.ikasan.spec.scheduled.instance.model;

import java.io.Serializable;

public interface StatefulEntity extends Serializable {

    /**
     * Retrieves the current status of an instance.
     *
     * @return The status of the instance.
     */
    InstanceStatus getStatus();

    /**
     * Sets the status of the instance.
     *
     * @param status the new status to be set for the instance
     */
    void setStatus(InstanceStatus status);

    /**
     * Checks whether the error for this StatefulEntity has been acknowledged.
     *
     * @return true if the error has been acknowledged, false otherwise
     */
    default Boolean isErrorAcknowledged() {
        return false;
    }
}
