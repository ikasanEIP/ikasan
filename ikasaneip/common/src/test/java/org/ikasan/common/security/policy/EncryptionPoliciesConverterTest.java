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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;


import org.ikasan.common.security.algo.PBE;
import org.ikasan.common.security.policy.EncryptionPolicy;
import org.ikasan.common.security.policy.EncryptionPolicies;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import junit.framework.JUnit4TestAdapter;

/**
 * This test class supports the XStream converter for <code>PolicyConverter</code>.
 *
 * @author Ikasan Development Team
 */
public class EncryptionPoliciesConverterTest
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(EncryptionPoliciesConverterTest.class);
    
    /** The encryption policies */
    private EncryptionPolicies encryptionPolicies = new EncryptionPolicies();
    
    /** The version */
    private String version = "1.0";
    
    /** The schema instance name space URI */
    private String schemaInstanceNSURI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
    
    /** The no name space schema location */
    private String noNamespaceSchemaLocation = "http://www.ikasan.org/security/conf";
    
    /** The application policy */
    private EncryptionPolicy encryptionPolicy = new EncryptionPolicy();
    
    /** The application policy list */
    private List<EncryptionPolicy> encryptionPolicyList = new ArrayList<EncryptionPolicy>();
    
    /** The algorithm (PBE) */
    private PBE algorithm = new PBE();
    
    /** The policy name */
    private String name = "myPolicy";
    
    /** The cipher */
    private String cipher = "myCipher";
    
    /** The password/passphrase */
    private String pass = "myPass";
    
    /** The iteration count */
    private Integer iterationCount = 21;
    
    /** The salt */
    private String salt = "mySalt";

    /**
     * Setup runs before each test
     */
    @Before public void setUp()
    {
        // create algorithm
        algorithm.setCipher(cipher);
        algorithm.setPass(pass);
        algorithm.setIterationCount(iterationCount);
        algorithm.setSalt(salt);
        
        // create encryption policy
        encryptionPolicy.setName(name);
        encryptionPolicy.setAlgorithm(algorithm);

        // create encryption policy list and populate encryption policies class
        encryptionPolicyList.add(encryptionPolicy);
        encryptionPolicies.setEncryptionPolicies(encryptionPolicyList);
        encryptionPolicies.setVersion(version);
        encryptionPolicies.setSchemaInstanceNSURI(schemaInstanceNSURI);
        encryptionPolicies.setNoNamespaceSchemaLocation(noNamespaceSchemaLocation);
    }

    /**
     * Test fully populated PBE
     * 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws IOException 
     */
    @Test public void EncryptionPoliciesConverter()
        throws ParserConfigurationException, SAXException, IOException
    {
        String expectedPolicyXML = "<EncryptionPolicies version=\"1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xsi:noNamespaceSchemaLocation=\"http://www.ikasan.org/security/conf\">\n"
            + "  <EncryptionPolicy name=\"myPolicy\">\n"
            + "    <PBE cipher=\"myCipher\" iterationCount=\"21\" pass=\"myPass\" salt=\"mySalt\"/>\n"
            + "  </EncryptionPolicy>\n"
            + "</EncryptionPolicies>";
        
        Boolean validate = false;
        
        String generatedXML = encryptionPolicies.toXML(validate);
        Assert.assertEquals(expectedPolicyXML, generatedXML);
        
        EncryptionPolicies encryptionPolicies2 = EncryptionPolicies.fromXML(generatedXML, validate);
        Assert.assertTrue(encryptionPolicies.equals(encryptionPolicies2));
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
        return new JUnit4TestAdapter(EncryptionPoliciesConverterTest.class);
    }    
    
}
