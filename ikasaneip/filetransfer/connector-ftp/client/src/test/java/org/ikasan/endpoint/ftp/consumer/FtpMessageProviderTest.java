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
package org.ikasan.endpoint.ftp.consumer;


import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.endpoint.ftp.util.FileBasedPasswordHelper;
import org.ikasan.filetransfer.Payload;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobExecutionContext;
import org.springframework.test.util.ReflectionTestUtils;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;

/**
 * Test class for {@link org.ikasan.endpoint.ftp.consumer.FtpMessageProvider}
 *
 * @author Ikasan Development Team
 */
public class FtpMessageProviderTest {

    private FtpMessageProvider uut;
    /**
     * Mockery for mocking concrete classes
     */
    final private Mockery mockery = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };


    private final ConnectionFactory connectionFactory = mockery.mock(ConnectionFactory.class, "mockConnectionFactory");

    private final FileBasedPasswordHelper fileBasedPasswordHelper = mockery.mock(FileBasedPasswordHelper.class, "mockFileBasedPasswordHelper");

    private final JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class);

    private final FtpConsumerConfiguration configuration = mockery.mock(FtpConsumerConfiguration.class);

    private final FileTransferConnectionTemplate activeFileTransferConnectionTemplate = mockery.mock(FileTransferConnectionTemplate.class,"mockactiveFileTransferConnectionTemplate");

    private final FileTransferConnectionTemplate fileTransferConnectionTemplate = mockery.mock(FileTransferConnectionTemplate.class,"mockfileTransferConnectionTemplate");


    @Before
    public void setup() {
        uut = new FtpMessageProvider(connectionFactory, fileBasedPasswordHelper);
        uut.setConfiguration(configuration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failed_constructor_when_both_arg_are_null() {
        new FtpMessageProvider(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failed_constructor_when_fileBasedPasswordHelper_is_null() {
        new FtpMessageProvider(connectionFactory, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failed_constructor_when_connectionFactory_is_null() {
        new FtpMessageProvider(null, fileBasedPasswordHelper);
    }

    @Test
    public void invoke_when_activeFileTransferConnectionTemplate_returns_payload() throws ResourceException {

        ReflectionTestUtils.setField(uut,"activeFileTransferConnectionTemplate" ,activeFileTransferConnectionTemplate);
        final String directory = "directory";
        final String filenamePattern = "filenamePattern";
        final String moveOnSuccessPath = "moveOnSuccessPath";
        final String renameOnSuccessExtension = ".tmp";
        final Payload payload = mockery.mock(Payload.class);
        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(configuration).getSourceDirectoryURLFactory();
                will(returnValue(null));
                exactly(1).of(configuration).getSourceDirectory();
                will(returnValue(directory));
                exactly(1).of(configuration).getFilenamePattern();
                will(returnValue(filenamePattern));
                exactly(1).of(configuration).getRenameOnSuccess();
                will(returnValue(true));
                exactly(1).of(configuration).getRenameOnSuccessExtension();
                will(returnValue(renameOnSuccessExtension));
                exactly(1).of(configuration).getMoveOnSuccess();
                will(returnValue(false));
                exactly(1).of(configuration).getMoveOnSuccessNewPath();
                will(returnValue(moveOnSuccessPath));
                exactly(1).of(configuration).getChunking();
                will(returnValue(false));
                exactly(1).of(configuration).getChunkSize();
                will(returnValue(10));
                exactly(1).of(configuration).getChecksum();
                will(returnValue(false));
                exactly(1).of(configuration).getMinAge();
                will(returnValue(10l));
                exactly(1).of(configuration).getDestructive();
                will(returnValue(false));
                exactly(1).of(configuration).getFilterDuplicates();
                will(returnValue(false));
                exactly(1).of(configuration).getFilterOnFilename();
                will(returnValue(false));
                exactly(1).of(configuration).getFilterOnLastModifiedDate();
                will(returnValue(false));
                exactly(1).of(configuration).getChronological();
                will(returnValue(false));

                exactly(1).of(activeFileTransferConnectionTemplate).getDiscoveredFile("directory", "filenamePattern", true
                        , ".tmp", false, "moveOnSuccessPath", false, 10, false, 10L, false, false, false, false, false);
                will(returnValue(payload));

            }
        });

        uut.invoke(jobExecutionContext);
        mockery.assertIsSatisfied();
    }

    @Test
    public void invoke_when_activeFileTransferConnectionTemplate_returns_null() throws ResourceException {

        ReflectionTestUtils.setField(uut,"activeFileTransferConnectionTemplate" ,activeFileTransferConnectionTemplate);
        ReflectionTestUtils.setField(uut,"fileTransferConnectionTemplate" ,fileTransferConnectionTemplate);

        final String directory = "directory";
        final String filenamePattern = "filenamePattern";
        final String moveOnSuccessPath = "moveOnSuccessPath";
        final String renameOnSuccessExtension = ".tmp";
        final int maxRow = 10;
        final int ageOfFiles = 10;
        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(configuration).getSourceDirectoryURLFactory();
                will(returnValue(null));
                exactly(1).of(configuration).getSourceDirectory();
                will(returnValue(directory));
                exactly(1).of(configuration).getFilenamePattern();
                will(returnValue(filenamePattern));
                exactly(1).of(configuration).getRenameOnSuccess();
                will(returnValue(true));
                exactly(1).of(configuration).getRenameOnSuccessExtension();
                will(returnValue(renameOnSuccessExtension));
                exactly(1).of(configuration).getMoveOnSuccess();
                will(returnValue(false));
                exactly(1).of(configuration).getMoveOnSuccessNewPath();
                will(returnValue(moveOnSuccessPath));
                exactly(1).of(configuration).getChunking();
                will(returnValue(false));
                exactly(1).of(configuration).getChunkSize();
                will(returnValue(10));
                exactly(1).of(configuration).getChecksum();
                will(returnValue(false));
                exactly(1).of(configuration).getMinAge();
                will(returnValue(10l));
                exactly(1).of(configuration).getDestructive();
                will(returnValue(false));
                exactly(1).of(configuration).getFilterDuplicates();
                will(returnValue(false));
                exactly(1).of(configuration).getFilterOnFilename();
                will(returnValue(false));
                exactly(1).of(configuration).getFilterOnLastModifiedDate();
                will(returnValue(false));
                exactly(1).of(configuration).getChronological();
                will(returnValue(false));

                exactly(1).of(activeFileTransferConnectionTemplate).getDiscoveredFile("directory", "filenamePattern", true
                        , ".tmp", false, "moveOnSuccessPath", false, 10, false, 10L, false, false, false, false, false);
                will(returnValue(null));

                exactly(1).of(configuration).getMaxRows();
                will(returnValue(maxRow));
                exactly(1).of(configuration).getAgeOfFiles();
                will(returnValue(ageOfFiles));

                exactly(1).of(fileTransferConnectionTemplate).housekeep(maxRow,ageOfFiles);

            }
        });

        uut.invoke(jobExecutionContext);
        mockery.assertIsSatisfied();
    }

}
