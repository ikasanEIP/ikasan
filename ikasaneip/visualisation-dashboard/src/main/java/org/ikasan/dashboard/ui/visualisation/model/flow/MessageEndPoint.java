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

    public static MessageEndPointBuilder messageEndPointBuilder()
    {
        return new MessageEndPointBuilder();
    }

    /**
     * Builder class
     */
    public static class MessageEndPointBuilder
    {
        private String id;
        private String name;
        private String transitionLabel;
        private Node transition;

        public MessageEndPointBuilder withId(String id)
        {
            this.id = id;
            return this;
        }

        public MessageEndPointBuilder withName(String name)
        {
            this.name = name;
            return this;
        }

        public MessageEndPointBuilder withTransitionLabel(String transitionLabel)
        {
            this.transitionLabel = transitionLabel;
            return this;
        }

        public MessageEndPointBuilder withTransition(Node transition)
        {
            this.transition = transition;
            return this;
        }


        public MessageEndPoint build()
        {
            if (id == null || name == null)
            {
                throw new IllegalStateException("Cannot create DeadEndPoint. id and name cannot be null!");
            }

            return new MessageEndPoint(id, name, transitionLabel, transition);
        }
    }
}
