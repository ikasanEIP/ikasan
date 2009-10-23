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

import java.net.MalformedURLException;
import java.net.URL;
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
import org.jmock.api.Action;
import org.jmock.api.Invocation;
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
	 * Base URL for URL Construction
	 */
	private static final String BASE_URL_STRING = "http://yourserver:9999/yourcontext";
	
	private String actionTaken = "actionTaken";

	
	/**
	 * System under tests
	 */
	private DefaultErrorLoggingServiceImpl errorLoggingServiceImpl;
	
	/**
	 * Constructor
	 * @throws MalformedURLException 
	 */
	public DefaultErrorLoggingServiceImplTest() throws MalformedURLException{
		List<ErrorOccurrenceListener> listeners = new ArrayList<ErrorOccurrenceListener>();
		listeners.add(firstListener);
		listeners.add(secondListener);
		errorLoggingServiceImpl = new DefaultErrorLoggingServiceImpl(errorOccurrenceDao,excludedEventDao,new URL(BASE_URL_STRING), listeners);
	}
	
	/**
	 * Test method for {@link org.ikasan.framework.error.service.DefaultErrorLoggingServiceImpl#logError(java.lang.Throwable, java.lang.String, java.lang.String, java.lang.String, org.ikasan.framework.component.Event)}.
	 * @throws MalformedURLException 
	 */
	@Test
	public void testLogError_withEvent_willCreateErrorOccurrencePersistAndNotify() throws MalformedURLException {
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
		
		
		final Long errorOccurrenceId = new Long(1000);
		
		final ErrorOccurrence expectedUnpersistedErrorOccurrence = new ErrorOccurrence(throwable, currentEvent,  moduleName, flowName, flowElementName, new Date(), null);
		final ErrorOccurrenceMatcher unpersistedMatcher = new ErrorOccurrenceMatcher(expectedUnpersistedErrorOccurrence);

		final ErrorOccurrence persistedErrorOccurrence = new ErrorOccurrence(throwable, currentEvent,  moduleName, flowName, flowElementName, new Date(), null);
		persistedErrorOccurrence.setId(errorOccurrenceId);
		persistedErrorOccurrence.setUrl(BASE_URL_STRING+"/admin/errors/viewError.htm?errorId="+errorOccurrenceId);
		
		final ErrorOccurrenceMatcher persistedMatcher = new ErrorOccurrenceMatcher(persistedErrorOccurrence);

		

		
		mockery.checking(new Expectations()
        {
            {
            	
				one(errorOccurrenceDao).save((ErrorOccurrence) with(unpersistedMatcher));
            	inSequence(sequence);
            	will(setId(errorOccurrenceId));
            	one(firstListener).notifyErrorOccurrence(with(persistedMatcher));
            	one(secondListener).notifyErrorOccurrence(with(persistedMatcher));
            }


        });
		
		
		errorLoggingServiceImpl.logError(throwable, moduleName, flowName, flowElementName, currentEvent, actionTaken);
	
		mockery.assertIsSatisfied();
	
	}
	
	private Action setId(Long errorOccurrenceId) {
	    return new SetErrorOccurrenceId(errorOccurrenceId);

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
		
		
		final Long errorOccurrenceId = new Long(1000);
		
		final ErrorOccurrence expectedUnpersistedErrorOccurrence = new ErrorOccurrence(throwable,  moduleName, initiatorName, new Date(), actionTaken);
		final ErrorOccurrenceMatcher unpersistedMatcher = new ErrorOccurrenceMatcher(expectedUnpersistedErrorOccurrence);

		final ErrorOccurrence persistedErrorOccurrence = new ErrorOccurrence(throwable,  moduleName, initiatorName, new Date(), actionTaken);
		persistedErrorOccurrence.setId(errorOccurrenceId);
		persistedErrorOccurrence.setUrl(BASE_URL_STRING+"/admin/errors/viewError.htm?errorId="+errorOccurrenceId);

		final ErrorOccurrenceMatcher persistedMatcher = new ErrorOccurrenceMatcher(persistedErrorOccurrence);
		
		
		mockery.checking(new Expectations()
        {
            {
            	
				one(errorOccurrenceDao).save((ErrorOccurrence) with(unpersistedMatcher));
            	inSequence(sequence);
            	will(setId(errorOccurrenceId));
            	one(firstListener).notifyErrorOccurrence(with(persistedMatcher));
            	one(secondListener).notifyErrorOccurrence(with(persistedMatcher));
            }
        });
		
		
		errorLoggingServiceImpl.logError(throwable, moduleName, initiatorName, actionTaken);
	
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
            	
            	one(excludedEventDao).getExcludedEvent(eventId, false);
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
			if (!same(errorOccurrence.getId(),item.getId())){
				result = false;
			}	
			if (!same(errorOccurrence.getUrl(),item.getUrl())){
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

	public class SetErrorOccurrenceId implements Action {
	    private Long id;
	    
	    public SetErrorOccurrenceId(Long id) {
	        this.id = id;
	    }
	    
	    public void describeTo(Description description) {
	        description.appendText("call the setId method with "+id);
	    }
	    
	    public Object invoke(Invocation invocation) throws Throwable {
	        ((ErrorOccurrence)invocation.getParameter(0)).setId(id);
	        return null;
	    }
	}
}
