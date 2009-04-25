 /* 
 * $Id: FileTransferInputStreamPayloadPublisherTest.java 16744 2009-04-22 10:05:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/client-filetransfer/src/test/java/org/ikasan/framework/payload/service/FileTransferInputStreamPayloadPublisherTest.java $
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
import java.io.IOException;
import java.io.InputStream;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionSpec;

import org.ikasan.common.Payload;
import org.ikasan.connector.base.outbound.EISConnectionFactory;
import org.ikasan.connector.basefiletransfer.outbound.BaseFileTransferConnection;
import org.ikasan.framework.payload.service.PayloadInputStreamAcquirer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for FileTransferInputStreamPayloadPublisher
 * 
 * @author Ikasan Development Team
 *
 */
public class FileTransferInputStreamPayloadPublisherTest {

	/**
	 * Mockery for mocking concrete classes
	 */
	private Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	/** Mock the PayloadInputStreamAcquirer */
	PayloadInputStreamAcquirer payloadInputStreamAcquirer = mockery
			.mock(PayloadInputStreamAcquirer.class);

	/** Mock the payload */
	Payload payload = mockery.mock(Payload.class);

	/** Some test content */
	private byte[] payloadContent = "someContent".getBytes();

	/** A byte input stream */
	InputStream byteContentInputStream = new ByteArrayInputStream(
			payloadContent);

	/** Test payload name */
	String payloadName = "payloadName";

	/** Test rename extension */
	String renameExtension = ".rename";

	/**
	 * Mocked BaseFileTransferConnection
	 */
	BaseFileTransferConnection fileTransferConnection = mockery
			.mock(BaseFileTransferConnection.class);
	/**
	 * Mocked EISConnectionFactory
	 */
	EISConnectionFactory connectionFactory = mockery
			.mock(EISConnectionFactory.class);

	/** Test connection spec */
	ConnectionSpec connectionSpec = mockery.mock(ConnectionSpec.class);

	/** Parent path */
	String parentPath = "parentPath";

	/**
	 * Test the happy path, file delivered OK
	 * 
	 * @throws IOException File System Exception
	 * @throws ResourceException Exception thrown by connector
	 */
	@Test
	public void testPublish_happyPath() throws IOException, ResourceException {

		FileTransferInputStreamPayloadPublisher fileTransferInputStreamPayloadPublisher = new FileTransferInputStreamPayloadPublisher(
				payloadInputStreamAcquirer, parentPath, renameExtension,
				connectionFactory, connectionSpec);

		mockery.checking(new Expectations() {
			{
				one(payloadInputStreamAcquirer).acquireInputStream(payload);
				will(returnValue(byteContentInputStream));
				one(payload).getName();
				will(returnValue(payloadName));

				one(connectionFactory).getConnection(connectionSpec);
				will(returnValue(fileTransferConnection));

				one(fileTransferConnection).deliverInputStream(
						byteContentInputStream, payloadName, parentPath, false,
						renameExtension, false, false);

			}
		});

		fileTransferInputStreamPayloadPublisher.publish(payload);

		mockery.assertIsSatisfied();
	}

	/**
	 * Test that a ResourceException is thrown when an IOException is caused
	 * 
	 * @throws IOException File Exception 
	 */
	@Test
	public void testPublish_throwsResourceExceptionForIOException()
			throws IOException {

		final IOException ioException = new IOException();
		FileTransferInputStreamPayloadPublisher fileTransferInputStreamPayloadPublisher = new FileTransferInputStreamPayloadPublisher(
				payloadInputStreamAcquirer, parentPath, renameExtension,
				connectionFactory, connectionSpec);

		mockery.checking(new Expectations() {
			{
				one(payloadInputStreamAcquirer).acquireInputStream(payload);
				will(throwException(ioException));
				

			}
		});

		ResourceException thrownException = null;
		try {
			fileTransferInputStreamPayloadPublisher.publish(payload);
			fail("ResourceException should have been thrown for an underlying IOException");
		} catch (ResourceException re) {
			thrownException = re;
		}
		
		Assert.assertNotNull(thrownException);

		mockery.assertIsSatisfied();
	}

}
