package org.ikasan.spec.scheduled.instance.service;

public interface ContextInstancePublicationService<CONTEXT_INSTANCE> {

    /**
     * This method is responsible for publishing a context instance to an agent.
     *
     * @param contextUrl
     * @param instance
     */
    void publish(String contextUrl, CONTEXT_INSTANCE instance);

    /**
     * This method is responsible for removing a context instance from an agent.
     *
     * @param contextUrl
     * @param instance
     */
    void remove(String contextUrl, CONTEXT_INSTANCE instance);

    /**
     * This method is responsible for removing all context instances from an agent.
     *
     * @param contextUrl
     */
    void removeAll(String contextUrl);
}