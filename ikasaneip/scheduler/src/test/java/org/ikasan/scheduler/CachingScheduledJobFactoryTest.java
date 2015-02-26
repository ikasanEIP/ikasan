/* 
 * $Id: SchedulerFactoryTest.java 3629 2011-04-18 10:00:52Z mitcje $
 * $URL: http://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/scheduler/src/test/java/org/ikasan/scheduler/SchedulerFactoryTest.java $
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
package org.ikasan.scheduler;

import org.junit.Assert;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import java.util.Map;

/**
 * This test class supports the <code>CachingScheduledJobFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
public class CachingScheduledJobFactoryTest
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

    /** Mock cache */
    final Map mockCache = mockery.mock(Map.class, "mockCache");

    /** Mock defaultJobFactory */
    final JobFactory mockDefaultJobFactory = mockery.mock(JobFactory.class, "mockDefaultJobFactory");

    /** Mock job */
    final Job job = mockery.mock(Job.class, "mockJob");

    /** Mock triggerFiredBundle */
    final TriggerFiredBundle triggerFiredBundle = mockery.mock(TriggerFiredBundle.class, "mockTriggerFiredBundle");
    
    /** Mock jobDetail */
    final JobDetail jobDetail = mockery.mock(JobDetail.class, "mockJobDetail");
    
    /**
     * Test successful create jobDetail instance.
     * @throws SchedulerException 
     */
    @Test
    public void test_createJobDetail() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("name", "group");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockCache).put(jobKey, job);
            }
        });

        ScheduledJobFactory scheduledJobFactory = new StubbedCachingScheduledJobFactory();
        Assert.assertTrue( scheduledJobFactory.createJobDetail(job, job.getClass(), "name", "group") instanceof JobDetail );
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test successful new job callback where job is found in the cache.
     * @throws SchedulerException 
     */
    @Test
    public void test_newJob_callback_found_in_cache() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("name", "group");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(triggerFiredBundle).getJobDetail();
                will(returnValue(jobDetail));
                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));
                exactly(1).of(mockCache).get(jobKey);
                will(returnValue(job));
            }
        });

        ScheduledJobFactory scheduledJobFactory = new StubbedCachingScheduledJobFactory();
        Assert.assertTrue( scheduledJobFactory.newJob(triggerFiredBundle, scheduler) instanceof Job);
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test successful new job callback where job is not found in the cache.
     * @throws SchedulerException 
     */
    @Test
    public void test_newJob_callback_not_found_in_cache() throws SchedulerException
    {
        final JobKey jobKey = new JobKey("name", "group");
        
        // expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(triggerFiredBundle).getJobDetail();
                will(returnValue(jobDetail));
                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));
                exactly(1).of(mockCache).get(jobKey);
                will(returnValue(null));
                exactly(1).of(mockDefaultJobFactory).newJob(triggerFiredBundle, scheduler);
                will(returnValue(job));
            }
        });

        ScheduledJobFactory scheduledJobFactory = new StubbedCachingScheduledJobFactory();
        Assert.assertTrue( scheduledJobFactory.newJob(triggerFiredBundle, scheduler) instanceof Job);
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test class extends SchedulerJobFactory to allow mock substitution.
     * @author Ikasan Development Team
     *
     */
    private class StubbedCachingScheduledJobFactory extends CachingScheduledJobFactory
    {
        protected StubbedCachingScheduledJobFactory()
        {
            this.cachedJobs = mockCache;
            this.defaultJobFactory = mockDefaultJobFactory;
        }

    }
}
