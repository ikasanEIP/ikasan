package org.ikasan.spec.scheduled;

public interface ContextParameterInstance extends ContextParameter {
    Object getValue();

    void setValue(Object value);
}
