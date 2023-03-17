package org.ikasan.spec.scheduled.event.model;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.InstanceStatus;

import java.io.Serializable;

public interface ContextInstanceStateChangeEvent extends StateChangeEvent, Serializable {

    /**
     * Get the identifier of the parent context instance.
     *
     * @return
     */
    String getContextInstanceId();

    /**
     * Get the actual context instance that was updated. This could be a child.
     *
     * @return
     */
    ContextInstance getContextInstance();

}
