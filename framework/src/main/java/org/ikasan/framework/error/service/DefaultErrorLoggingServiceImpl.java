/*
 * $Id
 * $URL
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
package org.ikasan.framework.error.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.error.dao.ErrorOccurrenceDao;
import org.ikasan.framework.error.model.ErrorOccurrence;
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
	 * List of all registered listeners
	 */
	private List<ErrorOccurrenceListener> errorOccurrenceListeners;

	/**
	 * Constructor
	 * 
	 * @param errorOccurrenceDao
	 */
	public DefaultErrorLoggingServiceImpl(ErrorOccurrenceDao errorOccurrenceDao) {
		super();
		this.errorOccurrenceDao = errorOccurrenceDao;
	}
	
	/**
	 * Constructor
	 * 
	 * @param errorOccurrenceDao
	 * @param errorOccurrenceListeners
	 */
	public DefaultErrorLoggingServiceImpl(ErrorOccurrenceDao errorOccurrenceDao,List<ErrorOccurrenceListener> errorOccurrenceListeners ) {
		this(errorOccurrenceDao);
		this.errorOccurrenceListeners = new ArrayList<ErrorOccurrenceListener>();
		this.errorOccurrenceListeners.addAll(errorOccurrenceListeners);
	}
	
	/**
	 * Constructor
	 * 
	 * @param errorOccurrenceDao
	 * @param errorOccurrenceListener
	 */
	public DefaultErrorLoggingServiceImpl(ErrorOccurrenceDao errorOccurrenceDao,ErrorOccurrenceListener errorOccurrenceListener ) {
		this(errorOccurrenceDao);
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
		return errorOccurrenceDao.getErrorOccurrence(errorOccurrenceId);
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

}
