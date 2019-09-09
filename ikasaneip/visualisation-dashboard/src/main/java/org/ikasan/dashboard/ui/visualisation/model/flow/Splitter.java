package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;

/**
 * Created by stewmi on 07/11/2018.
 */
public class Splitter extends AbstractSingleTransition
{
	public static final String IMAGE = "frontend/images/splitter.png";

    /**
     * Constructor
     *
     * @param id
     * @param name
     * @param transitionLabel
     * @param transition
     */
	public Splitter(String id, String name, String transitionLabel, Node transition)
    {
        super(id, name, transition, transitionLabel, IMAGE);
    }

    public static SplitterBuilder splitterBuilder()
    {
        return new SplitterBuilder();
    }

    /**
     * Builder class
     */
    public static class SplitterBuilder
    {
        private String id;
        private String name;
        private String transitionLabel;
        private Node transition;

        public SplitterBuilder withId(String id)
        {
            this.id = id;
            return this;
        }

        public SplitterBuilder withName(String name)
        {
            this.name = name;
            return this;
        }

        public SplitterBuilder withTransitionLabel(String transitionLabel)
        {
            this.transitionLabel = transitionLabel;
            return this;
        }

        public SplitterBuilder withTransition(Node transition)
        {
            this.transition = transition;
            return this;
        }


        public Splitter build()
        {
            if (id == null || name == null || transition == null)
            {
                throw new IllegalStateException("Cannot create Splitter. id, name and transition cannot be null!");
            }

            return new Splitter(id, name, transitionLabel, transition);
        }
    }
}
