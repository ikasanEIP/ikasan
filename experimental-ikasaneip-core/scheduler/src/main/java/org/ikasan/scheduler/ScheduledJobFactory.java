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
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Scheduled job factory implementation.
 * This allows multiple different job instances to be handled 
 * through one scheduler instance.
 * Each job instance is cached and passed back as the invokable job
 * based on the job name and group on newJob call back.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledJobFactory implements JobFactory
{
    /** singleton instance */
    private static ScheduledJobFactory scheduledJobFactory;
    
    /** map of scheduled jobs */
    protected Map<String,Job> scheduledJobs;
    
    /**
     * Singleton instance accessor
     * @return
     */
    public static ScheduledJobFactory getInstance()
    {
        if(scheduledJobFactory == null)
        {
            scheduledJobFactory = new ScheduledJobFactory();
        }
        
        return scheduledJobFactory;
    }
    
    /**
     * Constructor
     */
    protected ScheduledJobFactory()
    {
        this.scheduledJobs = new ConcurrentHashMap<String,Job>();
    }

    /**
     * Getter for the map of scheduled jobs
     * @return
     */
    public Map<String,Job> getScheduledJobs()
    {
        return this.scheduledJobs;
    }
    
    /**
     * Callback from the JobFactory.
     * @param triggerFiredBundle
     */
    public Job newJob(TriggerFiredBundle triggerFiredBundle) throws SchedulerException
    {
        JobDetail jobDetail = triggerFiredBundle.getJobDetail();
        String jobKey = jobDetail.getName() + jobDetail.getGroup();
        return scheduledJobs.get(jobKey);
    }
}
