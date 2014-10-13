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

import org.ikasan.scheduler.CachingScheduledJobFactory;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.scheduler.SchedulerFactory;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.recovery.RecoveryManager;
import org.quartz.Scheduler;

/**
 * Recovery Manager Factory provides recovery manager instances 
 * currently only based on a the Ikasan Quartz scheduler for scheduled recovery 
 * management.
 * 
 * @author Ikasan Development Team
 */
public class RecoveryManagerFactory
{
    /** Quartz Scheduler */
    private Scheduler scheduler;
    
    /** Ikasan extended Quartz job factory */
    private ScheduledJobFactory scheduledJobFactory;
    
    /**
     * Default implementation of a RecoveryManagerFactory instance.
     * @return RecoveryManagerFactory
     */
    public static RecoveryManagerFactory getInstance()
    {
    	return new RecoveryManagerFactory(SchedulerFactory.getInstance().getScheduler(), CachingScheduledJobFactory.getInstance());
    }
    
    /**
     * Constructor
     * @param scheduler
     * @param scheduledJobFactory
     */
    public RecoveryManagerFactory(Scheduler scheduler, ScheduledJobFactory scheduledJobFactory)
    {
        this.scheduler = scheduler;
        if(scheduler == null)
        {
            throw new IllegalArgumentException("scheduler cannot be 'null'");
        }

        this.scheduledJobFactory = scheduledJobFactory;
        if(scheduledJobFactory == null)
        {
            throw new IllegalArgumentException("scheduledJobFactory cannot be 'null'");
        }
    }

    /**
     * Create a new recovery manager instance based on the incoming parameters.
     * 
     * @param flowName
     * @param moduleName
     * @param consumer
     * @return RecoveryManager
     */
    public RecoveryManager getRecoveryManager(String flowName, String moduleName, Consumer consumer, ExclusionService exclusionService)
    {
        return new ScheduledRecoveryManager(scheduler, scheduledJobFactory, flowName, moduleName, consumer, exclusionService);
    }
    
}
