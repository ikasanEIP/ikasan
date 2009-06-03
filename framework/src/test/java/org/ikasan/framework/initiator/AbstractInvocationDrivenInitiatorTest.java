/**
 * 
 */
package org.ikasan.framework.initiator;

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.error.service.ErrorLoggingService;
import org.ikasan.framework.event.service.EventProvider;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.flow.Flow;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.junit.Test;

/**
 * @author Ikasan Development Team
 *
 */
public class AbstractInvocationDrivenInitiatorTest {
	
	Mockery mockery = new Mockery();
	
	EventProvider eventProvider = mockery.mock(EventProvider.class);
	
	IkasanExceptionHandler exceptionHandler = mockery.mock(IkasanExceptionHandler.class);

	/**
	 * Tests that an invoke() call on a stopping InvocationDrivenInitiator will be ignored
	 * 
	 * Test method for {@link org.ikasan.framework.initiator.AbstractInvocationDrivenInitiator#invoke()}.
	 */
	@Test
	public void testInvoke_willDoNothingIfStopping() {
		MockAbstractInvocationDrivenInitiator initiator = new MockAbstractInvocationDrivenInitiator("name", "moduleName", null, null, eventProvider);
		initiator.stop();
		
		Assert.assertFalse("invokeFlow should never have been called on a new initiator", initiator.isInvokeFlowCalled());
		initiator.invoke();
		Assert.assertFalse("invokeFlow should not have been called on a stopping initiator when invoke is called", initiator.isInvokeFlowCalled());
	
		mockery.assertIsSatisfied();
	}
	
	/**
	 * Tests the happy path of invokeFlow being called with the successfully sourced List of Events as provided by the EventProvider
	 * 
	 * @throws ResourceException
	 */
	@Test
	public void testInvoke_willCallInvokeFlow_withResultOfPriorCallToEventProvider() throws ResourceException {
		MockAbstractInvocationDrivenInitiator initiator = new MockAbstractInvocationDrivenInitiator("name", "moduleName", null, null, eventProvider);
		final List<Event> providedEvents = new ArrayList<Event>();
        mockery.checking(new Expectations()
        {
            {
                one(eventProvider).getEvents();will(returnValue(providedEvents));
            }
        });
		
		
		Assert.assertFalse("invokeFlow should never have been called on a new initiator", initiator.isInvokeFlowCalled());
		initiator.invoke();
		Assert.assertTrue("invokeFlow should have been called on a non-stopping initiator when invoke is called", initiator.isInvokeFlowCalled());
		Assert.assertEquals("invokeFlow should be called with the Events provided by the eventProvider", providedEvents, initiator.getInvokeFlowArgument());
	
		mockery.assertIsSatisfied();
	}
	
	/**
	 * Tests the unhappy path of the EventProvider failing (throwing some Throwable). This should be logged, and the throwable dealt with as a result of the
	 * ExceptionHandler's action
	 * @throws ResourceException 
	 */
	@Test
	public void testInvoke_willLogErrorCallExceptionHandlerAndHandleAction_whenEventProviderThrows() throws ResourceException{
		final String initiatorName = "initiatorName";
		final String moduleName = "moduleName";
		
		MockAbstractInvocationDrivenInitiator initiator = new MockAbstractInvocationDrivenInitiator(initiatorName, moduleName, null, exceptionHandler, eventProvider);
		
		final ErrorLoggingService errorLoggingService = mockery.mock(ErrorLoggingService.class);
		initiator.setErrorLoggingService(errorLoggingService);
		
		final Throwable throwable = new NullPointerException();
		final IkasanExceptionAction exceptionAction = mockery.mock(IkasanExceptionAction.class);
        
		final Sequence sequence = mockery.sequence("invocationSequence");
		mockery.checking(new Expectations()
        {
            {
            	//eventProvider fails
                one(eventProvider).getEvents();
                inSequence(sequence);
                will(throwException(throwable));
                
                //errorService notified
                one(errorLoggingService).logError(throwable, moduleName, initiatorName);
                inSequence(sequence);
                
                //exceptionHandlerCalled
                one(exceptionHandler).invoke(initiatorName, throwable);
                inSequence(sequence);
                will(returnValue(exceptionAction));
            }
        });
        
        initiator.invoke();
        
        Assert.assertTrue("handleAction should have been called", initiator.isHandleActionCalled());
        Assert.assertEquals("exceptionAction passed to handleAction should be that which was returned from the exceptionHandler", exceptionAction, initiator.getHandActionArgument());
        
	
		mockery.assertIsSatisfied();
	}
	


}
/**
 * Mock extension of the abstact class under test
 * 
 * @author Ikasan Development Team
 *
 */
class MockAbstractInvocationDrivenInitiator extends AbstractInvocationDrivenInitiator{

	
	boolean invokeFlowCalled = false;
	List<Event> invokeFlowArgument = null;
	IkasanExceptionAction handleActionArgument = null;
	boolean handleActionCalled = false;
	
	/**
	 * @param name
	 * @param moduleName
	 * @param flow
	 * @param exceptionHandler
	 */
	public MockAbstractInvocationDrivenInitiator(String name,
			String moduleName, Flow flow,
			IkasanExceptionHandler exceptionHandler, EventProvider eventProvider) {
		super(name, moduleName, flow, exceptionHandler,eventProvider);
	}

	@Override
	protected void handleAction(IkasanExceptionAction action) {
		handleActionCalled = true;
		handleActionArgument = action;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInvocationDrivenInitiator#invokeFlow()
	 */
	@Override
	protected void invokeFlow(List<Event>events) {
		invokeFlowCalled = true;
		invokeFlowArgument = events;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInitiator#cancelRetryCycle()
	 */
	@Override
	protected void cancelRetryCycle() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInitiator#completeRetryCycle()
	 */
	@Override
	protected void completeRetryCycle() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInitiator#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInitiator#startInitiator()
	 */
	@Override
	protected void startInitiator() throws InitiatorOperationException {
		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInitiator#startRetryCycle(java.lang.Integer, long)
	 */
	@Override
	protected void startRetryCycle(Integer maxAttempts, long delay)
			throws InitiatorOperationException {
		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInitiator#stopInitiator()
	 */
	@Override
	protected void stopInitiator() throws InitiatorOperationException {
		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.Initiator#getType()
	 */
	public String getType() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.Initiator#isRecovering()
	 */
	public boolean isRecovering() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.Initiator#isRunning()
	 */
	public boolean isRunning() {
		return false;
	}
	
	public boolean isInvokeFlowCalled(){
		return invokeFlowCalled;
	}
	public boolean isHandleActionCalled(){
		return handleActionCalled;
	}
	
	public List<Event> getInvokeFlowArgument(){
		return invokeFlowArgument;
	}
	
	public IkasanExceptionAction getHandActionArgument(){
		return handleActionArgument;
	}
	
}