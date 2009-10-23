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
package org.ikasan.framework.initiator.messagedriven;

import static org.junit.Assert.fail;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.event.serialisation.EventDeserialisationException;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.RetryAction;
import org.ikasan.framework.exception.StopAction;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.flow.invoker.FlowInvocationContext;
import org.ikasan.framework.initiator.AbortTransactionException;
import org.ikasan.framework.initiator.AbstractInitiator;
import org.ikasan.framework.initiator.InitiatorState;
import org.ikasan.framework.monitor.MonitorListener;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for <code>JmsMessageDrivenInitiatorImpl</code>
 * 
 * @author Ikasan Development Team
 * 
 */
public class JmsMessageDrivenInitiatorImplTest
{
    private static Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private final MessageListenerContainer messageListenerContainer = mockery.mock(MessageListenerContainer.class);

    private final Flow flow = mockery.mock(Flow.class);

    private final MonitorListener monitorListener = mockery.mock(MonitorListener.class);

    private final String initiatorName = "initiatorName";

    private Event eventFromTextMessage = mockery.mock(Event.class);

    private TextMessage textMessage = mockTextMessage();

    private IkasanExceptionAction rollbackStopAction = StopAction.instance();

    private long retryDelay = 5l;

    private int maxRetryAttempts = 3;

    private IkasanExceptionAction retryAction = new RetryAction( retryDelay, maxRetryAttempts);

    private IkasanExceptionAction retryZeroAction = new RetryAction( retryDelay, 0);

    private IkasanExceptionHandler exceptionHandler = mockery.mock(IkasanExceptionHandler.class);
    
    
    /**
     * System under test
     */
    private StubJmsMessageDrivenInitiatorImpl stubJmsMessageDrivenInitiatorImpl = null;

    public JmsMessageDrivenInitiatorImplTest()
    {


        stubJmsMessageDrivenInitiatorImpl = new StubJmsMessageDrivenInitiatorImpl("moduleName", initiatorName, flow, exceptionHandler);
        mockery.checking(new Expectations()
        {
            {
                one(messageListenerContainer).setListenerSetupExceptionListener(stubJmsMessageDrivenInitiatorImpl);
                
            }
        });
        
        stubJmsMessageDrivenInitiatorImpl.setMessageListenerContainer(messageListenerContainer);
        stubJmsMessageDrivenInitiatorImpl.addListener(monitorListener);
    }

    @Test
    public void testStop()
    {
        final Sequence sequence = mockery.sequence("invocationSequence");
        mockery.checking(new Expectations()
        {
            {
                one(messageListenerContainer).stop();
                inSequence(sequence);
                // attempts to notify listeners of its state
                // lets assume the underlying container actually stops
                one(messageListenerContainer).isRunning();
                will(returnValue(false));
                inSequence(sequence);
                one(monitorListener).notify(with(equal("stopped")));
                inSequence(sequence);
            }
        });
        stubJmsMessageDrivenInitiatorImpl.stop();
        mockery.assertIsSatisfied();
    }

    @Test
    public void testStart()
    {
        final Sequence sequence = mockery.sequence("invocationSequence");
        mockery.checking(new Expectations()
        {
            {
                one(messageListenerContainer).start();
                inSequence(sequence);
                // attempts to notify listeners of its state
                // lets assume the underlying container actually starts
                one(messageListenerContainer).isRunning();
                will(returnValue(true));
                inSequence(sequence);
                one(monitorListener).notify(with(equal("running")));
                inSequence(sequence);
            }
        });
        stubJmsMessageDrivenInitiatorImpl.start();
        mockery.assertIsSatisfied();
    }

    @Test
    public void testGetState()
    {
        // if underlying message listener container is running, state should be
        // running
        mockery.checking(new Expectations()
        {
            {
                one(messageListenerContainer).isRunning();
                will(returnValue(true));
            }
        });
        Assert.assertEquals(InitiatorState.RUNNING, stubJmsMessageDrivenInitiatorImpl.getState());
        mockery.assertIsSatisfied();
        // GRRR cannot unit test recovering, stopped or error!
    }

    @Test
    public void testGetName()
    {
        Assert.assertEquals(initiatorName, stubJmsMessageDrivenInitiatorImpl.getName());
    }

    /**
     * Test that the result of a handle[type]Message method is used to invoke
     * the flow
     */
    @Test
    public void testOnMessage_withSupportedMessageTypeInvokesFlowWithEventFromHandleMethod()
    {
        // lets pretend that our subclass handles TextMessages (hence the
        // overriding of handleTextMessage)
        mockery.checking(new Expectations()
        {
            {	
            	one(eventFromTextMessage).getId();will(returnValue("eventId"));
            	
            	one(flow).invoke((FlowInvocationContext) with(a(FlowInvocationContext.class)), (Event)with(equal(eventFromTextMessage)));
                will(returnValue(null));
            }
        });
        stubJmsMessageDrivenInitiatorImpl.onMessage(textMessage);
        mockery.assertIsSatisfied();
    }

    private TextMessage mockTextMessage()
    {
        final TextMessage result = mockery.mock(TextMessage.class);
        try
        {
            mockery.checking(new Expectations()
            {
                {
                    allowing(result).getJMSMessageID();
                    will(returnValue("messageId"));
                }
            });
        }
        catch (JMSException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Test that the initiator stops if the flow returns a stop action
     */
    @Test
    public void testOnMessage_stopsTheInitiatorInResponseToFlowReturningStopAction()
    {
    	final Throwable throwable = new RuntimeException();
    	
    	
        mockery.checking(new Expectations()
        {
            {
            	one(eventFromTextMessage).getId();will(returnValue("eventId"));
            	
            	one(flow).invoke((FlowInvocationContext) with(a(FlowInvocationContext.class)), (Event)with(equal(eventFromTextMessage)));
                will(throwException(throwable));
            	
                one(exceptionHandler).handleThrowable(with(any(String.class)), (Throwable)with(equal(throwable)));                
            	will(returnValue(rollbackStopAction));
                
                
                
                
                one(messageListenerContainer).stop();
            }
        });
        monitorExpectsStoppedInError();
        AbortTransactionException abortTransactionException = null;
        try{
        	stubJmsMessageDrivenInitiatorImpl.onMessage(textMessage);
        	fail();
        } catch(AbortTransactionException exception){
        	abortTransactionException = exception;
        }
        Assert.assertNotNull(abortTransactionException);
        
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test that the initiator stops if an EventDeserialisationException is thrown by the underlying derserialisaion implementation
     */
    @Test
    public void testOnMessage_stopsTheInitiatorForAnEventDeserialisationException()
    {
    	
    	stubJmsMessageDrivenInitiatorImpl.setThrowEventDeserialisationExceptionWhenHandlingMessage(true);
    	
        mockery.checking(new Expectations()
        {
            {
                one(messageListenerContainer).stop();
            }
        });
        monitorExpectsStoppedInError();
        AbortTransactionException abortTransactionException = null;
        try{
        	stubJmsMessageDrivenInitiatorImpl.onMessage(textMessage);
        	fail();
        } catch(AbortTransactionException exception){
        	abortTransactionException = exception;
        }
        Assert.assertNotNull(abortTransactionException);
        
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test that the initiator stops if an UnsupportedOperationException is thrown
     */
    @Test
    public void testOnMessage_stopsTheInitiatorForAnUnsupportedOperationException()
    {
    	
    	stubJmsMessageDrivenInitiatorImpl.setThrowUnsupportedOperationExceptionWhenHandlingMessage(true);
    	
        mockery.checking(new Expectations()
        {
            {
                one(messageListenerContainer).stop();
            }
        });
        monitorExpectsStoppedInError();
        AbortTransactionException abortTransactionException = null;
        try{
        	stubJmsMessageDrivenInitiatorImpl.onMessage(textMessage);
        	fail();
        } catch(AbortTransactionException exception){
        	abortTransactionException = exception;
        }
        Assert.assertNotNull(abortTransactionException);
        
        mockery.assertIsSatisfied();
    }

    private void monitorExpectsStoppedInError()
    {
        mockery.checking(new Expectations()
        {
            {
                // notifying monitor listeners - stopped in error ....
                one(messageListenerContainer).isRunning();
                will(returnValue(false));
                one(monitorListener).notify(with(equal("stoppedInError")));
            }
        });
    }

    /**
     * Test that the initiator stops and throws a RuntimeException if the flow
     * returns a stop and rollback action
     */
    @Test
    public void testOnMessage_stopsTheInitiatorAndThrowsRuntimeExceptionInResponseToFlowReturningStopRollbackAction()
    {
    	
    	final Throwable throwable = new RuntimeException();
        mockery.checking(new Expectations()
        {
            {
            	one(flow).invoke((FlowInvocationContext) with(a(FlowInvocationContext.class)), (Event) with(equal(eventFromTextMessage)));
                will(throwException(throwable));
                
                one(eventFromTextMessage).getId();will(returnValue("eventId"));
            	
                one(exceptionHandler).handleThrowable(with(any(String.class)), (Throwable)with(equal(throwable)));                
                will(returnValue(rollbackStopAction));
            	
            	
                one(messageListenerContainer).stop();
            }
        });
        monitorExpectsStoppedInError();
        RuntimeException thrownException = null;
        try
        {
            stubJmsMessageDrivenInitiatorImpl.onMessage(textMessage);
            fail("Exception should have been thrown to force the rollback");
        }
        catch (RuntimeException runtimeException)
        {
            thrownException = runtimeException;
        }
        if (thrownException != null)
        {
            Assert.assertEquals(JmsMessageDrivenInitiatorImpl.EXCEPTION_ACTION_IMPLIED_ROLLBACK, thrownException.getMessage());
        }
        else
        {
            fail("thrownException was null.");
        }
        mockery.assertIsSatisfied();
    }

    /**
     * Test that the initiator suspends the underlying container for the
     * configured delay period for a retry action
     * 
     */
    @Test
    public void testOnMessage_suspendsTheContainerForARetryAction()
    {
    	final Throwable throwable = new RuntimeException();
    	
        final Sequence sequence = mockery.sequence("invocationSequence");
        // expectations for the suspension
        mockery.checking(new Expectations()
        {
            {
            	//flow invocation fails
            	one(flow).invoke((FlowInvocationContext) with(a(FlowInvocationContext.class)), (Event) with(equal(eventFromTextMessage)));
                inSequence(sequence);
                will(throwException(throwable));
                
                //exceptionhandler says RETRY
                one(exceptionHandler).handleThrowable(with(any(String.class)), (Throwable) with(equal(throwable)));
                inSequence(sequence);
                will(returnValue(retryAction));
                
                allowing(eventFromTextMessage).getId();will(returnValue("eventId"));
                
                // suspends
                one(messageListenerContainer).stop();
                // inSequence(sequence);
                one(messageListenerContainer).isRunning();
                will(returnValue(false));
                // inSequence(sequence);
                one(monitorListener).notify(with(equal("runningInRecovery")));
            }
        });
        // Assert.assertTrue("retries cache should be empty",
        // retries.isEmpty());
        RuntimeException thrownException = null;
        try
        {
            stubJmsMessageDrivenInitiatorImpl.onMessage(textMessage);
            fail("Exception should have been thrown to force the rollback");
        }
        catch (RuntimeException runtimeException)
        {
            thrownException = runtimeException;
        }
        if (thrownException != null)
        {
            Assert.assertEquals(JmsMessageDrivenInitiatorImpl.EXCEPTION_ACTION_IMPLIED_ROLLBACK, thrownException.getMessage());
        }
        else
        {
            fail("thrownException was null.");
        }
        
        mockery.assertIsSatisfied(); 
        
        // Assert.assertTrue("we should now be in recovering mode",
        // stubJmsMessageDrivenInitiatorImpl.getState().isRecovering());
        
        // expectations for the resumption
        
        mockery.checking(new Expectations()
        {
            {
                // reawakens
                one(messageListenerContainer).start();
                inSequence(sequence);
                allowing(messageListenerContainer).isRunning();
                will(returnValue(true));
            }
        });
        /*
         * go to sleep for a while, then come back hopefully after the container
         * has been woken
         * 
         * Note that this sleep period needs to be substantially longer than the
         * retryDelay to ensure that there is time to reawaken the container.
         * However it should never be so long that it starts to drag on the
         * performance of this test running
         * 
         * Note though, that if you see intermittent failures of this test, it
         * could be a timing issue
         */
        try
        {
            Thread.sleep(10 * retryDelay);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        mockery.assertIsSatisfied(); // see note above

        // now lets retry the message, but successfully this time
        mockery.checking(new Expectations()
        {
            {
            	//one(eventFromTextMessage).getId();will(returnValue("eventId"));
            	one(flow).invoke((FlowInvocationContext) with(a(FlowInvocationContext.class)), (Event) with(equal(eventFromTextMessage)));
                inSequence(sequence);
                will(returnValue(null));
                one(monitorListener).notify(with(equal("running")));
            }
        });
        stubJmsMessageDrivenInitiatorImpl.onMessage(textMessage);
        mockery.assertIsSatisfied();
        Assert.assertFalse("we should no longer be in recovering mode", stubJmsMessageDrivenInitiatorImpl.getState().isRecovering());

    }

    /**
     * Test that the initiator rollsback and stops in error if the maxAttempts
     * value is exceeded for a given retryAction
     */
    @Test
    public void testOnMessage_rollsbackAndStopsInErrorWhenExceedingMaxAttemptsOnRetry()
    {
        final Sequence sequence = mockery.sequence("invocationSequence");
        
        final Throwable throwable = new RuntimeException();
        mockery.checking(new Expectations()
        {
            {
            	//flow invocation fails
            	one(flow).invoke((FlowInvocationContext)with(a(FlowInvocationContext.class)), (Event)with(equal(eventFromTextMessage)));
                inSequence(sequence);
                will(throwException(throwable));
                
                allowing(eventFromTextMessage).getId();will(returnValue("eventId"));
                
                //exception handler says RETRY maximum of zero times (slightly nonsensical, but for arguments sake!)
                one(exceptionHandler).handleThrowable(with(any(String.class)),  (Throwable)with(equal(throwable)));
                inSequence(sequence);
                will(returnValue(retryZeroAction));
                
                
                // which of course results in a stop
                one(messageListenerContainer).stop();
                inSequence(sequence);
                monitorExpectsStoppedInError();
            }
        });

        
        
        RuntimeException thrownException = null;
        try
        {
            stubJmsMessageDrivenInitiatorImpl.onMessage(textMessage);
            fail("Exception should have been thrown to force the rollback");
        }
        catch (RuntimeException runtimeException)
        {
            thrownException = runtimeException;
        }
        if (thrownException != null)
        {
            
            Assert.assertEquals(AbstractInitiator.EXCEPTION_ACTION_IMPLIED_ROLLBACK, thrownException.getMessage());
        }
        else
        {
            fail("thrownException was null.");
        }

        mockery.assertIsSatisfied();
    }

    final class StubJmsMessageDrivenInitiatorImpl extends JmsMessageDrivenInitiatorImpl
    {
        private Logger logger = Logger.getLogger(StubJmsMessageDrivenInitiatorImpl.class);
        
        private boolean throwEventDeserialisationExceptionWhenHandlingMessage = false;
        private boolean throwUnsupportedOperationExceptionWhenHandlingMessage = false;
        
        public void setThrowEventDeserialisationExceptionWhenHandlingMessage(
				boolean throwEventDeserialisationExceptionWhenHandlingMessage) {
			this.throwEventDeserialisationExceptionWhenHandlingMessage = throwEventDeserialisationExceptionWhenHandlingMessage;
		}

		public void setThrowUnsupportedOperationExceptionWhenHandlingMessage(
				boolean throwUnsupportedOperationExceptionWhenHandlingMessage) {
			this.throwUnsupportedOperationExceptionWhenHandlingMessage = throwUnsupportedOperationExceptionWhenHandlingMessage;
			
		}

		public StubJmsMessageDrivenInitiatorImpl(String moduleName, String name, Flow flow, IkasanExceptionHandler exceptionHandler)
        {
            super(moduleName, name, flow, exceptionHandler);
        }

        @Override
        protected Event handleTextMessage(TextMessage message) throws JMSException, EventDeserialisationException
        {
        	if (throwEventDeserialisationExceptionWhenHandlingMessage){
        		throw new EventDeserialisationException("");
        	} else if(throwUnsupportedOperationExceptionWhenHandlingMessage){
        		throw new UnsupportedOperationException();
        	}
        	
            return eventFromTextMessage;
        }

        @Override
        protected Logger getLogger()
        {
            return logger;
        }
        

    }
}
