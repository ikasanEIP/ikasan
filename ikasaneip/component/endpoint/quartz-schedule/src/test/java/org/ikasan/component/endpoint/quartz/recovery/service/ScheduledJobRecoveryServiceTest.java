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
package org.ikasan.component.endpoint.quartz.recovery.service;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.*;

import java.util.Date;

/**
 * This test class supports the <code>ScheduledJobRecovery</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledJobRecoveryServiceTest
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

    /** Mock jobexcecutioncontext */
    private final JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class, "mockJobExecutionContext");

    /** Mock trigger */
    private final Trigger trigger = mockery.mock(Trigger.class, "mockTrigger");

    /**
     * Test.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_start_no_recovery_required_firetime_in_future() throws SchedulerException
    {
        final TriggerKey triggerKey = new TriggerKey("flowName", "moduleName");
        final Date fireTime = new Date();
        final Date nextFireTime = new Date(System.currentTimeMillis() + 10000L);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get flow and module name from the trigger
                exactly(1).of(jobExecutionContext).getTrigger();
                will(returnValue(trigger));

                exactly(1).of(trigger).getKey();
                will(returnValue(triggerKey));

                // get firetime
                exactly(1).of(jobExecutionContext).getFireTime();
                will(returnValue(fireTime));

                // get next firetime
                exactly(1).of(jobExecutionContext).getNextFireTime();
                will(returnValue(nextFireTime));
            }
        });

        ScheduledJobRecoveryService scheduledJobRecoveryService =
            ScheduledJobRecoveryServiceFactory.getInstance();

        scheduledJobRecoveryService.save(jobExecutionContext);
        Assert.assertFalse( scheduledJobRecoveryService.isRecoveryRequired("moduleName", "flowName", 0l) );

        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     * @throws SchedulerException
     */
    @Test
    public void test_successful_start_no_recovery_required_firetime_in_past_beyond_tolerance() throws SchedulerException
    {
        final TriggerKey triggerKey = new TriggerKey("flowName", "moduleName");
        final Date fireTime = new Date();
        final Date nextFireTime = new Date(System.currentTimeMillis() - 10000L);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get flow and module name from the trigger
                exactly(1).of(jobExecutionContext).getTrigger();
                will(returnValue(trigger));

                exactly(1).of(trigger).getKey();
                will(returnValue(triggerKey));

                // get firetime
                exactly(1).of(jobExecutionContext).getFireTime();
                will(returnValue(fireTime));

                // get next firetime
                exactly(1).of(jobExecutionContext).getNextFireTime();
                will(returnValue(nextFireTime));
            }
        });

        ScheduledJobRecoveryService scheduledJobRecoveryService =
            ScheduledJobRecoveryServiceFactory.getInstance();

        scheduledJobRecoveryService.save(jobExecutionContext);
        Assert.assertFalse( scheduledJobRecoveryService.isRecoveryRequired("moduleName", "flowName", 5l) );

        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     * @throws SchedulerException
     */
    @Test
    public void test_successful_start_no_recovery_required_due_to_no_persistence() throws SchedulerException
    {
        final TriggerKey triggerKey = new TriggerKey("flowName", "moduleName");
        final Date fireTime = new Date();
        final Date nextFireTime = new Date(System.currentTimeMillis() + 10000L);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get flow and module name from the trigger
                exactly(1).of(jobExecutionContext).getTrigger();
                will(returnValue(trigger));

                exactly(1).of(trigger).getKey();
                will(returnValue(triggerKey));

                // get firetime
                exactly(1).of(jobExecutionContext).getFireTime();
                will(returnValue(fireTime));

                // get next firetime
                exactly(1).of(jobExecutionContext).getNextFireTime();
                will(returnValue(nextFireTime));
            }
        });

        ScheduledJobRecoveryService scheduledJobRecoveryService =
            ScheduledJobRecoveryServiceFactory.getInstance();

        scheduledJobRecoveryService.save(jobExecutionContext);
        Assert.assertFalse( scheduledJobRecoveryService.isRecoveryRequired("moduleName", "NoMatchflowName", 0l) );

        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     * @throws SchedulerException
     */
    @Test
    public void test_successful_start_recovery_required_firetime_in_past_within_tolerance() throws SchedulerException
    {
        final TriggerKey triggerKey = new TriggerKey("flowName", "moduleName");
        final Date fireTime = new Date();
        final Date nextFireTime = new Date(System.currentTimeMillis() - 100L);

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get flow and module name from the trigger
                exactly(1).of(jobExecutionContext).getTrigger();
                will(returnValue(trigger));

                exactly(1).of(trigger).getKey();
                will(returnValue(triggerKey));

                // get firetime
                exactly(1).of(jobExecutionContext).getFireTime();
                will(returnValue(fireTime));

                // get next firetime
                exactly(1).of(jobExecutionContext).getNextFireTime();
                will(returnValue(nextFireTime));
            }
        });

        ScheduledJobRecoveryService scheduledJobRecoveryService =
            ScheduledJobRecoveryServiceFactory.getInstance();

        scheduledJobRecoveryService.save(jobExecutionContext);
        Assert.assertTrue( scheduledJobRecoveryService.isRecoveryRequired("flowName", "moduleName", 10000l) );

        mockery.assertIsSatisfied();
    }
}
