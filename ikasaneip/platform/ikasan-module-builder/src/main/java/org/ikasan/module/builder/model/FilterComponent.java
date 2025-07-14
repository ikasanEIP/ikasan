package org.ikasan.module.builder.model;

public class FilterComponent extends AbstractSingleTransition {
    public static final String TYPE = "org.ikasan.spec.component.filter.FilterComponent";


    /**
     * Constructor for creating a FilterComponent object.
     *
     * @param name the name of the filter
     * @param implementingClass the implementing class of the filter
     * @param transition the transition component of the filter
     */
    public FilterComponent(String name, String implementingClass, Component transition) {
        super(name, TYPE, implementingClass, transition);
    }
}
