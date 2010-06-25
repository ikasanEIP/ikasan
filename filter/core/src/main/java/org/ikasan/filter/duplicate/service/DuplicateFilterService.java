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

import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntry;

/**
 * Service interface for accessing persisted message. Implementors are responsible
 * for translating any message to the {@link DefaultFilterEntry}
 * @author Ikasan Development Team
 *
 */
public interface DuplicateFilterService
{
    /**
     * Message is new, persist it.
     * @param message
     */
    public void persistMessage(FilterEntry message);

    /**
     * Search for a message.
     * @param message the message to be found
     * @return true of message is found, false otherwise
     */
    public boolean isDuplicate(FilterEntry message);

    /**
     * Housekeep expired message filter entries
     */
    public void housekeep();
}
