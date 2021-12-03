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

import org.ikasan.ootb.scheduled.model.ScheduledProcessEventImpl;
import org.ikasan.ootb.scheduler.agent.module.component.router.BlackoutRouter;
import org.ikasan.ootb.scheduler.agent.module.component.router.configuration.BlackoutRouterConfiguration;
import org.ikasan.spec.scheduled.ScheduledProcessEvent;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * This test class supports the <code>BlackoutRouter</code>.
 *
 * @author Ikasan Development Team
 */
public class BlackoutRouterTest
{
    /**
     * Test simple invocation.
     */
    @Test
    public void test_successful_router_cronExpression_outside_blackout_period()
    {
        ScheduledProcessEvent scheduledProcessEvent = new ScheduledProcessEventImpl();
        scheduledProcessEvent.setFireTime(System.currentTimeMillis());

        List<String> cronExpression = new ArrayList<String>();
        cronExpression.add("0 15 10 * * ? 3000");

        SingleRecipientRouter blackoutRouter = new BlackoutRouter();
        BlackoutRouterConfiguration configuration = new BlackoutRouterConfiguration();
        configuration.setCronExpressions(cronExpression);
        ((ConfiguredResource)blackoutRouter).setConfiguration(configuration);
        String result = blackoutRouter.route(scheduledProcessEvent);

        Assert.assertEquals(result, BlackoutRouter.OUTSIDE_BLACKOUT_PERIOD);
    }

    /**
     * Test simple invocation.
     */
    @Test
    public void test_successful_router_cronExpression_inside_blackout_period()
    {
        ScheduledProcessEvent scheduledProcessEvent = new ScheduledProcessEventImpl();
        scheduledProcessEvent.setFireTime(System.currentTimeMillis());

        List<String> cronExpression = new ArrayList<String>();
        cronExpression.add("*/1 * * * * ?");

        SingleRecipientRouter blackoutRouter = new BlackoutRouter();
        BlackoutRouterConfiguration configuration = new BlackoutRouterConfiguration();
        configuration.setCronExpressions(cronExpression);
        ((ConfiguredResource)blackoutRouter).setConfiguration(configuration);
        String result = blackoutRouter.route(scheduledProcessEvent);

        Assert.assertEquals(result, BlackoutRouter.INSIDE_BLACKOUT_PERIOD);
    }

    /**
     * Test simple invocation.
     */
    @Test
    public void test_successful_router_dateRange_inside_blackout_period()
    {
        ScheduledProcessEvent scheduledProcessEvent = new ScheduledProcessEventImpl();
        scheduledProcessEvent.setFireTime(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, -1);
        long inThePast = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_YEAR, 2);
        long inTheFuture = calendar.getTimeInMillis();

        Map<String,String> dateTimeRanges = new HashMap<>();
        dateTimeRanges.put(String.valueOf(inThePast), String.valueOf(inTheFuture));

        SingleRecipientRouter blackoutRouter = new BlackoutRouter();
        BlackoutRouterConfiguration configuration = new BlackoutRouterConfiguration();
        configuration.setDateTimeRanges(dateTimeRanges);
        ((ConfiguredResource)blackoutRouter).setConfiguration(configuration);
        String result = blackoutRouter.route(scheduledProcessEvent);

        Assert.assertEquals(result, BlackoutRouter.INSIDE_BLACKOUT_PERIOD);
    }

    /**
     * Test simple invocation.
     */
    @Test
    public void test_successful_router_dateRange_outside_before_blackout_period()
    {
        ScheduledProcessEvent scheduledProcessEvent = new ScheduledProcessEventImpl();
        scheduledProcessEvent.setFireTime(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        long inTheFuture1 = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_YEAR, 2);
        long inTheFuture2 = calendar.getTimeInMillis();

        Map<String,String> dateTimeRanges = new HashMap<>();
        dateTimeRanges.put(String.valueOf(inTheFuture1), String.valueOf(inTheFuture2));

        SingleRecipientRouter blackoutRouter = new BlackoutRouter();
        BlackoutRouterConfiguration configuration = new BlackoutRouterConfiguration();
        configuration.setDateTimeRanges(dateTimeRanges);
        ((ConfiguredResource)blackoutRouter).setConfiguration(configuration);
        String result = blackoutRouter.route(scheduledProcessEvent);

        Assert.assertEquals(result, BlackoutRouter.OUTSIDE_BLACKOUT_PERIOD);
    }

    /**
     * Test simple invocation.
     */
    @Test
    public void test_successful_router_dateRange_outside_after_blackout_period()
    {
        ScheduledProcessEvent scheduledProcessEvent = new ScheduledProcessEventImpl();
        scheduledProcessEvent.setFireTime(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, -2);
        long inThePast1 = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_YEAR, -1);
        long inThePast2 = calendar.getTimeInMillis();

        Map<String,String> dateTimeRanges = new HashMap<>();
        dateTimeRanges.put(String.valueOf(inThePast1), String.valueOf(inThePast2));

        SingleRecipientRouter blackoutRouter = new BlackoutRouter();
        BlackoutRouterConfiguration configuration = new BlackoutRouterConfiguration();
        configuration.setDateTimeRanges(dateTimeRanges);
        ((ConfiguredResource)blackoutRouter).setConfiguration(configuration);
        String result = blackoutRouter.route(scheduledProcessEvent);

        Assert.assertEquals(result, BlackoutRouter.OUTSIDE_BLACKOUT_PERIOD);
    }
}
