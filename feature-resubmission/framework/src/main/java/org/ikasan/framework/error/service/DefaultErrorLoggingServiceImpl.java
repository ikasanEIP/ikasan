/* 
 * $Id: 
 * $URL: 
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
package org.ikasan.framework.error.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.error.dao.ErrorOccurrenceDao;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.event.exclusion.dao.ExcludedEventDao;
import org.ikasan.framework.event.exclusion.service.ExcludedEventService;
import org.ikasan.framework.management.search.PagedSearchResult;

/**
 * Default implementation of <code>ErrorLoggingServiceImpl</code>
 * 
 * When logging error, simply persists a new instance of <code>ErrorOccurrence</code> and notifies listeners
 * 
 * @author Ikasan Development Team
 *
 */
public class DefaultErrorLoggingServiceImpl implements ErrorLoggingService {
	
	private static final long MILLISECONDS_IN_A_DAY = 1000 * 60 * 60 * 24;

	/**
	 * Logger instance
	 */
	private Logger logger = Logger.getLogger(DefaultErrorLoggingServiceImpl.class);

	/**
	 * Maximum time for ErrorOccurrences to be kept. After this time they may be housekept
	 */
	private long errorTimeToLiveDays = 7;
	

	/**
	 * Data access object for the persistence of ErrorOccurrences
	 */
	private ErrorOccurrenceDao errorOccurrenceDao;
	
	/**
	 * Data access object for the persistence of ExcludedEvents 
	 */	
	private ExcludedEventDao excludedEventDao;
	
	/**
	 * List of all registered listeners
	 */
	private List<ErrorOccurrenceListener> errorOccurrenceListeners= new ArrayList<ErrorOccurrenceListener>();

	/**
	 * Constructor
	 * 
	 * @param errorOccurrenceDao
	 */
	public DefaultErrorLoggingServiceImpl(ErrorOccurrenceDao errorOccurrenceDao, ExcludedEventDao excludedEventDao) {
		super();
		this.errorOccurrenceDao = errorOccurrenceDao;
		this.excludedEventDao = excludedEventDao;
	}
	
	/**
	 * Constructor
	 * 
	 * @param errorOccurrenceDao
	 * @param excludedEventDao
	 * @param errorOccurrenceListeners
	 */
	public DefaultErrorLoggingServiceImpl(ErrorOccurrenceDao errorOccurrenceDao, ExcludedEventDao excludedEventDao,List<ErrorOccurrenceListener> errorOccurrenceListeners ) {
		this(errorOccurrenceDao, excludedEventDao);
		this.errorOccurrenceListeners = new ArrayList<ErrorOccurrenceListener>();
		this.errorOccurrenceListeners.addAll(errorOccurrenceListeners);
	}
	
	/**
	 * Constructor
	 * 
	 * @param errorOccurrenceDao
	 * @param errorOccurrenceListener
	 */
	public DefaultErrorLoggingServiceImpl(ErrorOccurrenceDao errorOccurrenceDao,ExcludedEventDao excludedEventDao,ErrorOccurrenceListener errorOccurrenceListener ) {
		this(errorOccurrenceDao, excludedEventDao);
		this.errorOccurrenceListeners = new ArrayList<ErrorOccurrenceListener>();
		errorOccurrenceListeners.add(errorOccurrenceListener);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.error.service.ErrorLoggingService#logError(java.lang.Throwable, java.lang.String, java.lang.String, java.lang.String, org.ikasan.framework.component.Event)
	 */
	public void logError(Throwable throwable, String moduleName,
			String flowName, String flowElementName, Event currentEvent) {
		ErrorOccurrence errorOccurrence = new ErrorOccurrence(throwable, currentEvent, moduleName, flowName, flowElementName, calculateExpiry());
		persistAndNotifyListeners(errorOccurrence);
	}

	/**
	 * Calculates the expiry date based on the errorTimeToLiveDays and current time
	 * 
	 * @return
	 */
	private Date calculateExpiry() {
		return new Date(System.currentTimeMillis() + (errorTimeToLiveDays * MILLISECONDS_IN_A_DAY));
	}

	/**
	 * @param errorOccurrence
	 */
	private void persistAndNotifyListeners(ErrorOccurrence errorOccurrence) {
		logger.info("logging error");
		errorOccurrenceDao.save(errorOccurrence);
		if (errorOccurrenceListeners!=null){
			for (ErrorOccurrenceListener errorOccurrenceListener : errorOccurrenceListeners){
				errorOccurrenceListener.notifyErrorOccurrence(errorOccurrence);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.error.service.ErrorLoggingService#getErrors()
	 */
	public PagedSearchResult<ErrorOccurrence> getErrors(int pageNo, int pageSize, String orderBy, boolean orderAscending,String moduleName, String flowName) {
		if (pageNo<0){
			throw new IllegalArgumentException("pageNo must be >= 0");
		}
		if (pageSize<1){
			throw new IllegalArgumentException("pageSize must be > 0");
		}
		
		return errorOccurrenceDao.findErrorOccurrences(pageNo, pageSize, orderBy, orderAscending, moduleName, flowName);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.error.service.ErrorLoggingService#logError(java.lang.Throwable, java.lang.String, java.lang.String)
	 */
	public void logError(Throwable throwable, String moduleName,
			String initiatorName) {
		ErrorOccurrence errorOccurrence = new ErrorOccurrence(throwable, moduleName, initiatorName, calculateExpiry());
		persistAndNotifyListeners(errorOccurrence);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.error.service.ErrorLoggingService#getErrorOccurrence(long)
	 */
	public ErrorOccurrence getErrorOccurrence(long errorOccurrenceId) {
		ErrorOccurrence errorOccurrence = errorOccurrenceDao.getErrorOccurrence(errorOccurrenceId);
		if (errorOccurrence!=null){
			errorOccurrence.setExcludedEvent(excludedEventDao.getExcludedEvent(errorOccurrence.getEventId(), false));
		}
		return errorOccurrence;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.error.service.ErrorLoggingService#housekeep()
	 */
	public void housekeep() {
		errorOccurrenceDao.deleteAllExpired();
	}
	
	/**
	 * Setter method for timeToLiveDays, allows default value to be overridden
	 * 
	 * @param errorTimeToLiveDays
	 */
	public void setErrorTimeToLiveDays(long errorTimeToLiveDays) {
		this.errorTimeToLiveDays = errorTimeToLiveDays;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.error.service.ErrorLoggingService#getErrorOccurrences(java.lang.String)
	 */
	public List<ErrorOccurrence> getErrorOccurrences(String eventId) {
		return errorOccurrenceDao.getErrorOccurrences(eventId);
	}
	
	
	public void addErrorOccurrenceListener(ErrorOccurrenceListener errorOccurrenceListener){
		errorOccurrenceListeners.add(errorOccurrenceListener);
	}
	
	public void removeErrorOccurrenceListener(ErrorOccurrenceListener errorOccurrenceListener){
		errorOccurrenceListeners.remove(errorOccurrenceListener);
	}

}
