package org.ikasan.module.service;

/**
 * Exception thrown when spring config fails to initialise a bean.
 *
 * @author Ikasan Development Team
 */
public class MissingBeanConfigurationException extends RuntimeException
{

    public MissingBeanConfigurationException(String message, Throwable t){
        super(message,t);
    }
}
