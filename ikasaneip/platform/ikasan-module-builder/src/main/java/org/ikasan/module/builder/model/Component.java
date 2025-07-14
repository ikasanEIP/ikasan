package org.ikasan.module.builder.model;

public class Component {
    private String name;
    private String componentType;
    private String implementingClass;

    public Component(String name, String componentType, String implementingClass) {
        this.name = name;
        this.componentType = componentType;
        this.implementingClass = implementingClass;
    }

    public String getName() {
        return name;
    }

    public String getComponentType() {
        return componentType;
    }

    public String getImplementingClass() {
        return implementingClass;
    }
}
