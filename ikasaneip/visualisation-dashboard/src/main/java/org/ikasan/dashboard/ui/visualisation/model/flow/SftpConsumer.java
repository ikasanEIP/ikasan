package org.ikasan.dashboard.ui.visualisation.model.flow;


import org.ikasan.vaadin.visjs.network.Node;

/**
 * Created by stewmi on 07/11/2018.
 */
public class SftpConsumer extends Consumer
{
	public static final String IMAGE = "frontend/images/sftp-consumer.png";

    /**
     * Constructor
     *
     * @param id
     * @param name
     * @param transitionLabel
     * @param transition
     */
	private SftpConsumer(String id, String name, String transitionLabel, Node transition, Node source)
    {
        super(id, name, transitionLabel, transition, IMAGE, source);
    }

    public static SftpConsumerBuilder sftpConsumerBuilder()
    {
        return new SftpConsumerBuilder();
    }

    /**
     * Builder class
     */
    public static class SftpConsumerBuilder
    {
        private String id;
        private String name;
        private String transitionLabel;
        private Node transition;
        private Node source;

        public SftpConsumerBuilder withId(String id)
        {
            this.id = id;
            return this;
        }

        public SftpConsumerBuilder withName(String name)
        {
            this.name = name;
            return this;
        }

        public SftpConsumerBuilder withTransitionLabel(String transitionLabel)
        {
            this.transitionLabel = transitionLabel;
            return this;
        }

        public SftpConsumerBuilder withTransition(Node transition)
        {
            this.transition = transition;
            return this;
        }

        public SftpConsumerBuilder withSource(Node source)
        {
            this.source = source;
            return this;
        }

        public SftpConsumer build()
        {
            if (id == null || name == null || transition == null)
            {
                throw new IllegalStateException("Cannot create SftpConsumer. id, name and transition cannot be null!");
            }

            return new SftpConsumer(id, name, transitionLabel, transition, source);
        }
    }
}
