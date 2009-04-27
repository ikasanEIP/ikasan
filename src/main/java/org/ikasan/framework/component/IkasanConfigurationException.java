/*
 * $Id: IkasanConfigurationException.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/IkasanConfigurationException.java $
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.component;

// Imported log4j classes
import org.apache.log4j.Logger;

import org.ikasan.common.CommonExceptionType;
import org.ikasan.common.ExceptionType;
import org.ikasan.framework.FrameworkException;

/**
 * Exception encountered in the configuration of an Ikasan flow.
 * 
 * @author Ikasan Development Team
 */
public class IkasanConfigurationException extends FrameworkException
{
    /** Serial ID */
    private static final long serialVersionUID = -7496003165699436807L;

    /** Logger */
    private static Logger logger = Logger.getLogger(IkasanConfigurationException.class);

    /**
     * Creates a new instance of <code>IkasanConfigurationException</code> with the specified message.
     * 
     * @param message - The exception message
     */
    public IkasanConfigurationException(final String message)
    {
        super(message, (Throwable) null);
        logger.debug("IkasanConfigurationException(String) constructor " + "created IkasanConfigurationException");
    }

    /**
     * Creates a new instance of <code>IkasanConfigurationException</code> with the specified exception instance. The
     * detailed message of the <code>IkasanConfigurationException</code> is set to be the same as the one of the
     * specified exception instance.
     * 
     * @param cause - The exception cause
     */
    public IkasanConfigurationException(final Throwable cause)
    {
        super(cause.getMessage(), cause);
        logger.debug("IkasanConfigurationException(Throwable) constructor " + "created IkasanConfigurationException");
    }

    /**
     * Creates a new instance of <code>IkasanConfigurationException</code> with the specified message and exception
     * instance.
     * 
     * @param message - The exception message
     * @param cause - THe exception cause
     */
    public IkasanConfigurationException(final String message, final Throwable cause)
    {
        super(message, cause);
        logger.debug("IkasanConfigurationException(String,Throwable) constructor "
                + "created IkasanConfigurationException");
    }

    /**
     * Creates a new instance of <code>IkasanConfigurationException</code> with the specified message and exception
     * type.
     * 
     * @param message - The exception message
     * @param exceptionType - The exception type
     */
    public IkasanConfigurationException(final String message, final ExceptionType exceptionType)
    {
        super(message, (Throwable) null, exceptionType);
        logger.debug("IkasanConfigurationException(String,ExceptionType) constructor "
                + "created IkasanConfigurationException");
    }

    /**
     * Creates a new instance of <code>IkasanConfigurationException</code> with the specified exception instance and
     * type.
     * 
     * @param cause - The exception cause
     * @param exceptionType - The exception type
     */
    public IkasanConfigurationException(final Throwable cause, final ExceptionType exceptionType)
    {
        super(cause.getMessage(), cause, exceptionType);
        logger.debug("IkasanConfigurationException(Throwable,ExceptionType) constructor "
                + "created IkasanConfigurationException");
    }

    /**
     * Creates a new instance of <code>IkasanConfigurationException</code> with the specified message, exception
     * instance and type.
     * 
     * @param message - The exception message
     * @param cause - The exception cause
     * @param exceptionType - The exception type
     */
    public IkasanConfigurationException(final String message, final Throwable cause, final ExceptionType exceptionType)
    {
        super(message, cause, exceptionType);
        logger.debug("IkasanConfigurationException(String,Throwable,ExceptionType) constructor "
                + "created IkasanConfigurationException");
    }

    /**
     * Runs this class for test.
     * 
     * @param args - Arguments
     */
    public static void main(String args[])
    {
        new IkasanConfigurationException("test", CommonExceptionType.UNDEFINED);
        new IkasanConfigurationException(new Exception(), CommonExceptionType.UNDEFINED);
        new IkasanConfigurationException("test", new Exception(), CommonExceptionType.UNDEFINED);
    }
}
