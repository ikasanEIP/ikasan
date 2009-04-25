/*
 * $Id: ConnectorRuntimeException.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/main/java/org/ikasan/connector/ConnectorRuntimeException.java $
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
package org.ikasan.connector;

import org.ikasan.common.CommonRuntimeException;
import org.ikasan.common.CommonExceptionType;
import org.ikasan.common.ExceptionType;

/**
 * ConnectorRuntimeException exception extends the CommonRuntimeException
 * and includes an ExceptionType to allow categorisation of the thrown
 * exception at the point it is raised.
 * If no ExceptionType is specified a default of UNKNOWN will be used.
 * 
 * @author Ikasan Development Team
 */
public class ConnectorRuntimeException
    extends CommonRuntimeException
{
    /** Serial ID */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new connector resource exception with <code>null</code> 
     * as its detail message and an UNKNOWN ExceptionType.
     */
    public ConnectorRuntimeException() 
    {
        super();
    }

    /**
     * Constructs a new connector resource exception with <code>null</code> 
     * as its detail message and a specified exception type.
     * @param exceptionType 
     */
    public ConnectorRuntimeException(final ExceptionType exceptionType) 
    {
        super(exceptionType);
    }

    /**
     * Constructs a new exception with the specified detail message
     * and an UNKNOWN ExceptionType.
     *
     * @param   message   the detail message.
     */
    public ConnectorRuntimeException(final String message)
    {
        super(message, CommonExceptionType.UNDEFINED);
    }

    /**
     * Constructs a new exception with the specified detail message
     * and given ExceptionType.
     *
     * @param   message   the detail message.
     * @param exceptionType 
     */
    public ConnectorRuntimeException(final String message, final ExceptionType exceptionType)
    {
        super(message, exceptionType);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause and an UNKNOWN ExceptionType.
     * 
     * @param  message the detail message.
     * @param  cause the cause.
     */
    public ConnectorRuntimeException(final String message, final Throwable cause)
    {
        super(message, cause, CommonExceptionType.UNDEFINED);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause and the specified exception type.
     * 
     * @param  message the detail message.
     * @param  cause the cause.
     * @param  exceptionType 
     */
    public ConnectorRuntimeException(final String message, final Throwable cause, 
            final ExceptionType exceptionType)
    {
        super(message, cause, exceptionType);
    }

    /**
     * Constructs a new exception with the specified cause
     * and an UNKNOWN ExceptionType.
     *
     * @param  cause the cause
     * @since  1.4
     */
    public ConnectorRuntimeException(final Throwable cause)
    {
        super(cause, CommonExceptionType.UNDEFINED);
    }

    /**
     * Constructs a new exception with the specified cause
     * and the specified exception type.
     *
     * @param  cause the cause
     * @param exceptionType 
     * @since  1.4
     */
    public ConnectorRuntimeException(final Throwable cause, final ExceptionType exceptionType)
    {
        super(cause, exceptionType);
    }

}
