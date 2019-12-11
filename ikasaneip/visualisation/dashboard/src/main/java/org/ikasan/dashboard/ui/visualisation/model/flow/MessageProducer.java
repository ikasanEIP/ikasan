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

    public static MessageProducerBuilder messageProducerBuilder()
    {
        return new MessageProducerBuilder();
    }

    /**
     * Builder class
     */
    public static class MessageProducerBuilder
    {
        private String id;
        private String name;
        private String transitionLabel;
        private Node transition;

        public MessageProducerBuilder withId(String id)
        {
            this.id = id;
            return this;
        }

        public MessageProducerBuilder withName(String name)
        {
            this.name = name;
            return this;
        }

        public MessageProducerBuilder withTransitionLabel(String transitionLabel)
        {
            this.transitionLabel = transitionLabel;
            return this;
        }

        public MessageProducerBuilder withTransition(Node transition)
        {
            this.transition = transition;
            return this;
        }

        public MessageProducer build()
        {
            if(id == null || name == null || transition == null)
            {
                throw new IllegalStateException("Cannot create FtpConsumer. id, name and transition cannot be null!");
            }

            return new MessageProducer(id, name, transitionLabel, transition);
        }
    }
}
