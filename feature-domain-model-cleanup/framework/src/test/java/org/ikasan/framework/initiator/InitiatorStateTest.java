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
package org.ikasan.framework.initiator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.apache.log4j.Logger;
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
