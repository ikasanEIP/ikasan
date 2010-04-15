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
