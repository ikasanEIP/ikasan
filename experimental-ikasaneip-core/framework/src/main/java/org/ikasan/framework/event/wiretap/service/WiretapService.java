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
package org.ikasan.framework.event.wiretap.service;

import java.util.Date;
import java.util.Set;

import org.ikasan.framework.event.wiretap.model.PagedWiretapSearchResult;
import org.ikasan.framework.event.wiretap.model.WiretapEvent;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.ikasan.spec.flow.event.FlowEvent;

/**
 * This Service allows <code>FlowEvent</code>s to be dumped out at runtime for later
 * retrieval and inspection
 * 
 * @author Ikasan Development Team
 */
public interface WiretapService
{
    /**
     * @deprecated - Use other findWiretapFlowEvents instead
     * 
     *             Allows previously stored FlowEvents to be searched for.
     * 
     *             By default the search has no restrictions. Specifying any of
     *             the arguments as anything other than null will cause the
     *             search to be restricted by an exact match on that field
     * 
     * @param moduleNames - Set of names of modules to include in search - must
     *            contain at least one moduleName
     * @param moduleFlow - The name of Flow internal to the Module
     * @param componentName - The name of the component
     * @param eventId - The FlowEvent Id
     * @param payloadId - The Payload Id
     * @param fromDate - Include only events after fromDate
     * @param untilDate - Include only events before untilDate
     * @param payloadContent - The Payload content
     * @param pageSize - how many results to return in the result
     * @param pageNo - page index into the greater result set
     * 
     * @throws IllegalArgumentException - if moduleNames is null or empty
     * @return List of <code>WiretapFlowEventHeader</code> representing the result
     *         of the search
     */
    public PagedWiretapSearchResult findWiretapEvents(Set<String> moduleNames, String moduleFlow, String componentName, String eventId, String payloadId, Date fromDate,
            Date untilDate, String payloadContent, int pageSize, int pageNo);

    /**
     * Allows previously stored FlowEvents to be searched for.
     * 
     * @param pageNo - page index into the greater result set
     * @param pageSize - how many results to return in the result
     * @param orderBy - The field to order by
     * @param orderAscending - Ascending flag
     * @param moduleNames - Set of names of modules to include in search - must
     *            contain at least one moduleName
     * @param moduleFlow - The name of Flow internal to the Module
     * @param componentName - The name of the component
     * @param eventId - The FlowEvent Id
     * @param payloadId - The Payload Id
     * @param fromDate - Include only events after fromDate
     * @param untilDate - Include only events before untilDate
     * @param payloadContent - The Payload content
     * 
     * @throws IllegalArgumentException - if moduleNames is null or empty
     * @return List of <code>WiretapFlowEventHeader</code> representing the result
     *         of the search
     */
    public PagedSearchResult<WiretapEvent> findWiretapEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending, Set<String> moduleNames,
            String moduleFlow, String componentName, String eventId, String payloadId, Date fromDate, Date untilDate, String payloadContent);

    /**
     * Retrieve a specific <code>WiretapFlowEvent</code> by Id
     * 
     * @param wiretapFlowEventId - The id of the wiretap event to retrieve
     * @return <code>WiretapFlowEvent</code>
     */
    public WiretapEvent getWiretapEvent(Long wiretapEventId);

    /**
     * dumps a snapshot of an <code>FlowEvent</code> at runtime in the form of one
     * or more <code>WiretapFlowEvent</code>s - one for every <code>Payload</code>
     * contained
     * 
     * @param event - FlowEvent to snapshot
     * @param componentName - name of the component
     * @param moduleName - name of the <code>Module</code>
     * @param flowName - name of the <code>Flow</code>
     * @param timeToLive - no of minutes from now until
     *            <code>WiretapFlowEvents</code> should expire
     */
    public void tapEvent(FlowEvent event, String componentName, String moduleName, String flowName, Long timeToLive);

    /**
     * Causes all <code>WiretapFlowEvent</code>s that are past their expiry to be
     * deleted
     */
    public void housekeep();
}
