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
