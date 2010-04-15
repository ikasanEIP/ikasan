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
package org.ikasan.framework.initiator.scheduled.quartz;

import junit.framework.JUnit4TestAdapter;

import org.apache.log4j.Logger;
import org.ikasan.framework.initiator.AbortTransactionException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

/**
 * This test class supports the <code>QuartzStatefulJob</code> class.
 * 
 * @author Ikasan Development Team
 */
public class QuartzStatefulJobTest
{
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(QuartzStatefulJobTest.class);

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /**
     * Mock objects
     */
    final QuartzStatefulScheduledDrivenInitiator initiator = mockery.mock(QuartzStatefulScheduledDrivenInitiator.class);
    final JobExecutionContext jec = mockery.mock(JobExecutionContext.class);
    final JobDetail jobDetail = mockery.mock(JobDetail.class);
    final JobDataMap jobDataMap = mockery.mock(JobDataMap.class);

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing to do here
    }

    /**
     * Test successful execution of the QuartzStatefulJob
     */
    @Test
    public void test_successfulExecute()
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
            	//initial invocation
            	one(jec).getMergedJobDataMap();will(returnValue(jobDataMap));
                exactly(1).of(initiator).invoke(jobDataMap);
                
                //no more invocations
                one(jobDataMap).get(QuartzStatefulScheduledDrivenInitiator.REINVOKE_IMMEDIATELY_FLAG);will(returnValue(Boolean.FALSE));

            }
        });

        //
        // run test
        QuartzStatefulJob job = new QuartzStatefulJob(initiator);
        job.execute(jec);
        mockery.assertIsSatisfied();
    }
    
    /**
     * Test repeated execution of the QuartzStatefulJob, when flag set in jobDataMap
     */
    @Test
    public void test_successfulRepeatedExecute()
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
            	//initial invocation
            	one(jec).getMergedJobDataMap();will(returnValue(jobDataMap));
                exactly(1).of(initiator).invoke(jobDataMap);
                
                //immediate reinvoke
                one(jobDataMap).get(QuartzStatefulScheduledDrivenInitiator.REINVOKE_IMMEDIATELY_FLAG);will(returnValue(Boolean.TRUE));
                exactly(1).of(initiator).invoke(jobDataMap);
                
                //immediate reinvoke
                one(jobDataMap).get(QuartzStatefulScheduledDrivenInitiator.REINVOKE_IMMEDIATELY_FLAG);will(returnValue(Boolean.TRUE));
                exactly(1).of(initiator).invoke(jobDataMap);
                
                //no more invocations
                one(jobDataMap).get(QuartzStatefulScheduledDrivenInitiator.REINVOKE_IMMEDIATELY_FLAG);will(returnValue(Boolean.FALSE));
            }
        });

        //
        // run test
        QuartzStatefulJob job = new QuartzStatefulJob(initiator);
        job.execute(jec);
        mockery.assertIsSatisfied();
    }
    
    

    /**
     * Test successful throwing of an AbortTransactionException
     * on execution of the QuartzStatefulJob
     */
    @Test
    public void test_successfulAbortTransactionExceptionOnExecute()
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
            	one(jec).getMergedJobDataMap();will(returnValue(jobDataMap));
                exactly(1).of(initiator).invoke(jobDataMap);
                will(throwException(new AbortTransactionException()));
            }
        });

        //
        // run test
        QuartzStatefulJob job = new QuartzStatefulJob(initiator);
        job.execute(jec);
        mockery.assertIsSatisfied();
    }

    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        // nothing to tear down
        logger.info("tearDown");
    }

    /**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(QuartzStatefulJobTest.class);
    }

}
