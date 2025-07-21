package org.ikasan.module.builder.model;

public class FlowModel {
    private String name;
    String moduleBasePackage;
    private Component consumer;

    public FlowModel(String name, String moduleBasePackage, Component consumer) {
        this.name = name;
        this.moduleBasePackage = moduleBasePackage;
        this.consumer = consumer;
    }

    public String getName() {
        return name;
    }

    public String getModuleBasePackage() {
        return moduleBasePackage;
    }

    public Component getConsumer() {
        return consumer;
    }
}
