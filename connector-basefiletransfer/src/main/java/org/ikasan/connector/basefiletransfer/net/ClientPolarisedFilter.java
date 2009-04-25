/*
 * $Id: ClientPolarisedFilter.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/java/org/ikasan/connector/basefiletransfer/net/ClientPolarisedFilter.java $
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
