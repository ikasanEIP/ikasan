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
package org.ikasan.component.endpoint.quartz.consumer;

import org.ikasan.component.endpoint.quartz.recovery.service.ScheduledJobRecoveryService;
import org.ikasan.spec.event.ForceTransactionRollbackException;
import org.ikasan.spec.event.ForceTransactionRollbackForEventExclusionException;
import org.quartz.*;

import java.util.Set;

/**
 * This implements the CallBackMessageConsumer contract supporting message provider callbacks.
 *
 * @author Ikasan Development Team
 */
@DisallowConcurrentExecution
public class CallBackScheduledConsumer<T> extends ScheduledConsumer implements CallBackMessageConsumer<T>
{
    /**
     * default messageProvider is set to QuartzMessageProvider - can be overridden via the setter
     */
    private CallBackMessageProvider<Boolean> messageProvider;

    /**
     * Constructor
     * @param scheduler
     */
    public CallBackScheduledConsumer(Scheduler scheduler)
    {
        super(scheduler);
    }

    /**
     * Constructor
     * @param scheduler
     * @param scheduledJobRecoveryService
     */
    public CallBackScheduledConsumer(Scheduler scheduler, ScheduledJobRecoveryService scheduledJobRecoveryService)
    {
        super(scheduler, scheduledJobRecoveryService);
    }

    @Override
    public void start()
    {
        if(this.messageProvider == null)
        {
            throw new IllegalArgumentException("callBackMessageProvider not set. Check you are setting this via the setCallBackMessageProvider(CallBackMessageProvider ..) method.");
        }

        super.start();
    }

    /**
     * Callback from the scheduler.
     *
     * @param context
     */
    @Override
    public void execute(JobExecutionContext context)
    {
        try
        {
            boolean isRecovering = managedResourceRecoveryManager.isRecovering();

            // TODO - should this be saved after the event invocation?
            // only persist schedule for misfire if a business schedule ie not a recovery manager schedule
            if(!isRecovering && this.consumerConfiguration.isPersistentRecovery())
            {
                this.scheduledJobRecoveryService.save(context);
            }

            boolean isSuccessful = messageProvider.invoke(context).booleanValue();

            // we were recovering, but are now ok so restore eager or business trigger
            if(isRecovering)
            {
                if(this.getConfiguration().isEager() && isSuccessful)
                {
                    invokeEagerSchedule(context.getTrigger());
                }
                else
                {
                    scheduleAsBusinessTrigger(context.getTrigger());
                }

                // cancel any remnants of the recovery
                this.managedResourceRecoveryManager.cancel();
            }
            else
            {
                if(this.getConfiguration().isEager())
                {
                    // potentially more data so use eager trigger
                    if(isSuccessful)
                    {
                        invokeEagerSchedule(context.getTrigger());
                    }
                    // no more data and if callback is from an eager trigger then switch back to the business trigger
                    else if(isEagerCallback(context.getTrigger()))
                    {
                        scheduleAsBusinessTrigger(context.getTrigger());
                    }

                    // else do not change the business trigger
                }
                else
                {
                    // if persistent recovery callback then reschedule business cron
                    if(isPersistentRecoveryTrigger(context.getTrigger()))
                    {
                        scheduleAsBusinessTrigger(context.getTrigger());
                    }
                }
            }
        }
        catch (ForceTransactionRollbackForEventExclusionException thrownByRecoveryManager)
        {
            // reschedule immediately to allow the event to be excluded
            // assumes we will get the same event again
            try
            {
                scheduleAsEagerTrigger(context.getTrigger(), 0);
                throw thrownByRecoveryManager;
            }
            catch (SchedulerException e)
            {
                throw new RuntimeException(e);
            }
        }
        catch (ForceTransactionRollbackException thrownByRecoveryManager)
        {
            throw thrownByRecoveryManager;
        }
        catch (Throwable thr)
        {
            managedResourceRecoveryManager.recover(thr);
        }
    }

    public void setCallBackMessageProvider(CallBackMessageProvider<Boolean> messageProvider)
    {
        this.messageProvider = messageProvider;
    }
}
