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
import javax.transaction.xa.Xid;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.springframework.beans.factory.BeanFactory;

import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.TransactionalResource;
import org.ikasan.connector.base.command.XidImpl;
import org.ikasan.connector.base.journal.TransactionJournal;
import org.ikasan.connector.base.journal.TransactionJournalingException;
import org.ikasan.connector.basefiletransfer.net.FileTransferClient;
import org.ikasan.connector.basefiletransfer.outbound.command.CleanupChunksCommand;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;

/**
 * Test class for the CleanupChunksCommand
 * 
 * @author Ikasan Development Team 
 */
public class CleanupChunksCommandTest extends TestCase
{
    /**
     * Test that when the commit method is called, it in turn calls the 
     * delete method on the dao with the fileChunkHeader set during the 
     * execute method
     * 
     * @throws ResourceException
     * @throws TransactionJournalingException 
     */
    public void testExecuteAndCommit() throws ResourceException, TransactionJournalingException{
        
        Mockery interfaceMockery = new Mockery();
        
        final FileChunkHeader fileChunkHeader = new FileChunkHeader(null, null, null, null);
        
        Mockery classMockery = new Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        
        
        // mock the dao
        final FileChunkDao dao = interfaceMockery.mock(FileChunkDao.class);
        final TransactionJournal transactionJournal = interfaceMockery.mock(TransactionJournal.class);
        final BeanFactory beanFactory = interfaceMockery.mock(BeanFactory.class);
        
        final TransactionalResource transactionalResource = classMockery.mock(FileTransferClient.class);
        
        final CleanupChunksCommand cleanupChunksCommand = new CleanupChunksCommand();
        
        final XidImpl xidImpl = new XidImpl(new byte[0], new byte[0], 0);
        
        interfaceMockery.checking(new Expectations()
        
        { 
            {
                one(beanFactory).getBean(with(same("fileChunkDao")));
                will(returnValue(dao));
                one(dao).delete(fileChunkHeader);
                allowing(transactionJournal).notifyUpdate(cleanupChunksCommand);
                allowing(transactionJournal).resolveXid((Xid) with(a(Xid.class)));will(returnValue(xidImpl));
            }
        });
        
        //set the prerequisite transaction journal, though we arent really interested in this here
        cleanupChunksCommand.setTransactionJournal(transactionJournal);
        cleanupChunksCommand.setBeanFactory(beanFactory);
        
        //set the execution context
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put(ExecutionContext.FILE_CHUNK_HEADER, fileChunkHeader);
        cleanupChunksCommand.setExecutionContext(executionContext);
        
        cleanupChunksCommand.execute(transactionalResource, new XidImpl(new byte[0], new byte[0], 0));
        
        cleanupChunksCommand.commit();
    }
}
