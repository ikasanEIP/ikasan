/**
 * 
 */
package org.ikasan.framework.initiator;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.flow.Flow;
import org.junit.Test;

/**
 * @author duncro
 *
 */
public class AbstractInvocationDrivenInitiatorTest {

	/**
	 * Test method for {@link org.ikasan.framework.initiator.AbstractInvocationDrivenInitiator#invoke()}.
	 */
	@Test
	public void testInvoke_willDoNothingIfStopping() {
		MockAbstractInvocationDrivenInitiator initiator = new MockAbstractInvocationDrivenInitiator("name", "moduleName", null, null);
		initiator.stop();
		
		Assert.assertFalse("invokeFlow should never have been called on a new initiator", initiator.isInvokeFlowCalled());
		initiator.invoke();
		Assert.assertFalse("invokeFlow should not have been called on a stopping initiator when invoke is called", initiator.isInvokeFlowCalled());
	}
	
	@Test
	public void testInvoke_willCallInvokeFlowWithResultOfPriorCallToSourceEvents() {
		MockAbstractInvocationDrivenInitiator initiator = new MockAbstractInvocationDrivenInitiator("name", "moduleName", null, null);
		Assert.assertFalse("invokeFlow should never have been called on a new initiator", initiator.isInvokeFlowCalled());
		initiator.invoke();
		Assert.assertTrue("invokeFlow should have been called on a non-stopping initiator when invoke is called", initiator.isInvokeFlowCalled());

	}
	


}
class MockAbstractInvocationDrivenInitiator extends AbstractInvocationDrivenInitiator{

	
	boolean invokeFlowCalled = false;
	/**
	 * @param name
	 * @param moduleName
	 * @param flow
	 * @param exceptionHandler
	 */
	public MockAbstractInvocationDrivenInitiator(String name,
			String moduleName, Flow flow,
			IkasanExceptionHandler exceptionHandler) {
		super(name, moduleName, flow, exceptionHandler);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInvocationDrivenInitiator#invokeFlow()
	 */
	@Override
	protected void invokeFlow() {
		invokeFlowCalled = true;
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
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInitiator#startInitiator()
	 */
	@Override
	protected void startInitiator() throws InitiatorOperationException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInitiator#startRetryCycle(java.lang.Integer, long)
	 */
	@Override
	protected void startRetryCycle(Integer maxAttempts, long delay)
			throws InitiatorOperationException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.AbstractInitiator#stopInitiator()
	 */
	@Override
	protected void stopInitiator() throws InitiatorOperationException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.Initiator#getType()
	 */
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.Initiator#isRecovering()
	 */
	public boolean isRecovering() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.framework.initiator.Initiator#isRunning()
	 */
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isInvokeFlowCalled(){
		return invokeFlowCalled;
	}
	
}