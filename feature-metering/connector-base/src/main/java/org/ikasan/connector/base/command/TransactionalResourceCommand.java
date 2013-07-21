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
package org.ikasan.connector.base.command;

import javax.resource.ResourceException;
import javax.transaction.xa.Xid;

import org.ikasan.connector.base.journal.TransactionJournal;
import org.springframework.beans.factory.BeanFactory;

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
