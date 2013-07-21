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
