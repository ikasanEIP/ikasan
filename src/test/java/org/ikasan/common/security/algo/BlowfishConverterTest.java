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
import org.ikasan.common.security.algo.Blowfish;
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
