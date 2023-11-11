/*
 * $Id:$
 * $URL:$
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Connector State Test Class
 * 
 * @author Ikasan Development Team
 *
 */
class ConnectionStateTest
{
    /** Logger */
    private static Logger logger = LoggerFactory.getLogger(ConnectionStateTest.class);

    /**
     * Setup before each test
     */
    @BeforeEach
    void setUp()
    {
        // nothing to setup
        logger.info("setUp");
    }

    /**
     * Test the isDisconnected method
     */
    @Test
    void testIsDisconnected()
    {
        ConnectionState state = ConnectionState.DISCONNECTED;
        assertTrue(state.isDisconnected());
        assertFalse(state.isConnectionOpen());
        assertTrue(state.isConnectionClosed());
        assertFalse(state.isSessionOpen());
        assertTrue(state.isSessionClosed());
    }

    /**
     * Test the isConnected method
     */
    @Test
    void testIsConnected()
    {
        ConnectionState state = ConnectionState.CONNECTED;
        assertFalse(state.isDisconnected());
        assertTrue(state.isConnectionOpen());
        assertFalse(state.isConnectionClosed());
        assertFalse(state.isSessionOpen());
        assertTrue(state.isSessionClosed());
    }

    /**
     * Test the isSessionOpen method
     */
    @Test
    void testIsSessionOpen()
    {
        ConnectionState state = ConnectionState.SESSION_OPEN;
        assertFalse(state.isDisconnected());
        assertTrue(state.isConnectionOpen());
        assertFalse(state.isConnectionClosed());
        assertTrue(state.isSessionOpen());
        assertFalse(state.isSessionClosed());
    }

    /**
     * Tear down after each test
     */
    @AfterEach
    void tearDown()
    {
        // nothing to tear down
        logger.info("tearDown");
    }

}
