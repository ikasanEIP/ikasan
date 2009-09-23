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
