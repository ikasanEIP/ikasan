package org.ikasan.spec.security.service;

public class EntitySecurityProvisionServiceException extends Exception {

    /**
     * Constructs a new EntitySecurityProvisionServiceException with no detail message or cause.
     * This exception is typically used to indicate an error related to security provisioning
     * for entities within the application.
     */
    public EntitySecurityProvisionServiceException() {
    }

    /**
     * Constructs a new EntitySecurityProvisionServiceException with the specified detail message.
     *
     * @param message the detail message, which provides more information about the exception.
     */
    public EntitySecurityProvisionServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new EntitySecurityProvisionServiceException with the specified detail message
     * and cause.
     *
     * @param message the detail message, which provides additional information about the exception.
     * @param cause the cause of the exception, which allows chaining exceptions to retain the
     *              underlying reason for the error.
     */
    public EntitySecurityProvisionServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new EntitySecurityProvisionServiceException with the specified cause.
     *
     * @param cause the cause of the exception, which may be retrieved later
     *              using the {@link Throwable#getCause()} method. A null value
     *              is permitted, and indicates that the cause is nonexistent or unknown.
     */
    public EntitySecurityProvisionServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new EntitySecurityProvisionServiceException with the specified detail
     * message, cause, suppression enabled or disabled, and writable stack trace enabled
     * or disabled.
     *
     * @param message the detail message, which provides additional information about the
     *                exception. Can be null.
     * @param cause the cause of the exception, which can provide the underlying reason
     *              for this exception. Can be null.
     * @param enableSuppression whether or not suppression is enabled or disabled for
     *                          this exception.
     * @param writableStackTrace whether or not the stack trace should be writable
     *                           for this exception.
     */
    public EntitySecurityProvisionServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
