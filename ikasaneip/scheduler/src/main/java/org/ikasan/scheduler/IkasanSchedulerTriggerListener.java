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

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trigger Listener implementation for the Ikasan platform scheduler.
 *
 * @author Ikasan Development Team.
 */
public class IkasanSchedulerTriggerListener implements TriggerListener, ScheduledTriggerListener
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(IkasanSchedulerTriggerListener.class);

    /**
     * Constructor
     */
    protected IkasanSchedulerTriggerListener(){};

    @Override public String getName()
    {
        return ScheduledTriggerListener.PLATFORM_TRIGGER_LISTENER;
    }

    @Override public void triggerFired(Trigger trigger, JobExecutionContext context)
    {
        if(logger.isDebugEnabled())
        {
            logger.debug(getName() + " Trigger fired for [" + trigger.getKey() + "] at " + trigger.getStartTime()
                    + " for job [" + trigger.getJobKey());
        }
    }

    @Override public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context)
    {
        return false;
    }

    @Override public void triggerMisfired(Trigger trigger)
    {
        logger.warn(getName() + " Misfire. Trigger [" + trigger.getKey() + "] misfired at " + trigger.getStartTime()
        + " for job [" + trigger.getJobKey());
    }

    @Override public void triggerComplete(Trigger trigger, JobExecutionContext context,
                                          Trigger.CompletedExecutionInstruction triggerInstructionCode)
    {
        if(logger.isDebugEnabled())
        {
            logger.debug(getName() + " Trigger complete for [" + trigger.getKey() + "] at " + trigger.getStartTime()
                    + " for job [" + context.getJobDetail().getKey());
        }
    }
}
