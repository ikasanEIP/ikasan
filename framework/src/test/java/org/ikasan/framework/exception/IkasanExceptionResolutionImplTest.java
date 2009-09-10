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
package org.ikasan.framework.exception;

import junit.framework.JUnit4TestAdapter;

// Imported log4j classes
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
            new IkasanExceptionActionImpl(IkasanExceptionActionType.CONTINUE);        
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
