package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.util.Shape;

/**
 * Created by stewmi on 08/11/2018.
 */
public abstract class Consumer extends Node implements SingleTransition
{
	private Node transition;

	public Consumer(String id, String label, String image, Node transition)
	{
        super(id, label, Nodes.builder().withShape(Shape.image).withImage(image));
		this.transition = transition;
	}

	public Node getTransition()
	{
		return transition;
	}
}
