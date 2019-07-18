package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;

/**
 * Created by stewmi on 07/11/2018.
 */
public class Broker extends AbstractSingleTransition
{
	public static final String IMAGE = "frontend/images/broker.png";


	public Broker(String id, String name, String transitionLabel, Node transition)
	{
		super(id, name, transition, transitionLabel, IMAGE);
	}
}
