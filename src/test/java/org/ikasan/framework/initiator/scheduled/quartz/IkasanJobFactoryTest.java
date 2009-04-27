/*
 * $Id: IkasanJobFactoryTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/initiator/scheduled/quartz/IkasanJobFactoryTest.java $
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

// Imported log4j classes
import org.apache.log4j.Logger;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.spi.TriggerFiredBundle;

/**
 * This test class supports the <code>IkasanJobFactory</code> class.
 * 
 * @author Ikasan Development Team
 */
public class IkasanJobFactoryTest
{
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(IkasanJobFactoryTest.class);

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
    final QuartzStatefulScheduledDrivenInitiator si = mockery.mock(QuartzStatefulScheduledDrivenInitiator.class);
    final TriggerFiredBundle bundle = mockery.mock(TriggerFiredBundle.class);
    final JobDetail jobDetail = mockery.mock(JobDetail.class);
    final QuartzStatefulJob job = mockery.mock(QuartzStatefulJob.class);

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing to do here
    }

    /**
     * Test successful execution of the IkasanJobFactory
     * @throws SchedulerException 
     */
    @Test
    public void test_successfulNewJob()
        throws SchedulerException
    {
        // 
        // set expectations
        mockery.checking(new Expectations()
        {
            {
                // get job specifics 
                exactly(1).of(bundle).getJobDetail();
                will(returnValue(jobDetail));
                exactly(1).of(jobDetail).getJobClass();
                will(returnValue(job.getClass()));
                
                // logger.debug
                exactly(1).of(jobDetail).getFullName();
                will(returnValue("anyString"));
            }
        });

        //
        // run test
        IkasanJobFactory ijf = new IkasanJobFactory(si);
        ijf.newJob(bundle);
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
        return new JUnit4TestAdapter(IkasanJobFactoryTest.class);
    }

}
