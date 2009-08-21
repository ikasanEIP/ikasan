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
package org.ikasan.framework.payload.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.resource.ResourceException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.jmock.Expectations;
import org.jmock.Mockery;

/**
 * JUnit test class for FileSystemPayloadPublisher
 * @author Ikasan Development Team
 * 
 * TODO Test coverage is currently at 88.0%.
 */
public class FileSystemPayloadPublisherTest extends TestCase
{

    /** Logger instance */
    private static Logger logger = Logger.getLogger(FileSystemPayloadPublisherTest.class);

    /** The test content */
    private final static String PAYLOAD_CONTENT = "payloadContent";

    /** The test file name */
    private final static String FILE_NAME = "foobar.txt";

    
    /**
     * Test publishing
     */
    public void testSuccessfulPublish()
    {
        try
        {
            FileSystemPayloadPublisher fileSystemPayloadPublisher = new FileSystemPayloadPublisher(".");
            Payload payload = getMockedPayload();
            fileSystemPayloadPublisher.publish(payload);
            File fileToRead = new File("." + File.separator + FILE_NAME);
            try
            {
                FileInputStream fis = new FileInputStream(fileToRead);
                byte[] content = new byte[100];
                fis.read(content);
                fis.close();
                String contentAsString = new String(content);
                contentAsString = contentAsString.trim();
                assertEquals(PAYLOAD_CONTENT, contentAsString);
            }
            catch (FileNotFoundException e)
            {
                fail("File was not found to read back in.");
            }
            catch (IOException e)
            {
                fail("Could not read content.");
            }

        }
        catch (ResourceException re)
        {
            logger.warn("Caught ResourceException", re);
            fail();
        }
    }

    /**
     * Test publishing to a null directory
     */
    public void testNullDirectory()
    {
        try
        {
            FileSystemPayloadPublisher fileSystemPayloadPublisher = new FileSystemPayloadPublisher(null);
            Payload payload = getMockedPayload();
            fileSystemPayloadPublisher.publish(payload);
            fail();
        }
        catch (ResourceException re)
        {
            logger.info("Caught ResourceException as expected.");
        }
    }

    /**
     * Test publishing to a non existent directory
     */
    public void testNonExistentDirectory()
    {
        try
        {
            FileSystemPayloadPublisher fileSystemPayloadPublisher = new FileSystemPayloadPublisher("");
            Payload payload = getMockedPayload();
            fileSystemPayloadPublisher.publish(payload);
            fail();
        }
        catch (ResourceException re)
        {
            logger.info("Caught ResourceException as expected.");
        }
    }

    /**
     * Helper class that returns a mocked payload for testing purposes
     *
     * @return Mocked payload
     */
    private Payload getMockedPayload()
    {
        Mockery mockery = new Mockery();
        final byte[] payloadContent = PAYLOAD_CONTENT.getBytes();
        final Payload payload = mockery.mock(Payload.class);
        mockery.checking(new Expectations()
        {
            {
                allowing(payload).getId();
                will(returnValue("1"));
                allowing(payload).getContent();
                will(returnValue(payloadContent));
                allowing(payload).getName();
                will(returnValue(FILE_NAME));
            }
        });
        return payload;
    }

    @Override
    protected void tearDown()
    {
        File fileToDelete = new File("." + File.separator + FILE_NAME);
        if (fileToDelete.exists())
        {
            logger.info("Cleaning up [" + fileToDelete.getAbsoluteFile() + "]");
            if (fileToDelete.delete())
            {
                logger.info("Unit test successfully cleaned up.");
            }
            else
            {
                logger.error("Unit test failed to clean up, please manually clean up the file!");
            }
        }
    }

}
