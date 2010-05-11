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

package org.ikasan.filter.duplicate;

import org.ikasan.filter.FilterRule;
import org.ikasan.filter.duplicate.service.DuplicatesFilterService;

/**
 * A {@link FilterRule} determining if a message has been "seen" before, or not.
 * 
 * @author Summer
 *
 */
public class IsDuplicateFilterRule implements FilterRule
{
    /** Service to access previous encountered messages*/
    private final DuplicatesFilterService filterService;

    /**
     * Constructor 
     * @param filterService
     */
    public IsDuplicateFilterRule(final DuplicatesFilterService filterService)
    {
        this.filterService = filterService;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.filter.FilterRule#accept(java.lang.String)
     */
    public boolean accept(String message)
    {
        boolean messageFound = this.filterService.isDuplicate(message);
        if (!messageFound)
        {
            this.filterService.persistMessage(message);
            return true;
        }
        else
        {
            return false;
        }
    }
}
