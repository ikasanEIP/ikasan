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
package org.ikasan.connector.basefiletransfer.outbound.persistence;

import org.hibernate.HibernateException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;

/**
 * Data Access Interface for File Transfer objects
 * 
 * @author Ikasan Development Team
 */
public interface BaseFileTransferDao {

	/**
	 * Method that checks if an <code>ClientListEntry</code> is already
	 * included in the filter table.
	 * 
	 * @param entry The <code>ClientListEntry</code> to filter
	 * @param filterOnFilename 
	 * @param filterOnLastModifiedDate 
	 * @return <code>true</code> if the entry is a duplicate,
	 *         <code>false</code> otherwise.
	 * @throws NullPointerException
	 * @throws HibernateException
	 */
	public abstract boolean isDuplicate(ClientListEntry entry, boolean filterOnFilename, boolean filterOnLastModifiedDate) throws HibernateException;

	/**
	 * Derives an <code>FileFilter</code> from the provided
	 * <code>ClientListEntry</code> and persists it to the filtering
	 * table.
	 * 
	 * @param entry the <code>ClientListEntry</code> to save.
	 * @throws HibernateException
	 */
	public abstract void persistClientListEntry(ClientListEntry entry) throws HibernateException;

    /**
     * Housekeeps <code>FileFilter</code> entries.
     * 
     * @param clientId The clientId (as multiple clients share the same table) 
     * @param ageOfFiles (delete from today minus ageOfFiles in Days)
     * @param maxRows The maximum number of rows the housekeeping can deal with
     * 
     * @throws HibernateException
     */
    public abstract void housekeep(String clientId, int ageOfFiles, int maxRows) throws HibernateException;
	
}