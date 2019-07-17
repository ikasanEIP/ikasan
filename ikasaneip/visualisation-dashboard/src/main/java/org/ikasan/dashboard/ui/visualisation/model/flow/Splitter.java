package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.util.Shape;

/**
 * Created by stewmi on 07/11/2018.
 */
public class Splitter extends Node implements SingleTransition
{
	public static final String IMAGE = "frontend/images/splitter.png";

	private Node transition;

	public Splitter(String id, String name, Node transition)
	{
        super(id, name, Nodes.builder().withShape(Shape.image).withImage(IMAGE));
		this.transition = transition;
	}

	@Override
	public Node getTransition()
	{
		return transition;
	}
}
