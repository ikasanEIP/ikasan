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
package org.ikasan.framework.event.exclusion.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.event.exclusion.dao.ExcludedEventDao;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.flow.invoker.FlowInvocationContext;
import org.ikasan.framework.initiator.AbortTransactionException;
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
	
	private ErrorLoggingService errorLoggingService;
	
	private ModuleService moduleService;
	
	/**
	 * Only used for debugging the transaction status
	 */
	private TransactionManager transactionManager;
	
	private Logger logger = Logger.getLogger(ExcludedEventServiceImpl.class);
	
	
	
	/**
	 * @param excludedEventDao
	 * @param errorLoggingService
	 * @param listeners
	 * @param moduleService
	 */
	public ExcludedEventServiceImpl(ExcludedEventDao excludedEventDao,
			ErrorLoggingService errorLoggingService,
			List<ExcludedEventListener> listeners, ModuleService moduleService) {
		this.excludedEventDao = excludedEventDao;
		excludedEventListeners.addAll(listeners);
		this.moduleService = moduleService;
		this.errorLoggingService = errorLoggingService;
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

	public PagedSearchResult<ExcludedEvent> getExcludedEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending, String moduleName, String flowName) {
		if (pageNo<0){
			throw new IllegalArgumentException("pageNo must be >= 0");
		}
		if (pageSize<1){
			throw new IllegalArgumentException("pageSize must be > 0");
		}
		//TODO validate the orderBy field - must be one of [id|moduleName|flowName|exclusionTime]
		
		return excludedEventDao.findExcludedEvents(pageNo, pageSize, orderBy, orderAscending,  moduleName, flowName);
	}

	
	public ExcludedEvent getExcludedEvent(String eventId) {
		ExcludedEvent excludedEvent = excludedEventDao.getExcludedEvent(eventId, false);
		if (excludedEvent!=null){
			List<ErrorOccurrence> errorOccurrences = errorLoggingService.getErrorOccurrences(eventId);
			excludedEvent.setErrorOccurrences(errorOccurrences);
		}	
		
		return excludedEvent;
	}

	/* (non-Javadoc)
	 * 
	 * synchronously resubmit, not handling any errors, simply allowing any exception to propogate
	 * 
	 * 
	 * @see org.ikasan.framework.event.exclusion.service.ExcludedEventService#resubmit(long)
	 */
	public void resubmit(String eventId, String resubmitter) {
		

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
		
        
		ExcludedEvent excludedEvent = excludedEventDao.getExcludedEvent(eventId, false);
		
		if (excludedEvent==null){
			throw new IllegalArgumentException("Cannot find Excluded Event id:"+eventId);
		}
		
		if (excludedEvent.isResolved()){
			throw new IllegalStateException("Attempt made to resubmit event:"+eventId);
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
	    Event event = excludedEvent.getEvent();
	    FlowInvocationContext flowInvocationContext = new FlowInvocationContext();
	    try{
			flow.invoke(flowInvocationContext, event);
	    } catch (Throwable throwable){
	    	try {
	    		
	    		//for some reason, if dont clone event for error occurrence, hibernate fails to persist
	    		//dont know why this is
				Event clonedEvent = event.clone();
				errorLoggingService.logError(throwable, excludedEvent.getModuleName(), excludedEvent.getFlowName(), flowInvocationContext.getLastComponentName(), clonedEvent, null);
			} catch (CloneNotSupportedException e) {
				//ignore
			}
			
	    	
	    	throw new AbortTransactionException("Resubmission failed!", throwable);
	    }
	    
	    
	    //mark excludedEvent as resubmitted
	    //need to get a fresh handle on the ExcludedEvent, because the original now has changes we dont want to save
	    excludedEvent = excludedEventDao.getExcludedEvent(eventId, true);
	    excludedEvent.resolveAsResubmitted(resubmitter);

	    excludedEventDao.save(excludedEvent);
	    
	}
	
	

	/* (non-Javadoc)
	 * @see org.ikasan.framework.event.exclusion.service.ExcludedEventService#cancel(java.lang.String, java.lang.String)
	 */
	public void cancel(String eventId, String canceller) {

		ExcludedEvent excludedEvent = excludedEventDao.getExcludedEvent(eventId, true);
		
		if (excludedEvent==null){
			throw new IllegalArgumentException("Cannot find Excluded Event id:"+eventId);
		}
		
		if (excludedEvent.isResolved()){
			throw new IllegalStateException("Attempt made to resubmit event:"+eventId);
		}
		
		Module module = moduleService.getModule(excludedEvent.getModuleName());
		if (module==null){
			throw new IllegalArgumentException("unknown Module:"+excludedEvent.getModuleName());
		}	
			
	    //mark excludedEvent as cancelled
	    excludedEvent.resolveAsCancelled(canceller);

	    excludedEventDao.save(excludedEvent);
	    
	    

	    
	}

	public void setTransactionManager(TransactionManager transactionManager){
		this.transactionManager = transactionManager;
	}
}
