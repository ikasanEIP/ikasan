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
package org.ikasan.framework.initiator.scheduled.quartz;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;
import org.ikasan.framework.initiator.InvocationDrivenInitiator;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Ikasan specific implementation of the Quartz JobFactory. This is required to allow us to pass a handle to the
 * Initiator instance on every new job instance created by a callback from the scheduler.
 * 
 * @author Ikasan Development Team
 */
public class IkasanJobFactory implements JobFactory
{
    /** Logger */
    private static Logger logger = Logger.getLogger(IkasanJobFactory.class);

    /** Scheduled initiator instance passed on creation of the scheduled job */
    private InvocationDrivenInitiator initiator;

    /**
     * Constructor
     * 
     * @param initiator The initiator for this job factory
     */
    public IkasanJobFactory(final InvocationDrivenInitiator initiator)
    {
        this.initiator = initiator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.quartz.spi.JobFactory#newJob(org.quartz.spi.TriggerFiredBundle)
     */
    @SuppressWarnings("unchecked")
    public Job newJob(TriggerFiredBundle bundle) throws SchedulerException
    {
        JobDetail jobDetail = bundle.getJobDetail();
        Class<Job> jobClass = jobDetail.getJobClass();
        if (logger.isDebugEnabled())
        {
            logger.debug("Producing instance of Job '" + jobDetail.getFullName() + "', class=" + jobClass.getName());
        }
        try
        {
            Class<?>[] paramTypes = { InvocationDrivenInitiator.class };
            Object[] paramArgs = { this.initiator };
            Constructor<Job> con = jobClass.getConstructor(paramTypes);
            return con.newInstance(paramArgs);
        }
        catch (Exception e)
        {
            SchedulerException se = new SchedulerException("Problem instantiating class '"
                    + jobDetail.getJobClass().getName() + "'", e);
            throw se;
        }
    }
}