package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.util.Shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stewmi on 07/11/2018.
 */
public class SingleRecipientRouter extends Node implements MultiTransition
{
	private static final String IMAGE = "frontend/images/message-router.png";

	private Map<String, Node> transitions;

	public SingleRecipientRouter(String id, String name)
	{
        super(id, name, Nodes.builder().withShape(Shape.image).withImage(IMAGE));
		transitions = new HashMap<>();
	}

	public void addTransition(String transitionContext, Node node)
	{
		this.transitions.put(transitionContext, node);
	}

	@Override
	public Map<String, Node> getTransitions()
	{
		return this.transitions;
	}
}
