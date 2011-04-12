/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 *
 * Copyright (c) 2000-20010 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.consumer.quartz;

import junit.framework.Assert;

import org.junit.Test;
import org.quartz.SchedulerException;

/**
 * This test class supports the <code>ScheduledConsumer</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledConsumerConfigurationTest
{
    /**
     * Test configuration mutators.
     */
    @Test
    public void test_mutators() throws SchedulerException
    {
        ScheduledConsumerConfiguration consumerConfiguration = new ScheduledConsumerConfiguration();
        Assert.assertNull("initial jobName should be null", consumerConfiguration.getJobName());
        Assert.assertNull("initial jobGroup should be null", consumerConfiguration.getJobGroup());
        Assert.assertNull("initial cronExpression should be null", consumerConfiguration.getCronExpression());

        consumerConfiguration.setJobName("jobName");
        consumerConfiguration.setJobGroup("jobGroup");
        consumerConfiguration.setCronExpression("cronExpression");
        
        Assert.assertEquals("jobName should be populated with 'jobName'", "jobName", consumerConfiguration.getJobName());
        Assert.assertEquals("jobGroup should be populated with 'jobGroup'", "jobGroup", consumerConfiguration.getJobGroup());
        Assert.assertEquals("cronExpression should be populated with 'cronExpression'", "cronExpression", consumerConfiguration.getCronExpression());
    }

}
