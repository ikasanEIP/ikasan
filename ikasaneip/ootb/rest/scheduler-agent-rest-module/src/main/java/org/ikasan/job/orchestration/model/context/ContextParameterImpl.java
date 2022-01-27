package org.ikasan.job.orchestration.model.context;

import org.ikasan.spec.scheduled.context.model.ContextParameter;

import java.util.Objects;

public class ContextParameterImpl implements ContextParameter {
    protected String name;
    protected String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContextParameterImpl that = (ContextParameterImpl) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
