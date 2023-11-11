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
package org.ikasan.builder.component.endpoint;

import org.hamcrest.CoreMatchers;
import org.ikasan.builder.AopProxyProvider;
import org.ikasan.component.endpoint.quartz.consumer.CallBackMessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test class supports the <code>ComponentBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
class ScheduledConsumerBuilderTest {
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocks
     */
    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");
    final AopProxyProvider aopProxyProvider = mockery.mock(AopProxyProvider.class, "mockAopProxyProvider");
    final ScheduledJobFactory scheduledJobFactory = mockery.mock(ScheduledJobFactory.class, "mockScheduledJobFactory");
    final JobDetail jobDetail = mockery.mock(JobDetail.class, "mockJobDetail");
    final MessageProvider messageProvider = mockery.mock(MessageProvider.class, "mockMessageProvider");
    final CallBackMessageProvider callbackMessageProvider = mockery.mock(CallBackMessageProvider.class, "mockCallBackMessageProvider");
    final EventFactory eventFactory = mockery.mock(EventFactory.class, "mockEventFactory");
    final ManagedEventIdentifierService managedEventIdentifierService = mockery.mock(ManagedEventIdentifierService.class, "mockManagedEventIdentifierService");
    final ManagedResourceRecoveryManager managedResourceRecoveryManager = mockery.mock(ManagedResourceRecoveryManager.class, "mockManagedResourceRecoveryManager");

    /**
     * Test successful builder creation.
     */
    @Test
    void scheduledConsumer_build_when_configuration_provided() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        ScheduledConsumerBuilder scheduledConsumerBuilder = new ExtendedScheduledConsumerBuilderImpl(emptyScheduleConsumer,
                scheduler, scheduledJobFactory, aopProxyProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("defaultScheduledJobName"),with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with("defaultScheduledJobName"),
                        with("defaultScheduledJobGroupName"));
                will(returnValue(jobDetail));
            }
        });

        Consumer scheduledConsumer = scheduledConsumerBuilder
                .setCronExpression("121212")
                .setEager(true)
                .setIgnoreMisfire(true)
                .setTimezone("UTC")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobName("defaultScheduledJobName")
                .setScheduledJobGroupName("defaultScheduledJobGroupName")
                .build();

        assertTrue(scheduledConsumer instanceof ScheduledConsumer, "instance should be a ScheduledConsumer");

        ScheduledConsumerConfiguration configuration = ((ConfiguredResource<ScheduledConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("121212", configuration.getCronExpression(), "cronExpression should be '121212'");
        assertTrue(configuration.isEager(), "eager should be 'true'");
        assertTrue(configuration.isIgnoreMisfire(), "ignoreMisfire should be 'true'");
        assertTrue(configuration.getTimezone() == "UTC", "Timezone should be 'true'");

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful builder creation.
     */
    @Test
    void scheduledConsumer_build_when_no_aop_proxy() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        ScheduledConsumerBuilder scheduledConsumerBuilder = new ExtendedScheduledConsumerBuilderImpl(emptyScheduleConsumer,
                scheduler, scheduledJobFactory, null);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with("defaultScheduledJobName"),
                        with("defaultScheduledJobGroupName"));
                will(returnValue(jobDetail));
            }
        });

        Consumer scheduledConsumer = scheduledConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobName("defaultScheduledJobName")
                .setScheduledJobGroupName("defaultScheduledJobGroupName")
                .build();

        assertTrue(scheduledConsumer instanceof ScheduledConsumer, "instance should be a ScheduledConsumer");

        ScheduledConsumerConfiguration configuration = ((ConfiguredResource<ScheduledConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("121212", configuration.getCronExpression(), "cronExpression should be '121212'");

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful builder creation.
     */
    @Test
    void scheduledConsumer_build_when_jobName_and_jobGroup_set() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        ScheduledConsumerBuilder scheduledConsumerBuilder = new ExtendedScheduledConsumerBuilderImpl(emptyScheduleConsumer,
                scheduler, scheduledJobFactory, aopProxyProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("testjob"),with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with("testjob"),
                        with("testGroup"));
                will(returnValue(jobDetail));
            }
        });

        Consumer scheduledConsumer = scheduledConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName("testGroup")
                .setScheduledJobName("testjob")
                .build();

        assertTrue(scheduledConsumer instanceof ScheduledConsumer, "instance should be a ScheduledConsumer");

        ScheduledConsumerConfiguration configuration = ((ConfiguredResource<ScheduledConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("121212", configuration.getCronExpression(), "cronExpression should be '121212'");

        mockery.assertIsSatisfied();

    }

    @Test
    void scheduledConsumer_build_when_jobName_not_set() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        ScheduledConsumerBuilder scheduledConsumerBuilder = new ExtendedScheduledConsumerBuilderImpl(emptyScheduleConsumer,
                scheduler, scheduledJobFactory, aopProxyProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with(any(String.class)),with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with(any(String.class)),
                        with("testGroup"));
                will(returnValue(jobDetail));
            }
        });

        scheduledConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName("testGroup")
                .setScheduledJobName(null)
                .build();

        mockery.assertIsSatisfied();
    }

    @Test
    void scheduledConsumer_build_when_jobGroupName_not_set() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        ScheduledConsumerBuilder scheduledConsumerBuilder = new ExtendedScheduledConsumerBuilderImpl(emptyScheduleConsumer,
                scheduler, scheduledJobFactory, aopProxyProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("testJob"), with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with("testJob"),
                        with(any(String.class)));
                will(returnValue(jobDetail));
            }
        });

        scheduledConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName(null)
                .setScheduledJobName("testJob")
                .build();

        mockery.assertIsSatisfied();
    }

    @Test
    void scheduledConsumer_build_when_all_attributes_set_scheduledConsumer_vanilla_messageProvider() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        ScheduledConsumerBuilder scheduledConsumerBuilder = new ExtendedScheduledConsumerBuilderImpl(emptyScheduleConsumer,
                scheduler, scheduledJobFactory, aopProxyProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("testJob"), with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with("testJob"),
                        with(any(String.class)));
                will(returnValue(jobDetail));
            }
        });

        Consumer consumer = scheduledConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName("jobGroupName")
                .setScheduledJobName("testJob")
                .setConfiguredResourceId("configuredResourceId")
                .setConfiguration(new ScheduledConsumerConfiguration())
                .setCriticalOnStartup(true)
                .setEager(true)
                .setIgnoreMisfire(true)
                .setMaxEagerCallbacks(10)
                .setMessageProvider(messageProvider)
                .setTimezone("GMT")
                .setEventFactory(eventFactory)
                .setManagedEventIdentifierService(managedEventIdentifierService)
                .setManagedResourceRecoveryManager(managedResourceRecoveryManager)
                .build();

        ScheduledConsumer scheduledConsumer = (ScheduledConsumer)consumer;
        assertEquals("121212", scheduledConsumer.getConfiguration().getCronExpression());
        assertEquals("configuredResourceId", scheduledConsumer.getConfiguredResourceId());
        assertTrue(scheduledConsumer.isCriticalOnStartup());
        assertTrue(scheduledConsumer.getConfiguration().isEager());
        assertTrue(scheduledConsumer.getConfiguration().isIgnoreMisfire());
        assertTrue(scheduledConsumer.getConfiguration().isEager());
        assertEquals(10, scheduledConsumer.getConfiguration().getMaxEagerCallbacks());
        assertTrue(scheduledConsumer.getMessageProvider() instanceof MessageProvider);
        assertEquals("GMT", scheduledConsumer.getConfiguration().getTimezone());
        assertTrue(scheduledConsumer.getEventFactory() != null);
        assertTrue(scheduledConsumer.getManagedEventIdentifierService() != null);

        mockery.assertIsSatisfied();
    }

    @Test
    void scheduledConsumer_build_when_all_attributes_set_scheduledConsumer_callback_messageProvider() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        ScheduledConsumerBuilder scheduledConsumerBuilder = new ExtendedScheduledConsumerBuilderImpl(emptyScheduleConsumer,
                scheduler, scheduledJobFactory, aopProxyProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("testJob"), with(emptyScheduleConsumer));
                will(returnValue(emptyScheduleConsumer));

                oneOf(scheduledJobFactory).createJobDetail(with(emptyScheduleConsumer),
                        with(is(CoreMatchers.equalTo(ScheduledConsumer.class))),
                        with("testJob"),
                        with(any(String.class)));
                will(returnValue(jobDetail));
            }
        });

        Consumer consumer = scheduledConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName("jobGroupName")
                .setScheduledJobName("testJob")
                .setConfiguredResourceId("configuredResourceId")
                .setConfiguration(new ScheduledConsumerConfiguration())
                .setCriticalOnStartup(true)
                .setEager(true)
                .setIgnoreMisfire(true)
                .setMaxEagerCallbacks(10)
                .setMessageProvider(callbackMessageProvider)
                .setTimezone("GMT")
                .setEventFactory(eventFactory)
                .setManagedEventIdentifierService(managedEventIdentifierService)
                .setManagedResourceRecoveryManager(managedResourceRecoveryManager)
                .build();

        ScheduledConsumer scheduledConsumer = (ScheduledConsumer)consumer;

        assertEquals("121212", scheduledConsumer.getConfiguration().getCronExpression());
        assertEquals("configuredResourceId", scheduledConsumer.getConfiguredResourceId());
        assertTrue(scheduledConsumer.isCriticalOnStartup());
        assertTrue(scheduledConsumer.getConfiguration().isEager());
        assertTrue(scheduledConsumer.getConfiguration().isIgnoreMisfire());
        assertTrue(scheduledConsumer.getConfiguration().isEager());
        assertEquals(10, scheduledConsumer.getConfiguration().getMaxEagerCallbacks());
        assertTrue(scheduledConsumer.getMessageProvider() instanceof CallBackMessageProvider);
        assertEquals("GMT", scheduledConsumer.getConfiguration().getTimezone());
        assertTrue(scheduledConsumer.getEventFactory() != null);
        assertTrue(scheduledConsumer.getManagedEventIdentifierService() != null);

        mockery.assertIsSatisfied();
    }

    class ExtendedScheduledConsumerBuilderImpl extends AbstractScheduledConsumerBuilderImpl<ScheduledConsumerBuilder> implements
            ScheduledConsumerBuilder
    {
        ScheduledConsumer scheduledConsumer;

        /**
         * Constructor
         *
         * @param scheduler
         * @param scheduledJobFactory
         * @param aopProxyProvider
         */
        public ExtendedScheduledConsumerBuilderImpl(ScheduledConsumer scheduledConsumer, Scheduler scheduler, ScheduledJobFactory scheduledJobFactory, AopProxyProvider aopProxyProvider)
        {
            super(scheduler, scheduledJobFactory, aopProxyProvider);
            this.scheduledConsumer = scheduledConsumer;
        }

        /**
         * Factory method to return a vanilla scheduled consumer to aid testing
         * @return
         */
        protected ScheduledConsumer getScheduledConsumer()
        {
            return scheduledConsumer;
        }

        /**
         * Factory method to return a callback scheduled consumer to aid testing
         * @return
         */
        protected ScheduledConsumer getCallbackScheduledConsumer()
        {
            return scheduledConsumer;
        }

        @Override
        protected ScheduledConsumerConfiguration createConfiguration()
        {
            return new ScheduledConsumerConfiguration();
        }

        @Override
        public ScheduledConsumerBuilder setConfiguration(ScheduledConsumerConfiguration configuration)
        {
            this.configuration = configuration;
            return this;
        }
    }
}
