/*
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
package org.ikasan.framework.event.exclusion.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.event.exclusion.dao.ExcludedEventDao;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.management.search.PagedSearchResult;

/**
 * @author The Ikasan Development Service
 *
 */
public class ExcludedEventServiceImpl implements ExcludedEventService {
	
	private List<ExcludedEventListener> excludedEventListeners = new ArrayList<ExcludedEventListener>();

	private ExcludedEventDao excludedEventDao;
	
	private Logger logger = Logger.getLogger(ExcludedEventServiceImpl.class);
	
	/**
	 * @param excludedEventDao
	 * @param listeners
	 */
	public ExcludedEventServiceImpl(ExcludedEventDao excludedEventDao,
			List<ExcludedEventListener> listeners) {
		this.excludedEventDao = excludedEventDao;
		excludedEventListeners.addAll(listeners);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.event.exclusion.service.EventExclusionService#excludeEvent(org.ikasan.framework.component.Event)
	 */
	public void excludeEvent(Event event, String moduleName, String flowName) {
		Date exclusionTime = new Date();
		//create and save a new ExcludedEvent
		logger.info("excluding event from module:"+moduleName+", flow:"+flowName);
		excludedEventDao.save(new ExcludedEvent(event, moduleName, flowName, exclusionTime));
		
		//notify all listeners that this event has been excluded
		for (ExcludedEventListener excludedEventListener : excludedEventListeners){
			excludedEventListener.notifyExcludedEvent(event);
		}

	}

	public PagedSearchResult<ExcludedEvent> getExcludedEvents(int pageNo, int pageSize) {
		if (pageNo<0){
			throw new IllegalArgumentException("pageNo must be >= 0");
		}
		if (pageSize<1){
			throw new IllegalArgumentException("pageSize must be > 0");
		}
		
		return excludedEventDao.findExcludedEvents(pageNo, pageSize);
	}

	public ExcludedEvent getExcludedEvent(long excludedEventId) {
		return excludedEventDao.getExcludedEvent(excludedEventId);
	}

}
