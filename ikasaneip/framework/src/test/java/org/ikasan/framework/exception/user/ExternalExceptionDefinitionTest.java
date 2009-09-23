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
