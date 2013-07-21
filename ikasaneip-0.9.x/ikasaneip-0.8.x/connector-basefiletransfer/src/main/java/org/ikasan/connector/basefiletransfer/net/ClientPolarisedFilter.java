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

/**
 * Utility class to hold and apply an <code>ClientFilter</code> and return
 * results via a public <code>applyFilter()</code> method. This class allows
 * one to polarise the list of results returned by negating or accepting the
 * result of the <code>ClientFilter.match()</code> call.
 * 
 * For example:
 * 
 * If one applies the <code>ClientDirectoryFilter</code> on a list of
 * <code>ClientListEntry</code> objects, the filtered list will by default
 * contain all the matched entries which are directories. If the opposite is
 * required, then this class can be used with negative polarity (i.e. set to
 * <code>polarity = false</code> to return everything but the directories.
 * 
 * @author Ikasan Development Team 
 */
public class ClientPolarisedFilter
{
    /** client filter */
    public ClientFilter filter;
    
    /** Polartiy flag */
    public boolean polarity;

    /**
     * Default constructor
     * 
     * @param filter The relative <code>ClientFilter</code> to apply
     * @param polarity <code>false</code> to negate the filter results when
     *            the filter is applied; <code>true</code> otherwise.
     */
    public ClientPolarisedFilter(ClientFilter filter, boolean polarity)
    {
        this.filter = filter;
        this.polarity = polarity;
    }

    /**
     * Applies the <code>ClientFilter</code> on the
     * <code>List&lt;ClientListEntry&gt;</code> and return the resulting
     * polarised filtered list.
     * 
     * @param list 
     * @return The resulting <code>List&lt;ClientListEntry&gt;</code>.
     */
    public List<ClientListEntry> applyFilter(List<ClientListEntry> list)
    {
        List<ClientListEntry> filteredList = 
            new ArrayList<ClientListEntry>(list.size());
        
        for (ClientListEntry entry : list)
        {
            if (this.filter.match(entry) == this.polarity)
                filteredList.add(entry);
        }
        return filteredList;
    }

    /**
     * @return the filter
     */
    public ClientFilter getFilter()
    {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(ClientFilter filter)
    {
        this.filter = filter;
    }

    /**
     * @return the polarisation
     */
    public boolean isPolarity()
    {
        return polarity;
    }

    /**
     * @param polarity the polarisation to set
     */
    public void setPolarity(boolean polarity)
    {
        this.polarity = polarity;
    }
}
