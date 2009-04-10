/*
 * $Id: PollDancerTest.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/test/java/org/ikasan/common/util/PollDancerTest.java $
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
package org.ikasan.common.util;

import org.apache.log4j.Logger;
import org.junit.*;

import junit.framework.JUnit4TestAdapter;

/**
 * Poll Dancer Test Class
 * 
 * @author Jeff Mitchell
 *
 */
public class PollDancerTest
{
    /** Logger */
    private static Logger logger = Logger.getLogger(PollDancerTest.class);
    
    /** object to be tested */
    PollDancer pollDancer;

    /**
     * Test setup
     */
    @Before public void setUp()
    {
        /** our configured (actual) pollTime */
        long initialPollTime = 5000;
        /** adjustment percentage */
        long percentageAdjustment = 60;
        /** minimum value */
        long min = 10;
        /** maximum value */
        long max = 10000;

        this.pollDancer = new PollDancer(initialPollTime, percentageAdjustment, min, max);
    }
    
    /**
     * Test increasePollTimeAgl1
     */
    @Test public void testIncreasePollTimeAlg1()
    {
        /** test for valid increase in pollTime */
        long newPollTime = this.pollDancer.increasePollTimeAlg1();
        Assert.assertEquals(new Long(newPollTime), new Long(8000));

        newPollTime = this.pollDancer.increasePollTimeAlg1();
        Assert.assertEquals(new Long(newPollTime), new Long(10000));
    }

    /**
     * Test reducePollTimeAgl1
     */
    @Test public void testDecreasePollTimeAlg1()
    {
        /** test for valid decrease in pollTime */
        long newPollTime = this.pollDancer.decreasePollTimeAlg1();
        Assert.assertEquals(new Long(newPollTime), new Long(2000));

        /** test for decrease on odd number pollTime */
        newPollTime = this.pollDancer.decreasePollTimeAlg1();
        Assert.assertEquals(new Long(newPollTime), new Long(800));
    }

    /**
     * teardown
     */
    @After public void tearDown()
    {
        // nothing to tear down
        logger.info("tearDown"); //$NON-NLS-1$
    }

    /**
     * main test initiator
     * @return nothing
     */
    public static junit.framework.Test suite() 
    {
        return new JUnit4TestAdapter(PollDancerTest.class);
    }    
}
