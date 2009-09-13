/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.common.util;

import org.apache.log4j.Logger;

/**
 * This class provides very simple static utility methods for adjusting poll
 * timers based on requirements of the caller.
 *
 * @author Ikasan Development Team
 */
public class PollDancer
{
    /** Logger */
    private static Logger logger = Logger.getLogger(PollDancer.class);
    
    /** The default percentage adjustment */
    public final static long DEFAULT_PERCENTAGE_ADJUSTMENT = 10;
    
    /** initial pollTime */
    private long initialPollTime;
    /** The maximum poll time */
    private long maxPollTime;
    /** The minimum poll time */
    private long minPollTime;
    /** The current poll time */
    private long currentPollTime;
    /** The adjustment percentage */
    private long adjustmentPercentage;
    
    /**
     * Default constructor for PollDancer.
     * This will create a pollDancer instance based on the following defaults,
     * initialPollTime - 3 seconds
     * maxPollTime = 10 seconds
     * minPollTime = 1 second
     * adjustmentPercentage = 10%
     */
    public PollDancer()
    {
        this.initialPollTime = 3000;
        this.maxPollTime = 10000;
        this.minPollTime = 1000;
        this.adjustmentPercentage = 10;

        /** default current to initial */
        this.currentPollTime = this.initialPollTime;
    }
    
    /**
     * Constructor for PollDancer
     * @param initialPollTime - initially configured pollTime prior to 
     * any adjustments
     * @param adjustmentPercentage - % by which the pollTime will up adjusted 
     * up or down
     * @param minPollTime - minimum allowable pollTime
     * @param maxPollTime - maximum allowable pollTime
     */
    public PollDancer(long initialPollTime, long adjustmentPercentage,
            long minPollTime, long maxPollTime)
    {
        this.initialPollTime = initialPollTime;
        this.maxPollTime = maxPollTime;
        this.minPollTime = minPollTime;
        this.adjustmentPercentage = adjustmentPercentage;

        /** default current to initial */
        this.currentPollTime = initialPollTime;
    }
    
    /**
     * Constructor for PollDancer. Maximum allowable pollTime defaulted 
     * from initialPollTime.
     * @param initialPollTime
     * @param adjustmentPercentage
     * @param minPollTime
     */
    public PollDancer(long initialPollTime, long adjustmentPercentage,
            long minPollTime)
    {
        this(initialPollTime, adjustmentPercentage, minPollTime, initialPollTime);
    }
    
    /**
     * Constructor for PollDancer. Minimum allowable pollTime defaulted to 1.
     * Maximum allowable pollTime defaulted from initialPollTime.
     * @param initialPollTime
     * @param adjustmentPercentage
     */
    public PollDancer(long initialPollTime, long adjustmentPercentage)
    {
        this(initialPollTime, adjustmentPercentage, 1000, initialPollTime);
    }
    
    /**
     * Utility method to reduce the polltime to accommodate higher levels
     * of traffic. This method implements a simple divisor algorithm lowering 
     * the pollTime on each invocation down to a minimum prescribed pollTime.
     * @return reduced pollTime
     */
    public long decreasePollTimeAlg1()
    {
        /** dont go below minimum pollTime */
        if(this.currentPollTime <= this.minPollTime)
        {
            return this.currentPollTime;
        }

        /** dont adjust by zero percentage */
        if(this.adjustmentPercentage == 0)
        {
            return this.currentPollTime;
        }
            
        /** adjust pollTime */
        long savedCurrentPollTime = this.currentPollTime;
        long adjustAmount = (this.currentPollTime * this.adjustmentPercentage)/100;
        if(this.currentPollTime - adjustAmount < this.minPollTime)
        {
            this.currentPollTime = this.minPollTime;
        }
        else
        {
            this.currentPollTime = this.currentPollTime - adjustAmount;
        }
        
        logger.info("Current pollTime [" + savedCurrentPollTime + "] " //$NON-NLS-1$ //$NON-NLS-2$
                + "changed to [" + this.currentPollTime + "]."); //$NON-NLS-1$ //$NON-NLS-2$

        return this.currentPollTime;
    }

    /**
     * Utility method to increase the polltime to accommodate lower levels
     * of traffic. This method implements a simple divisor algorithm increasing 
     * the pollTime on each invocation up to a maximum prescribed pollTime.
     * @return reduced pollTime
     */
    public long increasePollTimeAlg1()
    {
        /** dont go above maximum pollTime */
        if(this.currentPollTime >= this.maxPollTime)
        {
            return this.currentPollTime;
        }

        /** dont adjust by zero percentage */
        if(this.adjustmentPercentage == 0)
        {
            return this.currentPollTime;
        }
            
        /** adjust pollTime */
        long savedCurrentPollTime = this.currentPollTime;
        long adjustAmount = (this.currentPollTime * this.adjustmentPercentage)/100;
        if (adjustAmount <= 0) adjustAmount = 1L;
        if(this.currentPollTime + adjustAmount > this.maxPollTime)
        {
            this.currentPollTime = this.maxPollTime;
        }
        else
        {
            this.currentPollTime = this.currentPollTime + adjustAmount;
        }
        
        logger.info("Current pollTime [" + savedCurrentPollTime + "] " //$NON-NLS-1$ //$NON-NLS-2$
                + "changed to [" + this.currentPollTime + "]."); //$NON-NLS-1$ //$NON-NLS-2$

        return this.currentPollTime;
    }
    
    /**
     * Returns the original initial pollTime provided on the creation of this
     * instance.
     * @return original pollTime
     */
    public long restorePollTime()
    {
        return this.initialPollTime;
    }
    
    /**
     * Returns the current pollTime provided on the creation of this
     * instance.
     * @return current pollTime
     */
    public long getCurrentPollTime()
    {
        return this.currentPollTime;
    }
    
}
