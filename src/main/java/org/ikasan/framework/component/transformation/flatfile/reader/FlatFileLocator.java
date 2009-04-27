/* 
 * $Id: FlatFileLocator.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/component/transformation/flatfile/reader/FlatFileLocator.java $
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
package org.ikasan.framework.component.transformation.flatfile.reader;

import org.xml.sax.Locator;

/**
 * @author Ikasan Development Team
 * 
 */
public class FlatFileLocator implements Locator
{
    /** Column number in the FlatFile */
    private int columnNumber;
    /** Line number in the FlatFile */
    private int lineNumber;

    /**
     * Constructor
     * 
     * @param columnNumber
     */
    public FlatFileLocator(int columnNumber)
    {
        this.columnNumber = columnNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.Locator#getColumnNumber()
     */
    public int getColumnNumber()
    {
        return columnNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.Locator#getLineNumber()
     */
    public int getLineNumber()
    {
        return lineNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.Locator#getPublicId()
     */
    public String getPublicId()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.Locator#getSystemId()
     */
    public String getSystemId()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
