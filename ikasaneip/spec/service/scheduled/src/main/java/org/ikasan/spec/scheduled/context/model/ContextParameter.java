package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;

public interface ContextParameter extends Serializable {

    String getName();

    void setName(String name);

    String getType();

    void setType(String type);
}
