package org.ikasan.backup.h2.exception;

public class InvalidH2ConnectionUrlException extends Exception {
    public InvalidH2ConnectionUrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
