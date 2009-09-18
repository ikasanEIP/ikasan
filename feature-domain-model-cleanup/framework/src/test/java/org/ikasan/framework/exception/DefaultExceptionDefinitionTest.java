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
import org.ikasan.common.CommonExceptionType;
import org.ikasan.common.ExceptionType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>IkasanExceptionDefinition</code> concrete
 * implementation class.
 * 
 * @author Ikasan Development Team
 */
public class DefaultExceptionDefinitionTest
{
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(DefaultExceptionDefinitionTest.class);

    //
    // create action instances
    /** Action Type */
    IkasanExceptionActionType actionType = IkasanExceptionActionType.ROLLBACK_RETRY;
    /** Action */
    IkasanExceptionAction action = new IkasanExceptionActionImpl(actionType);

    //
    // create resolution instances
    /** Resolution Id */
    String resolutionId = "resolutionId";
    /** Resolution */
    IkasanExceptionResolution resolution = new IkasanExceptionResolutionImpl(resolutionId, action);

    //
    // create other bits required for DefaultExceptionDefinition tests
    /** exception className */
    String className = "com.classname.test";
    /** exception type */
    ExceptionType type = CommonExceptionType.INVALID_PAYLOAD_SPEC;
    
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
    public void testDefaultExceptionDefinitionConstructorWithArgs()
    {
        DefaultExceptionDefinition ded = new DefaultExceptionDefinition(resolution, className, type);
        
        Assert.assertTrue(ded.getResolution().getId().equals(resolutionId));
        Assert.assertTrue(ded.getResolution().getAction().equals(action));
        Assert.assertTrue(ded.getClassName().equals(className));
        Assert.assertTrue(ded.getType().equals(type));
    }

    /**
     * Test default based Constructor with setters
     */
    @Test
    public void testDefaultExceptionDefinitionConstructorWithSetters()
    {
        DefaultExceptionDefinition benchmark = 
            new DefaultExceptionDefinition(resolution, className, type);

        DefaultExceptionDefinition ded = new DefaultExceptionDefinition(resolution, className);
        ded.setClassName(className);
        ded.setResolution(resolution);
        ded.setType(type);
        
        Assert.assertTrue(ded.getClassName().equals(benchmark.getClassName()));
        Assert.assertTrue(ded.getResolution().equals(benchmark.getResolution()));
        Assert.assertTrue(ded.getType().equals(benchmark.getType()));
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
        return new JUnit4TestAdapter(DefaultExceptionDefinitionTest.class);
    }
}
