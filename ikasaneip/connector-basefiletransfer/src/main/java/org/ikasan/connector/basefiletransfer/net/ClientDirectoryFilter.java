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

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @author Ikasan Development Team 
 */
public class ClientDirectoryFilter implements ClientFilter
{
    /** Logger */
    private static Logger logger = 
        Logger.getLogger(ClientDirectoryFilter.class);
    
    /**
     * Constructor
     */
    public ClientDirectoryFilter()
    {
        // Do Nothing
    }

    /**
     * Method to match <code>ClientListEntry</code> objects based on
     * whether they represent directories or not.
     * 
     * @param lsEntry The <code>ClientListEntry</code> to match.
     * @return <code>true</code> if <code>ClientListEntry</code> is a
     *         directory, <code>false</code> otherwise.
     */
    public boolean match(ClientListEntry lsEntry)
    {
        return (lsEntry.isDirectory()) ? true: false;
    }
    
    /**
     * Method to filter out unmatched <code>ClientListEntry</code> objects
     * from an ArrayList.
     * 
     * @param entries The <code>ArrayList</code> of
     *            <code>ClientListEntries</code> to filter.
     * @return An <code>ArrayList</code> of all
     *         <code>ClientListEntries</code> matching the
     *         <code>Filter</code>'s criteria
     */
    public List<ClientListEntry> filter(List<ClientListEntry> entries)
    {
        ArrayList<ClientListEntry> validEntries = 
            new ArrayList<ClientListEntry>();
        
        for (ClientListEntry entry : entries)
            if (this.match(entry))
            {
                logger.debug("Filtering out entry [" + entry + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                logger.debug("Including entry [" + entry + "] to valid entries"); //$NON-NLS-1$ //$NON-NLS-2$
                validEntries.add(entry);
            }

        return validEntries;
    }
}
