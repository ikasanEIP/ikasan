/*
 * $Id:$
 * $URL:$
 *
 * ====================================================================
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
 * ====================================================================
 */
package org.ikasan.connector.base.socket;

import org.junit.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * TCP Socket Test Class
 *
 * @author Ikasan Development Team
 */
public class TCPSocketTest
{
    private static byte START_MARKER = '|';

    private static byte END_MARKER = '|';

    private static String CHARSET_ISO_88591 = "ISO-8859-1";

    private static String CHARSET_UTF8 = "UTF-8";

    private static String CHARSET_INVALID = "invalid";

    private byte[] mockedOutputStreamWriteResult;

    private boolean isMockedFlushHit;

    private BufferedOutputStream mockOutputStream = new BufferedOutputStream(null)
    {
        @Override
        public void write(byte b[]) throws IOException
        {
            mockedOutputStreamWriteResult = b;
        }

        @Override
        public void flush() throws IOException
        {
            isMockedFlushHit = true;
        }
    };

    /**
     * Unit under test
     */
    private TCPSocket uut;


    /**
     * Setup before each test
     */
    @Before public void setUp()
    {
        System.out.println("setUp");
        uut = new TCPSocket();
        ReflectionTestUtils.setField(uut, "bons", mockOutputStream);
    }

    @Test public void test_send_when_no_characterset_was_set() throws IOException
    {
        String input = "sampleData";
        final byte[] data = input.getBytes();
        //do test
        uut.send(data, START_MARKER, END_MARKER);
        // assert
        final byte[] expectedData = "|sampleData|".getBytes();
        Assert.assertTrue(isMockedFlushHit);
        System.out.println("Expected:" + new String(expectedData));
        System.out.println("Actual:" + new String(mockedOutputStreamWriteResult));
        Assert.assertArrayEquals(expectedData, mockedOutputStreamWriteResult);
    }

    @Test
    public void test_send_when_characterset_was_set_to_iso88591() throws IOException
    {
        // set uut iso
        uut.setAcceptedCharsetName(CHARSET_ISO_88591);
        String input = "sampleData";
        //do test
        uut.send(input, START_MARKER, END_MARKER);
        // assert
        final byte[] expectedData = "|sampleData|".getBytes();
        Assert.assertTrue(isMockedFlushHit);
        System.out.println("Expected:" + new String(expectedData, CHARSET_ISO_88591));
        System.out.println("Actual:" + new String(mockedOutputStreamWriteResult, CHARSET_ISO_88591));
        Assert.assertArrayEquals(expectedData, mockedOutputStreamWriteResult);
    }

    @Test
    public void test_send_when_characterset_was_set_to_iso88591_and_input_has_complex_characters() throws IOException
    {
        // set uut iso
        uut.setAcceptedCharsetName(CHARSET_ISO_88591);
        String input = "{|}~¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñò";
        //do test
        uut.send(input, START_MARKER, END_MARKER);
        // assert
        final byte[] expectedData = "|{|}~¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñò|"
                .getBytes(CHARSET_ISO_88591);
        Assert.assertTrue(isMockedFlushHit);
        System.out.println("Expected:" + new String(expectedData, CHARSET_ISO_88591));
        System.out.println("Actual--:" + new String(mockedOutputStreamWriteResult, CHARSET_ISO_88591));
        Assert.assertArrayEquals(expectedData, mockedOutputStreamWriteResult);
    }

    @Ignore
    @Test
    public void test_send_when_characterset_was_set_to_utf8_and_input_has_complex_characters() throws IOException
    {
        // set uut iso
        uut.setAcceptedCharsetName(CHARSET_UTF8);
        String input = "{|}~¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñò";
        //do test
        uut.send(input, START_MARKER, END_MARKER);
        // assert
        final byte[] expectedData = "|{|}~¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñò|"
                .getBytes();
        Assert.assertTrue(isMockedFlushHit);
        System.out.println("Expected:" + new String(expectedData, CHARSET_UTF8));
        System.out.println("Actual--:" + new String(mockedOutputStreamWriteResult, CHARSET_UTF8));
        Assert.assertArrayEquals(expectedData, mockedOutputStreamWriteResult);
    }

    @Test
    public void test_send_when_characterset_was_set_to_invalid_value() throws IOException
    {
        // set uut iso
        uut.setAcceptedCharsetName(CHARSET_INVALID);
        String input = "simpleData";
        //do test
        uut.send(input, START_MARKER, END_MARKER);
        // assert
        final byte[] expectedData = "|simpleData|".getBytes();
        Assert.assertTrue(isMockedFlushHit);
        System.out.println("Expected:" + new String(expectedData));
        System.out.println("Actual--:" + new String(mockedOutputStreamWriteResult));
        Assert.assertArrayEquals(expectedData, mockedOutputStreamWriteResult);
    }

    private void printByteArrays(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
        {
            sb.append(String.format("%02X ", b));
        }
        System.out.println(sb.toString());
    }

    /**
     * Tear down after each test
     */
    @After public void tearDown()
    {
        // nothing to tear down
        System.out.println("tearDown");
    }
}
