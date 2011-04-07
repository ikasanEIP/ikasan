/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 *
 * Copyright (c) 2000-20010 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.recoveryManager;

import java.util.Date;

import junit.framework.Assert;

import org.ikasan.exceptionHandler.ExceptionHandler;
import org.ikasan.exceptionHandler.action.ExceptionAction;
import org.ikasan.exceptionHandler.action.ExcludeEventAction;
import org.ikasan.exceptionHandler.action.RetryAction;
import org.ikasan.exceptionHandler.action.StopAction;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.recoveryManager.RecoveryManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

/**
 * This test class supports the <code>ScheduledRecoveryManager</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledRecoveryManagerTest
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
    
    /** Mock consumer flowElement */
    final Consumer consumer = mockery.mock(Consumer.class, "mockConsumer");

    /** Mock exception handler */
    final ExceptionHandler exceptionHandler = mockery.mock(ExceptionHandler.class, "mockExceptionHandler");

    /** Mock scheduler */
    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");

    /** Mock recovery job detail */
    final JobDetail jobDetail = mockery.mock(JobDetail.class, "mockJobDetail");

    /** Mock recovery job trigger */
    final Trigger trigger = mockery.mock(Trigger.class, "mockTrigger");

    /** Mock stopAction */
    final StopAction stopAction = mockery.mock(StopAction.class, "mockStopAction");
    
    /** Mock retryAction */
    final RetryAction retryAction = mockery.mock(RetryAction.class, "mockRetryAction");
    
    /** Mock excludeEventAction */
    final ExcludeEventAction excludeEventAction = mockery.mock(ExcludeEventAction.class, "mockExcludeEventAction");
    
    /**
     * Test failed constructor due to null flow name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullFlowName()
    {
        new ScheduledRecoveryManager(null, null, null, null, null, null);
    }

    /**
     * Test failed constructor due to null consumer.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullConsumer()
    {
        new ScheduledRecoveryManager(null, null, null, null, null, null);
    }

    /**
     * Test failed constructor due to null consumer.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullConsumer()
    {
        new ScheduledRecoveryManager(null, null, null, null, null, null);
    }

    /**
     * Test failed constructor due to null consumer name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullConsumerName()
    {
        new ScheduledRecoveryManager(consumer, null, null, null);
    }

    /**
     * Test failed constructor due to null exception handler.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullExceptionHandler()
    {
        new ScheduledRecoveryManager(consumer, "consumerName", null, null);
    }

    /**
     * Test failed constructor due to null scheduler.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullScheduler()
    {
        new ScheduledRecoveryManager(consumer, "consumerName", exceptionHandler, null);
    }

    /**
     * Test successful instantiation.
     */
    @Test
    public void test_successful_instantiation()
    {
        new ScheduledRecoveryManager(consumer, "consumerName", exceptionHandler, scheduler);
    }

    /**
     * Test successful stop action on recovery.
     * @throws SchedulerException 
     */
    @Test(expected = RuntimeException.class)
    public void test_successful_recover_to_stopAction_with_no_previousAction() throws SchedulerException
    {
        final Exception exception = new Exception();
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // resolve the component name and exception to an action
                exactly(1).of(exceptionHandler).handleThrowable("componentName", exception);
                will(returnValue(stopAction));
                
                exactly(1).of(scheduler).isStarted();
                will(returnValue(false));
                exactly(1).of(consumer).stop();
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(consumer, "consumerName", exceptionHandler, scheduler);
        recoveryManager.recover("componentName", exception);
        
        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful retry action on recovery.
     * @throws SchedulerException 
     */
    @Test(expected = RuntimeException.class)
    public void test_successful_recover_to_retryAction_with_no_previousAction() throws SchedulerException
    {
        final Exception exception = new Exception();
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // resolve the component name and exception to an action
                exactly(1).of(exceptionHandler).handleThrowable("componentName", exception);
                will(returnValue(retryAction));
                
                // firstly stop the consumer
                exactly(1).of(consumer).stop();

                // for this test we are not already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(false));
                
                // so start the scheduler
                exactly(1).of(scheduler).start();

                // create the recovery job and associated trigger
                exactly(1).of(retryAction).getMaxRetries();
                will(returnValue(2));
                exactly(2).of(retryAction).getDelay();
                will(returnValue(2000));
                exactly(1).of(trigger).setStartTime(with(any(Date.class)));
                
                // schedule the recovery job with its trigger
                exactly(1).of(scheduler).scheduleJob(jobDetail, trigger);

                // now we are in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(consumer, "consumerName", exceptionHandler, scheduler);
        recoveryManager.recover("componentName", exception);

        Assert.assertTrue(recoveryManager.isRecovering());
        
        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 1);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful three consecutive retry actions with the last one
     * exceeding the maximum attempts limit.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_recover_to_three_retryActions_until_exceeds_max_attempts() throws SchedulerException
    {
        final Exception exception = new Exception();
        final long delay = 2000;
        final int maxRetries = 2;
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                //
                // first time retry action is invoked
                // 
                
                // resolve the component name and exception to an action
                exactly(1).of(exceptionHandler).handleThrowable("componentName", exception);
                will(returnValue(retryAction));
                
                // firstly stop the consumer
                exactly(1).of(consumer).stop();

                // for this test we are not already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(false));
                
                // so start the scheduler
                exactly(1).of(scheduler).start();

                // create the recovery job and associated trigger
                exactly(1).of(retryAction).getMaxRetries();
                will(returnValue(maxRetries));
                exactly(2).of(retryAction).getDelay();
                will(returnValue(delay));
                exactly(1).of(trigger).setStartTime(with(any(Date.class)));
                
                // schedule the recovery job with its trigger
                exactly(1).of(scheduler).scheduleJob(jobDetail, trigger);

                //
                // second time retry action is invoked
                // 
                
                // resolve the component name and exception to an action
                exactly(1).of(exceptionHandler).handleThrowable("componentName", exception);
                will(returnValue(retryAction));
                
                // stop the consumer
                exactly(1).of(consumer).stop();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
                
                // check we have not exceeded retry limits
                exactly(4).of(retryAction).getMaxRetries();
                will(returnValue(maxRetries));

                //
                // third time retry action is invoked
                // 
                
                // resolve the component name and exception to an action
                exactly(1).of(exceptionHandler).handleThrowable("componentName", exception);
                will(returnValue(retryAction));
                
                // stop the consumer
                exactly(1).of(consumer).stop();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
                
                // check we have not exceeded retry limits
                exactly(4).of(retryAction).getMaxRetries();
                will(returnValue(maxRetries));
                
                // cancel the recovery
                exactly(1).of(scheduler).deleteJob("recoveryJob", "recoveryManager");
                exactly(1).of(scheduler).shutdown();
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(consumer, "consumerName", exceptionHandler, scheduler);
        recoveryManager.recover("componentName", exception);

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 1);

        recoveryManager.recover("componentName", exception);

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 2);

        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("Exhausted maximum retries.", e.getMessage());
        }
        
        Assert.assertTrue(recoveryManager.isUnrecoverable());

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test failed recovery due to unsupported recovery action.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void test_failed_recover_due_to_unsupported_recovery_action()
    {
        final Exception exception = new Exception();
        
        final ExceptionAction unsupportedExceptionAction = new UnsupportedExceptionAction();
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // resolve the component name and exception to an action
                exactly(1).of(exceptionHandler).handleThrowable("componentName", exception);
                will(returnValue(unsupportedExceptionAction));
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(consumer, "consumerName", exceptionHandler, scheduler);
        recoveryManager.recover("componentName", exception);

        mockery.assertIsSatisfied();
    }

    /**
     * Extended class allowing mocking of the quartz recovery job and trigger.
     * @author Ikasan Development Team
     *
     */
    private class StubbedScheduledRecoveryManager extends ScheduledRecoveryManager
    {

        public StubbedScheduledRecoveryManager(Consumer consumer, String consumerName, ExceptionHandler exceptionHandler, Scheduler scheduler)
        {
            super(consumer, consumerName, exceptionHandler, scheduler);
        }
        
        @Override
        protected JobDetail newRecoveryJob()
        {
            return jobDetail;
        }
        
        @Override
        protected Trigger newRecoveryTrigger(int maxRetries, long delay)
        {
            return trigger;
        }
        
        /**
         * Added to allow testing on retry attempts counter
         */
        protected int getRetryAttempts()
        {
            return this.recoveryAttempts;
        }
    }

    /**
     * Test instance of an unsupported exception action.
     * @author Ikasan Development Team
     *
     */
    private class UnsupportedExceptionAction implements ExceptionAction
    {
        // nothing to do
    }
}
