/*
 * $Id: UserService.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/security/service/UserService.java $
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

    
    /* (non-Javadoc)
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
     */
    public void changeUsersPassword(String username, String newPassword);
    
    
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
