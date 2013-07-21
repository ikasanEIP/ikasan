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
package org.ikasan.connector.base.journal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.xa.Xid;

import junit.framework.TestCase;

import org.ikasan.connector.base.command.TransactionalResourceCommand;
import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.base.command.TransactionalResourceCommandPersistenceException;
import org.ikasan.connector.base.command.XidImpl;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * Test class for the basic TrasactionJournal
 * 
 * @author Ikasan Development Team
 */
public class TransactionJournalImplTest extends TestCase
{
    
    /**
     * Tests that the TransactionJournal saves the command whenever the notifyUpdate method is called
     * @throws TransactionalResourceCommandPersistenceException 
     * @throws TransactionJournalingException 
     */
    @Test
    public void testNotifyUpdate() throws TransactionalResourceCommandPersistenceException, TransactionJournalingException{
        Mockery interfaceMockery = new Mockery();
        final TransactionalResourceCommandDAO dao = interfaceMockery.mock(TransactionalResourceCommandDAO.class);
        final TransactionalResourceCommand command = interfaceMockery.mock(TransactionalResourceCommand.class);
        
        final Map<String,Object> beanFactory = interfaceMockery.mock(Map.class);
        
        interfaceMockery.checking(new Expectations()
        {
            {
                one(command).getState(); //really dont care what it returns
                one(dao).save(command); //the dao should receive a save call passing in the command
            }
        });
        
        TransactionJournalImpl journal = new TransactionJournalImpl(dao, null, beanFactory);
        
        //call the method
        journal.notifyUpdate(command);
        
    }
    
    /**
     * Tests that the TransactionJournal calls the appropriate dao method on GetExecutedTransactions
     * @throws TransactionalResourceCommandPersistenceException 
     * @throws TransactionJournalingException 
     */
    @Test
    public void testGetExecutedTransactions() throws TransactionalResourceCommandPersistenceException, TransactionJournalingException{

        final List<XidImpl> exeucutedTransactionXids = new ArrayList<XidImpl>();
        
        XidImpl firstExecutedTransaction = new XidImpl(new byte[]{1}, new byte[0], 0);
        XidImpl secondExecutedTransaction = new XidImpl(new byte[]{2}, new byte[0], 0);
        XidImpl thirdExecutedTransaction = new XidImpl(new byte[]{3}, new byte[0], 0);
        
        exeucutedTransactionXids.add(firstExecutedTransaction);
        exeucutedTransactionXids.add(secondExecutedTransaction);
        exeucutedTransactionXids.add(thirdExecutedTransaction);
        
        
        Mockery interfaceMockery = new Mockery();
        final TransactionalResourceCommandDAO dao = interfaceMockery.mock(TransactionalResourceCommandDAO.class);
               
        interfaceMockery.checking(new Expectations()
        {
            {
                one(dao).findXidbyState("prepare");
                will(returnValue(exeucutedTransactionXids));

            }
        });
//        
        TransactionJournalImpl journal = new TransactionJournalImpl(dao, null, null);
//        
//        //call the method
        Xid[] executedTransactions = journal.getExecutedTransactions();
        assertEquals("There should be 3 results", 3, executedTransactions.length);
        assertEquals("First result should be the first XId in the List returned from the dao", firstExecutedTransaction, executedTransactions[0]);
        assertEquals("Second result should be the second XId in the List returned from the dao", secondExecutedTransaction, executedTransactions[1]);
        assertEquals("Third result should be the third XId in the List returned from the dao", thirdExecutedTransaction, executedTransactions[2]);
        
    }
    
    /**
     * Tests that the TransactionJournal calls the appropriate dao method on GetExecutedTransactions
     * @throws TransactionalResourceCommandPersistenceException 
     */
    @Test
    public void testGetExecutedTransactions_handlesDaoException() throws TransactionalResourceCommandPersistenceException{
        Mockery interfaceMockery = new Mockery();
        final TransactionalResourceCommandDAO dao = interfaceMockery.mock(TransactionalResourceCommandDAO.class);
        
        final TransactionalResourceCommandPersistenceException daoException = new TransactionalResourceCommandPersistenceException("An exception", null);
   
        
        interfaceMockery.checking(new Expectations()
        {
            {

                //set up the expected method calls on the dao
                one(dao).findXidbyState("prepare"); 
                will(throwException(daoException));
            }
        });
        
        TransactionJournalImpl journal = new TransactionJournalImpl(dao,null, null);
        
        //call the method
        try{
            journal.getExecutedTransactions();
            fail("exception should have been thrown");
        }
        catch (TransactionJournalingException e){
            assertEquals("Underlying cause of Journling Exception should be dao exception", daoException, e.getCause());
        }
        
    }
}
