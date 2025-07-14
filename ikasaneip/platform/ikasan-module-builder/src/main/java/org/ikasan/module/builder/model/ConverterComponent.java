package org.ikasan.module.builder.model;

public class ConverterComponent extends AbstractSingleTransition {
    public static final String TYPE = "org.ikasan.spec.component.transformation.ConverterComponent";


    /**
     * Constructor for creating a ConverterComponent object.
     *
     * @param name the name of the converter
     * @param implementingClass the implementing class of the converter
     * @param transition the transition component of the converter
     */
    public ConverterComponent(String name, String implementingClass, Component transition) {
        super(name, TYPE, implementingClass, transition);
    }
}
