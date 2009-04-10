/*
 * $Id: EncryptionPoliciesConverterTest.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/test/java/org/ikasan/common/security/policy/EncryptionPoliciesConverterTest.java $
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
 * @author <a href="jeff.mitchell:info@ikasan.org">Ikasan Development Team</a>
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
