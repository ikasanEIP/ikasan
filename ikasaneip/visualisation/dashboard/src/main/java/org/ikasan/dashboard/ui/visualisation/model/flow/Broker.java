package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;

/**
 * Created by stewmi on 07/11/2018.
 */
public class Broker extends AbstractSingleTransition
{
	public static final String IMAGE = "frontend/images/broker.png";


	public Broker(String id, String name, String transitionLabel, Node transition)
	{
		super(id, name, transition, transitionLabel, IMAGE);
	}

    public static BrokerBuilder brokerBuilder()
    {
        return new BrokerBuilder();
    }

    /**
     * Builder class
     */
    public static class BrokerBuilder
    {
        private String id;
        private String name;
        private String transitionLabel;
        private Node transition;

        public BrokerBuilder withId(String id)
        {
            this.id = id;
            return this;
        }

        public BrokerBuilder withName(String name)
        {
            this.name = name;
            return this;
        }

        public BrokerBuilder withTransitionLabel(String transitionLabel)
        {
            this.transitionLabel = transitionLabel;
            return this;
        }

        public BrokerBuilder withTransition(Node transition)
        {
            this.transition = transition;
            return this;
        }


        public Broker build()
        {
            if (id == null || name == null || transition == null)
            {
                throw new IllegalStateException("Cannot create Broker. id, name and transition cannot be null!");
            }

            return new Broker(id, name, transitionLabel, transition);
        }
    }
}
