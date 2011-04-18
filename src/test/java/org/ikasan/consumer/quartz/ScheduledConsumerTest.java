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
package org.ikasan.consumer.quartz;

import java.text.ParseException;
import java.util.Date;

import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.FlowEvent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

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

    /** Mock job detail */
    final JobDetail mockJobDetail = mockery.mock(JobDetail.class, "mockJobDetail");

    /** Mock trigger */
    final Trigger trigger = mockery.mock(Trigger.class, "mockTrigger");

    /** Mock flowEventFactory */
    final EventFactory<FlowEvent> flowEventFactory = mockery.mock(EventFactory.class, "mockEventFactory");

    /** Mock consumerConfiguration */
    final ScheduledConsumerConfiguration consumerConfiguration = 
        mockery.mock(ScheduledConsumerConfiguration.class, "mockScheduledConsumerConfiguration");

    /**
     * Test failed constructor for scheduled consumer due to null scheduler.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullScheduler()
    {
        new ScheduledConsumer(null, null, null);
    }

    /**
     * Test failed constructor for scheduled consumer due to null flowEventFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullJobDetail()
    {
        new ScheduledConsumer(scheduler, null, null);
    }

    /**
     * Test failed constructor for scheduled consumer due to null flowEventFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullFlowEventFactory()
    {
        new ScheduledConsumer(scheduler, mockJobDetail, null);
    }

    /**
     * Test successful consumer start.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_start() throws SchedulerException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // access configuration for details
                exactly(3).of(consumerConfiguration).getJobName();
                will(returnValue("flowName"));
                exactly(3).of(consumerConfiguration).getJobGroup();
                will(returnValue("moduleName"));
                exactly(1).of(consumerConfiguration).getCronExpression();
                will(returnValue("* * * * ? ?"));

                // set the name of the job detail
                exactly(1).of(mockJobDetail).setName("flowName");
                exactly(1).of(mockJobDetail).setGroup("moduleName");
                
                // schedule the job
                exactly(1).of(scheduler).scheduleJob(mockJobDetail, trigger);
                will(returnValue(new Date()));
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler, flowEventFactory);
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
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // access configuration for details
                exactly(3).of(consumerConfiguration).getJobName();
                will(returnValue("flowName"));
                exactly(3).of(consumerConfiguration).getJobGroup();
                will(returnValue("moduleName"));
                exactly(1).of(consumerConfiguration).getCronExpression();
                will(returnValue("* * * * ? ?"));

                // set the name of the job detail
                exactly(1).of(mockJobDetail).setName("flowName");
                exactly(1).of(mockJobDetail).setGroup("moduleName");
                
                // schedule the job
                exactly(1).of(scheduler).scheduleJob(mockJobDetail, trigger);
                will(throwException(new SchedulerException()));
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler, flowEventFactory);
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
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // access configuration for details
                exactly(3).of(consumerConfiguration).getJobName();
                will(returnValue("flowName"));
                exactly(3).of(consumerConfiguration).getJobGroup();
                will(returnValue("moduleName"));
                exactly(1).of(consumerConfiguration).getCronExpression();
                will(returnValue("* * * * ? ?"));

                // set the name of the job detail
                exactly(1).of(mockJobDetail).setName("flowName");
                exactly(1).of(mockJobDetail).setGroup("moduleName");
                
                // schedule the job
                exactly(1).of(scheduler).scheduleJob(mockJobDetail, trigger);
                will(throwException(new ParseException("test",0)));
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler, flowEventFactory);
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
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // access configuration for details
                exactly(1).of(mockJobDetail).getName();
                will(returnValue("flowName"));
                exactly(1).of(mockJobDetail).getGroup();
                will(returnValue("moduleName"));

                // unschedule the job
                exactly(1).of(scheduler).deleteJob("flowName", "moduleName");
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler, flowEventFactory);
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
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // access configuration for details
                exactly(1).of(mockJobDetail).getName();
                will(returnValue("flowName"));
                exactly(1).of(mockJobDetail).getGroup();
                will(returnValue("moduleName"));

                // unschedule the job
                exactly(1).of(scheduler).deleteJob("flowName", "moduleName");
                will(throwException(new SchedulerException()));
            }
        });

        ScheduledConsumer scheduledConsumer = new StubbedScheduledConsumer(scheduler, flowEventFactory);
        scheduledConsumer.setConfiguration(consumerConfiguration);
        scheduledConsumer.stop();
        mockery.assertIsSatisfied();
    }

    /**
     * Extended ScheduledRecoveryManagerJobFactory for testing with replacement mocks.
     * @author Ikasan Development Team
     *
     */
    private class StubbedScheduledConsumer extends ScheduledConsumer
    {
        protected StubbedScheduledConsumer(Scheduler scheduler, EventFactory flowEventFactory)
        {
            super(scheduler, mockJobDetail, flowEventFactory);
        }
        
        @Override
        protected Trigger getCronTrigger(String name, String group, String cronExpression)
        {
            return trigger;
        }
    }

}
