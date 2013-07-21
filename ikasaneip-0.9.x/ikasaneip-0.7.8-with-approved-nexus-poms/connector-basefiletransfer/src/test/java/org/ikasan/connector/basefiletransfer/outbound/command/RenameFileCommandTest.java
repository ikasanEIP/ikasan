/*
 * $Id$
 * $URL$
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
