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
package org.ikasan.endpoint.ftp.producer;

import java.io.ByteArrayInputStream;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;

import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.endpoint.ftp.FileTransferConnectionTemplate;
import org.ikasan.endpoint.ftp.FtpResourceNotStartedException;
import org.ikasan.filetransfer.FilePayloadAttributeNames;
import org.ikasan.filetransfer.Payload;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.jta.JtaTransactionManager;

public class FtpProducerTest {

	private FtpProducer uut;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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

	private final JtaTransactionManager transactionManager = mockery.mock(JtaTransactionManager.class,"mocktransactionManager");
	private final BaseFileTransferDao baseFileTransferDao = mockery.mock(BaseFileTransferDao.class,"mockbaseFileTransferDao");
	//private final FileChunkDao fileChunkDao = mockery.mock(FileChunkDao.class,"mockFileChunkDao");
	private final TransactionalResourceCommandDAO transactionalResourceCommandDAO = mockery.mock(TransactionalResourceCommandDAO.class,"mocktransactionalResourceCommandDAO");

	@Before
	public void setup() {
		uut = new FtpProducer(transactionManager,baseFileTransferDao,null,transactionalResourceCommandDAO);
		uut.setConfiguration(configuration);
	}

	@After
	public void tearDown() {
		mockery.assertIsSatisfied();
	}


    @Test
    public void invoke_when_ftpTemplate_is_null() throws ResourceException {
        // set up
        ReflectionTestUtils.setField(uut, "activeFileTransferConnectionTemplate", null);

        thrown.expect(FtpResourceNotStartedException.class);

        // execute
        uut.invoke(payload);
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
		ReflectionTestUtils.setField(uut, "activeFileTransferConnectionTemplate", activeFileTransferConnectionTemplate);

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
		uut.invoke(payload);
	}

	/**
	 * When the producer is configured with an alternate connection template, on
	 * failure, producer will switch to use the alternate next time it is invoked.
	 * 
	 * @throws EndpointException
	 *             if error invoking endpoint
	 */
	@Test
	public void producer_fails_changes_to_alternate_connection_template() throws ResourceException {
		// set up
		ReflectionTestUtils.setField(uut, "fileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
		ReflectionTestUtils.setField(uut, "activeFileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
		ReflectionTestUtils.setField(uut, "alternateFileTransferConnectionTemplate", alternateFileTransferConnectionTemplate);

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
        thrown.expect(EndpointException.class);
       	uut.invoke(payload);

		Assert.fail("Unreachable code.");
	}

	/**
	 * If an error occurs while producer using alternative connection template,
	 * switch back to original connection template
	 * 
	 * @throws EndpointException
	 *             if error invoking endpoint
	 */
	@Test
	public void producer_fails_changes_to_original_connection_template() throws ResourceException {
		// set up
		ReflectionTestUtils.setField(uut, "fileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
		ReflectionTestUtils.setField(uut, "activeFileTransferConnectionTemplate", activeFileTransferConnectionTemplate);
		ReflectionTestUtils.setField(uut, "alternateFileTransferConnectionTemplate", alternateFileTransferConnectionTemplate);

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
        thrown.expect(EndpointException.class);

        try {
			uut.invoke(payload);
		} catch (Exception e) {
			Assert.assertEquals(alternateFileTransferConnectionTemplate,
					ReflectionTestUtils.getField(uut, "activeFileTransferConnectionTemplate"));
			try {
				uut.invoke(payload);
			} catch (Exception e2) {
				Assert.assertEquals(activeFileTransferConnectionTemplate,
						ReflectionTestUtils.getField(uut, "activeFileTransferConnectionTemplate"));
				throw e2;
			}
		}

		Assert.fail("Unreachable code.");
	}

}
