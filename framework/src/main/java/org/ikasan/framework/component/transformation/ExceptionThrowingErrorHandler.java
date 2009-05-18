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
