package org.ikasan.spec.scheduled.reset;

public interface ContextResetService {

    /**
     * Rest a context instance.
     *
     * @param contextName the name of the context.
     * @param holdCommandExecutionJob flag to determine if commands job should be on hold.
     */
    void resetContext(String contextName, boolean holdCommandExecutionJob);
}
