/*
 * $Id: TransactionalResourceCommandDAO.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/main/java/org/ikasan/connector/base/command/TransactionalResourceCommandDAO.java $
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

import java.util.List;

import javax.transaction.xa.Xid;

import org.ikasan.connector.base.command.state.State;

/**
 * Data Access Interface for Transactional Resource Commands
 * 
 * @author Ikasan Development Team
 */
public interface TransactionalResourceCommandDAO
{
    /**
     * Persists a TransactionalResourceCommand
     * 
     * @param command
     * @throws TransactionalResourceCommandPersistenceException
     */
    public void save(TransactionalResourceCommand command) throws TransactionalResourceCommandPersistenceException;

    /**
     * Returns all <code>TransactionalResourceCommand</code>s for the
     * specified transaction
     * 
     * @param xid
     * @return List of <code>TransactionalResourceCommand</code>, may be
     *         empty
     * @throws TransactionalResourceCommandPersistenceException
     */
    public List<TransactionalResourceCommand> findCommandsByTransaction(Xid xid) throws TransactionalResourceCommandPersistenceException;

    /**
     * Returns all <code>TransactionalResourceCommand</code>s in the
     * specified <code>State</code>
     * 
     * @param state
     * @return List of <code>TransactionalResourceCommand</code>, may be
     *         empty
     * @throws TransactionalResourceCommandPersistenceException
     */
    public List<TransactionalResourceCommand> findCommandsByState(State state) throws TransactionalResourceCommandPersistenceException;

    /**
     * Find a XidImpl
     * 
     * @param xid
     * @return XidImpl
     * @throws TransactionalResourceCommandPersistenceException
     */
    public XidImpl find(Xid xid) throws TransactionalResourceCommandPersistenceException;

    /**
     * Save the xidImpl
     * 
     * @param xidImpl
     * @throws TransactionalResourceCommandPersistenceException
     */
    public void save(XidImpl xidImpl) throws TransactionalResourceCommandPersistenceException;

    /**
     * Find Xid by State
     * 
     * @param state
     * @return List of XidImpls
     * 
     * @throws TransactionalResourceCommandPersistenceException
     */
    public List<XidImpl> findXidbyState(String state) throws TransactionalResourceCommandPersistenceException;

    /**
     * Deletes a specified TransactionalResourceCommand
     * 
     * @param command
     * @throws TransactionalResourceCommandPersistenceException
     */
    public void deleteCommand(TransactionalResourceCommand command) throws TransactionalResourceCommandPersistenceException;

    /**
     * Deletes a specified XidImpl
     * 
     * @param resolveXid
     * @throws TransactionalResourceCommandPersistenceException
     */
    public void deleteXid(XidImpl resolveXid) throws TransactionalResourceCommandPersistenceException;
}
