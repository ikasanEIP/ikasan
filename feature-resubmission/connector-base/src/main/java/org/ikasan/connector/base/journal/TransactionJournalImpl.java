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
package org.ikasan.connector.base.journal;

import java.util.Date;
import java.util.List;

import javax.transaction.xa.Xid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

import org.ikasan.connector.base.command.TransactionalResourceCommand;
import org.ikasan.connector.base.command.TransactionalResourceCommandDAO;
import org.ikasan.connector.base.command.TransactionalResourceCommandPersistenceException;
import org.ikasan.connector.base.command.XidImpl;

/**
 * @author Ikasan Development Team
 * 
 */
public class TransactionJournalImpl implements TransactionJournal
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(TransactionJournalImpl.class);

    /** Data Access Object for persisting Transactional Resource Commands */
    private TransactionalResourceCommandDAO dao;

    /**
     * Bean factory that commands use for resource lookup
     */
    private BeanFactory beanFactory;

    /** The id of the client registering this transaction */
    private String clientId;
    
    /**
     * Alternate constructor supporting passing of a datasource handle.
     * 
     * @param dao
     * @param clientId
     * @param beanFactory
     */
    public TransactionJournalImpl(TransactionalResourceCommandDAO dao, String clientId, BeanFactory beanFactory)
    {
        this.dao = dao;
        this.beanFactory = beanFactory;
        this.clientId = clientId;
    }

    public void notifyUpdate(TransactionalResourceCommand command) throws TransactionJournalingException
    {
        logger.debug("Journal notified of command update, command class is [" //$NON-NLS-1$
                + command.getClass() + "] state is [" + command.getState() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        // TODO do we want to persist each and every state change?
        try
        {
            dao.save(command);
        }
        catch (TransactionalResourceCommandPersistenceException e)
        {
            throw new TransactionJournalingException("Exception saving command [" + command + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public List<TransactionalResourceCommand> getCommands(Xid xid) throws TransactionJournalingException
    {
        List<TransactionalResourceCommand> commands = null;
        try
        {

            commands = dao.findCommandsByTransaction(xid);

            // reset the transaction journal on each of the commands, as this is
            // transient
            for (TransactionalResourceCommand command : commands)
            {
                command.setTransactionJournal(this);
                command.setBeanFactory(beanFactory);
            }

        }
        catch (TransactionalResourceCommandPersistenceException e)
        {
            logger.error(e);

            throw new TransactionJournalingException("Exception finding commands for Xid [" + xid + "]", e); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return commands;
    }

    public Xid[] getExecutedTransactions() throws TransactionJournalingException
    {
        Xid[] result;
        try
        {

            List<XidImpl> preparedXids = dao.findXidbyState("prepare");
            result = preparedXids.toArray(new Xid[0]);

        }
        catch (TransactionalResourceCommandPersistenceException e)
        {
            throw new TransactionJournalingException("Exception finding executed transactions", e); //$NON-NLS-1$
        }

        return result;
    }

    public XidImpl resolveXid(Xid xid) throws TransactionJournalingException
    {
        XidImpl result = null;

        try
        {
            result = dao.find(xid);
        }
        catch (TransactionalResourceCommandPersistenceException e)
        {
            throw new TransactionJournalingException("Exception finding persisted Xid [" + xid + "]", e);  //$NON-NLS-1$//$NON-NLS-2$
        }

        return result;
    }

    public void onXAEvent(Xid xid, String state) throws TransactionJournalingException
    {

        logger.debug("xid = [" + xid + "] state = [" + state + "]"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

        XidImpl xidImpl = null;

        if ("start".equals(state))
        {
            createXid(xid);
        }
        else
        {
            xidImpl = resolveXid(xid);
            if (xidImpl == null)
            {
                throw new TransactionJournalingException("Could not resolve XidImpl for [" + xid + "]", null);  //$NON-NLS-1$//$NON-NLS-2$
            }

            xidImpl.setState(state);
            xidImpl.setLastUpdatedDateTime(new Date());

            try
            {
                dao.save(xidImpl);
            }
            catch (TransactionalResourceCommandPersistenceException e)
            {
                throw new TransactionJournalingException("Exception persisting Xid", e); //$NON-NLS-1$
            }
        }

    }

    /**
     * Create a new Xid
     * 
     * @param xid
     * @throws TransactionJournalingException
     */
    private void createXid(Xid xid) throws TransactionJournalingException
    {
        XidImpl xidImpl = new XidImpl(xid);
        xidImpl.setClientId(clientId);
        try
        {
            XidImpl beforeFound = dao.find(xidImpl);
            logger.debug("before create, found  Xid [" + beforeFound + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            dao.save(xidImpl);
            logger.debug("saved new Xid matching [" + xid + "], id is [" + xidImpl.getId() + "]"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            XidImpl afterFound = dao.find(xidImpl);
            logger.debug("end create, found just added Xid [" + afterFound + "]"); //$NON-NLS-1$//$NON-NLS-2$
        }
        catch (TransactionalResourceCommandPersistenceException e)
        {
            throw new TransactionJournalingException("Exception persisting new Xid", e); //$NON-NLS-1$
        }
    }

    /*
     * (non-Javadoc) Cleans up any/all TransactionalResourceCommands for an Xid,
     * followed by the Xid itself
     * 
     * @see org.ikasan.connector.base.journal.TransactionJournal#cleanup(javax.transaction.xa.Xid)
     */
    public void cleanup(Xid xid) throws TransactionJournalingException
    {

        List<TransactionalResourceCommand> commands = getCommands(xid);

        try
        {
            for (TransactionalResourceCommand command : commands)
            {
                dao.deleteCommand(command);
            }
            dao.deleteXid(resolveXid(xid));
        }
        catch (TransactionalResourceCommandPersistenceException e)
        {
            throw new TransactionJournalingException("Exception cleaning up transaction journal", e); //$NON-NLS-1$
        }

    }

}
