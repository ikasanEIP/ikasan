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
package org.ikasan.common.xml.transform;

// Imported trax classes
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

/**
 * This class extends <code>javax.xml.transform.TransformerException</code>
 * as error exception that occured during the transformation process.
 *
 * @author Ikasan Development Team
 *
 */
public class ErrorTransformerException
    extends TransformerException
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>ErrorTransformerException</code>
     * with the specified message.
     * 
     * @param message 
     */
    public ErrorTransformerException(String message)
    {
        super(message);
    }

    /**
     * Creates a new instance of <code>ErrorTransformerException</code>
     * with the specified message and source locator.
     * 
     * @param message 
     * @param locator 
     */
    public ErrorTransformerException(String message, SourceLocator locator)
    {
        super(message, locator);
    }

    /**
     * Creates a new instance of <code>ErrorTransformerException</code>
     * with the specified message, source locator and the existing exception.
     * 
     * @param message 
     * @param locator 
     * @param e 
     */
    public ErrorTransformerException(String message, SourceLocator locator,
                                     Throwable e)
    {
        super(message, locator, e);
    }

    /**
     * Creates a new instance of <code>ErrorTransformerException</code>
     * with the specified message and the existing exception.
     * 
     * @param message 
     * @param e 
     */
    public ErrorTransformerException(String message, Throwable e)
    {
        super(message, e);
    }

    /**
     * Creates a new instance of <code>ErrorTransformerException</code>
     * wrapping the existing exception.
     * 
     * @param e 
     */
    public ErrorTransformerException(Throwable e)
    {
        super(e);
    }

}
