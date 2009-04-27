/*
 * $Id: ScheduledDrivenQuartzContextTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/initiator/scheduled/quartz/ScheduledDrivenQuartzContextTest.java $
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

import org.ikasan.framework.exception.IkasanExceptionAction;
import org.ikasan.framework.exception.IkasanExceptionActionImpl;
import org.ikasan.framework.exception.IkasanExceptionActionType;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;

/**
 * This test class supports the <code>ScheduledDrivenQuartzContext</code> class.
 * 
 * @author Ikasan Development Team
 */
public class ScheduledDrivenQuartzContextTest
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
    
    /**
     * Mock objects
     */
    final IkasanExceptionAction action = mockery.mock(IkasanExceptionAction.class);
    final JobExecutionContext jec = mockery.mock(JobExecutionContext.class);
    final JobDetail jobDetail = mockery.mock(JobDetail.class);
    final JobDataMap jobDataMap = mockery.mock(JobDataMap.class);
    final Trigger trigger = mockery.mock(Trigger.class);
    final ScheduledDrivenQuartzContext sdContext = mockery.mock(ScheduledDrivenQuartzContext.class);

    /**
     * Real objects
     */
    static final String RETRY_ACTION = "retryAction";
    static final String RETRY_COUNT = "retryCount";
    private ScheduledDrivenQuartzContext realSdContext;
    
    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(jec).getJobDetail();
                will(returnValue(jobDetail));
            }
        });

        realSdContext = new ScheduledDrivenQuartzContext(jec);
    }

    /**
     * Test successful get jobDetail.
     */
    @Test
    public void test_successful_jobDetailGetter()
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(sdContext).getJobDetail();
                will(returnValue(jobDetail));
            }
        });

        sdContext.getJobDetail();
    }


    /**
     * Test successful getTrigger.
     */
    @Test
    public void test_successful_getTrigger()
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(jec).getTrigger();
                will(returnValue(trigger));
            }
        });

        realSdContext.getTrigger();
    }

    /**
     * Test successful ikasanExceptionAction setter to ensure
     * the retry action and retry count are correctly initialised.
     */
    @Test
    public void test_successful_ikasanExceptionActionSetter()
    {
        final IkasanExceptionAction realAction = 
            new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_RETRY);
        
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                // set the RETRY_ACTION in the data map
                exactly(1).of(jobDetail).getJobDataMap();
                will(returnValue(jobDataMap));
                exactly(1).of(jobDataMap).put(RETRY_ACTION, realAction);
                
                // set the retry count
                exactly(1).of(jobDetail).getJobDataMap();
                will(returnValue(jobDataMap));
                exactly(1).of(jobDataMap).put(RETRY_COUNT, 0);
            }
        });

        realSdContext.setIkasanExceptionAction(realAction);
    }

    /**
     * Test successful ikasanExceptionAction getter.
     */
    @Test
    public void test_successful_ikasanExceptionActionGetter()
    {
        final IkasanExceptionAction realAction = 
            new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_RETRY);
        
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                // get the RETRY_ACTION in the data map
                exactly(1).of(jobDetail).getJobDataMap();
                will(returnValue(jobDataMap));
                exactly(1).of(jobDataMap).get(RETRY_ACTION);
                will(returnValue(realAction));
            }
        });

        realSdContext.getIkasanExceptionAction();
    }

    /**
     * Test successful retryCount setter.
     */
    @Test
    public void test_successful_retryCountSetter()
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                // set the RETRY_COUNT in the data map
                exactly(1).of(jobDetail).getJobDataMap();
                will(returnValue(jobDataMap));
                exactly(1).of(jobDataMap).put(RETRY_COUNT, 100);
            }
        });

        // test setter and getter
        realSdContext.setRetryCount(100);
    }

    /**
     * Test successful retryCount getter.
     */
    @Test
    public void test_successful_retryCountGetter()
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                // get the RETRY_COUNT in the data map
                exactly(1).of(jobDetail).getJobDataMap();
                will(returnValue(jobDataMap));
                exactly(1).of(jobDataMap).get(RETRY_COUNT);
                will(returnValue(100));
            }
        });

        // test setter and getter
        realSdContext.getRetryCount();
    }

    /**
     * Test successful retryCount setter and getter.
     */
    @Test
    public void test_successful_clearRetry()
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                // get the RETRY_COUNT in the data map
                exactly(1).of(jobDetail).getJobDataMap();
                will(returnValue(jobDataMap));
                exactly(1).of(jobDataMap).remove(RETRY_ACTION);
                exactly(1).of(jobDataMap).remove(RETRY_COUNT);
            }
        });

        realSdContext.clearRetry();
    }
    
    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        mockery.assertIsSatisfied();
    }

    /**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(ScheduledDrivenQuartzContextTest.class);
    }

}
