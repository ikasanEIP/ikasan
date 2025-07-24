package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.TypeParameter;

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
}
