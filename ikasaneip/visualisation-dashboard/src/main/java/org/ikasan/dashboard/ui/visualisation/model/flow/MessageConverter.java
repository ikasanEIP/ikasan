package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;

/**
 * Created by stewmi on 07/11/2018.
 */
public class MessageConverter extends AbstractSingleTransition
{
	public static final String IMAGE = "frontend/images/message-translator.png";

    /**
     * Constructor
     *
     * @param id
     * @param name
     * @param transitionLabel
     * @param transition
     */
	public MessageConverter(String id, String name, String transitionLabel, Node transition)
    {
        super(id, name, transition, transitionLabel, IMAGE);
    }

    public static MessageConverterBuilder messageConverterBuilder()
    {
        return new MessageConverterBuilder();
    }

    /**
     * Builder class
     */
    public static class MessageConverterBuilder
    {
        private String id;
        private String name;
        private String transitionLabel;
        private Node transition;

        public MessageConverterBuilder withId(String id)
        {
            this.id = id;
            return this;
        }

        public MessageConverterBuilder withName(String name)
        {
            this.name = name;
            return this;
        }

        public MessageConverterBuilder withTransitionLabel(String transitionLabel)
        {
            this.transitionLabel = transitionLabel;
            return this;
        }

        public MessageConverterBuilder withTransition(Node transition)
        {
            this.transition = transition;
            return this;
        }


        public MessageConverter build()
        {
            if (id == null || name == null || transition == null)
            {
                throw new IllegalStateException("Cannot create MessageConverter. id, name and transition cannot be null!");
            }

            return new MessageConverter(id, name, transitionLabel, transition);
        }
    }
}
