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
