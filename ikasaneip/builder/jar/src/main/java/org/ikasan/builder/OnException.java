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
package org.ikasan.builder;

import org.ikasan.exceptionResolver.action.*;

/**
 * Helper for Exception Actions.
 * 
 * @author Ikasan Development Team
 */
public class OnException
{
    /**
     * Stop the flow in error
     * @return
     */
    public static ExceptionAction stop()
    {
        return StopAction.instance();
    }

    /**
     * Ignore the exception and continue
     * @return
     */
    public static ExceptionAction ignoreException()
    {
        return IgnoreAction.instance();
    }

    /**
     * Rollback any actions resulting from this inflight event and exclude it
     * @return
     */
    public static ExceptionAction excludeEvent()
    {
        return ExcludeEventAction.instance();
    }

    /**
     * Continually retry with a delay period specified in millis
     * @param delay period to wait between retries
     * @return
     */
    public static ExceptionAction retryIndefinitely(long delay)
    {
        RetryAction retryAction = new RetryAction();
        retryAction.setDelay(delay);
        return retryAction;
    }

    /**
     * Continually retry with a default delay period of 5 seconds
     * @return
     */
    public static ExceptionAction retryIndefinitely()
    {
        return new RetryAction();
    }

    /**
     * Retry up to a maximum number of attempts based on the specified delay between attempts.
     * @param delay period to wait between retries
     * @param maxRetries limit the number of continuous retries for the same exception
     * @return
     */
    public static ExceptionAction retry(long delay, int maxRetries)
    {
        return new RetryAction(delay, maxRetries);
    }

    /**
     * Retry based on a given cron expression up to a maximum number of attempts
     * @param cronExpression
     * @param maxRetries
     * @return
     */
    public static ExceptionAction scheduledCronRetry(String cronExpression, int maxRetries)
    {
        return new ScheduledRetryAction(cronExpression, maxRetries);
    }
}
