package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;

/**
 * Created by stewmi on 08/11/2018.
 */
public abstract class Consumer extends AbstractSingleTransition implements SingleTransition
{
    /**
     * Constructor
     *
     * @param id
     * @param name
     * @param transitionLabel
     * @param transition
     * @param image
     */
	public Consumer(String id, String name, String transitionLabel, Node transition, String image)
    {
        super(id, name, transition, transitionLabel, image);
    }
}
