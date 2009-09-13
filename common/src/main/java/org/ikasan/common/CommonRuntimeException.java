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
package org.ikasan.common;

import org.ikasan.common.ExceptionType;

/**
 * CommonRuntimeException exception extends the standard RuntimeException
 * and includes an ExceptionType to allow categorisation of the thrown
 * exception at the point it is raised.
 * If no ExceptionType is specified a default of UNDEFINED will be used.
 * 
 * @author Ikasan Development Team
 */
public class CommonRuntimeException
    extends RuntimeException
{
    /** Serial ID */
    private static final long serialVersionUID = 1L;

    /** Allow type definition to categorise the exception */
    protected ExceptionType exceptionType;

    /**
     * Constructs a new common runtime exception with <code>null</code> 
     * as its detail message and an UNDEFINED ExceptionType.
     */
    public CommonRuntimeException() 
    {
        this(CommonExceptionType.UNDEFINED);
    }

    /**
     * Constructs a new common runtime exception with <code>null</code> 
     * as its detail message and a specified exception type.
     * @param exceptionType 
     */
    public CommonRuntimeException(final ExceptionType exceptionType) 
    {
        this.exceptionType = exceptionType;
    }

    /**
     * Constructs a new common runtime exception with the specified 
     * detail message and an UNDEFINED ExceptionType.
     *
     * @param   message   the detail message.
     */
    public CommonRuntimeException(final String message)
    {
        this(message, CommonExceptionType.UNDEFINED);
    }

    /**
     * Constructs a new common runtime exception with the specified 
     * detail message and given ExceptionType.
     *
     * @param   message   the detail message.
     * @param exceptionType 
     */
    public CommonRuntimeException(final String message, final ExceptionType exceptionType)
    {
        super(message);
        this.exceptionType = exceptionType;
    }

    /**
     * Constructs a new common runtime exception with the specified 
     * detail message and cause and an UNDEFINED ExceptionType.
     * 
     * @param  message the detail message.
     * @param  cause the cause.
     */
    public CommonRuntimeException(final String message, final Throwable cause)
    {
        this(message, cause, CommonExceptionType.UNDEFINED);
    }

    /**
     * Constructs a new common runtime exception with the specified 
     * detail message and cause and the specified exception type.
     * 
     * @param  message the detail message.
     * @param  cause the cause.
     * @param  exceptionType 
     */
    public CommonRuntimeException(final String message, final Throwable cause, 
            ExceptionType exceptionType)
    {
        super(message, cause);
        this.exceptionType = exceptionType;
    }

    /**
     * Constructs a new common runtime exception with the specified cause
     * and an UNDEFINED ExceptionType.
     *
     * @param  cause the cause
     */
    public CommonRuntimeException(final Throwable cause)
    {
        this(cause, CommonExceptionType.UNDEFINED);
    }

    /**
     * Constructs a new common runtime exception with the specified cause
     * and the specified exception type.
     *
     * @param  cause the cause
     * @param exceptionType the exception type
     */
    public CommonRuntimeException(final Throwable cause, final ExceptionType exceptionType)
    {
        super(cause);
        this.exceptionType = exceptionType;
    }

    /**
     * Setter for exceptionType
     * @param exceptionType type of exception
     */
    public void setExceptionType(final ExceptionType exceptionType)
    {
        this.exceptionType = exceptionType;
    }

    /**
     * Getter for exceptionType
     * 
     * @return ExceptionType
     */
    public ExceptionType getExceptionType()
    {
        return this.exceptionType;
    }
    
}
