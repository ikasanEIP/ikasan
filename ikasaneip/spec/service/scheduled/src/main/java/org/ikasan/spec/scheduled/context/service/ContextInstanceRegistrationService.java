package org.ikasan.spec.scheduled.context.service;

import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;

import java.util.List;

/**
 * This interface represents a service for registering, scheduling, and managing context instances.
 */
public interface ContextInstanceRegistrationService {

    /**
     * Method to prepare a future context instance.
     *
     * @param contextName
     */
    void prepareFutureContextInstance(String contextName);



    /**
     * Reschedule a context using the provided context name and scheduler service.
     *
     * @param contextName The name of the context to reschedule.
     * @param contextInstanceSchedulerService The service responsible for managing context instance scheduling.
     */
    void reSchedule(String contextName, ContextInstanceSchedulerService contextInstanceSchedulerService);


    /**
     * Registers a context with the specified name and context instance scheduler service.
     *
     * @param contextName The name of the context to register.
     * @param contextInstanceSchedulerService The service used for managing context instance scheduling.
     */
    void register(String contextName, ContextInstanceSchedulerService contextInstanceSchedulerService);


    /**
     * Registers a context with the given parameters.
     *
     * @param contextName The name of the context to register.
     * @param contextParameterInstances The list of context parameter instances associated with the context.
     * @param contextInstanceSchedulerService The service used for managing context instance scheduling.
     * @return A string representing the registration status.
     */
    String register(String contextName, List<ContextParameterInstance> contextParameterInstances
        , ContextInstanceSchedulerService contextInstanceSchedulerService);


    /**
     * Deregisters a context by its name. This method removes the context from the scheduler service.
     *
     * @param contextName The name of the context to deregister.
     * @param contextInstanceSchedulerService The service used for managing context instance scheduling.
     */
    void deRegisterByName(String contextName, ContextInstanceSchedulerService contextInstanceSchedulerService);

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
