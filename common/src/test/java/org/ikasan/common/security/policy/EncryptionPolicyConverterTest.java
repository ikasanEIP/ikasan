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
package org.ikasan.common.security.policy;

// Imported java classes
import junit.framework.JUnit4TestAdapter;

import org.apache.log4j.Logger;
import org.ikasan.common.security.algo.PBE;
import org.ikasan.common.security.policy.EncryptionPolicy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the XStream converter for <code>PolicyConverterTest</code>.
 *
 * @author Ikasan Development Team
 */
public class EncryptionPolicyConverterTest
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(EncryptionPolicyConverterTest.class);
    
    /** Application Policy */
    private EncryptionPolicy encryptionPolicy = new EncryptionPolicy();
    
    /** Name of the policy */
    private String name = "myPolicy";
    
    /** The algorthim (PBE) */
    private PBE algorithm;
    
    /** The cipher */
    private String cipher = "myCipher";
    
    /** The password/passphrase */
    private String pass = "myPass";
    
    /** Iteration count */
    private Integer iterationCount = 21;
    
    /** The salt */
    private String salt = "mySalt";

    /**
     * Setup runs before each test
     */
    @Before public void setUp()
    {
        algorithm = new PBE();
        algorithm.setCipher(cipher);
        algorithm.setPass(pass);
        algorithm.setIterationCount(iterationCount);
        algorithm.setSalt(salt);
        
        encryptionPolicy.setName(name);
        encryptionPolicy.setAlgorithm(algorithm);
    }

    /**
     * Test fully populated PBE
     */
    @Test public void PolicyConverter()
    {
        String expectedPolicyXML = "<EncryptionPolicy name=\"myPolicy\">\n" 
            + "  <PBE cipher=\"myCipher\" iterationCount=\"21\" "
            + "pass=\"myPass\" salt=\"mySalt\"/>\n"
            + "</EncryptionPolicy>";
        
        String generatedXML = encryptionPolicy.toXML();
        Assert.assertEquals(expectedPolicyXML, generatedXML);
        
        EncryptionPolicy encryptionPolicy2 = EncryptionPolicy.fromXML(generatedXML);
        Assert.assertTrue(encryptionPolicy.equals(encryptionPolicy2));
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
        return new JUnit4TestAdapter(EncryptionPolicyConverterTest.class);
    }    
    
}
