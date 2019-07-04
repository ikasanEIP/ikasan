package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.util.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stewmi on 07/11/2018.
 */
public class MessageChannel extends Node
{
	public static final String IMAGE = "VAADIN/themes/ikasan/images/MessageChannel.png";

	private List<EventDrivenConsumer> consumers;
	private boolean isPrivate;

	public MessageChannel(String id, String name, boolean isPrivate)
	{
        super(id, name, Nodes.builder().withShape(Shape.image).withImage(IMAGE));
		this.consumers = new ArrayList<>();
		this.isPrivate = isPrivate;
	}

	public void addConsumer(EventDrivenConsumer consumer)
	{
		this.consumers.add(consumer);
	}

	public boolean isPrivate()
	{
		return isPrivate;
	}
}
