/*
 * $Id: DefaultErrorListener.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/xml/transform/DefaultErrorListener.java $
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
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * This class implements interface
 * <code>javax.xml.transform.ErrorListener</code>
 * to report errors during transformation.
 *
 * @author Jun Suetake
 *
 */
public class DefaultErrorListener
    implements ErrorListener
{
    /**
     * Creates a new instance of <code>DefaultErrorListener</code>.
     */
    public DefaultErrorListener()
    {
        // Do Nothing
    }

    /**
     * ErrorListener - Warning.
     * @param e 
     * @throws TransformerException 
     */
    public void warning(TransformerException e)
	throws TransformerException
    {
        throw new WarningTransformerException(e);
    }

    /**
     * ErrorListener - Error.
     * @param e 
     * @throws TransformerException 
     */
    public void error(TransformerException e)
	throws TransformerException
    {
        throw new ErrorTransformerException(e);
    }

    /**
     * ErrorListener - Fatal error.
     * @param e 
     * @throws TransformerException 
     */
    public void fatalError(TransformerException e)
	throws TransformerException
    {
        throw new FatalErrorTransformerException(e);
    }

}
