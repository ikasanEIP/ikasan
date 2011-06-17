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
package org.ikasan.connector.base.outbound.xa;

import javax.resource.ResourceException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.ikasan.connector.base.outbound.EISManagedConnection;

import com.arjuna.ats.jta.resources.LastResourceCommitOptimisation;

/**
 * This is an abstract class representing the XAManagedConnection 
 * for the resource adapter.
 *  
 * This is derived form the EISManagedConnection, but requires the derived 
 * classes implement the XA methods.
 *  
 * @author Ikasan Development Team
 */  
public abstract class EISXALRCOManagedConnection 
    extends EISManagedConnection
    implements LastResourceCommitOptimisation
{

    /**
     * When a connection is in an auto-commit mode, an operation on the 
     * connection automatically commits after it has been executed. 
     * The auto-commit mode must be off if multiple interactions have 
     * to be grouped in a single transaction, either local or XA, 
     * and committed or rolled back as a unit.
     * 
     * This is an XA Transaction and may have multiple operations within 
     * a single unit, so we must set auto-commit to false.
     * 
     * @return false
     */
    @Override
    public boolean getAutoCommit()
    {
        return false;
    }

    /**
     * Derived class must implement the XA Resource specifics
     */
    @Override
    public abstract XAResource getXAResource() throws ResourceException;
    public abstract void commit(Xid xid, boolean bool) throws XAException;
    public abstract void end(Xid arg0, int arg1) throws XAException;
    public abstract void forget(Xid arg0) throws XAException;
    public abstract int getTransactionTimeout() throws XAException;
    public abstract boolean isSameRM(XAResource arg0) throws XAException;
    public abstract int prepare(Xid arg0) throws XAException;
    public abstract Xid[] recover(int arg0) throws XAException;
    public abstract void rollback(Xid arg0) throws XAException;
    public abstract boolean setTransactionTimeout(int arg0) throws XAException;
    public abstract void start(Xid arg0, int arg1) throws XAException;

} 
