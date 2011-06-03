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
package org.ikasan.scheduler;

import java.util.Map;

import org.ikasan.scheduler.ScheduledJobFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.spi.TriggerFiredBundle;

/**
 * This test class supports the <code>ScheduledJobFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledJobFactoryTest
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

    /** Mock scheduled jobs */
    final Map<String,Job> mockScheduledJobs = mockery.mock(Map.class, "mockScheduledJobs");

    /** Mock job */
    final Job job = mockery.mock(Job.class, "mockJob");

    /** Mock triggerFiredBundle */
    final TriggerFiredBundle triggerFiredBundle = mockery.mock(TriggerFiredBundle.class, "mockTriggerFiredBundle");

    /** Mock jobDetail */
    final JobDetail jobDetail = mockery.mock(JobDetail.class, "mockJobDetail");

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
                will(returnValue("moduleName"));
                exactly(1).of(mockScheduledJobs).get("recoveryJob_flowNamemoduleName");
                will(returnValue(job));
            }
        });

        ScheduledJobFactory scheduledJobFactory = new StubbedScheduledJobFactory();
        scheduledJobFactory.newJob(triggerFiredBundle);
        mockery.assertIsSatisfied();
    }
    
    /**
     * Extended ScheduledJobFactory for testing.
     * @author mitcje
     *
     */
    private class StubbedScheduledJobFactory extends ScheduledJobFactory
    {
        protected StubbedScheduledJobFactory()
        {
            super();
            this.scheduledJobs = mockScheduledJobs;
        }
    }
}
