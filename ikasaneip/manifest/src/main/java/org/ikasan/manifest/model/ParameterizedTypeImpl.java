package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.ParameterizedType;
import org.ikasan.spec.metadata.TypeParameter;

import java.util.List;
import java.util.Objects;

public class ParameterizedTypeImpl implements ParameterizedType {
    private String implementingClassName;
    private List<TypeParameter> typeParameters;

    @Override
    public String getImplementingClassName() {
        return implementingClassName;
    }

    @Override
    public void setImplementingClassName(String implementingClassName) {
        this.implementingClassName = implementingClassName;
    }

    @Override
    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    @Override
    public void setTypeParameters(List<TypeParameter> typeParameters) {
        this.typeParameters = typeParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParameterizedTypeImpl that = (ParameterizedTypeImpl) o;
        return Objects.equals(implementingClassName, that.implementingClassName)
            && Objects.equals(typeParameters, that.typeParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(implementingClassName, typeParameters);
    }
}