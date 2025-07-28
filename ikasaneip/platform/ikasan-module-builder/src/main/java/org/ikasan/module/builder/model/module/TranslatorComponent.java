package org.ikasan.module.builder.model.module;

public class TranslatorComponent extends AbstractSingleTransition {
    public static final String TYPE = "org.ikasan.spec.component.transformation.TranslatorComponent";


    /**
     * Constructor for creating a TranslatorComponent object.
     *
     * @param name the name of the translator
     * @param implementingClass the implementing class of the translator
     * @param transition the transition component of the translator
     */
    public TranslatorComponent(String name, String implementingClass, Component transition) {
        super(name, TYPE, implementingClass, transition);
    }
}
