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
package org.ikasan.endpoint.sftp.consumer;



import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.endpoint.sftp.FileTransferConnectionTemplate;
import org.ikasan.endpoint.sftp.SftpResourceNotStartedException;
import org.ikasan.filetransfer.Payload;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.quartz.JobExecutionContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.resource.ResourceException;

/**
 * Test class for {@link SftpMessageProvider}
 *
 * @author Ikasan Development Team
 */
public class SftpMessageProviderTest
{

    private SftpMessageProvider uut;
    /**
     * Mockery for mocking concrete classes
     */
    final private Mockery mockery = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class);

    private final SftpConsumerConfiguration configuration = mockery.mock(SftpConsumerConfiguration.class);

    private final FileTransferConnectionTemplate activeFileTransferConnectionTemplate = mockery.mock(FileTransferConnectionTemplate.class,"mockactiveFileTransferConnectionTemplate");

    private final FileTransferConnectionTemplate fileTransferConnectionTemplate = mockery.mock(FileTransferConnectionTemplate.class,"mockfileTransferConnectionTemplate");

    private final JtaTransactionManager transactionManager = mockery.mock(JtaTransactionManager.class,"mocktransactionManager");
    private final BaseFileTransferDao baseFileTransferDao = mockery.mock(BaseFileTransferDao.class,"mockbaseFileTransferDao");
    //private final FileChunkDao fileChunkDao = mockery.mock(FileChunkDao.class,"mockFileChunkDao");
    private final TransactionalResourceCommandDAO transactionalResourceCommandDAO = mockery.mock(TransactionalResourceCommandDAO.class,"mocktransactionalResourceCommandDAO");


    @Before
    public void setup() {
        uut = new SftpMessageProvider(transactionManager,baseFileTransferDao,null,transactionalResourceCommandDAO);
        uut.setConfiguration(configuration);
    }

    @Test
    public void invoke_when_activeFileTransferConnectionTemplate_is_null() throws ResourceException {

        ReflectionTestUtils.setField(uut,"activeFileTransferConnectionTemplate" ,null);

        thrown.expect(SftpResourceNotStartedException.class);

        final String directory = "directory";

        // expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(configuration).getSourceDirectoryURLFactory();
                will(returnValue(null));
                exactly(1).of(configuration).getSourceDirectory();
                will(returnValue(directory));

            }
        });

        uut.invoke(jobExecutionContext);

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
                exactly(1).of(configuration).getIsRecursive();
                will(returnValue(false));

                exactly(1).of(activeFileTransferConnectionTemplate).getDiscoveredFile("directory", "filenamePattern", true
                        , ".tmp", false, "moveOnSuccessPath", false, 10, false, 10L, false, false, false, false, false,false);
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
                exactly(1).of(configuration).getIsRecursive();
                will(returnValue(false));

                exactly(1).of(activeFileTransferConnectionTemplate).getDiscoveredFile("directory", "filenamePattern", true
                        , ".tmp", false, "moveOnSuccessPath", false, 10, false, 10L, false, false, false, false, false,false);
                will(returnValue(null));

                exactly(1).of(configuration).getMaxRows();
                will(returnValue(maxRow));
                exactly(1).of(configuration).getAgeOfFiles();
                will(returnValue(ageOfFiles));

                exactly(1).of(activeFileTransferConnectionTemplate).housekeep(maxRow,ageOfFiles);

            }
        });

        uut.invoke(jobExecutionContext);
        mockery.assertIsSatisfied();
    }

}
