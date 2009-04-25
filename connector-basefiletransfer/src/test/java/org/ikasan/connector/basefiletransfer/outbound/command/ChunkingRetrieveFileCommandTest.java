/*
 * $Id: ChunkingRetrieveFileCommandTest.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/test/java/org/ikasan/connector/basefiletransfer/outbound/command/ChunkingRetrieveFileCommandTest.java $
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
package org.ikasan.connector.basefiletransfer.outbound.command;

import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;
import junit.framework.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.lib.legacy.ClassImposteriser;

import org.ikasan.connector.base.command.AbstractTransactionalResourceCommand;
import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.base.command.XidImpl;
import org.ikasan.connector.basefiletransfer.net.ClientCommandGetException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandLsException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.net.FileTransferClient;
import org.ikasan.connector.basefiletransfer.outbound.command.ChunkingRetrieveFileCommand;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.util.chunking.model.FileChunk;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;
import org.ikasan.connector.util.chunking.model.FileConstituentHandle;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;

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
     * @author duncro
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
