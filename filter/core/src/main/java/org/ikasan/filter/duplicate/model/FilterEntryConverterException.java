/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2010 Mizuho International plc. and individual contributors as indicated
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
package org.ikasan.filter.duplicate.model;

import org.ikasan.filter.FilterException;

/**
 * FilterEntryConvertorException defines the exception on the 
 * FilterEntryConverter contract.
 * 
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("serial")
public class FilterEntryConverterException extends FilterException 
{
    /**
     * Constructor
     * @param cause
     */
    public FilterEntryConverterException(Throwable cause)
    {
       super(cause);
    }

    /**
     * Constructor
     * @param message
     */
    public FilterEntryConverterException(String message) 
    {
        super(message);
    }
}
