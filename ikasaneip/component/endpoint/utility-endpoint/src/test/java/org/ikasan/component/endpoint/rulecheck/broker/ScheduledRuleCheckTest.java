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
package org.ikasan.component.endpoint.rulecheck.broker;

import org.ikasan.component.endpoint.rulecheck.Rule;
import org.ikasan.component.endpoint.rulecheck.RuleBreachException;
import org.ikasan.component.endpoint.rulecheck.RelativeTimeIntervalRuleConfiguration;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.quartz.*;

import java.util.Date;

/**
 * Test class for ScheduledRuleCheck.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledRuleCheckTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
            setImposteriser(ClassImposteriser.INSTANCE);
    }};

    /** Mock scheduler */
    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");

    /** Mock job detail */
    final JobDetail mockJobDetail = mockery.mock(JobDetail.class, "mockJobDetail");

    /** Mock trigger */
    final Trigger trigger = mockery.mock(Trigger.class, "mockTrigger");

    /** Mock rule */
    final Rule rule = mockery.mock(Rule.class, "mockRule");

    /** Mock consumerConfiguration */
    final RelativeTimeIntervalRuleConfiguration configuration =
            mockery.mock(RelativeTimeIntervalRuleConfiguration.class, "mockScheduledRuleCheckTimeThresholdConfiguration");

    private FlowEvent flowEvent = mockery.mock(FlowEvent.class);

    /** Mock jobExecutionContext **/
    final JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class);

    /** Mock recovery manager handle */
    final ManagedResourceRecoveryManager managedResourceRecoveryManager = mockery.mock(ManagedResourceRecoveryManager.class);

    /**
     * Test failed constructor.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_due_to_null_scheduler()
    {
        new ScheduledRuleCheckBroker(null,null);
    }

    /**
     * Test failed constructor.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_due_to_null_rule()
    {
        new ScheduledRuleCheckBroker(scheduler,null);
    }

    /**
     * Test successful start.
     * @throws SchedulerException
     */
    @Test
    public void test_successful_start() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("flowName", "moduleName");

        // expectations
        mockery.checking(new Expectations() {
            {
                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // access configuration for details
                exactly(1).of(configuration).getCronExpression();
                will(returnValue("* * * * ? ?"));

                // rebase the rule
                exactly(1).of(rule).rebase();

                // schedule the job
                exactly(1).of(scheduler).scheduleJob(mockJobDetail, trigger);
                will(returnValue(new Date()));
            }
        });

        ScheduledRuleCheckBroker scheduledRuleCheck = new StubbedScheduledRuleCheck(scheduler, rule);
        scheduledRuleCheck.setConfiguration(configuration);
        scheduledRuleCheck.setJobDetail(mockJobDetail);
        scheduledRuleCheck.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        scheduledRuleCheck.startManagedResource();
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful no rule breach.
     * @throws SchedulerException
     */
    @Test
    public void test_successful_no_rule_breach() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("flowName", "moduleName");

        // expectations
        mockery.checking(new Expectations() {
            {
                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // access configuration for details
                exactly(1).of(configuration).getCronExpression();
                will(returnValue("* * * * ? ?"));

                // schedule the job
                exactly(1).of(scheduler).scheduleJob(mockJobDetail, trigger);
                will(returnValue(new Date()));

                // access configuration for details
                exactly(1).of(configuration).getTimeInterval();
                will(returnValue(2000L));

            }
        });

        ScheduledRuleCheckBroker scheduledRuleCheck = new StubbedScheduledRuleCheck(scheduler, new TimerRule());
        scheduledRuleCheck.setConfiguration(configuration);
        scheduledRuleCheck.setJobDetail(mockJobDetail);
        scheduledRuleCheck.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        scheduledRuleCheck.startManagedResource();

        scheduledRuleCheck.invoke(new String("first event"));
        scheduledRuleCheck.execute(jobExecutionContext);
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful rule breach.
     * @throws SchedulerException
     */
    @Test
    public void test_successful_rule_breach() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("flowName", "moduleName");

        // expectations
        mockery.checking(new Expectations() {
            {
                // get flow and module name from the job
                exactly(1).of(mockJobDetail).getKey();
                will(returnValue(jobKey));

                // access configuration for details
                exactly(1).of(configuration).getCronExpression();
                will(returnValue("* * * * ? ?"));

                // schedule the job
                exactly(1).of(scheduler).scheduleJob(mockJobDetail, trigger);
                will(returnValue(new Date()));

                // access configuration for details
                exactly(1).of(configuration).getTimeInterval();
                will(returnValue(-1L));

                // recovery manager should be invoked
                exactly(1).of(managedResourceRecoveryManager).recover(with(any(Exception.class)));
            }
        });

        ScheduledRuleCheckBroker scheduledRuleCheck = new StubbedScheduledRuleCheck(scheduler, new TimerRule());
        scheduledRuleCheck.setConfiguration(configuration);
        scheduledRuleCheck.setJobDetail(mockJobDetail);
        scheduledRuleCheck.setManagedResourceRecoveryManager(managedResourceRecoveryManager);
        scheduledRuleCheck.startManagedResource();

        scheduledRuleCheck.invoke(new String("first event"));
        scheduledRuleCheck.execute(jobExecutionContext);
        mockery.assertIsSatisfied();
    }

    /**
     * Extended ScheduledRuleCheck for testing with replacement mocks.
     * @author Ikasan Development Team
     *
     */
    private class StubbedScheduledRuleCheck extends ScheduledRuleCheckBroker
    {
        protected StubbedScheduledRuleCheck(Scheduler scheduler, Rule rule)
        {
            super(scheduler, rule);
        }

        @Override
        protected Trigger getCronTrigger(JobKey jobkey, String cronExpression)
        {
            return trigger;
        }
    }

    /**
     * Simple rule to test parent class
     */
    private class TimerRule implements Rule, Configured<RelativeTimeIntervalRuleConfiguration>
    {
        long lastEventTimestamp;

        @Override
        public void rebase()
        {
            lastEventTimestamp = System.currentTimeMillis();
        }

        @Override
        public void update(Object o)
        {
            this.lastEventTimestamp = System.currentTimeMillis();
        }

        @Override
        public void check(Object o) throws RuleBreachException
        {
            long now = System.currentTimeMillis();
            if(now - configuration.getTimeInterval() > this.lastEventTimestamp)
            {
                throw new RuleBreachException("rule failed");
            }
        }

        @Override
        public RelativeTimeIntervalRuleConfiguration getConfiguration()
        {
            return configuration;
        }

        @Override
        public void setConfiguration(RelativeTimeIntervalRuleConfiguration configuration)
        {
            // do nothing as this is just for testing
        }
    }
}
