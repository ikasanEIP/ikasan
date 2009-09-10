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

import javax.resource.ResourceException;
import javax.transaction.xa.Xid;

import org.springframework.beans.factory.BeanFactory;

import org.ikasan.connector.base.journal.TransactionJournal;

/**
 * An implementation of the Command Pattern to encapsulate a single interaction
 * with a transactional resource
 * 
 * @author Ikasan Development Team
 * 
 */
public interface TransactionalResourceCommand
{
    /**
     * Execute a transactional operation on the underlying transactional
     * resource
     * 
     * @param resource Underlying transaction managed resource
     * @param xid referencing the transaction
     * @return ExecutionOutput encapsulating all useful output
     * @throws ResourceException
     */
    public ExecutionOutput execute(TransactionalResource resource, final Xid xid)
            throws ResourceException;

    /**
     * Sets the ExecutionContext on the command
     * 
     * @param context
     */
    public void setExecutionContext(ExecutionContext context);
    
    /**
     * Carry out any operations required to commit the result of this command
     * 
     * @throws ResourceException
     */
    public void commit() throws ResourceException;

    /**
     * Carry out any operations required to rollback the result of this command
     * 
     * @throws ResourceException
     */
    public void rollback() throws ResourceException;

    
    /**
     * Setter for the TransactionJournal
     * @param transactionJournal
     */
    public void setTransactionJournal(TransactionJournal transactionJournal);
    
    /**
     * @return a String representation of the State of this Command
     */
    public String getState();
    
    /**
     * @param beanFactory
     */
    public void setBeanFactory(BeanFactory beanFactory);

    /**
     * Allows the allows TransactionalResource to be set 
     * @param transactionalResource
     */
    public void setTransactionalResource(
            TransactionalResource transactionalResource);
    

    /**
     * Returns the <code>Xid</code> representing the transaction associated with 
     * this <code>AbstractTransactionalResourceCommand</code>
     * @return <code>Xid</code>
     */
    public Xid getXid();
}
