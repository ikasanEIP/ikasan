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

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.framework.event.wiretap.dao.WiretapDao;
import org.ikasan.framework.event.wiretap.model.PagedWiretapSearchResult;
import org.ikasan.framework.event.wiretap.model.WiretapEvent;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.ikasan.framework.module.service.ModuleService;

/**
 * Default implementation of the <code>WiretapService</code>
 * 
 * @author Ikasan Development Team
 */
public class WiretapServiceImpl implements WiretapService
{
    /** Data access object for the persistence of <code>WiretapFlowEvent</code> */
    private WiretapDao wiretapDao;

    /** Logger for this class */
    private static Logger logger = Logger.getLogger(WiretapServiceImpl.class);

    /**
     * Container for modules
     */
    private ModuleService moduleService;

    /**
     * Constructor
     * 
     * @param wiretapDao - The wire tap DAO
     * @param moduleService - The module service to use
     */
    public WiretapServiceImpl(WiretapDao wiretapDao, ModuleService moduleService)
    {
        super();
        this.wiretapDao = wiretapDao;
        this.moduleService = moduleService;
        logger.info("created");
    }

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
            String moduleFlow, String componentName, String eventId, String payloadId, Date fromDate, Date untilDate, String payloadContent)
    {
        if (pageNo < 0)
        {
            throw new IllegalArgumentException("pageNo must be >= 0");
        }
        if (pageSize < 1)
        {
            throw new IllegalArgumentException("pageSize must be > 0");
        }
        return wiretapDao.findWiretapEvents(pageNo, pageSize, orderBy, orderAscending, moduleNames, moduleFlow, componentName, eventId, payloadId, fromDate, untilDate,
            payloadContent);
    }

    /**
     * Allows previously stored FlowEvents to be searched for.
     * 
     * @deprecated Use other findWiretapFlowEvents method
     * 
     * @param pageNo - page index into the greater result set
     * @param pageSize - how many results to return in the result
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
    public PagedWiretapSearchResult findWiretapEvents(Set<String> moduleNames, String moduleFlow, String componentName, String eventId, String payloadId, Date fromDate,
            Date untilDate, String payloadContent, int pageSize, int pageNo)
    {
        if (moduleNames == null || moduleNames.isEmpty())
        {
            throw new IllegalArgumentException("moduleNames must be nonEmpty");
        }
        return wiretapDao.findPaging(moduleNames, moduleFlow, componentName, eventId, payloadId, fromDate, untilDate, payloadContent, pageSize, (pageSize * (pageNo - 1)));
    }

    /**
     * Get a wireTap event given its Id
     * 
     * @param wiretapFlowEventId - The Id to search with
     * @return The WiretapFlowEvent
     */
    public WiretapEvent getWiretapEvent(Long wiretapEventId)
    {
        WiretapEvent wiretapEvent = wiretapDao.findById(wiretapEventId);
        if (wiretapEvent != null)
        {
            // before returning wiretapFlowEvent, check that we can access the
            // associated module
            // this is an easier security check that access controlling every
            // WiretapFlowEvent individually
            // If the user can 'read' the module, then they are allowed to read
            // its associated WiretapFlowEvents
            moduleService.getModule(wiretapEvent.getModuleName());
        }
        return wiretapEvent;
    }

    /**
     * Wiretap an FlowEvent
     * 
     * @param event - FlowEvent to be wiretapped
     * @param componentName - The component this FlowEvent is currently in
     * @param moduleName - The module this FlowEvent is currently in
     * @param flowName - The Flow this FlowEvent is currently in
     * @param timeToLive - Time to live for the wiretap
     */
    public void tapEvent(FlowEvent event, String componentName, String moduleName, String flowName, Long timeToLive)
    {
        String eventId = event.getIdentifier();
        Date expiry = new Date(System.currentTimeMillis() + (timeToLive * 60000));
//        for (Payload payload : event.getPayloads())
//        {
//            WiretapEvent wiretapFlowEvent = new WiretapEvent(moduleName, flowName, componentName, eventId, payload.getId(), new String(payload.getContent()),
            WiretapEvent wiretapEvent = 
                new WiretapEvent(moduleName, flowName, componentName, eventId, "padyloadId doesnt exist", new String("payload content must go here"),
                expiry);
            this.wiretapDao.save(wiretapEvent);
            if (logger.isDebugEnabled())
            {
                logger.debug("Created wiretapFlowEvent [" + wiretapEvent.toString() + "]");
            }
//        }
    }

    /**
     * Housekeep the wiretaps by deleting expired ones.
     */
    public void housekeep()
    {
    	logger.info("wiretap housekeep called");
    	long startTime = System.currentTimeMillis();
        wiretapDao.deleteAllExpired();
        long endTime = System.currentTimeMillis();
        logger.info("wiretap housekeep completed in ["+(endTime-startTime)+" ms]");
    }
}
