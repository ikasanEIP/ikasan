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
package org.ikasan.common.security;

import java.util.HashMap;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ConfigFactory.
 * 
 * @author Ikasan Development Team
 */
public class ConfigFactoryTest
{
    /** The logger */
    private Logger logger = Logger.getLogger(ConfigFactoryTest.class);
    
    /** The properties file */
    private String propFile = "W:\\work\\trunk\\subProjects\\security\\src\\conf\\temp-securePolicies.xml";
    
    /** The policy name */
    private String policyName = "CMITrade01JMS-DSEncryptionPolicy";
    
    /** The security options */
    private Map<String, String> options = null;
    
    /**
     * Setup before each test
     */
    @Before
    public void setUp()
    {
        options = new HashMap<String, String>();
        
        options.put(AuthenticationPolicyConst.POLICY_NAME_LITERAL, policyName);
        options.put(AuthenticationPolicyConst.ENCRYPTION_POLICIES_URL_LITERAL, propFile);
    }

    /**
     * Tear down after each test
     */
    @After
    public void tearDown() 
    {
        // Do Nothing
    }

    /**
     * Test the retrieval of the configuration
     */
    @Test
    public void testGetConfig()
    {
        AuthenticationPolicy config = AuthenticationPolicyFactory.getAuthenticationPolicy(options);
        String policy = config.getPolicyName();
        String encryptionPolicyURL = config.getEncryptionPoliciesURL();
        
        logger.info("Policy: "+policy);
        logger.info("PolicyFile: "+encryptionPolicyURL);
   }
    
    /**
     * JUnit suite
     * @return Test
     */
    public static junit.framework.Test suite() 
    {
        return new JUnit4TestAdapter(ConfigFactoryTest.class);
    }    
    
}
