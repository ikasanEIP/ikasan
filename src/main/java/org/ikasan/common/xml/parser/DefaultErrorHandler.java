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
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This class implements interface <code>org.xml.sax.ErrorHandler</code>
 * to report errors while parsing XML documents.
 *
 * @author Ikasan Development Team
 */
public class DefaultErrorHandler
    implements ErrorHandler
{

    /**
     * Create a new instance of <code>CmiErrorHandler</code>
     * with the default trace level.
     */
    public DefaultErrorHandler()
    {
        // Do nothing
    }

    /**
     * ErrorHandler - Warning.
     * @param e 
     * @throws SAXException 
     */
    public void warning(SAXParseException e)
        throws SAXException
    {
        throw new WarningSAXParseException(e);
    }

    /**
     * ErrorHandler - Error.
     * @param e 
     * @throws SAXException 
     */
    public void error(SAXParseException e)
        throws SAXException
    {
        throw new ErrorSAXParseException(e);
    }

    /**
     * ErrorHandler - Fatal error.
     * @param e 
     * @throws SAXException 
     */
    public void fatalError(SAXParseException e)
        throws SAXException
    {
        throw new FatalErrorSAXParseException(e);
    }
}
