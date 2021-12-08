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
package org.ikasan.ootb.scheduler.agent.module.component.router;

import org.ikasan.ootb.scheduled.model.Outcome;
import org.ikasan.ootb.scheduler.agent.module.component.router.configuration.BlackoutRouterConfiguration;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.ikasan.spec.component.routing.RouterException;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * Router to determine whether the scheduled process event has fired in a defined blackout period.
 *
 * @author Ikasan Development Team
 *
 */
public class BlackoutRouter implements SingleRecipientRouter<ScheduledProcessEvent>,
    ConfiguredResource<BlackoutRouterConfiguration>
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(BlackoutRouter.class);

    public static String OUTSIDE_BLACKOUT_PERIOD = "Outside Blackout Period";
    public static String INSIDE_BLACKOUT_PERIOD = "Inside Blackout Period";
    String configuredResourceId;
    BlackoutRouterConfiguration configuration = new BlackoutRouterConfiguration();

    @Override
    public String route(ScheduledProcessEvent messageToRoute) throws RouterException
    {
        Date now = new Date();
        if(configuration.getCronExpressions() != null && configuration.getCronExpressions().size() > 0)
        {
            for(String cronExpression : configuration.getCronExpressions())
            {
                try
                {
                    CronExpression cronExpressionObj = new CronExpression(cronExpression);
                    if(cronExpressionObj.isSatisfiedBy(now))
                    {
                        messageToRoute.setOutcome(Outcome.EXECUTION_IGNORED_INSIDE_BLACKOUT_WINDOW);
                        return INSIDE_BLACKOUT_PERIOD;
                    }
                }
                catch (ParseException e)
                {
                    logger.warn("Failed to parse cronExpression [" +cronExpression + "]. Please fix configuration", e);
                }
            }
        }

        if(configuration.getDateTimeRanges() != null && configuration.getDateTimeRanges().size() > 0)
        {
            for(Map.Entry<String,String> dateRangeEntry : configuration.getDateTimeRanges().entrySet())
            {
                long from = Long.parseLong(dateRangeEntry.getKey());
                long to = Long.parseLong(dateRangeEntry.getValue());
                long fireTime = messageToRoute.getFireTime();
                if(fireTime >= from && fireTime <= to)
                {
                    messageToRoute.setOutcome(Outcome.EXECUTION_IGNORED_INSIDE_BLACKOUT_WINDOW);
                    return INSIDE_BLACKOUT_PERIOD;
                }
            }
        }

        return OUTSIDE_BLACKOUT_PERIOD;
    }

    @Override
    public String getConfiguredResourceId()
    {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public BlackoutRouterConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(BlackoutRouterConfiguration configuration)
    {
        this.configuration = configuration;
    }
}
