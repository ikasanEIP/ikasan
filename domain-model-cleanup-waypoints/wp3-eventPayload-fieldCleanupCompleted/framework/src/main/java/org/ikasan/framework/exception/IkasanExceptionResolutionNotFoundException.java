/*
 * $Id$
 * $URL$
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
package org.ikasan.framework.exception;

// Imported log4j classes
import org.apache.log4j.Logger;
import org.ikasan.common.ExceptionType;
import org.ikasan.framework.FrameworkException;

/**
 * Exception when trying to resolve the exception resolution instance, 
 * but nothing is found.
 * 
 * @author Ikasan Development Team
 *
 */
public class IkasanExceptionResolutionNotFoundException
    extends FrameworkException
{



    /**
     * default serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
    
    /** Logger */
    private static Logger logger = Logger.getLogger(IkasanExceptionResolutionNotFoundException.class);

    /**
     * Creates a new instance of <code>IkasanExceptionResolutionNotFoundException</code>
     * with the specified message.
     * 
     * @param message 
     */
    public IkasanExceptionResolutionNotFoundException(final String message)
    {
        super(message, (Throwable)null);
        logger.debug("IkasanExceptionResolutionNotFoundException(String) constructor "
                   + "created IkasanExceptionResolutionNotFoundException");
    }

    /**
     * Creates a new instance of <code>IkasanExceptionResolutionNotFoundException</code> with the 
     * specified exception instance. The detailed message of the
     * <code>IkasanExceptionResolutionNotFoundException</code> is set to be the same as 
     * the one of the specified exception instance.
     *   
     * @param cause 
     */
    public IkasanExceptionResolutionNotFoundException(final Throwable cause)
    {
        super(cause.getMessage(), cause);
        logger.debug("IkasanExceptionResolutionNotFoundException(Throwable) constructor "
                   + "created IkasanExceptionResolutionNotFoundException");
    }

    /**
     * Creates a new instance of <code>IkasanExceptionResolutionNotFoundException</code>
     * with the specified message and exception instance.
     * 
     * @param message 
     * @param cause 
     */
    public IkasanExceptionResolutionNotFoundException(final String message, final Throwable cause)
    {
        super(message, cause);
        logger.debug("IkasanExceptionResolutionNotFoundException(String,Throwable) constructor "
                   + "created IkasanExceptionResolutionNotFoundException");
    }

    /**
     * Creates a new instance of <code>IkasanExceptionResolutionNotFoundException</code>
     * with the specified message and exception type.
     * 
     * @param message 
     * @param exceptionType 
     */
    public IkasanExceptionResolutionNotFoundException(final String message, 
                              final ExceptionType exceptionType)
    {
        super(message, (Throwable)null, exceptionType);
        logger.debug("IkasanExceptionResolutionNotFoundException(String,ExceptionType) constructor "
                   + "created IkasanExceptionResolutionNotFoundException");
    }

    /**
     * Creates a new instance of <code>IkasanExceptionResolutionNotFoundException</code>
     * with the specified exception instance and type.
     * 
     * @param cause 
     * @param exceptionType 
     */
    public IkasanExceptionResolutionNotFoundException(final Throwable cause, 
                              final ExceptionType exceptionType)
    {
        super(cause.getMessage(), cause, exceptionType);
        logger.debug("IkasanExceptionResolutionNotFoundException(Throwable,ExceptionType) constructor "
                   + "created IkasanExceptionResolutionNotFoundException");
    }

    /**
     * Creates a new instance of <code>IkasanExceptionResolutionNotFoundException</code>
     * with the specified message, exception instance and type.
     * 
     * @param message 
     * @param cause 
     * @param exceptionType 
     */
    public IkasanExceptionResolutionNotFoundException(final String message, 
                                           final Throwable cause,
                                           final ExceptionType exceptionType)
    {
        super(message, cause, exceptionType);
        logger.debug("IkasanExceptionResolutionNotFoundException(String,Throwable,ExceptionType) constructor "
                   + "created IkasanExceptionResolutionNotFoundException");
    }

}
