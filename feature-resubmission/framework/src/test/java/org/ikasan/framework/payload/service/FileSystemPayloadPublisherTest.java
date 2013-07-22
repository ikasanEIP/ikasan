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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.resource.ResourceException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.ikasan.common.FilePayloadAttributeNames;
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
                allowing(payload).getAttribute(FilePayloadAttributeNames.FILE_NAME);
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
