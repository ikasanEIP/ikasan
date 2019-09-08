package org.ikasan.dashboard.ui.visualisation.model.flow;


import org.ikasan.vaadin.visjs.network.Node;

/**
 * Created by stewmi on 07/11/2018.
 */
public class PollingConsumer extends Consumer
{
	public static final String IMAGE = "frontend/images/polling-consumer.png";

    /**
     * Constructor
     *
     * @param id
     * @param name
     * @param transitionLabel
     * @param transition
     */
	public PollingConsumer(String id, String name, String transitionLabel, Node transition, Node source)
    {
        super(id, name, transitionLabel, transition, IMAGE, source);
    }
}
