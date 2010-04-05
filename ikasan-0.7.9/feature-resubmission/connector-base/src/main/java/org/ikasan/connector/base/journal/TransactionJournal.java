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
