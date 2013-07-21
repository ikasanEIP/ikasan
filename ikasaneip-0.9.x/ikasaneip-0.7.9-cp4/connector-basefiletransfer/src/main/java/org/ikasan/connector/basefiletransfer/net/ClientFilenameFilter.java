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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ikasan Development Team 
 */
public class ClientFilenameFilter implements ClientFilter
{
    /** Logger */
    private static Logger logger =
        Logger.getLogger(ClientFilenameFilter.class);

    /** The Pattern as a String for the filter */
    private String patternString = null;
    /** The Pattern for the filter */
    private Pattern pattern = null;

    /**
     * Constructor
     *
     * @param patternString The filename pattern to match. This could be any
     * regular expression. Some examples are:
     * <ul>
     *  <li>"filename.java"</li>
     *  <li>"[Ff]ilename.java"</li>
     *  <li>"filename.+"</li>
     * </ul>
     */
    public ClientFilenameFilter(String patternString)
    {
        this.patternString = patternString;
        this.pattern = Pattern.compile(patternString);
    }

    /**
     * Method to match <code>ClientListEntry</code> objects based on
     * whether they the regular expression provided when the object is
     * instantiated.
     *
     * @param entry The <code>ClientListEntry</code> to match.
     * @return <code>true</code> if <code>ClientListEntry</code>'s filename
     *         matches the pattern, <code>false</code> otherwise.
     */
    public boolean match(ClientListEntry entry)
    {
        if (this.pattern != null && this.patternString != null)
        {
            File file = new File(entry.getUri().getPath());
            Matcher matcher = pattern.matcher(file.getName());
            return (matcher.matches()) ? true : false;
        }
        return false;
    }

    /**
     * Return true if the entry matches the pattern
     * @param entry
     * @return true if the entry matches the pattern else false
     */
    public boolean match(String entry)
    {
        if (this.pattern != null && this.patternString != null)
        {
            File file = new File(entry);
            Matcher matcher = pattern.matcher(file.getName());
            return (matcher.matches()) ? true : false;
        }
        return false;
    }

    /**
     * Method to filter out unmatched <code>ClientListEntry</code> objects
     * from an <code>List</code>.
     *
     * @param entries The <code>List</code> of <code>ClientListEntries</code>
     * to filter.
     * @return A <code>AList</code> of all <code>ClientListEntries</code>
     * matching this filter's criteria
     */
    public List<ClientListEntry> filter(List<ClientListEntry> entries)
    {
        List<ClientListEntry> validEntries =
            new ArrayList<ClientListEntry>();

        for (ClientListEntry entry : entries)
            if (this.match(entry))
            {
                logger.debug("Including entry [" + entry + "] to valid entries"); //$NON-NLS-1$ //$NON-NLS-2$
                validEntries.add(entry);
            }
            else
            {
                logger.debug("Filtering out entry [" + entry + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }

        return validEntries;
    }

    /**
     * Utility method for filtering local directories as returned by dir.list()
     * @param entries
     * @return List of filtered directories
     */
    public List<String> filter(String[] entries)
    {
        ArrayList<String> validEntries = new ArrayList<String>();
        for (String entry: entries)
            if (this.match(entry))
            {
                logger.info("Including entry [" + entry + "] to valid entries"); //$NON-NLS-1$ //$NON-NLS-2$
                validEntries.add(entry);
            }
            else
            {
                logger.info("Filtering out entry [" + entry + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        return validEntries;
    }
}