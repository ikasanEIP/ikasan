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
package org.ikasan.framework.initiator.scheduled.quartz;

import org.apache.log4j.Logger;
import org.ikasan.framework.initiator.AbortTransactionException;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.StatefulJob;


/**
 * Quartz stateful job implementation. This is implemented as a 'Stateful' Quartz job to ensure only single
 * non-concurrent invocations are invoked at any one time.
 * 
 * @author Ikasan Development Team
 */
public class QuartzStatefulJob implements StatefulJob
{
    /** Must have a handle on to the initiator we will invoke */
    private QuartzDrivenInitiator initiator;
    
    private static Logger logger = Logger.getLogger(QuartzStatefulJob.class); 

    /**
     * Constructor
     * 
     * @param initiator The initiator
     */
    public QuartzStatefulJob(QuartzDrivenInitiator initiator)
    {
        this.initiator = initiator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext ctx)
    {
        // Invoke the initiator
    	JobDataMap mergedJobDataMap = ctx.getMergedJobDataMap();
    	
    	boolean problemsEncountered = false;
    	
    	
    	//initial invocation
    	problemsEncountered = invokeInitiator(mergedJobDataMap);
        
        while(!problemsEncountered && Boolean.TRUE.equals(mergedJobDataMap.get(QuartzStatefulScheduledDrivenInitiator.REINVOKE_IMMEDIATELY_FLAG))){
        	//repeat invocations
        	logger.info("reinvoking initiator immediately");
        	problemsEncountered = invokeInitiator(mergedJobDataMap);
        }
        
    }

	private boolean invokeInitiator(JobDataMap mergedJobDataMap) {
		boolean transactionAborted = false;
		try
        {
            this.initiator.invoke(mergedJobDataMap);
        }
        catch (AbortTransactionException e)
        {
            // These exceptions can be ignored as they have already
            // been dealt with by the Transaction Manager proxy class
        	transactionAborted = true;
        }
        return transactionAborted;
	}
}
