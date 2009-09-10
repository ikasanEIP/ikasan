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
package org.ikasan.framework.payload.dao;

import java.util.List;

import org.ikasan.framework.payload.model.DatabasePayload;

/**
 * Data access interface for the persistence of <code>DatabasePayload</code>s
 * 
 * @author Ikasan Development Team
 */
public interface DatabasePayloadDao
{
    /**
     * Persists a <code>DatabsePayload</code>
     * 
     * @param databasePayload to persist
     */
    public void save(DatabasePayload databasePayload);

    /**
     * Retrieves a List of unconsumed <code>DatabsePayload</code>s
     * 
     * @return List of unconsumed <code>DatabsePayload</code>s
     */
    public List<DatabasePayload> findUnconsumed();

    /**
     * Retrieves a List of Id's for unconsumed Database Payloads
     * 
     * @return List of Id's
     */
    public List<Long> findUnconsumedIds();

    /**
     * Retrieves a <code>DatabasePayload</code> by Id
     * 
     * @param id Id of the payload
     * 
     * @return <code>DatabasePayload</code>
     */
    public DatabasePayload getDatabasePayload(Long id);

    /**
     * Deletes a <code>DatabsePayload</code> from persistent storage
     * 
     * @param databasePayload to delete
     */
    public void delete(DatabasePayload databasePayload);
}
