package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;

/**
 * Created by stewmi on 07/11/2018.
 */
public class MessageTranslator extends AbstractSingleTransition
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
	public MessageTranslator(String id, String name, String transitionLabel, Node transition)
    {
        super(id, name, transition, transitionLabel, IMAGE);
    }

    public static MessageTranslatorBuilder messageConverterBuilder()
    {
        return new MessageTranslatorBuilder();
    }

    /**
     * Builder class
     */
    public static class MessageTranslatorBuilder
    {
        private String id;
        private String name;
        private String transitionLabel;
        private Node transition;

        public MessageTranslatorBuilder withId(String id)
        {
            this.id = id;
            return this;
        }

        public MessageTranslatorBuilder withName(String name)
        {
            this.name = name;
            return this;
        }

        public MessageTranslatorBuilder withTransitionLabel(String transitionLabel)
        {
            this.transitionLabel = transitionLabel;
            return this;
        }

        public MessageTranslatorBuilder withTransition(Node transition)
        {
            this.transition = transition;
            return this;
        }


        public MessageTranslator build()
        {
            if (id == null || name == null || transition == null)
            {
                throw new IllegalStateException("Cannot create MessageConverter. id, name and transition cannot be null!");
            }

            return new MessageTranslator(id, name, transitionLabel, transition);
        }
    }
}
