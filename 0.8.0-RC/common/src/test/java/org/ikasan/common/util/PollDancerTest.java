/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.common.util;

import junit.framework.JUnit4TestAdapter;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Poll Dancer Test Class
 * 
 * @author Ikasan Development Team
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
