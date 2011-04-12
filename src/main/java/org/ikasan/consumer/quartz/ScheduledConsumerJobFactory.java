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
package org.ikasan.consumer.quartz;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * This test class supports the <code>Translator</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledConsumerJobFactory implements JobFactory
{
    /** singleton instance */
    private static ScheduledConsumerJobFactory scheduledConsumerJobFactory;
    
    /** map of consumers */
    private Map<String,Job> consumers;

    /**
     * Singleton instance accessor
     * @return ScheduledConsumerJobFactory
     */
    public static ScheduledConsumerJobFactory getInstance()
    {
        if(scheduledConsumerJobFactory == null)
        {
            scheduledConsumerJobFactory = new ScheduledConsumerJobFactory();
        }
        
        return scheduledConsumerJobFactory;
    }
    
    /**
     * Constructor
     */
    protected ScheduledConsumerJobFactory()
    {
        this.consumers = getConsumerCache();
    }
    
    /**
     * Add consumer to the job factory
     * @param flowName
     * @param moduleName
     * @param job
     */
    public void addConsumer(String flowName, String moduleName, Job job)
    {
        consumers.put(flowName + moduleName, job);
    }
    
    /**
     * Callback from Quartz to get the actual job to execute.
     */
    public Job newJob(TriggerFiredBundle triggerFiredBundle) throws SchedulerException
    {
        JobDetail jobDetail = triggerFiredBundle.getJobDetail();
        String jobKey = jobDetail.getName() + jobDetail.getGroup();
        return consumers.get(jobKey);
    }

    /**
     * Factory method for creating an initial consumers cache.
     * @return
     */
    protected Map<String,Job> getConsumerCache()
    {
        return new ConcurrentHashMap<String,Job>();
    }
    
}
