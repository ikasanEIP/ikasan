package org.ikasan.module.builder.model.module;

public class BrokerComponent extends AbstractSingleTransition {
    public static final String TYPE = "org.ikasan.spec.component.endpoint.BrokerComponent";


    /**
     * Constructor for creating a BrokerComponent object.
     *
     * @param name the name of the broker
     * @param implementingClass the implementing class of the broker
     * @param transition the transition component of the broker
     */
    public BrokerComponent(String name, String implementingClass, Component transition) {
        super(name, TYPE, implementingClass, transition);
    }
}
