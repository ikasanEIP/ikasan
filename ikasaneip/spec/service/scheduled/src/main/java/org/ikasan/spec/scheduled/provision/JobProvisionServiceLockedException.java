package org.ikasan.spec.scheduled.provision;

public class JobProvisionServiceLockedException extends RuntimeException {
    /**
     * Constructs a new JobProvisionServiceLockedException with no detail message.
     */
    public JobProvisionServiceLockedException() {
    }

    /**
     * Constructs a new JobProvisionServiceLockedException with the specified detail message.
     *
     * @param message the detail message
     */
    public JobProvisionServiceLockedException(String message) {
        super(message);
    }

    /**
     * Constructs a new JobProvisionServiceLockedException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public JobProvisionServiceLockedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new JobProvisionServiceLockedException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public JobProvisionServiceLockedException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new JobProvisionServiceLockedException with the specified detail message, cause,
     * suppression enabled or disabled, and stack trace being writable or not.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     * @param enableSuppression whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     */
    public JobProvisionServiceLockedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
