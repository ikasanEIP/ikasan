package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;

/**
 * Created by stewmi on 07/11/2018.
 */
public class MessageProducer extends AbstractSingleTransition
{
	public static final String IMAGE = "frontend/images/channel-adapter.png";

    /**
     * Constructor
     *
     * @param id
     * @param name
     * @param transitionLabel
     * @param transition
     */
	public MessageProducer(String id, String name, String transitionLabel, Node transition)
    {
        super(id, name, transition, transitionLabel, IMAGE);
    }
}
