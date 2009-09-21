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
