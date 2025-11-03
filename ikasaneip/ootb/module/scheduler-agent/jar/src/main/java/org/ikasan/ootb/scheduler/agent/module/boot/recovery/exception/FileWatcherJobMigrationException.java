package org.ikasan.ootb.scheduler.agent.module.boot.recovery.exception;

public class FileWatcherJobMigrationException extends RuntimeException {

    /**
     * Exception to be thrown when there is an issue related to the migration of a file watcher job.
     */
    public FileWatcherJobMigrationException() {
    }

    /**
     * Constructs a new FileWatcherJobMigrationException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public FileWatcherJobMigrationException(String message) {
        super(message);
    }

    /**
     * Constructs a new FileWatcherJobMigrationException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public FileWatcherJobMigrationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FileWatcherJobMigrationException with the specified cause.
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public FileWatcherJobMigrationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new FileWatcherJobMigrationException with the specified detail message, cause, and suppression and stack trace options.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     * @param enableSuppression whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     */
    public FileWatcherJobMigrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
