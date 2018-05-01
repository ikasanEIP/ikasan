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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This test class supports the <code>CallBackMessageProviderImpl</code> class.
 * 
 * @author Ikasan Development Team
 */
public class CallbackMessageProviderImplTest
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
    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");

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

    /** Mock managedResourceRecoveryManager **/
    final ManagedResourceRecoveryManager mockManagedResourceRecoveryManager = mockery.mock(ManagedResourceRecoveryManager.class);

    /** consumer event listener */
    final EventListener eventListener = mockery.mock(EventListener.class);

    final ManagedEventIdentifierService  mockManagedEventIdentifierService = mockery.mock(ManagedEventIdentifierService.class);


    /**
     * Test failed constructor for scheduled consumer due to null scheduler.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullScheduler()
    {
        new CallBackScheduledConsumer(null);
    }

    /**
     * Test successful consumer start.
     * @throws org.quartz.SchedulerException
     */
    @Test
    public void test_successful_callbackMessageProvider_callback_to_consumer() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("flowName", "moduleName");
        final CallBackMessageProvider mockCallBackMessageProvider = mockery.mock( CallBackMessageProvider.class);
        final FlowEvent mockFlowEvent = mockery.mock( FlowEvent.class);
        final String identifier = "testId";

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // schedule the job
                exactly(1).of(scheduler).scheduleJob(mockJobDetail, trigger);
                will(returnValue(new Date()));

                exactly(3).of(mockManagedResourceRecoveryManager).isRecovering();
                will(returnValue(false));

                // create flowEvent and call flow
                exactly(1).of(mockManagedEventIdentifierService).getEventIdentifier("one");
                will(returnValue("1"));
                exactly(1).of(flowEventFactory).newEvent("1","one");
                will(returnValue(mockFlowEvent));
                exactly(1).of(eventListener).invoke(mockFlowEvent);

                // create flowEvent and call flow
                exactly(1).of(mockManagedEventIdentifierService).getEventIdentifier("two");
                will(returnValue("2"));
                exactly(1).of(flowEventFactory).newEvent("2","two");
                will(returnValue(mockFlowEvent));
                exactly(1).of(eventListener).invoke(mockFlowEvent);

                // create flowEvent and call flow
                exactly(1).of(mockManagedEventIdentifierService).getEventIdentifier("three");
                will(returnValue("3"));
                exactly(1).of(flowEventFactory).newEvent("3","three");
                will(returnValue(mockFlowEvent));
                exactly(1).of(eventListener).invoke(mockFlowEvent);

                // check if consumer is eager
                exactly(1).of(consumerConfiguration).isEager();
                will(returnValue(false));

            }
        });

        CallBackScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler);
        scheduledConsumer.setEventFactory(flowEventFactory);
        scheduledConsumer.setEventListener(eventListener);
        scheduledConsumer.setManagedEventIdentifierService(mockManagedEventIdentifierService);
        scheduledConsumer.setConfiguration(consumerConfiguration);
        scheduledConsumer.setJobDetail(mockJobDetail);
        scheduledConsumer.setCallBackMessageProvider(new ExampleCallBackMessageProvider(scheduledConsumer));
        scheduledConsumer.setManagedResourceRecoveryManager(mockManagedResourceRecoveryManager);

        scheduledConsumer.start();
        scheduledConsumer.execute(jobExecutionContext);
        mockery.assertIsSatisfied();
    }

    /**
     * Extended ScheduledRecoveryManagerJobFactory for testing with replacement mocks.
     * @author Ikasan Development Team
     *
     */
    private class StubbedScheduledConsumer extends CallBackScheduledConsumer
    {
        protected StubbedScheduledConsumer(Scheduler scheduler)
        {
            super(scheduler);
        }
        
        @Override
        protected Trigger getBusinessTrigger(TriggerBuilder triggerBuilder) throws ParseException
        {
            return trigger;
        }
    }

    /**
     * Test with an example callback message provider
     */
    private class ExampleCallBackMessageProvider implements CallBackMessageProvider
    {
        /** consumer to call back on for flow invocation */
        CallBackMessageConsumer<String> callBackMessageConsumer;

        /**
         * Constructor
         * @param callBackMessageConsumer
         */
        public ExampleCallBackMessageProvider(CallBackMessageConsumer<String> callBackMessageConsumer)
        {
            this.callBackMessageConsumer = callBackMessageConsumer;
            if(callBackMessageConsumer == null)
            {
                throw new IllegalArgumentException("callBackMessageConsumer cannot be 'null'");
            }
        }

        @Override
        public boolean invoke(JobExecutionContext context)
        {
            List<String> strings = new ArrayList<String>();
            strings.add("one");
            strings.add("two");
            strings.add("three");

            for(String str:strings)
            {
                callBackMessageConsumer.invoke(str);
            }
            return true;
        }

        @Override
        public void setCallBackMessageConsumer(CallBackMessageConsumer callBackMessageConsumer)
        {
            this.callBackMessageConsumer = callBackMessageConsumer;
        }
    }
}
