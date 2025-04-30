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
import org.ikasan.spec.component.IsConsumerAware;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.IsErrorReportingServiceAware;
import org.ikasan.spec.event.ForceTransactionRollbackException;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.exclusion.IsExclusionServiceAware;
import org.ikasan.spec.flow.*;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.recovery.RecoveryManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This test class supports the <code>ScheduledRecoveryManager</code> class.
 * 
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
public class ScheduledRecoveryManagerTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
        }
    };
    
    /** Mock consumer flowElement */
    private final Consumer consumer = mockery.mock(Consumer.class, "mockConsumer");

    private final Broker broker = mockery.mock(Broker.class, "mockBroker");

    /** Mock exception resolver */
    private final ExceptionResolver exceptionResolver = mockery.mock(ExceptionResolver.class, "mockExceptionResolver");

    /** Mock scheduler */
    private final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");

    /** Mock scheduledJobFactory */
    private final ScheduledJobFactory scheduledJobFactory = mockery.mock(ScheduledJobFactory.class, "mockScheduledJobFactory");

    /** Mock recovery job detail */
    private final JobDetail jobDetail = mockery.mock(JobDetail.class, "mockJobDetail");

    /** Mock recovery job trigger */
    private final Trigger trigger = mockery.mock(Trigger.class, "mockTrigger");

    /** Mock stopAction */
    private final StopAction stopAction = mockery.mock(StopAction.class, "Stop");
    
    /** Mock retryAction */
    private final RetryAction retryAction = mockery.mock(RetryAction.class, "Retry");

    /** Mock retryAction */
    private final ScheduledRetryAction scheduledRetryAction = mockery.mock(ScheduledRetryAction.class, "scheduledRetryAction");
    
    /** Mock excludeEventAction */
    private final ExcludeEventAction excludeEventAction = mockery.mock(ExcludeEventAction.class, "ExcludeEvent");
    
    /** Mock ignoreAction */
    private final IgnoreAction ignoreAction = mockery.mock(IgnoreAction.class, "Ignore");
    
    /** Mock flowElement */
    private final FlowElement flowElement = mockery.mock(FlowElement.class, "FlowElement");
    
    /** Mock managedResource */
    private final ManagedResource managedResource = mockery.mock(ManagedResource.class, "ManagedResource");

    /** Mock exclusion service */
    private final ExclusionService exclusionService = mockery.mock(ExclusionService.class, "mockExclusionService");

    /** Mock error reporting service */
    private final ErrorReportingService errorReportingService = mockery.mock(ErrorReportingService.class, "mockErrorReportingService");

    /** Mock flowEvent */
    private final FlowEvent flowEvent = mockery.mock(FlowEvent.class, "mockFlowEvent");

    /** Mock flowInvocationContext*/
    private final FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "flowInvocationContext");

    /** Mock jobExecutionContext*/
    private final JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class, "jobExecutionContext");

    /** Represents a flow object that has been mocked for testing purposes. */
    private final Flow flow = mockery.mock(Flow.class, "flow");

    /**
     * Test failed constructor due to null scheduler.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullScheduler()
    {
        new ScheduledRecoveryManager(null, null, null, null);
    }

    /**
     * Test failed constructor due to null scheduled job factory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullScheduledJobFactory()
    {
        new ScheduledRecoveryManager(scheduler, null, null, null);
    }

    /**
     * Test failed constructor due to null flow name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullFlowName()
    {
        new ScheduledRecoveryManager(scheduler, scheduledJobFactory, null, null);
    }

    /**
     * Test failed constructor due to null module name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullModuleName()
    {
        new ScheduledRecoveryManager(scheduler, scheduledJobFactory, "flowName", null);
    }

    /**
     * Test successful constructor due to null consumer.
     */
    @Test
    public void test_successful_constructor()
    {
        new ScheduledRecoveryManager(scheduler, scheduledJobFactory, "flowName", "moduleName");
    }

    /**
     * Test is consumer aware.
     */
    @Test
    public void test_isConsumerAware()
    {
        ScheduledRecoveryManager scheduledRecoveryManager = new ScheduledRecoveryManager(scheduler, scheduledJobFactory, "flowName", "moduleName");
        Assert.assertFalse( scheduledRecoveryManager instanceof IsConsumerAware);
    }

    /**
     * Test is flow aware.
     */
    @Test
    public void test_isFlowAware()
    {
        ScheduledRecoveryManager scheduledRecoveryManager = new ScheduledRecoveryManager(scheduler, scheduledJobFactory, "flowName", "moduleName");
        Assert.assertTrue( scheduledRecoveryManager instanceof IsFlowAware);
    }

    /**
     * Test successful instantiation.
     */
    @Test
    public void test_isErrorReportingServiceAware()
    {
        ScheduledRecoveryManager scheduledRecoveryManager = new ScheduledRecoveryManager(scheduler, scheduledJobFactory, "flowName", "moduleName");
        Assert.assertTrue( scheduledRecoveryManager instanceof IsErrorReportingServiceAware);
    }


    /**
     * Test successful instantiation.
     */
    @Test
    public void test_isExclusionServiceAware()
    {
        ScheduledRecoveryManager scheduledRecoveryManager = new ScheduledRecoveryManager(scheduler, scheduledJobFactory, "flowName", "moduleName");
        Assert.assertTrue( scheduledRecoveryManager instanceof IsExclusionServiceAware);
    }


    /**
     * Test successful instantiation.
     */
    @Test
    public void test_successful_instantiation()
    {
        new ScheduledRecoveryManager(scheduler, scheduledJobFactory, "flowName", "moduleName");
    }

    /**
     * Test we can call cancelAll on the recovery manager even if no recovery jobs are in progress
     * @throws SchedulerException if the scheduler setup fails
     */
    @Test
    public void test_cancel_no_jobs() throws SchedulerException
    {
        mockery.checking(new Expectations() {
             {
                 exactly(1).of(flow).isMultiThreadedCapable();
                 will(returnValue(false));
             }
         });

        System.setProperty("org.quartz.scheduler.skipUpdateCheck", "true");
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        ScheduledRecoveryManager scheduledRecoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flow", "module");
        setIsAware(scheduledRecoveryManager);
        scheduledRecoveryManager.cancelAll();
        Assert.assertTrue("cancelAll called with no jobs", true);
    }

    /**
     * Test successful stop action on recovery.
     * @throws SchedulerException if the scheduler fails
     */
    @Test
    public void test_successful_recover_to_stopAction_with_no_previousAction_noManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(stopAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, stopAction.toString());
                will(returnValue("errorUri"));

                exactly(1).of(scheduler).isStarted();
                will(returnValue(false));
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);
        
        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("Stop", e.getMessage());
        }

        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());
        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful exclude action on recovery.
     * @throws SchedulerException if the scheduler fails
     */
    @Test
    public void test_successful_recover_to_excludeAction() throws SchedulerException
    {
        final Exception exception = new Exception();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                exactly(1).of(flowInvocationContext).getLastComponentName();
                will(returnValue("componentName"));

                exactly(1).of(flowInvocationContext).setFinalAction(FinalAction.EXCLUDE);

                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(excludeEventAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", flowEvent, exception, excludeEventAction.toString());
                will(returnValue("errorUri"));

                exactly(1).of(flowInvocationContext).setErrorUri("errorUri");

                // add to exclusion list
                exactly(1).of(exclusionService).addBlacklisted("identifier", "errorUri", flowInvocationContext);
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
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
        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful stop action on recovery.
     * @throws SchedulerException if the scheduler fails
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
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(stopAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, stopAction.toString());
                will(returnValue("errorUri"));

                exactly(1).of(scheduler).isStarted();
                will(returnValue(false));
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
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
        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful ignore action on recovery.
     * @throws SchedulerException if the scheduler fails
     */
    @Test
    public void test_successful_recover_to_ignoreAction_with_no_previousAction_noManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        
        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(ignoreAction));
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);
        
        recoveryManager.recover("componentName", exception);

        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful ignore action on recovery.
     * @throws SchedulerException if the scheduler fails
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
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(ignoreAction));
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);

        recoveryManager.recover("componentName", exception);

        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful retry action on recovery.
     * @throws SchedulerException if the scheduler fails
     */
    @Test
    public void test_successful_recover_to_retryAction_with_no_previousAction_noManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");

        final RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(retryAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, retryAction.toString());
                will(returnValue("errorUri"));

                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));

                // for this test we are not already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(false));

                exactly(1).of(scheduler).checkExists(with(any(JobKey.class)));
                will(returnValue(false));

                // create the recovery job and associated trigger
                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));

                exactly(2).of(retryAction).getMaxRetries();
                will(returnValue(2));
                exactly(1).of(retryAction).getDelay();
                will(returnValue(2000L));
                
                // schedule the recovery job with its trigger
                exactly(1).of(scheduler).scheduleJob(jobDetail, trigger);
                exactly(1).of(scheduler).getJobKeys(with(any(GroupMatcher.class)));
                will(returnValue(Set.of(jobKey)));

                // now we are in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
            }
        });

        setIsAware(recoveryManager);
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
        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager) recoveryManager).getRetryAttempts() == 1);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful retry action on recovery.
     * @throws SchedulerException if the scheduler fails
     */
    @Test
    public void test_successful_recover_to_scheduledRetryAction_with_no_previousAction_noManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");

        final RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(scheduledRetryAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, scheduledRetryAction.toString());
                will(returnValue("errorUri"));

                // call recovery stop on flow
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));

                // for this test we are not already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(false));

                // create the recovery job and associated trigger
                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));

                exactly(2).of(scheduledRetryAction).getCronExpression();
                will(returnValue("0/30 * * * * ? *"));

                exactly(2).of(scheduledRetryAction).getMaxRetries();
                will(returnValue(2));

                exactly(1).of(scheduler).checkExists(with(any(JobKey.class)));
                will(returnValue(false));

                // schedule the recovery job with its trigger
                exactly(1).of(scheduler).scheduleJob(with(any(JobDetail.class)), with(any(CronTriggerImpl.class)));
                exactly(1).of(scheduler).getJobKeys(with(any(GroupMatcher.class)));
                will(returnValue(Set.of(jobKey)));

                // now we are in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
            }
        });

        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);

        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("scheduledRetryAction", e.getMessage());
        }

        Assert.assertTrue(recoveryManager.isRecovering());
        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager) recoveryManager).getRetryAttempts() == 1);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful retry action on recovery.
     * @throws SchedulerException if the scheduler fails
     */
    @Test
    public void test_successful_recover_to_retryAction_with_no_previousAction_withManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        
        final RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(retryAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, retryAction.toString());
                will(returnValue("errorUri"));

                // firstly stop the consumer
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                // for this test we are not already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(false));

                exactly(1).of(scheduler).checkExists(with(any(JobKey.class)));
                will(returnValue(false));

                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));

                // create the recovery job and associated trigger
                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));

                exactly(2).of(retryAction).getMaxRetries();
                will(returnValue(2));
                exactly(1).of(retryAction).getDelay();
                will(returnValue(2000L));
                
                // schedule the recovery job with its trigger
                exactly(1).of(scheduler).scheduleJob(jobDetail, trigger);
                exactly(1).of(scheduler).getJobKeys(with(any(GroupMatcher.class)));
                will(returnValue(Set.of(jobKey)));

                // now we are in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
            }
        });

        setIsAware(recoveryManager);
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
        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());
        
        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 1);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful retry action on recovery.
     * @throws SchedulerException if the scheduler fails
     */
    @Test
    public void test_successful_recover_to_scheduledRetryAction_with_no_previousAction_withManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();

        final RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(scheduledRetryAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, scheduledRetryAction.toString());
                will(returnValue("errorUri"));

                // firstly stop the consumer
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                // for this test we are not already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(false));

                exactly(1).of(scheduler).checkExists(with(any(JobKey.class)));
                will(returnValue(false));

                // create the recovery job and associated trigger
                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));

                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));

                exactly(2).of(scheduledRetryAction).getCronExpression();
                will(returnValue("0/30 * * * * ? *"));

                exactly(2).of(scheduledRetryAction).getMaxRetries();
                will(returnValue(2));

                // schedule the recovery job with its trigger
                exactly(1).of(scheduler).scheduleJob(with(any(JobDetail.class)), with(any(CronTriggerImpl.class)));
                exactly(1).of(scheduler).getJobKeys(with(any(GroupMatcher.class)));
                will(returnValue(Set.of(jobKey)));

                // now we are in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
            }
        });

        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);

        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("scheduledRetryAction", e.getMessage());
        }

        Assert.assertTrue(recoveryManager.isRecovering());
        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 1);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful three consecutive retry actions with the last one
     * exceeding the maximum attempts limit.
     * @throws SchedulerException if the scheduler fails
     */
    @Test
    public void test_successful_recover_to_three_retryActions_until_exceeds_max_attempts_noManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        final long delay = 2000;
        final int maxRetries = 2;
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));
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
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
                
                exactly(3).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));
                
                // create the recovery job and associated trigger
                exactly(3).of(retryAction).getMaxRetries();
                will(returnValue(maxRetries));
                exactly(3).of(retryAction).getDelay();
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
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

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
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
                
                // is recovery job already scheduled
                exactly(3).of(jobDetail).getKey();
                will(returnValue(jobKey));
                exactly(2).of(scheduler).getJobKeys(with(any(GroupMatcher.class)));
                will(returnValue(Set.of(jobKey)));

                // check we have not exceeded retry limits
                exactly(1).of(retryAction).getMaxRetries();
                will(returnValue(maxRetries));

                exactly(1).of(scheduler).getTriggersOfJob(with(any(JobKey.class)));
                will(returnValue(List.of()));

                exactly(3).of(scheduler).checkExists(jobKey);
                will(returnValue(false));

                // cancelAll the recovery
                exactly(3).of(scheduler).checkExists(jobKey);
                will(returnValue(true));
                exactly(1).of(scheduler).deleteJob(jobKey);
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
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
        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful three consecutive retry actions with the last one
     * exceeding the maximum attempts limit.
     * @throws SchedulerException if the scheduler fails
     */
    @Test
    public void test_successful_recover_to_three_retryActions_until_exceeds_max_attempts_withManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        final long delay = 2000;
        final int maxRetries = 2;
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

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
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));

                exactly(3).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));
                
                // create the recovery job and associated trigger
                exactly(3).of(retryAction).getMaxRetries();
                will(returnValue(maxRetries));
                exactly(3).of(retryAction).getDelay();
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
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

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
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
                
                // is recovery job already scheduled
                exactly(3).of(jobDetail).getKey();
                will(returnValue(jobKey));
                exactly(2).of(scheduler).getJobKeys(with(any(GroupMatcher.class)));
                will(returnValue(Set.of(jobKey)));

                // check we have not exceeded retry limits
                exactly(1).of(retryAction).getMaxRetries();
                will(returnValue(maxRetries));

                exactly(1).of(scheduler).getTriggersOfJob(with(any(JobKey.class)));
                will(returnValue(List.of()));

                exactly(3).of(scheduler).checkExists(jobKey);
                will(returnValue(false));
                
                // cancelAll the recovery
                exactly(3).of(scheduler).checkExists(jobKey);
                will(returnValue(true));
                exactly(1).of(scheduler).deleteJob(jobKey);
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
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
        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful three consecutive retry actions with the last one
     * exceeding the maximum attempts limit.
     * @throws SchedulerException if the scheduler fails
     */
    @Test
    public void test_successful_recover_to_three_scheduledRetryActions_until_exceeds_max_attempts_withManagedResources() throws SchedulerException
    {
        final Exception exception = new Exception();
        final int maxRetries = 2;
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                //
                // first time retry action is invoked
                //

                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(scheduledRetryAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, scheduledRetryAction.toString());
                will(returnValue("errorUri"));

                // firstly stop the consumer
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));

                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));

                // create the recovery job and associated trigger
                exactly(3).of(scheduledRetryAction).getMaxRetries();
                will(returnValue(maxRetries));

                // schedule the recovery job with its trigger
                exactly(1).of(scheduler).scheduleJob(with(any(JobDetail.class)), with(any(CronTriggerImpl.class)));

                //
                // second time retry action is invoked
                //

                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(scheduledRetryAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, scheduledRetryAction.toString());
                will(returnValue("errorUri"));

                // stop the consumer
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));

                exactly(2).of(scheduledRetryAction).getCronExpression();
                will(returnValue("0/30 * * * * ? *"));

                // check we have not exceeded retry limits
                exactly(2).of(scheduledRetryAction).getMaxRetries();
                will(returnValue(maxRetries));

                //
                // third time retry action is invoked
                //

                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(scheduledRetryAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, scheduledRetryAction.toString());
                will(returnValue("errorUri"));

                // stop the consumer
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));

                exactly(1).of(scheduler).checkExists(with(any(JobKey.class)));
                will(returnValue(false));

                // is recovery job already scheduled
                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));
                exactly(1).of(scheduler).getJobKeys(with(any(GroupMatcher.class)));
                will(returnValue(Set.of(jobKey)));

                // check we have not exceeded retry limits
                exactly(1).of(scheduledRetryAction).getMaxRetries();
                will(returnValue(maxRetries));

                exactly(1).of(scheduler).checkExists(jobKey);
                will(returnValue(false));
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);

        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("scheduledRetryAction", e.getMessage());
        }

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 1);

        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("scheduledRetryAction", e.getMessage());
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
        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 0);

        mockery.assertIsSatisfied();
    }

    /**
     * Test failed recovery due to unsupported recovery action.
     */
    @Test
    public void test_failed_recover_due_to_unsupported_recovery_action() throws SchedulerException
    {
        final Exception exception = new Exception();
        
        final ExceptionAction unsupportedExceptionAction = new UnsupportedExceptionAction();
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(unsupportedExceptionAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, unsupportedExceptionAction.toString());
                will(returnValue("errorUri"));

                exactly(1).of(scheduler).isStarted();
                will(returnValue(false));
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);

        try
        {
            recoveryManager.recover("componentName", exception);
        }
        catch(RuntimeException e)
        {
            Assert.assertEquals("UnsupportedExceptionAction", e.getMessage());
        }

        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 0);
        mockery.assertIsSatisfied();
    }

    /**
     * This method tests the successful recovery process by retrying actions until the maximum retry attempts are exceeded.
     * The recovery job is already scheduled in the future, so it will not reschedule a future job.
     * @throws SchedulerException if an error occurs with the scheduler
     */
    @Test
    public void test_successful_recover_to_three_retryActions_until_exceeds_max_attempts_recovery_job_already_scheduled_in_future_so_will_not_reschedule_future_job() throws SchedulerException
    {
        final Exception exception = new Exception();
        final long delay = 2000;
        final int maxRetries = 2;
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final JobKey consumerJobKey = new JobKey("consumerRecoveryJob_flowName" + 0, "moduleName");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                //
                // first time retry action is invoked
                //

                // resolve the component name and exception to an action
                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(retryAction));

                // report error
                exactly(1).of(errorReportingService).notify("componentName", exception, retryAction.toString());
                will(returnValue("errorUri"));

                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));

                exactly(3).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));

                // create the recovery job and associated trigger
                exactly(3).of(retryAction).getMaxRetries();
                will(returnValue(maxRetries));
                exactly(3).of(retryAction).getDelay();
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

                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));

                // check we have not exceeded retry limits
                exactly(5).of(retryAction).getMaxRetries();
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
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));

                // is recovery job already scheduled
                exactly(3).of(jobDetail).getKey();
                will(returnValue(jobKey));
                exactly(2).of(scheduler).getJobKeys(with(any(GroupMatcher.class)));
                will(returnValue(Set.of(jobKey)));

                exactly(1).of(scheduler).getTriggersOfJob(with(any(JobKey.class)));
                will(returnValue(List.of(trigger)));

                exactly(3).of(scheduler).checkExists(with(any(JobKey.class)));
                will(returnValue(false));

                exactly(3).of(scheduler).checkExists(with(any(JobKey.class)));
                will(returnValue(true));

                exactly(1).of(scheduler).deleteJob(jobKey);
            }
        });

        RecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
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
        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 0);

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_recover_to_three_retryActions_until_exceeds_max_attempts_recovery_job_already_scheduled_in_past_so_will_reschedule_future_job() throws SchedulerException
    {
        final Exception exception = new Exception();
        final long delay = 2000;
        final int maxRetries = 2;
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final JobKey consumerJobKey = new JobKey("consumerRecoveryJob_flowName" + 0, "moduleName");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));
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
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

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
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));

                // check we have not exceeded retry limits
                exactly(3).of(retryAction).getMaxRetries();
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
                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                // for this test we are already in a recovery
                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));

                // is recovery job already scheduled
                exactly(2).of(jobDetail).getKey();
                will(returnValue(jobKey));
                exactly(2).of(scheduler).getJobKeys(with(any(GroupMatcher.class)));
                will(returnValue(Set.of(jobKey)));

                // check we have not exceeded retry limits
                exactly(2).of(retryAction).getMaxRetries();
                will(returnValue(maxRetries));

                // There is a job for the job key registered with the scheduler but in the past.
                exactly(1).of(scheduler).getTriggersOfJob(with(any(JobKey.class)));
                will(returnValue(List.of(trigger)));

                exactly(4).of(scheduler).checkExists(jobKey);
                will(returnValue(false));

                // cancelAll the recovery
                exactly(2).of(scheduler).checkExists(jobKey);
                will(returnValue(true));
                exactly(1).of(scheduler).deleteJob(jobKey);
            }
        });

        ScheduledRecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
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
        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertTrue(((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts() == 0);

        mockery.assertIsSatisfied();
    }


    /**
     * Test that a quartz job fired with managed components allows the consumer to start successfully.
     *
     * @throws SchedulerException if there is an issue with the scheduler
     */
    @Test
    public void test_successful_recovery_quartz_job_fired_with_managed_components_consumer_successfully_starts() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                exactly(1).of(flow).startFlowDueToRecoveryAttempt();

                exactly(2).of(scheduler).checkExists(jobKey);
                will(returnValue(true));

                exactly(1).of(scheduler).deleteJob(jobKey);
            }
        });

        ScheduledRecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);

        // We need to use reflection to set a job key on the recovery manager.
        ReflectionTestUtils.setField(recoveryManager, "recoveryJobKey", jobKey);

        recoveryManager.execute(this.jobExecutionContext);

        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertEquals(0,((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts());

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_recovery_quartz_job_fired_with_managed_components_consumer_successfully_starts_multi_threaded_consumer() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                exactly(1).of(flow).startFlowDueToRecoveryAttempt();

                exactly(1).of(scheduler).checkExists(jobKey);
                will(returnValue(true));

                exactly(2).of(scheduler).getJobKeys(with(any(GroupMatcher.class)));
                will(returnValue(Set.of(jobKey)));

                exactly(1).of(scheduler).deleteJob(jobKey);
            }
        });

        ScheduledRecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);

        // We need to use reflection to set a job key on the recovery manager.
        ReflectionTestUtils.setField(recoveryManager, "recoveryJobKey", jobKey);
        ReflectionTestUtils.setField(recoveryManager, "isConsumerMultiThreaded", true);

        recoveryManager.execute(this.jobExecutionContext);

        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertEquals(0,((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts());

        mockery.assertIsSatisfied();
    }

    /**
     * Test method for verifying the behavior of a Quartz job recovery process with managed components
     * where the consumer throws an exception during startup.
     *
     * @throws SchedulerException if an issue occurs with the scheduler
     */
    @Test
    public void test_successful_recovery_quartz_job_fired_with_managed_components_consumer_managed_resource_throws_exception_on_startup_managed_resource_critical_on_startup() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);
        RuntimeException exception =  new RuntimeException("error!");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                exactly(1).of(flow).startFlowDueToRecoveryAttempt();
                will(throwException(exception));

                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(stopAction));

                exactly(1).of(errorReportingService).notify("componentName", exception, "Stop");

                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
            }
        });

        ScheduledRecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);

        // We need to use reflection to set a job key on the recovery manager.
        ReflectionTestUtils.setField(recoveryManager, "recoveryJobKey", jobKey);
        ReflectionTestUtils.setField(recoveryManager, "previousComponentName", "componentName");

        try {
            recoveryManager.execute(this.jobExecutionContext);
        }
        catch (Exception e) {
            Assert.assertTrue(e instanceof ForceTransactionRollbackException);
        }

        Assert.assertTrue(recoveryManager.isFlowStartBasedRecovery());
        // test aspects we cannot access through the interface
        Assert.assertEquals(0,((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts());

        mockery.assertIsSatisfied();
    }

    /**
     * Test method for verifying the behavior of a Quartz job recovery process with managed components
     * where the consumer throws an exception during startup.
     *
     * @throws SchedulerException if an issue occurs with the scheduler
     */
    @Test
    public void test_successful_recovery_quartz_job_fired_with_managed_components_consumer_managed_resource_throws_exception_on_startup_managed_resource_critical_on_startup_multi_threaded_consumer() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);
        RuntimeException exception =  new RuntimeException("error!");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(true));

                // call recovery start on flow
                exactly(1).of(flow).startFlowDueToRecoveryAttempt();
                will(throwException(exception));

                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(stopAction));

                exactly(1).of(errorReportingService).notify("componentName", exception, "Stop");

                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
            }
        });

        ScheduledRecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);

        // We need to use reflection to set a job key on the recovery manager.
        ReflectionTestUtils.setField(recoveryManager, "recoveryJobKey", jobKey);
        ReflectionTestUtils.setField(recoveryManager, "previousComponentName", "componentName");
        ReflectionTestUtils.setField(recoveryManager, "isConsumerMultiThreaded", true);

        try {
            recoveryManager.execute(this.jobExecutionContext);
        }
        catch (Exception e) {
            Assert.assertTrue(e instanceof ForceTransactionRollbackException);
        }

        Assert.assertTrue(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertEquals(0,((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts());

        mockery.assertIsSatisfied();
    }

    /**
     * Test method for verifying the behavior of a Quartz job recovery process with managed components
     * where the consumer throws an exception during startup. The managed resource is not critical on startup.
     *
     * @throws SchedulerException if there is an issue with the scheduler
     */
    @Test
    public void test_successful_recovery_quartz_job_fired_with_managed_components_consumer_managed_resource_throws_exception_on_startup_managed_resource_NOT_critical_on_startup() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);
        RuntimeException exception =  new RuntimeException("error!");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                // exceptions will be behind recoveryStart method call and will not bubble up
                // if managed component not critical.
                exactly(1).of(flow).startFlowDueToRecoveryAttempt();

                exactly(2).of(scheduler).checkExists(jobKey);
                will(returnValue(true));

                exactly(1).of(scheduler).deleteJob(jobKey);
            }
        });

        ScheduledRecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);

        // We need to use reflection to set a job key on the recovery manager.
        ReflectionTestUtils.setField(recoveryManager, "recoveryJobKey", jobKey);
        ReflectionTestUtils.setField(recoveryManager, "previousComponentName", "componentName");

        try {
            recoveryManager.execute(this.jobExecutionContext);
        }
        catch (Exception e) {
            Assert.assertTrue(e instanceof ForceTransactionRollbackException);
        }

        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertEquals(0,((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts());

        mockery.assertIsSatisfied();
    }

    /**
     * Test method for verifying the behavior of a Quartz job recovery process with managed components
     * where the consumer throws an exception during startup. The managed resource is not critical on startup.
     *
     * @throws SchedulerException if there is an issue with the scheduler
     */
    @Test
    public void test_successful_recovery_quartz_job_fired_with_managed_components_consumer_managed_resource_throws_exception_on_startup_managed_resource_NOT_critical_on_startup_multi_threaded_consumer() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);
        RuntimeException exception =  new RuntimeException("error!");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));
                // no exception thrown on start because it is hidden behind interface
                // and will only bubble up if critical
                exactly(1).of(flow).startFlowDueToRecoveryAttempt();

                exactly(2).of(scheduler).getJobKeys(with(any(GroupMatcher.class)));
                will(returnValue(Set.of(jobKey)));

                exactly(1).of(scheduler).checkExists(jobKey);
                will(returnValue(true));

                exactly(1).of(scheduler).deleteJob(jobKey);
            }
        });

        ScheduledRecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);

        // We need to use reflection to set a job key on the recovery manager.
        ReflectionTestUtils.setField(recoveryManager, "recoveryJobKey", jobKey);
        ReflectionTestUtils.setField(recoveryManager, "previousComponentName", "componentName");
        ReflectionTestUtils.setField(recoveryManager, "isConsumerMultiThreaded", true);


        try {
            recoveryManager.execute(this.jobExecutionContext);
        }
        catch (Exception e) {
            Assert.assertTrue(e instanceof ForceTransactionRollbackException);
        }

        Assert.assertFalse(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertEquals(0,((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts());

        mockery.assertIsSatisfied();
    }


    /**
     * This method tests the successful recovery process by simulating a Quartz job firing with managed components.
     * It verifies that the consumer throws a retry exception upon startup.
     *
     * @throws SchedulerException if there is an issue with the scheduler
     */
    @Test
    public void test_successful_recovery_quartz_job_fired_with_managed_components_consumer_throws_retry_exception_on_startup() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);

        EndpointException exception = new EndpointException("error!");
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(true));

                exactly(1).of(flow).startFlowDueToRecoveryAttempt();
                will(throwException(exception));

                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(retryAction));

                exactly(1).of(errorReportingService).notify("componentName", exception, "Retry");

                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));

                exactly(1).of(scheduler).checkExists(with(any(JobKey.class)));
                will(returnValue(false));

                exactly(1).of(scheduler).getJobKeys(with(any(GroupMatcher.class)));
                will(returnValue(Set.of(jobKey)));

                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));

                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));

                exactly(1).of(retryAction).getDelay();
                will(returnValue(10000L));

                exactly(1).of(scheduler).scheduleJob(jobDetail, trigger);

                exactly(1).of(retryAction).getMaxRetries();
                will(returnValue(-1));
            }
        });

        ScheduledRecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);

        // We need to use reflection to set a job key on the recovery manager.
        ReflectionTestUtils.setField(recoveryManager, "recoveryJobKey", jobKey);
        ReflectionTestUtils.setField(recoveryManager, "previousComponentName", "componentName");

        try {
            recoveryManager.execute(this.jobExecutionContext);
        }
        catch (Exception e) {
            Assert.assertTrue(e instanceof ForceTransactionRollbackException);
        }

        Assert.assertTrue(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertEquals(1,((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts());

        mockery.assertIsSatisfied();
    }

    /**
     * Test the successful recovery of a Quartz job fired with managed components where the consumer throws a retry exception on startup in a multi-threaded consumer.
     * The test sets up expectations using a mocking framework for certain interactions with managed resources, consumers, exception handling, error reporting, and scheduler.
     * It then creates a StubbedScheduledRecoveryManager instance with the specified scheduler, flow name, and module name.
     * The test executes the recovery process by invoking the execute method on the recovery manager and expects a ForceTransactionRollbackException to be thrown.
     * Finally, it asserts on the number of retry attempts made and ensures that all expectations set up in the mocking framework are satisfied.
     * @throws SchedulerException if an error occurs while interacting with the scheduler
     */
    @Test
    public void test_successful_recovery_quartz_job_fired_with_managed_components_consumer_throws_retry_exception_on_startup_multi_threaded_consumer() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);

        EndpointException exception = new EndpointException("error!");
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                exactly(1).of(flow).startFlowDueToRecoveryAttempt();
                will(throwException(exception));

                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(retryAction));

                exactly(1).of(errorReportingService).notify("componentName", exception, "Retry");

                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));

                exactly(1).of(scheduler).checkExists(with(any(JobKey.class)));
                will(returnValue(false));

                exactly(1).of(scheduler).getJobKeys(with(any(GroupMatcher.class)));
                will(returnValue(Set.of(jobKey)));

                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));

                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));

                exactly(1).of(retryAction).getDelay();
                will(returnValue(10000L));

                exactly(1).of(scheduler).scheduleJob(jobDetail, trigger);

                exactly(1).of(retryAction).getMaxRetries();
                will(returnValue(-1));
            }
        });

        ScheduledRecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);

        // We need to use reflection to set a job key on the recovery manager.
        ReflectionTestUtils.setField(recoveryManager, "recoveryJobKey", jobKey);
        ReflectionTestUtils.setField(recoveryManager, "previousComponentName", "componentName");
        ReflectionTestUtils.setField(recoveryManager, "isConsumerMultiThreaded", true);

        try {
            recoveryManager.execute(this.jobExecutionContext);
        }
        catch (Exception e) {
            Assert.assertTrue(e instanceof ForceTransactionRollbackException);
        }

        Assert.assertTrue(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertEquals(1,((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts());

        mockery.assertIsSatisfied();
    }


    /**
     * Test method for simulating a Quartz job firing with managed components where the consumer throws an exception causing the flow to stop.
     * Verifies the handling of the exception by the recovery manager, error reporting, and stopping of the flow.
     *
     * @throws SchedulerException if there is an issue with the scheduler setup
     */
    @Test
    public void test_successful_recovery_quartz_job_fired_with_managed_components_consumer_throws_exception_causing_flow_to_stop() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);

        RuntimeException exception = new RuntimeException("error!");
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                exactly(1).of(flow).startFlowDueToRecoveryAttempt();
                will(throwException(exception));

                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(stopAction));

                exactly(1).of(errorReportingService).notify("componentName", exception, "Stop");

                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
            }
        });

        ScheduledRecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);

        // We need to use reflection to set a job key on the recovery manager.
        ReflectionTestUtils.setField(recoveryManager, "recoveryJobKey", jobKey);
        ReflectionTestUtils.setField(recoveryManager, "previousComponentName", "componentName");

        try {
            recoveryManager.execute(this.jobExecutionContext);
        }
        catch (Exception e) {
            Assert.assertTrue(e instanceof ForceTransactionRollbackException);
        }

        Assert.assertTrue(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertEquals(0,((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts());

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_successful_recovery_quartz_job_fired_with_managed_components_consumer_throws_exception_causing_flow_to_stop_with_multi_threaded_consumer() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("recoveryJob_flowName" + 0, "moduleName");
        final List managedResources = new ArrayList();
        managedResources.add(flowElement);

        RuntimeException exception = new RuntimeException("error!");
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flow).isMultiThreadedCapable();
                will(returnValue(false));

                exactly(1).of(flow).startFlowDueToRecoveryAttempt();
                will(throwException(exception));

                exactly(1).of(exceptionResolver).resolve("componentName", exception);
                will(returnValue(stopAction));

                exactly(1).of(errorReportingService).notify("componentName", exception, "Stop");

                exactly(1).of(flow).stopFlowDueToRecoveryAttemptFailing();

                exactly(1).of(scheduler).isStarted();
                will(returnValue(true));
            }
        });

        ScheduledRecoveryManager recoveryManager = new StubbedScheduledRecoveryManager(scheduler, "flowName", "moduleName");
        setIsAware(recoveryManager);
        recoveryManager.setResolver(exceptionResolver);
        recoveryManager.setManagedResources(managedResources);

        // We need to use reflection to set a job key on the recovery manager.
        ReflectionTestUtils.setField(recoveryManager, "recoveryJobKey", jobKey);
        ReflectionTestUtils.setField(recoveryManager, "previousComponentName", "componentName");
        ReflectionTestUtils.setField(recoveryManager, "isConsumerMultiThreaded", true);

        try {
            recoveryManager.execute(this.jobExecutionContext);
        }
        catch (Exception e) {
            Assert.assertTrue(e instanceof ForceTransactionRollbackException);
        }

        Assert.assertTrue(recoveryManager.isFlowStartBasedRecovery());

        // test aspects we cannot access through the interface
        Assert.assertEquals(0,((StubbedScheduledRecoveryManager)recoveryManager).getRetryAttempts());

        mockery.assertIsSatisfied();
    }

    /**
     * Call isAware setters on the RM to populate as required.
     *
     * @param recoveryManager
     */
    private void setIsAware(RecoveryManager recoveryManager)
    {
        if(recoveryManager instanceof IsConsumerAware aware)
        {
            aware.setConsumer(consumer);
        }

        if(recoveryManager instanceof IsExclusionServiceAware aware)
        {
            aware.setExclusionService(exclusionService);
        }

        if(recoveryManager instanceof IsErrorReportingServiceAware aware)
        {
            aware.setErrorReportingService(errorReportingService);
        }

        if(recoveryManager instanceof IsFlowAware aware)
        {
            aware.setFlow(flow);
        }
    }


    /**
     * Extended class allowing mocking of the quartz recovery job and trigger.
     * @author Ikasan Development Team
     *
     */
    private class StubbedScheduledRecoveryManager extends ScheduledRecoveryManager
    {

        StubbedScheduledRecoveryManager(Scheduler scheduler, String flowName, String moduleName)
        {
            super(scheduler, scheduledJobFactory, flowName, moduleName);
        }
        
        @Override
        protected Trigger newRecoveryTrigger(long delay, String triggerName)
        {
            return trigger;
        }
        
        /**
         * Added to allow testing on retry attempts counter
         */
        int getRetryAttempts()
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

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("UnsupportedExceptionAction");
            return sb.toString();
        }
    }
}
