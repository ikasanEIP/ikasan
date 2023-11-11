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

import org.ikasan.spec.configuration.InvalidConfigurationException;
import org.ikasan.spec.configuration.IsValidationAware;
import org.junit.jupiter.api.Test;
import org.quartz.SchedulerException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class supports the <code>ScheduledConsumer</code> class.
 * 
 * @author Ikasan Development Team
 */
class ScheduledConsumerConfigurationTest
{
    /**
     * Test configuration mutators.
     */
    @Test
    void test_mutators() throws SchedulerException
    {
        ScheduledConsumerConfiguration consumerConfiguration = new ScheduledConsumerConfiguration();
        assertNotNull(consumerConfiguration.getTimezone(), "Timezone Id cannopt be null");
        assertNull(consumerConfiguration.getCronExpression(), "initial cronExpression should be null");
        consumerConfiguration.setCronExpression("cronExpression");
        assertEquals("cronExpression", consumerConfiguration.getCronExpression(), "cronExpression should be populated with 'cronExpression'");
    }

    /**
     * Test configuration mutators.
     */
    @Test
    void test_maxEagerCallbacks() throws SchedulerException
    {
        ScheduledConsumerConfiguration consumerConfiguration = new ScheduledConsumerConfiguration();
        assertNotNull(consumerConfiguration.getTimezone(), "Timezone Id cannopt be null");
        assertEquals(0, consumerConfiguration.getMaxEagerCallbacks(), "initial maxEagerCallbacks should be 0");
        consumerConfiguration.setMaxEagerCallbacks(2);
        assertEquals(2, consumerConfiguration.getMaxEagerCallbacks(), "maxEagerCallbacks should be populated with '2'");
    }

    /**
     * Test non configuration mutators.
     */
    @Test
    void test_getConsolidatedCronExpressions()
    {
        ScheduledConsumerConfiguration consumerConfiguration = new ScheduledConsumerConfiguration();
        assertNull(consumerConfiguration.getCronExpression(), "cron expression should be null");
        assertEquals(0, consumerConfiguration.getCronExpressions().size(), "cron expressions should be 0");
        assertEquals(0, consumerConfiguration.getConsolidatedCronExpressions().size(), "consolidated cron expressions should be 0");
        consumerConfiguration.setCronExpression("0/5 * * * * ?");
        assertEquals(1, consumerConfiguration.getConsolidatedCronExpressions().size(), "consolidated cron expressions should be 1");

        List<String> cronExpressions = new ArrayList<String>();
        cronExpressions.add("0/1 * * * * ?");
        cronExpressions.add("0/2 * * * * ?");
        cronExpressions.add("0/3 * * * * ?");
        cronExpressions.add("0/5 * * * * ?");   // added even though repeat of cronExpression
        consumerConfiguration.setCronExpressions(cronExpressions);
        assertEquals(5, consumerConfiguration.getConsolidatedCronExpressions().size(), "consolidated cron expressions should be 5");
    }

    /**
     * Test to ensure the configuration is validation aware.
     *
     **/
    @Test
    void test_configuration_isValidationAware() throws InvalidConfigurationException
    {
        assertTrue(new ScheduledConsumerConfiguration() instanceof IsValidationAware, "Configuration doesnt implement IsValidationAware");
    }

    /**
     * Test.
     */
    @Test
    void test_invalid_configuration_invalid_cronExpression()
    {
        ScheduledConsumerConfiguration scheduledConsumerConfiguration = new ScheduledConsumerConfiguration();

        try
        {
            scheduledConsumerConfiguration.validate();
            fail("configuration is not valid");
        }
        catch(InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Test.
     */
    @Test
    void test_valid_configuration()
    {
        ScheduledConsumerConfiguration scheduledConsumerConfiguration = new ScheduledConsumerConfiguration();
        scheduledConsumerConfiguration.setCronExpression("0/5 * * * * ?");
        scheduledConsumerConfiguration.validate();
    }
}
