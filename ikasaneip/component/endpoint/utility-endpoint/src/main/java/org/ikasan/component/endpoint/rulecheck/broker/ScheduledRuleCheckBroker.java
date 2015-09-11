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
package org.ikasan.component.endpoint.rulecheck.broker;

import org.apache.log4j.Logger;
import org.ikasan.component.endpoint.rulecheck.Rule;
import org.ikasan.component.endpoint.rulecheck.RuleCheckConfiguration;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.quartz.*;

import java.text.ParseException;
import java.util.Date;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Simple scheduled rule invoker.
 * @author Ikasan Development Team
 * @param <EVENT>
 */
public class ScheduledRuleCheckBroker<EVENT> implements Broker<EVENT,EVENT>, ManagedResource,ConfiguredResource<RuleCheckConfiguration>, Job
{
    /**
     * logger
     */
    private static Logger logger = Logger.getLogger(ScheduledRuleCheckBroker.class);

    /**
     * Scheduler
     */
    private Scheduler scheduler;

    /**
     * job detail wired by spring config.
     */
    private JobDetail jobDetail;

    /** is this a critical resource to cause startup failure */
    private boolean criticalOnStartup;

    /**
     * Recovery manager for this Managed Resource and any extending implementations of it
     */
    protected ManagedResourceRecoveryManager managedResourceRecoveryManager;

    /** unique identifier for the configuration of this component */
    private String configuredResourceId;

    /**
     * db configuration
     */
    private RuleCheckConfiguration configuration;

    /** rule to check */
    private Rule rule;

    /**
     * Constructor
     *
     * @param scheduler
     */
    public ScheduledRuleCheckBroker(Scheduler scheduler, Rule rule)
    {
        this.scheduler = scheduler;
        if (scheduler == null)
        {
            throw new IllegalArgumentException("scheduler cannot be 'null'");
        }

        this.rule = rule;
        if (rule == null)
        {
            throw new IllegalArgumentException("rule cannot be 'null'");
        }
    }

    @Override
    public EVENT invoke(EVENT event) throws EndpointException
    {
        this.rule.update(event);
        return event;
    }

    /**
     * Start the scheduled job and triggers
     */
    public void startManagedResource()
    {
        try
        {
            // configure the rule is possible
            if(rule instanceof Configured)
            {
                ((Configured)rule).setConfiguration(this.configuration);
            }

            // reset the rule on start of the component
            this.rule.rebase();

            // create trigger
            JobKey jobkey = jobDetail.getKey();
            Trigger trigger = getCronTrigger(jobkey, this.configuration.getCronExpression());
            Date scheduledDate = scheduler.scheduleJob(jobDetail, trigger);
            logger.info("Scheduled rule check for ["
                    + jobkey.getName()
                    + "] [" + jobkey.getGroup()
                    + "] starting at [" + scheduledDate + "]");
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stop the scheduled job and triggers
     */
    public void stopManagedResource()
    {
        try
        {
            JobKey jobKey = jobDetail.getKey();
            if (this.scheduler.checkExists(jobKey))
            {
                this.scheduler.deleteJob(jobKey);
            }
        }
        catch (SchedulerException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        this.managedResourceRecoveryManager = managedResourceRecoveryManager;
    }

    @Override
    public boolean isCriticalOnStartup()
    {
        return criticalOnStartup;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup)
    {
        this.criticalOnStartup = criticalOnStartup;
    }

    /**
     * Method factory for creating a cron trigger
     *
     * @return jobDetail
     * @throws java.text.ParseException
     */
    protected Trigger getCronTrigger(JobKey jobkey, String cronExpression) throws ParseException
    {
        return newTrigger().withIdentity(jobkey.getName(), jobkey.getGroup()).withSchedule(cronSchedule(cronExpression))
                .build();
    }

    @Override
    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public RuleCheckConfiguration getConfiguration()
    {
        return this.configuration;
    }

    @Override
    public void setConfiguration(RuleCheckConfiguration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
    {
        try
        {
            rule.check(jobExecutionContext);
        }
        catch(Throwable t)
        {
            this.managedResourceRecoveryManager.recover(t);
        }
    }

    public void setJobDetail(JobDetail jobDetail)
    {
        this.jobDetail = jobDetail;
    }
}
