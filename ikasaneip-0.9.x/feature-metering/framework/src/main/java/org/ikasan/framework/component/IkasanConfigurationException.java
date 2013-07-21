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
