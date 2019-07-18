package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;

/**
 * Created by stewmi on 07/11/2018.
 */
public class MessageEndPoint extends AbstractSingleTransition implements Endpoint
{
	public static final String IMAGE = "frontend/images/message-endpoint.png";


    /**
     * Constructor
     *
     * @param id
     * @param name
     * @param transitionLabel
     * @param transition
     */
	public MessageEndPoint(String id, String name, String transitionLabel, Node transition)
    {
        super(id, name, transition, transitionLabel, IMAGE);
    }

}
