package org.ikasan.module.builder.model;

public class ProducerComponent extends AbstractSingleTransition {
    public static final String TYPE = "org.ikasan.spec.component.endpoint.ProducerComponent";


    /**
     * Constructor for creating a ProducerComponent object.
     *
     * @param name the name of the producer
     * @param implementingClass the implementing class of the producer
     * @param transition the transition component of the producer
     */
    public ProducerComponent(String name, String implementingClass, Component transition) {
        super(name, TYPE, implementingClass, transition);
    }
}
