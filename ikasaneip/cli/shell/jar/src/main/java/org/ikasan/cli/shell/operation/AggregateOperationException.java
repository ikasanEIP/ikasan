package org.ikasan.cli.shell.operation;

public class AggregateOperationException extends RuntimeException {

    /**
     * Constructs a new AggregateOperationException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public AggregateOperationException(String message) {
        super(message);
    }


    /**
     * Constructs a new AggregateOperationException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public AggregateOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
