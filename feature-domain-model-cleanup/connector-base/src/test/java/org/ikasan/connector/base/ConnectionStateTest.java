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
package org.ikasan.connector.base;

import junit.framework.JUnit4TestAdapter;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Connector State Test Class
 * 
 * @author Ikasan Development Team
 *
 */
public class ConnectionStateTest
{
    /** Logger */
    private static Logger logger = Logger.getLogger(ConnectionStateTest.class);
    
    /**
     * Setup before each test
     */
    @Before public void setUp()
    {
        // nothing to setup
        logger.info("setUp");
    }
    
    /**
     * Test the isDisconnected method
     */
    @Test public void testIsDisconnected()
    {
        ConnectionState state = ConnectionState.DISCONNECTED;
        Assert.assertEquals(state.isDisconnected(), true);
        Assert.assertEquals(state.isConnectionOpen(), false);
        Assert.assertEquals(state.isConnectionClosed(), true);
        Assert.assertEquals(state.isSessionOpen(), false);
        Assert.assertEquals(state.isSessionClosed(), true);
    }

    /**
     * Test the isConnected method
     */
    @Test public void testIsConnected()
    {
        ConnectionState state = ConnectionState.CONNECTED;
        Assert.assertEquals(state.isDisconnected(), false);
        Assert.assertEquals(state.isConnectionOpen(), true);
        Assert.assertEquals(state.isConnectionClosed(), false);
        Assert.assertEquals(state.isSessionOpen(), false);
        Assert.assertEquals(state.isSessionClosed(), true);
    }

    /**
     * Test the isSessionOpen method
     */
    @Test public void testIsSessionOpen()
    {
        ConnectionState state = ConnectionState.SESSION_OPEN;
        Assert.assertEquals(state.isDisconnected(), false);
        Assert.assertEquals(state.isConnectionOpen(), true);
        Assert.assertEquals(state.isConnectionClosed(), false);
        Assert.assertEquals(state.isSessionOpen(), true);
        Assert.assertEquals(state.isSessionClosed(), false);
    }

    /**
     * Tear down after each test
     */
    @After public void tearDown()
    {
        // nothing to tear down
        logger.info("tearDown");
    }

    /**
     * Return the suite of tests
     * 
     * @return suite of tests
     */
    public static junit.framework.Test suite() 
    {
        return new JUnit4TestAdapter(ConnectionStateTest.class);
    }    

}
