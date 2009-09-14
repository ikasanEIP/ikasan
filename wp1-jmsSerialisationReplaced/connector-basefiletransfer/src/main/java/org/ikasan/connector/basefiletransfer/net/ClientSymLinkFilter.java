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
package org.ikasan.connector.basefiletransfer.net;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Symbolic Link filter
 * 
 * @author Ikasan Development Team 
 */
public class ClientSymLinkFilter implements ClientFilter
{
    /** logger */
    private static Logger logger = 
        Logger.getLogger(ClientSymLinkFilter.class);
    
    /**
     * Constructor
     */
    public ClientSymLinkFilter()
    {
        // Do Nothing;
    }

    /**
     * Method to match <code>ClientListEntry</code> objects based on
     * whether they represent symbolic links or not.
     * 
     * @param entry The <code>ClientListEntry</code> to match.
     * @return <code>true</code> if <code>ClientListEntry</code> is a
     *         symbolic link, <code>false</code> otherwise.
     */
    public boolean match(ClientListEntry entry)
    {
        return (entry.isLink()) ? true : false;
    }

    /**
     * Method to filter out unmatched <code>ClientListEntry</code> objects
     * from a List.
     * 
     * @param entries The <code>List</code> of <code>ClientListEntries</code> 
     * to filter.
     * @return A <code>List</code> of all <code>ClientListEntries</code> 
     * matching the <code>Filter</code>'s criteria.
     * 
     */
    public List<ClientListEntry> filter(List<ClientListEntry> entries)
    {
        ArrayList<ClientListEntry> validEntries = 
            new ArrayList<ClientListEntry>();
        
        for (ClientListEntry entry : entries)
            if (this.match(entry))
            {
                logger.debug("Filtering out entry [" + entry + "]");  //$NON-NLS-1$//$NON-NLS-2$
            }
            else
            {
                logger.debug("Including entry [" + entry + "] to valid entries");  //$NON-NLS-1$//$NON-NLS-2$
                validEntries.add(entry);
            }

        return validEntries;
    }
}