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
package org.ikasan.common.xml.parser;

// Imported sax classes
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/**
 * This class extends <code>org.xml.sax.SAXParseException</code>
 * that encapsulates an XML parse fatal error.
 *
 * @author Ikasan Development Team
 *
 */
public class FatalErrorSAXParseException
    extends SAXParseException
{
    /** Serial GUID */
    private static final long serialVersionUID = 6266340366950391902L;

    /**
     * Creates a new instance of <code>FatalErrorSAXParseException</code>
     * with the specified message and locator.
     * 
     * @param message 
     * @param locator 
     */
    public FatalErrorSAXParseException(String message, Locator locator)
    {
        super(message, locator);
    }

    /**
     * Creates a new instance of <code>FatalErrorSAXParseException</code>
     * with the specified message, locator and the existing exception.
     * 
     * @param message 
     * @param locator 
     * @param e 
     */
    public FatalErrorSAXParseException(String message, Locator locator,
                                       Exception e)
    {
        super(message, locator, e);
    }

    /**
     * Creates a new instance of <code>FatalErrorSAXParseException</code>
     * with the specified message, public identifier of the entity,
     * system identifier of the entity, exception line number and
     * exception column number.
     * 
     * @param message 
     * @param publicId 
     * @param systemId 
     * @param lineNumber 
     * @param columnNumber 
     */
    public FatalErrorSAXParseException(String message,
                                       String publicId, String systemId,
                                       int lineNumber, int columnNumber)
    {
        super(message, publicId, systemId, lineNumber, columnNumber);
    }

    /**
     * Creates a new instance of <code>FatalErrorSAXParseException</code>
     * with the specified message, public identifier of the entity,
     * system identifier of the entity, exception line number,
     * exception column number and the existing exception.
     * 
     * @param message 
     * @param publicId 
     * @param systemId 
     * @param lineNumber 
     * @param columnNumber 
     * @param e 
     */
    public FatalErrorSAXParseException(String message,
                                       String publicId, String systemId,
                                       int lineNumber, int columnNumber,
                                       Exception e)
    {
        super(message, publicId, systemId, lineNumber, columnNumber, e);
    }

    /**
     * Creates a new instance of <code>FatalErrorSAXParseException</code>
     * with the specified existing <code>SAXParseException</code> instance.
     * 
     * @param e 
     */
    public FatalErrorSAXParseException(SAXParseException e)
    {
        super(e.getMessage(), e.getPublicId(), e.getSystemId(),
              e.getLineNumber(), e.getColumnNumber(), e.getException());
    }

}
