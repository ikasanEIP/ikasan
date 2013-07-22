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
package org.ikasan.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Scheduled job factory implementation for returning cached job instances on callback.
 * Each job instance is cached and passed back as the invokable job
 * based on the job name and group on newJob call back. If the instance has
 * not been cached then the default job scheduler instantiates a new instance
 * and returns that.
 * 
 * @author Ikasan Development Team
 */
public class CachingScheduledJobFactory implements ScheduledJobFactory
{
    /** singleton instance */
    private static ScheduledJobFactory cachingScheduledJobFactory;
    
    /** 
     * Default job factory to delegate job creation if
     * this class doesn't have a cached instance of the job.
     */
    protected JobFactory defaultJobFactory;
    
    /** map of cached scheduled jobs */
    protected Map<JobKey,Job> cachedJobs;
    
    /**
     * Singleton instance accessor
     * @return ScheduledJobFactory
     */
    public static ScheduledJobFactory getInstance()
    {
        if(cachingScheduledJobFactory == null)
        {
            cachingScheduledJobFactory = new CachingScheduledJobFactory();
        }
        
        return cachingScheduledJobFactory;
    }
    
    /**
     * Constructor
     */
    protected CachingScheduledJobFactory()
    {
        this.cachedJobs = new ConcurrentHashMap<JobKey,Job>();
        this.defaultJobFactory = new SimpleJobFactory();
    }

    /**
     * Callback from the JobFactory.
     * This will locate the job entry in the cache based on the jobKey and return the job.
     * If no job exists then null is returned.
     * @param triggerFiredBundle
     * @param scheduler
     * @return Job
     */
    public Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler) throws SchedulerException
    {
        JobDetail jobDetail = triggerFiredBundle.getJobDetail();
        JobKey jobKey = jobDetail.getKey();
        Job job = cachedJobs.get(jobKey);
        if(job == null)
        {
            // job not previously cached so delegate creation to the defaultJobFactory
            job = defaultJobFactory.newJob(triggerFiredBundle, scheduler);
        }
        return job;
    }

    /**
     * Create the job detail and cache the job instance for invocation on 
     * scheduler callback.
     * @param Job
     * @param name
     * @param group
     * @return JobDetail
     */
    public JobDetail createJobDetail(Job job, String name, String group)
    {
        JobKey jobKey = new JobKey(name, group);
        this.cachedJobs.put(jobKey, job);
        return org.quartz.JobBuilder.newJob( job.getClass() ).withIdentity(name,group).build(); 
    }
}