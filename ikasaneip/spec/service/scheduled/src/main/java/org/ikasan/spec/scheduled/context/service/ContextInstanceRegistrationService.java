package org.ikasan.spec.scheduled.context.service;

import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;

import java.util.List;

public interface ContextInstanceRegistrationService {

    /**
     * Method to prepare a future context instance.
     *
     * @param contextName
     */
    void prepareFutureContextInstance(String contextName);

    /**
     * Method to re-schedule a context. This will remove any scheduled triggers
     * associated with the context and reschedule based on the new start time, timezone
     * or blackout windows.
     *
     * @param contextName
     */
    void reSchedule(String contextName);

    /**
     * Method to register a context by name.
     *
     * @param contextName
     */
    void register(String contextName);

    /**
     * Method to register a context by name.
     *
     * @param contextName
     * @param contextParameterInstances
     * @return
     */
    String register(String contextName, List<ContextParameterInstance> contextParameterInstances);

    /**
     * Method to deregister all context instance of the named context.
     *
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
