/*
 * $Id: TransactionJournalImplTest.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/test/java/org/ikasan/connector/base/journal/TransactionJournalImplTest.java $
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
package org.ikasan.connector.base.journal;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.xa.Xid;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

import org.ikasan.connector.base.command.TransactionalResourceCommand;
import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.base.command.TransactionalResourceCommandPersistenceException;
import org.ikasan.connector.base.command.XidImpl;

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
        
        final BeanFactory beanFactory = interfaceMockery.mock(BeanFactory.class);
        
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
