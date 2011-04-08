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
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Scheduled recovery manager job factory implementation.
 * This allows multiple different recovery job instances to be handled 
 * through one scheduler instance.
 * Each recovery job instance is cached and passed back as the invocable job
 * based on the job name and group on newJob call back.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledRecoveryManagerJobFactory implements JobFactory
{
    /** recovery job name */
    protected static final String RECOVERY_JOB_NAME = "recoveryJob_";
    
    /** recovery job group */
    protected static final String RECOVERY_JOB_GROUP = "recoveryManager_";
    
    /** singleton instance */
    private static ScheduledRecoveryManagerJobFactory scheduledRecoveryManagerJobFactory;
    
    /** map of recovery jobs */
    private Map<String,Job> recoveries = new ConcurrentHashMap<String,Job>();
    
    /**
     * Singleton instance accessor
     * @return
     */
    public static ScheduledRecoveryManagerJobFactory getInstance()
    {
        if(scheduledRecoveryManagerJobFactory == null)
        {
            scheduledRecoveryManagerJobFactory = new ScheduledRecoveryManagerJobFactory();
        }
        
        return scheduledRecoveryManagerJobFactory;
    }
    
    /**
     * Constructor
     */
    private ScheduledRecoveryManagerJobFactory()
    {
        // nothing to do
    }
    
    /**
     * Add the job instance to the cache of known jobs keyed on their flow name
     * and module name.
     * @param flowName
     * @param moduleName
     * @param job
     */
    public void addJob(String flowName, String moduleName, Job job)
    {
        recoveries.put(RECOVERY_JOB_NAME + flowName + RECOVERY_JOB_GROUP + moduleName, job);
    }
    
    /**
     * Callback from the JobFactory.
     * @param triggerFiredBundle
     */
    public Job newJob(TriggerFiredBundle triggerFiredBundle) throws SchedulerException
    {
        JobDetail jobDetail = triggerFiredBundle.getJobDetail();
        String jobKey = jobDetail.getName() + jobDetail.getGroup();
        return recoveries.get(jobKey);
    }
}
