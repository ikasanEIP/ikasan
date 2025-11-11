package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.TypeParameter;

import java.util.Objects;

public class TypeParameterImpl implements TypeParameter {
    private String name;
    private String type;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeParameterImpl that = (TypeParameterImpl) o;
        return Objects.equals(name, that.name)
            && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
