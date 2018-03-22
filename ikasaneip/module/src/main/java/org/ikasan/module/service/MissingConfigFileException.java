package org.ikasan.module.service;

/**
 * Exception thrown when spring config fails to load one of config.xml defined in loader-conf.xml
 *
 * @author Ikasan Development Team
 */
public class MissingConfigFileException extends RuntimeException
{

    public MissingConfigFileException(String message, Throwable t){
        super(message,t);
    }
}
