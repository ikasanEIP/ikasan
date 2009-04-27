/* 
 * $Id: ScheduledDrivenQuartzContext.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/initiator/scheduled/quartz/ScheduledDrivenQuartzContext.java $
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

import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.initiator.InitiatorContext;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;

/**
 * Ikasan default recovery context implementation.
 * 
 * @author Ikasan Development Team
 *
 */
public class ScheduledDrivenQuartzContext
    implements InitiatorContext
{
    /** constant for IkasanExceptionAction jobDetail map entry */
    private static final String RETRY_ACTION = "retryAction";
    /** constant for retry count jobDetail map entry */
    private static final String RETRY_COUNT = "retryCount";
    /** Quartz job context */
    private JobExecutionContext jobExecutionContext;
    /** Quartz job detail */
    private JobDetail jobDetail;
    
    /**
     * Constructor
     * @param jobExecutionContext
     */
    public ScheduledDrivenQuartzContext(JobExecutionContext jobExecutionContext)
    {
        this.jobExecutionContext = jobExecutionContext;
        this.jobDetail = jobExecutionContext.getJobDetail();
    }
    
    /**
     * Get the Quartz job detail
     * @return jobDetail
     */
    public JobDetail getJobDetail()
    {
        return this.jobDetail;
    }

    /**
     * Get the Quartz trigger that fired the job.
     * @return trigger
     */
    public Trigger getTrigger()
    {
        return this.jobExecutionContext.getTrigger();
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.InitiatorContext#setIkasanExceptionAction(org.ikasan.framework.exception.IkasanExceptionAction)
     */
    public void setIkasanExceptionAction(IkasanExceptionAction action)
    {
        this.jobDetail.getJobDataMap().put(RETRY_ACTION, action);
        this.setRetryCount(0);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.InitiatorContext#getIkasanExceptionAction()
     */
    public IkasanExceptionAction getIkasanExceptionAction()
    {
        return (IkasanExceptionAction)this.jobDetail.getJobDataMap().get(RETRY_ACTION);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.InitiatorContextImpl#setRetryCount(int)
     */
    public void setRetryCount(int retryCount)
    {
        this.jobDetail.getJobDataMap().put(RETRY_COUNT, retryCount);
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.InitiatorContextImpl#getRetryCount()
     */
    public int getRetryCount()
    {
        Integer count = (Integer)this.jobDetail.getJobDataMap().get(RETRY_COUNT);
        if(count == null)
            return 0;
        
        return count.intValue();
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.initiator.InitiatorContextImpl#clearRetry()
     */
    public void clearRetry()
    {
        JobDataMap jdm = this.jobDetail.getJobDataMap();
        jdm.remove(RETRY_ACTION);
        jdm.remove(RETRY_COUNT);
    }

}
