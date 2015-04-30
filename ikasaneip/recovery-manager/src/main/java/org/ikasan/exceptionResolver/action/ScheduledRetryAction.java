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
package org.ikasan.exceptionResolver.action;

/**
 * Exception Action indicating the operation should be retried within the context of a cron expressed schedule.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledRetryAction implements ExceptionAction
{
    /**
     * indicator for an infinite retry
     */
    public static final Integer RETRY_INFINITE = -1;

    /**
     * cron expression
     */
    private String cronExpression;

    /**
     * Maximum no of times to retry
     */
    private int maxRetries = RETRY_INFINITE;

    /**
     * Default Constructor
     */
    public ScheduledRetryAction()
    {
        // Do Nothing
    }

    /**
     * Constructor
     *
     * @param cronExpression - cron expression for the retry
     * @param maxRetries - The maximum number of retries to attempt
     */
    public ScheduledRetryAction(String cronExpression, int maxRetries)
    {
        super();
        this.cronExpression = cronExpression;
        this.maxRetries = maxRetries;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    /**
     * Accessor for maxRetries
     * 
     * @return maxRetries
     */
    public int getMaxRetries()
    {
        return maxRetries;
    }

    @Override
    public String toString()
    {
        return "ScheduledRetry (cronExpression=" + cronExpression + ", maxRetries=" + maxRetries + ")";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object)
    {
        if(object instanceof ScheduledRetryAction)
        {
            // is same object type
            ScheduledRetryAction retryAction = (ScheduledRetryAction) object;
            if(this.getCronExpression() == retryAction.getCronExpression() && this.getMaxRetries() == retryAction.getMaxRetries())
            {
                return true;
            }
        }

        return false;
    }

    /**
     * HashCode default implementation
     * 
     * @return int hashcode
     */
    @Override
    public int hashCode()
    {
        int hash = 1;
        hash = hash * 31 + this.maxRetries;
        hash = hash * 31 + this.cronExpression.hashCode();
        return hash;
    }
}