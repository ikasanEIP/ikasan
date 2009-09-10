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
package org.ikasan.common.component;

import org.ikasan.common.CommonException;
import org.ikasan.common.ExceptionType;

/**
 * Unknown Mesage Content exception - thrown if we cannot determine the
 * incoming message content to be an Envelope or a Payload.
 * 
 * @author Ikasan Development Team
 */
public class UnknownMessageContentException
    extends CommonException
{
    /** Serial UID */
    private static final long serialVersionUID = 5794502845958985994L;

    /**
     * Constructs a new connector resource exception with <code>null</code> 
     * as its detail message.
     */
    public UnknownMessageContentException() 
    {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public UnknownMessageContentException(final String message)
    {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     * 
     * @param  message the detail message.
     * @param  cause the cause.
     */
    public UnknownMessageContentException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause
     * @since  1.4
     */
    public UnknownMessageContentException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs a new connector resource exception with a category exception type. 
     * @param  exceptionType  category type of the exception
     */
    public UnknownMessageContentException(final ExceptionType exceptionType) 
    {
        super(exceptionType);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     * @param  exceptionType  category type of the exception
     */
    public UnknownMessageContentException(final String message, final ExceptionType exceptionType)
    {
        super(message, exceptionType);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     * 
     * @param  message the detail message.
     * @param  cause the cause.
     * @param  exceptionType  category type of the exception
     */
    public UnknownMessageContentException(final String message, final Throwable cause, 
            final ExceptionType exceptionType)
    {
        super(message, cause, exceptionType);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause
     * @param  exceptionType  category type of the exception
     * @since  1.4
     */
    public UnknownMessageContentException(final Throwable cause, final ExceptionType exceptionType)
    {
        super(cause, exceptionType);
    }

}
