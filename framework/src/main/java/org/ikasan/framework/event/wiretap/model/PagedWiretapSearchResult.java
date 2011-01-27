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
package org.ikasan.framework.event.wiretap.model;

import java.io.Serializable;
import java.util.List;

/**
 * Search result Data Transfer Object for WireTapFlowEvents
 * 
 * This DTO object contains:
 *  + a List of WiretapFlowEventHeaders (lightweight references to the heavy weight WiretapFlowEvent)
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

    private List<WiretapEventHeader> wiretapFlowEventHeaders;
    
    private int firstResult;
    
    private long resultSize;

    public PagedWiretapSearchResult(List<WiretapEventHeader> wiretapFlowEventHeaders, long resultSize, int firstResult)
    {
        super();
        this.wiretapFlowEventHeaders = wiretapFlowEventHeaders;
        this.resultSize = resultSize;
        this.firstResult = firstResult;
    }

    public int getFirstResult()
    {
        return firstResult;
    }

    public long getResultSize()
    {
        return resultSize;
    }

    public List<WiretapEventHeader> getWiretapFlowEventHeaders()
    {
        return wiretapFlowEventHeaders;
    }
    
    public int getFirstIndex(){
        return firstResult+1;
    }
    
    public int getLastIndex(){
        return firstResult + wiretapFlowEventHeaders.size();
    }
}
