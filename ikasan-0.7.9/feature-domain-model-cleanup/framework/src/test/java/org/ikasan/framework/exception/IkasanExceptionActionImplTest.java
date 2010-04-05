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
package org.ikasan.framework.exception;

import junit.framework.JUnit4TestAdapter;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>IkasanExceptionActionImplTest</code> concrete
 * implementation class.
 * 
 * @author Ikasan Development Team
 */
public class IkasanExceptionActionImplTest
{
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(IkasanExceptionActionImplTest.class);

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing to do here
    }

    /**
     * Test param based Constructor
     */
    @Test
    public void testIkasanExceptionActionImplConstructorWithAllArgs()
    {
        // 
        // set up
        IkasanExceptionActionType type = IkasanExceptionActionType.ROLLBACK_RETRY;
        Long delay = new Long(1000);
        Integer maxAttempts = new Integer(12);
        
        // 
        // create
        IkasanExceptionAction iea = new IkasanExceptionActionImpl(type,delay, maxAttempts);
        
        // 
        // test
        Assert.assertTrue(iea.getType().equals(type));
        Assert.assertTrue(iea.getDelay().equals(delay));
        Assert.assertTrue(iea.getMaxAttempts().equals(maxAttempts));
    }

    /**
     * Test minimal param based Constructor
     */
    @Test
    public void testIkasanExceptionActionImplConstructorWithMinimalArgs()
    {
        // 
        // set up
        IkasanExceptionActionType type = IkasanExceptionActionType.ROLLBACK_RETRY;
        
        // 
        // create
        IkasanExceptionAction iea = new IkasanExceptionActionImpl(type);
        
        // 
        // test
        Assert.assertTrue(iea.getType().equals(type));
        Assert.assertTrue(iea.getDelay().equals(new Long(IkasanExceptionAction.DEFAULT_DELAY)));
        Assert.assertTrue(iea.getMaxAttempts().equals(new Integer(IkasanExceptionAction.DEFAULT_MAX_ATTEMPTS)));
    }

    /**
     * Test minimal param based Constructor
     */
    @Test
    public void testIkasanExceptionActionImplSetters()
    {
        // 
        // set up
        IkasanExceptionActionType type = IkasanExceptionActionType.ROLLBACK_RETRY;
        IkasanExceptionActionType newType = IkasanExceptionActionType.ROLLBACK_STOP;
        Long delay = new Long(2000);
        Integer maxAttempts = new Integer(5);
        
        // 
        // create
        IkasanExceptionActionImpl ieaImpl = new IkasanExceptionActionImpl(type);
        ieaImpl.setType(newType);
        ieaImpl.setDelay(delay);
        ieaImpl.setMaxAttempts(maxAttempts);
        
        // 
        // test
        Assert.assertTrue(ieaImpl.getType().equals(newType));
        Assert.assertTrue(ieaImpl.getDelay().equals(delay));
        Assert.assertTrue(ieaImpl.getMaxAttempts().equals(maxAttempts));
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
        return new JUnit4TestAdapter(IkasanExceptionActionImplTest.class);
    }
}
