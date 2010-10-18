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