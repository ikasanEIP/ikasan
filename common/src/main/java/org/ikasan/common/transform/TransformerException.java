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
package org.ikasan.common.transform;


/**
 * This class extends <code>java.lang.Exception</code> and can be used by 
 * different transformation classes to wrap exceptions that occur during the
 * transformation process.
 *
 * @author Ikasan Development Team
 *
 */
public class TransformerException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = -5599762715929371006L;

    /**
     * Creates a new instance of <code>TransformerException</code> with 
     * <code>null</code> as its detail message.
     */
    public TransformerException()
    {
        super();
    }
    
    /**
     * Creates a new instance of <code>TransformerException</code>
     * with the specified message.
     * 
     * @param message 
     */
    public TransformerException(String message)
    {
        super(message);
    }

    /**
     * Creates a new instance of <code>TransformerException</code>
     * with the specified message and the existing exception.
     * 
     * @param message 
     * @param e 
     */
    public TransformerException(String message, Throwable e)
    {
        super(message, e);
    }

    /**
     * Creates a new instance of <code>TransformerException</code>
     * wrapping the existing exception.
     * 
     * @param e 
     */
    public TransformerException(Throwable e)
    {
        super(e);
    }

}
