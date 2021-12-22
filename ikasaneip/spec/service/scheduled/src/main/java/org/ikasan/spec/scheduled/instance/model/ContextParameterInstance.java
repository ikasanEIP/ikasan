package org.ikasan.spec.scheduled.instance.model;

import org.ikasan.spec.scheduled.context.model.ContextParameter;

public interface ContextParameterInstance extends ContextParameter {
    Object getValue();

    void setValue(Object value);
}
