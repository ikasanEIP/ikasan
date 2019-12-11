package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;

/**
 * Created by stewmi on 07/11/2018.
 */
public class DeadEndPoint extends AbstractSingleTransition implements SingleTransition, Endpoint
{
	public static final String IMAGE = "frontend/images/dead-end-point.png";

    /**
     * Constructor
     *
     * @param id
     * @param name
     * @param transitionLabel
     * @param transition
     */
	private DeadEndPoint(String id, String name, String transitionLabel, Node transition)
    {
        super(id, name, transition, transitionLabel, IMAGE);
    }

    public static DeadEndPointBuilder deadEndPointBuilder()
    {
        return new DeadEndPointBuilder();
    }

    /**
     * Builder class
     */
    public static class DeadEndPointBuilder
    {
        private String id;
        private String name;
        private String transitionLabel;
        private Node transition;

        public DeadEndPointBuilder withId(String id)
        {
            this.id = id;
            return this;
        }

        public DeadEndPointBuilder withName(String name)
        {
            this.name = name;
            return this;
        }

        public DeadEndPointBuilder withTransitionLabel(String transitionLabel)
        {
            this.transitionLabel = transitionLabel;
            return this;
        }

        public DeadEndPointBuilder withTransition(Node transition)
        {
            this.transition = transition;
            return this;
        }


        public DeadEndPoint build()
        {
            if (id == null || name == null)
            {
                throw new IllegalStateException("Cannot create DeadEndPoint. id and name cannot be null!");
            }

            return new DeadEndPoint(id, name, transitionLabel, transition);
        }
    }
}
