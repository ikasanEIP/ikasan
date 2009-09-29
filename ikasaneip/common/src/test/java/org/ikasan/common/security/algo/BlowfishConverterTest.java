/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.common.security.algo;

// Imported java classes
import junit.framework.JUnit4TestAdapter;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the XStream converter for <code>BlowfishConverterTest</code>.
 *
 * @author Ikasan Development Team
 */
public class BlowfishConverterTest
{
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(BlowfishConverterTest.class);
    
    /** The cipher */
    private String cipher;
    
    /** The password/phrase */
    private String pass;

    /**
     * Setup runs before each test
     */
    @Before public void setUp()
    {
        cipher = "myCipher";
        pass = "myPass";
    }

    /**
     * Test fully populated blowfish
     */
    @Test public void BlowfishConverter()
    {
        String expectedXML = "<Blowfish cipher=\"" + cipher + "\""
            + " pass=\"" + pass + "\"/>";
        
        Blowfish algorithm = new Blowfish();
        algorithm.setCipher(cipher);
        algorithm.setPass(pass);
        
        String generatedXML = algorithm.toXML();
        Assert.assertEquals(expectedXML, generatedXML);
        
        Algorithm algorithm2 = algorithm.fromXML(generatedXML);
        Assert.assertTrue(algorithm.equals(algorithm2));
    }
    
    /**
     * Test partially populated blowfish (one of the attributes being 'null').
     */
    @Test public void PartialBlowfishConverter()
    {
        pass = null;
        String expectedXML = "<Blowfish cipher=\"" + cipher + "\"/>";
        
        Blowfish algorithm = new Blowfish();
        algorithm.setCipher(cipher);
        algorithm.setPass(pass);
        
        String generatedXML = algorithm.toXML();
        Assert.assertEquals(expectedXML, generatedXML);
        
        Algorithm algorithm2 = algorithm.fromXML(generatedXML);
        Assert.assertTrue(algorithm.equals(algorithm2));
    }
    
    /**
     * Teardown after each test
     */
    @After public void tearDown()
    {
        // nothing to tear down
        logger.info("tearDown");
    }

    /**
     * The suite is this class
     * @return JUnit Test class
     */
    public static junit.framework.Test suite() 
    {
        return new JUnit4TestAdapter(BlowfishConverterTest.class);
    }    
    
}
