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
import org.ikasan.framework.error.model.ErrorOccurrence;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.event.exclusion.dao.ExcludedEventDao;
import org.ikasan.framework.event.exclusion.model.ExcludedEvent;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.flow.invoker.FlowInvocationContext;
import org.ikasan.framework.initiator.AbortTransactionException;
import org.ikasan.framework.management.search.ArrayListPagedSearchResult;
import org.ikasan.framework.management.search.PagedSearchResult;
import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.service.ModuleService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
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
    
    private Event event = mockery.mock(Event.class);
    
    private ExcludedEvent excludedEvent = mockery.mock(ExcludedEvent.class);
    
    private ErrorOccurrence errorOccurrence = mockery.mock(ErrorOccurrence.class);
    
    private List<ErrorOccurrence> errorOccurrences = new ArrayList<ErrorOccurrence>();
    
    private ExcludedEventDao excludedEventDao = mockery.mock(ExcludedEventDao.class);
    
    private ErrorLoggingService errorLoggingService = mockery.mock(ErrorLoggingService.class);
    
    private ExcludedEventListener excludedEventListener1 = mockery.mock(ExcludedEventListener.class, "excludedEventListener1");
    
    private ExcludedEventListener excludedEventListener2 = mockery.mock(ExcludedEventListener.class, "excludedEventListener2");
	
    private ModuleService moduleService = mockery.mock(ModuleService.class);
    
	final String eventId = "eventId";
	
	final String resolver = "resolver";
	
	
	
	
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
		excludedEventService = new ExcludedEventServiceImpl(excludedEventDao,errorLoggingService, listeners,moduleService);
		errorOccurrences.add(errorOccurrence);
	}
	
	@Test
	public void testExcludeEvent(){
		final String moduleName = "moduleName";
		final String flowName = "flowName";
		
		final Sequence sequence = mockery.sequence("invocationSequence");
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).save(with(new ExcludedEventMatcher(new ExcludedEvent(event,moduleName, flowName, new Date()))));
            	inSequence(sequence);
            	one(excludedEventListener1).notifyExcludedEvent(event);
            	inSequence(sequence);
            	one(excludedEventListener2).notifyExcludedEvent(event);
            	inSequence(sequence);
            }
        });
		
		excludedEventService.excludeEvent(event, moduleName, flowName);
		
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


		final Sequence sequence = mockery.sequence("invocationSequence");
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).getExcludedEvent(eventId, false);inSequence(sequence);will(returnValue(excludedEvent));
            	one(errorLoggingService).getErrorOccurrences(eventId);inSequence(sequence);will(returnValue(errorOccurrences));
            	one(excludedEvent).setErrorOccurrences(errorOccurrences);
            }
        });
		
		ExcludedEvent excludedEventResult = excludedEventService.getExcludedEvent(eventId);
		Assert.assertEquals("ExcludedEvent returned by getExcludedEvent should be that returned by dao method ", excludedEvent, excludedEventResult);
		
		mockery.assertIsSatisfied();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testResubmit_withNonExistantExcludedEvent_willThrowIllegalArgumentException(){
		final String eventId = "eventId";
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).getExcludedEvent(eventId, false);will(returnValue(null));
            }
        });
		excludedEventService.resubmit(eventId, resolver);
		mockery.assertIsSatisfied();
	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCancel_withNonExistantExcludedEvent_willThrowIllegalArgumentException(){
		final String eventId = "eventId";
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).getExcludedEvent(eventId, true);will(returnValue(null));
            }
        });
		excludedEventService.cancel(eventId, resolver);
		mockery.assertIsSatisfied();
	
	}

	
	@Test(expected=IllegalArgumentException.class)
	public void testResubmit_withExcludedEventFromUnknownModule_willThrowIllegalArgumentException(){
		final String eventId = "eventId";
		final String moduleName = "unknownModule";
		
		final ExcludedEvent excludedEvent = new ExcludedEvent(null, moduleName, null, null);
		
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).getExcludedEvent(eventId, false);will(returnValue(excludedEvent));
            	one(moduleService).getModule(moduleName);will(returnValue(null));
            }
        });
		excludedEventService.resubmit(eventId, resolver);
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testResubmit_withSuccessfulResubmission_willMarkExcludedEventAsResubmitted(){
		final String eventId = "eventId";
		final String moduleName = "moduleName";
		final String flowName = "unknownFlow";
		final Event event = mockery.mock(Event.class);
		
		final ExcludedEvent excludedEvent = mockery.mock(ExcludedEvent.class);
		final Module module = mockery.mock(Module.class);
		final Flow flow = mockery.mock(Flow.class);
		final Map<String, Flow> flows = new HashMap<String,Flow>();
		flows.put(flowName, flow);
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).getExcludedEvent(eventId, false);will(returnValue(excludedEvent));
            	one(excludedEvent).isResolved();will(returnValue(false));
            	one(excludedEvent).getModuleName();will(returnValue(moduleName));
            	one(moduleService).getModule(moduleName);will(returnValue(module));
            	one(module).getFlows();will(returnValue(flows));
            	one(excludedEvent).getFlowName();will(returnValue(flowName));
            	one(excludedEvent).getEvent();will(returnValue(event));
            	one(flow).invoke(with(any(FlowInvocationContext.class)), with(equal(event)));
            	one(excludedEventDao).getExcludedEvent(eventId, true);will(returnValue(excludedEvent));
            	one(excludedEvent).resolveAsResubmitted(resolver);
            	one(excludedEventDao).save(excludedEvent);
            }
        });
		
		excludedEventService.resubmit(eventId, resolver);
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void testCancel_willMarkExcludedEventAsCancelled(){
		final String eventId = "eventId";
		final String moduleName = "moduleName";
		
		final ExcludedEvent excludedEvent = mockery.mock(ExcludedEvent.class);
		final Module module = mockery.mock(Module.class);
		
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).getExcludedEvent(eventId, true);will(returnValue(excludedEvent));
            	one(excludedEvent).isResolved();will(returnValue(false));
            	one(excludedEvent).getModuleName();will(returnValue(moduleName));
            	one(moduleService).getModule(moduleName);will(returnValue(module));
            	one(excludedEvent).resolveAsCancelled(resolver);
            	one(excludedEventDao).save(excludedEvent);
            }
        });
		
		excludedEventService.cancel(eventId, resolver);
		mockery.assertIsSatisfied();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testResubmit_withExcludedEventFromUnknownFlow_willThrowIllegalArgumentException(){
		final String eventId = "eventId";
		final String moduleName = "moduleName";
		final String flowName = "unknownFlow";
		
		final ExcludedEvent excludedEvent = new ExcludedEvent(null, moduleName, flowName, null);
		final Module module = mockery.mock(Module.class);
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).getExcludedEvent(eventId, false);will(returnValue(excludedEvent));
            	one(moduleService).getModule(moduleName);will(returnValue(module));
            	one(module).getFlows();will(returnValue(new HashMap<String,Flow>()));
            }
        });
		excludedEventService.resubmit(eventId, resolver);
		mockery.assertIsSatisfied();
	}
	
	@Test(expected=IllegalStateException.class)
	public void testResubmit_withAlreadyResubmittedEvent_willThrowIllegalStateException(){

		
		final ExcludedEvent excludedEvent = mockery.mock(ExcludedEvent.class);

		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).getExcludedEvent(eventId, false);will(returnValue(excludedEvent));
            	one(excludedEvent).isResolved();will(returnValue(true));
            }
        });
		excludedEventService.resubmit(eventId, resolver);
		mockery.assertIsSatisfied();
	}
	
	@Test(expected=AbortTransactionException.class)
	public void testResubmit_whereResubmissionFails_willCallErrorLoggingServiceAndThrowAbortTransactionException() throws CloneNotSupportedException{
		final String eventId = "eventId";
		final String moduleName = "moduleName";
		final String flowName = "flowName";
		final String componentName = "componentName";
		final Event event = mockery.mock(Event.class);
		final Event clonedEvent = mockery.mock(Event.class);
		
		final ExcludedEvent excludedEvent = new ExcludedEvent(event, moduleName, flowName, null);
		final Module module = mockery.mock(Module.class);
		final Flow flow = mockery.mock(Flow.class);
		final Map<String, Flow> flows = new HashMap<String,Flow>();
		flows.put(flowName, flow);
		
		final Throwable throwable = new RuntimeException();
		
		final Sequence sequence = mockery.sequence("invocation sequence");
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventDao).getExcludedEvent(eventId, false);
            	inSequence(sequence);
            	will(returnValue(excludedEvent));
            	
            	
            	one(moduleService).getModule(moduleName);
            	inSequence(sequence);
            	will(returnValue(module));
            	
            	
            	one(module).getFlows();
            	inSequence(sequence);
            	will(returnValue(flows));
            	
            	//invoke the flow will update the flow invocation context before failing
                one(flow).invoke((FlowInvocationContext)(with(a(FlowInvocationContext.class))), (Event) with(equal(event)));
                inSequence(sequence);
                will(doAll(addComponentNameToContext(componentName), throwException(throwable)));

                
                //invokes the errorLoggingService
                one(event).clone();will(returnValue(clonedEvent));
                
                one(errorLoggingService).logError(throwable,moduleName,flowName,componentName,clonedEvent, null);
                inSequence(sequence);
            }
        });
		excludedEventService.resubmit(eventId, resolver);
		mockery.assertIsSatisfied();
	}
	
    public static  Action addComponentNameToContext(String componentName) {
        return new AddComponentNameAction(componentName);
    }
	
	
}
/**
 * Models the action of the flow updating the FlowInvocationContext with a componentName
 *
 */
class AddComponentNameAction implements Action {
    private String componentName;
    
    public AddComponentNameAction(String componentName) {
        this.componentName = componentName;
    }
    
    public void describeTo(Description description) {

    }
    
    public Object invoke(Invocation invocation) throws Throwable {
    	((FlowInvocationContext)invocation.getParameter(0)).addInvokedComponentName(componentName);   
        return null;
    }
}
