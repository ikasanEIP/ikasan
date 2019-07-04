package org.ikasan.dashboard.ui.visualisation.model.flow;


import org.ikasan.vaadin.visjs.network.Node;

/**
 * Created by stewmi on 07/11/2018.
 */
public class EventDrivenConsumer extends Consumer
{
	public static final String IMAGE = "frontend/Event Driven Consumer.png";

	public EventDrivenConsumer(String id, String name, Node transition)
	{
        super(id, name, IMAGE, transition);
	}
}
