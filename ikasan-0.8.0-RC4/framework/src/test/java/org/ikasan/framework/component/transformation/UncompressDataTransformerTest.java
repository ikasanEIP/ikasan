/*
 * $Id$
 * $URL$
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
package org.ikasan.framework.component.transformation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.ikasan.common.Payload;
import org.ikasan.common.component.DefaultPayload;
import org.ikasan.framework.component.Event;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link UncompressDataTransformer}
 * 
 * TODO: How can we test exception handling if closing a stream fails?
 * 
 * @author Ikasan Development Team
 *
 */

public class UncompressDataTransformerTest
{
    /** Transformer instance being tested */
    private Transformer transformerToTest = new UncompressDataTransformer();

    /**
     * Uncompressing data in GZIP file, result in the original 
     * uncompressed single file
     * 
     * @throws IOException thrown if test data file could not be read
     */
    @Test public void uncompress_GZIP_into_original_single_file() throws IOException
    {
        // Setup test objects
        Payload payload = new DefaultPayload("gzipFile", this.loadFile("data/TEST.TXT.gz"));
        List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(payload);
        Event event = new Event("unitTest", "uncompressTestCase", "gzipFileEvent", payloads);

        // Run the test
        this.transformerToTest.onEvent(event);

        // Making assertions
        Assert.assertEquals(new String(this.loadFile("data/TEST.TXT")), new String(event.getPayloads().get(0).getContent()));
    }

    /**
     * Reading compressed data fails with IOException. Transformer must stop processing event
     * any further and throw a TransfromationException.
     */
    @Test(expected=TransformationException.class)
    public void reading_compressed_data_fails_transformer_quits_and_throws_exception()
    {
        Payload payload = new DefaultPayload("gzipFile", "This is not a GZIP format file".getBytes());
        List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(payload);
        Event event = new Event("unitTest", "failureTestCase", "gzipFileEvent", payloads);

        this.transformerToTest.onEvent(event);
        Assert.fail();
    }

    /**
     * Incoming event contains multiple payloads each is a GZIP file. The transformer
     * will uncompress each of the payloads
     * 
     * @throws IOException thrown if test data file could not be read
     */
    @Test public void uncompress_multiple_files() throws IOException
    {
        // Setup test objects
        Payload payload_1 = new DefaultPayload("gzipFile_1", this.loadFile("data/TEST.TXT.gz"));
        Payload payload_2 = new DefaultPayload("gzipFile_2", this.loadFile("data/TEST.TXT.gz"));
        List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(payload_1);
        payloads.add(payload_2);
        Event event = new Event("unitTest", "multiplePayloadsTestCase", "gzipFileEvent", payloads);

        // Run the test
        this.transformerToTest.onEvent(event);

        // Making assertions
        Assert.assertEquals(new String(this.loadFile("data/TEST.TXT")), new String(event.getPayloads().get(0).getContent()));
        Assert.assertEquals(new String(this.loadFile("data/TEST.TXT")), new String(event.getPayloads().get(1).getContent()));
    }

    /**
     * Utility method to load test files from classpath.
     * 
     * @param fileName The name of file to be loaded.
     * @return byte array representation of loaded file
     * @throws IOException Thrown if file could not be read.
     */
    private byte[] loadFile(final String fileName) throws IOException
    {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(fileName);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int c = resourceAsStream.read(); c != UncompressDataTransformer.END_OF_FILE; c = resourceAsStream.read())
        {
            // Write each byte into the output stream.
            byteArrayOutputStream.write(c);
        }
        byte[] content = byteArrayOutputStream.toByteArray();

        // Clean up
        resourceAsStream.close();
        byteArrayOutputStream.close();

        return content;
    }
}

