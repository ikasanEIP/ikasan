package org.ikasan.backup.h2.exception;

public class H2DatabaseValidationException extends Exception {
    public H2DatabaseValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
