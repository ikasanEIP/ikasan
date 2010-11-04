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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionSpec;

import org.ikasan.common.FilePayloadAttributeNames;
import org.ikasan.common.Payload;
import org.ikasan.connector.base.outbound.EISConnectionFactory;
import org.ikasan.connector.basefiletransfer.outbound.BaseFileTransferConnection;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for FileTransferAlternateConnectionPayloadPublisherPublisher
 * 
 * @author Ikasan Development Team
 *
 */
public class FileTransferAlternateConnectionPayloadPublisherTest 
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

	/** Mock the PayloadInputStreamAcquirer */
	PayloadInputStreamAcquirer payloadInputStreamAcquirer = 
	    mockery.mock(PayloadInputStreamAcquirer.class);

	/** Mock the payload */
	Payload payload = mockery.mock(Payload.class);

	/** Some test content */
	private byte[] payloadContent = "someContent".getBytes();

	/** A byte input stream */
	InputStream byteContentInputStream = 
	    new ByteArrayInputStream(payloadContent);

	/** Test payload name */
	String payloadName = "payloadName";

	/** Test rename extension */
	String renameExtension = ".rename";

	/**
	 * Mocked BaseFileTransferConnection
	 */
	BaseFileTransferConnection fileTransferConnection = 
	    mockery.mock(BaseFileTransferConnection.class);

    /**
	 * Mocked EISConnectionFactory
	 */
	EISConnectionFactory connectionFactory = 
	    mockery.mock(EISConnectionFactory.class);

	/** Test connection spec */
    ConnectionSpec connectionSpec = mockery.mock(ConnectionSpec.class);
//    ConnectionSpec alternateConnectionSpec = mockery.mock(ConnectionSpec.class);

	/** Parent path */
	String parentPath = "parentPath";

    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        // nothing to do here
    }

	/**
	 * Test the happy path, file delivered OK
	 * 
	 * @throws IOException - Exception if there is an IO problem
	 * @throws ResourceException - Exception if there is a JCA related problem
	 */
	@Test
	public void testPublish_happyPath() 
	    throws IOException, ResourceException 
	{

		FileTransferAlternateConnectionPayloadPublisher payloadPublisher = 
		    new FileTransferAlternateConnectionPayloadPublisher(
				payloadInputStreamAcquirer, parentPath, renameExtension,
				connectionFactory, connectionSpec, connectionSpec);

		mockery.checking(new Expectations() {
			{
				one(payloadInputStreamAcquirer).acquireInputStream(payload);
				will(returnValue(byteContentInputStream));

				one(payload).getAttribute(FilePayloadAttributeNames.FILE_NAME);
				will(returnValue(payloadName));

				one(connectionFactory).getConnection(connectionSpec);
				will(returnValue(fileTransferConnection));

				one(fileTransferConnection).deliverInputStream(
						byteContentInputStream, payloadName, parentPath, false,
						renameExtension, false, false);

			}
		});

		payloadPublisher.publish(payload);
	}

	/**
	 * Test that a ResourceException is thrown when an IOException is caused
	 * from the input stream.
	 * 
     * @throws IOException - Exception if there is an IO problem
     * @throws ResourceException - Exception if there is a JCA related problem
	 */
    @Test(expected = javax.resource.ResourceException.class)    
	public void testPublish_throwsResourceExceptionForIOException()
		throws ResourceException, IOException 
	{

		final IOException ioException = new IOException();

		FileTransferAlternateConnectionPayloadPublisher payloadPublisher = 
            new FileTransferAlternateConnectionPayloadPublisher(
                payloadInputStreamAcquirer, parentPath, renameExtension,
                connectionFactory, connectionSpec, connectionSpec);

		mockery.checking(new Expectations() {
			{
				one(payloadInputStreamAcquirer).acquireInputStream(payload);
				will(throwException(ioException));
			}
		});

	    payloadPublisher.publish(payload);
	}

    /**
     * Test that a ResourceException is thrown for a failed delivery.
     * 
     * @throws IOException - Exception if there is an IO problem
     * @throws ResourceException - Exception if there is a JCA related problem
     */
    @Test(expected = javax.resource.ResourceException.class)    
    public void testPublish_throwsResourceExceptionForResourceException()
        throws ResourceException, IOException 
    {

        final ResourceException resourceException = new ResourceException();

        FileTransferAlternateConnectionPayloadPublisher payloadPublisher = 
            new FileTransferAlternateConnectionPayloadPublisher(
                payloadInputStreamAcquirer, parentPath, renameExtension,
                connectionFactory, connectionSpec, connectionSpec);

        mockery.checking(new Expectations() 
        {
            {
                exactly(1).of(payloadInputStreamAcquirer).acquireInputStream(payload);
                will(returnValue(byteContentInputStream));

                exactly(1).of(payload).getAttribute(FilePayloadAttributeNames.FILE_NAME);
                will(returnValue(payloadName));

                exactly(1).of(connectionFactory).getConnection(connectionSpec);
                will(returnValue(fileTransferConnection));

                exactly(1).of(fileTransferConnection).deliverInputStream(
                        byteContentInputStream, payloadName, parentPath, false,
                        renameExtension, false, false);
                will(throwException(resourceException));
            }
        });

        payloadPublisher.publish(payload);
    }

    // TODO - how to test the private switchActiveConnection method is successful?
    
    /**
     * Tear down after each test
     */
    @After
    public void tearDown()
    {
        // check all expectations were satisfied
        mockery.assertIsSatisfied();
    }
	
}
