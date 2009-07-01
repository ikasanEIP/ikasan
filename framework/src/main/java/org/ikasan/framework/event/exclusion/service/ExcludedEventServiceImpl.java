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

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.exclusion.dao.ExcludedEventDao;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.flow.FlowInvocationContext;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.service.ModuleService;

/**
 * @author The Ikasan Development Service
 *
 */
public class ExcludedEventServiceImpl implements ExcludedEventService {
	
	private List<ExcludedEventListener> excludedEventListeners = new ArrayList<ExcludedEventListener>();

	private ExcludedEventDao excludedEventDao;
	
	private ModuleService moduleService;
	
	/**
	 * Only used for debugging the transaction status
	 */
	private TransactionManager transactionManager;
	
	private Logger logger = Logger.getLogger(ExcludedEventServiceImpl.class);
	
	
	
	/**
	 * @param excludedEventDao
	 * @param listeners
	 * @param moduleService
	 */
	public ExcludedEventServiceImpl(ExcludedEventDao excludedEventDao,
			List<ExcludedEventListener> listeners, ModuleService moduleService) {
		this.excludedEventDao = excludedEventDao;
		excludedEventListeners.addAll(listeners);
		this.moduleService = moduleService;
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

	/* (non-Javadoc)
	 * 
	 * synchronously resubmit, not handling any errors, simply allowing any exception to propogate
	 * 
	 * 
	 * @see org.ikasan.framework.event.exclusion.service.ExcludedEventService#resubmit(long)
	 */
	public void resubmit(long excludedEventId) {
		
		if (transactionManager!=null){
			try {
				int status = transactionManager.getStatus();
				if (Status.STATUS_ACTIVE!=status){
					logger.warn("Warning! Resubmission invoked outside of an active transaction!");
				} 
			} catch (SystemException e) {
				logger.error(e);
			}
		}
		
        
		ExcludedEvent excludedEvent = getExcludedEvent(excludedEventId);
		
		if (excludedEvent==null){
			throw new IllegalArgumentException("unknown ExcludedEvent id:"+excludedEventId);
		}
		
		Module module = moduleService.getModule(excludedEvent.getModuleName());
		if (module==null){
			throw new IllegalArgumentException("unknown Module:"+excludedEvent.getModuleName());
		}	
			
	    Flow flow = module.getFlows().get(excludedEvent.getFlowName());
	    if (flow==null){ 
			throw new IllegalArgumentException("unknown Flow"+excludedEvent.getFlowName());
		}
		
	    //invoke the flow with the Event. Any exceptions are left to propagate
	    flow.invoke(new FlowInvocationContext(), excludedEvent.getEvent());
	   
	    
	    //cleanup the excludedEvent
	    excludedEventDao.delete(excludedEvent);
	    
	}

	public void setTransactionManager(TransactionManager transactionManager){
		this.transactionManager = transactionManager;
	}
}
