package org.ikasan.ootb.scheduler.agent.rest.dto;

import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;

public class ContextParameterInstanceDto implements ContextParameterInstance {

    private String name;
    private String defaultValue;
    private String value;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
