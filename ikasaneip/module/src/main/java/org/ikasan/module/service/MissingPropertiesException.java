package org.ikasan.module.service;

/**
 * Exception thrown when spring config fails to load one of properties.
 *
 * @author Ikasan Development Team
 */
public class MissingPropertiesException extends RuntimeException
{

    public MissingPropertiesException(String message, Throwable t){
        super(message,t);
    }
}
