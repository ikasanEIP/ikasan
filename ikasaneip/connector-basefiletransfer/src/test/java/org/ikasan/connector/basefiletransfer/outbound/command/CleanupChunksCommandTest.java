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
import javax.transaction.xa.Xid;

import junit.framework.TestCase;

import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.TransactionalResource;
import org.ikasan.connector.base.command.XidImpl;
import org.ikasan.connector.base.journal.TransactionJournal;
import org.ikasan.connector.base.journal.TransactionJournalingException;
import org.ikasan.connector.basefiletransfer.net.FileTransferClient;
import org.ikasan.connector.util.chunking.model.FileChunkHeader;
import org.ikasan.connector.util.chunking.model.dao.FileChunkDao;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.springframework.beans.factory.BeanFactory;

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
