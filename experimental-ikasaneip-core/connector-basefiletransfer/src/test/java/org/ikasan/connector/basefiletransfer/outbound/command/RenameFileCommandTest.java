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

import javax.resource.ResourceException;

import junit.framework.TestCase;

import org.ikasan.connector.base.command.AbstractTransactionalResourceCommand;
import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.XidImpl;
import org.ikasan.connector.basefiletransfer.net.ClientCommandRenameException;
import org.ikasan.connector.basefiletransfer.net.FileTransferClient;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

/**
 * Test class for the RenameFileCommand
 * 
 * @author Ikasan Development Team
 */
public class RenameFileCommandTest extends TestCase
{
    /**
     * tests the execute method
     * 
     * @throws ResourceException
     * @throws ClientCommandRenameException
     */
    public void testExecute() throws ResourceException,
            ClientCommandRenameException
    {
        Mockery clientMockery = new Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };

        // mock the client
        final FileTransferClient client = clientMockery.mock(FileTransferClient.class);
        final String newPath = "newDir/newFile.new";
        final String oldPath = "oldDir/oldFile.old";

        clientMockery.checking(new Expectations()
        {
            {
                one(client).ensureConnection();
                one(client).rename(oldPath, newPath);
            }
        });
        
        final RenameFileCommand command = new RenameFileCommand();

        command.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(command, 3));
        
        setupExecutionContext(newPath, oldPath, command);
        
        // Execute the cmd
        command.execute(client, new XidImpl(new byte[0], new byte[0], 0));

        assertTrue("command state should be executed", //$NON-NLS-1$
            AbstractTransactionalResourceCommand.EXECUTED_STATE.getName().equals(command
                .getState()));
    }

    /**
     * @param newPath
     * @param oldPath
     * @param command
     */
    private void setupExecutionContext(final String newPath,
            final String oldPath, final RenameFileCommand command)
    {
        ExecutionContext executionContext = new ExecutionContext();
        
        executionContext.put(ExecutionContext.RENAMABLE_FILE_PATH_PARAM, oldPath);
        executionContext.put(ExecutionContext.NEW_FILE_PATH_PARAM, newPath);
        
        command.setExecutionContext(executionContext);
    }

    /**
     * tests the rollback method
     * 
     * @throws ResourceException
     * @throws ClientCommandRenameException
     */
    public void testRollback() throws ResourceException,
            ClientCommandRenameException
    {
        Mockery clientMockery = new Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        // mock the client
        final FileTransferClient client = clientMockery.mock(FileTransferClient.class);
        final String newPath = "newDir/newFile.new";
        final String oldPath = "oldDir/oldFile.old";

        clientMockery.checking(new Expectations()
        {
            {
                one(client).ensureConnection();
                one(client).rename(newPath, oldPath);
                one(client).ensureConnection();
                one(client).rename(oldPath, newPath);
            }
        });
        RenameFileCommand command = new RenameFileCommand();
        
        command.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(command, 5));
        
        setupExecutionContext(newPath, oldPath, command);
        
        assertEquals("command state should be initialised", //$NON-NLS-1$
            AbstractTransactionalResourceCommand.INITIALISED_STATE.getName(), command
                .getState());
        
        // Execute the cmd
        command.execute(client, new XidImpl(new byte[0], new byte[0], 0));
        
        assertEquals("command state should be executed", //$NON-NLS-1$
            AbstractTransactionalResourceCommand.EXECUTED_STATE.getName(), command
                .getState());
        
        command.rollback();
        
        assertEquals("command state should be rolled_back_executed", //$NON-NLS-1$
            AbstractTransactionalResourceCommand.ROLLED_BACK_STATE.getName(),
            command.getState());
    }
}
