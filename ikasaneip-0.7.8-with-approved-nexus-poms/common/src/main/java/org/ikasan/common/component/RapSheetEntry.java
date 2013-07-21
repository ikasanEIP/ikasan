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
package org.ikasan.common.component;

import org.apache.log4j.Logger;

import org.ikasan.common.CommonException;
import org.ikasan.common.ExceptionType;

/**
 * RapSheetEntry provides a bean the ability to maintain stats on offending
 * throwable exceptions.
 * 
 * A list of RapSheetEntries is passed to the ExceptionHandler to provide
 * additional historic details of this beans encountered FCEs.
 * 
 * @author Ikasan Development Team
 * 
 */
public class RapSheetEntry
{
    /** Logger */
    private static Logger logger = Logger.getLogger(RapSheetEntry.class);
    /** time in millis this RapSheetEntry last happened */
    private long lastOccurrence = 0;
    /** Actual offense that occurred */
    private final Throwable offense;
    /** Running total count ie total number of FCE occurrences */
    private int totalCount;
    /** Consecutive count ie number of times this FCE has consecutively happened */
    private int consecutiveCount;

    /**
     * Default constructor
     * 
     * @param offense cause of the rapSheet entry
     */
    public RapSheetEntry(final Throwable offense)
    {
        this.offense = offense;
        this.lastOccurrence = java.util.Calendar.getInstance()
            .getTimeInMillis();
        this.totalCount = 1;
        this.consecutiveCount = 1;
    }

    /**
     * Getter for consecutive count. This is the consecutive number of times
     * this offense has occurred.
     * 
     * @return consecutiveCount
     */
    public int getConsecutiveCount()
    {
        logger
            .debug("Getting consecutiveCount [" + this.consecutiveCount + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.consecutiveCount;
    }

    /**
     * Setter for consecutive count. This is the consecutive number of times
     * this offense has occurred.
     * 
     * @param consecutiveCount
     */
    public void setConsecutiveCount(final int consecutiveCount)
    {
        this.consecutiveCount = consecutiveCount;
        logger.debug("Setting consecutiveCount [" + this.consecutiveCount //$NON-NLS-1$
                + "]"); //$NON-NLS-1$
    }

    /**
     * Getter for the offense for which the stats are being maintained.
     * 
     * @return offense
     */
    public Throwable getOffense()
    {
        logger.debug("Getting offense [" + this.offense + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.offense;
    }

    /**
     * Getter for the last occurrence time (in millis) of this offense.
     * 
     * @return lastOccurrence
     */
    public long getLastOccurrence()
    {
        logger.debug("Getting lastOccurrence [" + this.lastOccurrence + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.lastOccurrence;
    }

    /**
     * Setter for the last occurrence time (in millis) of this offense.
     * 
     * @param lastOccurrence
     */
    public void setLastOccurrence(final long lastOccurrence)
    {
        this.lastOccurrence = lastOccurrence;
        logger.debug("Setting lastOccurrence [" + this.lastOccurrence + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Getter for the total number of occurrences of this offense.
     * 
     * @return totalCount
     */
    public int getTotalCount()
    {
        logger.debug("Getting totalCount [" + this.totalCount + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.totalCount;
    }

    /**
     * Setter for the total number of occurrences of this offense.
     * 
     * @param totalCount
     */
    public void setTotalCount(final int totalCount)
    {
        this.totalCount = totalCount;
        logger.debug("Setting totalCount [" + this.totalCount + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @param throwable
     * @return boolean
     */
    public boolean equals(Throwable throwable)
    {
        // get details on incoming throwable for comparison
        ExceptionType incomingExceptionType = null;
        if (throwable instanceof CommonException)
            incomingExceptionType = ((CommonException) throwable)
                .getExceptionType();
        // get details of this offense for comparison
        ExceptionType offenseExceptionType = null;
        if (this.offense instanceof CommonException)
            offenseExceptionType = ((CommonException) this.offense)
                .getExceptionType();
        // compare the two
        if (this.offense.getClass().equals(throwable.getClass())
                && this.offense.getMessage().equals(throwable.getMessage()))
        {
            if (incomingExceptionType == null && offenseExceptionType == null)
                return true;
            if (incomingExceptionType != null
                    && incomingExceptionType.equals(offenseExceptionType))
                return true;
        }
        return false;
    }

    /** 
     * Main TODO Unit Test
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        Throwable t1 = new Exception("Bad Stuff2");
        Throwable t2 = new Exception("Bad Stuff");
        RapSheetEntry rse = new RapSheetEntry(t1);
        System.out.println("Offense [" + rse.getOffense().getMessage() + "]");
        System.out.println("Last Occurrence [" + rse.getLastOccurrence() + "]");
        System.out.println("Consecutive Count [" + rse.getConsecutiveCount()
                + "]");
        System.out.println("Total Count [" + rse.getTotalCount() + "]");
        boolean same = rse.equals(t2);
        if (same)
            System.out.println("they are the same");
        else
            System.out.println("they are not the same");
    }
}
