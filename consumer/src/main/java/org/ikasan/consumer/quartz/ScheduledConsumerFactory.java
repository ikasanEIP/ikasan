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

import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.EventFactory;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;

/**
 * Scheduled based consumer Factory provides
 * consumer instances using a single scheduler and job factory
 * for coordination.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledConsumerFactory
{
    /** Quartz Scheduler */
    private Scheduler scheduler;

    /** event factory */
    private EventFactory eventFactory;
    
    /**
     * Constructor
     * @param scheduler
     * @param scheduledConsumerJobFactory
     */
    public ScheduledConsumerFactory(Scheduler scheduler, EventFactory eventFactory)
    {
        this.scheduler = scheduler;
        if(scheduler == null)
        {
            throw new IllegalArgumentException("scheduler cannot be 'null'");
        }
        
        this.eventFactory = eventFactory;
        if(eventFactory == null)
        {
            throw new IllegalArgumentException("eventFactory cannot be 'null'");
        }
        
    }

    public Consumer getScheduledConsumer(String flowName, String moduleName)
    {
        JobDetail jobDetail = new JobDetail();
        jobDetail.setJobClass(ScheduledConsumer.class);
        jobDetail.setName(flowName);
        jobDetail.setGroup(moduleName);
        ScheduledConsumer scheduledConsumer = new ScheduledConsumer(this.scheduler, jobDetail, this.eventFactory);

        // add the new instance to the cached jobs job factory 
        Map<String,Job> jobs = ScheduledJobFactory.getInstance().getScheduledJobs();
        jobs.put(flowName + moduleName, (Job)scheduledConsumer);

        return scheduledConsumer;
    }
}
