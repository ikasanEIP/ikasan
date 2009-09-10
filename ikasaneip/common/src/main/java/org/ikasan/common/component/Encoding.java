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
package org.ikasan.common.component;

/**
 * DeliveryProperties allows indirect setting of properties on the JMS.
 * 
 * @author Ikasan Development Team
 */
public enum Encoding
{
    /** No encoding */
    NOENC("noenc"),
    /** Base 64 encoding */
    BASE64("base64"),
    /** Hex encoding */
    HEX("hex");
    
    /** Encoding type */
    private final String type;

    /**
     * Creates a new instance of <code>Encoding</code>
     * with the specified encoding type.
     * 
     * @param type 
     */
    private Encoding(final String type)
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
