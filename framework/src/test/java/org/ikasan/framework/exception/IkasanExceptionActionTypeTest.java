/*
 * $Id: IkasanExceptionActionTypeTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/exception/IkasanExceptionActionTypeTest.java $
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
 * This test class supports the <code>IkasanExceptionActionType</code> concrete
 * implementation class.
 * 
 * @author Ikasan Development Team
 */
public class IkasanExceptionActionTypeTest
{
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(IkasanExceptionActionTypeTest.class);

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
    public void testIkasanExceptionActionTypeGetters()
    {
        // 
        // set up
        IkasanExceptionActionType type = IkasanExceptionActionType.ROLLBACK_RETRY;
        String currentText = "Operation will rollback and retry.";
        
        // 
        // create
        String actionTypeText = type.getDescription();
        
        // 
        // test
        Assert.assertTrue(actionTypeText.equals(currentText));
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
        return new JUnit4TestAdapter(IkasanExceptionActionTypeTest.class);
    }
}
