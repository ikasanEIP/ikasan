/*
 * $Id: AlgorithmConverterTest.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/test/java/org/ikasan/common/security/algo/AlgorithmConverterTest.java $
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
import org.ikasan.common.security.algo.PBE;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the XStream converter for <code>AlgorithmConverterTest</code>.
 *
 * @author <a href="jeff.mitchell:info@ikasan.org">Jeff Mitchell</a>
 */
public class AlgorithmConverterTest
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(AlgorithmConverterTest.class);
    
    /** The cipher */
    private String cipher;
    
    /** The password/passphrase */
    private String pass;
    
    /** The PBE algorithm */
    private PBE pbeAlg;
    
    /** iteration count */
    private Integer iterationCount;
    
    /** Salt/Seed for the algorithm */
    private String salt;
    
    /** The blowfish algorithm */
    private Blowfish blowfishAlg;

    /**
     * Setup runs before each test
     */
    @Before public void setUp()
    {
        cipher = "myCipher";
        pass = "myPass";
        iterationCount = 21;
        salt = "mySalt";
        
        pbeAlg = new PBE();
        pbeAlg.setCipher(cipher);
        pbeAlg.setPass(pass);
        pbeAlg.setIterationCount(iterationCount);
        pbeAlg.setSalt(salt);

        blowfishAlg = new Blowfish();
        blowfishAlg.setCipher(cipher);
        blowfishAlg.setPass(pass);
    }

    /**
     * Test fully populateed PBE
     */
    @Test public void AlgorithmConverterFullPBE()
    {
        String expectedPBEXML = "<PBE cipher=\"" + cipher 
            + "\" iterationCount=\"" + iterationCount + "\"" 
            + " pass=\"" + pass + "\""
            + " salt=\"" + salt + "\"/>";
        
        String generatedXML = pbeAlg.toXML();
        Assert.assertEquals(expectedPBEXML, generatedXML);
        
        Algorithm algorithm2 = pbeAlg.fromXML(generatedXML);
        Assert.assertTrue(pbeAlg.equals(algorithm2));
    }
    
    /**
     * Test fully populated blowfish.
     */
    @Test public void AlgorithmConverterFullBlowfish()
    {
        String expectedBlowfishXML = "<Blowfish cipher=\"" + cipher + "\"" 
            + " pass=\"" + pass + "\"/>";
        
        String generatedXML = blowfishAlg.toXML();
        Assert.assertEquals(expectedBlowfishXML, generatedXML);
        
        Algorithm algorithm2 = blowfishAlg.fromXML(generatedXML);
        Assert.assertTrue(blowfishAlg.equals(algorithm2));
    }
    
    /**
     * Test part populated PBE (one attribute being 'null').
     */
    @Test public void AlgorithmConverterPartPBE()
    {
        // remove cipher
        pbeAlg.setCipher(null);
        
        String expectedPBEXML = "<PBE iterationCount=\"" + iterationCount + "\"" 
            + " pass=\"" + pass + "\""
            + " salt=\"" + salt + "\"/>";
        
        String generatedXML = pbeAlg.toXML();
        Assert.assertEquals(expectedPBEXML, generatedXML);
        
        Algorithm algorithm2 = pbeAlg.fromXML(generatedXML);
        Assert.assertTrue(pbeAlg.equals(algorithm2));
    }
    
    /**
     * Test part populated blowfish (one attribute being 'null').
     */
    @Test public void AlgorithmConverterPartBlowfish()
    {
        blowfishAlg.setPass(null);
        String expectedBlowfishXML = "<Blowfish cipher=\"" + cipher + "\"/>";
        
        String generatedXML = blowfishAlg.toXML();
        Assert.assertEquals(expectedBlowfishXML, generatedXML);
        
        Algorithm algorithm2 = blowfishAlg.fromXML(generatedXML);
        Assert.assertTrue(blowfishAlg.equals(algorithm2));
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
        return new JUnit4TestAdapter(AlgorithmConverterTest.class);
    }    
    
}
