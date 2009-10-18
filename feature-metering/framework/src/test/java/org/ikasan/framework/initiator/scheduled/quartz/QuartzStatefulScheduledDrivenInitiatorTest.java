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
package org.ikasan.framework.initiator.scheduled.quartz;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import junit.framework.Assert;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.event.service.EventProvider;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.IkasanExceptionActionImpl;
import org.ikasan.framework.exception.IkasanExceptionActionType;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.initiator.AbortTransactionException;
import org.ikasan.framework.monitor.MonitorListener;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

/**
 * This test class supports the <code>QuartzStatefulScheduledDrivenInitiator</code> class.
 * 
 * @author Ikasan Development Team
 */
public class QuartzStatefulScheduledDrivenInitiatorTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mock objects
     */
    final EventProvider eventProvider = classMockery
            .mock(EventProvider.class);
    final Event event = classMockery.mock(Event.class);
    final Flow flow = classMockery.mock(Flow.class);
    final IkasanExceptionHandler exceptionHandler = 
        classMockery.mock(IkasanExceptionHandler.class);
    final Scheduler scheduler = classMockery.mock(Scheduler.class);

    final MonitorListener monitorListener = classMockery.mock(MonitorListener.class);

    /**
     * Real objects
     */
    final IkasanExceptionAction rollbackRetryAction =
        new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_RETRY);
    final IkasanExceptionAction rollbackStopAction =
        new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_STOP);
    final IkasanExceptionAction rollforwardStopAction =
        new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLFORWARD_STOP);
    final IkasanExceptionAction continueAction =
        new IkasanExceptionActionImpl(IkasanExceptionActionType.CONTINUE);
    final IkasanExceptionAction skipAction =
        new IkasanExceptionActionImpl(IkasanExceptionActionType.SKIP_EVENT);
    final String initiatorName = "initiatorName";
    final String moduleName = "moduleName";




    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on a
     * failure due to initiator being in a 'STOPPED' state.
     * 
     * @throws SchedulerException
     */
    public void test_failed_InvokeDueToInitiatorNotBeingInAStartedState()
        throws SchedulerException
    {

        //
        // expectations due to stop() being called as part of the test set-up
        setStopInitiatorExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, moduleName,eventProvider, flow, exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // stop initiator and check state is stopped
        sdi.stop();
        assertTrue(sdi.getState().isStopped());

        // invoke initiator
        sdi.invoke();
    }


    
    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on a
     * successful initiator execute callback, but no returned event.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_successful_ExecuteWithNoEvent() 
        throws ResourceException
    {
        QuartzStatefulScheduledDrivenInitiator sdi = setupInitiator();
        
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // get events from event provider
                one(eventProvider).getEvents();
                will(returnValue(null));
            }
        });

        //expectations for handleNullAction
        setExpectationsForResume(false);

        // invoke initiator
        sdi.invoke();
    }

    
    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on a
     * successful initiator execute callback with returned event.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_successful_ExecuteWithEvent() 
        throws ResourceException
    {
        QuartzStatefulScheduledDrivenInitiator sdi = setupInitiator();
        
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // return 'null' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));
            }
        });

        // set common expectations
        this.setEventExpectations();
        
        //for a successful execution we will be handling a null action
        setExpectationsForResume(false);

        // invoke initiator
        sdi.invoke();
    }



    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on a
     * successful initiator execute callback with returned multiple events.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_successful_ExecuteWithMultipleEvents() 
        throws ResourceException
    {
        QuartzStatefulScheduledDrivenInitiator sdi = setupInitiator();       
        
        final int numOfEvents = 3;

        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // return 'null' ikasanExceptionAction from the flow invocation
                exactly(numOfEvents).of(flow).invoke(with(any(Event.class)));
                will(returnValue(null));
            }
        });

        
        // set common expectations
        this.setMultipleEventExpectations(numOfEvents);       
        
        //for a successful execution we will be handling a null action
        setExpectationsForResume(false);
        
        // invoke initiator
        sdi.invoke();
    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on a
     * flow invocation returning a 'STOP_ROLLBACK' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test(expected = org.ikasan.framework.initiator.AbortTransactionException.class)    
    public void test_successful_ExecuteWithReturnedStopRollbackAction() 
        throws ResourceException, SchedulerException
    {
        QuartzStatefulScheduledDrivenInitiator sdi = setupInitiator();
        
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // return 'rollforwardStop' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackStopAction));
            }
        });

        // set common expectations
        this.setEventExpectations();

        setExpectationsForHandleStopAction(false);

        // invoke initiator
        sdi.invoke();

        // check initiator is error
        assertTrue(sdi.isError());
    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on 
     * multiple (3) events with the second event causing a 'STOP_ROLLBACK' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test(expected = org.ikasan.framework.initiator.AbortTransactionException.class)    
    public void test_successful_ExecuteMultipleEventsWithReturnedStopRollbackAction() 
        throws ResourceException, SchedulerException
    {
        QuartzStatefulScheduledDrivenInitiator sdi = setupInitiator();       
        
        int numOfEvents = 3;
        
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // first event return 'null' action
                exactly(1).of(flow).invoke(with(any(Event.class)));
                will(returnValue(null));               
                
                // second event returns 'rollforwardStop' action
                exactly(1).of(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackStopAction));

            }
        });

        // set common expectations
        this.setMultipleEventExpectations(numOfEvents);

        setExpectationsForHandleStopAction(false);   

        // invoke initiator
        sdi.invoke();

        // check initiator status is error
        assertTrue(sdi.getState().isError());
    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on a
     * flow invocation returning a 'STOP_ROLLFORWARD' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test
    public void test_successful_ExecuteWithReturnedStopRollForwardAction() 
        throws ResourceException, SchedulerException
    {
        QuartzStatefulScheduledDrivenInitiator sdi = setupInitiator();
        
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // return 'rollforwardStop' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollforwardStopAction));
            }
        });

        // set common expectations
        this.setEventExpectations();
       
        setExpectationsForHandleStopAction(false);

        // invoke initiator
        sdi.invoke();

        // check is in error
        assertTrue(sdi.isError());
    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on 
     * multiple (3) events with the second event causing a 'STOP_ROLLFORWARD' action.
     * 
     * NOTE: The nature of a ROLLFORWARD midway through multiple events results
     * in all events being processed and the initiator state being set to the
     * worse case outcome based on state priority.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test
    public void test_successful_ExecuteMultipleEventsWithReturnedStopRollForwardAction() 
        throws ResourceException, SchedulerException
    {
        QuartzStatefulScheduledDrivenInitiator sdi = setupInitiator();
        
        int numOfEvents = 3;
        
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // first event return 'null' action
                exactly(1).of(flow).invoke(with(any(Event.class)));
                will(returnValue(null));
                // second event returns 'rollforwardStop' action
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollforwardStopAction));
                // third event return 'null' action
                exactly(1).of(flow).invoke(with(any(Event.class)));
                will(returnValue(null));
            }
        });

        // set common expectations
        this.setMultipleEventExpectations(numOfEvents);
        
        setExpectationsForHandleStopAction(false);
        
        // invoke initiator
        sdi.invoke();

        // check initiator status is error
        assertTrue(sdi.isError());
    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on a
     * flow invocation returning a 'CONTINUE' action.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_successful_ExecuteWithReturnedContinueAction() 
        throws ResourceException
    {
        QuartzStatefulScheduledDrivenInitiator sdi = setupInitiator();
        
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // return 'continue' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(continueAction));
            }
        });
    
        // set common expectations
        this.setEventExpectations();

        setExpectationsForResume(false);
    
        // invoke initiator
        sdi.invoke();
    
    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on 
     * multiple (3) events with the second event returning a 'CONTINUE' action.
     * 
     * NOTE: The nature of a CONTINUE midway through multiple events results
     * in all events being processed and the initiator state being set to the
     * worse case outcome based on state priority.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_successful_ExecuteMultipleEventsWithReturnedContinueAction() 
        throws ResourceException
    {
        int numOfEvents = 3;

        QuartzStatefulScheduledDrivenInitiator sdi = setupInitiator();
        
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // first event return 'null' action
                exactly(1).of(flow).invoke(with(any(Event.class)));
                will(returnValue(null));
                // second event returns 'continue' action
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(continueAction));
                // third event return 'null' action
                exactly(1).of(flow).invoke(with(any(Event.class)));
                will(returnValue(null));
            }
        });
    
        // set common expectations
        this.setMultipleEventExpectations(numOfEvents);

        setExpectationsForResume(false);
        
        // invoke initiator
        sdi.invoke();
    

    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on a
     * flow invocation returning a 'SKIP_EVENT' action.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_successful_ExecuteWithReturnedSkipEventAction() 
        throws ResourceException
    {
        QuartzStatefulScheduledDrivenInitiator sdi = setupInitiator();
        
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // return 'SKIP' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(skipAction));
            }
        });
    
        // set common expectations
        this.setEventExpectations();
        
        //SKIP and continue are handle together
        setExpectationsForResume(false);
        
        // invoke initiator
        sdi.invoke();
    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on 
     * multiple (3) events with the second event returning a 'SKIP' action.
     * 
     * NOTE: The nature of a SKIP midway through multiple events results
     * in all events being processed and the initiator state being set to the
     * worse case outcome based on state priority.
     * 
     * @throws ResourceException
     */
    @Test
    public void test_successful_ExecuteMultipleEventsWithReturnedSkipEventAction() 
        throws ResourceException
    {
        int numOfEvents = 3;

        QuartzStatefulScheduledDrivenInitiator sdi = setupInitiator();
        
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // first event return 'null' action
                exactly(1).of(flow).invoke(with(any(Event.class)));
                will(returnValue(null));
                // second event returns 'continue' action
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(skipAction));
                // third event return 'null' action
                exactly(1).of(flow).invoke(with(any(Event.class)));
                will(returnValue(null));
            }
        });
    
        // set common expectations
        this.setMultipleEventExpectations(numOfEvents);
        
        //SKIP and continue are handle together
        setExpectationsForResume(false);
    
        // invoke initiator
        sdi.invoke();
    }



    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on a
     * flow invocation returning a 'ROLLBACK_RETRY' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test(expected = org.ikasan.framework.initiator.AbortTransactionException.class)    
    public void test_successful_ExecuteWithReturnedRollbackRetryAction() 
        throws ResourceException, SchedulerException
    {
        QuartzStatefulScheduledDrivenInitiator sdi = setupInitiator();
        
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // return 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));
            }
        });

        // set common expectations
        this.setEventExpectations();


        setExpectationsForHandleRetryAction(false);

        // invoke initiator
        sdi.invoke();
    }
    
    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on 
     * multiple (3) events with the second event returning a 'ROLLBACKRETRY' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test(expected = org.ikasan.framework.initiator.AbortTransactionException.class)    
    public void test_successful_ExecuteMultipleEventsWithReturnedRollbackRetryAction() 
        throws ResourceException, SchedulerException
    {
        int numOfEvents = 3;

        QuartzStatefulScheduledDrivenInitiator sdi = setupInitiator();
        
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // first event returns 'null'
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));

                // second event fails and returns 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));

            }
        });

        // set common expectations
        this.setMultipleEventExpectations(numOfEvents);
        
        setExpectationsForHandleRetryAction(false);

        // invoke initiator
        sdi.invoke();
    }
    


    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on a
     * flow invocation returning a 'ROLLBACK_RETRY' action followed by 
     * a subsequent 'ROLLBACK_RETRY' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test
    public void test_successful_RollbackRetryActionFollowedByRollbackRetryAction() 
        throws ResourceException, SchedulerException
    {
        QuartzStatefulScheduledDrivenInitiator scheduledDrivenInitiator = setupInitiator();

        // first pass
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // return 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));

            }
        });
        setEventExpectations();
        setExpectationsForHandleRetryAction(false);
        
        
        
        Assert.assertNull("Retry count should be null initially", scheduledDrivenInitiator.getRetryCount());
        
        // invoke initiator
        invokeInitiatorExpectingRollback(scheduledDrivenInitiator);
        
        Assert.assertEquals("Retry count should still be 0 before retry has fired for the first time", new Integer(0),scheduledDrivenInitiator.getRetryCount());

        //everything cool so far?
        classMockery.assertIsSatisfied();

        // second pass
        //set up for another invocation - this time we should already be in recovery
        classMockery.checking(new Expectations()
        {
            {
                // return 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));
            }

        });     
        setEventExpectations();
        setExpectationsForHandleRetryAction(true);
        

        // invoke initiator on retry (recovering)
        invokeInitiatorExpectingRollback(scheduledDrivenInitiator);
        
        Assert.assertEquals("Retry count should 1 after retry has fired for the first time", new Integer(1),scheduledDrivenInitiator.getRetryCount());
    }



    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on
     * multiple events (3) with the second event returning a 'ROLLBACK_RETRY' action followed by 
     * a subsequent 'ROLLBACK_RETRY' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test
    public void test_successful_MultipleEventRollbackRetryActionFollowedByRollbackRetryAction() 
        throws ResourceException, SchedulerException
    {
        QuartzStatefulScheduledDrivenInitiator scheduledDrivenInitiator = setupInitiator();
        
        int numOfEvents = 3;
        
        // first pass
        classMockery.checking(new Expectations()
        {
            {
                // first event returns 'null'
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));

                // second event fails and returns 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));

            }
        });
        setMultipleEventExpectations(numOfEvents);
        setExpectationsForHandleRetryAction(false);

  
        Assert.assertNull("Retry count should be null initially", scheduledDrivenInitiator.getRetryCount());
        
        // invoke initiator
        invokeInitiatorExpectingRollback(scheduledDrivenInitiator);
        
        Assert.assertEquals("Retry count should still be 0 before retry has fired for the first time", new Integer(0),scheduledDrivenInitiator.getRetryCount());

        //everything cool so far?
        classMockery.assertIsSatisfied(); 
        
        
        

        // second pass
        classMockery.checking(new Expectations()
        {
            {
                // first event returns 'null'
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));

                // second event fails and returns 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));

            }
        });
        setMultipleEventExpectations(numOfEvents);
        setExpectationsForHandleRetryAction(true);
        

        
        // invoke initiator on retry (recovering)
        invokeInitiatorExpectingRollback(scheduledDrivenInitiator);
        
        Assert.assertEquals("Retry count should 1 after retry has fired for the first time", new Integer(1),scheduledDrivenInitiator.getRetryCount());

    }



    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on a
     * flow invocation returning a 'ROLLBACK_RETRY' action followed by 
     * a subsequent '*_STOP' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test
    public void test_successful_RollbackRetryActionFollowedByAnyStopAction() 
        throws ResourceException, SchedulerException
    {
        QuartzStatefulScheduledDrivenInitiator scheduledDrivenInitiator = setupInitiator();

        // first pass
        classMockery.checking(new Expectations()
        {
            {
                // return 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));
            }
        });
        setEventExpectations();
        setExpectationsForHandleRetryAction(false);
        
        
        // invoke initiator
        invokeInitiatorExpectingRollback(scheduledDrivenInitiator);
         
        //everything cool so far?
        classMockery.assertIsSatisfied(); 
        
        // second pass
        classMockery.checking(new Expectations()
        {
            {
                // return 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackStopAction));
            }
        });
        setEventExpectations();
        setExpectationsForHandleStopAction(true);

        
        // invoke initiator second time
        invokeInitiatorExpectingRollback(scheduledDrivenInitiator);
        
        classMockery.assertIsSatisfied();

    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on
     * multiple events (3) with the second event returning a 'ROLLBACK_RETRY' 
     * action followed by a subsequent '*_STOP' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test
    public void test_successful_MultipleEventRollbackRetryActionFollowedByAnyStopAction() 
        throws ResourceException, SchedulerException
    {
        QuartzStatefulScheduledDrivenInitiator scheduledDrivenInitiator = setupInitiator();
        
        int numOfEvents = 3;


        // first pass
        classMockery.checking(new Expectations()
        {
            {
                // first event returns 'null'
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));

                // second event returns 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));

            }
        });
        setMultipleEventExpectations(numOfEvents);
        setExpectationsForHandleRetryAction(false);
        
        
        // invoke initiator
        invokeInitiatorExpectingRollback(scheduledDrivenInitiator);
         
        //everything cool so far?
        classMockery.assertIsSatisfied(); 
        
        
        // second pass
        classMockery.checking(new Expectations()
        {
            {

                // first event returns 'null'
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));

                // second event returns 'rollbackStop' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackStopAction));


            }
        });

        setMultipleEventExpectations(numOfEvents);
        setExpectationsForHandleStopAction(true);
        
        
        
        
        // invoke initiator second time
        invokeInitiatorExpectingRollback(scheduledDrivenInitiator);
    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on a
     * flow invocation returning a 'ROLLBACK_RETRY' action followed by 
     * a subsequent 'CONTINUE' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test
    public void test_successful_RollbackRetryActionFollowedByContinueAction() 
        throws ResourceException, SchedulerException
    {
        QuartzStatefulScheduledDrivenInitiator scheduledDrivenInitiator = setupInitiator();

        // first pass
        classMockery.checking(new Expectations()
        {
            {
                // return 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));
            }
        });
        setEventExpectations();
        setExpectationsForHandleRetryAction(false);
        
        
        // invoke initiator
        invokeInitiatorExpectingRollback(scheduledDrivenInitiator);
         
        //everything cool so far?
        classMockery.assertIsSatisfied(); 
        
        // second pass
        classMockery.checking(new Expectations()
        {
            {
                // return 'continue' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(continueAction));
            }
        });
        setEventExpectations();
        setExpectationsForResume(true);       
        
        // invoke initiator on retry (recovering)
        scheduledDrivenInitiator.invoke();


    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on
     * multiple events (3) with the second event returning a 'ROLLBACK_RETRY' action 
     * followed by this second event returning a subsequent 'CONTINUE' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test
    public void test_successful_MultipleEventRollbackRetryActionFollowedByContinueAction() 
        throws ResourceException, SchedulerException
    {

        int numOfEvents = 3;

        QuartzStatefulScheduledDrivenInitiator scheduledDrivenInitiator = setupInitiator();

        // first pass
        classMockery.checking(new Expectations()
        {
            {
                // first event returns 'null'
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));

                // second event returns 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));
            }
        });
        setMultipleEventExpectations(numOfEvents);
        setExpectationsForHandleRetryAction(false);
        
        
        // invoke initiator
        invokeInitiatorExpectingRollback(scheduledDrivenInitiator);
         
        //everything cool so far?
        classMockery.assertIsSatisfied(); 
        
        // second pass
        classMockery.checking(new Expectations()
        {
            {
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));

                // return 'continue' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(continueAction));

                // third event returns 'null'
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));
            }
        });
        setMultipleEventExpectations(numOfEvents);
        setExpectationsForResume(true);       
        
        // invoke initiator on retry (recovering)
        scheduledDrivenInitiator.invoke();
        
    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on a
     * flow invocation returning a 'ROLLBACK_RETRY' action followed by 
     * a subsequent 'NULL' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test
    public void test_successful_RollbackRetryActionFollowedByNullAction() 
        throws ResourceException, SchedulerException
    {
        QuartzStatefulScheduledDrivenInitiator scheduledDrivenInitiator = setupInitiator();

        // first pass
        classMockery.checking(new Expectations()
        {
            {
                // return 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));
            }
        });
        setEventExpectations();
        setExpectationsForHandleRetryAction(false);
        
        
        // invoke initiator
        invokeInitiatorExpectingRollback(scheduledDrivenInitiator);
         
        //everything cool so far?
        classMockery.assertIsSatisfied(); 
        
        // second pass
        classMockery.checking(new Expectations()
        {
            {
                // return 'continue' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));
            }
        });
        setEventExpectations();
        setExpectationsForResume(true);       
        
        // invoke initiator on retry (recovering)
        scheduledDrivenInitiator.invoke();
    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on
     * multiple events (3) with the second event returning a 'ROLLBACK_RETRY' 
     * action followed by  a subsequent 'NULL' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test
    public void test_successful_MultipleEventRollbackRetryActionFollowedByNullAction() 
        throws ResourceException, SchedulerException
    {

        int numOfEvents = 3;

        
        QuartzStatefulScheduledDrivenInitiator scheduledDrivenInitiator = setupInitiator();

        // first pass
        classMockery.checking(new Expectations()
        {
            {
                // first event returns 'null'
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));

                // return 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));
            }
        });
        setMultipleEventExpectations(numOfEvents);
        setExpectationsForHandleRetryAction(false);
        
        
        // invoke initiator
        invokeInitiatorExpectingRollback(scheduledDrivenInitiator);
         
        //everything cool so far?
        classMockery.assertIsSatisfied(); 
        
        // second pass
        classMockery.checking(new Expectations()
        {
            {
                // first event returns 'null'
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));

                // return 'continue' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));

                // third event returns 'null'
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));
            }
        });
        setMultipleEventExpectations(numOfEvents);
        setExpectationsForResume(true);       
        
        // invoke initiator on retry (recovering)
        scheduledDrivenInitiator.invoke();       
    }

    /**
     * Test successful external start of an initiator.
     * 
     * @throws SchedulerException
     */
    @Test
    public void test_successful_StartInitiator() 
        throws SchedulerException
    {
        QuartzStatefulScheduledDrivenInitiator scheduleDrivenInitiator = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName,moduleName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        scheduleDrivenInitiator.setScheduler(scheduler);

        //expect it to be started
        setStartInitiatorExpectations(scheduleDrivenInitiator,false);
        
        // invoke start on the initiator
        scheduleDrivenInitiator.start();
    }

    /**
     * Test successful external stop of an initiator.
     * 
     * @throws SchedulerException
     */
    @Test
    public void test_successful_StopInitiator() 
        throws SchedulerException
    {
        QuartzStatefulScheduledDrivenInitiator scheduleDrivenInitiator = new QuartzStatefulScheduledDrivenInitiator(
            initiatorName,moduleName, eventProvider, flow,
            exceptionHandler);
        
        setStopInitiatorExpectations();
        
        setExpectationsForIsRecovering(false);

        // give the initiator a quartz scheduler
        scheduleDrivenInitiator.setScheduler(scheduler);



        // invoke stop on the initiator
        scheduleDrivenInitiator.stop();


    }

 

    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        // check all expectations were satisfied
        classMockery.assertIsSatisfied();
    }
    
    private void setStopInitiatorExpectations()

    {

        try
        {
            classMockery.checking(new Expectations()
            {
                {
                    // required as part of the stop call on the initiator
                    one(scheduler).pauseJobGroup(with(any(String.class)));
                }
            });
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }
   
    
    /**
     * Usual expectations for the notifyMonitorListener method.
     * 
     * Note that the only aspects of state that we may potentially care about
     * concern the error flag as the rest should be derived.
     * 
     */
    private void setNotifyMonitorListenerExpectations()
    {
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                //expectations for getState()
                //assume we are in RUNNING state
                setExpectationsForIsRunning(true);
                setExpectationsForIsRecovering(false);
                one(monitorListener).notify(with(any(String.class)));
            }
        });
    }
    
    private void setExpectationsForIsRecovering(final boolean isRecovering){
        setExpectationsForGetRecoveryTrigger(isRecovering);
    }
    
    private void setExpectationsForGetRecoveryTrigger(final boolean isRecovering){
        final Trigger recoveryTrigger = classMockery.mock(Trigger.class, "recoveryTrigger");
        
        try
        {
            classMockery.checking(new Expectations()
            {
                {
                    
                    
                    one(scheduler).getTrigger("retry_trigger",moduleName+"-"+initiatorName);
                    if (!isRecovering){
                        will(returnValue(null));
                    } else{
                        will(returnValue(recoveryTrigger));
                        allowing(recoveryTrigger).getName();will(returnValue("retry_trigger"));
                        allowing(recoveryTrigger).getGroup();will(returnValue(moduleName+"-"+initiatorName));
                    }
                }
            });
        }
        catch (SchedulerException e)
        {
           throw new RuntimeException(e);
        }
        
    }
    
    
    private void setExpectationsForIsRunning(final boolean isRunning){
        
        final Trigger trigger = classMockery.mock(Trigger.class);
        final Trigger[] triggers = new Trigger[]{trigger};
        final String triggerName = "triggerName";
        final String triggerGroup = "triggerGroup";
        try
        {
            classMockery.checking(new Expectations()
            {
                {
                    one(trigger).getName();will(returnValue(triggerName));
                    one(trigger).getGroup();will(returnValue(triggerGroup));
                    one(scheduler).getTriggerState(triggerName, triggerGroup);
                    if (isRunning){
                        will(returnValue(Trigger.STATE_NORMAL));
                    } else{
                        will(returnValue(Trigger.STATE_PAUSED));
                    }
                 }
            });

            classMockery.checking(new Expectations()
            {
                {
                    one(scheduler).isInStandbyMode();will(returnValue(false));
                    one(scheduler).isShutdown();will(returnValue(false));
                    one(scheduler).getTriggersOfJob("initiatorJob", moduleName+"-"+initiatorName);will(returnValue(triggers));
                }
            });
        }
        catch (SchedulerException e)
        {
           throw new RuntimeException(e);
        }
        
    }

    private void setEventExpectations()
        throws ResourceException
    {
        final List<Event> events = new ArrayList<Event>();
        events.add(event);

        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // get events from event provider
                one(eventProvider).getEvents();
                will(returnValue(events));
                // ignore event operations - this is not the test focus
                ignoring(event);
            }
        });
    }

    private void setMultipleEventExpectations(int numOfEvents)
        throws ResourceException
    {
        final List<Event> events = new ArrayList<Event>();
        for(int x=0; x < numOfEvents; x++)
        {
            events.add(event);
        }
    
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // get events from event provider
                one(eventProvider).getEvents();
                will(returnValue(events));
                // ignore event operations - this is not the test focus
                ignoring(event);
            }
        });
    }
    
    private void setExpectationsForResume(boolean isRecovering)
    {
        //expectations for handleNullAction
        setExpectationsForIsRecovering(isRecovering);
        if (isRecovering){
            setCancelRetryCycleExpectations(isRecovering);
            setNotifyMonitorListenerExpectations();
            //setStartInitiatorExpectations(true);
            try
            {
                classMockery.checking(new Expectations()
                {
                    {
                exactly(1).of(scheduler).resumeJobGroup(with(equal(moduleName+"-"+initiatorName)));
                    }
                });
            }
            catch (SchedulerException e)
            {
                throw new RuntimeException(e);
            }
        }
        
    }
    

    
    private void setExpectationsForHandleStopAction(boolean isRecovering)
    {
        //expectations for handleStopAction
        setExpectationsForIsRecovering(isRecovering);

        if (isRecovering){
            setCancelRetryCycleExpectations(isRecovering);
        }
        setStopInitiatorExpectations();
        setNotifyMonitorListenerExpectations();
        
    }
    
    private void setExpectationsForHandleRetryAction(boolean isRecovering)
    {
        //expectations for handleRetryAction
        setExpectationsForIsRecovering(isRecovering);
        if (!isRecovering){
            setStartRetryCycleExpectations();
            setNotifyMonitorListenerExpectations();
        }
        
    }
    
    private void setStartRetryCycleExpectations()
    {
        // set expectations
        try
        {
            classMockery.checking(new Expectations()
            {
                {
                    // we must stop the scheduled triggers on this job on group name            
                    one(scheduler).pauseJobGroup(with(equal(moduleName+"-"+initiatorName))); 
                     
                    // and then start retry trigger
                    //TODO - can we match the retry trigger a little tighter                    
                    one(scheduler).scheduleJob(with(any(Trigger.class)));
                }
            });
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    
    private void invokeInitiatorExpectingRollback(QuartzStatefulScheduledDrivenInitiator scheduledDrivenInitiator)
    {
        AbortTransactionException abortTransactionException = null;
        try
        {
            scheduledDrivenInitiator.invoke();
            fail("exception should have been thrown");
        }
        catch(AbortTransactionException e)
        {
            abortTransactionException = e;
        }
        Assert.assertNotNull("exception should have been thrown",abortTransactionException);
    }

    private QuartzStatefulScheduledDrivenInitiator setupInitiator()
    {
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName,moduleName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        return sdi;
    }
    
    private void setCancelRetryCycleExpectations(final boolean isRecovering)
    {
        
        setExpectationsForGetRecoveryTrigger(isRecovering);
        
        // set expectations
        try
        {
            classMockery.checking(new Expectations()
            {
                {
                    if (isRecovering){
                    // unschedule recovery trigger
                    one(scheduler).unscheduleJob(with(equal("retry_trigger")), with(equal(moduleName+"-"+initiatorName)));
                    }
                }
            });
        }
        catch (SchedulerException e)
        {
           throw new RuntimeException(e);
        }
    }



    private void setStartInitiatorExpectations(QuartzStatefulScheduledDrivenInitiator scheduleDrivenInitiator, final boolean previouslyStarted)
    {
        final Trigger businessAsUsualTrigger1 = classMockery.mock(Trigger.class, "businessAsUsualTrigger1");
        final Trigger businessAsUsualTrigger2 = classMockery.mock(Trigger.class, "businessAsUsualTrigger2");
        
        List<Trigger> triggers = new ArrayList<Trigger>();
        triggers.add(businessAsUsualTrigger1);
        triggers.add(businessAsUsualTrigger2);
        scheduleDrivenInitiator.setTriggers(triggers);
        
        try
        {
            classMockery.checking(new Expectations()
            {
                {
                    one(scheduler).getTriggersOfJob("initiatorJob", moduleName+"-"+initiatorName);
                    if (previouslyStarted){
                        will(returnValue(new Trigger[]{businessAsUsualTrigger1, businessAsUsualTrigger2}));
                    } else{
                        will(returnValue(new Trigger[]{}));
                        
                        //first trigger
                        one(businessAsUsualTrigger1).setGroup(moduleName+"-"+initiatorName);
                        one(scheduler).scheduleJob((JobDetail)with(any(JobDetail.class)), with(equal(businessAsUsualTrigger1)));
                        
                        //subsequent trigger
                        one(businessAsUsualTrigger2).setGroup(moduleName+"-"+initiatorName);
                        one(businessAsUsualTrigger2).setJobGroup(moduleName+"-"+initiatorName);
                        one(businessAsUsualTrigger2).setJobName("initiatorJob");
                        one(scheduler).scheduleJob(businessAsUsualTrigger2 );
                    }
                    
                    
                    
                    // resume the flow
                    exactly(1).of(scheduler).resumeJobGroup(with(equal(moduleName+"-"+initiatorName)));
                }
            });
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }
}
