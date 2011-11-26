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

import java.text.ParseException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;

/**
 * This test class tests the scheduling and callback for jobs within Quartz.
 * 
 * @author Ikasan Development Team
 */
public class SchedulerCallbackExecutionTest implements Job
{
    /** context to be populated on callback */
    private JobExecutionContext context;

    /** caching scheduled job factory instance on test */
    private ScheduledJobFactory scheduledJobFactory;

    @Before
    public void setup()
    {
        this.context = null;
        this.scheduledJobFactory = new CachingScheduledJobFactory();
    }
    
    /**
     * Test successful registration of a job with the scheduler and associated callback.
     * @throws SchedulerException 
     * @throws ParseException 
     * @throws InterruptedException 
     */
    @Test
    public void test_createJobDetail() throws SchedulerException, ParseException, InterruptedException
    {
        Scheduler scheduler = SchedulerFactory.getInstance().getScheduler();
        JobDetail jobDetail = CachingScheduledJobFactory.getInstance().createJobDetail(this, "name", "group");
        Trigger trigger = newTrigger().withIdentity("name", "group").withSchedule(cronSchedule("0/5 * * * * ?")).build();
        scheduler.scheduleJob(jobDetail, trigger);
        Assert.assertNull(this.context);
        Thread.sleep(6000);
        Assert.assertNotNull(this.context);
    }

    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        this.context = context;
    }
    
}

