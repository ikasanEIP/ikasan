package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.util.Shape;

public class AbstractSingleTransition extends Node implements SingleTransition
{
    protected Node transition;
    protected String transitionLabel;

    /**
     * Constructor
     *
     * @param id
     * @param name
     * @param transition
     * @param label
     * @param image
     */
    public AbstractSingleTransition(String id,  String name, Node transition, String label, String image)
    {
        super(id, name, Nodes.builder().withShape(Shape.image).withImage(image));
        this.transition = transition;
        this.transitionLabel = label;
    }

    @Override
    public Node getTransition()
    {
        return this.transition;
    }

    @Override
    public String getTransitionLabel()
    {
        return this.transitionLabel;
    }
}
