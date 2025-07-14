package org.ikasan.module.builder.model;

public class ConsumerComponent extends AbstractSingleTransition {
    public static final String TYPE = "org.ikasan.spec.component.endpoint.ConsumerComponent";


    /**
     * Constructor for creating a ConsumerComponent object.
     *
     * @param name the name of the consumer
     * @param implementingClass the implementing class of the consumer
     * @param transition the transition component of the consumer
     */
    public ConsumerComponent(String name, String implementingClass, Component transition) {
        super(name, TYPE, implementingClass, transition);
    }
}
