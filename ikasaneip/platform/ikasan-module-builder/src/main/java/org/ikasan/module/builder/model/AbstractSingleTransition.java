package org.ikasan.module.builder.model;

public class AbstractSingleTransition extends Component implements SingleTransition
{
    protected Component transition;


    /**
     * Constructor for creating an AbstractSingleTransition object.
     *
     * @param name the name of the transition
     * @param componentType the type of component
     * @param implementingClass the implementing class of the transition
     * @param transition the transition component
     */
    public AbstractSingleTransition(String name, String componentType, String implementingClass, Component transition)
    {
        super(name, componentType, implementingClass);
        this.transition = transition;
    }

    @Override
    public Component getTransition()
    {
        return this.transition;
    }
}
