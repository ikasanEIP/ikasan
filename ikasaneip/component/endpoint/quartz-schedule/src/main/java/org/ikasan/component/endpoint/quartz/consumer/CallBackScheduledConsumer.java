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

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.spec.event.ForceTransactionRollbackException;
import org.ikasan.spec.management.ManagedResource;
import org.quartz.*;

/**
 * This implements the CallBackMessageConsumer contract supporting message provider callbacks.
 *
 * @author Ikasan Development Team
 */
@DisallowConcurrentExecution
public class CallBackScheduledConsumer<T> extends ScheduledConsumer implements CallBackMessageConsumer<T>
{
    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(CallBackScheduledConsumer.class);

    /**
     * default messageProvider is set to QuartzMessageProvider - can be overridden via the setter
     */
    private CallBackMessageProvider messageProvider;

    /**
     * Constructor
     *
     * @param scheduler
     */
    public CallBackScheduledConsumer(Scheduler scheduler)
    {
        super(scheduler);
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
            boolean isSuccessful = messageProvider.invoke(context);
            if(this.getConfiguration().isEager() && isSuccessful)
            {
                Trigger trigger = context.getTrigger();

                // potentially more data so use eager trigger
                if(isSuccessful)
                {
                    invokeEagerSchedule(trigger);
                }
                // no more data and if callback is from an eager trigger then switch back to the business trigger
                else if(isEagerCallback(trigger))
                {
                    scheduleAsBusinessTrigger(trigger);
                }

                // else do not change the business trigger
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

    public void setCallBackMessageProvider(CallBackMessageProvider messageProvider)
    {
        this.messageProvider = messageProvider;
    }

    @Override
    public void startManagedResource()
    {
        if(messageProvider instanceof ManagedResource)
        {
            ((ManagedResource)messageProvider).startManagedResource();
        }
    }

    @Override
    public void stopManagedResource()
    {
        if(messageProvider instanceof ManagedResource)
        {
            ((ManagedResource)messageProvider).stopManagedResource();
        }
    }
}
