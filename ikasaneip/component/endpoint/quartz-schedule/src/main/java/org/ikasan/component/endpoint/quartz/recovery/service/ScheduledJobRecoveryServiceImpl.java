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
package org.ikasan.component.endpoint.quartz.recovery.service;

import org.ikasan.component.endpoint.quartz.recovery.dao.ScheduledJobRecoveryDao;
import org.ikasan.component.endpoint.quartz.recovery.model.ScheduledJobRecoveryModel;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Implementation of a ScheduledJobRecovery contract.
 *
 * @author Ikasan Development Team
 */
@SuppressWarnings("unchecked")
public class ScheduledJobRecoveryServiceImpl implements ScheduledJobRecoveryService<JobExecutionContext>
{
    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(ScheduledJobRecoveryServiceImpl.class);

    // handle to the recovery DAO
    ScheduledJobRecoveryDao<ScheduledJobRecoveryModel> scheduledJobRecoveryDao;

    /**
     * Constructor
     * @param scheduledJobRecoveryDao
     */
    public ScheduledJobRecoveryServiceImpl(ScheduledJobRecoveryDao scheduledJobRecoveryDao)
    {
        this.scheduledJobRecoveryDao = scheduledJobRecoveryDao;
        if(scheduledJobRecoveryDao == null)
        {
            throw new IllegalArgumentException("scheduledJobRecoveryDao cannot be 'null'");
        }
    }

    @Override
    public boolean isRecoveryRequired(String name, String group, long tolerance)
    {
        // find the job - if none then assume all ok return false
        ScheduledJobRecoveryModel model = this.scheduledJobRecoveryDao.find(name, group);
        if(model == null)
        {
            return false;
        }

        // if job found then check if last call was within expected tolerance
        return requiresRecover(model, tolerance);
    }

    @Override
    public void removeRecovery(String name, String group)
    {
        this.scheduledJobRecoveryDao.deleteRecovery(name, group);
    }

    @Override
    public void removeAllRecoveries()
    {
        this.scheduledJobRecoveryDao.deleteAllRecoveries();
    }

    @Override
    public void setNextFireTime(String name, String group, Date nextFireTime)
    {
        ScheduledJobRecoveryModel model = this.scheduledJobRecoveryDao.find(name, group);
        if(model == null)
        {
            model = new ScheduledJobRecoveryModel();
        }

        model.setName(name);
        model.setGroup(group);
        model.setNextFireTime(nextFireTime);

        // persist
        this.scheduledJobRecoveryDao.save(model);
    }

    @Override
    public void save(JobExecutionContext jobExecutionContext)
    {
        TriggerKey triggerKey = jobExecutionContext.getTrigger().getKey();
        ScheduledJobRecoveryModel model = new ScheduledJobRecoveryModel();
        model.setName(triggerKey.getName());
        model.setGroup(triggerKey.getGroup());
        model.setFireTime(jobExecutionContext.getFireTime());
        model.setNextFireTime(jobExecutionContext.getNextFireTime());

        // persist
        this.scheduledJobRecoveryDao.save(model);
    }

    /**
     * Check the last executions next fire time compared to now.
     * If its in the future then we are good and no need to recover anything.
     * If in the past or now, then check how far in the past based on the recovery tolerance
     * to determine whether recovery is valid or not.
     *
     * Whether we recover or not is based on the nextFireTime compared to now() + recoveryTolerance in millis.
     *
     * If nextFireTime is 5:30 and we check at 5:31 with 0 recoveryTolerance then we should not recover.
     * If nextFireTime is 5:30 and we check at 5:31 with 2 min recoveryTolerance then we should recover.
     *
     * @param lastExecutionModel
     * @param recoveryTolerance
     * @return
     */
    private boolean requiresRecover(ScheduledJobRecoveryModel lastExecutionModel, long recoveryTolerance)
    {
        // no model or no nextFireTime then no recovery required
        if(lastExecutionModel == null || lastExecutionModel.getNextFireTime() == null)
        {
            return false;
        }

        Date now = new Date();

        // recovery is only needed if the next fire time is in the past or equal to now
        if(lastExecutionModel.getNextFireTime().before(now) || lastExecutionModel.getNextFireTime().equals(now))
        {
            // apply the tolerance ie within how much time in millis of the next fire time is this still recoverable
            Date nextFireTimeWithRecoveryTolerance = new Date(lastExecutionModel.getNextFireTime().getTime() + recoveryTolerance);
            if(nextFireTimeWithRecoveryTolerance.before(now))
            {
                return false;
            }

            return true;
        }

        return false;
    }
}
