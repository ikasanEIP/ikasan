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
 * Exception Action indicating the operation should be retried
 * 
 * @author Ikasan Development Team
 */
public class SimpleRetryAction implements ExceptionAction, HasAlternateFinalAction, RetryAction
{
    /**
     * Length of time in milliseconds between retries
     */
    private long delay = 5000l;

    /**
     * Maximum no of times to retry
     */
    private int maxRetries = RETRY_INFINITE;

    /**
     * final action if we hit max retries
     */
    private ExceptionAction finalAction = StopAction.instance();

    /**
     * Default Constructor
     */
    public SimpleRetryAction()
    {
        // Do Nothing
    }

    /**
     * Constructor
     *
     * @param delay - The delay in milliseconds before retrying
     * @param maxRetries - The maximum number of retries to attempt
     */
    public SimpleRetryAction(long delay, int maxRetries)
    {
        super();
        this.delay = delay;
        this.maxRetries = maxRetries;
    }

    /**
     * Constructor
     *
     * @param delay - The delay in milliseconds before retrying
     * @param maxRetries - The maximum number of retries to attempt
     * @param finalAction - final action to take when we exhaust the max retries
     */
    public SimpleRetryAction(long delay, int maxRetries, ExceptionAction finalAction)
    {
        super();
        this.delay = delay;
        this.maxRetries = maxRetries;
        this.finalAction = finalAction;
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
    public ExceptionAction getFinalAction()
    {
        return this.finalAction;
    }

    @Override
    public String toString()
    {
        return "Retry (delay=" + delay + ", maxRetries=" + maxRetries + ", finalAction=" + finalAction + ")";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object)
    {
        if(object instanceof SimpleRetryAction)
        {
            // is same object type
            SimpleRetryAction retryAction = (SimpleRetryAction) object;
            if(this.getDelay() == retryAction.getDelay() && this.getMaxRetries() == retryAction.getMaxRetries() && this.getFinalAction().equals(retryAction.getFinalAction()))
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
        hash = hash * 31 + (int)this.delay;
        hash = hash * 31 + finalAction.hashCode();
        return hash;
    }
}