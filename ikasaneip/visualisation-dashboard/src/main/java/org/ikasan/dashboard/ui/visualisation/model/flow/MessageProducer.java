package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.util.Shape;

/**
 * Created by stewmi on 07/11/2018.
 */
public class MessageProducer extends Node implements SingleTransition
{
	public static final String IMAGE = "VAADIN/themes/ikasan/images/Message Gateway.png";

	private MessageChannel messageChannel;

	public MessageProducer(String id, String name, MessageChannel messageChannel)
	{
        super(id, name, Nodes.builder().withShape(Shape.image).withImage(IMAGE));
		this.messageChannel = messageChannel;
	}

	public MessageChannel getMessageChannel()
	{
		return messageChannel;
	}


	@Override
	public Node getTransition()
	{
		return messageChannel;
	}
}
