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
