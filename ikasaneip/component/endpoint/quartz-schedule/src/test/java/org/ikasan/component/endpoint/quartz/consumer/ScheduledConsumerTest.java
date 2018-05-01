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
package org.ikasan.component.endpoint.quartz.consumer;

import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.quartz.*;
import org.quartz.utils.Key;

import java.text.ParseException;
import java.util.Date;


/**
 * This test class supports the <code>ScheduledConsumer</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledConsumerTest
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

    /** Mock scheduler */
    private final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");

    /** Mock scheduler Context */
    private final SchedulerContext schedulerContext = mockery.mock(SchedulerContext.class, "mockSchedulerContext");

    /** Mock job detail */
    private final JobDetail mockJobDetail = mockery.mock(JobDetail.class, "mockJobDetail");

    /** Mock triggerBuilder */
    private final TriggerBuilder triggerBuilder = mockery.mock(TriggerBuilder.class, "mockTriggerBuilder");

    /** Mock trigger */
    private final Trigger trigger = mockery.mock(Trigger.class, "mockTrigger");

    /** Mock flowEventFactory */
    private final EventFactory<FlowEvent> flowEventFactory = mockery.mock(EventFactory.class, "mockEventFactory");

    /** Mock consumerConfiguration */
    private final ScheduledConsumerConfiguration consumerConfiguration =
        mockery.mock(ScheduledConsumerConfiguration.class, "mockScheduledConsumerConfiguration");

    /** Mock jobExecutionContext **/
    private final JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class);

    /** Mock managedResourceRecoveryManager **/
    private final ManagedResourceRecoveryManager mockManagedResourceRecoveryManager = mockery.mock(ManagedResourceRecoveryManager.class);

    /** consumer event listener */
    private final EventListener eventListener = mockery.mock(EventListener.class);

    private final ManagedEventIdentifierService  mockManagedEventIdentifierService = mockery.mock(ManagedEventIdentifierService.class);

    /**
     * Test failed constructor for scheduled consumer due to null scheduler.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullScheduler()
    {
        new ScheduledConsumer(null);
    }

    /**
     * Test successful consumer start.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_start() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("flowName", "moduleName");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // access configuration for details
                exactly(1).of(consumerConfiguration).getCronExpression();
                will(returnValue("* * * * * ?"));

                // access configuration for details
                exactly(1).of(consumerConfiguration).isIgnoreMisfire();
                will(returnValue(true));

                // access configuration for details
                exactly(3).of(consumerConfiguration).getTimezone();
                will(returnValue("UTC"));

                exactly(1).of(triggerBuilder).withSchedule(with(any(ScheduleBuilder.class)));
                exactly(1).of(triggerBuilder).build();
                will(returnValue(trigger));

                // schedule the job
                exactly(1).of(scheduler).scheduleJob(mockJobDetail, trigger);
                will(returnValue(new Date()));
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler);
        scheduledConsumer.setConfiguration(consumerConfiguration);
        scheduledConsumer.setJobDetail(mockJobDetail);
        scheduledConsumer.start();
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed consumer start.
     * @throws SchedulerException 
     */
    @Test(expected = RuntimeException.class)
    public void test_failed_start_due_to_schedulerException() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("flowName", "moduleName");
        
        // expectations
        mockery.checking(new Expectations() {
            {
                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // access configuration for details
                exactly(1).of(consumerConfiguration).getCronExpression();
                will(returnValue("* * * * ? ?"));

                // schedule the job
                exactly(1).of(scheduler).scheduleJob(mockJobDetail, trigger);
                will(throwException(new SchedulerException()));
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler);
        scheduledConsumer.setConfiguration(consumerConfiguration);
        scheduledConsumer.start();
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed consumer start.
     * @throws SchedulerException 
     */
    @Test(expected = RuntimeException.class)
    public void test_failed_start_due_to_parserException() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("flowName", "moduleName");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // access configuration for details
                exactly(1).of(consumerConfiguration).getCronExpression();
                will(returnValue("* * * * ? ?"));
                
                // schedule the job
                exactly(1).of(scheduler).scheduleJob(mockJobDetail, trigger);
                will(throwException(new ParseException("test",0)));
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler);
        scheduledConsumer.setConfiguration(consumerConfiguration);
        scheduledConsumer.start();
        mockery.assertIsSatisfied();
    }


    
    /**
     * Test successful consumer stop.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_stop() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("flowName", "moduleName");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // unschedule the job
                exactly(1).of(scheduler).checkExists(jobKey);
                will(returnValue(Boolean.TRUE));

                exactly(1).of(scheduler).deleteJob(jobKey);
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler);
        scheduledConsumer.setConfiguration(consumerConfiguration);
        scheduledConsumer.setJobDetail(mockJobDetail);
        scheduledConsumer.stop();
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed consumer stop due to scheduler exception.
     * @throws SchedulerException 
     */
    @Test(expected = RuntimeException.class)
    public void test_failed_stop_due_to_schedulerException() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("flowName", "moduleName");
        
        // expectations
        mockery.checking(new Expectations() {
            {
                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // unschedule the job
                exactly(1).of(scheduler).checkExists(jobKey);
                will(returnValue(Boolean.TRUE));

                exactly(1).of(scheduler).deleteJob(jobKey);
                will(throwException(new SchedulerException()));
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler);
        scheduledConsumer.setConfiguration(consumerConfiguration);
        scheduledConsumer.stop();
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_execute_when_messageProvider_message_is_not_null() throws SchedulerException
    {
        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);
        final String identifier = "testId";

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockManagedResourceRecoveryManager).isRecovering();
                will(returnValue(false));

                // schedule the job
                exactly(1).of(mockManagedEventIdentifierService).getEventIdentifier(jobExecutionContext);
                will(returnValue(identifier));

                exactly(1).of(flowEventFactory).newEvent(identifier,jobExecutionContext);
                will(returnValue(mockFlowEvent));

                exactly(1).of(eventListener).invoke(mockFlowEvent);

                exactly(1).of(consumerConfiguration).isEager();
                will(returnValue(false));

            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler);
        scheduledConsumer.setConfiguration(consumerConfiguration);
        scheduledConsumer.setEventFactory(flowEventFactory);
        scheduledConsumer.setEventListener(eventListener);
        scheduledConsumer.setManagedResourceRecoveryManager(mockManagedResourceRecoveryManager);
        scheduledConsumer.setManagedEventIdentifierService(mockManagedEventIdentifierService);
        // test
        scheduledConsumer.execute(jobExecutionContext);
        // assert
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_execute_when_messageProvider_message_is_null_not_in_recovery() throws SchedulerException
    {
        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);
        final MessageProvider mockMessageProvider = mockery.mock( MessageProvider.class);
        final String identifier = "testId";

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockMessageProvider).invoke(jobExecutionContext);
                will(returnValue(null));

                // schedule the job
                exactly(0).of(mockManagedEventIdentifierService).getEventIdentifier(jobExecutionContext);
                will(returnValue(identifier));

                exactly(0).of(flowEventFactory).newEvent(identifier, jobExecutionContext);
                will(returnValue(mockFlowEvent));

                exactly(0).of(eventListener).invoke(mockFlowEvent);

                exactly(1).of(mockManagedResourceRecoveryManager).isRecovering();
                will(returnValue(false));

                exactly(1).of(consumerConfiguration).isEager();
                will(returnValue(false));

            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler);
        scheduledConsumer.setConfiguration(consumerConfiguration);
        scheduledConsumer.setEventFactory(flowEventFactory);
        scheduledConsumer.setEventListener(eventListener);
        scheduledConsumer.setManagedEventIdentifierService(mockManagedEventIdentifierService);
        scheduledConsumer.setManagedResourceRecoveryManager(mockManagedResourceRecoveryManager);
        scheduledConsumer.setMessageProvider(mockMessageProvider);
        // test
        scheduledConsumer.execute(jobExecutionContext);
        // assert
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_execute_when_messageProvider_message_is_null_when_in_recovery_and_reinstate_business_schedule() throws SchedulerException
    {
        final MessageProvider mockMessageProvider = mockery.mock( MessageProvider.class);
        final JobKey jobKey = new JobKey("flowName", "moduleName");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(consumerConfiguration).isEager();
                will(returnValue(false));

                exactly(2).of(mockManagedResourceRecoveryManager).isRecovering();
                will(returnValue(true));

                exactly(1).of(mockMessageProvider).invoke(jobExecutionContext);
                will(returnValue(null));

                exactly(1).of(consumerConfiguration).getCronExpression();
                will(returnValue("* * * * * ?"));

                exactly(1).of(consumerConfiguration).isIgnoreMisfire();
                will(returnValue(true));

                exactly(3).of(consumerConfiguration).getTimezone();
                will(returnValue("UTC"));

                exactly(1).of(triggerBuilder).withSchedule(with(any(ScheduleBuilder.class)));
                exactly(1).of(triggerBuilder).build();
                will(returnValue(trigger));

                // cancelAll recovery
                exactly(1).of(mockManagedResourceRecoveryManager).cancel();

                // nope, consumer is not running
                exactly(1).of(scheduler).isShutdown();
                will(returnValue(false));
                exactly(1).of(scheduler).isInStandbyMode();
                will(returnValue(false));
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));
                exactly(1).of(scheduler).checkExists(jobKey);
                will(returnValue(false));

                // start consumer
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                exactly(1).of(scheduler).scheduleJob(mockJobDetail, trigger);
                will(returnValue(new Date()));

            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler);
        scheduledConsumer.setEventFactory(flowEventFactory);
        scheduledConsumer.setEventListener(eventListener);
        scheduledConsumer.setManagedEventIdentifierService(mockManagedEventIdentifierService);
        scheduledConsumer.setManagedResourceRecoveryManager(mockManagedResourceRecoveryManager);
        scheduledConsumer.setMessageProvider(mockMessageProvider);
        scheduledConsumer.setJobDetail(mockJobDetail);
        scheduledConsumer.setConfiguration(consumerConfiguration);

        // test
        scheduledConsumer.execute(jobExecutionContext);
        // assert
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_execute_when_messageProvider_throws_exception() throws SchedulerException
    {
        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);
        final MessageProvider mockMessageProvider = mockery.mock( MessageProvider.class);
        final String identifier = "testId";
        final RuntimeException rt = new RuntimeException("rt is thrown");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockMessageProvider).invoke(jobExecutionContext);
                will(throwException(rt));
                exactly(1).of(mockManagedResourceRecoveryManager).recover(rt);
                // schedule the job
                exactly(0).of(mockManagedEventIdentifierService).getEventIdentifier(jobExecutionContext);
                will(returnValue(identifier));

                exactly(0).of(flowEventFactory).newEvent(identifier,jobExecutionContext);
                will(returnValue(mockFlowEvent));

                exactly(0).of(eventListener).invoke(mockFlowEvent);
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler);
        
        scheduledConsumer.setEventFactory(flowEventFactory);
        scheduledConsumer.setEventListener(eventListener);
        scheduledConsumer.setManagedEventIdentifierService(mockManagedEventIdentifierService);
        scheduledConsumer.setMessageProvider(mockMessageProvider);
        scheduledConsumer.setManagedResourceRecoveryManager(mockManagedResourceRecoveryManager);
        // test
        scheduledConsumer.execute(jobExecutionContext);
        // assert
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_execute_when_messageProvider_message_is_not_null_and_consumer_is_eager() throws SchedulerException
    {
        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);
        final String identifier = "testId";
        final JobKey jobKey = new JobKey("flowName", "moduleName");
        final JobDetail jobDetail = mockery.mock(JobDetail.class);
        final TriggerKey triggerKey = new TriggerKey("flowName","moduleName");
        final JobDataMap jobDataMap = new JobDataMap();

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockManagedResourceRecoveryManager).isRecovering();
                will(returnValue(false));

                // schedule the job
                exactly(1).of(mockManagedEventIdentifierService).getEventIdentifier(jobExecutionContext);
                will(returnValue(identifier));

                exactly(1).of(flowEventFactory).newEvent(identifier,jobExecutionContext);
                will(returnValue(mockFlowEvent));

                exactly(1).of(eventListener).invoke(mockFlowEvent);

                exactly(1).of(consumerConfiguration).isEager();
                will(returnValue(true));

                exactly(1).of(scheduler).checkExists(with(any(TriggerKey.class)));
                will(returnValue(false));

                exactly(1).of(jobExecutionContext).getTrigger();
                will(returnValue(trigger));

                exactly(1).of(consumerConfiguration).getMaxEagerCallbacks();
                will(returnValue(0));

                exactly(1).of(trigger).getTriggerBuilder();
                will(returnValue(triggerBuilder));

                exactly(1).of(triggerBuilder).usingJobData("eagerCallback", new Integer(1));
                will(returnValue(triggerBuilder));
                exactly(1).of(triggerBuilder).startNow();
                will(returnValue(triggerBuilder));
                exactly(1).of(triggerBuilder).withSchedule(with(any(ScheduleBuilder.class)));

                exactly(1).of(trigger).getKey();
                will(returnValue(triggerKey));

                exactly(1).of(trigger).getJobDataMap();
                will(returnValue(jobDataMap));

                exactly(1).of(scheduler).scheduleJob(with(any(Trigger.class)));
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler);
        scheduledConsumer.setConfiguration(consumerConfiguration);
        scheduledConsumer.setEventFactory(flowEventFactory);
        scheduledConsumer.setEventListener(eventListener);
        scheduledConsumer.setManagedEventIdentifierService(mockManagedEventIdentifierService);
        scheduledConsumer.setManagedResourceRecoveryManager(mockManagedResourceRecoveryManager);
        scheduledConsumer.setJobDetail(jobDetail);

        // test
        scheduledConsumer.execute(jobExecutionContext);
        // assert
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_execute_when_messageProvider_message_is_not_null_and_consumer_is_eager_existing_eagerTrigger() throws SchedulerException
    {
        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);
        final String identifier = "testId";
        final JobKey jobKey = new JobKey("flowName", "moduleName");
        final JobDetail jobDetail = mockery.mock(JobDetail.class);
        final TriggerKey triggerKey = new TriggerKey("flowName","moduleName");
        final JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("eagerCallbacks", Integer.valueOf(1));

        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockManagedResourceRecoveryManager).isRecovering();
                will(returnValue(false));

                // schedule the job
                exactly(1).of(mockManagedEventIdentifierService).getEventIdentifier(jobExecutionContext);
                will(returnValue(identifier));

                exactly(1).of(flowEventFactory).newEvent(identifier,jobExecutionContext);
                will(returnValue(mockFlowEvent));

                exactly(1).of(eventListener).invoke(mockFlowEvent);

                exactly(1).of(consumerConfiguration).isEager();
                will(returnValue(true));

                exactly(1).of(trigger).getTriggerBuilder();
                will(returnValue(triggerBuilder));

                exactly(1).of(triggerBuilder).usingJobData("eagerCallback", new Integer(1));
                will(returnValue(triggerBuilder));
                exactly(1).of(triggerBuilder).startNow();
                will(returnValue(triggerBuilder));
                exactly(1).of(triggerBuilder).withSchedule(with(any(ScheduleBuilder.class)));

                exactly(1).of(jobExecutionContext).getTrigger();
                will(returnValue(trigger));

                exactly(1).of(consumerConfiguration).getMaxEagerCallbacks();
                will(returnValue(0));

                exactly(1).of(trigger).getKey();
                will(returnValue(triggerKey));

                exactly(1).of(trigger).getKey();
                will(returnValue(triggerKey));

                exactly(1).of(trigger).getJobDataMap();
                will(returnValue(jobDataMap));

                exactly(1).of(scheduler).checkExists(with(any(TriggerKey.class)));
                will(returnValue(true));

                exactly(1).of(scheduler).rescheduleJob(with(any(TriggerKey.class)), with(any(Trigger.class)));
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler);
        scheduledConsumer.setConfiguration(consumerConfiguration);
        scheduledConsumer.setEventFactory(flowEventFactory);
        scheduledConsumer.setEventListener(eventListener);
        scheduledConsumer.setManagedEventIdentifierService(mockManagedEventIdentifierService);
        scheduledConsumer.setManagedResourceRecoveryManager(mockManagedResourceRecoveryManager);
        scheduledConsumer.setJobDetail(jobDetail);

        // test
        scheduledConsumer.execute(jobExecutionContext);
        // assert
        mockery.assertIsSatisfied();
    }

    /**
     * Extended ScheduledRecoveryManagerJobFactory for testing with replacement mocks.
     * @author Ikasan Development Team
     *
     */
    private class StubbedScheduledConsumer extends ScheduledConsumer
    {
        protected StubbedScheduledConsumer(Scheduler scheduler)
        {
            super(scheduler);
        }
        
        @Override
        protected TriggerBuilder newTriggerFor(String name, String group)
        {
            return triggerBuilder;
        }
    }

}
