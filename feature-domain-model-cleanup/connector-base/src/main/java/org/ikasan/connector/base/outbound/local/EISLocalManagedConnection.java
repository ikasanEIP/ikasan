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
package org.ikasan.connector.base.outbound.local;

import javax.resource.ResourceException;
import javax.resource.spi.LocalTransaction;

import org.ikasan.connector.base.outbound.EISManagedConnection;

/**
 * This is an abstract class representing the XAManagedConnection 
 * for the resource adapter.
 *  
 * This is derived form the EISManagedConnection, but requires the derived 
 * classes implement the XA methods.
 *  
 * @author Ikasan Development Team
 */  
public abstract class EISLocalManagedConnection 
    extends EISManagedConnection
    implements LocalTransaction
{
    /**
     * When a connection is in an auto-commit mode, an operation on the 
     * connection automatically commits after it has been executed. 
     * The auto-commit mode must be off if multiple interactions have 
     * to be grouped in a single transaction, either local or XA, 
     * and committed or rolled back as a unit.
     * 
     * This is a Local Transaction and may have multiple operations within 
     * a single unit, so we must set auto-commit to false.
     * 
     * @return false
     */
    @Override
    public boolean getAutoCommit()
    {
        return false;
    }

    @Override
    public abstract void associateConnection(Object arg0) throws ResourceException;
    public abstract void begin() throws ResourceException;
    public abstract void commit() throws ResourceException;
    public abstract void rollback() throws ResourceException;
    
} 
