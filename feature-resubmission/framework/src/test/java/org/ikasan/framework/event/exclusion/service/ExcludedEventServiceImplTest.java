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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.exclusion.dao.ExcludedEventDao;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.flow.invoker.FlowInvocationContext;
import org.ikasan.framework.management.search.ArrayListPagedSearchResult;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.service.ModuleService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;


/**
 * Test class for ExcludedEventServiceImpl
 * 
 * 
 * @author The Ikasan Development Team
 *
 */
public class ExcludedEventServiceImplTest {
	
	/**
	 * Mockery for testing
	 */
	private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    private Event excludedEvent = mockery.mock(Event.class);
    
    private ExcludedEventDao excludedEventDao = mockery.mock(ExcludedEventDao.class);
    
    private ExcludedEventListener excludedEventListener1 = mockery.mock(ExcludedEventListener.class, "excludedEventListener1");
    
    private ExcludedEventListener excludedEventListener2 = mockery.mock(ExcludedEventListener.class, "excludedEventListener2");
	
    private ModuleService moduleService = mockery.mock(ModuleService.class);
	/**
	 * Class under test
	 */
	private ExcludedEventServiceImpl excludedEventService;
    
	/**
	 * Constructor
	 */
	public ExcludedEventServiceImplTest(){
		List<ExcludedEventListener> listeners = new ArrayList<ExcludedEventListener>();
		listeners.add(excludedEventListener1);
		listeners.add(excludedEventListener2);
		excludedEventService = new ExcludedEventServiceImpl(excludedEventDao, listeners,moduleService);
	}
	
	@Test
	public void testExcludeEvent(){
		final String moduleName = "moduleName";
		final String flowName = "flowName";
		
		final Sequence sequence = mockery.sequence("invocationSequence");
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).save(with(new ExcludedEventMatcher(new ExcludedEvent(excludedEvent,moduleName, flowName, new Date()))));
            	inSequence(sequence);
            	one(excludedEventListener1).notifyExcludedEvent(excludedEvent);
            	inSequence(sequence);
            	one(excludedEventListener2).notifyExcludedEvent(excludedEvent);
            	inSequence(sequence);
            }
        });
		
		excludedEventService.excludeEvent(excludedEvent, moduleName, flowName);
		
		mockery.assertIsSatisfied();
	}
	
	public class ExcludedEventMatcher extends TypeSafeMatcher<ExcludedEvent> {

		private ExcludedEvent excludedEvent;
		
		
		/**
		 * @param expectedErrorOccurrence
		 */
		public ExcludedEventMatcher(ExcludedEvent expectedExcludedEvent) {
			this.excludedEvent = expectedExcludedEvent;
		}

		/* (non-Javadoc)
		 * @see org.junit.matchers.TypeSafeMatcher#matchesSafely(java.lang.Object)
		 */
		@Override
		public boolean matchesSafely(ExcludedEvent item) {
			boolean result = true;
			
			if (!same(excludedEvent.getEvent(),item.getEvent())){
				result = false;
			}
			if (!same(excludedEvent.getModuleName(),item.getModuleName())){
				result = false;
			}
			if (!same(excludedEvent.getFlowName(),item.getFlowName())){
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
	
	@Test
	public void testList(){
		
		final PagedSearchResult<ExcludedEvent> listing = new ArrayListPagedSearchResult<ExcludedEvent>(new ArrayList<ExcludedEvent>(),0,25);
		final String moduleName = "moduleName";
		final String flowName = "flowName";
		
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).findExcludedEvents(0, 25, "orderByField", true, moduleName, flowName);will(returnValue( listing));
            }
        });
		
		PagedSearchResult<ExcludedEvent> result = excludedEventService.getExcludedEvents(0, 25, "orderByField",true, moduleName, flowName);
		Assert.assertEquals("resultant list should be that returned from dao", listing, result);
		
		mockery.assertIsSatisfied();
	}
	
	@Test 
	public void testGetExcludedEvent(){
		final long excludedEventId = 1l;
		final ExcludedEvent excludedEvent = new ExcludedEvent(null, null, null, null);
		
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).getExcludedEvent(excludedEventId);will(returnValue(excludedEvent));
            }
        });
		
		Assert.assertEquals("ExcludedEvent returned by getExcludedEvent should be that returned by dao method ", excludedEvent, excludedEventService.getExcludedEvent(excludedEventId));
		
		mockery.assertIsSatisfied();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testResubmit_withNonExistantExcludedEvent_willThrowIllegalArgumentException(){
		final long excludedEventId = 1l;
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).getExcludedEvent(excludedEventId);will(returnValue(null));
            }
        });
		excludedEventService.resubmit(excludedEventId);
		mockery.assertIsSatisfied();
	
	}

	
	@Test(expected=IllegalArgumentException.class)
	public void testResubmit_withExcludedEventFromUnknownModule_willThrowIllegalArgumentException(){
		final long excludedEventId = 1l;
		final String moduleName = "unknownModule";
		
		final ExcludedEvent excludedEvent = new ExcludedEvent(null, moduleName, null, null);
		
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).getExcludedEvent(excludedEventId);will(returnValue(excludedEvent));
            	one(moduleService).getModule(moduleName);will(returnValue(null));
            }
        });
		excludedEventService.resubmit(excludedEventId);
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testResubmit_withSuccessfulResubmission_willDeleteExcludedEvent(){
		final long excludedEventId = 1l;
		final String moduleName = "moduleName";
		final String flowName = "unknownFlow";
		final Event event = mockery.mock(Event.class);
		
		final ExcludedEvent excludedEvent = new ExcludedEvent(event, moduleName, flowName, null);
		final Module module = mockery.mock(Module.class);
		final Flow flow = mockery.mock(Flow.class);
		final Map<String, Flow> flows = new HashMap<String,Flow>();
		flows.put(flowName, flow);
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).getExcludedEvent(excludedEventId);will(returnValue(excludedEvent));
            	one(moduleService).getModule(moduleName);will(returnValue(module));
            	one(module).getFlows();will(returnValue(flows));
            	one(flow).invoke(with(any(FlowInvocationContext.class)), with(equal(event)));
            	one(excludedEventDao).delete(excludedEvent);
            }
        });
		
		excludedEventService.resubmit(excludedEventId);
		mockery.assertIsSatisfied();
	}
	
	public void testResubmit_withExcludedEventFromUnknownFlow_willThrowIllegalArgumentException(){
		final long excludedEventId = 1l;
		final String moduleName = "moduleName";
		final String flowName = "unknownFlow";
		
		final ExcludedEvent excludedEvent = new ExcludedEvent(null, moduleName, flowName, null);
		final Module module = mockery.mock(Module.class);
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).getExcludedEvent(excludedEventId);will(returnValue(excludedEvent));
            	one(moduleService).getModule(moduleName);will(returnValue(module));
            	one(module).getFlows();will(returnValue(new HashMap<String,Flow>()));
            }
        });
		excludedEventService.resubmit(excludedEventId);
		mockery.assertIsSatisfied();
	}
	
}
