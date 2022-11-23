package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;

public interface ContextParameter extends Serializable {

    String getName();

    void setName(String name);

    String getDefaultValue();

    void setDefaultValue(String defaultValue);
}
