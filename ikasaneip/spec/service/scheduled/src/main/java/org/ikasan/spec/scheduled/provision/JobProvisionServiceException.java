package org.ikasan.spec.scheduled.provision;

public class JobProvisionServiceException extends RuntimeException {
    /**
     * Constructs a new JobProvisionServiceException with no detail message.
     */
    public JobProvisionServiceException() {
    }

    /**
     * Constructs a new JobProvisionServiceException with the specified detail message.
     *
     * @param message the detail message
     */
    public JobProvisionServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new JobProvisionServiceException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public JobProvisionServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new JobProvisionServiceException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public JobProvisionServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new JobProvisionServiceException with the specified detail message, cause, suppression enabled
     * or disabled, and stack trace being writable or not.
     *
     * @param message            the detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause              the cause (which is saved for later retrieval by the getCause() method)
     * @param enableSuppression  whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     */
    public JobProvisionServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
