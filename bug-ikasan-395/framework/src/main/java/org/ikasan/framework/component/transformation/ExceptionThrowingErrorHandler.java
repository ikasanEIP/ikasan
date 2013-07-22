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
package org.ikasan.framework.component.transformation;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
/**
 * Simple implementation of <code>ErrorHandler</code> capable of directly rethrowing the exception
 * 
 * @author Ikasan Development Team
 *
 */
public class ExceptionThrowingErrorHandler implements ErrorHandler
{
    /** Control flag for 'error' category of errors, defining whether or not such should be rethrown */
    private boolean throwExceptionOnError = true;

    /** Control flag for 'fatalError' category of errors, defining whether or not such should be rethrown */
    private boolean throwExceptionOnFatalError = true;

    /** Control flag for 'warning' category of errors, defining whether or not such should be rethrown */
    private boolean throwExceptionOnWarning = true;

    /**
     * Default constructor
     */
    public ExceptionThrowingErrorHandler()
    {
        super();
    }

    /**
     * Constructor overriding default value for boolean flags
     * @param throwExceptionOnError Boolean flag for 'error' category of errors
     * @param throwExceptionOnFatalError Boolean flag for 'fatalError' category of errors
     * @param throwExceptionOnWarning  Boolean flag for 'warning' category of errors
     */
    public ExceptionThrowingErrorHandler(boolean throwExceptionOnError, boolean throwExceptionOnFatalError, boolean throwExceptionOnWarning)
    {
        this.throwExceptionOnError = throwExceptionOnError;
        this.throwExceptionOnFatalError = throwExceptionOnFatalError;
        this.throwExceptionOnWarning = throwExceptionOnWarning;
    }

    /* 
     * (non-Javadoc)
     * @see javax.xml.sax.ErrorHandler#error(javax.xml.sax.SAXParseException)
     */
    public void error(SAXParseException exception) throws SAXException
    {
        if (this.throwExceptionOnError)
        {
            throw exception;
        }
    }

    /* 
     * (non-Javadoc)
     * @see javax.xml.sax.ErrorHandler#fatalError(javax.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException exception) throws SAXException
    {
        if (this.throwExceptionOnFatalError)
        {
            throw exception;
        }
    }

    /* 
     * (non-Javadoc)
     * @see javax.xml.sax.ErrorHandler#fatalError(javax.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException exception) throws SAXException
    {
        if (this.throwExceptionOnWarning)
        {
            throw exception;
        }
    }
}
