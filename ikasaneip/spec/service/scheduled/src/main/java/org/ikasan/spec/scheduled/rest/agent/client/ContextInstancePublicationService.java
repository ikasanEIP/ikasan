package org.ikasan.spec.scheduled.rest.agent.client;

public interface ContextInstancePublicationService<CONTEXT_INSTANCE> {

    /**
     * This method is responsible for publishing a context instance to an agent.
     *
     * @param contextUrl
     * @param instance
     */
    void publish(String contextUrl, CONTEXT_INSTANCE instance);
}