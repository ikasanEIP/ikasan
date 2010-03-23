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
package org.ikasan.framework.security.service;

import java.util.List;

import org.ikasan.framework.security.model.Authority;
import org.ikasan.framework.security.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.security.userdetails.UserDetailsManager;
import org.springframework.security.userdetails.UsernameNotFoundException;

/**
 * User and Authority service interface
 * 
 * @author Ikasan Development Team
 * 
 */
public interface UserService extends UserDetailsManager
{
    /**
     * Gets all Users in the system
     * 
     * @return all Users
     */
    public List<User> getUsers();

    /**
     * Gets all Authorities in the system
     * 
     * @return all Authorities
     */
    public List<Authority> getAuthorities();

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    public User loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException;

    /**
     * Attempts to grant an authority to a user
     * 
     * @param username
     * @param authority
     * @throws IllegalArgumentException - if the user already has the authority
     */
    public void grantAuthority(String username, String authority);

    /**
     * Attempts to revoke an authority from a user
     * 
     * @param username
     * @param authority
     * @throws IllegalArgumentException - if the user does not hold the authority
     */
    public void revokeAuthority(String username, String authority);

    /**
     * Allows a specified user's password.
     * 
     * Note that this should be an administrator function
     * 
     * @param username
     * @param newPassword as plain text
     * @param confirmNewPassword as plain text
     * @throws IllegalArgumentException If the passwords don't match
     */
    public void changeUsersPassword(String username, String newPassword, String confirmNewPassword) throws IllegalArgumentException;

    /**
     * Disables the specified user
     * 
     * @param username
     * @throws IllegalArgumentException - if the specified user does not exist
     */
    public void disableUser(String username);

    /**
     * Enables the specified user
     * 
     * @param username
     * @throws IllegalArgumentException - if the specified user does not exist
     */
    public void enableUser(String username);

    /**
     * Creates a new Authority on the system
     * 
     * @param newAuthority
     */
    public void createAuthority(Authority newAuthority);
}
