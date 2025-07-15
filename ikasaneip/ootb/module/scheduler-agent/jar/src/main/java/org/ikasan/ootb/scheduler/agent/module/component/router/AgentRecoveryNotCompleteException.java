package org.ikasan.ootb.scheduler.agent.module.component.router;

/**
 * Exception for when the recovery of an agent is not complete.
 */
public class AgentRecoveryNotCompleteException extends RuntimeException {

    /**
     * Constructs a new AgentRecoveryNotCompleteException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     */
    public AgentRecoveryNotCompleteException(String message) {
        super(message);
    }
}
