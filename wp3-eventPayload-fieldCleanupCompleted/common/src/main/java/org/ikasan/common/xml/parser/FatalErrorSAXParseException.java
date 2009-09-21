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
