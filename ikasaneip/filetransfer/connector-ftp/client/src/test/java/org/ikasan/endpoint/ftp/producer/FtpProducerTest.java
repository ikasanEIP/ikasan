/**
 * Copyright (c) 2017. Mizuho Securities Asia.
 */
package org.ikasan.endpoint.ftp.producer;

import java.io.ByteArrayInputStream;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;

import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.filetransfer.FilePayloadAttributeNames;
import org.ikasan.filetransfer.Payload;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class FtpProducerTest {

	private FtpProducer ftpProducer;

	final private Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private ConnectionFactory connectionFactory = mockery.mock(ConnectionFactory.class, "mockConnectionFactory");

	private FtpProducerConfiguration configuration = mockery.mock(FtpProducerConfiguration.class, "mockFtpProducerConfiguration");

	private FileTransferConnectionTemplate activeFileTransferConnectionTemplate = mockery.mock(FileTransferConnectionTemplate.class, "mockFileTransferConnectionTemplate");

	private FileTransferConnectionTemplate alternateFileTransferConnectionTemplate = mockery.mock(FileTransferConnectionTemplate.class, "mockAlternateFileTransferConnectionTemplate");

	final Payload payload = mockery.mock(Payload.class);

	@Before
	public void setup() {
		ftpProducer = new FtpProducer(connectionFactory);
		ftpProducer.setConfiguration(configuration);
	}

	@After
	public void tearDown() {
		mockery.assertIsSatisfied();
	}

	/**
	 * Test failed constructor due to null fileTransferConnectionTemplate.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void constructor_fails_when_connectionFactory_is_null() {
		new FtpProducer(null);
	}

	/**
	 * Test successful invocation based on a single file.
	 * 
	 * @throws EndpointException
	 *             if error invoking the endpoint
	 */
	@Test
	public void producer_succeeds_when_receive_single_file() throws ResourceException {
		// set up
		ReflectionTestUtils.setField(ftpProducer, "activeFileTransferConnectionTemplate", activeFileTransferConnectionTemplate);

		final String content = "content";
		final String fileName = "fileName";
		final String outputDirectory = "outputDirectory";
		final boolean overwrite = true;
		final String renameExtension = "renameExtension";
		final boolean checksumDelivered = false;
		final boolean unzip = false;
		final boolean createParentDirectory = true;
		final String tempFileName = "tempFileName";

		// expectations
		mockery.checking(new Expectations() {
			{
				oneOf(payload).getContent();
				will(returnValue(content.getBytes()));
				oneOf(payload).getAttribute(FilePayloadAttributeNames.FILE_NAME);
				will(returnValue(fileName));
				oneOf(configuration).getOutputDirectory();
				will(returnValue(outputDirectory));
				oneOf(configuration).getOverwrite();
				will(returnValue(overwrite));
				oneOf(configuration).getRenameExtension();
				will(returnValue(renameExtension));
				oneOf(configuration).getChecksumDelivered();
				will(returnValue(checksumDelivered));
				oneOf(configuration).getUnzip();
				will(returnValue(unzip));
				oneOf(configuration).getCreateParentDirectory();
				will(returnValue(createParentDirectory));
				oneOf(configuration).getTempFileName();
				will(returnValue(tempFileName));
				oneOf(activeFileTransferConnectionTemplate).deliverInputStream(
						with(any(ByteArrayInputStream.class)),
						with(equal(fileName)),
						with(equal(outputDirectory)),
						with(equal(overwrite)),
						with(equal(renameExtension)),
						with(equal(checksumDelivered)),
						with(equal(unzip)),
						with(equal(createParentDirectory)),
						with(equal(tempFileName)));
			}
		});

		// execute
		ftpProducer.invoke(payload);
	}

	/**
	 * When the producer is configured with an alternate connection template, on
	 * failure, producer will switch to use the alternate next time it is invoked.
	 * 
	 * @throws EndpointException
	 *             if error invoking endpoint
	 */
	@Test(expected = EndpointException.class)
	public void producer_fails_changes_to_alternate_connection_template() throws ResourceException {
		// set up
		ReflectionTestUtils.setField(ftpProducer, "fileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
		ReflectionTestUtils.setField(ftpProducer, "activeFileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
		ReflectionTestUtils.setField(ftpProducer, "alternateFileTransferConnectionTemplate", alternateFileTransferConnectionTemplate);

		final String content = "content";
		final String fileName = "fileName";
		final String outputDirectory = "outputDirectory";
		final boolean overwrite = true;
		final String renameExtension = "renameExtension";
		final boolean checksumDelivered = false;
		final boolean unzip = false;
		final boolean createParentDirectory = true;
		final String tempFileName = "tempFileName";

		final Payload payload = mockery.mock(Payload.class, "mockPayload");
		final ResourceException exception = new ResourceException(new RuntimeException("Something gone wrong"));

		// expectations
		mockery.checking(new Expectations() {
			{
				oneOf(payload).getContent();
				will(returnValue(content.getBytes()));
				oneOf(payload).getAttribute(FilePayloadAttributeNames.FILE_NAME);
				will(returnValue(fileName));
				oneOf(configuration).getOutputDirectory();
				will(returnValue(outputDirectory));
				oneOf(configuration).getOverwrite();
				will(returnValue(overwrite));
				oneOf(configuration).getRenameExtension();
				will(returnValue(renameExtension));
				oneOf(configuration).getChecksumDelivered();
				will(returnValue(checksumDelivered));
				oneOf(configuration).getUnzip();
				will(returnValue(unzip));
				oneOf(configuration).getCreateParentDirectory();
				will(returnValue(createParentDirectory));
				oneOf(configuration).getTempFileName();
				will(returnValue(tempFileName));
				oneOf(activeFileTransferConnectionTemplate).deliverInputStream(
						with(any(ByteArrayInputStream.class)),
						with(equal(fileName)),
						with(equal(outputDirectory)),
						with(equal(overwrite)),
						with(equal(renameExtension)),
						with(equal(checksumDelivered)),
						with(equal(unzip)),
						with(equal(createParentDirectory)),
						with(equal(tempFileName)));
				will(throwException(exception));
			}
		});

		// execute
		try {
			ftpProducer.invoke(payload);
		} catch (EndpointException e) {
			Assert.assertEquals(alternateFileTransferConnectionTemplate,
					ReflectionTestUtils.getField(ftpProducer, "activeFileTransferConnectionTemplate"));
			throw e;
		}

		Assert.fail("Unreachable code.");
	}

	/**
	 * If an error occurs while producer using alternative connection template,
	 * switch back to original connection template
	 * 
	 * @throws EndpointException
	 *             if error invoking endpoint
	 */
	@Test(expected = EndpointException.class)
	public void producer_fails_changes_to_original_connection_template() throws ResourceException {
		// set up
		ReflectionTestUtils.setField(ftpProducer, "fileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
		ReflectionTestUtils.setField(ftpProducer, "activeFileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
		ReflectionTestUtils.setField(ftpProducer, "alternateFileTransferConnectionTemplate", alternateFileTransferConnectionTemplate);

		final String content = "content";
		final String fileName = "fileName";
		final String outputDirectory = "outputDirectory";
		final boolean overwrite = true;
		final String renameExtension = "renameExtension";
		final boolean checksumDelivered = false;
		final boolean unzip = false;
		final boolean createParentDirectory = true;
		final String tempFileName = "tempFileName";

		final Payload payload = mockery.mock(Payload.class, "mockPayload");
		final ResourceException exception = new ResourceException(new RuntimeException("Something gone wrong"));

		// expectations
		mockery.checking(new Expectations() {
			{
				exactly(2).of(payload).getContent();
				will(returnValue(content.getBytes()));
				exactly(2).of(payload).getAttribute(FilePayloadAttributeNames.FILE_NAME);
				will(returnValue(fileName));
				exactly(2).of(configuration).getOutputDirectory();
				will(returnValue(outputDirectory));
				exactly(2).of(configuration).getOverwrite();
				will(returnValue(overwrite));
				exactly(2).of(configuration).getRenameExtension();
				will(returnValue(renameExtension));
				exactly(2).of(configuration).getChecksumDelivered();
				will(returnValue(checksumDelivered));
				exactly(2).of(configuration).getUnzip();
				will(returnValue(unzip));
				exactly(2).of(configuration).getCreateParentDirectory();
				will(returnValue(createParentDirectory));
				exactly(2).of(configuration).getTempFileName();
				will(returnValue(tempFileName));
				oneOf(activeFileTransferConnectionTemplate).deliverInputStream(
						with(any(ByteArrayInputStream.class)),
						with(equal(fileName)),
						with(equal(outputDirectory)),
						with(equal(overwrite)),
						with(equal(renameExtension)),
						with(equal(checksumDelivered)),
						with(equal(unzip)),
						with(equal(createParentDirectory)),
						with(equal(tempFileName)));
				will(throwException(exception));
				oneOf(alternateFileTransferConnectionTemplate).deliverInputStream(
						with(any(ByteArrayInputStream.class)),
						with(equal(fileName)),
						with(equal(outputDirectory)),
						with(equal(overwrite)),
						with(equal(renameExtension)),
						with(equal(checksumDelivered)),
						with(equal(unzip)),
						with(equal(createParentDirectory)),
						with(equal(tempFileName)));
				will(throwException(exception));
			}
		});

		// execute
		try {
			ftpProducer.invoke(payload);
		} catch (Exception e) {
			Assert.assertEquals(alternateFileTransferConnectionTemplate,
					ReflectionTestUtils.getField(ftpProducer, "activeFileTransferConnectionTemplate"));
			try {
				ftpProducer.invoke(payload);
			} catch (Exception e2) {
				Assert.assertEquals(activeFileTransferConnectionTemplate,
						ReflectionTestUtils.getField(ftpProducer, "activeFileTransferConnectionTemplate"));
				throw e2;
			}
		}

		Assert.fail("Unreachable code.");
	}

}
