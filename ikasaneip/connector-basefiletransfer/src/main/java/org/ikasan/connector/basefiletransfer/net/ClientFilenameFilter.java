/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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