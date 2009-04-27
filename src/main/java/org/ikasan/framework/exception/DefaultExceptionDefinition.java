/*
 * $Id: DefaultExceptionDefinition.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/exception/DefaultExceptionDefinition.java $
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
package org.ikasan.framework.exception;

import org.ikasan.common.ExceptionType;

/**
 * Default implementation of an Ikasan Exception Definition as utilised by the IkasanExceptionResolver concrete
 * implementation.
 * 
 * @author Ikasan Development Team
 */
public class DefaultExceptionDefinition
{
    /** Resolution of this exception definition */
    private IkasanExceptionResolution resolution;

    /** ClassName of this exception definition */
    private String className;

    /** Optional type of this exception definition */
    private ExceptionType type;

    /**
     * Constructor
     * 
     * @param resolution The resolution
     * @param className The class name of this definition
     */
    public DefaultExceptionDefinition(final IkasanExceptionResolution resolution, final String className)
    {
        this.resolution = resolution;
        this.className = className;
    }

    /**
     * Constructor
     * 
     * @param resolution The resolution
     * @param className The class name of this definition
     * @param type The type of this definition
     */
    public DefaultExceptionDefinition(final IkasanExceptionResolution resolution, final String className,
            final ExceptionType type)
    {
        this.resolution = resolution;
        this.className = className;
        this.type = type;
    }

    /**
     * @return the resolution
     */
    public IkasanExceptionResolution getResolution()
    {
        return this.resolution;
    }

    /**
     * @param resolution the resolution to set
     */
    public void setResolution(IkasanExceptionResolution resolution)
    {
        this.resolution = resolution;
    }

    /**
     * @return the className
     */
    public String getClassName()
    {
        return this.className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className)
    {
        this.className = className;
    }

    /**
     * @return the type
     */
    public ExceptionType getType()
    {
        return this.type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ExceptionType type)
    {
        this.type = type;
    }
}
