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
public class SingleRecipientRouter extends AbstractMultiTransition
{
	private static final String IMAGE = "frontend/images/message-router.png";

	public SingleRecipientRouter(String id, String name)
	{
        super(id, name, IMAGE);
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


    public static SingleRecipientRouterBuilder singleRecipientRouterBuilder()
    {
        return new SingleRecipientRouterBuilder();
    }

    /**
     * Builder class
     */
    public static class SingleRecipientRouterBuilder
    {
        private String id;
        private String name;

        public SingleRecipientRouterBuilder withId(String id)
        {
            this.id = id;
            return this;
        }

        public SingleRecipientRouterBuilder withName(String name)
        {
            this.name = name;
            return this;
        }


        public SingleRecipientRouter build()
        {
            if (id == null || name == null)
            {
                throw new IllegalStateException("Cannot create SingleRecipientRouter. id and name cannot ne null!");
            }

            return new SingleRecipientRouter(id, name);
        }
    }
}
