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
package org.ikasan.framework.initiator;

import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;

// Imported log4j classes
import org.apache.log4j.Logger;

import org.ikasan.framework.initiator.InitiatorState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>InitiatorStateImpl</code> class
 * and associated InitiatorState interface.
 * 
 * @author Ikasan Development Team
 */
public class InitiatorStateTest
{
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(InitiatorStateTest.class);

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing to do here
    }

    /**
     * Test initiator state stopped
     */
    @Test
    public void test_initiatorState_stopped()
    {
        assertTrue(InitiatorState.STOPPED.getName().equals("stopped"));
        assertTrue(InitiatorState.STOPPED.isStopped());
        assertFalse(InitiatorState.STOPPED.isRunning());
        assertFalse(InitiatorState.STOPPED.isRecovering());
        assertFalse(InitiatorState.STOPPED.isError());
    }

    /**
     * Test initiator state running
     */
    @Test
    public void test_initiatorState_running()
    {
        assertTrue(InitiatorState.RUNNING.getName().equals("running"));
        assertFalse(InitiatorState.RUNNING.isStopped());
        assertTrue(InitiatorState.RUNNING.isRunning());
        assertFalse(InitiatorState.RUNNING.isRecovering());
        assertFalse(InitiatorState.RUNNING.isError());
    }

    /**
     * Test initiator state recovering
     */
    @Test
    public void test_initiatorState_recovering()
    {
        assertTrue(InitiatorState.RECOVERING.getName().equals("runningInRecovery"));
        assertFalse(InitiatorState.RECOVERING.isStopped());
        assertTrue(InitiatorState.RECOVERING.isRunning());
        assertTrue(InitiatorState.RECOVERING.isRecovering());
        assertFalse(InitiatorState.RECOVERING.isError());
    }

    /**
     * Test initiator state error
     */
    @Test
    public void test_initiatorState_error()
    {
        assertTrue(InitiatorState.ERROR.getName().equals("stoppedInError"));
        assertTrue(InitiatorState.ERROR.isStopped());
        assertFalse(InitiatorState.ERROR.isRunning());
        assertFalse(InitiatorState.ERROR.isRecovering());
        assertTrue(InitiatorState.ERROR.isError());
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
        return new JUnit4TestAdapter(InitiatorStateTest.class);
    }
}
