package org.ikasan.spec.scheduled.context.service;

public interface ContextInstanceRegistrationService {

    /**
     * Method to register a context by name.
     *
     * @param contextName
     * @return
     */
    String register(String contextName);

    /**
     * Method to deregister all context instance of the named context.
     * @param contextName
     */
    void deRegisterByName(String contextName);

    /**
     * Deregister a specific context instance.
     *
     * @param contextInstanceId
     */
    void deRegisterById(String contextInstanceId);

    /**
     * Manually deregister a context instance.
     *
     * @param contextInstanceId
     */
    void deregisterManually(String contextInstanceId);
}
