package org.ikasan.ootb.scheduler.agent.rest.dto;

import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;

public class ContextParameterInstanceDto implements ContextParameterInstance {

    private String name;
    private String type;
    private Object value;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }
}
