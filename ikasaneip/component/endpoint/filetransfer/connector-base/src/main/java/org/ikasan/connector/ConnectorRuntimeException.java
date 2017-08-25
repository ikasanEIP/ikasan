/*
 * $Id$
 * $URL$
 *
 * =============================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * =============================================================================
 */
package org.ikasan.connector;

import org.ikasan.filetransfer.CommonExceptionType;
import org.ikasan.filetransfer.CommonRuntimeException;
import org.ikasan.filetransfer.ExceptionType;

/**
 * ConnectorRuntimeException exception extends the CommonRuntimeException and
 * includes an ExceptionType to allow categorisation of the thrown exception at
 * the point it is raised. If no ExceptionType is specified a default of UNKNOWN
 * will be used.
 * 
 * @author Ikasan Development Team
 */
public class ConnectorRuntimeException extends CommonRuntimeException
{
    /** Serial ID */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new connector resource exception with <code>null</code> as
     * its detail message and an UNKNOWN ExceptionType.
     */
    public ConnectorRuntimeException()
    {
        super();
    }

    /**
     * Constructs a new connector resource exception with <code>null</code> as
     * its detail message and a specified exception type.
     * 
     * @param exceptionType - The exception type
     */
    public ConnectorRuntimeException(final ExceptionType exceptionType)
    {
        super(exceptionType);
    }

    /**
     * Constructs a new exception with the specified detail message and an
     * UNKNOWN ExceptionType.
     * 
     * @param message the detail message.
     */
    public ConnectorRuntimeException(final String message)
    {
        super(message, CommonExceptionType.UNDEFINED);
    }

    /**
     * Constructs a new exception with the specified detail message and given
     * ExceptionType.
     * 
     * @param message the detail message.
     * @param exceptionType - The exception type
     */
    public ConnectorRuntimeException(final String message, final ExceptionType exceptionType)
    {
        super(message, exceptionType);
    }

    /**
     * Constructs a new exception with the specified detail message and cause
     * and an UNKNOWN ExceptionType.
     * 
     * @param message the detail message.
     * @param cause the cause.
     */
    public ConnectorRuntimeException(final String message, final Throwable cause)
    {
        super(message, cause, CommonExceptionType.UNDEFINED);
    }

    /**
     * Constructs a new exception with the specified detail message and cause
     * and the specified exception type.
     * 
     * @param message the detail message.
     * @param cause the cause.
     * @param exceptionType - The exception type
     */
    public ConnectorRuntimeException(final String message, final Throwable cause, final ExceptionType exceptionType)
    {
        super(message, cause, exceptionType);
    }

    /**
     * Constructs a new exception with the specified cause and an UNKNOWN
     * ExceptionType.
     * 
     * @param cause the cause
     * @since 1.4
     */
    public ConnectorRuntimeException(final Throwable cause)
    {
        super(cause, CommonExceptionType.UNDEFINED);
    }

    /**
     * Constructs a new exception with the specified cause and the specified
     * exception type.
     * 
     * @param cause the cause
     * @param exceptionType - The exception type
     * @since 1.4
     */
    public ConnectorRuntimeException(final Throwable cause, final ExceptionType exceptionType)
    {
        super(cause, exceptionType);
    }
}
