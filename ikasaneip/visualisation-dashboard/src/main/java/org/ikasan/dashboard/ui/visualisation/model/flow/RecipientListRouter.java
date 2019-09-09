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

    public static RecipientListRouterBuilder recipientRouterBuilder()
    {
        return new RecipientListRouterBuilder();
    }

    /**
     * Builder class
     */
    public static class RecipientListRouterBuilder
    {
        private String id;
        private String name;

        public RecipientListRouterBuilder withId(String id)
        {
            this.id = id;
            return this;
        }

        public RecipientListRouterBuilder withName(String name)
        {
            this.name = name;
            return this;
        }


        public RecipientListRouter build()
        {
            if (id == null || name == null)
            {
                throw new IllegalStateException("Cannot create RecipientListRouter. id and name cannot ne null!");
            }

            return new RecipientListRouter(id, name);
        }
    }
}
