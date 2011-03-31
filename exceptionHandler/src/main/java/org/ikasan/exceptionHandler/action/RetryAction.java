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
package org.ikasan.exceptionHandler.action;

/**
 * Exception Action indicating the operation should be retried
 * 
 * @author Ikasan Development Team
 */
public class RetryAction implements ExceptionAction
{
    /**
     * indicator for an infinite retry
     */
    public static final Integer RETRY_INFINITE = -1;

    /**
     * Length of time in milliseconds between retries
     */
    private long delay = 5000l;

    /**
     * Maximum no of times to retry
     */
    private int maxRetries = RETRY_INFINITE;

    /**
     * Default Constructor
     */
    public RetryAction()
    {
        // Do Nothing
    }

    /**
     * Constructor
     * 
     * @param delay - The delay in milliseconds before retrying
     * @param maxRetries - The maximum number of retries to attempt
     */
    public RetryAction(long delay, int maxRetries)
    {
        super();
        this.delay = delay;
        this.maxRetries = maxRetries;
    }

    /**
     * Accessor for delay
     * 
     * @return delay
     */
    public long getDelay()
    {
        return delay;
    }

    /**
     * Mutator for delay
     * 
     * @param delay
     */
    public void setDelay(long delay)
    {
        this.delay = delay;
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
        return "Retry (delay=" + delay + ", maxRetries=" + maxRetries + ")";
    }
}
