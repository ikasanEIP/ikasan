/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.common.util;

import org.apache.log4j.Logger;

/**
 * This class provides very simple static utility methods for adjusting poll
 * timers based on requirements of the caller.
 *
 * @author Jeff Mitchell
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
