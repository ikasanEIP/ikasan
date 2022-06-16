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
package org.ikasan.component.endpoint.quartz.consumer;


import org.ikasan.spec.configuration.IsValidationAware;
import org.ikasan.spec.configuration.InvalidConfigurationException;

import java.util.*;

/**
 * Scheduled consumer configuration bean.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledConsumerConfiguration implements IsValidationAware
{
    /** name for this schedule */
    private String jobName;

    /** group name for this schedule */
    private String jobGroupName;

    /** description for this schedule */
    private String description;

    /** cron based expression for this schedule */
    private String cronExpression;

    /** allow multiple cron based expressions for this schedule */
    private List<String> cronExpressions = new ArrayList<>();

    /** whether to ignore a misfire - default true */
    private boolean ignoreMisfire = true;

    /** Determines whether consumer will be eagerly executing after successful run */
    private boolean eager = false;

    /** maximum number of consecutive eager scheduled callbacks before reverting to business schedule - default 0 = unlimited */
    private int maxEagerCallbacks;

    /** a valid optional timezone to set on the scheduled job
     *  a default of blank or null will use the JVM's timezone */
    private String timezone = TimeZone.getDefault().getID();

    /** generic properties to be passed into the job at schedule time and subsequently passed back on schedule execution */
    private Map<String,String> passthroughProperties = new HashMap<String,String>();

    /** allow for persistent recovery of a schedule - default true */
    private boolean persistentRecovery = true;

    /** tolerance period in millis within which it makes sense to rerun a schedule if it was missed - default 30 minutes */
    private long recoveryTolerance = 30 * 60 * 1000;

    public String getJobName()
    {
        return jobName;
    }

    public void setJobName(String jobName)
    {
        this.jobName = jobName;
    }

    public String getJobGroupName()
    {
        return jobGroupName;
    }

    public void setJobGroupName(String jobGroupName)
    {
        this.jobGroupName = jobGroupName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Map<String, String> getPassthroughProperties()
    {
        return passthroughProperties;
    }

    public void setPassthroughProperties(Map<String, String> passthroughProperties)
    {
        this.passthroughProperties = passthroughProperties;
    }

    public String getCronExpression()
    {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    public boolean isEager() {
        return eager;
    }

    public int getMaxEagerCallbacks() {
        return maxEagerCallbacks;
    }

    public void setMaxEagerCallbacks(int maxEagerCallbacks) {
        this.maxEagerCallbacks = maxEagerCallbacks;
    }

    public void setEager(boolean eager) {
        this.eager = eager;
    }

    public void setIgnoreMisfire(boolean ignoreMisfire)
    {
        this.ignoreMisfire = ignoreMisfire;
    }

    public boolean isIgnoreMisfire()
    {
        return this.ignoreMisfire;
    }

    public String getTimezone()
    {
        return timezone;
    }

    public void setTimezone(String timezone)
    {
        this.timezone = timezone;
    }

    public long getRecoveryTolerance()
    {
        return recoveryTolerance;
    }

    public void setRecoveryTolerance(long recoveryTolerance)
    {
        this.recoveryTolerance = recoveryTolerance;
    }

    public boolean isPersistentRecovery()
    {
        return persistentRecovery;
    }

    public void setPersistentRecovery(boolean persistentRecovery)
    {
        this.persistentRecovery = persistentRecovery;
    }

    public List<String> getCronExpressions()
    {
        return cronExpressions;
    }

    public List<String> getConsolidatedCronExpressions()
    {
        List<String> allCronExpressions = new ArrayList<String>(cronExpressions.size() + 1);
        if(cronExpression != null)
        {
            allCronExpressions.add(cronExpression);
        }

        for(String expression:cronExpressions)
        {
            allCronExpressions.add(expression);
        }

        return allCronExpressions;
    }

    public void setCronExpressions(List<String> cronExpressions)
    {
        this.cronExpressions = cronExpressions;
    }

    @Override
    public void validate() throws InvalidConfigurationException
    {
        if(getConsolidatedCronExpressions().size() == 0)
        {
            throw new InvalidConfigurationException("At least one cronExpression must be specified.");
        }
    }
}
