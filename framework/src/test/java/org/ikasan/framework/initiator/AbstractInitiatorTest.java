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
package org.ikasan.framework.initiator;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.hamcrest.Description;
import org.ikasan.core.flow.Flow;
import org.ikasan.core.flow.invoker.FlowInvocationContext;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.event.exclusion.service.ExcludedEventService;
import org.ikasan.framework.exception.ExcludeEventAction;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.RetryAction;
import org.ikasan.framework.exception.StopAction;
import org.ikasan.framework.monitor.MonitorListener;
import org.ikasan.spec.configuration.ConfigurationException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;


public class AbstractInitiatorTest
{

    
    /**
     * JMock Mockery
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    
    /**
     * mock MonitorListener
     */
    private MonitorListener firstMonitorListener = mockery.mock(MonitorListener.class, "firstMonitorListener");
 
    /**
     * mock MonitorListener
     */
    private MonitorListener secondMonitorListener = mockery.mock(MonitorListener.class, "secondMonitorListener");

    /**
     * name of the module
     */
    private String moduleName = "moduleName";
    
    /**
     * name of the initiator
     */
    private String initiatorName = "initiatorName";
 
    /**
     * mocked Flow
     */
    private Flow flow = mockery.mock(Flow.class);
        
    /**
     * An arbitrary name for a component in the flow
     */
    final String componentName = "componentName";
    
    
    /**
     * List of Events to play
     */
    private List<Event> eventsToPlay = new ArrayList<Event>();
    /**
     * mocked Event to play
     */
    private Event event1 = mockery.mock(Event.class, "Event1");
    
    /**
     * mocked Event to play
     */
    private Event event2 = mockery.mock(Event.class, "Event2");

    /**
     * mocked exception handler
     */
    private IkasanExceptionHandler exceptionHandler = mockery.mock(IkasanExceptionHandler.class);
    
    private IkasanExceptionAction rollbackStopAction = StopAction.instance();
    
    final long retryDelay = 1000;
    
    final IkasanExceptionAction rollbackRetryTwiceAction =  new RetryAction( retryDelay, 2);
    
    final IkasanExceptionAction rollbackRetryOnceAction =  new RetryAction( retryDelay, 1);

    final ExcludeEventAction excludeAction =  ExcludeEventAction.instance();

    /**
     * mocked error logging service
     */
    private ErrorLoggingService errorLoggingService = mockery.mock(ErrorLoggingService.class);
   
    /**
     * mocked ExcludedEventService
     */   
    private ExcludedEventService excludedEventService = mockery.mock(ExcludedEventService.class);
    
    /**
     * An arbitrary name for the flow
     */
    final String flowName = "flowName";
    
    
    
    
    /**
     * System under test
     */
    private AbstractInitiator abstractInitiator = new MockInitiator(moduleName,initiatorName, flow, exceptionHandler);

    
    public AbstractInitiatorTest() {
		super();
		eventsToPlay = new ArrayList<Event>();
		eventsToPlay.add(event1);
		eventsToPlay.add(event2);
		
		abstractInitiator.setErrorLoggingService(errorLoggingService);
	}

    
    @Test
    public void testConstructor(){
        //just testing the constructor our system under test already used
        
        Assert.assertEquals("name should be that passed in on constructor", initiatorName, abstractInitiator.getName());
        Assert.assertEquals("moduleName should be that passed in on constructor", moduleName, abstractInitiator.getModuleName());
        Assert.assertEquals("flow should be that passed in on constructor", flow, abstractInitiator.getFlow());
    }
    
    @Test 
    public void testNotifyMonitorListeners_willNotifyAllRegisteredListeners(){
        final String runningState = InitiatorState.RUNNING.getName();
        //add two monitorListeners and check that they are notified with the state name
        mockery.checking(new Expectations()
        {
            {
                one(firstMonitorListener).notify(runningState);
                one(secondMonitorListener).notify(runningState);
            }
        });
        
        abstractInitiator.addListener(firstMonitorListener);
        abstractInitiator.addListener(secondMonitorListener);
        abstractInitiator.notifyMonitorListeners();
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void testAddRemoveNotifyListener()
    {
        
        
        //by default there should be no monitorListeners registered with this initiator
        Assert.assertTrue("by default there should be no monitorListeners registered with this initiator", abstractInitiator.getMonitorListeners().isEmpty());
        
        abstractInitiator.addListener(firstMonitorListener);
        abstractInitiator.addListener(secondMonitorListener);
        
        Assert.assertEquals("there should be exactly 2 monitor listeners, following the registrtion of two distinct listeners", 2, abstractInitiator.getMonitorListeners().size());        
        Assert.assertEquals("first MonitorListener should be the first one added", firstMonitorListener, abstractInitiator.getMonitorListeners().get(0));
        Assert.assertEquals("second MonitorListener should be the second one added", secondMonitorListener, abstractInitiator.getMonitorListeners().get(1));
        
        //remove one of the listeners and check that only the other one now is registered
        abstractInitiator.removeListener(firstMonitorListener);       

        Assert.assertEquals("there should be exactly 1 monitor listeners, after we started with two, and we deregistered 1", 1, abstractInitiator.getMonitorListeners().size());        
        Assert.assertFalse("list of registered monitor listeners should not contain the one we deregistered", abstractInitiator.getMonitorListeners().contains(firstMonitorListener));
    }
    
    @Test
    public void testGetState(){
        Assert.assertEquals("getState should return InitiatorState.RUNNING if initiator implementation isRunning(), but not isRecovering()", InitiatorState.RUNNING,abstractInitiator.getState()); 
        
        ((MockInitiator)abstractInitiator).setRetryCount(new Integer(0));
        Assert.assertEquals("getState should return InitiatorState.RECOVERING if initiator implementation isRunning(), AND isRecovering()", InitiatorState.RECOVERING,abstractInitiator.getState()); 
    
        ((MockInitiator)abstractInitiator).setRunning(false);
        Assert.assertEquals("getState should return InitiatorState.STOPPED if initiator implementation !isRunning(), AND !isError()", InitiatorState.STOPPED,abstractInitiator.getState()); 
    
        ((MockInitiator)abstractInitiator).setError(true);
        Assert.assertEquals("getState should return InitiatorState.ERROR if initiator implementation !isRunning(), AND isError()", InitiatorState.ERROR,abstractInitiator.getState()); 
    }
    
    @Test 
    public void testIsError(){
        Assert.assertFalse("isError() should return value of the error flag", abstractInitiator.isError()); 
        ((MockInitiator)abstractInitiator).setError(true);
        Assert.assertTrue("isError() should return value of the error flag", abstractInitiator.isError()); 

    }
    
    @Test
    public void testStart_willClearFlagsBeforeInvokingStartInitiator(){
        
        //set up the mock implementation such that the error flag and the stopping flag are set beforehand, - we just want to make sure these get cleared on start
        ((MockInitiator)abstractInitiator).setError(true);
        ((MockInitiator)abstractInitiator).setStopping(true);
        
        expectFlowStartSuccess();
        Assert.assertFalse("just checking that our mock implementation has not had startInitiator called on it before", ((MockInitiator)abstractInitiator).isStartInitiatorCalled());
        abstractInitiator.start();
        Assert.assertTrue("startInitiator should have been called as a part of the start method", ((MockInitiator)abstractInitiator).isStartInitiatorCalled());
        mockery.assertIsSatisfied();
    }

    @Test
    public void testStartInitiator_withConfiguredResourcesFailing()
    {
        this.expectFlowStartConfiguredResourcesFailed();
        Assert.assertFalse("just checking that our mock implementation has not had startInitiator called on it before", ((MockInitiator)abstractInitiator).isStartInitiatorCalled());
        abstractInitiator.start();
        Assert.assertFalse("startInitiator should not have been called as a part of the start method", ((MockInitiator)abstractInitiator).isStartInitiatorCalled());
        mockery.assertIsSatisfied();
    }

    @Test
    public void testStop_onRecoveringInitiator_willSetStoppingFlagAndCancelRetryBeforeInvokingStopInitiator(){
    	((MockInitiator)abstractInitiator).setRetryCount(new Integer(0));
        
        expectFlowStopManagedResourcesSuccess();
        Assert.assertFalse("just checking that our mock implementation has not had stopInitiator called on it before", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
        abstractInitiator.stop();
        Assert.assertTrue("stopInitiator should have been called as a part of the stop method", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
        mockery.assertIsSatisfied();
    }
   
    @Test
    public void testStop_onNonRecoveringInitiator_willSetStoppingBeforeInvokingStopInitiator(){
    	((MockInitiator)abstractInitiator).setRetryCount(new Integer(0));
        
        expectFlowStopManagedResourcesSuccess();
        Assert.assertFalse("just checking that our mock implementation has not had stopInitiator called on it before", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
        abstractInitiator.stop();
        Assert.assertTrue("stopInitiator should have been called as a part of the stop method", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
        mockery.assertIsSatisfied();
    }
    
    /**
     * When handleAction is passed a null action, and the initiator is recovering, it should invoke the completeRetry routine
     */
    @Test
    public void testHandleAction_withNullAction_willCompleteRetryIfRecovering(){
    	((MockInitiator)abstractInitiator).setRetryCount(new Integer(0));
        Assert.assertFalse("just checking that our mock implementation has not had completeRetry called on it before", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());
        
        //invoke the method that will result in handleAction
        ((MockInitiator)abstractInitiator).invokeHandleAction(null);
        Assert.assertTrue("completeRetry should have been called on concrete implemetation when handling a null action on a recovering Initiator", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());
    }
    
    /**
     * Tests that handling a stop action will complete the retry cycle on a recovering initiator, and stop it in error
     */
    @Test
    public void testHandleAction_withStopExceptionAction_onRecoveringInitiator_willCancelRetryAndStopInError(){
        //set up a recovering initiator
        ((MockInitiator)abstractInitiator).setRetryCount(0);
        
        expectFlowStopManagedResourcesSuccess();

        //invoke handeAction with stopAction
    	AbortTransactionException abortTransactionException = null;
    	try{
        	((MockInitiator)abstractInitiator).handleAction(rollbackStopAction, null);
        	Assert.fail("action implied rollback which should have thrown an AbortTransactionException");
    	} catch(AbortTransactionException exception){
    		abortTransactionException = exception;
    	}
    	Assert.assertNotNull("action implied rollback which should have thrown an AbortTransactionException", abortTransactionException);
        
    	
    	Assert.assertTrue("cancelRetry should have been called on concrete implemetation when handling a stop action on a recovering Initiator", ((MockInitiator)abstractInitiator).isCancelRetryCycleCalled());
    	Assert.assertTrue("stopInitiator should have been called on concrete implemetation when handling a stop action on a recovering Initiator", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
    	Assert.assertTrue("initiator should now be stopping", abstractInitiator.isStopping());
    	Assert.assertTrue("initiator should now be in error", abstractInitiator.isError());
        mockery.assertIsSatisfied();
    }
    
    /**
     * Tests that handling a stop action will stop it in error
     */
    @Test
    public void testHandleAction_withStopExceptionAction_onRunningInitiator_willStopInError()
    {
        expectFlowStopManagedResourcesSuccess();
        //invoke handeAction with stopAction
    	AbortTransactionException abortTransactionException = null;
    	try{
        	((MockInitiator)abstractInitiator).handleAction(rollbackStopAction,null);
        	Assert.fail("action implied rollback which should have thrown an AbortTransactionException");
    	} catch(AbortTransactionException exception){
    		abortTransactionException = exception;
    	}
    	Assert.assertNotNull("action implied rollback which should have thrown an AbortTransactionException", abortTransactionException);
        
    	
    	Assert.assertFalse("cancelRetry should not have been called on concrete implemetation when handling a stop action on a recovering Initiator", ((MockInitiator)abstractInitiator).isCancelRetryCycleCalled());
    	Assert.assertTrue("stopInitiator should have been called on concrete implemetation when handling a stop action on a recovering Initiator", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
    	Assert.assertTrue("initiator should now be stopping", abstractInitiator.isStopping());
    	Assert.assertTrue("initiator should now be in error", abstractInitiator.isError());
        mockery.assertIsSatisfied();
    }
    
    
    /**
     * Tests that handling an EXCLUDE action will note the exclusion before rolling back
     */
    @Test
    public void testHandleAction_withExcludeAction_willNoteExclusion(){
    	final String currentEventId = "currentEventId";
    	
    	abstractInitiator.setExcludedEventService(excludedEventService);
    	
        //invoke handeAction with excludeAction
    	AbortTransactionException abortTransactionException = null;
    	try{
        	((MockInitiator)abstractInitiator).handleAction(excludeAction, currentEventId);
        	Assert.fail("action implied rollback which should have thrown an AbortTransactionException");
    	} catch(AbortTransactionException exception){
    		abortTransactionException = exception;
    	}
    	Assert.assertNotNull("action implied rollback which should have thrown an AbortTransactionException", abortTransactionException);
        
    	Assert.assertTrue("this event should be noted for exclusion following handling of an Exclude action for this event", abstractInitiator.getExclusions().contains(currentEventId));
    }
    
    /**
     * Tests that handling an EXCLUDE action will stop and rollback if exclusions are not supported
     */
    @Test
    public void testHandleAction_withExcludeAction_willStopAndRollbackIfExclusionsNotSupported(){
    	final String currentEventId = "currentEventId";
    	
    	//set the excludedEventService to null on the initiator
    	abstractInitiator.setExcludedEventService(null);
        expectFlowStopManagedResourcesSuccess();

        //invoke handeAction with excludeAction
    	AbortTransactionException abortTransactionException = null;
    	try{
        	((MockInitiator)abstractInitiator).handleAction(excludeAction,currentEventId);
        	Assert.fail("action implied rollback which should have thrown an AbortTransactionException");
    	} catch(AbortTransactionException exception){
    		abortTransactionException = exception;
    	}
    	Assert.assertNotNull("action implied rollback which should have thrown an AbortTransactionException", abortTransactionException);
        
    	Assert.assertTrue("initiator should now be stopping", abstractInitiator.isStopping());
    	Assert.assertTrue("initiator should now be in error", abstractInitiator.isError());
        mockery.assertIsSatisfied();
   }
    
    @Test 
    public void testSupportsExclusions(){
    	Assert.assertFalse("AbstractInitiator without an ExcludedEventService should not support exclusions", abstractInitiator.supportsExclusions());
    	abstractInitiator.setExcludedEventService(excludedEventService);
    	Assert.assertTrue("AbstractInitiator with an ExcludedEventService should support exclusions", abstractInitiator.supportsExclusions());

    }
    

    
    
    
    /**
     * Tests that handling a retry action on a recovering initiator will increment the retry count and rollback
     */
    @Test
    public void testHandleAction_withRetryExceptionAction_onRecoveringInitiator_willIncrementRetryCountAndContinueRetry(){
        //set up a recovering initiator - ie it has already run once, failed, and gone into recovery. This invocation will be its first retry
    	int initialRetryCount = 0;
    	((MockInitiator)abstractInitiator).setRetryCount(initialRetryCount);
    	
        //invoke handeAction with retry (once) action
    	AbortTransactionException abortTransactionException = null;
    	try{
        	((MockInitiator)abstractInitiator).handleAction(rollbackRetryTwiceAction,null);
        	Assert.fail("action implied rollback which should have thrown an AbortTransactionException");
    	} catch(AbortTransactionException exception){
    		abortTransactionException = exception;
    	}
    	Assert.assertNotNull("action implied rollback which should have thrown an AbortTransactionException", abortTransactionException);
        
    	Assert.assertTrue("retry count should have been incremeneted", initialRetryCount+1==abstractInitiator.getRetryCount());
    	Assert.assertTrue("continueRetry should have been called on concrete implemetation with the retry delay", ((MockInitiator)abstractInitiator).isContinueRetryCycleCalled());

    }
    
 
    /**
     * Tests that handling a retry action on a recovering initiator will increment the retry count and rollback
     */
    @Test
    public void testHandleAction_withRetryExceptionAction_onRunningInitiator_willSetRetryCountAndCallStartRetry(){

        //invoke handeAction with retry (once) action
    	AbortTransactionException abortTransactionException = null;
    	try{
        	((MockInitiator)abstractInitiator).handleAction(rollbackRetryOnceAction,null);
        	Assert.fail("action implied rollback which should have thrown an AbortTransactionException");
    	} catch(AbortTransactionException exception){
    		abortTransactionException = exception;
    	}
    	Assert.assertNotNull("action implied rollback which should have thrown an AbortTransactionException", abortTransactionException);
        
    	
    	Assert.assertTrue("retry count should be set at 0", 0==abstractInitiator.getRetryCount());
    	Assert.assertTrue("startRetry should have been called on concrete implemetation with the retry delay", ((MockInitiator)abstractInitiator).isStartRetryCycleCalled());
    	Assert.assertTrue("startRetry should have been called with maxAttempts 1", 1==((MockInitiator)abstractInitiator).getStartRetryCycleMaxAttemptsArgument());
    	Assert.assertTrue("startRetry should have been called with delay of 'retryDelay'", retryDelay==((MockInitiator)abstractInitiator).getStartRetryCycleDelayArgument());

    }   
    
    /**
     * Tests when the maximum retry account is reached on a recovering initiator, it will stop in error
     */
    @Test
    public void testHandleAction_withRetryExceptionAction_exceedingRetryCount_willStopInError(){

        //invoke handeAction with retry (once) action
    	AbortTransactionException abortTransactionException = null;
    	try{
        	((MockInitiator)abstractInitiator).handleAction(rollbackRetryOnceAction,null);
        	Assert.fail("action implied rollback which should have thrown an AbortTransactionException");
    	} catch(AbortTransactionException exception){
    		abortTransactionException = exception;
    	}
    	Assert.assertNotNull("action implied rollback which should have thrown an AbortTransactionException", abortTransactionException);
    	Assert.assertTrue("retry count should now be 0", 0==abstractInitiator.getRetryCount());
    	
    	//invoke again, as the first retry. This time, receiving the rollbackRetryOnce, should cause it to stop in error as it has already now retried its one time
    	abortTransactionException = null;
        expectFlowStopManagedResourcesSuccess();
    	try{
        	((MockInitiator)abstractInitiator).handleAction(rollbackRetryOnceAction,null);
        	Assert.fail("action implied rollback which should have thrown an AbortTransactionException");
    	} catch(AbortTransactionException exception){
    		abortTransactionException = exception;
    	}
    	
    	Assert.assertNotNull("action implied rollback which should have thrown an AbortTransactionException", abortTransactionException);
    	Assert.assertTrue("cancelRetry should have been called on concrete implemetation when handling a stop action on a recovering Initiator", ((MockInitiator)abstractInitiator).isCancelRetryCycleCalled());
    	Assert.assertTrue("stopInitiator should have been called on concrete implemetation when handling a stop action on a recovering Initiator", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
    	Assert.assertTrue("initiator should now be stopping", abstractInitiator.isStopping());
    	Assert.assertTrue("initiator should now be in error", abstractInitiator.isError());
        mockery.assertIsSatisfied();
    }
	
    /**
     * Tests that invocation of invokeFlow with a null Event List will invoke the handleAction(null) routine
     */
    @Test
    public void testInvokeFlow_withNullEvent_willResultInHandlingNullAction(){
        Assert.assertFalse("just checking that our mock implementation has not had completeRetry called on it before", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());

        //invoke the flow
        Event event = null;
        abstractInitiator.invokeFlow(event);
        Assert.assertTrue("handleAction should have been called with null exceptionAction", ((MockInitiator)abstractInitiator).isHandleActionCalled());
        Assert.assertNull("handleAction should have been called with null exceptionAction", ((MockInitiator)abstractInitiator).getHandleActionArgument());

    }
    
    /**
     * Tests that invocation of invokeFlow with an empty Event List will invoke the handleAction(null) routine
     */
    @Test
    public void testInvokeFlow_withEmptyEventList_willResultInHandlingNullAction(){
        Assert.assertFalse("just checking that our mock implementation has not had completeRetry called on it before", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());

        //invoke the method that will result in invokeFlow being called
        ((MockInitiator)abstractInitiator).invokeFlow(new ArrayList<Event>());
        Assert.assertTrue("handleAction should have been called with null exceptionAction", ((MockInitiator)abstractInitiator).isHandleActionCalled());
        Assert.assertNull("handleAction should have been called with null exceptionAction", ((MockInitiator)abstractInitiator).getHandleActionArgument());
    } 
    
    /**
     * Tests that invocation of invokeFlow with a 2 Event List will:
     * 	1) invoke the flow cleanly with each in turn
     *  2) not get an exception for either
     *  3) handle the null action 
     */
    @Test
    public void testInvokeFlow_withTwoEventsResultingInNoActions_willHandleNullAction(){
    	Assert.assertFalse("just checking that our mock implementation has not had completeRetry called on it before", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());
        
        final Sequence sequence = mockery.sequence("invocationSequence"); 

        //expect event1 to be played cleanly
        expectFlowInvocationSuccess(event1, sequence, "event1");
        
        //followed by event2 to be played cleanly
        expectFlowInvocationSuccess(event2, sequence, "event2");
        
        //invoke the method that will result in invokeFlow being called
        ((MockInitiator)abstractInitiator).invokeFlow(eventsToPlay);
        Assert.assertTrue("handleAction should have been called with null exceptionAction", ((MockInitiator)abstractInitiator).isHandleActionCalled());
        Assert.assertNull("handleAction should have been called with null exceptionAction", ((MockInitiator)abstractInitiator).getHandleActionArgument());
    
        mockery.assertIsSatisfied();
    }
    
    /**
     * Tests that invocation of invokeFlow with a 2 Event List, where the first Event fails will
     * 	1) invoke the flow with the first Event only
     *  2) get an exceptionAction back for the firstEvent
     *  3) handle action
     */
    @Test
    public void testInvokeFlow_withTwoEventsFirstFailing_willHandleAction(){        
       
        final IkasanExceptionAction exceptionAction = StopAction.instance();
        final Sequence sequence = mockery.sequence("invocationSequence");
        
        //expect the first event to get played, but fail resulting in an exceptionAction
        expectFlowInvocationFailure(event1, sequence, exceptionAction, "event1");

        // stop managed resources when initiator stops
        expectFlowStopManagedResourcesSuccess();
        
        //invoke the method that will result in invokeFlow being called
        AbortTransactionException abortTransactionException = null;
        try{
        	((MockInitiator)abstractInitiator).invokeFlow(eventsToPlay);
	        Assert.fail();
        }catch(AbortTransactionException exception){
        	abortTransactionException = exception;
        }
        Assert.assertNotNull(abortTransactionException);
        
        //check that the action was handled
        Assert.assertTrue("handleAction should have been called with the exceptionAction returned by the exceptionHandler",((MockInitiator)abstractInitiator).isHandleActionCalled());
        Assert.assertEquals("handleAction should have been called with the exceptionAction returned by the exceptionHandler",exceptionAction, ((MockInitiator)abstractInitiator).getHandleActionArgument());
                
        mockery.assertIsSatisfied();
    }    
    
    @Test
    public void testInvokeFlow_withEventNotedForExclusion_willExcludeEventAndInvokeNullAction(){
    	
    	//set up the initiator as if it had previously noted to exclude event1
    	abstractInitiator.setExcludedEventService(excludedEventService);
    	((MockInitiator)abstractInitiator).addExclusion("event1");
    	
        final Sequence sequence = mockery.sequence("invocationSequence"); 

        //expect event1 to be excluded
        expectEventExclusion(event1, sequence, "event1");
        
        //expect flow to be invoked with event 2
        expectFlowInvocationSuccess(event2, sequence, "event2");
        
        ((MockInitiator)abstractInitiator).invokeFlow(eventsToPlay);
        
        Assert.assertFalse("event1 should no longer be noted for exclusion following its exclusion", abstractInitiator.getExclusions().contains("event1"));
        mockery.assertIsSatisfied();	
    }

    /**
	 * @param event 
	 * @param sequence
	 */
	private void expectFlowInvocationSuccess(final Event event, final Sequence sequence, final String eventId) {
		mockery.checking(new Expectations()
        {
            {
            	//event gets played successfully
            	one(event).getId();will(returnValue(eventId));
            	
                one(flow).invoke((FlowInvocationContext) with(a(FlowInvocationContext.class)), (Event) with(equal(event)) );
                inSequence(sequence);
                will(returnValue(null)); 
            }
        });
	}

    /**
     * startManagedResources & configuredResource successful invocation expectations
     */
    private void expectFlowStartSuccess() {
        mockery.checking(new Expectations()
        {
            {
                one(flow).start();
            }
        });
    }

    /**
     * startConfiguredResources failed invocation expectations
     */
    private void expectFlowStartConfiguredResourcesFailed() 
    {
        final ConfigurationException exception = new ConfigurationException("test failed managed resource start");
        final String actionTaken = null;
        
        mockery.checking(new Expectations()
        {
            {
                one(flow).start();
                will(throwException(exception));
                
                //invokes the errorLoggingService
                one(errorLoggingService).logError(with(equal(exception)),with(equal(moduleName)),with(any(String.class)), with(equal(actionTaken)));
            }
        });
    }

    /**
     * stopManagedResources successful invocation expectations
     */
    private void expectFlowStopManagedResourcesSuccess() {
        mockery.checking(new Expectations()
        {
            {
                one(flow).stop();
            }
        });
    }

    /**
	 * @param event 
	 * @param sequence
	 */
	private void expectEventExclusion(final Event event, final Sequence sequence, final String eventId) {
		mockery.checking(new Expectations()
        {
            {
            	//event gets played successfully
            	one(event).getId();
                inSequence(sequence);
                will(returnValue(eventId));
                one(flow).getName();
                will(returnValue(flowName));
                inSequence(sequence);
                one(excludedEventService).excludeEvent(event, moduleName, flowName);
                inSequence(sequence);
            }
        });
	}
	/**
	 * @param event
	 * @param sequence
	 * @param exceptionAction
	 */
	private void expectFlowInvocationFailure(final Event event,
			final Sequence sequence, final IkasanExceptionAction exceptionAction, final String eventId) {
		final Throwable throwable = new RuntimeException();
        mockery.checking(new Expectations()
        {
            {
            	//invoke the flow will update the flow invocation context before failing
            	one(event).getId();will(returnValue(eventId));
                one(flow).invoke((FlowInvocationContext)(with(a(FlowInvocationContext.class))), (Event) with(equal(event)));
                inSequence(sequence);
                will(doAll(addComponentNameToContext(componentName), throwException(throwable)));
                
                
                //calls off to the exceptionHandler which returns an exceptionAction
                one(exceptionHandler).handleThrowable(componentName,throwable);
                will(returnValue(exceptionAction));
                inSequence(sequence);
                
                
                //gets the name of the flow from the flow
                one(flow).getName();
                inSequence(sequence);
                will(returnValue(flowName));
                
                
                //invokes the errorLoggingService
                one(errorLoggingService).logError(with(equal(throwable)),with(equal(moduleName)),with(equal(flowName)),with(equal(componentName)),with(equal(event)), (String)with(a(String.class)));
                inSequence(sequence);
            }
        });
	}
	
    public static  Action addComponentNameToContext(String componentName) {
        return new AddComponentNameAction(componentName);
    }
    
    class MockInitiator extends AbstractInitiator implements Initiator{
        
        private boolean running = true;

        private boolean startInitiatorCalled = false;
        private boolean completeRetryCycleCalled = false;
    	private IkasanExceptionAction handleActionArgument = null;
    	private boolean handleActionCalled = false;
    	private boolean cancelRetryCycleCalled = false;
    	private boolean continueRetryCycleCalled = false;
    	private Long continueRetryCycleArgument = null;
    	private boolean startRetryCycleCalled = false;
        private boolean stopInitiatorCalled = false;
		private Integer startRetryCycleMaxAttemptsArgument;
		private long startRetryCycleDelayArgument;
		private Logger logger = Logger.getLogger(MockInitiator.class);
        
        public boolean isStartRetryCycleCalled() {
			return startRetryCycleCalled;
		}


		public void addExclusion(String eventId) {
			exclusions.add(eventId);
		}


		public boolean isCompleteRetryCycleCalled()
        {
            return completeRetryCycleCalled;
        }


        public void setRetryCount(int i) {
			retryCount=i;
			
		}


		public void invokeHandleAction(IkasanExceptionAction ikasanExceptionAction){
            handleAction(ikasanExceptionAction, null);
        }
        
        
        public boolean isStartInitiatorCalled()
        {
            return startInitiatorCalled;
        }



        public boolean isStopInitiatorCalled()
        {
            return stopInitiatorCalled;
        }


        public Integer getStartRetryCycleMaxAttemptsArgument() {
			return startRetryCycleMaxAttemptsArgument;
		}


		public MockInitiator(String moduleName, String name, Flow flow, IkasanExceptionHandler exceptionHandler)
        {
            super(moduleName, name, flow, exceptionHandler);
        }

        public void setRunning(boolean running){
            this.running = running;
        }
        


        public String getType()
        {
            // TODO Auto-generated method stub
            return null;
        }


        
        public void setError(boolean error){
            this.error = error;
        }
        
        public void setStopping(boolean stopping){
            this.stopping = stopping;
        }

        public boolean isRecovering()
        {
            return retryCount!=null;
        }

        public boolean isRunning()
        {
            return running;
        }


        @Override
        protected void completeRetryCycle()
        {
            retryCount=null;
            completeRetryCycleCalled = true;
        }

        @Override
        protected Logger getLogger()
        {

            return logger;
        }

        @Override
        protected void cancelRetryCycle()
        {
        	cancelRetryCycleCalled=true;
            retryCount=null;
            
        }

        @Override
        protected void startInitiator() throws InitiatorOperationException
        {
            running = true;
            startInitiatorCalled=true;
            Assert.assertFalse("stopping flag should never be set by the time startInitiator is called", stopping);
            Assert.assertFalse("error flag should never be set by the time startInitiator is called", error);
            
        }

        @Override
        protected void stopInitiator() throws InitiatorOperationException
        {
            running = false;
            stopInitiatorCalled = true;
            Assert.assertTrue("stopping flag should always be set prior to stopInitiator being called", stopping);
            Assert.assertFalse("isRecovering() should never be true once stopInitiator is called", isRecovering());
        }


        @Override
		protected void continueRetryCycle(long delay) {
			continueRetryCycleCalled=true;
			continueRetryCycleArgument = delay;
			super.continueRetryCycle(delay);
		}
        
        public boolean isContinueRetryCycleCalled() {
			return continueRetryCycleCalled;
		}


		@Override
        protected void startRetryCycle(Integer maxAttempts, long delay) throws InitiatorOperationException
        {
            startRetryCycleCalled = true;
            startRetryCycleMaxAttemptsArgument = maxAttempts;
            startRetryCycleDelayArgument = delay;
            
        }
        
        public long getStartRetryCycleDelayArgument() {
			return startRetryCycleDelayArgument;
		}


		@Override
		protected void handleAction(IkasanExceptionAction action, String eventId) {
			handleActionCalled=true;
			handleActionArgument = action;
			super.handleAction(action, eventId);
		}

        public boolean isHandleActionCalled(){
        	return handleActionCalled;
        }
        
        public boolean isCancelRetryCycleCalled(){
        	return cancelRetryCycleCalled;
        }
        
        public IkasanExceptionAction getHandleActionArgument(){
        	return handleActionArgument;
        }
        
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
