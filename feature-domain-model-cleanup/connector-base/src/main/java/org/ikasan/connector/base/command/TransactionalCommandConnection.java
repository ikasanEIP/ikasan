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
package org.ikasan.connector.base.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.resource.ResourceException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.log4j.Logger;
import org.ikasan.connector.base.journal.TransactionJournal;
import org.ikasan.connector.base.journal.TransactionJournalingException;
import org.ikasan.connector.base.outbound.xa.EISXALRCOManagedConnection;

/**
 * This class provides an implementation of the command pattern to encapsulate a
 * stateful transaction scoped history of events executed through a
 * transactional resource
 * 
 * Each executable interaction is provided as a
 * <code>TransactionalResourceCommand</code> which is maintained in a well
 * ordered list by this class, until such time as the transaction is committed,
 * and/or rolled back, at which time the commit or rollback invocation is
 * propagated to each of the commands in turn.
 * 
 * Hook pre/post commit and rollback methods are provided, and may be overridden
 * 
 * @author Ikasan Development Team
 * 
 */
public abstract class TransactionalCommandConnection extends EISXALRCOManagedConnection
{

    /** The logger instance. */
    private static Logger logger = Logger.getLogger(TransactionalCommandConnection.class);

    /** A list of method calls that the command has gone through */
    private List<String> methodCalls = new ArrayList<String>();

    /** The currently executing transaction */
    protected Xid xid;

    /** Journaling service for logging all significant state changes to commands */
    protected TransactionJournal transactionJournal;

    /** Debug counter for monitoring instance count */
    protected static int instanceCount = 0;

    /** Debug counter for monitoring instance count */
    protected Integer instanceOrdinal = null;

    /**
     * Extra work for when the txn starts, in this case do nothing
     * 
     * @see org.ikasan.connector.base.outbound.xa.EISXAManagedConnection#start(javax.transaction.xa.Xid,
     *      int)
     */
    @Override
    public final void start(Xid pXid, int flag) throws XAException
    {
        methodCalls.add("start " + new String(pXid.getBranchQualifier()) + " instance [" + instanceOrdinal + "] of ["   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                + instanceCount + "]"); //$NON-NLS-1$

        boolean tmjoin = flag == XAResource.TMJOIN;
        boolean tmresume = flag == XAResource.TMRESUME;

        if (xid != null)
        {
            logger.warn("in XAResource.start, but the xid is already set as [" + xid + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            for (String methodCall : methodCalls)
            {
                logger.warn("method call:" + methodCall); //$NON-NLS-1$
            }
        }

        logger.debug("in start, got xid [" + pXid + "] and arg1 [" + flag + "] tmjoin =[" + tmjoin + "] tmresume =[" + tmresume + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

        if (!tmresume)
        {
            setXid(pXid);
            logger.debug("We are not resuming a previous txn, this is a brand new one."); //$NON-NLS-1$

            try
            {
                transactionJournal.onXAEvent(xid, "start");
            }
            catch (TransactionJournalingException e)
            {
                logger.error(e);
                throw new XAException("Exception caught on XA.start :" + e.getMessage()); //$NON-NLS-1$
            }
        }
        else
        {
            logger.info("attempt made to resume with xid [" + xid + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }

    }

    /**
     * Deal with any specific end tasks for the txn, in this case do nothing
     * 
     * @see org.ikasan.connector.base.outbound.xa.EISXAManagedConnection#end(javax.transaction.xa.Xid,
     *      int)
     */
    @Override
    public final void end(Xid arg0, int flag) throws XAException
    {
        boolean tmsuccess = flag == XAResource.TMSUCCESS;
        boolean tmfail = flag == XAResource.TMFAIL;
        boolean tmsuspend = flag == XAResource.TMSUSPEND;

        methodCalls.add("end " + new String(arg0.getBranchQualifier()) + " storedXid=[" //$NON-NLS-1$//$NON-NLS-2$
            + xid + "] tmsuccess =[" + tmsuccess + "] tmfail=[" + tmfail + "] tmsuspend=["  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            + tmsuspend + "]"); //$NON-NLS-1$

        testXidArg(arg0, xid);

        logger.debug("in end, xid=[" + arg0 + "]" + "] flag=[" + flag + "] tmsuccess =[" + tmsuccess + "] tmfail=[" + tmfail + "] tmsuspend=[" + tmsuspend + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

        try
        {
            transactionJournal.onXAEvent(xid, "end");
        }
        catch (TransactionJournalingException e)
        {
            logger.error(e);
            throw new XAException("Exception caught on XA.end :" + e.getMessage()); //$NON-NLS-1$
        }
    }

    /**
     * Test the two Xids by comparing the contents of their global txn ids and their 
     * branch qualifiers
     * 
     * @param receivedXid
     * @param storedXid
     */
    private void testXidArg(Xid receivedXid, Xid storedXid)
    {
        byte[] recievedXidGlobalTxnId = receivedXid.getGlobalTransactionId();
        byte[] storedXidGlobalTxnId = storedXid.getGlobalTransactionId();
        byte[] recievedXidBranchQualifier = receivedXid.getBranchQualifier();
        byte[] storedXidBranchQualifer = storedXid.getBranchQualifier();

        if (!Arrays.equals(recievedXidGlobalTxnId, storedXidGlobalTxnId)
                || !Arrays.equals(recievedXidBranchQualifier, storedXidBranchQualifer)
                || (receivedXid.getFormatId() != storedXid.getFormatId()))
        {
            logger.warn("Received a different xid [" + receivedXid + "] than that recorded during start [" + storedXid + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            for (String methodCall : methodCalls)
            {
                logger.warn("method call:" + methodCall); //$NON-NLS-1$
            }
        }

    }

    /**
     * Executes a TransactionalResourceCommand using the transactional resource
     * and adds it to the List of commands associated with this managed
     * connection
     * 
     * @param command
     * @return ExectionOutput
     * @throws ResourceException
     */
    public ExecutionOutput executeCommand(TransactionalResourceCommand command) throws ResourceException
    {
        methodCalls.add("executeCommand stored xid =[" + xid + "]");  //$NON-NLS-1$//$NON-NLS-2$
        ExecutionOutput result = null;
        try
        {
            // give the command a transient reference to the transaction journal
            if (transactionJournal == null)
            {
                throw new ResourceException("transactionJournal should not be null"); //$NON-NLS-1$
            }

            command.setTransactionJournal(transactionJournal);

            TransactionalResource transactionalResource = getTransactionalResource();
            transactionalResource.ensureConnection();
            result = command.execute(transactionalResource, xid);
        }
        catch (ResourceException re)
        {
            methodCalls.add("exception in executeCommand stored xid =[" + xid + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            logger.error("Exception caught when executing command: [" + command + "]", //$NON-NLS-1$ //$NON-NLS-2$
                re);
            throw re;
        }
        methodCalls.add("end of executeCommand stored xid =[" + xid + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return result;
    }

    /**
     * Rollback the transaction, deal with the get and put cases
     * 
     * @see org.ikasan.connector.base.outbound.xa.EISXAManagedConnection#rollback(javax.transaction.xa.Xid)
     */
    @Override
    public void rollback(Xid pXid) throws XAException
    {
        methodCalls.add("rollback stored Xid =[" + new String(xid.getBranchQualifier()) + "] received Xid=["  //$NON-NLS-1$//$NON-NLS-2$
                + new String(pXid.getBranchQualifier() + "]"));
        
        logger.debug("rollback called with xid [" + pXid + "]"); //$NON-NLS-1$ //$NON-NLS-2$

        testXidArg(pXid, xid);

        preRollback(pXid);
        try
        {
            rollbackCommands();
        }
        catch (ResourceException e)
        {
            logger.warn("Received a ResourceException in the rollback, " + //$NON-NLS-1$
                    "wrapping message inside a XAException and throwing that", e); //$NON-NLS-1$
            // Have to throw the message here as we can't wrap the ResourceException
            // in a XAException
            throw new XAException(e.getMessage());
        }
        postRollback(pXid);

        try
        {
            transactionJournal.onXAEvent(xid, "rollback");
        }
        catch (TransactionJournalingException e)
        {
            logger.error(e);
            throw new XAException("Exception caught on XA.rollback :" + e.getMessage()); //$NON-NLS-1$
        }

        // Cleanup the Transaction Journal entries if configured to do so
        if (cleanupJournalOnComplete())
        {
            try
            {
                transactionJournal.cleanup(xid);
            }
            catch (TransactionJournalingException e)
            {
                logger.error("Exception caught in postRollback, cannot do anything with this!", e); //$NON-NLS-1$
            }
        }
        
        // reset the Xid
        setXid(null);
    }

    /**
     * Set the Xid for this command
     * @param xid
     */
    private void setXid(Xid xid)
    {
        logger.debug("setXid called with [" + xid + "] was previously [" + this.xid + "]");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        this.xid = xid;
    }

    /**
     * Perform the commit, performs special processing for the get and put cases
     * 
     * @see org.ikasan.connector.base.outbound.xa.EISXAManagedConnection#commit(javax.transaction.xa.Xid,
     *      boolean)
     */
    @Override
    public void commit(Xid resourceId, boolean flag) throws XAException
    {
        logger.debug("in commit, got xid [" + resourceId + "] and flag [" + flag + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        methodCalls.add("commit " + new String(resourceId.getBranchQualifier())); //$NON-NLS-1$
        testXidArg(resourceId, xid);
        // Calling preCommit
        preCommit(resourceId);
        try
        {
            commitCommands();
        }
        // Catch the ResourceException and wrap its message in a XA Exception
        catch (ResourceException e)
        {
            logger.warn("Received a  ResourceException in the commit, " + //$NON-NLS-1$
                    "wrapping message inside a XAException and throwing that", e); //$NON-NLS-1$
            throw new XAException(e.getMessage());
        }
        postCommit(resourceId);

        try
        {
            transactionJournal.onXAEvent(xid, "commit");
        }
        catch (TransactionJournalingException e)
        {
            logger.error(e);
            throw new XAException("Exception caught on XA.commit :" + e.getMessage()); //$NON-NLS-1$
        }

        if (cleanupJournalOnComplete())
        {
            try
            {
                transactionJournal.cleanup(xid);
            }
            catch (TransactionJournalingException e)
            {
                logger.error("Exception caught in postCommit, cannot do anything with this!", e); //$NON-NLS-1$
            }
        }

        // reset the Xid
        setXid(null);
    }

    /**
     * Should this connection clean up its entries in the transaction journal on
     * completion (commit or rollback) defaults to false, may be overridden
     * 
     * @return boolean
     */
    protected boolean cleanupJournalOnComplete()
    {
        return false;
    }

    /**
     * This method is called by the <code>RecoveryManager</code> on behalf of
     * the TransactionManager
     * 
     * When calling this method, the <code>RecoveryManager</code> will be
     * performing a 'scan' looking for 'recoverable' transactions.
     * 
     * @see org.ikasan.connector.base.outbound.xa.EISXAManagedConnection#recover(int)
     */
    @Override
    public Xid[] recover(int flag) throws XAException
    {
        logger.debug("in recover"); //$NON-NLS-1$
        Xid[] result = new Xid[0];
        switch (flag)
        {
        case XAResource.TMSTARTRSCAN:
        case XAResource.TMNOFLAGS:
        {
            try
            {
                result = transactionJournal.getExecutedTransactions();
            }
            catch (TransactionJournalingException e)
            {
                throw new XAException("Exception caught retrieving executed transactions from Transaction Journal. " //$NON-NLS-1$
                        + e.getMessage());
            }
            break;
        }
        case XAResource.TMENDRSCAN:
        {
            // What do we do here ??????? ??????? ?
            // for now return nothing
            break;
        }
        default:
            throw new XAException("Unsupported flag passed to recover method [" + flag + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return result;
    }

    /**
     * Calls commit on all of the transactional commands
     * 
     * @throws ResourceException
     */
    protected void commitCommands() throws ResourceException
    {
        TransactionalResource transactionalResource = getTransactionalResource();
        for (TransactionalResourceCommand command : getCommands())
        {
            logger.debug("about to call commit on command [" + command + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            command.setTransactionalResource(transactionalResource);
            command.commit();
        }
    }

    /**
     * Retrieves all commands associated with the current
     * 
     * @return List<TransactionalResourceCommand>
     * @throws ResourceException
     */
    private List<TransactionalResourceCommand> getCommands() throws ResourceException
    {
        List<TransactionalResourceCommand> commands;
        try
        {
            commands = transactionJournal.getCommands(xid);
        }
        catch (TransactionJournalingException e)
        {
            throw new ResourceException("Exception finding command history in journal for tx [" + xid + "]", e);  //$NON-NLS-1$//$NON-NLS-2$
        }
        return commands;
    }

    /**
     * Calls rollback on all of the transactional commands in reverse order
     * 
     * @throws ResourceException
     */
    protected void rollbackCommands() throws ResourceException
    {
        List<TransactionalResourceCommand> reverseCommands = new ArrayList<TransactionalResourceCommand>();
        TransactionalResource transactionalResource = getTransactionalResource();

        reverseCommands.addAll(getCommands());
        Collections.reverse(reverseCommands);

        for (TransactionalResourceCommand command : reverseCommands)
        {
            command.setTransactionalResource(transactionalResource);
            command.rollback();
        }
    }

    /**
     * Requires extending classes to provide access to the underlying
     * transactional resource
     * 
     * @return TransactionalResource
     */
    protected abstract TransactionalResource getTransactionalResource();

    /**
     * Hook method to allow any connector specific pre rollback functionality
     * 
     * @param arg0
     */
    protected void preRollback(Xid arg0)
    {
        // override where necessary
        logger.debug("preRollback with: [" + arg0 + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Hook method to allow any connector specific pre commit functionality
     * 
     * @param arg0
     */
    protected void preCommit(Xid arg0)
    {
        // override where necessary
        logger.debug("In preCommit with: [" + arg0 + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Hook method to allow any connector specific post rollback functionality
     * 
     * @param arg0
     */
    protected void postRollback(Xid arg0)
    {
        // override where necessary
        logger.debug("postRollback with: [" + arg0 + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Hook method to allow any connector specific post commit functionality
     * 
     * @param arg0
     */
    protected void postCommit(Xid arg0)
    {
        // override where necessary
        logger.debug("In postCommit with: [" + arg0 + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Allows the TransactionJournal to be set on the managed connection
     * 
     * @param transactionJournal
     */
    public void setTransactionJournal(TransactionJournal transactionJournal)
    {
        this.transactionJournal = transactionJournal;
    }

    /**
     * Prepare for the commit, in this case do nothing, return 0 always
     * 
     * @see org.ikasan.connector.base.outbound.xa.EISXAManagedConnection#prepare(javax.transaction.xa.Xid)
     * @return 0
     */
    @Override
    public int prepare(Xid arg0) throws XAException
    {

        methodCalls.add("prepare " + new String(arg0.getBranchQualifier())); //$NON-NLS-1$
        
        /*
         * duncro - For SFTP we simply vote 'OK'
         * 
         * We could potentially do more here, such as carry out some sort of
         * validation that the hidden, delivered data is both accessible and
         * complete, but that is only of limited value, as that could change by
         * the time commit is called
         */

        logger.debug("in prepare with [" + arg0 + "]"); //$NON-NLS-1$ //$NON-NLS-2$

        testXidArg(arg0, xid);

        try
        {
            transactionJournal.onXAEvent(xid, "prepare");
        }
        catch (TransactionJournalingException e)
        {
            logger.error(e);
            throw new XAException("Exception caught on XA.prepare :" + e.getMessage()); //$NON-NLS-1$
        }
        return XA_OK;
    }

    /**
     * Returns true if there is an uncommitted/unrolled back transaction
     * associated with this managed connection
     * 
     * @return true if the txn is in progress
     */
    public boolean transactionInProgress()
    {
        return (xid != null);
    }
}
