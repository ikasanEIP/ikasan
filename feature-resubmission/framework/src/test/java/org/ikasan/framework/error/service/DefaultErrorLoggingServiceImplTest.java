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

import junit.framework.Assert;

import org.hamcrest.Description;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.error.dao.ErrorOccurrenceDao;
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.event.exclusion.dao.ExcludedEventDao;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.management.search.ArrayListPagedSearchResult;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.matchers.TypeSafeMatcher;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class DefaultErrorLoggingServiceImplTest {
	
	/**
	 * Mockery for testing
	 */
	private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
	/**
	 * mocked errorOccurrenceDao
	 */
	private ErrorOccurrenceDao errorOccurrenceDao = mockery.mock(ErrorOccurrenceDao.class);
	
	/**
	 * mocked excludedEventDao
	 */
	private ExcludedEventDao excludedEventDao = mockery.mock(ExcludedEventDao.class);
	
	/**
	 * mocked ErrorOccurrenceListener
	 */
	private ErrorOccurrenceListener firstListener = mockery.mock(ErrorOccurrenceListener.class, "firstListerner");
	
	/**
	 * another mocked ErrorOccurrenceListener
	 */
	private ErrorOccurrenceListener secondListener = mockery.mock(ErrorOccurrenceListener.class, "firstListerner");
	
	/**
	 * System under tests
	 */
	private DefaultErrorLoggingServiceImpl errorLoggingServiceImpl;
	
	/**
	 * Constructor
	 */
	public DefaultErrorLoggingServiceImplTest(){
		List<ErrorOccurrenceListener> listeners = new ArrayList<ErrorOccurrenceListener>();
		listeners.add(firstListener);
		listeners.add(secondListener);
		errorLoggingServiceImpl = new DefaultErrorLoggingServiceImpl(errorOccurrenceDao,excludedEventDao, listeners);
	}
	
	/**
	 * Test method for {@link org.ikasan.framework.error.service.DefaultErrorLoggingServiceImpl#logError(java.lang.Throwable, java.lang.String, java.lang.String, java.lang.String, org.ikasan.framework.component.Event)}.
	 */
	@Test
	public void testLogError_withEvent_willCreateErrorOccurrencePersistAndNotify() {
		Throwable throwable = new NullPointerException();
		String moduleName = "moduleName";
		String flowName = "flowName";
		String flowElementName = "flowElementName";
		
		final Event currentEvent = mockery.mock(Event.class); 
		
		final Sequence sequence = mockery.sequence("invocationSequence");
		
		mockery.checking(new Expectations()
        {
            {
            	allowing(currentEvent).getId();will(returnValue("eventId"));
            	
            }
        });
		
		final ErrorOccurrence expectedErrorOccurrence = new ErrorOccurrence(throwable, currentEvent,  moduleName, flowName, flowElementName, new Date());
		final ErrorOccurrenceMatcher matcher = new ErrorOccurrenceMatcher(expectedErrorOccurrence);

		
		
		mockery.checking(new Expectations()
        {
            {
            	
				one(errorOccurrenceDao).save((ErrorOccurrence) with(matcher));
            	inSequence(sequence);
            	one(firstListener).notifyErrorOccurrence(with(matcher));
            	one(secondListener).notifyErrorOccurrence(with(matcher));
            }
        });
		
		
		errorLoggingServiceImpl.logError(throwable, moduleName, flowName, flowElementName, currentEvent);
	
		mockery.assertIsSatisfied();
	
	}
	
	/**
	 * Test method for {@link org.ikasan.framework.error.service.DefaultErrorLoggingServiceImpl#logError(java.lang.Throwable, java.lang.String, java.lang.String, java.lang.String, org.ikasan.framework.component.Event)}.
	 */
	@Test
	public void testLogError_withoutEvent_willCreateErrorOccurrencePersistAndNotify() {
		Throwable throwable = new NullPointerException();
		String moduleName = "moduleName";
		String initiatorName = "initiatorName";
		
		
		final Sequence sequence = mockery.sequence("invocationSequence");
		
		
		final ErrorOccurrence expectedErrorOccurrence = new ErrorOccurrence(throwable,  moduleName, initiatorName, new Date());
		final ErrorOccurrenceMatcher matcher = new ErrorOccurrenceMatcher(expectedErrorOccurrence);

		
		
		mockery.checking(new Expectations()
        {
            {
            	
				one(errorOccurrenceDao).save((ErrorOccurrence) with(matcher));
            	inSequence(sequence);
            	one(firstListener).notifyErrorOccurrence(with(matcher));
            	one(secondListener).notifyErrorOccurrence(with(matcher));
            }
        });
		
		
		errorLoggingServiceImpl.logError(throwable, moduleName, initiatorName);
	
		mockery.assertIsSatisfied();
	
	}
	@Test
	public void testGetErrorOccurrence(){
		final ErrorOccurrence errorOccurrence = mockery.mock(ErrorOccurrence.class);
		final ExcludedEvent excludedEvent = mockery.mock(ExcludedEvent.class);
		
		final long errorOccurrenceId = 1l;
		
		final String eventId = "eventId";
		mockery.checking(new Expectations()
        {
            {
            	one(errorOccurrenceDao).getErrorOccurrence(errorOccurrenceId);
            	will(returnValue(errorOccurrence));
            	
            	one(errorOccurrence).getEventId();
            	will(returnValue(eventId));
            	
            	one(excludedEventDao).getExcludedEvent(eventId);
            	will(returnValue(excludedEvent));
            	
            	one(errorOccurrence).setExcludedEvent(excludedEvent);
             }
        });
		
		Assert.assertEquals(errorOccurrence, errorLoggingServiceImpl.getErrorOccurrence(errorOccurrenceId));
		mockery.assertIsSatisfied();
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetErrors_withInvalidPageNo_willThrowIllegalArgumentException(){	
		errorLoggingServiceImpl.getErrors(-2, 10, "id", true, null, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetErrors_withInvalidPageSize_willThrowIllegalArgumentException(){	
		errorLoggingServiceImpl.getErrors(0, 0,"id", true, null, null);
	}
	
	@Test
	public void testGetErrors_withValidParameters_willPropgateCallToDao(){
		final PagedSearchResult<ErrorOccurrence> resultList = new ArrayListPagedSearchResult<ErrorOccurrence>(new ArrayList<ErrorOccurrence>(),0,10);
		final int pageNo=0;
		final int pageSize=10;
		
		mockery.checking(new Expectations()
        {
            {
            	one(errorOccurrenceDao).findErrorOccurrences(pageNo, pageSize, "id", true, null, null);will(returnValue(resultList));
             }
        });
		
		
		Assert.assertEquals(resultList, errorLoggingServiceImpl.getErrors(pageNo, pageSize, "id", true, null, null));
	
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testHousekeep_willPropogateToDao(){
		mockery.checking(new Expectations()
        {
            {
            	one(errorOccurrenceDao).deleteAllExpired();
             }
        });
		errorLoggingServiceImpl.housekeep();
		mockery.assertIsSatisfied();
	}
	
	public class ErrorOccurrenceMatcher extends TypeSafeMatcher<ErrorOccurrence> {

		private ErrorOccurrence errorOccurrence;
		
		
		/**
		 * @param expectedErrorOccurrence
		 */
		public ErrorOccurrenceMatcher(ErrorOccurrence expectedErrorOccurrence) {
			this.errorOccurrence = expectedErrorOccurrence;
		}

		/* (non-Javadoc)
		 * @see org.junit.matchers.TypeSafeMatcher#matchesSafely(java.lang.Object)
		 */
		@Override
		public boolean matchesSafely(ErrorOccurrence item) {
			boolean result = true;
			
			if (!same(errorOccurrence.getCurrentEvent(),item.getCurrentEvent())){
				result = false;
			}
			if (!same(errorOccurrence.getErrorDetail(),item.getErrorDetail())){
				result = false;
			}
			if (!same(errorOccurrence.getEventId(),item.getEventId())){
				result = false;
			}			
			if (!same(errorOccurrence.getFlowElementName(),item.getFlowElementName())){
				result = false;
			}	
			if (!same(errorOccurrence.getFlowName(),item.getFlowName())){
				result = false;
			}	
			if (!same(errorOccurrence.getInitiatorName(),item.getInitiatorName())){
				result = false;
			}	
			if (!same(errorOccurrence.getModuleName(),item.getModuleName())){
				result = false;
			}	
			return result;
		}


		/* (non-Javadoc)
		 * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
		 */
		public void describeTo(Description arg0) {}
	
		private boolean same(Object thisOne, Object thatOne){
			if (thisOne==null||thatOne==null){
				if ((thisOne==null)==(thatOne==null)){
					return true;
				}
				return false;
			}
			return thisOne.equals(thatOne);
		}
		
	}

}
