/* 
 * $Id: HousekeeperServiceHttpImplTest.java
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
package org.ikasan.platform.service;

import org.junit.Test;

/**
 * A Test of HTTP Implementation of the Housekeeper interface
 * 
 * @author Ikasan Development Team
 */
public class HouseKeeperServiceHttpImplTest
{
    /**
     * Test that a NULL URL passed in to the constructor causes an Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testHouseKeeperServiceHttpImplWithNullUrl()
    {
        String url = null;
        String userName = "username";
        String password = "password";
        HousekeeperServiceHttpImpl hskpHttpImpl = new HousekeeperServiceHttpImpl(url, userName, password);
        hskpHttpImpl.housekeepWiretapEvents();
    }

    /**
     * Test that a NULL Username passed in to the constructor causes an Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testHouseKeeperServiceHttpImplWithNulluserName()
    {
        String url = "http://localhost:8080";
        String userName = null;
        String password = "password";
        HousekeeperServiceHttpImpl hskpHttpImpl = new HousekeeperServiceHttpImpl(url, userName, password);
        hskpHttpImpl.housekeepWiretapEvents();
    }

    /**
     * Test that a NULL password passed in to the constructor causes an Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testHouseKeeperServiceHttpImplWithNullPassword()
    {
        String url = "http://localhost:8080";
        String userName = "username";
        String password = null;
        HousekeeperServiceHttpImpl hskpHttpImpl = new HousekeeperServiceHttpImpl(url, userName, password);
        hskpHttpImpl.housekeepWiretapEvents();
    }

    /**
     * Ensure once the right constructor arguments are set, no other uncaught exceptions thrown
     */
    @Test
    public void testHouseKeeperServiceHttpImplWithNonNullValues()
    {
        String url = "http://localhost:8080/demoFileDelivery/events/housekeeping.htm";
        String userName = "housekeeper";
        String password = "housekeeper";
        new HousekeeperServiceHttpImpl(url, userName, password);
    }
}
