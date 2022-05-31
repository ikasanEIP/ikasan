package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.context.model.ContextParameter;

import java.io.Serializable;

public interface ContextParameterInstance extends ContextParameter, Serializable {
    Object getValue();

    void setValue(Object value);
}
