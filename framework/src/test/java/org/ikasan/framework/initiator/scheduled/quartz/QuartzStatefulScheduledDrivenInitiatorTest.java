/*
 * $Id: QuartzStatefulScheduledDrivenInitiatorTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/initiator/scheduled/quartz/QuartzStatefulScheduledDrivenInitiatorTest.java $
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
package org.ikasan.framework.initiator.scheduled.quartz;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import junit.framework.JUnit4TestAdapter;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.event.service.EventProvider;
import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.IkasanExceptionActionImpl;
import org.ikasan.framework.exception.IkasanExceptionActionType;
import org.ikasan.framework.flow.Flow;
import org.ikasan.framework.initiator.AbortTransactionException;
import org.ikasan.framework.monitor.MonitorListener;
import org.ikasan.framework.monitor.MonitorListenerNotFoundException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
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
    final JobExecutionContext jec = 
        classMockery.mock(JobExecutionContext.class);
    final ScheduledDrivenQuartzContext initiatorContext = 
        classMockery.mock(ScheduledDrivenQuartzContext.class);
    final JobDetail jobDetail = classMockery.mock(JobDetail.class);
    final JobDataMap jobDataMap = classMockery.mock(JobDataMap.class);
    final Trigger trigger = classMockery.mock(Trigger.class);
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

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing to do here
    }

    private void setStopInitiatorExpectations()
        throws SchedulerException
    {
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // required as part of the stop call on the initiator
                one(scheduler).pauseJobGroup(with(any(String.class)));
            }
        });
    }

    private void setStopRecoveringInitiatorExpectations()
        throws SchedulerException
    {
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // required as part of the stop call on a recovering initiator
                one(scheduler).getTrigger(with(any(String.class)), with(any(String.class)));
            }
        });
    }

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
                initiatorName, eventProvider, flow, exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // stop initiator and check state is stopped
        sdi.stop();
        assertTrue(sdi.getState().isStopped());

        // invoke initiator
        sdi.invoke(initiatorContext);
    }

    /**
     * Expectations for the addListener method.
     */
    private void setAddListenerExpectations()
    {
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                one(monitorListener).getName();
                will(returnValue("monitorListenerName"));
            }
        });
    }
    
    /**
     * Expectations for the removeListener method.
     */
    private void setRemoveListenerExpectations()
    {
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                one(monitorListener).getName();
                will(returnValue("monitorListenerName"));
            }
        });
    }
    
    /**
     * Usual expectations for the notifyMonitorListener method.
     */
    private void setNotifyMonitorListenerExpectations()
    {
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                one(monitorListener).notify(with(any(String.class)));
            }
        });
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
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // get events from event provider
                one(eventProvider).getEvents();
                will(returnValue(null));
            }
        });

        // set common expectations
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        sdi.invoke(initiatorContext);
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
        // 
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
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        sdi.invoke(initiatorContext);
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
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        sdi.invoke(initiatorContext);
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
        // 
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
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();
        this.setStopInitiatorExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        sdi.invoke(initiatorContext);

        // check initiator status is error
        assertTrue(sdi.getState().isError());
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
                exactly(1).of(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackStopAction));

                // if we bail on rollback the third event is never reached
//                // third event return 'null' action
//                exactly(1).of(flow).invoke(with(any(Event.class)));
//                will(returnValue(null));
            }
        });

        // set common expectations
        this.setMultipleEventExpectations(numOfEvents);
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();
        this.setStopInitiatorExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        sdi.invoke(initiatorContext);

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
        // 
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
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();
        this.setStopInitiatorExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        sdi.invoke(initiatorContext);

        // check initiator status is error
        assertTrue(sdi.getState().isError());
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
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();
        this.setStopInitiatorExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        sdi.invoke(initiatorContext);

        // check initiator status is error
        assertTrue(sdi.getState().isError());
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
        // 
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
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);
    
        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);
    
        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());
    
        // invoke initiator
        sdi.invoke(initiatorContext);
    
        // check initiator status is running
        assertTrue(sdi.getState().isRunning());
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

        // 
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
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();

        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);
    
        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);
    
        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());
    
        // invoke initiator
        sdi.invoke(initiatorContext);
    
        // check initiator status is running
        assertTrue(sdi.getState().isRunning());
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
        // 
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
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);
    
        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);
    
        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());
    
        // invoke initiator
        sdi.invoke(initiatorContext);
    
        // check initiator status is running
        assertTrue(sdi.getState().isRunning());
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

        // 
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
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();

        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);
    
        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);
    
        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());
    
        // invoke initiator
        sdi.invoke(initiatorContext);
    
        // check initiator status is running
        assertTrue(sdi.getState().isRunning());
    }

    private void setStartRetryCycleExpectations()
        throws SchedulerException
    {
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // start retry cycle
                // context.getJobDetail and group name
                exactly(1).of(initiatorContext).getJobDetail();
                will(returnValue(jobDetail));
                exactly(2).of(jobDetail).getGroup();
                will(returnValue("jobGroup"));

                // we must stop the scheduled triggers on this job on group name
                one(scheduler).pauseJobGroup(with(any(String.class)));
                 
                // context.getJobDetail for name for trigger
                exactly(1).of(jobDetail).getName();
                will(returnValue("jobName"));
                // context.getJobDetail for group for trigger
                exactly(1).of(jobDetail).getGroup();
                will(returnValue("jobGroup"));
                 
                // and then start retry trigger
                one(scheduler).scheduleJob(with(any(Trigger.class)));
            }
        });
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
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // return 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));

                // handle the retry action.
                // check this action against the previous action
                // context.getIkasanExceptionAction - return null as no previous
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(null));

                // context.setIkasanExceptionAction to current action
                // which will initialise the retry count to 0
                exactly(1).of(initiatorContext).setIkasanExceptionAction(rollbackRetryAction);
            }
        });

        // set common expectations
        this.setEventExpectations();
        this.setStartRetryCycleExpectations();
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();

        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        sdi.invoke(initiatorContext);
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

        // 
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

                // if we bail on rollback the third event is never reached
//                // third event returns 'null'
//                one(flow).invoke(with(any(Event.class)));
//                will(returnValue(null));

                // handle the retry action.
                // check this action against the previous action
                // context.getIkasanExceptionAction - return null as no previous
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(null));

                // context.setIkasanExceptionAction to current action
                // which will initialise the retry count to 0
                exactly(1).of(initiatorContext).setIkasanExceptionAction(rollbackRetryAction);
            }
        });

        // set common expectations
        this.setMultipleEventExpectations(numOfEvents);
        this.setStartRetryCycleExpectations();
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();

        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        sdi.invoke(initiatorContext);
    }
    
    private void setContinueRetryCycleExpectations()
    {
        final int retryCount = 0;
        
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // get previous action for comparison i.e. is it the same
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(rollbackRetryAction));
    
                // get retry count to update
                exactly(1).of(initiatorContext).getRetryCount();
                will(returnValue(retryCount));
    
                // set retry count
                exactly(1).of(initiatorContext).setRetryCount(with(any(int.class)));
            }
        });
    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on a
     * flow invocation returning a 'ROLLBACK_RETRY' action followed by 
     * a subsequent 'ROLLBACK_RETRY' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test(expected = org.ikasan.framework.initiator.AbortTransactionException.class)    
    public void test_successful_RollbackRetryActionFollowedByRollbackRetryAction() 
        throws ResourceException, SchedulerException
    {
        final int retryCount = 0;
        
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // return 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));

                // handle the retry action.
                // check this action against the previous action
                // context.getIkasanExceptionAction - return null as no previous
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(null));

                // context.setIkasanExceptionAction to current action
                // which will initialise the retry count to 0
                exactly(1).of(initiatorContext).setIkasanExceptionAction(rollbackRetryAction);

                // ////////////////////////////////
                // RETRY after the rollback
                // ///////////////////////////////

                // return 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));

                // get retry count for logging
                exactly(1).of(initiatorContext).getRetryCount();
                will(returnValue(retryCount));
                
                // handle the retry action.
                // check this action against the previous action
                // context.getIkasanExceptionAction - return action as previous
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(rollbackRetryAction));
            }
        });

        // set common expectations
        // first pass
        this.setEventExpectations();
        this.setStartRetryCycleExpectations();
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();

        // second pass
        this.setEventExpectations();
        this.setContinueRetryCycleExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        try
        {
            sdi.invoke(initiatorContext);
        }
        catch(AbortTransactionException e)
        {
            // expected
        }
        
        // check initiator status is recovering
        assertTrue(sdi.getState().isRecovering());

        // invoke initiator on retry (recovering)
        sdi.invoke(initiatorContext);
    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on
     * multiple events (3) with the second event returning a 'ROLLBACK_RETRY' action followed by 
     * a subsequent 'ROLLBACK_RETRY' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test(expected = org.ikasan.framework.initiator.AbortTransactionException.class)    
    public void test_successful_MultipleEventRollbackRetryActionFollowedByRollbackRetryAction() 
        throws ResourceException, SchedulerException
    {
        final int retryCount = 0;
        int numOfEvents = 3;
        
        // 
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

                // if we bail on rollback the third event is never reached
//                // third event returns 'null'
//                one(flow).invoke(with(any(Event.class)));
//                will(returnValue(null));

                // handle the retry action.
                // check this action against the previous action
                // context.getIkasanExceptionAction - return null as no previous
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(null));

                // context.setIkasanExceptionAction to current action
                // which will initialise the retry count to 0
                exactly(1).of(initiatorContext).setIkasanExceptionAction(rollbackRetryAction);

                // ////////////////////////////////
                // RETRY after the rollback
                // ///////////////////////////////

                // first event returns 'null'
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));

                // second event fails and returns 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));

                // if we bail on rollback the third event is never reached
//                // third event returns 'null'
//                one(flow).invoke(with(any(Event.class)));
//                will(returnValue(null));

                // get retry count for logging
                exactly(1).of(initiatorContext).getRetryCount();
                will(returnValue(retryCount));
                
                // handle the retry action.
                // check this action against the previous action
                // context.getIkasanExceptionAction - return action as previous
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(rollbackRetryAction));
            }
        });

        // set common expectations
        // first pass
        this.setMultipleEventExpectations(numOfEvents);
        this.setStartRetryCycleExpectations();
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();

        // second pass
        this.setMultipleEventExpectations(numOfEvents);
        this.setContinueRetryCycleExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        try
        {
            sdi.invoke(initiatorContext);
        }
        catch(AbortTransactionException e)
        {
            // expected
        }
        
        // check initiator status is recovering
        assertTrue(sdi.getState().isRecovering());

        // invoke initiator on retry (recovering)
        sdi.invoke(initiatorContext);
    }

    private void setCancelRetryCycleExpectations()
        throws SchedulerException
    {
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // unschedule the job
                exactly(1).of(scheduler).unscheduleJob(with(any(String.class)), with(any(String.class)));
            }
        });
    }

    private void setCompleteRetryCycleExpectations()
        throws SchedulerException
    {
        this.setCancelRetryCycleExpectations();
        this.setStartInitiatorExpectations();
        
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // we need to cancel the recovery so get the trigger
                exactly(1).of(initiatorContext).getTrigger();
                exactly(1).of(initiatorContext).clearRetry();
            }
        });
    }

    private void setStartInitiatorExpectations()
        throws SchedulerException
    {
        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // resume the flow
                exactly(1).of(scheduler).resumeJobGroup(with(any(String.class)));
            }
        });
    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on a
     * flow invocation returning a 'ROLLBACK_RETRY' action followed by 
     * a subsequent '*_STOP' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test(expected = org.ikasan.framework.initiator.AbortTransactionException.class)    
    public void test_successful_RollbackRetryActionFollowedByAnyStopAction() 
        throws ResourceException, SchedulerException
    {
        final int retryCount = 0;

        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // return 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));

                // handle the retry action.
                // check this action against the previous action
                // context.getIkasanExceptionAction - return null as no previous
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(null));

                // context.setIkasanExceptionAction to current action
                // which will initialise the retry count to 0
                exactly(1).of(initiatorContext).setIkasanExceptionAction(rollbackRetryAction);

                // ////////////////////////////////
                // STOP after the rollback
                // ///////////////////////////////

                // we are in recovery so get the recovery details
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(rollbackRetryAction));
                
                // get retry count for logging
                exactly(1).of(initiatorContext).getRetryCount();
                will(returnValue(retryCount));

                // return 'rollbackStop' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackStopAction));
            }
        });

        // set common expectations
        // first pass
        this.setEventExpectations();
        this.setStartRetryCycleExpectations();
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();

        // second pass
        this.setEventExpectations();
        this.setCancelRetryCycleExpectations();
        this.setStopRecoveringInitiatorExpectations();
        this.setNotifyMonitorListenerExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        try
        {
            sdi.invoke(initiatorContext);
        }
        catch(AbortTransactionException e)
        {
            // expected
        }

        // check initiator status is recovering
        assertTrue(sdi.getState().isRecovering());

        // invoke initiator on retry (recovering)
        sdi.invoke(initiatorContext);
    }

    /**
     * Test execution of the QuartzStatefulScheduledDrivenInitiator based on
     * multiple events (3) with the second event returning a 'ROLLBACK_RETRY' 
     * action followed by a subsequent '*_STOP' action.
     * 
     * @throws ResourceException
     * @throws SchedulerException
     */
    @Test(expected = org.ikasan.framework.initiator.AbortTransactionException.class)    
    public void test_successful_MultipleEventRollbackRetryActionFollowedByAnyStopAction() 
        throws ResourceException, SchedulerException
    {
        final int retryCount = 0;
        int numOfEvents = 3;

        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // first event returns 'null'
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));

                // second event returns 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));

                // if we bail on rollback the third event is never reached
//                // third event returns 'null'
//                one(flow).invoke(with(any(Event.class)));
//                will(returnValue(null));

                // handle the retry action.
                // check this action against the previous action
                // context.getIkasanExceptionAction - return null as no previous
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(null));

                // context.setIkasanExceptionAction to current action
                // which will initialise the retry count to 0
                exactly(1).of(initiatorContext).setIkasanExceptionAction(rollbackRetryAction);

                // ////////////////////////////////
                // STOP after the rollback
                // ///////////////////////////////

                // we are in recovery so get the recovery details
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(rollbackRetryAction));
                
                // get retry count for logging
                exactly(1).of(initiatorContext).getRetryCount();
                will(returnValue(retryCount));

                // first event returns 'null'
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));

                // second event returns 'rollbackStop' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackStopAction));

                // if we bail on rollback the third event is never reached
//                // third event returns 'null'
//                one(flow).invoke(with(any(Event.class)));
//                will(returnValue(null));
            }
        });

        // set common expectations
        // first pass
        this.setMultipleEventExpectations(numOfEvents);
        this.setStartRetryCycleExpectations();
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();

        // second pass
        this.setMultipleEventExpectations(numOfEvents);
        this.setCancelRetryCycleExpectations();
        this.setStopRecoveringInitiatorExpectations();
        this.setNotifyMonitorListenerExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        try
        {
            sdi.invoke(initiatorContext);
        }
        catch(AbortTransactionException e)
        {
            // expected
        }

        // check initiator status is recovering
        assertTrue(sdi.getState().isRecovering());

        // invoke initiator on retry (recovering)
        sdi.invoke(initiatorContext);
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
        final int retryCount = 0;

        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // return 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));

                // handle the retry action.
                // check this action against the previous action
                // context.getIkasanExceptionAction - return null as no previous
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(null));

                // context.setIkasanExceptionAction to current action
                // which will initialise the retry count to 0
                exactly(1).of(initiatorContext).setIkasanExceptionAction(rollbackRetryAction);

                // ////////////////////////////////
                // CONTINUE after the rollback
                // ///////////////////////////////
                
                // we are in recovery so get the recovery details
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(rollbackRetryAction));
                
                // get retry count for logging
                exactly(1).of(initiatorContext).getRetryCount();
                will(returnValue(retryCount));

                // return 'continue' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(continueAction));
            }
        });

        // set common expectations
        // first pass
        this.setEventExpectations();
        this.setStartRetryCycleExpectations();
        this.setNotifyMonitorListenerExpectations();

        // second pass
        this.setEventExpectations();
        this.setCompleteRetryCycleExpectations();
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        try
        {
            sdi.invoke(initiatorContext);
        }
        catch(AbortTransactionException e)
        {
            // expected
        }

        // check initiator status is recovering
        assertTrue(sdi.getState().isRecovering());

        // invoke initiator on retry (recovering)
        sdi.invoke(initiatorContext);
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
        final int retryCount = 0;
        int numOfEvents = 3;

        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // first event returns 'null'
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));

                // second event returns 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));

                // if we bail on rollback the third event is never reached
//                // third event returns 'null'
//                one(flow).invoke(with(any(Event.class)));
//                will(returnValue(null));

                // handle the retry action.
                // check this action against the previous action
                // context.getIkasanExceptionAction - return null as no previous
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(null));

                // context.setIkasanExceptionAction to current action
                // which will initialise the retry count to 0
                exactly(1).of(initiatorContext).setIkasanExceptionAction(rollbackRetryAction);

                // ////////////////////////////////
                // CONTINUE after the rollback
                // ///////////////////////////////
                
                // we are in recovery so get the recovery details
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(rollbackRetryAction));
                
                // get retry count for logging
                exactly(1).of(initiatorContext).getRetryCount();
                will(returnValue(retryCount));

                // first event returns 'null'
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

        // set common expectations
        // first pass
        this.setMultipleEventExpectations(numOfEvents);
        this.setStartRetryCycleExpectations();
        this.setNotifyMonitorListenerExpectations();

        // second pass
        this.setMultipleEventExpectations(numOfEvents);
        this.setCompleteRetryCycleExpectations();
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        try
        {
            sdi.invoke(initiatorContext);
        }
        catch(AbortTransactionException e)
        {
            // expected
        }

        // check initiator status is recovering
        assertTrue(sdi.getState().isRecovering());

        // invoke initiator on retry (recovering)
        sdi.invoke(initiatorContext);
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
        final int retryCount = 0;

        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // return 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));

                // handle the retry action.
                // check this action against the previous action
                // context.getIkasanExceptionAction - return null as no previous
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(null));

                // context.setIkasanExceptionAction to current action
                // which will initialise the retry count to 0
                exactly(1).of(initiatorContext).setIkasanExceptionAction(rollbackRetryAction);

                // ////////////////////////////////
                // NULL after the rollback
                // ///////////////////////////////
                
                // we are in recovery so get the recovery details
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(rollbackRetryAction));
                
                // get retry count for logging
                exactly(1).of(initiatorContext).getRetryCount();
                will(returnValue(retryCount));

                // return 'continue' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));
            }
        });

        // set common expectations
        // first pass
        this.setEventExpectations();
        this.setStartRetryCycleExpectations();
        this.setNotifyMonitorListenerExpectations();

        // second pass
        this.setEventExpectations();
        this.setCompleteRetryCycleExpectations();
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        try
        {
            sdi.invoke(initiatorContext);
        }
        catch(AbortTransactionException e)
        {
            // expected
        }

        // check initiator status is recovering
        assertTrue(sdi.getState().isRecovering());

        // invoke initiator on retry (recovering)
        sdi.invoke(initiatorContext);
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
        final int retryCount = 0;
        int numOfEvents = 3;

        // 
        // set expectations
        classMockery.checking(new Expectations()
        {
            {
                // first event returns 'null'
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(null));

                // return 'rollbackRetry' ikasanExceptionAction from the flow invocation
                one(flow).invoke(with(any(Event.class)));
                will(returnValue(rollbackRetryAction));

                // if we bail on rollback the third event is never reached
//                // third event returns 'null'
//                one(flow).invoke(with(any(Event.class)));
//                will(returnValue(null));

                // handle the retry action.
                // check this action against the previous action
                // context.getIkasanExceptionAction - return null as no previous
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(null));

                // context.setIkasanExceptionAction to current action
                // which will initialise the retry count to 0
                exactly(1).of(initiatorContext).setIkasanExceptionAction(rollbackRetryAction);

                // ////////////////////////////////
                // NULL after the rollback
                // ///////////////////////////////
                
                // we are in recovery so get the recovery details
                exactly(1).of(initiatorContext).getIkasanExceptionAction();
                will(returnValue(rollbackRetryAction));
                
                // get retry count for logging
                exactly(1).of(initiatorContext).getRetryCount();
                will(returnValue(retryCount));

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

        // set common expectations
        // first pass
        this.setMultipleEventExpectations(numOfEvents);
        this.setStartRetryCycleExpectations();
        this.setNotifyMonitorListenerExpectations();

        // second pass
        this.setMultipleEventExpectations(numOfEvents);
        this.setCompleteRetryCycleExpectations();
        this.setAddListenerExpectations();
        this.setNotifyMonitorListenerExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);
        
        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke initiator
        try
        {
            sdi.invoke(initiatorContext);
        }
        catch(AbortTransactionException e)
        {
            // expected
        }

        // check initiator status is recovering
        assertTrue(sdi.getState().isRecovering());

        // invoke initiator on retry (recovering)
        sdi.invoke(initiatorContext);
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
        // set common expectations
        this.setStartInitiatorExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke stop on the initiator
        sdi.start();

        // check initiator status is running
        assertTrue(sdi.getState().isRunning());
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
        // set common expectations
        this.setStopInitiatorExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // check initiator status is running (default state on instantiation)
        assertTrue(sdi.getState().isRunning());

        // invoke stop on the initiator
        sdi.stop();

        // check initiator status is stopped
        assertTrue(sdi.getState().isStopped());
    }

    /**
     * Test successful add and remove of monitorListeners.
     * 
     * @throws MonitorListenerNotFoundException 
     */
    @Test
    public void test_successful_AddListenerAndRemoveListener() 
        throws MonitorListenerNotFoundException
    {
        // set common expectations
        this.setAddListenerExpectations();
        this.setRemoveListenerExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // add a monitor
        sdi.addListener(monitorListener);

        // remove a monitor
        sdi.removeListener(monitorListener);
    }

    /**
     * Test exception thrown on removal of a non-existent listener.
     * 
     * @throws MonitorListenerNotFoundException 
     */
    @Test(expected = org.ikasan.framework.monitor.MonitorListenerNotFoundException.class)    
    public void test_fail_NonExistentListenerRemoval() 
        throws MonitorListenerNotFoundException
    {
        // set common expectations
        this.setRemoveListenerExpectations();
        
        //
        // run test
        QuartzStatefulScheduledDrivenInitiator sdi = new QuartzStatefulScheduledDrivenInitiator(
                initiatorName, eventProvider, flow,
                exceptionHandler);

        // give the initiator a quartz scheduler
        sdi.setScheduler(scheduler);

        // remove a monitor
        sdi.removeListener(monitorListener);
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

    /**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(
                QuartzStatefulScheduledDrivenInitiatorTest.class);
    }
}
