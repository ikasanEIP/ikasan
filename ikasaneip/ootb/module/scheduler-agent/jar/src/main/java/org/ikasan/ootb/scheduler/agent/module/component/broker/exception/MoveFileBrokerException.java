package org.ikasan.ootb.scheduler.agent.module.component.broker.exception;


/**
 * This class defines a custom exception to be thrown when an error occurs in the MoveFileBroker process.
 * It extends the RuntimeException class.
 *
 * The MoveFileBrokerException can be instantiated with a specific detail message and cause of the exception.
 */
public class MoveFileBrokerException extends RuntimeException {


    /**
     * Constructs a new MoveFileBrokerException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public MoveFileBrokerException(String message, Throwable cause) {
        super(message, cause);
    }
}
