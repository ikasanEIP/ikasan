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
package org.ikasan.connector.base.outbound.xa;

import org.ikasan.connector.base.outbound.EISManagedConnection; 

import javax.resource.ResourceException;
import javax.transaction.xa.*; 

/**
 * This is an abstract class representing the XAManagedConnection 
 * for the resource adapter.
 *  
 * This is derived form the EISManagedConnection, but requires the derived 
 * classes implement the XA methods.
 *  
 * @author Ikasan Development Team
 */  
public abstract class EISXAManagedConnection 
    extends EISManagedConnection
    implements XAResource
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
