package org.ikasan.framework.initiator;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.hamcrest.Description;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.IkasanExceptionActionImpl;
import org.ikasan.framework.exception.IkasanExceptionActionType;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.flow.FlowInvocationContext;
import org.ikasan.framework.monitor.MonitorListener;
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
    
    /**
     * mocked error logging service
     */
    private ErrorLoggingService errorLoggingService = mockery.mock(ErrorLoggingService.class);
    
    /**
     * An arbitrary name for a component in the flow
     */
    final String componentName = "componentName";
    
    /**
     * An arbitrary name for the flow
     */
    final String flowName = "flowName";
    
    final IkasanExceptionAction rollbackStopAction =  new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_STOP);
    
    final long retryDelay = 1000;
    
    final IkasanExceptionAction rollbackRetryTwiceAction =  new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_RETRY, retryDelay, 2);
    
    final IkasanExceptionAction rollbackRetryOnceAction =  new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_RETRY, retryDelay, 1);
    
     /**
     * System under test
     */
    private AbstractInitiator abstractInitiator = new MockInitiator(moduleName,initiatorName, flow,exceptionHandler, errorLoggingService);

    /**
     * Constructor
     */
    public AbstractInitiatorTest(){
    	eventsToPlay.add(event1);
    	eventsToPlay.add(event2);
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
        
        ((MockInitiator)abstractInitiator).setRetryCount(0);
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
        Assert.assertFalse("just checking that our mock implementation has not had startInitiator called on it before", ((MockInitiator)abstractInitiator).isStartInitiatorCalled());
        abstractInitiator.start();
        Assert.assertTrue("startInitiator should have been called as a part of the start method", ((MockInitiator)abstractInitiator).isStartInitiatorCalled());
    }

    @Test
    public void testStop_onRecoveringInitiator_willSetStoppingFlagAndCancelRetryBeforeInvokingStopInitiator(){
        ((MockInitiator)abstractInitiator).setRetryCount(0);
        
        Assert.assertFalse("just checking that our mock implementation has not had stopInitiator called on it before", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
        abstractInitiator.stop();
        Assert.assertTrue("stopInitiator should have been called as a part of the stop method", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
    }
   
    @Test
    public void testStop_onNonRecoveringInitiator_willSetStoppingBeforeInvokingStopInitiator(){
        ((MockInitiator)abstractInitiator).setRetryCount(0);
        
        Assert.assertFalse("just checking that our mock implementation has not had stopInitiator called on it before", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
        abstractInitiator.stop();
        Assert.assertTrue("stopInitiator should have been called as a part of the stop method", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
    }
    

    

    
    /**
     * Tests that handling a null action will complete the retry cycle on a recovering initiator
     */
    @Test
    public void testHandleAction_withNullExceptionAction_onRecoveringInitiator_willCompleteRetry(){
        //set up a recovering initiator
        ((MockInitiator)abstractInitiator).setRetryCount(0);
        

        //invoke handeAction with null
        ((MockInitiator)abstractInitiator).handleAction(null);
        Assert.assertTrue("completeRetry should have been called on concrete implemetation when handling a null action on a recovering Initiator", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());
    }
    
    
    /**
     * Tests that handling a stop action will complete the retry cycle on a recovering initiator, and stop it in error
     */
    @Test
    public void testHandleAction_withStopExceptionAction_onRecoveringInitiator_willCancelRetryAndStopInError(){
        //set up a recovering initiator
        ((MockInitiator)abstractInitiator).setRetryCount(0);
        

        //invoke handeAction with stopAction
    	AbortTransactionException abortTransactionException = null;
    	try{
        	((MockInitiator)abstractInitiator).handleAction(rollbackStopAction);
        	Assert.fail("action implied rollback which should have thrown an AbortTransactionException");
    	} catch(AbortTransactionException exception){
    		abortTransactionException = exception;
    	}
    	Assert.assertNotNull("action implied rollback which should have thrown an AbortTransactionException", abortTransactionException);
        
    	
    	Assert.assertTrue("cancelRetry should have been called on concrete implemetation when handling a stop action on a recovering Initiator", ((MockInitiator)abstractInitiator).isCancelRetryCycleCalled());
    	Assert.assertTrue("stopInitiator should have been called on concrete implemetation when handling a stop action on a recovering Initiator", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
    	Assert.assertTrue("initiator should now be stopping", abstractInitiator.isStopping());
    	Assert.assertTrue("initiator should now be in error", abstractInitiator.isError());
    }
    
    /**
     * Tests that handling a stop action will stop it in error
     */
    @Test
    public void testHandleAction_withStopExceptionAction_onRunningInitiator_willStopInError(){
        //invoke handeAction with stopAction
    	AbortTransactionException abortTransactionException = null;
    	try{
        	((MockInitiator)abstractInitiator).handleAction(rollbackStopAction);
        	Assert.fail("action implied rollback which should have thrown an AbortTransactionException");
    	} catch(AbortTransactionException exception){
    		abortTransactionException = exception;
    	}
    	Assert.assertNotNull("action implied rollback which should have thrown an AbortTransactionException", abortTransactionException);
        
    	
    	Assert.assertFalse("cancelRetry should not have been called on concrete implemetation when handling a stop action on a recovering Initiator", ((MockInitiator)abstractInitiator).isCancelRetryCycleCalled());
    	Assert.assertTrue("stopInitiator should have been called on concrete implemetation when handling a stop action on a recovering Initiator", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
    	Assert.assertTrue("initiator should now be stopping", abstractInitiator.isStopping());
    	Assert.assertTrue("initiator should now be in error", abstractInitiator.isError());
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
        	((MockInitiator)abstractInitiator).handleAction(rollbackRetryTwiceAction);
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
        	((MockInitiator)abstractInitiator).handleAction(rollbackRetryOnceAction);
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
        	((MockInitiator)abstractInitiator).handleAction(rollbackRetryOnceAction);
        	Assert.fail("action implied rollback which should have thrown an AbortTransactionException");
    	} catch(AbortTransactionException exception){
    		abortTransactionException = exception;
    	}
    	Assert.assertNotNull("action implied rollback which should have thrown an AbortTransactionException", abortTransactionException);
    	Assert.assertTrue("retry count should now be 0", 0==abstractInitiator.getRetryCount());
    	
    	
    	//invoke again, as the first retry. This time, receiving the rollbackRetryOnce, should cause it to stop in error as it has already now retried its one time
    	abortTransactionException = null;
    	try{
        	((MockInitiator)abstractInitiator).handleAction(rollbackRetryOnceAction);
        	Assert.fail("action implied rollback which should have thrown an AbortTransactionException");
    	} catch(AbortTransactionException exception){
    		abortTransactionException = exception;
    	}
    	
    	
    	Assert.assertNotNull("action implied rollback which should have thrown an AbortTransactionException", abortTransactionException);
    	Assert.assertTrue("cancelRetry should have been called on concrete implemetation when handling a stop action on a recovering Initiator", ((MockInitiator)abstractInitiator).isCancelRetryCycleCalled());
    	Assert.assertTrue("stopInitiator should have been called on concrete implemetation when handling a stop action on a recovering Initiator", ((MockInitiator)abstractInitiator).isStopInitiatorCalled());
    	Assert.assertTrue("initiator should now be stopping", abstractInitiator.isStopping());
    	Assert.assertTrue("initiator should now be in error", abstractInitiator.isError());


    }
    
    
    
    /**
     * Tests that invocation of invokeFlow with a null Event List will invoke the handleAction(null) routine
     */
    @Test
    public void testInvokeFlow_withNullEventList_willResultInHandlingNullAction(){
        ((MockInitiator)abstractInitiator).setRetryCount(0);
        Assert.assertFalse("just checking that our mock implementation has not had completeRetry called on it before", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());

        //invoke the method that will result in invokeFlow being called
        ((MockInitiator)abstractInitiator).invokeInvokeFlow(null);
        Assert.assertTrue("handleAction should have been called with null exceptionAction", ((MockInitiator)abstractInitiator).isHandleActionCalled());
        Assert.assertNull("handleAction should have been called with null exceptionAction", ((MockInitiator)abstractInitiator).getHandleActionArgument());

    }
    
    
    /**
     * Tests that invocation of invokeFlow with an empty Event List will invoke the handleAction(null) routine
     */
    @Test
    public void testInvokeFlow_withEmptyEventList_willResultInHandlingNullAction(){
        ((MockInitiator)abstractInitiator).setRetryCount(0);
        Assert.assertFalse("just checking that our mock implementation has not had completeRetry called on it before", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());

        //invoke the method that will result in invokeFlow being called
        ((MockInitiator)abstractInitiator).invokeInvokeFlow(new ArrayList<Event>());
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
        ((MockInitiator)abstractInitiator).setRetryCount(0);
        Assert.assertFalse("just checking that our mock implementation has not had completeRetry called on it before", ((MockInitiator)abstractInitiator).isCompleteRetryCycleCalled());
        
        final Sequence sequence = mockery.sequence("invocationSequence"); 

        //expect event1 to be played cleanly
        expectFlowInvocationSuccess(event1, sequence);
        
        //followed by event2 to be played cleanly
        expectFlowInvocationSuccess(event2, sequence);
        
        //invoke the method that will result in invokeFlow being called
        ((MockInitiator)abstractInitiator).invokeInvokeFlow(eventsToPlay);
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
       
        final IkasanExceptionAction exceptionAction = new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_STOP);
        final Sequence sequence = mockery.sequence("invocationSequence");
        
        //expect the first event to get played, but fail resulting in an exceptionAction
        expectFlowInvocationFailure(event1, sequence, exceptionAction);
        
        
        //invoke the method that will result in invokeFlow being called
        AbortTransactionException abortTransactionException = null;
        try{
	        ((MockInitiator)abstractInitiator).invokeInvokeFlow(eventsToPlay);
	        Assert.fail();
        }catch(AbortTransactionException exception){
        	abortTransactionException = exception;
        }
        Assert.assertNotNull(abortTransactionException);
        
        //check that the action was handled
        Assert.assertTrue("handleAction should have been called with the exceptionAction returned by the exceptionHandler",((MockInitiator)abstractInitiator).isHandleActionCalled());
        Assert.assertEquals("handleAction should have been called with the exceptionAction returned by the exceptionHandler",exceptionAction, ((MockInitiator)abstractInitiator).getHandleActionArgument());
        
        //and did it attempt to stop?
        Assert.assertTrue("Initiator should now be stopping, if the exceptionAction was handled",abstractInitiator.isStopping());
        
        mockery.assertIsSatisfied();
    }


	/**
	 * @param event
	 * @param sequence
	 * @param exceptionAction
	 */
	private void expectFlowInvocationFailure(final Event event,
			final Sequence sequence, final IkasanExceptionAction exceptionAction) {

        final Throwable throwable = new RuntimeException();
        mockery.checking(new Expectations()
        {
            {
            	//invoke the flow will update the flow invocation context before failing
                one(flow).invoke((FlowInvocationContext)(with(a(FlowInvocationContext.class))), (Event) with(equal(event)));
                inSequence(sequence);
                will(doAll(addComponentNameToContext(componentName), throwException(throwable)));

                //gets the name of the flow from the flow
                one(flow).getName();
                inSequence(sequence);
                will(returnValue(flowName));
                
                //invokes the errorLoggingService
                one(errorLoggingService).logError(throwable,moduleName,flowName,componentName,event);
                
                
                //calls off to the exceptionHandler which returns an exceptionAction
                one(exceptionHandler).invoke(componentName,event,throwable);
                will(returnValue(exceptionAction));
                inSequence(sequence);
                
            }
        });
	}
    
    /**
     * Tests that invocation of invokeFlow with a 2 Event List, where the second Event fails will
     * 	1) invoke the flow with the both Events
     *  2) get an exceptionAction back for the second event only
     *  3) handle action
     */
    @Test
    public void testInvokeFlow_withTwoEventsSecondFailing_willHandleAction(){        
        final Sequence sequence = mockery.sequence("invocationSequence"); 
        
        

        
        //first expect event 1 played successfully
        expectFlowInvocationSuccess(event1, sequence);
        
        //secondly expect event 2 be played, but fail producing an errorAction
        expectFlowInvocationFailure(event2, sequence, rollbackStopAction);
        
        
        //invoke the method that will result in invokeFlow being called
        AbortTransactionException abortTransactionException = null;
        try{
	        ((MockInitiator)abstractInitiator).invokeInvokeFlow(eventsToPlay);
	        Assert.fail();
        }catch(AbortTransactionException exception){
        	abortTransactionException = exception;
        }
        Assert.assertNotNull(abortTransactionException);
        
        //check that the action was handled
        Assert.assertTrue("Initiator should now be stopping, if the exceptionAction was handled",abstractInitiator.isStopping());
        
        mockery.assertIsSatisfied();
    }

	/**
	 * @param event 
	 * @param sequence
	 */
	private void expectFlowInvocationSuccess(final Event event, final Sequence sequence) {
		mockery.checking(new Expectations()
        {
            {
            	//event gets played successfully
                one(flow).invoke((FlowInvocationContext)(with(a(FlowInvocationContext.class))), (Event) with(equal(event)));
                inSequence(sequence);
                will(returnValue(null)); 
            }
        });
	}
    
    public static  Action addComponentNameToContext(String componentName) {
        return new AddComponentNameAction(componentName);
    }
    class MockInitiator extends AbstractInitiator implements Initiator{
        
    	private Logger logger = Logger.getLogger(MockInitiator.class);

		private boolean running = true;

        private boolean startInitiatorCalled = false;
        private boolean completeRetryCycleCalled = false;
        private boolean cancelRetryCycleCalled = false;
        private boolean handleActionCalled = false;
        private boolean continueRetryCycleCalled = false;
        private boolean startRetryCycleCalled = false;
        private Long continueRetryCycleArgument = null; 
        private IkasanExceptionAction handleActionArgument = null;
        private Integer startRetryMaxAttemptsArgument = null;
        private Long startRetryDelayArgument = null;
        

        public boolean isStartRetryCycleCalled()
        {
            return startRetryCycleCalled;
        }
        
        public boolean isCompleteRetryCycleCalled()
        {
            return completeRetryCycleCalled;
        }
        
        public boolean isCancelRetryCycleCalled()
        {
            return cancelRetryCycleCalled;
        }


        public void invokeHandleAction(IkasanExceptionAction ikasanExceptionAction){
            handleAction(ikasanExceptionAction);
        }
        
        public void invokeInvokeFlow(List<Event>events){
            invokeFlow(events);
        }
        
        
        public boolean isStartInitiatorCalled()
        {
            return startInitiatorCalled;
        }

        @Override
		protected void continueRetryCycle(long delay) {
			continueRetryCycleCalled=true;
			continueRetryCycleArgument = delay;
			super.continueRetryCycle(delay);
		}
        
        public Long getContinueRetryCycleArgument(){
        	return continueRetryCycleArgument;
        }
        
        public Long getStartRetryCycleDelayArgument(){
        	return startRetryDelayArgument;
        }
        
        public Integer getStartRetryCycleMaxAttemptsArgument(){
        	return startRetryMaxAttemptsArgument;
        }
        
        public boolean isContinueRetryCycleCalled(){
        	return continueRetryCycleCalled;
        }


        public boolean isStopInitiatorCalled()
        {
            return stopInitiatorCalled;
        }

        private boolean stopInitiatorCalled = false;

        public MockInitiator(String moduleName, String name, Flow flow, IkasanExceptionHandler exceptionHandler, ErrorLoggingService errorLoggingService)
        {
            super(moduleName, name, flow, exceptionHandler);
            setErrorLoggingService(errorLoggingService);
        }
        public void setRetryCount(int retryCount){
        	this.retryCount = retryCount;
        }
        
        
        @Override
		protected void handleAction(IkasanExceptionAction action) {
			handleActionCalled=true;
			handleActionArgument = action;
			super.handleAction(action);
		}

        public boolean isHandleActionCalled(){
        	return handleActionCalled;
        }
        
        public IkasanExceptionAction getHandleActionArgument(){
        	return handleActionArgument;
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
        protected void cancelRetryCycle()
        {
        	retryCount=null;
            cancelRetryCycleCalled = true;
        }

        @Override
        protected Logger getLogger()
        {
            return logger;
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
        protected void startRetryCycle(Integer maxAttempts, long delay) throws InitiatorOperationException
        {
            startRetryCycleCalled = true;
            startRetryMaxAttemptsArgument = maxAttempts;
            startRetryDelayArgument = delay;
            
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
