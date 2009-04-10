/*
 * $Id: Format.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/component/Format.java $
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
package org.ikasan.common.component;

/**
 * Enumeration of formats used within the Payload object.
 * 
 * @author Jeff Mitchell
 */
public enum Format
{
    /** XML */
    XML("xml"),
    /** CSV */
    CSV("csv"),
    /** Reference */
    REFERENCE("reference");
    
    /** Encoding type */
    private final String type;

    /**
     * Constructor
     * @param type 
     */
    private Format(final String type)
    {
        this.type = type;
    }
        
    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString()
    {
        return this.type;
    }
}
