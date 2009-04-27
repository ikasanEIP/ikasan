/*
 * $Id: UserExceptionDefinitionTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/exception/user/UserExceptionDefinitionTest.java $
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
 * User exception definition class tests.
 * 
 * @author Ikasan Development Team
 */
public class UserExceptionDefinitionTest
{
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(UserExceptionDefinitionTest.class);

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
        String externalExceptionRef ="notSureAboutThisYet";
        
        //
        // invoke
        UserExceptionDefinition ued = new UserExceptionDefinition( majorCode,
                    minorCode, externalExceptionRef);

        //
        // check
        Assert.assertTrue(majorCode.equals(ued.getMajorCode()));
        Assert.assertTrue(minorCode.equals(ued.getMinorCode()));
        Assert.assertTrue(externalExceptionRef.equals(ued.getExternalExceptionRef()));
    }

    /**
     * Test method for setters and getters
     */
    @Test
    public void testSettersAndGetters()
    {
        //
        // set-up
        Integer majorCode = new Integer(999);
        Integer minorCode = new Integer(111);
        Boolean dropDuplicate = new Boolean(true);
        String dropDuplicateMask = new String("");
        Long dropDuplicatePeriod = new Long(4000);
        Boolean publishable = new Boolean(true);
        String externalExceptionRef ="notSureAboutThisYet";
        
        //
        // invoke
        UserExceptionDefinition ued = new UserExceptionDefinition(majorCode,
                    minorCode, externalExceptionRef);

        ued.setDropDuplicate(dropDuplicate);
        ued.setDropDuplicatePeriod(dropDuplicatePeriod);
        ued.setDuplicateMaskExpression(dropDuplicateMask);
        ued.setPublishable(publishable);

        Integer majorCode2 = new Integer(888);
        ued.setMajorCode(majorCode2);

        Integer minorCode2 = new Integer(222);
        ued.setMinorCode(minorCode2);

        String externalExceptionRef2 = "Still not sure";
        ued.setExternalExceptionRef(externalExceptionRef2);
        
        //
        // check
        Assert.assertTrue(majorCode2.equals(ued.getMajorCode()));
        Assert.assertTrue(minorCode2.equals(ued.getMinorCode()));
        Assert.assertTrue(dropDuplicate.equals(ued.getDropDuplicate()));
        Assert.assertTrue(dropDuplicatePeriod.equals(ued.getDropDuplicatePeriod()));
        Assert.assertTrue(dropDuplicateMask.equals(ued.getDuplicateMaskExpression()));
        Assert.assertTrue(externalExceptionRef2.equals(ued.getExternalExceptionRef()));
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
        return new JUnit4TestAdapter(UserExceptionDefinitionTest.class);
    }
}
