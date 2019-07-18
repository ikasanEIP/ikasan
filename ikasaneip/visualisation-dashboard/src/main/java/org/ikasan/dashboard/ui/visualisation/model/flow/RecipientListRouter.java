package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.util.Shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stewmi on 07/11/2018.
 */
public class RecipientListRouter extends Node implements MultiTransition
{
	public static final String IMAGE = "frontend/images/recipient-list-router.png";
    private Map<String, Node> transitions;

	public RecipientListRouter(String id, String name)
	{
        super(id, name, Nodes.builder().withShape(Shape.image).withImage(IMAGE));
		this.transitions = new HashMap<>();
	}

	public void addTransition(String context, Node node)
	{
		this.transitions.put(context, node);
	}

	@Override
	public Map<String, Node> getTransitions()
	{
		return this.transitions;
	}
}
