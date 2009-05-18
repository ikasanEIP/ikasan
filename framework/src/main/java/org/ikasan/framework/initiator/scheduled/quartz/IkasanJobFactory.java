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

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Ikasan specific implementation of the Quartz JobFactory. This is required to allow us to pass a handle to the
 * Initiator instance on every new job instance created by a callback from the scheduler.
 * 
 * @author Ikasan Development Team
 */
public class IkasanJobFactory implements JobFactory
{
    /** Logger */
    private static Logger logger = Logger.getLogger(IkasanJobFactory.class);

    /** Scheduled initiator instance passed on creation of the scheduled job */
    private QuartzStatefulScheduledDrivenInitiator initiator;

    /**
     * Constructor
     * 
     * @param initiator The initiator for this job factory
     */
    public IkasanJobFactory(final QuartzStatefulScheduledDrivenInitiator initiator)
    {
        this.initiator = initiator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.quartz.spi.JobFactory#newJob(org.quartz.spi.TriggerFiredBundle)
     */
    @SuppressWarnings("unchecked")
    public Job newJob(TriggerFiredBundle bundle) throws SchedulerException
    {
        JobDetail jobDetail = bundle.getJobDetail();
        Class<Job> jobClass = jobDetail.getJobClass();
        if (logger.isDebugEnabled())
        {
            logger.debug("Producing instance of Job '" + jobDetail.getFullName() + "', class=" + jobClass.getName());
        }
        try
        {
            Class<?>[] paramTypes = { QuartzStatefulScheduledDrivenInitiator.class };
            Object[] paramArgs = { this.initiator };
            Constructor<Job> con = jobClass.getConstructor(paramTypes);
            return con.newInstance(paramArgs);
        }
        catch (Exception e)
        {
            SchedulerException se = new SchedulerException("Problem instantiating class '"
                    + jobDetail.getJobClass().getName() + "'", e);
            throw se;
        }
    }
}