package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.util.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stewmi on 07/11/2018.
 */
public class RecipientListRouter extends Node implements MultiTransition
{
	public static final String IMAGE = "frontend/images/recipient-list-router.png";
	private List<Node> transitions;

	public RecipientListRouter(String id, String name)
	{
        super(id, name, Nodes.builder().withShape(Shape.image).withImage(IMAGE));
		this.transitions = new ArrayList<>();
	}

	public void addTransition(Node node)
	{
		this.transitions.add(node);
	}

	@Override
	public List<Node> getTransitions()
	{
		return transitions;
	}
}
