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

import junit.framework.Assert;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Test class for ExceptionThrowingErrorListener
 * 
 * @author Ikasan Development Team
 * 
 */
public class ExceptionThrowingErrorHandlerTest
{
    /**
     * An exception to handle
     */
    private SAXParseException saxParseException = new SAXParseException("saxParseException", null);

    /**
     * Tests that with default constructor arguments, error method rethrows same
     * exception
     */
    @Test
    public void testError_WithDefautSettings()
    {
        ExceptionThrowingErrorHandler errorHandler = new ExceptionThrowingErrorHandler();
        SAXException thrownException = null;
        try
        {
            errorHandler.error(saxParseException);
            Assert.fail("Exception should have been thrown");
        }
        catch (SAXException e)
        {
            thrownException = e;
        }
        Assert.assertEquals("Thrown exception should have been the exception passed to listener", saxParseException, thrownException);
    }

    /**
     * Tests that with default constructor arguments, fatalError method rethrows
     * same exception
     */
    @Test
    public void testFatalError_WithDefautSettings()
    {
        ExceptionThrowingErrorHandler errorHandler = new ExceptionThrowingErrorHandler();
        SAXException thrownException = null;
        try
        {
            errorHandler.fatalError(saxParseException);
            Assert.fail("Exception should have been thrown");
        }
        catch (SAXException e)
        {
            thrownException = e;
        }
        Assert.assertEquals("Thrown exception should have been the exception passed to listener", saxParseException, thrownException);
    }

    /**
     * Tests that with default constructor arguments, warning method rethrows
     * same exception
     */
    @Test
    public void testWarning_WithDefautSettings()
    {
        ExceptionThrowingErrorHandler errorHandler = new ExceptionThrowingErrorHandler();
        SAXException thrownException = null;
        try
        {
            errorHandler.warning(saxParseException);
            Assert.fail("Exception should have been thrown");
        }
        catch (SAXException e)
        {
            thrownException = e;
        }
        Assert.assertEquals("Thrown exception should have been the exception passed to listener", saxParseException, thrownException);
    }

    /**
     * Tests that with constructor argument set to false, error method does not
     * rethrow exception
     * @throws SAXException -
     */
    @Test
    public void testError_WithRethrowingDisabled() throws SAXException
    {
        ExceptionThrowingErrorHandler errorHandler = new ExceptionThrowingErrorHandler(false, true, true);
        errorHandler.error(saxParseException);
    }

    /**
     * Tests that with constructor argument set to false, fatalError method does
     * not rethrow exception
     * 
     * @throws SAXException - 
     */
    @Test
    public void testFatalError_WithRethrowingDisabled() throws SAXException
    {
        ExceptionThrowingErrorHandler errorHandler = new ExceptionThrowingErrorHandler(true, false, true);
        errorHandler.fatalError(saxParseException);
    }

    /**
     * Tests that with constructor argument set to false, fatalError method does
     * not rethrow exception
     * 
     * @throws SAXException - 
     */
    @Test
    public void testWarning_WithRethrowingDisabled() throws SAXException
    {
        ExceptionThrowingErrorHandler errorHandler = new ExceptionThrowingErrorHandler(true, true, false);
        errorHandler.warning(saxParseException);
    }
}
