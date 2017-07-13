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
package org.ikasan.security.dao;

import java.util.List;

import org.ikasan.security.model.User;
import org.ikasan.security.model.UserLite;

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
     * Retrieves all <code>UserLite</code>s known to the system
     *
     * @return List of all <code>UserLites</code>
     */
    public List<UserLite> getUserLites();

    /**
     * Retrieves a specific <code>User</code> by name
     * 
     * @param username
     * @return specified <code>User</code> or null if does not exist
     */
    public User getUser(String username);

    /**
     * Retrieves a List of <code>User</code> whose username like username%
     * 
     * @param username
     * @return specified <code>User</code> or null if does not exist
     */
    public List<User> getUserByUsernameLike(String username);
    
    /**
     * Retrieves a List of <code>User</code> whose firstname like firstname%
     * 
     * @param username
     * @return specified <code>User</code> or null if does not exist
     */
    public List<User> getUserByFirstnameLike(String firstname);
    
    /**
     * Retrieves a List of <code>User</code> whose surname like surname%
     * 
     * @param username
     * @return specified <code>User</code> or null if does not exist
     */
    public List<User> getUserBySurnameLike(String surname);

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
