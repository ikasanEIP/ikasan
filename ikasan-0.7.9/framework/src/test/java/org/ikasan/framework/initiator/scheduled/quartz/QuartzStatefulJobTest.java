/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
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
