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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.resource.ResourceException;

import junit.framework.TestCase;

import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.base.command.XidImpl;
import org.ikasan.connector.base.journal.TransactionJournal;
import org.ikasan.connector.basefiletransfer.net.ClientCommandCdException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandLsException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.net.FileTransferClient;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

/**
 * Test class for FileDiscoveryCommand
 * 
 * @author Ikasan Development Team
 */
public class FileDiscoveryCommandTest extends TestCase
{
    /**
     * Tests the execute method
     * 
     * @throws ResourceException Exception thrown by Connector
     * @throws ClientCommandCdException Exception if we can't change directory
     * @throws ClientCommandLsException Exception if we can't list directory
     * @throws URISyntaxException Exception if the URI is invalid
     */
    public void testExecute() throws ResourceException,
            ClientCommandCdException, ClientCommandLsException,
            URISyntaxException
    {
        Mockery clientMockery = new Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };

        // mock the client
        final FileTransferClient client = clientMockery.mock(FileTransferClient.class);
        final String sourceDir = "srcDir";
        final String filenamePattern = "[a-z].txt";
        final List<ClientListEntry> fileList = new ArrayList<ClientListEntry>();
        final ClientListEntry fileToIgnore = BaseFileTransferCommandJUnitHelper.createEntry("blah");
        fileList.add(fileToIgnore);
        final ClientListEntry youngFileToIgnore = BaseFileTransferCommandJUnitHelper.createEntry("b.txt", new Date());
        fileList.add(youngFileToIgnore);
        final ClientListEntry fileToDiscover = BaseFileTransferCommandJUnitHelper.createEntry("a.txt");
        fileList.add(fileToDiscover);

        clientMockery.checking(new Expectations()
        {
            {
                one(client).ensureConnection();
                one(client).cd(sourceDir);
                one(client).ls(sourceDir);
                will(returnValue(fileList));
            }
        });
        // mock the dao
        Mockery daoMockery = new Mockery();
        final BaseFileTransferDao dao = daoMockery.mock(BaseFileTransferDao.class);
        // dao expectations expectations
        daoMockery.checking(new Expectations()
        {
            {
                one(dao).isDuplicate(fileToDiscover, true, true);
                will(returnValue(false));
            }
        });
        
        final FileDiscoveryCommand command = new FileDiscoveryCommand(sourceDir,
            filenamePattern, dao, 120, true, true, true);
        
        final TransactionJournal transactionJournal = BaseFileTransferCommandJUnitHelper.getTransactionJournal(command, 3);
        command.setTransactionJournal(transactionJournal);
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put(ExecutionContext.CLIENT_ID, "clientId");
        
        command.setExecutionContext(executionContext);
        
        ExecutionOutput  output = command.execute(client, new XidImpl(new byte[0], new byte[0], 0));
        List<?> result = output.getResultList();
        assertNotNull("command result should not be null", result); //$NON-NLS-1$
        assertNotNull("command result should only contain one entry", result //$NON-NLS-1$
            .size() == 1);
        assertEquals(
            "command result's only entry should be the file that matches pattern", //$NON-NLS-1$
            fileToDiscover, result.get(0));
    }
}
