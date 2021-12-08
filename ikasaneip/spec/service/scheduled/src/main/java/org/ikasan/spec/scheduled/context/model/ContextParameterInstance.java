package org.ikasan.spec.scheduled.context.model;

public interface ContextParameterInstance extends ContextParameter {
    Object getValue();

    void setValue(Object value);
}
