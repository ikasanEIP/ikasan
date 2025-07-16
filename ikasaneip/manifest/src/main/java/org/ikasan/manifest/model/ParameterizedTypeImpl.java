package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.ParameterizedType;
import org.ikasan.spec.metadata.TypeParameter;

import java.util.List;

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
}