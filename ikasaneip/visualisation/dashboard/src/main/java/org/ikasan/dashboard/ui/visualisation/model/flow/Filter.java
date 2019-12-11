package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;

/**
 * Created by stewmi on 07/11/2018.
 */
public class Filter extends AbstractSingleTransition
{
	public static final String IMAGE = "frontend/images/message-filter.png";


    /**
     * Constructor
     *
     * @param id
     * @param name
     * @param transitionLabel
     * @param transition
     */
	public Filter(String id, String name, String transitionLabel, Node transition)
    {
        super(id, name, transition, transitionLabel, IMAGE);
    }

    public static FilterBuilder filterBuilder()
    {
        return new FilterBuilder();
    }

    /**
     * Builder class
     */
    public static class FilterBuilder
    {
        private String id;
        private String name;
        private String transitionLabel;
        private Node transition;

        public FilterBuilder withId(String id)
        {
            this.id = id;
            return this;
        }

        public FilterBuilder withName(String name)
        {
            this.name = name;
            return this;
        }

        public FilterBuilder withTransitionLabel(String transitionLabel)
        {
            this.transitionLabel = transitionLabel;
            return this;
        }

        public FilterBuilder withTransition(Node transition)
        {
            this.transition = transition;
            return this;
        }


        public Filter build()
        {
            if (id == null || name == null || transition == null)
            {
                throw new IllegalStateException("Cannot create Filter. id, name and transition cannot be null!");
            }

            return new Filter(id, name, transitionLabel, transition);
        }
    }
}
