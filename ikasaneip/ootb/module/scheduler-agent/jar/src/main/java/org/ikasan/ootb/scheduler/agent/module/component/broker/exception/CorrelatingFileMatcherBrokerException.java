package org.ikasan.ootb.scheduler.agent.module.component.broker.exception;

/**
 * Exception class to represent an error that occurred in the CorrelatingFileMatcherBroker.
 * Extends RuntimeException, making it an unchecked exception.
 *
 * This class is used to wrap any exceptions that occur during the operation of the CorrelatingFileMatcherBroker.
 * It provides a constructor that accepts a message to describe the error and a cause to provide more information about the exception.
 */
public class CorrelatingFileMatcherBrokerException extends RuntimeException {


    /**
     * Constructs a new CorrelatingFileMatcherBrokerException with the specified detail message and cause.
     *
     * @param message a description of the error that occurred
     * @param cause the cause of the exception
     */
    public CorrelatingFileMatcherBrokerException(String message, Throwable cause) {
        super(message, cause);
    }
}
