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
package org.ikasan.filter.duplicate.service;

import org.ikasan.filter.duplicate.dao.FilteredMessageDao;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.filter.duplicate.service.DuplicateFilterService;

/**
 * The default implementation for {@link DuplicateFilterService}
 * 
 * @author Ikasan Development Team
 *
 */
public class DefaultDuplicateFilterService implements DuplicateFilterService
{
    /** {@link FilteredMessageDao} for accessing encountered messages*/
    private final FilteredMessageDao dao;

    /**
     * Constructor
     * @param dao
     * @param converter
     */
    public DefaultDuplicateFilterService(final FilteredMessageDao dao)
    {
        this.dao = dao;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.filter.duplicate.service.DuplicateFilterService#isDuplicate(java.lang.String)
     */
    public boolean isDuplicate(FilterEntry message)
    {
        FilterEntry messageEntryFound = this.dao.findMessage(message);
        if (messageEntryFound == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.filter.duplicate.service.DuplicateFilterService#persistMessage(java.lang.String)
     */
    public void persistMessage(FilterEntry message)
    {
        this.dao.save(message);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.filter.duplicate.service.DuplicateFilterService#housekeepExpiredMessages()
     */
    public void housekeep()
    {
        this.dao.deleteAllExpired();
    }
}
