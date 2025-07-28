package org.ikasan.module.builder.model.module;

import java.util.Map;

public interface MultiTransition
{
    /**
     * Adds a transition to the MultiTransition object.
     * The transition is defined by the given context and Component node.
     *
     * @param context the context in which the transition occurs
     * @param component the Component node representing the transition
     */
    void addTransition(String context, Component component);

    /**
     * Retrieves a map of transitions associated with this MultiTransition.
     * Each key in the map represents a context and the corresponding value is the Component node.
     *
     * @return a map with String keys representing context and Component values representing nodes
     */
    Map<String, Component> getTransitions();
}
