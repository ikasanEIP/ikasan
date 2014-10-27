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
package org.ikasan.component.endpoint.ftp.endpoint;

import java.text.ParseException;
import java.util.Date;

import org.ikasan.component.endpoint.ftp.common.BaseFileTransferMappedRecord;
import org.ikasan.component.endpoint.ftp.common.ClientConnectionException;
import org.ikasan.component.endpoint.ftp.common.ClientInitialisationException;
import org.ikasan.component.endpoint.ftp.consumer.FtpConsumer;
import org.ikasan.component.endpoint.ftp.consumer.FtpConsumerConfiguration;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.EventListener;
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
import org.springframework.test.util.ReflectionTestUtils;


/**
 * This test class supports the <code>ScheduledConsumer</code> class.
 *
 * @author Ikasan Development Team
 */
public class FtpEndpointImplTest {
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    final Scheduler mockScheduler = mockery.mock(Scheduler.class, "mockScheduler");

    final ScheduledJobFactory mockScheduledJobFactory = mockery.mock(ScheduledJobFactory.class, "mockScheduledJobFactory");

    final JobDetail mockJobDetail = mockery.mock(JobDetail.class, "mockJobDetail");

    final Trigger mockTrigger = mockery.mock(Trigger.class, "mockTrigger");

    final EventFactory<FlowEvent> mockFlowEventFactory = mockery.mock(EventFactory.class, "mockEventFactory");

    final FtpConsumerConfiguration mockConsumerConfiguration =
            mockery.mock(FtpConsumerConfiguration.class, "mockFtpConsumerConfiguration");

    /**
     * Mock jobExecutionContext *
     */
    final JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class);

    final EventListener mockEventListener = mockery.mock(EventListener.class);

    final FtpEndpointFactory mockFtpEndpointFactory = mockery.mock(FtpEndpointFactory.class, "mockFtpEndpointFactory");

    final FtpEndpoint mockFtpEndpoint = mockery.mock(FtpEndpoint.class, "mockFtpEndpoint");


    @Test(expected = IllegalArgumentException.class)
    public void constructor_fails_when_schedulerIsNull() {
        new FtpConsumer(null, null, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_fails_when_flowEventFactoryIsNull() {
        new FtpConsumer(mockScheduler, null, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_fails_when_nameIsNull() {
        new FtpConsumer(mockScheduler, mockScheduledJobFactory, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_fails_when_groupIsNull() {
        new FtpConsumer(mockScheduler, mockScheduledJobFactory, "name", null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_fails_when_configurationIsNull() {
        new FtpConsumer(mockScheduler, mockScheduledJobFactory, "name", "group", null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_fails_when_ftpEndpointFactoryIsNull() {
        new FtpConsumer(mockScheduler, mockScheduledJobFactory, "name", "group", mockConsumerConfiguration, null);
    }

    @Test
    public void start_when_all_param_initialise() throws Exception {
        final JobKey jobKey = new JobKey("flowName", "moduleName");

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockScheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(mockJobDetail));

                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // access configuration for details
                exactly(1).of(mockConsumerConfiguration).getCronExpression();
                will(returnValue("* * * * * ?"));

                // schedule the job
                exactly(1).of(mockScheduler).scheduleJob(mockJobDetail, mockTrigger);
                will(returnValue(new Date()));

                // create mockFtpEndpoint
                exactly(1).of(mockFtpEndpointFactory).createFtpEndpoint(mockConsumerConfiguration);
                will(returnValue(mockFtpEndpoint));
            }
        });

        FtpConsumer ftpConsumer = new StubbedFtpConsumer(mockScheduler, mockScheduledJobFactory, "flowName", "moduleName", mockConsumerConfiguration, mockFtpEndpointFactory);
        ftpConsumer.start();
        mockery.assertIsSatisfied();
    }

    @Test(expected = RuntimeException.class)
    public void failed_start_when_schedulerThrowsSchedulerException() throws SchedulerException {
        final JobKey jobKey = new JobKey("flowName", "moduleName");

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockScheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(mockJobDetail));

                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // access configuration for details
                exactly(1).of(mockConsumerConfiguration).getCronExpression();
                will(returnValue("* * * * ? ?"));

                // schedule the job
                exactly(1).of(mockScheduler).scheduleJob(mockJobDetail, mockTrigger);
                will(throwException(new SchedulerException()));
            }
        });

        FtpConsumer ftpConsumer = new StubbedFtpConsumer(mockScheduler, mockScheduledJobFactory, "flowName", "moduleName", mockConsumerConfiguration, mockFtpEndpointFactory);
        ftpConsumer.start();
        mockery.assertIsSatisfied();
    }

    @Test(expected = RuntimeException.class)
    public void failed_start_when_schedulerThrowsParseException() throws SchedulerException {
        final JobKey jobKey = new JobKey("flowName", "moduleName");

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockScheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(mockJobDetail));

                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // access configuration for details
                exactly(1).of(mockConsumerConfiguration).getCronExpression();
                will(returnValue("* * * * ? ?"));

                // schedule the job
                exactly(1).of(mockScheduler).scheduleJob(mockJobDetail, mockTrigger);
                will(throwException(new ParseException("test", 0)));
            }
        });

        FtpConsumer ftpConsumer = new StubbedFtpConsumer(mockScheduler, mockScheduledJobFactory, "flowName", "moduleName", mockConsumerConfiguration, mockFtpEndpointFactory);
        ftpConsumer.start();
        mockery.assertIsSatisfied();
    }


    @Test(expected = RuntimeException.class)
    public void test_failed_start_due_to_parserException() throws SchedulerException {
        final JobKey jobKey = new JobKey("flowName", "moduleName");

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockScheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(mockJobDetail));

                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // access configuration for details
                exactly(1).of(mockConsumerConfiguration).getCronExpression();
                will(returnValue("* * * * ? ?"));

                // schedule the job
                exactly(1).of(mockScheduler).scheduleJob(mockJobDetail, mockTrigger);
                will(throwException(new ParseException("test", 0)));
            }
        });

        FtpConsumer ftpConsumer = new StubbedFtpConsumer(mockScheduler, mockScheduledJobFactory, "flowName", "moduleName", mockConsumerConfiguration, mockFtpEndpointFactory);
        ftpConsumer.start();
        mockery.assertIsSatisfied();
    }

    @Test(expected = RuntimeException.class)
    public void failed_start_when_ftpEndpointFactoryThrowsClientInitialisationException() throws SchedulerException, ClientConnectionException, ClientInitialisationException {
        final JobKey jobKey = new JobKey("flowName", "moduleName");

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockScheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(mockJobDetail));

                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // access configuration for details
                exactly(1).of(mockConsumerConfiguration).getCronExpression();
                will(returnValue("* * * * ? ?"));

                // schedule the job
                exactly(1).of(mockScheduler).scheduleJob(mockJobDetail, mockTrigger);
                will(returnValue(new Date()));


                // create mockFtpEndpoint
                exactly(1).of(mockFtpEndpointFactory).createFtpEndpoint(mockConsumerConfiguration);
                will(throwException(new ClientInitialisationException("Error starting client")));
            }
        });

        FtpConsumer ftpConsumer = new StubbedFtpConsumer(mockScheduler, mockScheduledJobFactory, "flowName", "moduleName", mockConsumerConfiguration, mockFtpEndpointFactory);
        ftpConsumer.start();
        mockery.assertIsSatisfied();
    }

    @Test(expected = RuntimeException.class)
    public void failed_start_when_ftpEndpointFactoryThrowsClientConnectionException() throws SchedulerException, ClientConnectionException, ClientInitialisationException {
        final JobKey jobKey = new JobKey("flowName", "moduleName");

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockScheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(String.class)), with(any(String.class)));
                will(returnValue(mockJobDetail));

                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // access configuration for details
                exactly(1).of(mockConsumerConfiguration).getCronExpression();
                will(returnValue("* * * * ? ?"));

                // schedule the job
                exactly(1).of(mockScheduler).scheduleJob(mockJobDetail, mockTrigger);
                will(returnValue(new Date()));


                // create mockFtpEndpoint
                exactly(1).of(mockFtpEndpointFactory).createFtpEndpoint(mockConsumerConfiguration);
                will(throwException(new ClientConnectionException("Error starting client")));
            }
        });

        FtpConsumer ftpConsumer = new StubbedFtpConsumer(mockScheduler, mockScheduledJobFactory, "flowName", "moduleName", mockConsumerConfiguration, mockFtpEndpointFactory);
        ftpConsumer.start();
        mockery.assertIsSatisfied();
    }


    @Test
    public void stop_when_all_no_exceptions() throws SchedulerException {
        final JobKey jobKey = new JobKey("flowName", "moduleName");

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockFtpEndpoint).closeSession();

                // unschedule the job
                exactly(1).of(mockScheduler).checkExists(jobKey);
                will(returnValue(Boolean.TRUE));

                exactly(1).of(mockScheduler).deleteJob(jobKey);
            }
        });

        FtpConsumer ftpConsumer = new StubbedFtpConsumer(mockScheduler, mockScheduledJobFactory, "flowName", "moduleName", mockConsumerConfiguration, mockFtpEndpointFactory);

        ReflectionTestUtils.setField(ftpConsumer, "ftpEndpoint", mockFtpEndpoint);

        // method under test
        ftpConsumer.stop();
        // assertions
        mockery.assertIsSatisfied();
    }

    @Test(expected = RuntimeException.class)
    public void stop_when_schedulerThrowsException() throws SchedulerException {
        final JobKey jobKey = new JobKey("flowName", "moduleName");

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockFtpEndpoint).closeSession();

                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // unschedule the job
                exactly(1).of(mockScheduler).checkExists(jobKey);
                will(returnValue(Boolean.TRUE));

                exactly(1).of(mockScheduler).deleteJob(jobKey);
                will(throwException(new SchedulerException()));
            }
        });

        FtpConsumer ftpConsumer = new StubbedFtpConsumer(mockScheduler, mockScheduledJobFactory, "flowName", "moduleName", mockConsumerConfiguration, mockFtpEndpointFactory);
        ftpConsumer.stop();
        mockery.assertIsSatisfied();
    }


    @Test
    public void execute_when_no_exceptions() throws SchedulerException {
        final JobKey jobKey = new JobKey("flowName", "moduleName");
        final BaseFileTransferMappedRecord consumedFile = new BaseFileTransferMappedRecord();
        consumedFile.setName("testFileName");

        final FlowEvent mockFlowEvent = mockery.mock(FlowEvent.class);

        // expectations
        mockery.checking(new Expectations() {
            {

                exactly(1).of(mockFtpEndpoint).get();
                will(returnValue(consumedFile));

                exactly(1).of(mockFlowEventFactory).newEvent(consumedFile.getName(), consumedFile);
                will(returnValue(mockFlowEvent));

                exactly(1).of(mockEventListener).invoke(mockFlowEvent);

            }

        });

        FtpConsumer ftpConsumer = new StubbedFtpConsumer(mockScheduler, mockScheduledJobFactory, "flowName", "moduleName", mockConsumerConfiguration, mockFtpEndpointFactory);

        ReflectionTestUtils.setField(ftpConsumer, "ftpEndpoint", mockFtpEndpoint);
        ReflectionTestUtils.setField(ftpConsumer, "flowEventFactory", mockFlowEventFactory);
        ReflectionTestUtils.setField(ftpConsumer, "eventListener", mockEventListener);

        // method under test
        ftpConsumer.execute(jobExecutionContext);
        // assertions
        mockery.assertIsSatisfied();
    }

    @Test
    public void execute_when_no_ftpGetsNoFile() throws SchedulerException {
        final JobKey jobKey = new JobKey("flowName", "moduleName");
        final BaseFileTransferMappedRecord consumedFile = new BaseFileTransferMappedRecord();
        consumedFile.setName("testFileName");

        final FlowEvent mockFlowEvent = mockery.mock(FlowEvent.class);

        // expectations
        mockery.checking(new Expectations() {
            {

                exactly(1).of(mockFtpEndpoint).get();
                will(returnValue(null));

                exactly(0).of(mockFlowEventFactory).newEvent(consumedFile.getName(), consumedFile);
                will(returnValue(mockFlowEvent));

                exactly(0).of(mockEventListener).invoke(mockFlowEvent);

            }

        });

        FtpConsumer ftpConsumer = new StubbedFtpConsumer(mockScheduler, mockScheduledJobFactory, "flowName", "moduleName", mockConsumerConfiguration, mockFtpEndpointFactory);

        ReflectionTestUtils.setField(ftpConsumer, "ftpEndpoint", mockFtpEndpoint);
        ReflectionTestUtils.setField(ftpConsumer, "flowEventFactory", mockFlowEventFactory);
        ReflectionTestUtils.setField(ftpConsumer, "eventListener", mockEventListener);

        // method under test
        ftpConsumer.execute(jobExecutionContext);
        // assertions
        mockery.assertIsSatisfied();
    }



    /**
     * Extended ScheduledRecoveryManagerJobFactory for testing with replacement mocks.
     *
     * @author Ikasan Development Team
     */
    private class StubbedFtpConsumer extends FtpConsumer {
        protected StubbedFtpConsumer(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory, String name, String group, FtpConsumerConfiguration configuration, FtpEndpointFactory ftpEndpointFactory) {
            super(scheduler, scheduledJobFactory, name, group, configuration, ftpEndpointFactory);
        }

        @Override
        protected Trigger getCronTrigger(JobKey jobkey, String cronExpression) {
            return mockTrigger;
        }
    }

}
