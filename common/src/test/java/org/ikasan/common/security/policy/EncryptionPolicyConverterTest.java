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
 * @author <a href="jeff.mitchell:info@ikasan.org">Ikasan Development Team</a>
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
