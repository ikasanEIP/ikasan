/* 
 * $Id: SchedulerFactoryTest.java 3817 2011-10-30 23:40:35Z mitcje $
 * $URL: https://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/scheduler/src/test/java/org/ikasan/scheduler/SchedulerFactoryTest.java $
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
import org.junit.Before;
import org.junit.Test;
import org.quartz.*;

import java.text.ParseException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * This test class tests the scheduling and callback for jobs within Quartz.
 * 
 * @author Ikasan Development Team
 */

//TODO - do we really need this test class? it looks to be checking the functionality of the DisallowConcurrentExecution annotation on Quartz

public class SchedulerCallbackExecutionTest
{
    protected CountDownLatch latch;
    
    @Before
    public void setup()
    {
        this.latch = new CountDownLatch(1);
    }
    
    /**
     * Test successful registration of a job with the scheduler and associated concurrent callbacks.
     * @throws SchedulerException 
     * @throws ParseException 
     * @throws InterruptedException 
     */
    @Test
    public void test_createJobDetail_concurrentCallbacks() throws SchedulerException, ParseException, InterruptedException
    {
        Scheduler scheduler = SchedulerFactory.getInstance().getScheduler();
        
        ConcurrentCallbackJob job = new ConcurrentCallbackJob(6000);
        JobDetail jobDetail = CachingScheduledJobFactory.getInstance().createJobDetail(job, ConcurrentCallbackJob.class, "name", "group");
        Trigger trigger = newTrigger().withIdentity("name", "group").withSchedule(cronSchedule("0/1 * * * * ?")).build();
        scheduler.scheduleJob(jobDetail, trigger);
        Assert.assertTrue("a callback should have occurred", latch.await(5, TimeUnit.SECONDS));
        latch = new CountDownLatch(1);
        Assert.assertTrue("another callback should have occurred", latch.await(3, TimeUnit.SECONDS));
    }

    /**
     * Test successful registration of a job with the scheduler and associated NON concurrent callbacks.
     * @throws SchedulerException 
     * @throws ParseException 
     * @throws InterruptedException 
     */
    @Test
    public void test_createJobDetail_non_concurrentCallbacks() throws SchedulerException, ParseException, InterruptedException
    {
        Scheduler scheduler = SchedulerFactory.getInstance().getScheduler();
        
        NonConcurrentCallbackJob job = new NonConcurrentCallbackJob(15000);

        JobDetail jobDetail = CachingScheduledJobFactory.getInstance().createJobDetail(job, NonConcurrentCallbackJob.class, "name2", "group");
        Trigger trigger = newTrigger().withIdentity("name2", "group").withSchedule(cronSchedule("0/1 * * * * ?")).build();
        scheduler.scheduleJob(jobDetail, trigger);
        Assert.assertTrue("a callback should have occurred", latch.await(5, TimeUnit.SECONDS));
        latch = new CountDownLatch(1);
        Assert.assertFalse("no further callbacks should have occurred", latch.await(3, TimeUnit.SECONDS));
    }

    /**
     * Job implementation allowing concurrent callbacks from Quartz
     * @author Ikasan Development Team
     *
     */
    private class ConcurrentCallbackJob implements Job
    {
    	protected long sleep;
    	
    	public ConcurrentCallbackJob(long sleep)
    	{
    		this.sleep = sleep;
    	}
    	
    	public void execute(JobExecutionContext context) throws JobExecutionException
        {
            latch.countDown();
            try
            {
            	Thread.sleep(sleep);
            }
            catch(InterruptedException e)
            {
            	Assert.fail("Thread woken early");
            }
        }
    }

    /**
     * Job implementation NOT allowing concurrent callbacks from Quartz
     * @author Ikasan Development Team
     *
     */
    @DisallowConcurrentExecution
    private class NonConcurrentCallbackJob extends ConcurrentCallbackJob
    {
    	public NonConcurrentCallbackJob(long sleep)
    	{
    		super(sleep);
    	}
    }
}

