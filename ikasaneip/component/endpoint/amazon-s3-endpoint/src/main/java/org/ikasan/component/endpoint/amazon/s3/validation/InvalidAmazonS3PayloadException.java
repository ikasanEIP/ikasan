package org.ikasan.component.endpoint.amazon.s3.validation;

/**
 * To be thrown when the amazon s3 payload is missing required properties. The user should configure ikasan
 * to exclude these payloads
 */
public class InvalidAmazonS3PayloadException extends RuntimeException{

    /**
     * Constructor
     *
     * @param cause - The cause
     */
    public InvalidAmazonS3PayloadException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructor
     *
     * @param message - The exception message
     */
    public InvalidAmazonS3PayloadException(String message)
    {
        super(message);
    }
}
