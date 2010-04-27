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
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.IkasanExceptionActionImpl;
import org.ikasan.framework.exception.IkasanExceptionActionType;
import org.ikasan.framework.flow.Flow;
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

    /** Ikasan commit and stop action */
    private IkasanExceptionAction rollForwardStopAction = new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLFORWARD_STOP);

    /** Ikasan rollback and stop action */
    private IkasanExceptionAction rollbackStopAction = new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_STOP);

    private long retryDelay = 5l;

    private int maxRetryAttempts = 3;

    /** Ikasan rollback and retry action */
    private IkasanExceptionAction retryAction = new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_RETRY, retryDelay, maxRetryAttempts);

    /** Ikasan rollback and retry (0 times!) action */
    private IkasanExceptionAction retryZeroAction = new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_RETRY, retryDelay, 0);

    /** class on test */
    private EventDrivenInitiatorImpl eventDrivenInitiator ;
    
    @Test(expected = IllegalArgumentException.class)
    public void test_constructor()
    {
        new EventDrivenInitiatorImpl(null, null, null);
    }
    
    @Before
    public void setup()
    {
        eventDrivenInitiator = new EventDrivenInitiatorImpl(moduleName, initiatorName, flow);
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
                one(flow).invoke(event);
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
    @Test
    public void testOnEvent_stopsTheInitiatorInResponseToFlowReturningStopAction() throws InterruptedException
    {
        mockery.checking(new Expectations()
        {
            {
                one(flow).invoke(event);
                will(returnValue(rollForwardStopAction));
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
        mockery.checking(new Expectations()
        {
            {
                one(flow).invoke(event);
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
            Assert.assertEquals(JmsMessageDrivenInitiatorImpl.EXCEPTION_ACTION_IMPLIED_ROLLBACK, thrownException.getMessage());
        }
        else
        {
            fail("thrownException was null.");
        }

        Thread.sleep(1000); // allow halt thread to complete
        mockery.assertIsSatisfied();
    }

//    /**
//     * Test that the initiator suspends the underlying container for the
//     * configured delay period for a retry action
//     * @throws InterruptedException 
//     * 
//     */
//    @Test
//    public void testOnEvent_suspendsTheContainerForARetryAction() throws InterruptedException
//    {
//        final Sequence sequence = mockery.sequence("invocationSequence");
//        // expectations for the suspension
//        mockery.checking(new Expectations()
//        {
//            {
//                one(flow).invoke(event);
//                inSequence(sequence);
//                will(returnValue(retryAction));
//
//                one(messageEndpointManager).stop();
//
//                one(messageEndpointManager).isRunning();
//                will(returnValue(false));
//
//                one(monitorListener).notify(with(equal("runningInRecovery")));
//            }
//        });
//
//        RuntimeException thrownException = null;
//        try
//        {
//            eventDrivenInitiator.onEvent(event);
//            fail("Exception should have been thrown to force the rollback");
//        }
//        catch (RuntimeException runtimeException)
//        {
//            thrownException = runtimeException;
//        }
//        if (thrownException != null)
//        {
//            Assert.assertEquals(JmsMessageDrivenInitiatorImpl.EXCEPTION_ACTION_IMPLIED_ROLLBACK, thrownException.getMessage());
//        }
//        else
//        {
//            fail("thrownException was null.");
//        }
//        
//        Thread.sleep(2000); // allow recovery thread to suspend endpoint
//        mockery.assertIsSatisfied(); 
//        
//        // Assert.assertTrue("we should now be in recovering mode",
//        
//        // expectations for the resumption
//        
//        mockery.checking(new Expectations()
//        {
//            {
//                // reawakens
//                one(messageEndpointManager).start();
//                inSequence(sequence);
//                allowing(messageEndpointManager).isRunning();
//                will(returnValue(true));
//            }
//        });
//        
//        /*
//         * go to sleep for a while, then come back hopefully after the container
//         * has been woken
//         * 
//         * Note that this sleep period needs to be substantially longer than the
//         * retryDelay to ensure that there is time to reawaken the container.
//         * However it should never be so long that it starts to drag on the
//         * performance of this test running
//         * 
//         * Note though, that if you see intermittent failures of this test, it
//         * could be a timing issue
//         */
//        try
//        {
//            Thread.sleep(10 * retryDelay);
//        }
//        catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }
//        mockery.assertIsSatisfied(); // see note above
//
//        // now lets retry the message, but successfully this time
//        mockery.checking(new Expectations()
//        {
//            {
//                one(flow).invoke(event);
//                inSequence(sequence);
//                will(returnValue(null));
//                one(monitorListener).notify(with(equal("running")));
//            }
//        });
//        eventDrivenInitiator.onEvent(event);
//        mockery.assertIsSatisfied();
//        Assert.assertFalse("we should no longer be in recovering mode", eventDrivenInitiator.getState().isRecovering());
//
//    }

//    /**
//     * Test that the initiator rollsback and stops in error if the maxAttempts
//     * value is exceeded for a given retryAction
//     */
//    @Test
//    public void testOnMessage_rollsbackAndStopsInErrorWhenExceedingMaxAttemptsOnRetry()
//    {
//        final Sequence sequence = mockery.sequence("invocationSequence");
//        mockery.checking(new Expectations()
//        {
//            {
//                one(flow).invoke(eventFromTextMessage);
//                inSequence(sequence);
//                will(returnValue(retryZeroAction));
//                // stops
//                one(messageListenerContainer).stop();
//                inSequence(sequence);
//                monitorExpectsStoppedInError();
//            }
//        });
//
//        
//        
//        RuntimeException thrownException = null;
//        try
//        {
//            stubJmsMessageDrivenInitiatorImpl.onMessage(textMessage);
//            fail("Exception should have been thrown to force the rollback");
//        }
//        catch (RuntimeException runtimeException)
//        {
//            thrownException = runtimeException;
//        }
//        if (thrownException != null)
//        {
//            
//            Assert.assertEquals(AbstractInitiator.EXCEPTION_ACTION_IMPLIED_ROLLBACK, thrownException.getMessage());
//        }
//        else
//        {
//            fail("thrownException was null.");
//        }
//
//        mockery.assertIsSatisfied();
//    }

}
