/* 
 * $Id: PagedWiretapSearchResult.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/event/wiretap/model/PagedWiretapSearchResult.java $
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
package org.ikasan.framework.event.wiretap.model;

import java.io.Serializable;
import java.util.List;

/**
 * Search result Data Transfer Object for WireTapEvents
 * 
 * This DTO object contains:
 *  + a List of WiretapEventHeaders (lightweight references to the heavy weight WiretapEvent)
 *      these are the page contents
 *  + resultSize - referring to the total size of the search results being paged
 *  + firstResult - the position in the greater result set where the first element of this page exists
 * 
 * @author Ikasan Development Team
 *
 */
public class PagedWiretapSearchResult implements Serializable
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7722626010968234606L;

    private List<WiretapEventHeader> wiretapEventHeaders;
    
    private int firstResult;
    
    private int resultSize;

    public PagedWiretapSearchResult(List<WiretapEventHeader> wiretapEventHeaders, int resultSize, int firstResult)
    {
        super();
        this.wiretapEventHeaders = wiretapEventHeaders;
        this.resultSize = resultSize;
        this.firstResult = firstResult;
    }

    public int getFirstResult()
    {
        return firstResult;
    }

    public int getResultSize()
    {
        return resultSize;
    }

    public List<WiretapEventHeader> getWiretapEventHeaders()
    {
        return wiretapEventHeaders;
    }
    
    public int getFirstIndex(){
        return firstResult+1;
    }
    
    public int getLastIndex(){
        return firstResult + wiretapEventHeaders.size();
    }
}
