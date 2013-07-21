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
package org.ikasan.framework.security.dao;

import java.util.List;

import org.ikasan.framework.security.model.User;

/**
 * Data Access interface for <code>User</code> instances
 * 
 * @author Ikasan Development Team
 *
 */
public interface UserDao
{

    /**
     * Retrieves all <code>User</code>s known to the system
     * 
     * @return List of all <code>Users</code>
     */
    public List<User> getUsers();

    /**
     * Retrieves a specific <code>User</code> by name
     * 
     * @param username
     * @return specified <code>User</code> or null if does not exist
     */
    public User getUser(String username);

    /**
     * Saves a <code>User</code> to persistent storage
     * 
     * @param user
     */
    public void save(User user);

    /**
     * Deletes a <code>User</code> from persistent storage
     * 
     * @param user
     */
    public void delete(User user);

   
}
