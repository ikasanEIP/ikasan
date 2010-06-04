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
package org.ikasan.framework.systemevent.service;

import java.util.Date;

import org.apache.log4j.Logger;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.ikasan.framework.systemevent.dao.SystemEventDao;
import org.ikasan.framework.systemevent.model.SystemEvent;



/**
 * SystemEvent service implementation
 * 
 * @author Ikasan Development Team
 *
 */
public class SystemEventServiceImpl implements SystemEventService
{
	private Logger logger = Logger.getLogger(SystemEventServiceImpl.class);
	
	/**
	 * Underlying data access object
	 */
	private SystemEventDao systemEventDao;
	
	/**
	 * no of minutes for this event to be kept until eligible for housekeep
	 * If null, then no expiry
	 */
	private Long eventExpiryMinutes;
	
	/**
	 * Constructor
	 * @param systemEventDao
	 * @param eventExpiryMinutes - no of minutes for this event to be kept until eligible for housekeep
	 */
	public SystemEventServiceImpl(SystemEventDao systemEventDao, Long eventExpiryMinutes) {
		this.systemEventDao = systemEventDao;
		this.eventExpiryMinutes = eventExpiryMinutes;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.systemevent.service.SystemEventService#logSystemEvent(java.lang.String, java.lang.String, java.util.Date, java.lang.String)
	 */
	public void logSystemEvent(String subject, String action, String actor){
		Date now = new Date();
		Date expiry = null;
		if (eventExpiryMinutes!=null){
			expiry = new Date(now.getTime()+(60000*eventExpiryMinutes));	
		}
		systemEventDao.save(new SystemEvent(subject, action, now, actor,expiry));
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.systemevent.service.SystemEventService#listSystemEvents(java.lang.String, java.lang.String, java.util.Date, java.util.Date, java.lang.String)
	 */
	public PagedSearchResult<SystemEvent> listSystemEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending,String subject, String action, Date timestampFrom, Date timestampTo, String actor) {

		return systemEventDao.find(pageNo, pageSize, orderBy, orderAscending, subject,action, timestampFrom, timestampTo, actor);

	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.systemevent.service.SystemEventService#housekeep()
	 */
	public void housekeep() {
		long before = System.currentTimeMillis();
		systemEventDao.deleteExpired();
		long after = System.currentTimeMillis();
		logger.info("housekeep completed in ["+(after-before)+"]ms");
		
	}
}
