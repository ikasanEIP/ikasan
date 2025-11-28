package org.ikasan.spec.bigqueue.service.exception;

/**
 * This class represents an exception that is thrown when a BigQueue is not found.
 */
public class BigQueueNotFoundException extends Exception {

    /**
     * Constructs a new BigQueueNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the Throwable.getMessage() method)
     */
    public BigQueueNotFoundException(String message) {
        super(message);
    }
}
