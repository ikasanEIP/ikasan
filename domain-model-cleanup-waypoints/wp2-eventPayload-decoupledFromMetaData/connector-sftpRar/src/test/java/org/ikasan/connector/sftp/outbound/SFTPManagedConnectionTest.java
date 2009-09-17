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
package org.ikasan.connector.sftp.outbound;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import org.ikasan.connector.base.command.TransactionalCommandConnection;
import org.ikasan.connector.base.journal.TransactionJournal;
import org.ikasan.connector.base.journal.TransactionJournalingException;

import junit.framework.TestCase;

/**
 * Test class for the <code>SFTPManagedConnection</code>
 * 
 * @author Ikasan Development Team
 * 
 */
public class SFTPManagedConnectionTest extends TestCase
{

    /**
     * Tests the function of the recover method. When called with the
     * TMSTARTRSCAN flag, it should return any executed, but uncommitted
     * transactions it finds in the TransactionJournal
     * 
     * @throws XAException
     * @throws TransactionJournalingException
     */
    public void testRecover() throws XAException, TransactionJournalingException
    {

        Mockery interfaceMockery = new Mockery();
        final TransactionJournal transactionJournal = interfaceMockery.mock(TransactionJournal.class);
        final Xid[] xids = new Xid[1];

        // mock the TransactionJournal
        interfaceMockery.checking(new Expectations()
        {
            {
                one(transactionJournal).getExecutedTransactions();
                // transaction journal will return a know set of Xids
                will(returnValue(xids));
            }
        });

        // setup the managed connection
        TransactionalCommandConnection managedConnection = new SFTPManagedConnection(
            getMockedManagedConnectionFactory(), getMockedConnectionRequestInfo());
        managedConnection.setTransactionJournal(transactionJournal);

        // execute the recover method
        Xid[] executedTransactions = managedConnection.recover(XAResource.TMSTARTRSCAN);

        // check the results
        assertEquals("recover method should return all the executed Xids returned from the transaction journal", xids, //$NON-NLS-1$
            executedTransactions);
    }

    /**
     * Tests the function of the recover method. Should throw an appropriate
     * XAException if the TransactionJournal itself fell over
     * 
     * @throws TransactionJournalingException
     */
    public void testRecover_handlesJournalingException() throws TransactionJournalingException
    {

        Mockery interfaceMockery = new Mockery();
        final TransactionJournal transactionJournal = interfaceMockery.mock(TransactionJournal.class);
        final TransactionJournalingException journalingException = new TransactionJournalingException(
            "A journaling exception", null); //$NON-NLS-1$

        // mock the TransactionJournal
        interfaceMockery.checking(new Expectations()
        {
            {
                one(transactionJournal).getExecutedTransactions();
                will(throwException(journalingException));
            }
        });

        // setup the managed connection
        TransactionalCommandConnection managedConnection = new SFTPManagedConnection(
            getMockedManagedConnectionFactory(), getMockedConnectionRequestInfo());
        managedConnection.setTransactionJournal(transactionJournal);

        // execute the recover method
        try
        {
            managedConnection.recover(XAResource.TMSTARTRSCAN);
            fail("exception should have been thrown"); //$NON-NLS-1$
        }
        catch (XAException e)
        {
            assertTrue(
                "As XAException cannot wrap other Throwables, message should end with message of underlying exception", //$NON-NLS-1$
                e.getMessage().endsWith(journalingException.getMessage()));
        }
    }

    /**
     * Tests the function of the recover method when an invalid flag is passed
     */
    public void testRecover_handlesInvalidFlag()
    {

        TransactionalCommandConnection managedConnection = new SFTPManagedConnection(
            getMockedManagedConnectionFactory(), getMockedConnectionRequestInfo());

        int invalidFlag = 99;
        boolean xaExceptionFound = false;
        try
        {
            managedConnection.recover(invalidFlag);
            fail("exception should have been thrown"); //$NON-NLS-1$
        }
        catch (XAException e)
        {
            xaExceptionFound = true;
        }

        assertTrue("XAException should be thrown when an invalid flag is passed to the recover method", //$NON-NLS-1$
            xaExceptionFound);
    }

    /**
     * Tests the function of the recover method when the timer end scan flag is
     * passed
     * 
     * TODO: What else should the ManagedConnection be doing on this method call?
     * 
     * @throws XAException
     */
    public void testRecover_WithTimerEndScanFlag() throws XAException
    {
        TransactionalCommandConnection managedConnection = new SFTPManagedConnection(
            getMockedManagedConnectionFactory(), getMockedConnectionRequestInfo());
        int flag = XAResource.TMENDRSCAN;
        Xid[] xids = managedConnection.recover(flag);

        assertEquals("No Xids should be returned when TimerEndScan flag is passed to recover", 0, xids.length); //$NON-NLS-1$
    }

    /**
     * Simply mocks the SFTPManagedConnectioFactory
     * 
     * @return SFTPManagedConnectionFactory
     */
    private SFTPManagedConnectionFactory getMockedManagedConnectionFactory()
    {
        // Mock the
        Mockery classMockery = new Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };

        final SFTPManagedConnectionFactory managedConnectionFactory = classMockery
            .mock(SFTPManagedConnectionFactory.class);
//        classMockery.checking(new Expectations()
//        {
//            {
//                one(managedConnectionFactory).getClientID();// dont care what
//                                                            // this returns
//            }
//        });
        return managedConnectionFactory;
    }

    private SFTPConnectionRequestInfo getMockedConnectionRequestInfo()
    {
        // Mock the
        Mockery classMockery = new Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };

        final SFTPConnectionRequestInfo connectionRequestInfo = classMockery
            .mock(SFTPConnectionRequestInfo.class);
        classMockery.checking(new Expectations()

        {
            {
                one(connectionRequestInfo).getClientID();// dont care what
                                                            // this returns
            }
        });
        return connectionRequestInfo;
    }
}
