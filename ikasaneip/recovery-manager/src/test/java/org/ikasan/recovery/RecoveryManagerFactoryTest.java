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
package org.ikasan.recovery;

import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.recovery.RecoveryManagerFactory;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * This test class supports the <code>RecoveryManagerFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
public class RecoveryManagerFactoryTest
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
    
    /** Mock consumer flowElement */
    final Consumer<?,?> consumer = mockery.mock(Consumer.class, "mockConsumer");

    /** Mock scheduler */
    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");

    /** Mock exclusion service */
    final ExclusionService exclusionService = mockery.mock(ExclusionService.class, "mockExclusionService");

    /** Mock error reporting service */
    final ErrorReportingService errorReportingService = mockery.mock(ErrorReportingService.class, "mockErrorReportingService");

    /** Mock scheduledJobFactory */
    final ScheduledJobFactory scheduledJobFactory = mockery.mock(ScheduledJobFactory.class, "mockScheduledJobFactory");

    /**
     * Test failed constructor due to null scheduler.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullScheduler()
    {
        new ScheduledRecoveryManagerFactory(null, null);
    }

    /**
     * Test failed constructor due to null scheduledJobFactory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullScheduledJobFactory()
    {
        new ScheduledRecoveryManagerFactory(scheduler, null);
    }

    /**
     * Test successful get recovery instantiation.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_getRecovery_instance() throws SchedulerException
    {
        RecoveryManagerFactory recoveryManagerFactory = new ScheduledRecoveryManagerFactory(scheduler, scheduledJobFactory);
        Assert.assertTrue(recoveryManagerFactory.getRecoveryManager("flowName", "moduleName") instanceof ScheduledRecoveryManager);
        
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful get recovery instantiation with resolver.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_getRecovery_instance_with_resolver() throws SchedulerException
    {
        RecoveryManagerFactory recoveryManagerFactory = new ScheduledRecoveryManagerFactory(scheduler, scheduledJobFactory);
        Assert.assertTrue(recoveryManagerFactory.getRecoveryManager("flowName", "moduleName") instanceof ScheduledRecoveryManager);
        
        mockery.assertIsSatisfied();
    }

}
