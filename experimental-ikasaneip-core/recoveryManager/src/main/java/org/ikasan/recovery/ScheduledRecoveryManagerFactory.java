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

import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.recovery.RecoveryManager;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * Scheduled based Recovery Manager Factory provides
 * recovery manager instances using a single scheduler and job factory
 * for coordination.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledRecoveryManagerFactory
{
    /** Quartz Scheduler */
    private Scheduler scheduler;
    
    /** Exception Resolver */
    private ExceptionResolver exceptionResolver;
    
    /** quartz job factory for the recovery manager callbacks */
    private ScheduledRecoveryManagerJobFactory recoveryManagerJobFactory = ScheduledRecoveryManagerJobFactory.getInstance();

    /**
     * Constructor
     * @param scheduler
     * @param exceptionResolver
     */
    public ScheduledRecoveryManagerFactory(Scheduler scheduler, ExceptionResolver exceptionResolver)
    {
        this.scheduler = scheduler;
        if(scheduler == null)
        {
            throw new IllegalArgumentException("scheduler cannot be 'null'");
        }
        
        this.exceptionResolver = exceptionResolver;
        
        try
        {
            this.scheduler.setJobFactory(recoveryManagerJobFactory);
            this.scheduler.start();
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor
     * @param flowElement
     * @param scheduler
     */
    public ScheduledRecoveryManagerFactory(Scheduler scheduler)
    {
        this(scheduler, null);
    }

    /**
     * Create a new recovery manager instance based on the incoming parameters.
     * This new recovery manager will utilise a single factory configured 
     * scheduler for call back coordination.
     * 
     * @param flowName
     * @param moduleName
     * @param consumer
     * @return
     */
    public RecoveryManager getRecoveryManager(String flowName, String moduleName, Consumer consumer)
    {
        ScheduledRecoveryManager recoveryManager = new ScheduledRecoveryManager(scheduler, flowName, moduleName, consumer, exceptionResolver);
        recoveryManagerJobFactory.addJob(flowName, moduleName, (Job)recoveryManager);
        return recoveryManager;
    }
    
}
