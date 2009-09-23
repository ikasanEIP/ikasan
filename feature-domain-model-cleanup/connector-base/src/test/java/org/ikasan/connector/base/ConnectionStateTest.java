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
