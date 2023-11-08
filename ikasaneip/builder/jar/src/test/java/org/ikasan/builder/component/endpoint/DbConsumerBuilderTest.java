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
import org.ikasan.component.endpoint.db.messageprovider.DbConsumerConfiguration;
import org.ikasan.component.endpoint.quartz.consumer.CallBackMessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.event.ManagedEventIdentifierService;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.quartz.JobDetail;
import org.quartz.Scheduler;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

/**
 * This test class supports the <code>DbConsumerBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
public class DbConsumerBuilderTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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

    @Test
    public void dbConsumer_build_when_all_attributes_set_scheduledConsumer_vanilla_messageProvider() {

        final ScheduledConsumer emptyScheduleConsumer = new ScheduledConsumer(scheduler);
        DbConsumerBuilder dbConsumerBuilder = new ExtendedDbConsumerBuilderImpl(emptyScheduleConsumer,
                scheduler, scheduledJobFactory, aopProxyProvider, callbackMessageProvider);

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

        Consumer consumer = dbConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName("jobGroupName")
                .setScheduledJobName("testJob")
                .setConfiguredResourceId("configuredResourceId")
                .setConfiguration(new DbConsumerConfiguration())
                .setCriticalOnStartup(true)
                .setEager(true)
                .setIgnoreMisfire(true)
                .setMaxEagerCallbacks(10)
                .setMessageProvider(messageProvider)
                .setTimezone("GMT")
                .setEventFactory(eventFactory)
                .setManagedEventIdentifierService(managedEventIdentifierService)
                .setManagedResourceRecoveryManager(managedResourceRecoveryManager)
                .setUrl("url")
                .setDriver("driver")
                .setUsername("username")
                .setPassword("password")
                .setSqlStatement("sqlStatement")
                .build();

        ScheduledConsumer scheduledConsumer = (ScheduledConsumer)consumer;

        assertTrue(scheduledConsumer.getConfiguration().getCronExpression().equals("121212"));
        assertTrue(scheduledConsumer.getConfiguredResourceId().equals("configuredResourceId"));
        assertTrue(scheduledConsumer.isCriticalOnStartup());

        DbConsumerConfiguration dbConsumerConfiguration = (DbConsumerConfiguration)scheduledConsumer.getConfiguration();
        assertTrue(dbConsumerConfiguration.getDriver().equals("driver"));
        assertTrue(dbConsumerConfiguration.getUrl().equals("url"));
        assertTrue(dbConsumerConfiguration.getUsername().equals("username"));
        assertTrue(dbConsumerConfiguration.getPassword().equals("password"));
        assertTrue(dbConsumerConfiguration.getSqlStatement().equals("sqlStatement"));

        assertTrue(scheduledConsumer.getConfiguration().isIgnoreMisfire());
        assertTrue(scheduledConsumer.getConfiguration().isEager());
        assertTrue(scheduledConsumer.getConfiguration().getMaxEagerCallbacks() == 10);
        assertTrue(scheduledConsumer.getMessageProvider() instanceof MessageProvider);
        assertTrue(scheduledConsumer.getConfiguration().getTimezone().equals("GMT"));
        assertTrue(scheduledConsumer.getEventFactory() != null);
        assertTrue(scheduledConsumer.getManagedEventIdentifierService() != null);


        mockery.assertIsSatisfied();
    }

    @Test
    public void dbConsumer_build_when_all_attributes_set_scheduledConsumer_callback_messageProvider() {

        final ScheduledConsumer emptyScheduleConsumer = new ScheduledConsumer(scheduler);
        DbConsumerBuilder dbConsumerBuilder = new ExtendedDbConsumerBuilderImpl(emptyScheduleConsumer,
                scheduler, scheduledJobFactory, aopProxyProvider, callbackMessageProvider);

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

        Consumer consumer = dbConsumerBuilder
                .setCronExpression("121212")
                .setConfiguredResourceId("testConfigId")
                .setScheduledJobGroupName("jobGroupName")
                .setScheduledJobName("testJob")
                .setConfiguredResourceId("configuredResourceId")
                .setConfiguration(new DbConsumerConfiguration())
                .setCriticalOnStartup(true)
                .setEager(true)
                .setIgnoreMisfire(true)
                .setMaxEagerCallbacks(10)
                .setMessageProvider(callbackMessageProvider)
                .setTimezone("GMT")
                .setEventFactory(eventFactory)
                .setManagedEventIdentifierService(managedEventIdentifierService)
                .setManagedResourceRecoveryManager(managedResourceRecoveryManager)
                .setUrl("url")
                .setDriver("driver")
                .setUsername("username")
                .setPassword("password")
                .setSqlStatement("sqlStatement")
                .build();

        ScheduledConsumer scheduledConsumer = (ScheduledConsumer)consumer;
        assertTrue(scheduledConsumer.getConfiguration().getCronExpression().equals("121212"));
        assertTrue(scheduledConsumer.getConfiguredResourceId().equals("configuredResourceId"));
        assertTrue(scheduledConsumer.isCriticalOnStartup());

        DbConsumerConfiguration dbConsumerConfiguration = (DbConsumerConfiguration)scheduledConsumer.getConfiguration();
        assertTrue(dbConsumerConfiguration.getDriver().equals("driver"));
        assertTrue(dbConsumerConfiguration.getUrl().equals("url"));
        assertTrue(dbConsumerConfiguration.getUsername().equals("username"));
        assertTrue(dbConsumerConfiguration.getPassword().equals("password"));
        assertTrue(dbConsumerConfiguration.getSqlStatement().equals("sqlStatement"));

        assertTrue(scheduledConsumer.getConfiguration().isIgnoreMisfire());
        assertTrue(scheduledConsumer.getConfiguration().isEager());
        assertTrue(scheduledConsumer.getConfiguration().getMaxEagerCallbacks() == 10);
        assertTrue(scheduledConsumer.getMessageProvider() instanceof CallBackMessageProvider);
        assertTrue(scheduledConsumer.getConfiguration().getTimezone().equals("GMT"));
        assertTrue(scheduledConsumer.getEventFactory() != null);
        assertTrue(scheduledConsumer.getManagedEventIdentifierService() != null);


        mockery.assertIsSatisfied();
    }

    /**
     * Test class
     */
    class ExtendedDbConsumerBuilderImpl extends DbConsumerBuilderImpl
    {
        ScheduledConsumer scheduledConsumer;

        /**
         * Constructor
         * @param scheduledConsumer
         * @param scheduler
         * @param scheduledJobFactory
         * @param aopProxyProvider
         * @param messageProvider
         */
        public ExtendedDbConsumerBuilderImpl(ScheduledConsumer scheduledConsumer,
                                             Scheduler scheduler,
                                             ScheduledJobFactory scheduledJobFactory,
                                             AopProxyProvider aopProxyProvider,
                                             MessageProvider messageProvider)
        {
            super(scheduler, scheduledJobFactory, aopProxyProvider, messageProvider);
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

    }
}
