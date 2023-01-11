package org.ikasan.spec.scheduled.reset;

public interface ContextResetService {

    /**
     * @deprecated Since a context can have multiple instances, we may need to use resetContextInstance
     * Rest a context instance.
     *
     * @param contextName the name of the context.
     * @param holdCommandExecutionJob flag to determine if commands job should be on hold.
     */
    void resetContext(String contextName, boolean holdCommandExecutionJob);

    /**
     * Rest a context instance.
     *
     * @param contextInstanceId the name of the context.
     * @param holdCommandExecutionJob flag to determine if commands job should be on hold.
     */
    void resetContextInstance(String contextInstanceId, boolean holdCommandExecutionJob);
}
