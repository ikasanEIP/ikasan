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
package org.ikasan.connector.basefiletransfer.outbound.command;

import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import junit.framework.TestCase;

import org.ikasan.connector.base.command.AbstractTransactionalResourceCommand;
import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.base.command.XidImpl;
import org.ikasan.connector.basefiletransfer.net.ClientCommandGetException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandLsException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.net.FileTransferClient;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.util.chunking.model.FileChunk;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;
import org.ikasan.connector.util.chunking.model.FileConstituentHandle;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.lib.legacy.ClassImposteriser;

/**
 * Test class for the ChunkingRetrieveFileCommand
 * 
 * @author Ikasan Development Team
 */
public class ChunkingRetrieveFileCommandTest extends TestCase
{

    /**
     * Tests just the execute method of the RetrieveFileCommand
     * 
     * @throws ResourceException Exception thrown by connector
     * @throws ClientCommandGetException Exception if we can't get the file
     * @throws URISyntaxException Exception if the URI is invalid
     * @throws ClientCommandLsException Exception if we can't list a a directory
     */
    public void testExecute() throws ResourceException, ClientCommandGetException, 
        URISyntaxException, ClientCommandLsException
    {
        Mockery context = new Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        Mockery interfaceMockery = new Mockery();
        // mock the client
        final FileTransferClient client = context.mock(FileTransferClient.class);
        final String sourceDir = "srcDir";
        final String fileName = "fileName";
        final ClientListEntry entry = BaseFileTransferCommandJUnitHelper.createEntry(sourceDir + "/" + fileName);

        final List<ClientListEntry> entryList = new ArrayList<ClientListEntry>();
        entryList.add(entry);

        final Sequence sequence = context.sequence("sequence");

        context.checking(new Expectations()
        {
            {
                // calls ls first
                String path = sourceDir + "/" + fileName;
                one(client).ls(path);
                inSequence(sequence);
                will(returnValue(entryList));

                // then get with the outputstream
                one(client).get(with(any(String.class)), (with(any(OutputStream.class))));
                inSequence(sequence);

            }
        });
        // mock the dao
        final BaseFileTransferDao dao = interfaceMockery.mock(BaseFileTransferDao.class);
        interfaceMockery.checking(new Expectations()
        {
            {
                one(dao).persistClientListEntry(entry);
            }
        });

        // mock the file chunk dao
        final FileChunkDao fileChunkDao = new MockFileChunkDao();

        // create the command
        ChunkingRetrieveFileCommand command = new ChunkingRetrieveFileCommand(dao, null, true,".sent", false, null, fileChunkDao,
            (1024 * 1024), false);

        assertEquals("command state should be initialised", AbstractTransactionalResourceCommand.INITIALISED_STATE
            .getName(), command.getState());
        // create the ExecutionContext
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put(ExecutionContext.RETRIEVABLE_FILE_PARAM, entry);

        command.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(command, 3));

        command.setExecutionContext(executionContext);

        // execute the command
        ExecutionOutput output = command.execute(client, new XidImpl(new byte[0], new byte[0], 0));
        Object result = output.getResult();
        assertNotNull("result should not be null", result);
        assertTrue("result should be file a FileChunkHeader", result instanceof FileChunkHeader);
        assertEquals("command state should be executed", AbstractTransactionalResourceCommand.EXECUTED_STATE.getName(),
            command.getState());
    }

    /**
     * Simple mock
     * 
     * @author Ikasan Development Team
     */
    class MockFileChunkDao implements FileChunkDao
    {

        public List<FileConstituentHandle> findChunks(String fileName, Long fileChunkTimeStamp, Long noOfChunks,
                Long maxAge)
        {
            return new ArrayList<FileConstituentHandle>();
        }

        public FileChunk load(FileConstituentHandle fileConstituentHandle)
        {
            return null;
        }

        public FileChunkHeader load(Long id)
        {
            return null;
        }

        public void save(FileChunk fileChunk)
        {
            // Auto-generated method stub
        }

        public void save(FileChunkHeader fileChunkHeader)
        {
            // Auto-generated method stub
        }

        public void delete(FileChunkHeader fileChunkHeader)
        {
            // Auto-generated method stub
        }

    }
}
