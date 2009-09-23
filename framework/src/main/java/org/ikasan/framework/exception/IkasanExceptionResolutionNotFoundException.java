/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
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
