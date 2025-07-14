package org.ikasan.module.builder.model;

public class SequencerComponent extends AbstractSingleTransition {
    public static final String TYPE = "org.ikasan.spec.component.sequencing.SequencerComponent";


    /**
     * Constructor for creating a SequencerComponent object.
     *
     * @param name the name of the sequencer
     * @param implementingClass the implementing class of the sequencer
     * @param transition the transition component of the sequencer
     */
    public SequencerComponent(String name, String implementingClass, Component transition) {
        super(name, TYPE, implementingClass, transition);
    }
}
