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

import java.util.List;

import javax.transaction.xa.Xid;

import org.ikasan.connector.base.command.TransactionalResourceCommand;
import org.ikasan.connector.base.command.XidImpl;

/**
 * Interface for the Transaction Journal which will log the state of all
 * transactional commands
 * 
 * @author Ikasan Development Team
 * 
 */
public interface TransactionJournal
{

    /**
     * Notifies the journal of an update to a command
     * 
     * @param transactionalResourceCommand
     * @throws TransactionJournalingException
     */
    public void notifyUpdate(TransactionalResourceCommand transactionalResourceCommand)
            throws TransactionJournalingException;

    /**
     * Retrieves all <code>TransactionalResourceCommand</code> associated with
     * this <code>Xid</code>
     * 
     * @param xid
     * 
     * @return List of all TransactionalResourceCommands for xid
     * @throws TransactionJournalingException 
     */
    public List<TransactionalResourceCommand> getCommands(Xid xid) throws TransactionJournalingException;

    /**
     * Retrieves <code>Xid</code> for all transactions whose commands have all
     * successfully been executed, but not committed.
     * 
     * @return Xid[]
     * @throws TransactionJournalingException
     */
    public Xid[] getExecutedTransactions() throws TransactionJournalingException;

    /**
     * Finds a Journaled XidImpl for a given Xid
     * @param xid
     * @return XidImpl
     * @throws TransactionJournalingException
     */
    public XidImpl resolveXid(Xid xid) throws TransactionJournalingException;

    /**
     * Notification method to update the journal of XAResource lifecycle state change
     * 
     * @param xid
     * @param state
     * @throws TransactionJournalingException
     */
    public void onXAEvent(Xid xid, String state) throws TransactionJournalingException;

    /**
     * Cleans up the Transaction Journal for a given Xid
     * 
     * @param xid
     * @throws TransactionJournalingException
     */
    public void cleanup(Xid xid) throws TransactionJournalingException;

}
