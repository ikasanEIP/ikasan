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

import java.util.HashMap;
import java.util.Map;

import javax.resource.ResourceException;

import junit.framework.TestCase;

import org.ikasan.common.Payload;
import org.ikasan.connector.base.outbound.EISConnectionFactory;
import org.ikasan.connector.basefiletransfer.outbound.BaseFileTransferConnection;
import org.jmock.Expectations;
import org.jmock.Mockery;

/**
 * @author Ikasan Development Team
 * 
 */
public class FileTransferPayloadPublisherTest extends TestCase
{
    /**
     * Mockery for interfaces
     */
    private Mockery mockery = new Mockery();
    /**
     * Mocked Payload
     */
    Payload payload = mockery.mock(Payload.class);
    /**
     * Mocked BaseFileTransferConnection
     */
    BaseFileTransferConnection fileTransferConnection = mockery.mock(BaseFileTransferConnection.class);
    /**
     * Mocked EISConnectionFactory
     */
    EISConnectionFactory connectionFactory = mockery.mock(EISConnectionFactory.class);

    /**
     * Performs the test setting the boolean arguments to a known value
     * 
     * @throws ResourceException Exception thrown by Connector
     */
    public void testPublish() throws ResourceException
    {
        final String outputDir = "outputDir";
        final boolean checksumDelivered = true;
        final boolean overwrite = true;
        final String renameExtension = ".rename";
        final boolean unzip = true;
        final Map<String, String> outputTargets = new HashMap<String, String>();
        FileTransferPayloadPublisher fileTransferPayloadPublisher = new FileTransferPayloadPublisher(outputDir, renameExtension, connectionFactory, null);
        fileTransferPayloadPublisher.setOverwrite(overwrite);
        fileTransferPayloadPublisher.setChecksumDelivered(checksumDelivered);
        fileTransferPayloadPublisher.setUnzip(unzip);
        fileTransferPayloadPublisher.setOutputTargets(outputTargets);
        mockery.checking(new Expectations()
        {
            {
                one(connectionFactory).getConnection();
                will(returnValue(fileTransferConnection));
                one(fileTransferConnection).deliverPayload(payload, outputDir, outputTargets, overwrite, renameExtension, checksumDelivered, unzip, true);// call
                                                                                                                                                            // the
                                                                                                                                                            // connector
                one(fileTransferConnection).close();// close the connection
            }
        });
        fileTransferPayloadPublisher.publish(payload);
    }

    /**
     * Test the isUnzip method call
     */
    public void testIsUnzip()
    {
        FileTransferPayloadPublisher fileTransferPayloadPublisher = new FileTransferPayloadPublisher(null, null, connectionFactory, null);
        fileTransferPayloadPublisher.setUnzip(true);
        assertTrue(fileTransferPayloadPublisher.isUnzip());
        fileTransferPayloadPublisher.setUnzip(false);
        assertFalse(fileTransferPayloadPublisher.isUnzip());
    }

    /**
     * Test the isOverwrite method call
     */
    public void testIsOverwrite()
    {
        FileTransferPayloadPublisher fileTransferPayloadPublisher = new FileTransferPayloadPublisher(null, null, connectionFactory, null);
        fileTransferPayloadPublisher.setOverwrite(true);
        assertTrue(fileTransferPayloadPublisher.isOverwrite());
        fileTransferPayloadPublisher.setOverwrite(false);
        assertFalse(fileTransferPayloadPublisher.isOverwrite());
    }

    /**
     * Test the isChecksumDelivered method call
     */
    public void testIsChecksumDelivered()
    {
        FileTransferPayloadPublisher fileTransferPayloadPublisher = new FileTransferPayloadPublisher(null, null, connectionFactory, null);
        fileTransferPayloadPublisher.setChecksumDelivered(true);
        assertTrue(fileTransferPayloadPublisher.isChecksumDelivered());
        fileTransferPayloadPublisher.setChecksumDelivered(false);
        assertFalse(fileTransferPayloadPublisher.isChecksumDelivered());
    }

    /**
     * Test the isCleanup method call
     */
    public void testIsCleanup()
    {
        FileTransferPayloadPublisher fileTransferPayloadPublisher = new FileTransferPayloadPublisher(null, null, connectionFactory, null);
        fileTransferPayloadPublisher.setCleanup(true);
        assertTrue(fileTransferPayloadPublisher.isCleanup());
        fileTransferPayloadPublisher.setCleanup(false);
        assertFalse(fileTransferPayloadPublisher.isCleanup());
    }

    /**
     * Test the getOutputTargets method call
     */
    public void testGetOutputTargets()
    {
        FileTransferPayloadPublisher fileTransferPayloadPublisher = new FileTransferPayloadPublisher(null, null, connectionFactory, null);
        Map<String, String> outputTargets = new HashMap<String, String>();
        fileTransferPayloadPublisher.setOutputTargets(outputTargets);
        assertEquals(outputTargets, fileTransferPayloadPublisher.getOutputTargets());
    }
}
