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

import java.util.Map;

import junit.framework.Assert;

import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.recovery.ScheduledRecoveryManagerFactory;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * This test class supports the <code>ScheduledRecoveryManagerFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledRecoveryManagerFactoryTest
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
    
    /** Mock consumer flowElement */
    final Consumer<?> consumer = mockery.mock(Consumer.class, "mockConsumer");

    /** Mock exception resolver */
    final ExceptionResolver exceptionResolver = mockery.mock(ExceptionResolver.class, "mockExceptionResolver");

    /** Mock scheduler */
    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");

    /** Mock scheduledJobFactory */
    final ScheduledJobFactory scheduledJobFactory = mockery.mock(ScheduledJobFactory.class, "mockScheduledJobFactory");

    /** Mock scheduledJobs */
    final Map<String,Job> scheduledJobs = mockery.mock(Map.class, "mockScheduledJobs");

    /** Mock job */
    final Job job = mockery.mock(Job.class, "mockJob");

    /** Mock schedledRecoveryManager */
    final ScheduledRecoveryManager scheduledRecoveryManager = 
        mockery.mock(ScheduledRecoveryManager.class, "mockScheduledRecoveryManager");

    /**
     * Test failed constructor due to null scheduler.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructorDueToNullScheduler()
    {
        new ScheduledRecoveryManagerFactory(null);
    }

    /**
     * Test successful get recovery instantiation.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_getRecovery_instance() throws SchedulerException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the recovery manager instance
                exactly(1).of(scheduledJobFactory).getScheduledJobs();
                will(returnValue(scheduledJobs));
                
                exactly(1).of(scheduledJobs).put("recoveryJob_flowNamemoduleName", scheduledRecoveryManager);
            }
        });

        ScheduledRecoveryManagerFactory recoveryManagerFactory = new StubbedScheduledRecoveryManagerFactory(scheduler);
        Assert.assertNotNull(recoveryManagerFactory.getRecoveryManager("flowName", "moduleName", consumer));
        
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful get recovery instantiation with resolver.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_getRecovery_instance_with_resolver() throws SchedulerException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the recovery manager instance
                exactly(1).of(scheduledJobFactory).getScheduledJobs();
                will(returnValue(scheduledJobs));
                
                exactly(1).of(scheduledJobs).put("recoveryJob_flowNamemoduleName", scheduledRecoveryManager);
            }
        });

        ScheduledRecoveryManagerFactory recoveryManagerFactory = new StubbedScheduledRecoveryManagerFactory(scheduler, exceptionResolver);
        Assert.assertNotNull(recoveryManagerFactory.getRecoveryManager("flowName", "moduleName", consumer));
        
        mockery.assertIsSatisfied();
    }

    /**
     * Test failed recovery manager factory instantiation
     * @throws SchedulerException 
     */
    @Test(expected = RuntimeException.class)
    public void test_failed_instantiation() throws SchedulerException
    {
        new StubbedScheduledRecoveryManagerFactory(null);
    }


    /**
     * Extended ScheduledRecoveryManagerFactory for testing with replacement mocks.
     * @author Ikasan Development Team
     *
     */
    private class StubbedScheduledRecoveryManagerFactory extends ScheduledRecoveryManagerFactory
    {

        public StubbedScheduledRecoveryManagerFactory(Scheduler scheduler)
        {
            super(scheduler);
        }
        
        public StubbedScheduledRecoveryManagerFactory(Scheduler scheduler, ExceptionResolver exceptionResolver)
        {
            super(scheduler, exceptionResolver);
        }
        
        @Override
        protected ScheduledRecoveryManager getRecoveryManagerInstance(String flowName, String moduleName, Consumer consumer)
        {
            return scheduledRecoveryManager;
        }

        @Override
        protected ScheduledJobFactory getScheduledJobFactory()
        {
            return scheduledJobFactory;
        }
    }

}
