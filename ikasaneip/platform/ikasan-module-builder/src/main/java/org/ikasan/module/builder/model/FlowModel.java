package org.ikasan.module.builder.model;

public class FlowModel {
    private String name;
    private Component consumer;

    public FlowModel(String name, Component consumer) {
        this.name = name;
        this.consumer = consumer;
    }

    public String getName() {
        return name;
    }

    public Component getConsumer() {
        return consumer;
    }
}
