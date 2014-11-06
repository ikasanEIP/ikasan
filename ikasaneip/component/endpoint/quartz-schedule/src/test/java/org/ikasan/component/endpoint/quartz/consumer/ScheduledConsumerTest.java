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

import java.text.ParseException;
import java.util.Date;

import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.flow.FlowEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey; 
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Job;

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
        }
    };

    /** Mock scheduler */
    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");

    /** Mock scheduled job factory */
    final ScheduledJobFactory scheduledJobFactory = mockery.mock(ScheduledJobFactory.class, "mockScheduledJobFactory");

    /** Mock job detail */
    final JobDetail mockJobDetail = mockery.mock(JobDetail.class, "mockJobDetail");

    /** Mock trigger */
    final Trigger trigger = mockery.mock(Trigger.class, "mockTrigger");

    /** Mock flowEventFactory */
    final EventFactory<FlowEvent> flowEventFactory = mockery.mock(EventFactory.class, "mockEventFactory");

    /** Mock consumerConfiguration */
    final ScheduledConsumerConfiguration consumerConfiguration = 
        mockery.mock(ScheduledConsumerConfiguration.class, "mockScheduledConsumerConfiguration");

    /** Mock jobExecutionContext **/
    final JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class); 
    
    /** consumer event listener */
    final EventListener eventListener = mockery.mock(EventListener.class);

    final ManagedEventIdentifierService  mockManagedEventIdentifierService = mockery.mock(ManagedEventIdentifierService.class);


    /**
     * Test failed constructor for scheduled consumer due to null scheduler.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullScheduler()
    {
        new ScheduledConsumer(null, null, null, null);
    }

    /**
     * Test failed constructor for scheduled consumer due to null flowEventFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullFlowEventFactory()
    {
        new ScheduledConsumer(scheduler, null, null, null);
    }

    /**
     * Test failed constructor for scheduled consumer due to null name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullName()
    {
        new ScheduledConsumer(scheduler, scheduledJobFactory, null, null);
    }

    /**
     * Test failed constructor for scheduled consumer due to null group.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullGroup()
    {
        new ScheduledConsumer(scheduler, scheduledJobFactory, "name", null);
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
                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(mockJobDetail));

                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // access configuration for details
                exactly(1).of(consumerConfiguration).getCronExpression();
                will(returnValue("* * * * ? ?"));

                // schedule the job
                exactly(1).of(scheduler).scheduleJob(mockJobDetail, trigger);
                will(returnValue(new Date()));
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler, scheduledJobFactory, "flowName", "moduleName");
        scheduledConsumer.setConfiguration(consumerConfiguration);
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
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(mockJobDetail));

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

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler, scheduledJobFactory, "flowName", "moduleName");
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
                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(mockJobDetail));

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

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler, scheduledJobFactory, "flowName", "moduleName");
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
                // unschedule the job
                exactly(1).of(scheduler).checkExists(jobKey);
                will(returnValue(Boolean.TRUE));

                exactly(1).of(scheduler).deleteJob(jobKey);
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler, scheduledJobFactory, "flowName", "moduleName");
        scheduledConsumer.setConfiguration(consumerConfiguration);
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
        mockery.checking(new Expectations()
        {
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

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler, scheduledJobFactory, "flowName", "moduleName");
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

                // schedule the job
                exactly(1).of(mockManagedEventIdentifierService).getEventIdentifier(jobExecutionContext);
                will(returnValue(identifier));

                exactly(1).of(flowEventFactory).newEvent(identifier,jobExecutionContext);
                will(returnValue(mockFlowEvent));

                exactly(1).of(eventListener).invoke(mockFlowEvent);
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler, scheduledJobFactory, "flowName", "moduleName");
        scheduledConsumer.setEventFactory(flowEventFactory);
        scheduledConsumer.setEventListener(eventListener);
        scheduledConsumer.setManagedEventIdentifierService(mockManagedEventIdentifierService);
        // test
        scheduledConsumer.execute(jobExecutionContext);
        // assert
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_execute_when_messageProvider_message_is_null() throws SchedulerException
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

                exactly(0).of(flowEventFactory).newEvent(identifier,jobExecutionContext);
                will(returnValue(mockFlowEvent));

                exactly(0).of(eventListener).invoke(mockFlowEvent);
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler, scheduledJobFactory, "flowName", "moduleName");
        scheduledConsumer.setEventFactory(flowEventFactory);
        scheduledConsumer.setEventListener(eventListener);
        scheduledConsumer.setManagedEventIdentifierService(mockManagedEventIdentifierService);
        scheduledConsumer.setMessageProvider(mockMessageProvider);
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
        protected StubbedScheduledConsumer(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory, String name, String group)
        {
            super(scheduler, scheduledJobFactory, name, group);
        }
        
        @Override
        protected Trigger getCronTrigger(JobKey jobkey, String cronExpression)
        {
            return trigger;
        }
    }

}
