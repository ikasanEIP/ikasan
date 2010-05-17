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

package org.ikasan.filter.duplicate.dao;

import org.ikasan.filter.duplicate.model.FilterEntry;

/**
 * DAO interface for interacting with filtered messages.
 * 
 * @author Summer
 *
 */
public interface FilteredMessageDao
{
    /**
     * Save new message.
     * @param message
     */
    public void save(FilterEntry message);

    /**
     * Try to find {@link FilterEntry} by its id: clientId and
     * criteria.
     * 
     * @param message {@link FilterEntry} to be found
     * 
     * @return The found {@link FilterEntry} or null if nothing
     *         found in persistence.
     */
    public FilterEntry findMessage(FilterEntry message);

    /**
     * Delete expired Filter Entries from persistence 
     */
    public void deleteAllExpired();
}
