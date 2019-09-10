package org.ikasan.dashboard.ui.visualisation.model.flow;


import org.ikasan.vaadin.visjs.network.Node;

/**
 * Created by stewmi on 07/11/2018.
 */
public class FtpConsumer extends Consumer
{
	public static final String IMAGE = "frontend/images/ftp-consumer.png";

    /**
     * Constructor
     *
     * @param id
     * @param name
     * @param transitionLabel
     * @param transition
     */
	private FtpConsumer(String id, String name, String transitionLabel, Node transition, Node source)
    {
        super(id, name, transitionLabel, transition, IMAGE, source);
    }

    public static FtpConsumerBuilder ftpConsumerBuilder()
    {
        return new FtpConsumerBuilder();
    }

    /**
     * Builder class
     */
    public static class FtpConsumerBuilder
    {
        private String id;
        private String name;
        private String transitionLabel;
        private Node transition;
        private Node source;

        public FtpConsumerBuilder withId(String id)
        {
            this.id = id;
            return this;
        }

        public FtpConsumerBuilder withName(String name)
        {
            this.name = name;
            return this;
        }

        public FtpConsumerBuilder withTransitionLabel(String transitionLabel)
        {
            this.transitionLabel = transitionLabel;
            return this;
        }

        public FtpConsumerBuilder withTransition(Node transition)
        {
            this.transition = transition;
            return this;
        }

        public FtpConsumerBuilder withSource(Node source)
        {
            this.source = source;
            return this;
        }

        public FtpConsumer build()
        {
            if(id == null || name == null || transition == null)
            {
                throw new IllegalStateException("Cannot create FtpConsumer. id, name and transition cannot be null!");
            }

            return new FtpConsumer(id, name, transitionLabel, transition, source);
        }
    }
}
