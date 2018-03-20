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
import org.ikasan.builder.component.endpoint.ScheduledConsumerBuilder;
import org.ikasan.builder.component.endpoint.ScheduledConsumerBuilderImpl;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumerConfiguration;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.quartz.JobDetail;
import org.quartz.Scheduler;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;

/**
 * This test class supports the <code>ComponentBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
public class ScheduledConsumerBuilderTest {
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    /**
     * Mocks
     */
    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");
    final AopProxyProvider aopProxyProvider = mockery.mock(AopProxyProvider.class, "mockAopProxyProvider");
    final ScheduledJobFactory scheduledJobFactory = mockery.mock(ScheduledJobFactory.class, "mockScheduledJobFactory");
    final JobDetail jobDetail = mockery.mock(JobDetail.class, "mockJobDetail");

    /**
     * Test successful builder creation.
     */
    @Test
    public void scheduledConsumer_build_when_configuration_provided() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        ScheduledConsumerBuilder scheduledConsumerBuilder = new ScheduledConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, aopProxyProvider);

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

        assertTrue("instance should be a ScheduledConsumer", scheduledConsumer instanceof ScheduledConsumer);

        ScheduledConsumerConfiguration configuration = ((ConfiguredResource<ScheduledConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("cronExpression should be '121212'","121212", configuration.getCronExpression());
        assertTrue("eager should be 'true'", configuration.isEager() == true);
        assertTrue("ignoreMisfire should be 'true'", configuration.isIgnoreMisfire() == true);
        assertTrue("Timezone should be 'true'", configuration.getTimezone() == "UTC");

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful builder creation.
     */
    @Test
    public void scheduledConsumer_build_when_no_aop_proxy() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        ScheduledConsumerBuilder scheduledConsumerBuilder = new ScheduledConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, null);

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

        assertTrue("instance should be a ScheduledConsumer", scheduledConsumer instanceof ScheduledConsumer);

        ScheduledConsumerConfiguration configuration = ((ConfiguredResource<ScheduledConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("cronExpression should be '121212'","121212", configuration.getCronExpression());

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful builder creation.
     */
    @Test
    public void scheduledConsumer_build_when_jobName_and_jobGroup_set() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        ScheduledConsumerBuilder scheduledConsumerBuilder = new ScheduledConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, aopProxyProvider);

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

        assertTrue("instance should be a ScheduledConsumer", scheduledConsumer instanceof ScheduledConsumer);

        ScheduledConsumerConfiguration configuration = ((ConfiguredResource<ScheduledConsumerConfiguration>) scheduledConsumer).getConfiguration();
        assertEquals("cronExpression should be '121212'","121212", configuration.getCronExpression());

        mockery.assertIsSatisfied();

    }

    @Test
    public void scheduledConsumer_build_when_jobName_not_set() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        ScheduledConsumerBuilder scheduledConsumerBuilder = new ScheduledConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, aopProxyProvider);

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
    public void scheduledConsumer_build_when_jobGroupName_not_set() {

        final ScheduledConsumer emptyScheduleConsumer =  new ScheduledConsumer(scheduler);
        ScheduledConsumerBuilder scheduledConsumerBuilder = new ScheduledConsumerBuilderImpl(emptyScheduleConsumer,
                scheduledJobFactory, aopProxyProvider);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set event factory
                oneOf(aopProxyProvider).applyPointcut(with("testJob"),with(emptyScheduleConsumer));
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

}
