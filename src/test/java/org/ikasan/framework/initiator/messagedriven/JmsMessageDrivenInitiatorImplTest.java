 /* 
 * $Id: JmsMessageDrivenInitiatorImplTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/initiator/messagedriven/JmsMessageDrivenInitiatorImplTest.java $
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
package org.ikasan.framework.initiator.messagedriven;

import static org.junit.Assert.fail;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.serialisation.EventSerialisationException;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.IkasanExceptionActionImpl;
import org.ikasan.framework.exception.IkasanExceptionActionType;
import org.ikasan.framework.flow.Flow;
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

    private IkasanExceptionAction rollForwardStopAction = new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLFORWARD_STOP);

    private IkasanExceptionAction rollbackStopAction = new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_STOP);

    private long retryDelay = 5l;

    private int maxRetryAttempts = 3;

    private IkasanExceptionAction retryAction = new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_RETRY, retryDelay, maxRetryAttempts);

    private IkasanExceptionAction retryZeroAction = new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_RETRY, retryDelay, 0);

    /**
     * System under test
     */
    private StubJmsMessageDrivenInitiatorImpl stubJmsMessageDrivenInitiatorImpl = null;

    public JmsMessageDrivenInitiatorImplTest()
    {
        stubJmsMessageDrivenInitiatorImpl = new StubJmsMessageDrivenInitiatorImpl("moduleName", initiatorName, flow);
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
                one(flow).invoke(eventFromTextMessage);
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
        mockery.checking(new Expectations()
        {
            {
                one(flow).invoke(eventFromTextMessage);
                will(returnValue(rollForwardStopAction));
                one(messageListenerContainer).stop();
            }
        });
        monitorExpectsStoppedInError();
        stubJmsMessageDrivenInitiatorImpl.onMessage(textMessage);
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
        mockery.checking(new Expectations()
        {
            {
                one(flow).invoke(eventFromTextMessage);
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
        final Sequence sequence = mockery.sequence("invocationSequence");
        // expectations for the suspension
        mockery.checking(new Expectations()
        {
            {
                one(flow).invoke(eventFromTextMessage);
                inSequence(sequence);
                will(returnValue(retryAction));
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
                inSequence(sequence);
                allowing(monitorListener).notify(with(equal("running")));
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
                one(flow).invoke(eventFromTextMessage);
                inSequence(sequence);
                will(returnValue(null));
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
        mockery.checking(new Expectations()
        {
            {
                one(flow).invoke(eventFromTextMessage);
                inSequence(sequence);
                will(returnValue(retryZeroAction));
                // stops
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
            Assert.assertEquals(JmsMessageDrivenInitiatorImpl.EXCEPTION_ACTION_IMPLIED_ROLLBACK, thrownException.getMessage());
        }
        else
        {
            fail("thrownException was null.");
        }

        mockery.assertIsSatisfied();
    }

    final class StubJmsMessageDrivenInitiatorImpl extends JmsMessageDrivenInitiatorImpl
    {
        public StubJmsMessageDrivenInitiatorImpl(String moduleName, String name, Flow flow)
        {
            super(moduleName, name, flow);
        }

        @Override
        protected Event handleTextMessage(TextMessage message) throws JMSException, EventSerialisationException
        {
            return eventFromTextMessage;
        }
    }
}
