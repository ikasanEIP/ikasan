/*
 * $Id: ClientCommandMkdirException.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/java/org/ikasan/connector/basefiletransfer/net/ClientCommandMkdirException.java $
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
package org.ikasan.connector.basefiletransfer.net;

/**
 * @author Ikasan Development Team 
 */
public class ClientCommandMkdirException extends Exception
{
    /** GUID */
    private static final long serialVersionUID = 1L;

    /** Default Constructor */
    public ClientCommandMkdirException()
    {
        // Do Nothing
    }

    /**
     * @param message
     * @param cause
     */
    public ClientCommandMkdirException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ClientCommandMkdirException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public ClientCommandMkdirException(Throwable cause)
    {
        super(cause);
    }

}
