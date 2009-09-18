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
package org.ikasan.common.util;

// Imported junit classes
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.codec.DecoderException;

/**
 * unit tests for the {@link org.ikasan.common.util.Codec} class.
 *
 * @author Ikasan Development Team
 */
public class CodecTest
    extends TestCase
{

    /** Test data */
    private byte[] data = new byte[1];
    
    /** Test encoding */
    String encoding = null;
    
    /** The result of the encoding */
    byte[] encodedData;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        data[0] = 1; 
    }
    
    /**
     * Test base64 encoding
     */
    public void testBase64Encoding()
    {
        encoding = "base64";
        encodedData = Codec.encode(data, encoding);
        assertTrue(encodedData[0] == 65);
        assertTrue(encodedData[1] == 81);
        assertTrue(encodedData[2] == 61);
        assertTrue(encodedData[3] == 61);
    }

    /**
     * Test hex encoding
     */
    public void testHexEncoding()
    {
        encoding = "hex";
        encodedData = Codec.encode(data, encoding);
        assertTrue(encodedData[0] == 48);
        assertTrue(encodedData[1] == 49);
    }

    /**
     * Test no encoding
     */
    public void testNoEncoding()
    {
        encoding = "noenc";
        encodedData = Codec.encode(data, encoding);
        assertTrue(encodedData[0] == 1);
    }

    /**
     * Test bad encoding
     */
    public void testUnsupportedEncoding()
    {
        encoding = "";
        try
        {
            encodedData = Codec.encode(data, encoding);
            fail("Exception should ghave been thrown.");
        }
        catch(IllegalArgumentException e)
        {
            //Do nothing
        }
    }

    /**
     * Test base64 decoding
     * @throws DecoderException 
     */
    public void testBase64Decoding() throws DecoderException
    {
        encoding = "base64";
        encodedData = new byte[4];
        encodedData[0] = 65;
        encodedData[1] = 81;
        encodedData[2] = 61;
        encodedData[3] = 61;
        data = Codec.decode(encodedData, encoding);
        assertTrue(data[0] == 1);
    }

    /**
     * Test hex decoding
     * @throws DecoderException 
     */
    public void testHexDecoding() throws DecoderException
    {
        encodedData = new byte[2];
        encodedData[0] = 48;
        encodedData[1] = 49;
        encoding = "hex";
        data = Codec.decode(encodedData, encoding);
        assertTrue(data[0] == 1);
    }

    /**
     * Test no decoding
     * @throws DecoderException 
     */
    public void testNoDecoding() throws DecoderException
    {
        encodedData = new byte[1];
        encodedData[0] = 1;
        encoding = "noenc";
        data = Codec.decode(encodedData, encoding);
        assertTrue(data[0] == 1);
    }

    /**
     * Test bad decoding
     * @throws DecoderException 
     */
    public void testUnsupportedDecoding() throws DecoderException
    {
        encoding = "";
        try
        {
            encodedData = Codec.decode(encodedData, encoding);
            fail("Exception should ghave been thrown.");
        }
        catch(IllegalArgumentException e)
        {
            //Do nothing
        }
    }
    
    /**
     * Runs suite test.
     * @return Test suite
     */
    public static Test suite()
    {
        TestSetup setup = new TestSetup(new TestSuite(CodecTest.class))
        {
            @Override
            protected void setUp()
            {
                // do your one-time setup here!
            }

            @Override
            protected void tearDown()
            {
                // do your one-time tear down here!
            }
        };

        return setup;
    }
    
}
