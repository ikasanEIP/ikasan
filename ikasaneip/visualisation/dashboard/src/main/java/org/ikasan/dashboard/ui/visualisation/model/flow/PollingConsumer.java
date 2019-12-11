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
	private PollingConsumer(String id, String name, String transitionLabel, Node transition, Node source)
    {
        super(id, name, transitionLabel, transition, IMAGE, source);
    }

    public static PollingConsumerBuilder pollingConsumerBuilder()
    {
        return new PollingConsumerBuilder();
    }

    /**
     * Builder class
     */
    public static class PollingConsumerBuilder
    {
        private String id;
        private String name;
        private String transitionLabel;
        private Node transition;
        private Node source;

        public PollingConsumerBuilder withId(String id)
        {
            this.id = id;
            return this;
        }

        public PollingConsumerBuilder withName(String name)
        {
            this.name = name;
            return this;
        }

        public PollingConsumerBuilder withTransitionLabel(String transitionLabel)
        {
            this.transitionLabel = transitionLabel;
            return this;
        }

        public PollingConsumerBuilder withTransition(Node transition)
        {
            this.transition = transition;
            return this;
        }

        public PollingConsumerBuilder withSource(Node source)
        {
            this.source = source;
            return this;
        }

        public PollingConsumer build()
        {
            if(id == null || name == null || transition == null)
            {
                throw new IllegalStateException("Cannot create PollingConsumer. id, name and transition cannot be null!");
            }

            return new PollingConsumer(id, name, transitionLabel, transition, source);
        }
    }
}
