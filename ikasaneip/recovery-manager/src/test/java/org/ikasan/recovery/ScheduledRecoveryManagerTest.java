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

import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.exceptionResolver.action.*;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.FinalAction;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.exceptionResolver.action.ExceptionAction;
import org.ikasan.spec.recovery.RecoveryManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.*;

import java.util.ArrayList;
import java.util.List;

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
            setThreadingPolicy(new Synchroniser());
        }
    };
    
    /** Mock consumer flowElement */
    final Consumer consumer = mockery.mock(Consumer.class, "mockConsumer");

    /** Mock exception resolver */
    final ExceptionResolver exceptionResolver = mockery.mock(ExceptionResolver.class, "mockExceptionResolver");

    /** Mock scheduler */
    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");

    /** Mock scheduledJobFactory */
    final ScheduledJobFactory scheduledJobFactory = mockery.mock(ScheduledJobFactory.class, "mockScheduledJobFactory");

    /** Mock recovery job detail */
    final JobDetail jobDetail = mockery.mock(JobDetail.class, "mockJobDetail");

    /** Mock recovery job trigger */
    final Trigger trigger = mockery.mock(Trigger.class, "mockTrigger");

    /** Mock stopAction */
    final StopAction stopAction = mockery.mock(StopAction.class, "Stop");
    
    /** Mock retryAction */
    final RetryAction retryAction = mockery.mock(RetryAction.class, "Retry");
    
    /** Mock excludeEventAction */
    final ExcludeEventAction excludeEventAction = mockery.mock(ExcludeEventAction.class, "ExcludeEvent");
    
    /** Mock ignoreAction */
    final IgnoreAction ignoreAction = mockery.mock(IgnoreAction.class, "Ignore");
    
    /** Mock flowElement */
    final FlowElement flowElement = mockery.mock(FlowElement.class, "FlowElement");
    
    /** Mock managedResource */
    final ManagedResource managedResource = mockery.mock(ManagedResource.class, "ManagedResource");

    /** Mock exclusion service */
    final ExclusionService exclusionService = mockery.mock(ExclusionService.class, "mockExclusionService");

    /** Mock error reporting service */
    final ErrorReportingService errorReportingService = mockery.mock(ErrorReportingService.class, "mockErrorReportingService");

    /** Mock flowEvent */
    final FlowEvent flowEvent = mockery.mock(FlowEvent.class, "mockFlowEvent");

    /** Mock flowInvocationContext*/
    final FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "flowInvocationContext");

    /**
     * Test failed constructor due to null scheduler.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullScheduler()
    {
        new ScheduledRecoveryManager(null, null, null, null, null, null, null);
    }

    /**
     * Test failed constructor due to null scheduled job factory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullScheduledJobFactory()
    {
        new ScheduledRecoveryManager(scheduler, null, null, null, null, null, null);
    }

    /**
     * Test failed constructor due to null flow name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullFlowName()
    {
        new ScheduledRecoveryManager(scheduler, scheduledJobFactory, null, null, null, null, null);
    }

    /**
     * Test failed constructor due to null module name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullModuleName()
    {
        new ScheduledRecoveryManager(scheduler, scheduledJobFactory, "flowName", null, null, null, null);
    }

    /**
     * Test failed constructor due to null consumer.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullConsumer()
    {
        new ScheduledRecoveryManager(scheduler, scheduledJobFactory, "flowName", "moduleName", null, null, null);
    }

    /**
     * Test failed constructor due to null exclusionService.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDieToNullExclusionService()
    {
        new ScheduledRecoveryManager(scheduler, scheduledJobFactory, "flowName", "moduleName", consumer, null, null);
    }

    /**
     * Test successful instantiation.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_instantiation_null_errorReportingService()
    {
        new ScheduledRecoveryManager(scheduler, scheduledJobFactory, "flowName", "moduleName", consumer, exclusionService, null);
    }

    /**
     * Test successful instantiation.
     */
    @Test
    public void test_successful_instantiation()
    {
        new ScheduledRecoveryManager(scheduler, scheduledJobFactory, "flowName", "moduleName", consumer, exclusionService, errorReportingService);
    }

    /**
     * Test successful stop action on recovery.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_recover_to_stopAction_with_no_previousAction_noManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(stopAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, stopAction.toString());
                will(returnValue("errorUri"));

                exactly(1).of(scheduler).isStarted();
                will(returnValue(false));
                exactly(1).of(consumer).stop();
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer);
        recoveryManager.setResolver(exceptionResolver);
        
        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("Stop", e.getMessage());
        }
        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful exclude action on recovery.
     * @throws SchedulerException
     */
    @Test
    public void test_successful_recover_to_excludeAction() throws SchedulerException
    {
        final Exception exception = new Exception();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowInvocationContext).getLastComponentName();
                will(returnValue("componentName"));

                exactly(1).of(flowInvocationContext).setFinalAction(FinalAction.EXCLUDE);

                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(excludeEventAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", flowEvent, exception, excludeEventAction.toString());
                will(returnValue("errorUri"));

                // add to exclusion list
                exactly(1).of(exclusionService).addBlacklisted("identifier", "errorUri");
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer);
        recoveryManager.setResolver(exceptionResolver);

        try
        {
            recoveryManager.recover(flowInvocationContext, exception, flowEvent, "identifier");
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("ExcludeEvent", e.getMessage());
        }
        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful stop action on recovery.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_recover_to_stopAction_with_no_previousAction_withManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(stopAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, stopAction.toString());
                will(returnValue("errorUri"));

                exactly(1).of(scheduler).isStarted();
                will(returnValue(false));
                exactly(1).of(consumer).stop();
                
                // stop managed resources
                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResource).stopManagedResource();
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);
        
        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("Stop", e.getMessage());
        }
        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful ignore action on recovery.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_recover_to_ignoreAction_with_no_previousAction_noManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        
        // expectations
        mockery.checking(new Expectations() {
            {
                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(ignoreAction));

//                exactly(1).of(errorReportingService).notify("componentName", exception, ignoreAction.toString());
//                will(returnValue("errorUri"));
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer);
        recoveryManager.setResolver(exceptionResolver);
        
        recoveryManager.recover("componentName", exception);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful ignore action on recovery.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_recover_to_ignoreAction_with_no_previousAction_withManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(ignoreAction));

                // report error
//                exactly(1).of(errorReportingService).notify("componentName", exception, ignoreAction.toString());
//                will(returnValue("errorUri"));
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);

        recoveryManager.recover("componentName", exception);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful retry action on recovery.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_recover_to_retryAction_with_no_previousAction_noManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        
        final RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(retryAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, retryAction.toString());
                will(returnValue("errorUri"));

                // firstly stop the consumer
                exactly(1).of(consumer).stop();

                // for this test we are not already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(false));
                
//                // so start the scheduler
//                exactly(1).of(scheduler).start();

                // create the recovery job and associated trigger
//                exactly(1).of(scheduledJobFactory).createJobDetail((Job)recoveryManager, "recoveryJob_flowName", "moduleName");
                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));
                
                exactly(2).of(retryAction).getMaxRetries();
                will(returnValue(2));
                exactly(1).of(retryAction).getDelay();
                will(returnValue(2000L));
//                exactly(1).of(trigger).setStartTime(with(any(Date.class)));
                
                // schedule the recovery job with its trigger
                exactly(1).of(scheduler).scheduleJob(jobDetail, trigger);

                // now we are in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
            }
        });

        recoveryManager.setResolver(exceptionResolver);

        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("Retry", e.getMessage());
        }

        Assert.assertTrue(recoveryManager.isRecovering());
        
        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager) recoveryManager).getRetryAttempts() == 1);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful retry action on recovery.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_recover_to_retryAction_with_no_previousAction_withManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        
        final RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer);
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(retryAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, retryAction.toString());
                will(returnValue("errorUri"));

                // firstly stop the consumer
                exactly(1).of(consumer).stop();

                // stop managed resources
                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResource).stopManagedResource();

                // for this test we are not already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(false));
                
//                // so start the scheduler
//                exactly(1).of(scheduler).start();

                // create the recovery job and associated trigger
//                exactly(1).of(scheduledJobFactory).createJobDetail((Job)recoveryManager, "recoveryJob_flowName", "moduleName");
                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));
                
                exactly(2).of(retryAction).getMaxRetries();
                will(returnValue(2));
                exactly(1).of(retryAction).getDelay();
                will(returnValue(2000L));
//                exactly(1).of(trigger).setStartTime(with(any(Date.class)));
                
                // schedule the recovery job with its trigger
                exactly(1).of(scheduler).scheduleJob(jobDetail, trigger);

                // now we are in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
            }
        });

        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);

        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("Retry", e.getMessage());
        }

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
    public void test_successful_recover_to_three_retryActions_until_exceeds_max_attempts_noManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        final long delay = 2000;
        final int maxRetries = 2;
        final JobKey jobKey = new JobKey("recoveryJob_flowName", "moduleName");
        final JobKey consumerJobKey = new JobKey("consumerRecoveryJob_flowName", "moduleName");

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

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, retryAction.toString());
                will(returnValue("errorUri"));

                // firstly stop the consumer
                exactly(1).of(consumer).stop();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
                
                exactly(2).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));
                
                // create the recovery job and associated trigger
                exactly(3).of(retryAction).getMaxRetries();
                will(returnValue(maxRetries));
                exactly(2).of(retryAction).getDelay();
                will(returnValue(delay));
                
                // schedule the recovery job with its trigger
                exactly(2).of(scheduler).scheduleJob(jobDetail, trigger);

                //
                // second time retry action is invoked
                // 
                
                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(retryAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, retryAction.toString());
                will(returnValue("errorUri"));

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

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, retryAction.toString());
                will(returnValue("errorUri"));

                // stop the consumer
                exactly(1).of(consumer).stop();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
                
                // is recovery job already scheduled
                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));
                exactly(1).of(scheduler).checkExists(jobKey);
                will(returnValue(Boolean.FALSE));

                // check we have not exceeded retry limits
                exactly(1).of(retryAction).getMaxRetries();
                will(returnValue(maxRetries));
                
                // cancel the recovery
                exactly(1).of(scheduler).deleteJob(jobKey);
                exactly(1).of(scheduler).checkExists(consumerJobKey);
                will(returnValue(Boolean.TRUE));
                exactly(1).of(scheduler).deleteJob(consumerJobKey);
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer);
        recoveryManager.setResolver(exceptionResolver);

        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("Retry", e.getMessage());
        }

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 1);

        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("Retry", e.getMessage());
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
     * Test successful three consecutive retry actions with the last one
     * exceeding the maximum attempts limit.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_recover_to_three_retryActions_until_exceeds_max_attempts_withManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        final long delay = 2000;
        final int maxRetries = 2;
        final JobKey jobKey = new JobKey("recoveryJob_flowName", "moduleName");
        final JobKey consumerJobKey = new JobKey("consumerRecoveryJob_flowName", "moduleName");
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);

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

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, retryAction.toString());
                will(returnValue("errorUri"));

                // firstly stop the consumer
                exactly(1).of(consumer).stop();

                // stop managed resources
                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResource).stopManagedResource();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
                
                exactly(2).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));
                
                // create the recovery job and associated trigger
                exactly(3).of(retryAction).getMaxRetries();
                will(returnValue(maxRetries));
                exactly(2).of(retryAction).getDelay();
                will(returnValue(delay));
                
                // schedule the recovery job with its trigger
                exactly(2).of(scheduler).scheduleJob(jobDetail, trigger);

                //
                // second time retry action is invoked
                // 
                
                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(retryAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, retryAction.toString());
                will(returnValue("errorUri"));

                // stop the consumer
                exactly(1).of(consumer).stop();

                // stop managed resources
                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResource).stopManagedResource();

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

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, retryAction.toString());
                will(returnValue("errorUri"));

                // stop the consumer
                exactly(1).of(consumer).stop();

                // stop managed resources
                exactly(1).of(flowElement).getFlowComponent();
                will(returnValue(managedResource));
                exactly(1).of(managedResource).stopManagedResource();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
                
                // is recovery job already scheduled
                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));
                exactly(1).of(scheduler).checkExists(jobKey);
                will(returnValue(Boolean.FALSE));

                // check we have not exceeded retry limits
                exactly(1).of(retryAction).getMaxRetries();
                will(returnValue(maxRetries));
                
                // cancel the recovery
                exactly(1).of(scheduler).deleteJob(jobKey);
                exactly(1).of(scheduler).checkExists(consumerJobKey);
                will(returnValue(Boolean.TRUE));
                exactly(1).of(scheduler).deleteJob(consumerJobKey);
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);
        
        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("Retry", e.getMessage());
        }

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 1);

        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("Retry", e.getMessage());
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

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, unsupportedExceptionAction.toString());
                will(returnValue("errorUri"));
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName", consumer);
        recoveryManager.setResolver(exceptionResolver);
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

        public StubbedScheduledRecoveryManager(Scheduler scheduler, String flowName, String moduleName, Consumer consumer)
        {
            super(scheduler, scheduledJobFactory, flowName, moduleName, consumer, exclusionService, errorReportingService);
        }
        
        @Override
        protected Trigger newRecoveryTrigger(long delay)
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
