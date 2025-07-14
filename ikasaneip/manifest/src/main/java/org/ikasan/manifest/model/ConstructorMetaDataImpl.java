package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.ConstructorMetaData;
import org.ikasan.spec.metadata.TypeParameter;

import java.util.List;

public class ConstructorMetaDataImpl implements ConstructorMetaData {
    private String componentName;
    private String className;
    private List<TypeParameter> constructorArguments;

    @Override
    public String getComponentName() {
        return componentName;
    }

    @Override
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public List<TypeParameter> getConstructorArguments() {
        return constructorArguments;
    }

    @Override
    public void setConstructorArguments(List<TypeParameter> constructorArguments) {
        this.constructorArguments = constructorArguments;
    }
}
