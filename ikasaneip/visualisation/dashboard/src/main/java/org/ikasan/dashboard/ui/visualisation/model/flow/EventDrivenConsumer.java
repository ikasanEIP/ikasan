package org.ikasan.dashboard.ui.visualisation.model.flow;


import org.ikasan.vaadin.visjs.network.Node;

/**
 * Created by stewmi on 07/11/2018.
 */
public class EventDrivenConsumer extends Consumer
{
	public static final String IMAGE = "frontend/images/event-driven-consumer.png";

    /**
     * Constructor
     *
     * @param id
     * @param name
     * @param transitionLabel
     * @param transition
     */
	private EventDrivenConsumer(String id, String name, String transitionLabel, Node transition, Node source)
    {
        super(id, name, transitionLabel, transition, IMAGE, source);
    }

    public static EventDrivenConsumerBuilder sftpConsumerBuilder()
    {
        return new EventDrivenConsumerBuilder();
    }

    /**
     * Builder class
     */
    public static class EventDrivenConsumerBuilder
    {
        private String id;
        private String name;
        private String transitionLabel;
        private Node transition;
        private Node source;

        public EventDrivenConsumerBuilder withId(String id)
        {
            this.id = id;
            return this;
        }

        public EventDrivenConsumerBuilder withName(String name)
        {
            this.name = name;
            return this;
        }

        public EventDrivenConsumerBuilder withTransitionLabel(String transitionLabel)
        {
            this.transitionLabel = transitionLabel;
            return this;
        }

        public EventDrivenConsumerBuilder withTransition(Node transition)
        {
            this.transition = transition;
            return this;
        }

        public EventDrivenConsumerBuilder withSource(Node source)
        {
            this.source = source;
            return this;
        }

        public EventDrivenConsumer build()
        {
            if (id == null || name == null || transition == null)
            {
                throw new IllegalStateException("Cannot create EventDrivenConsumer. id, name and transition cannot be null!");
            }

            return new EventDrivenConsumer(id, name, transitionLabel, transition, source);
        }
    }
}
