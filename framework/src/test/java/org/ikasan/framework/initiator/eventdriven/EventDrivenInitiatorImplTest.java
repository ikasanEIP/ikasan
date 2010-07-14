/*
 * $Id
 * $URL$
 *
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.framework.initiator.eventdriven;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.RetryAction;
import org.ikasan.framework.exception.StopAction;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.flow.invoker.FlowInvocationContext;
import org.ikasan.framework.initiator.AbortTransactionException;
import org.ikasan.framework.initiator.InitiatorState;
import org.ikasan.framework.initiator.eventdriven.EventDrivenInitiatorImpl;
import org.ikasan.framework.initiator.messagedriven.JmsMessageDrivenInitiatorImpl;
import org.ikasan.framework.monitor.MonitorListener;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for <code>EventDrivenInitiatorImpl</code>
 * 
 * @author Ikasan Development Team
 * 
 * TODO: Get rid of Thread.sleep(x) and test thead spawning properly!
 */
public class EventDrivenInitiatorImplTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** mocked endpoint manager */
    final MessageEndpointManager messageEndpointManager = mockery.mock(MessageEndpointManager.class);

    /** mocked flow context */
    final FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class);
    
    /** mocked flow */
    final Flow flow = mockery.mock(Flow.class);

    /** mocked monitor listener */
    final MonitorListener monitorListener = mockery.mock(MonitorListener.class);

    /** module name */
    final String moduleName = "moduleName";

    /** initiator name */
    final String initiatorName = "initiatorName";

    /** mocked event */
    final Event event = mockery.mock(Event.class);

    /** mock exceptionHandler */
    private IkasanExceptionHandler exceptionHandler = mockery.mock(IkasanExceptionHandler.class);

    /** Ikasan commit and stop action */
    private IkasanExceptionAction rollForwardStopAction = StopAction.instance();

    /** Ikasan rollback and stop action */
    private IkasanExceptionAction rollbackStopAction = StopAction.instance();

    private long retryDelay = 1000l;

    private int maxRetryAttempts = 3;

    /** Ikasan rollback and retry action */
    private IkasanExceptionAction retryAction = new RetryAction( retryDelay, maxRetryAttempts);

    /** Ikasan rollback and retry (0 times!) action */
    private IkasanExceptionAction retryZeroAction = new RetryAction( retryDelay, 0);

    /** class on test */
    private EventDrivenInitiatorImpl eventDrivenInitiator ;
    
    @Test(expected = IllegalArgumentException.class)
    public void test_constructor()
    {
        new EventDrivenInitiatorImpl(null, null, null, null);
    }
    
    @Before
    public void setup()
    {
        eventDrivenInitiator = new EventDrivenInitiatorImpl(moduleName, initiatorName, flow, exceptionHandler);
        eventDrivenInitiator.setMessageEndpointManager(messageEndpointManager);
        eventDrivenInitiator.addListener(monitorListener);
    }
    
    @Test
    public void testStart()
    {
        final Sequence sequence = mockery.sequence("invocationSequence");
        mockery.checking(new Expectations()
        {
            {
                one(messageEndpointManager).start();
                inSequence(sequence);
                // attempts to notify listeners of its state
                // lets assume the underlying container actually stops
                one(messageEndpointManager).isRunning();
                will(returnValue(true));
                inSequence(sequence);
                one(monitorListener).notify(with(equal("running")));
                inSequence(sequence);
            }
        });
        
        eventDrivenInitiator.start();
        mockery.assertIsSatisfied();
    }

    @Test
    public void testStop()
    {
        final Sequence sequence = mockery.sequence("invocationSequence");
        mockery.checking(new Expectations()
        {
            {
                one(messageEndpointManager).stop();
                inSequence(sequence);
                // attempts to notify listeners of its state
                // lets assume the underlying container actually stops
                one(messageEndpointManager).isRunning();
                will(returnValue(false));
                inSequence(sequence);
                one(monitorListener).notify(with(equal("stopped")));
                inSequence(sequence);
            }
        });
        
        eventDrivenInitiator.stop();
        mockery.assertIsSatisfied();
    }

    @Test
    public void testGetState()
    {
        // if underlying message endpoint is running, state should be
        // running
        mockery.checking(new Expectations()
        {
            {
                one(messageEndpointManager).isRunning();
                will(returnValue(true));

                one(messageEndpointManager).isRunning();
                will(returnValue(false));
            }
        });
        Assert.assertEquals(InitiatorState.RUNNING, eventDrivenInitiator.getState());
        Assert.assertEquals(InitiatorState.STOPPED, eventDrivenInitiator.getState());
        mockery.assertIsSatisfied();
    }

    @Test
    public void testGetName()
    {
        Assert.assertEquals(initiatorName, eventDrivenInitiator.getName());
    }

    /**
     * Test onEvent with happy flow invocation
     */
    @Test
    public void testOnEventWithFlowInvocation()
    {
        mockery.checking(new Expectations()
        {
            {
                one(event).getId();
                will(returnValue("eventId"));
                one(flow).invoke((FlowInvocationContext) with(a(FlowInvocationContext.class)), (Event)with(equal(event)));
                will(returnValue(null));
            }
        });
        eventDrivenInitiator.onEvent(event);
        mockery.assertIsSatisfied();
    }

    /**
     * Test onEvent with flow returning a stop action but transaction rolling forward
     * @throws InterruptedException 
     */
    @Test(expected = AbortTransactionException.class)
    public void testOnEvent_stopsTheInitiatorInResponseToFlowReturningStopAction() throws InterruptedException
    {
        final Throwable throwable = new RuntimeException();
        
        mockery.checking(new Expectations()
        {
            {
                one(event).getId();
                will(returnValue("eventId"));
                one(flow).invoke((FlowInvocationContext) with(a(FlowInvocationContext.class)), (Event)with(equal(event)));
                will(throwException(throwable));
                
                one(exceptionHandler).handleThrowable(with(any(String.class)), (Throwable)with(equal(throwable)));
                will(returnValue(rollbackStopAction));

                one(messageEndpointManager).stop();
                one(monitorListener).notify(with(equal("stoppedInError")));
            }
        });
        eventDrivenInitiator.onEvent(event);

        Thread.sleep(1000); // allow halt thread to complete
        mockery.assertIsSatisfied();
    }


    /**
     * Test onEvent with flow returning a stop action but transaction rolling back
     * @throws InterruptedException 
     */
    @Test
    public void testOnEvent_stopsTheInitiatorAndThrowsRuntimeExceptionInResponseToFlowReturningStopRollbackAction() throws InterruptedException
    {
        final Throwable throwable = new RuntimeException();

        mockery.checking(new Expectations()
        {
            {
                one(event).getId();
                will(returnValue("eventId"));
                one(flow).invoke((FlowInvocationContext) with(a(FlowInvocationContext.class)), (Event)with(equal(event)));
                will(throwException(throwable));
                
                one(exceptionHandler).handleThrowable(with(any(String.class)), (Throwable)with(equal(throwable)));
                will(returnValue(rollbackStopAction));

                will(returnValue(rollbackStopAction));
                one(messageEndpointManager).stop();
                one(monitorListener).notify(with(equal("stoppedInError")));
            }
        });
        RuntimeException thrownException = null;
        try
        {
            eventDrivenInitiator.onEvent(event);
            fail("Exception should have been thrown to force the rollback");
        }
        catch (RuntimeException runtimeException)
        {
            thrownException = runtimeException;
        }
        if (thrownException != null)
        {
            Assert.assertEquals(EventDrivenInitiatorImpl.EXCEPTION_ACTION_IMPLIED_ROLLBACK, thrownException.getMessage());
        }
        else
        {
            fail("thrownException was null.");
        }

        Thread.sleep(1000); // allow halt thread to complete
        mockery.assertIsSatisfied();
    }

    /**
     * Test that the initiator suspends the underlying endpoint for the
     * configured delay period for a retry action
     * @throws InterruptedException 
     * 
     */
    // TODO - need to resolve thread race condition in this test @Test
    public void testOnEvent_suspendsTheEndpointForARetryAction() throws InterruptedException
    {
        final Sequence sequence = mockery.sequence("invocationSequence");

        mockery.checking(new Expectations()
        {
            {
                // invoke results in retry which firstly stops the endpoint
                one(event).getId();
                will(returnValue("eventId"));
                one(flow).invoke((FlowInvocationContext) with(a(FlowInvocationContext.class)), (Event)with(equal(event)));
                will(returnValue(retryAction));
                inSequence(sequence);
                one(messageEndpointManager).stop();
                inSequence(sequence);
                one(monitorListener).notify(with(equal("runningInRecovery")));
                inSequence(sequence);

                // then restarts the endpoint
                one(messageEndpointManager).start();
                inSequence(sequence);
                allowing(messageEndpointManager).isRunning();
                will(returnValue(true));
                inSequence(sequence);
            }
        });

        RuntimeException thrownException = null;
        try
        {
            eventDrivenInitiator.onEvent(event);
            fail("Exception should have been thrown to force the rollback");
        }
        catch (RuntimeException runtimeException)
        {
            thrownException = runtimeException;
        }
        if (thrownException != null)
        {
            Assert.assertEquals(EventDrivenInitiatorImpl.EXCEPTION_ACTION_IMPLIED_ROLLBACK, thrownException.getMessage());
        }
        else
        {
            fail("thrownException was null.");
        }
        //Thread.sleep(5);
        mockery.assertIsSatisfied(); 
    }

    /**
     * Test that the initiator rollback and stops after exceeding max attempts for a retry action
     * @throws InterruptedException 
     * 
     */
    @Test
    public void testOnEvent_stopsTheEndpointAfterMaxAttemptsExceeded() throws InterruptedException
    {
        final Throwable throwable = new RuntimeException();
        final Sequence sequence = mockery.sequence("invocationSequence");

        mockery.checking(new Expectations()
        {
            {
                // invoke results in retry which firstly stops the endpoint
                //expectations of main execution thread
                one(event).getId();
                will(returnValue("eventId"));
                one(flow).invoke((FlowInvocationContext) with(a(FlowInvocationContext.class)), (Event)with(equal(event)));
                will(throwException(throwable));
                
                one(exceptionHandler).handleThrowable(with(any(String.class)), (Throwable)with(equal(throwable)));
                will(returnValue(retryZeroAction));
                inSequence(sequence);

                one(monitorListener).notify(with(equal("stoppedInError")));
                inSequence(sequence);

                //expectations of halt thread
                one(messageEndpointManager).stop();
                inSequence(sequence);
            }
        });

        RuntimeException thrownException = null;
        try
        {
            eventDrivenInitiator.onEvent(event);
            fail("Exception should have been thrown to force the rollback");
        }
        catch (RuntimeException runtimeException)
        {
            thrownException = runtimeException;
        }
        if (thrownException != null)
        {
            Assert.assertEquals(EventDrivenInitiatorImpl.EXCEPTION_ACTION_IMPLIED_ROLLBACK, thrownException.getMessage());
        }
        else
        {
            fail("thrownException was null.");
        }
        Thread.sleep(1000); // allow halt thread to complete
        mockery.assertIsSatisfied(); 
    }

}
