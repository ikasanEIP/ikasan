/* 
 * $Id: FileSystemInputStreamPayloadPublisherTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/payload/service/FileSystemInputStreamPayloadPublisherTest.java $
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
