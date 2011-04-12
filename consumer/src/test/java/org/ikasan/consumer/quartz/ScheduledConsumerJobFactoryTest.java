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

import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.spi.TriggerFiredBundle;

/**
 * This test class supports the <code>ScheduledConsumerJobFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledConsumerJobFactoryTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mock scheduledRecoveryManager */
    final Map<String,Job> consumers = mockery.mock(Map.class, "mockConsumers");

    /** Mock job */
    final Job job = mockery.mock(Job.class, "mockJob");

    /** Mock triggerFiredBundle */
    final TriggerFiredBundle triggerFiredBundle = mockery.mock(TriggerFiredBundle.class, "mockTriggerFiredBundle");

    /** Mock jobDetail */
    final JobDetail jobDetail = mockery.mock(JobDetail.class, "mockJobDetail");

    /**
     * Test successful adding of a job.
     */
    @Test
    public void test_successful_addJob()
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // set the job factory
                exactly(1).of(consumers).put("flowNamemoduleName", job);
            }
        });

        ScheduledConsumerJobFactory consumerJobFactory = new StubbedScheduledConsumerJobFactory();
        consumerJobFactory.addConsumer("flowName", "moduleName", job);
        mockery.assertIsSatisfied();
    }

    /**
     * Test successful new of a job.
     * @throws SchedulerException 
     */
    @Test
    public void test_successful_newJob() throws SchedulerException
    {
        // expectations
        mockery.checking(new Expectations()
        {
            {
                // get the job detail
                exactly(1).of(triggerFiredBundle).getJobDetail();
                will(returnValue(jobDetail));

                // get the job detail name and group for cache lookup
                exactly(1).of(jobDetail).getName();
                will(returnValue("flowName"));
                exactly(1).of(jobDetail).getGroup();
                will(returnValue("moduleName"));
                exactly(1).of(consumers).get("flowNamemoduleName");
                will(returnValue(job));
            }
        });

        ScheduledConsumerJobFactory consumerJobFactory = new StubbedScheduledConsumerJobFactory();
        consumerJobFactory.newJob(triggerFiredBundle);
        mockery.assertIsSatisfied();
    }

    /**
     * Extended ScheduledRecoveryManagerJobFactory for testing with replacement mocks.
     * @author Ikasan Development Team
     *
     */
    private class StubbedScheduledConsumerJobFactory extends ScheduledConsumerJobFactory
    {
        protected StubbedScheduledConsumerJobFactory()
        {
            super();
        }
        
        @Override
        protected Map<String,Job> getConsumerCache()
        {
            return consumers;
        }
    }

}
