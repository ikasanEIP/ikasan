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
package org.ikasan.common.security.algo;

// Imported java classes
import junit.framework.JUnit4TestAdapter;

import org.apache.log4j.Logger;
import org.ikasan.common.security.algo.Algorithm;
import org.ikasan.common.security.algo.PBE;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the XStream converter for <code>PBEConverterTest</code>.
 *
 * @author <a href="jeff.mitchell:info@ikasan.org">Jeff Mitchell</a>
 */
public class PBEConverterTest
{
    /**
     * The logger instance.
     */
    private static Logger logger = Logger.getLogger(PBEConverterTest.class);
    
    /** The cipher */
    private String cipher;
    
    /** The password/phrase */
    private String pass;
    
    /** The iteration count */
    private int iterationCount;
    
    /** The salt/seed */
    private String salt;
    
    /**
     * Setup runs before each test
     */
    @Before public void setUp()
    {
        cipher = "myCipher";
        pass = "myPass";
        iterationCount = 21;
        salt = "mySalt";
    }

    /**
     * Test fully populated PBE
     */
    @Test public void PBEConverter()
    {
        String expectedXML = "<PBE cipher=\"" + cipher 
            + "\" iterationCount=\"" + iterationCount + "\"" 
            + " pass=\"" + pass + "\""
            + " salt=\"" + salt + "\"/>";
        
        PBE algorithm = new PBE();
        algorithm.setCipher(cipher);
        algorithm.setPass(pass);
        algorithm.setIterationCount(21);
        algorithm.setSalt(salt);
        
        String generatedXML = algorithm.toXML();
        Assert.assertEquals(expectedXML, generatedXML);
        
        Algorithm algorithm2 = algorithm.fromXML(generatedXML);
        Assert.assertTrue(algorithm.equals(algorithm2));
    }
    
    /**
     * Test partially populated PBE (one of the attributes being 'null').
     */
    @Test public void PartialPBEConverter()
    {
        salt = null;
        String expectedXML = "<PBE cipher=\"" + cipher 
            + "\" iterationCount=\"" + iterationCount + "\"" 
            + " pass=\"" + pass + "\"/>";
        
        PBE algorithm = new PBE();
        algorithm.setCipher(cipher);
        algorithm.setPass(pass);
        algorithm.setIterationCount(21);
        algorithm.setSalt(salt);
        
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
        return new JUnit4TestAdapter(PBEConverterTest.class);
    }    
    
}
