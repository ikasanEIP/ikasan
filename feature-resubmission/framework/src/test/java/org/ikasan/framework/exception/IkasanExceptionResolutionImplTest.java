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
 * This test class supports the <code>IkasanExceptionResolutionImplTest</code> concrete
 * implementation class.
 * 
 * @author Ikasan Development Team
 */
public class IkasanExceptionResolutionImplTest
{
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(IkasanExceptionResolutionImplTest.class);

    /** Ikasan Exception Action Type */
    IkasanExceptionActionType type;

    /** Ikasan Exception Action */
    IkasanExceptionAction action;
    
    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // 
        // create
        type = IkasanExceptionActionType.ROLLBACK_RETRY;
        action = new IkasanExceptionActionImpl(type);
    }

    /**
     * Test param based Constructor
     */
    @Test
    public void testIkasanExceptionResolutionImplConstructor()
    {
        // 
        // set up
        String testExceptionId = "testExceptionId";
        
        // 
        // create
        IkasanExceptionResolution resolution = 
            new IkasanExceptionResolutionImpl(testExceptionId, action);
        
        // 
        // test
        Assert.assertTrue(resolution.getId().equals(testExceptionId));
        Assert.assertTrue(resolution.getAction().equals(action));
    }

    /**
     * Test param based Constructor
     */
    @Test
    public void testGetEmergencyResolution()
    {
        // 
        // create
        IkasanExceptionResolution resolution = 
            IkasanExceptionResolutionImpl.getEmergencyResolution();
        
        // 
        // test - comparison values from IkasanExceptionResolutionImpl
        Assert.assertTrue(resolution.getId().equals("emergencyResolution"));
        Assert.assertTrue(resolution.getAction().getType().equals(IkasanExceptionActionType.ROLLBACK_STOP));
    }

    /**
     * Test setters
     */
    @Test
    public void testIkasanExceptionResolutionSetters()
    {
        // 
        // set up
        String exceptionId = "exceptionId";
        String changedExceptionId = "changedExceptionId";
        IkasanExceptionAction changedAction = 
            new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_RETRY);        
        // 
        // create
        IkasanExceptionResolutionImpl resolution = 
            new IkasanExceptionResolutionImpl(exceptionId, action);
        resolution.setId(changedExceptionId);
        resolution.setAction(changedAction);
        
        // 
        // test
        Assert.assertTrue(resolution.getId().equals(changedExceptionId));
        Assert.assertTrue(resolution.getAction().equals(changedAction));
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
        return new JUnit4TestAdapter(IkasanExceptionResolutionImplTest.class);
    }
}
