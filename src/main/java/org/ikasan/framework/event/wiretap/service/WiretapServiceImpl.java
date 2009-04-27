/* 
 * $Id: WiretapServiceImpl.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/event/wiretap/service/WiretapServiceImpl.java $
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
package org.ikasan.framework.event.wiretap.service;

import java.util.Date;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.wiretap.dao.WiretapDao;
import org.ikasan.framework.event.wiretap.model.PagedWiretapSearchResult;
import org.ikasan.framework.event.wiretap.model.WiretapEvent;
import org.ikasan.framework.module.service.ModuleService;

/**
 * Default implementation of the <code>WiretapService</code>
 * 
 * @author Ikasan Development Team
 */
public class WiretapServiceImpl implements WiretapService
{
    /** Data access object for the persistence of <code>WiretapEvent</code> */
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

    public PagedWiretapSearchResult findWiretapEvents(Set<String> moduleNames, String componentName, String eventId, String payloadId, Date fromDate, Date untilDate, String payloadContent, int pageSize, int pageNo)
    {
        if (moduleNames ==null || moduleNames.isEmpty()){
            throw new IllegalArgumentException("moduleNames must be nonEmpty");
        }
        
        
        return wiretapDao.findPaging(moduleNames, componentName, eventId, payloadId, fromDate, untilDate, payloadContent, pageSize, (pageSize*(pageNo-1)));        
    }

    public WiretapEvent getWiretapEvent(Long wiretapEventId)
    {
        WiretapEvent wiretapEvent = wiretapDao.findById(wiretapEventId);
        
        if (wiretapEvent!=null){
            //before returning wiretapEvent, check that we can access the assocated module
            //this is an easier security check that access controlling every WiretapEvent individually
            //If the user can 'read' the module, then they are allowed to read its associated WiretapEvents
            moduleService.getModule(wiretapEvent.getModuleName());
        }
        
        return wiretapEvent;
    }

    public void tapEvent(Event event, String componentName, String moduleName, String flowName, Long timeToLive)
    {
        String eventId = event.getId();
        Date expiry = new Date(System.currentTimeMillis() + (timeToLive * 60000));
        for (Payload payload : event.getPayloads())
        {
            WiretapEvent wiretapEvent = new WiretapEvent(moduleName, flowName, componentName, eventId, payload.getId(), new String(payload.getContent()),
                expiry);
            this.wiretapDao.save(wiretapEvent);
            if (logger.isDebugEnabled())
            {
                logger.debug("Created wiretapEvent [" + wiretapEvent.toString() + "]");
            }
        }
    }

    public void housekeep()
    {
        wiretapDao.deleteAllExpired();
    }
}
