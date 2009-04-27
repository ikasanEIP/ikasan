/*
 * $Id: ExternalExceptionDefinitionTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/exception/user/ExternalExceptionDefinitionTest.java $
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
package org.ikasan.framework.exception.user;

import junit.framework.JUnit4TestAdapter;

// Imported log4j classes
import org.apache.log4j.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * External exception definition class tests.
 * 
 * @author Ikasan Development Team
 */
public class ExternalExceptionDefinitionTest
{
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(ExternalExceptionDefinitionTest.class);

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing to do here
    }

    /**
     * Test method for constructor
     */
    @Test
    public void testConstructor()
    {
        //
        // set-up
        Integer majorCode = new Integer(999);
        Integer minorCode = new Integer(111);
        ExternalUserAction eua = ExternalUserAction.ACCEPT;
        String sourceSystemRef = "sourceSystemRef";
        String returnSystemRef = "returnSystemRef";
        
        //
        // invoke
        ExternalExceptionDefinition eed = 
            new ExternalExceptionDefinition(majorCode,
                    minorCode, eua, sourceSystemRef, returnSystemRef);

        //
        // check
        Assert.assertTrue(majorCode.equals(eed.getMajorCode()));
        Assert.assertTrue(minorCode.equals(eed.getMinorCode()));
        Assert.assertTrue(eua.toString().equals(eed.getUserAction().toString()));
        Assert.assertTrue(sourceSystemRef.equals(eed.getSourceSystemRef()));
        Assert.assertTrue(returnSystemRef.equals(eed.getReturnSystemRef()));
    }
    
    /**
     * Test method setters and getters
     */
    @Test
    public void testSettersAndGetters()
    {
        //
        // set-up
        Integer majorCode1 = new Integer(999);
        Integer minorCode1 = new Integer(111);
        ExternalUserAction eua1 = ExternalUserAction.ACCEPT;
        String sourceSystemRef1 = "sourceSystemRef";
        String returnSystemRef1 = "returnSystemRef";
        
        //
        // invoke
        ExternalExceptionDefinition eed = 
            new ExternalExceptionDefinition(majorCode1,
                    minorCode1, eua1, sourceSystemRef1, returnSystemRef1);

        Integer majorCode2 = new Integer(888);
        Integer minorCode2 = new Integer(222);
        ExternalUserAction eua2 = ExternalUserAction.REJECT;
        String sourceSystemRef2 = "sourceSystemRef2";
        String returnSystemRef2 = "returnSystemRef2";

        //
        // change with setters
        eed.setMajorCode(majorCode2);
        eed.setMinorCode(minorCode2);
        eed.setUserAction(eua2);
        eed.setSourceSystemRef(sourceSystemRef2);
        eed.setReturnSystemRef(returnSystemRef2);
        
        //
        // check
        Assert.assertTrue(majorCode2.equals(eed.getMajorCode()));
        Assert.assertTrue(minorCode2.equals(eed.getMinorCode()));
        Assert.assertTrue(eua2.toString().equals(eed.getUserAction().toString()));
        Assert.assertTrue(sourceSystemRef2.equals(eed.getSourceSystemRef()));
        Assert.assertTrue(returnSystemRef2.equals(eed.getReturnSystemRef()));
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
        return new JUnit4TestAdapter(ExternalExceptionDefinitionTest.class);
    }
}
