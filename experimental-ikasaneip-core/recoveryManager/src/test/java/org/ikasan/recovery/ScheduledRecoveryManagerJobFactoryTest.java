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
package org.ikasan.recovery;

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
 * This test class supports the <code>ScheduledRecoveryManagerJobFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledRecoveryManagerJobFactoryTest
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
    final Map<String,Job> recoveries = mockery.mock(Map.class, "mockRecoveries");

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
                exactly(1).of(recoveries).put("recoveryJob_flowNamerecoveryManager_moduleName", job);
            }
        });

        ScheduledRecoveryManagerJobFactory scheduledRecoveryManagerJobFactory = 
            new StubbedScheduledRecoveryManagerJobFactory();
        scheduledRecoveryManagerJobFactory.addJob("flowName", "moduleName", job);
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
                will(returnValue("recoveryJob_flowName"));
                exactly(1).of(jobDetail).getGroup();
                will(returnValue("recoveryManager_moduleName"));
                exactly(1).of(recoveries).get("recoveryJob_flowNamerecoveryManager_moduleName");
                will(returnValue(job));
            }
        });

        ScheduledRecoveryManagerJobFactory scheduledRecoveryManagerJobFactory = 
            new StubbedScheduledRecoveryManagerJobFactory();
        scheduledRecoveryManagerJobFactory.newJob(triggerFiredBundle);
        mockery.assertIsSatisfied();
    }

    /**
     * Extended ScheduledRecoveryManagerJobFactory for testing with replacement mocks.
     * @author Ikasan Development Team
     *
     */
    private class StubbedScheduledRecoveryManagerJobFactory extends ScheduledRecoveryManagerJobFactory
    {
        protected StubbedScheduledRecoveryManagerJobFactory()
        {
            super();
        }
        
        @Override
        protected Map<String,Job> getRecoveriesCache()
        {
            return recoveries;
        }
    }

}
