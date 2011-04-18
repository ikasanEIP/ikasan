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
package org.ikasan.recovery;

import java.util.Date;

import junit.framework.Assert;

import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exceptionResolver.action.ExceptionAction;
import org.ikasan.exceptionResolver.action.ExcludeEventAction;
import org.ikasan.exceptionResolver.action.RetryAction;
import org.ikasan.exceptionResolver.action.StopAction;
import org.ikasan.recovery.ScheduledRecoveryManager;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.recovery.RecoveryManager;
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

    /** Mock exception resolver */
    final ExceptionResolver exceptionResolver = mockery.mock(ExceptionResolver.class, "mockExceptionResolver");

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
     * Test failed constructor due to null scheduler.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullScheduler()
    {
        new ScheduledRecoveryManager(null, null, null, null, null);
    }

    /**
     * Test failed constructor due to null flow name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullFlowName()
    {
        new ScheduledRecoveryManager(scheduler, null, null, null, null);
    }

    /**
     * Test failed constructor due to null module name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullModuleName()
    {
        new ScheduledRecoveryManager(scheduler, "flowName", null, null, null);
    }

    /**
     * Test failed constructor due to null consumer.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullConsumer()
    {
        new ScheduledRecoveryManager(scheduler, "flowName", "moduleName", null, null);
    }

    /**
     * Test constructor is happy regardless of null exception resolver.
     */
    @Test
    public void test_successful_constructor_evenWithDueToNullExceptionResolver()
    {
        new ScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer, null);
    }

    /**
     * Test successful instantiation.
     */
    @Test
    public void test_successful_instantiation()
    {
        new ScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer, exceptionResolver);
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
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(stopAction));
                
                exactly(1).of(scheduler).isStarted();
                will(returnValue(false));
                exactly(1).of(consumer).stop();
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer, exceptionResolver);
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
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
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

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer, exceptionResolver);
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
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(retryAction));
                
                // firstly stop the consumer
                exactly(1).of(consumer).stop();

                // for this test we are not already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
                
                // create the recovery job and associated trigger
                exactly(3).of(retryAction).getMaxRetries();
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
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
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
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(retryAction));
                
                // stop the consumer
                exactly(1).of(consumer).stop();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
                
                // check we have not exceeded retry limits
                exactly(2).of(retryAction).getMaxRetries();
                will(returnValue(maxRetries));
                
                // cancel the recovery
                exactly(1).of(scheduler).deleteJob("recoveryJob_flowName", "recoveryManager_moduleName");
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer, exceptionResolver);

        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("retryAction invoked", e.getMessage());
        }

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 1);

        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("retryAction invoked", e.getMessage());
        }

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
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(unsupportedExceptionAction));
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer, exceptionResolver);
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

        public StubbedScheduledRecoveryManager(Scheduler scheduler, String flowName, String moduleName, Consumer consumer, ExceptionResolver exceptionResolver)
        {
            super(scheduler, flowName, moduleName, consumer, exceptionResolver);
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
