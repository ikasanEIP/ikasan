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
package org.ikasan.framework.payload.service;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.resource.ResourceException;

import org.ikasan.common.Payload;
import org.ikasan.framework.payload.service.PayloadInputStreamAcquirer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * FileSystemInputStreamPayloadPublisher tests
 * 
 * @author Ikasan Development Team
 * 
 */
public class FileSystemInputStreamPayloadPublisherTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    PayloadInputStreamAcquirer payloadInputStreamAcquirer = mockery.mock(PayloadInputStreamAcquirer.class);
    Payload payload = mockery.mock(Payload.class);
    private byte[] payloadContent = "someContent".getBytes();
    InputStream byteContentInputStream = new ByteArrayInputStream(payloadContent);
    String payloadName = "payloadName";

    @Test
    public void testPublish() throws ResourceException, IOException
    {
        String parentPath = (".");
        FileSystemInputStreamPayloadPublisher fileSystemInputStreamPayloadPublisher = new FileSystemInputStreamPayloadPublisher(parentPath,
            payloadInputStreamAcquirer);
        File outputFile = new File(payloadName);
        // make sure this file does not exist upfront
        Assert.assertFalse(outputFile.exists());
        mockery.checking(new Expectations()
        {
            {
                one(payloadInputStreamAcquirer).acquireInputStream(payload);
                will(returnValue(byteContentInputStream));
                one(payload).getName();
                will(returnValue(payloadName));
            }
        });
        fileSystemInputStreamPayloadPublisher.publish(payload);
        Assert.assertTrue("file should exist after publisher has run", outputFile.exists());
        byte[] fileContent = load(outputFile);
        Assert.assertArrayEquals(payloadContent, fileContent);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testPublishWithNonExistantDirectoryThrowsResourceException() throws IOException
    {
        String parentPath = ("nonexistant");
        FileSystemInputStreamPayloadPublisher fileSystemInputStreamPayloadPublisher = new FileSystemInputStreamPayloadPublisher(parentPath,
            payloadInputStreamAcquirer);
        mockery.checking(new Expectations()
        {
            {
                one(payloadInputStreamAcquirer).acquireInputStream(payload);
                will(returnValue(byteContentInputStream));
                one(payload).getName();
                will(returnValue(payloadName));
            }
        });
        try
        {
            fileSystemInputStreamPayloadPublisher.publish(payload);
            fail("ReourceException should have been thrown for non existant parent dir");
        }
        catch (ResourceException re)
        {
            // Do Nothing
        }
    }

    @Test
    public void testPublishWithParentAsFileThrowsResourceException() throws IOException
    {
        String parentPath = ("parent");
        File parent = new File(parentPath);
        parent.createNewFile();
        FileSystemInputStreamPayloadPublisher fileSystemInputStreamPayloadPublisher = new FileSystemInputStreamPayloadPublisher(parentPath,
            payloadInputStreamAcquirer);
        mockery.checking(new Expectations()
        {
            {
                one(payloadInputStreamAcquirer).acquireInputStream(payload);
                will(returnValue(byteContentInputStream));
                one(payload).getName();
                will(returnValue(payloadName));
            }
        });
        try
        {
            fileSystemInputStreamPayloadPublisher.publish(payload);
            fail("ReourceException should have been thrown for non existant parent dir");
        }
        catch (ResourceException re)
        {
            // Do Nothing
        }
    }

    @After
    @Before
    public void cleanupTestData()
    {
        File preExistingFile = new File(payloadName);
        if (preExistingFile.exists())
        {
            preExistingFile.delete();
        }
        File preExistingParent = new File("parent");
        if (preExistingParent.exists())
        {
            preExistingParent.delete();
        }
    }

    private static byte[] load(File file) throws IOException
    {
        InputStream is = new FileInputStream(file);
        // Get the size of the file
        long length = file.length();
        if (length > Integer.MAX_VALUE)
        {
            // File is too large
        }
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
        {
            offset += numRead;
        }
        // Ensure all the bytes have been read in
        if (offset < bytes.length)
        {
            throw new IOException("Could not completely read file " + file.getName());
        }
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
}
