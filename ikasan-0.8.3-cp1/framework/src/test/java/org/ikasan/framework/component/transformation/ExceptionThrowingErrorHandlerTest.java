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
