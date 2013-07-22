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
package org.ikasan.framework.security.service;

import java.util.List;

import org.ikasan.framework.security.dao.AuthorityDao;
import org.ikasan.framework.security.dao.UserDao;
import org.ikasan.framework.security.model.Authority;
import org.ikasan.framework.security.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;

/**
 * Default implementation of the <code>UserService</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class UserServiceImpl implements UserService
{
    
    /**
     * Data access object for <code>User</code>
     */
    private UserDao userDao;
    
    /**
     * Data access object for <code>Authority</code>s
     */
    private AuthorityDao authorityDao;
    
    /**
     * <code>PasswordEncoder</code> for encoding user passwords
     */
    private PasswordEncoder passwordEncoder;
    

    /**
     * Constructor
     * 
     * @param userDao
     * @param authorityDao
     * @param passwordEncoder
     */
    public UserServiceImpl(UserDao userDao,AuthorityDao authorityDao,PasswordEncoder passwordEncoder)
    {
        super();
        this.userDao = userDao;
        this.authorityDao = authorityDao;
        this.passwordEncoder = passwordEncoder;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.security.service.UserService#getUsers()
     */
    public List<User> getUsers()
    {
        return userDao.getUsers();
    }

    /* (non-Javadoc)
     * @see org.springframework.security.userdetails.UserDetailsManager#changePassword(java.lang.String, java.lang.String)
     */
    public void changePassword(String oldPassword, String newPassword)
    {
        throw new UnsupportedOperationException();
        
    }

    /* (non-Javadoc)
     * @see org.springframework.security.userdetails.UserDetailsManager#createUser(org.springframework.security.userdetails.UserDetails)
     */
    public void createUser(UserDetails userDetails)
    {
        String username = userDetails.getUsername();
        String password = userDetails.getPassword();
        boolean enabled = userDetails.isEnabled();
        
        
        if (username ==null || "".equals(username)){
            throw new IllegalArgumentException("userDetails must contain a non empty username");
        }

        if (password ==null || "".equals(password)){
            throw new IllegalArgumentException("userDetails must contain a non empty password");
        }
        
        if (userExists(username)){
            throw new IllegalArgumentException("userDetails must contain a unique username");
        }
        
        String encodedPassword = passwordEncoder.encodePassword(password, null);
        
        
        
        User userToCreate = new User(username, encodedPassword, enabled);
        
        userDao.save(userToCreate);

        
    }

    /* (non-Javadoc)
     * @see org.springframework.security.userdetails.UserDetailsManager#deleteUser(java.lang.String)
     */
    public void deleteUser(String username)
    {
        userDao.delete(getUserForOperation(username));       
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.security.service.UserService#disableUser(java.lang.String)
     */
    public void disableUser(String username)
    {
        User user = getUserForOperation(username);
        
        user.setEnabled(false);
        
        userDao.save(user);
        
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.security.service.UserService#enableUser(java.lang.String)
     */
    public void enableUser(String username)
    {
        User user = getUserForOperation(username);
        
        user.setEnabled(true);
        
        userDao.save(user);
        
    }

    /**
     * Looks up a user, but throws an exception if it doesnt exist
     * 
     * @param username
     * @return User if exists
     */
    private User getUserForOperation(String username)
    {
        User user = userDao.getUser(username);
        if (user==null){
            throw new IllegalArgumentException("user does not exist with username ["+username+"]");
        }
        return user;
    }



    /* (non-Javadoc)
     * @see org.springframework.security.userdetails.UserDetailsManager#updateUser(org.springframework.security.userdetails.UserDetails)
     */
    public void updateUser(UserDetails userDetails)
    {
        userDao.save((User)userDetails);  
    }
    

    /* (non-Javadoc)
     * @see org.springframework.security.userdetails.UserDetailsManager#userExists(java.lang.String)
     */
    public boolean userExists(String username)
    {
        return (userDao.getUser(username)!=null);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.security.service.UserService#loadUserByUsername(java.lang.String)
     */
    public User loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException
    {
        User user = userDao.getUser(username);
        
        if (user == null){
            throw new UsernameNotFoundException("Unknown username : "+username);
        }
        return user;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.security.service.UserService#getAuthorities()
     */
    public List<Authority> getAuthorities()
    {
        return authorityDao.getAuthorities();
    }
    

    /* (non-Javadoc)
     * @see org.ikasan.framework.security.service.UserService#grantAuthority(java.lang.String, java.lang.String)
     */
    public void grantAuthority(String username, String authority)
    {
        User user = loadUserByUsername(username);
        Authority nongrantedAuthority = authorityDao.getAuthority(authority);
        
        user.grantAuthority(nongrantedAuthority);
        
        userDao.save(user);
        
    }
    
    /* (non-Javadoc)
     * @see org.ikasan.framework.security.service.UserService#revokeAuthority(java.lang.String, java.lang.String)
     */
    public void revokeAuthority(String username, String authority)
    {
        User user = loadUserByUsername(username);
        Authority grantedAuthority = authorityDao.getAuthority(authority);
        
        user.revokeAuthority(grantedAuthority);
        
        userDao.save(user);
        
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.security.service.UserService#changeUsersPassword(java.lang.String, java.lang.String)
     */
    public void changeUsersPassword(String username, String newPassword)
    {
        String encodedPassword = passwordEncoder.encodePassword(newPassword, null);
        User user = loadUserByUsername(username);
        
        user.setPassword(encodedPassword);
        
        userDao.save(user);
        
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.security.service.UserService#createAuthority(org.ikasan.framework.security.model.Authority)
     */
    public void createAuthority(Authority newAuthority)
    {
        if (authorityDao.getAuthorities().contains(newAuthority)){
            throw new IllegalArgumentException("Cannot create new authority ["+newAuthority+"] as it already exists!");
        }
        
        authorityDao.save(newAuthority);
        
    }


}
