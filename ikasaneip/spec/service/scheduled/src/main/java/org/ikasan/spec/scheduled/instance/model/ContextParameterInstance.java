package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.context.model.ContextParameter;

import java.io.Serializable;

public interface ContextParameterInstance extends ContextParameter, Serializable {

    /**
     * Get the parameter instance value.
     * @return
     */
    String getValue();

    /**
     * Set the parameter instance value.
     *
     * @param value
     */
    void setValue(String value);
}
