package org.ikasan.module.builder.model.module;

public class SplitterComponent extends AbstractSingleTransition {
    public static final String TYPE = "org.ikasan.spec.component.splitting.SplitterComponent";


    /**
     * Constructor for creating a SplitterComponent object.
     *
     * @param name the name of the splitter
     * @param implementingClass the implementing class of the splitter
     * @param transition the transition component of the splitter
     */
    public SplitterComponent(String name, String implementingClass, Component transition) {
        super(name, TYPE, implementingClass, transition);
    }
}
