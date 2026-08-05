package org.ikasan.component.endpoint.pulsar.schema;

/**
 * Exception thrown when Pulsar schema creation fails.
 *
 * @author Ikasan Development Team
 */
public class PulsarSchemaCreationException extends RuntimeException {

    /**
     * Constructs a new {@code PulsarSchemaCreationException} with the specified detail message.
     *
     * @param message the detail message that provides more context about the exception.
     */
    public PulsarSchemaCreationException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code PulsarSchemaCreationException} with the specified detail message and cause.
     *
     * @param message the detail message that provides more context about the exception.
     * @param cause the cause of the exception, which can be used to trace the root problem.
     */
    public PulsarSchemaCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
