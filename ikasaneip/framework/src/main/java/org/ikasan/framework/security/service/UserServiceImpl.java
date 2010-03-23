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
    public UserServiceImpl(UserDao userDao, AuthorityDao authorityDao, PasswordEncoder passwordEncoder)
    {
        super();
        this.userDao = userDao;
        this.authorityDao = authorityDao;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.security.service.UserService#getUsers()
     */
    public List<User> getUsers()
    {
        return userDao.getUsers();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.userdetails.UserDetailsManager#changePassword(java.lang.String,
     * java.lang.String)
     */
    public void changePassword(String oldPassword, String newPassword)
    {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.userdetails.UserDetailsManager#createUser(org.springframework.security.userdetails
     * .UserDetails)
     */
    public void createUser(UserDetails userDetails)
    {
        String username = userDetails.getUsername();
        String password = userDetails.getPassword();
        String email = "";
        if (userDetails instanceof User)
        {
            User tempUser = (User)userDetails;
            email = tempUser.getEmail();
        }
        boolean enabled = userDetails.isEnabled();
        if (username == null || "".equals(username))
        {
            throw new IllegalArgumentException("userDetails must contain a non empty username");
        }
        if (password == null || "".equals(password))
        {
            throw new IllegalArgumentException("userDetails must contain a non empty password");
        }
        if (email == null || "".equals(email))
        {
            throw new IllegalArgumentException("user must contain a non empty email address");
        }
        if (userExists(username))
        {
            throw new IllegalArgumentException("userDetails must contain a unique username");
        }
        String encodedPassword = passwordEncoder.encodePassword(password, null);
        User userToCreate = new User(username, encodedPassword, email, enabled);
        userDao.save(userToCreate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.userdetails.UserDetailsManager#deleteUser(java.lang.String)
     */
    public void deleteUser(String username)
    {
        userDao.delete(getUserForOperation(username));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.security.service.UserService#disableUser(java.lang.String)
     */
    public void disableUser(String username)
    {
        User user = getUserForOperation(username);
        user.setEnabled(false);
        userDao.save(user);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.security.service.UserService#enableUser(java.lang.String)
     */
    public void enableUser(String username)
    {
        User user = getUserForOperation(username);
        user.setEnabled(true);
        userDao.save(user);
    }

    /**
     * Looks up a user, but throws an exception if it doesn't exist
     * 
     * @param username
     * @return User if exists
     * @throws IllegalArgumentException if user is not found
     */
    private User getUserForOperation(String username) throws IllegalArgumentException 
    {
        User user = userDao.getUser(username);
        if (user == null)
        {
            throw new IllegalArgumentException("user does not exist with username [" + username + "]");
        }
        return user;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.userdetails.UserDetailsManager#updateUser(org.springframework.security.userdetails
     * .UserDetails)
     */
    public void updateUser(UserDetails userDetails)
    {
        userDao.save((User) userDetails);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.userdetails.UserDetailsManager#userExists(java.lang.String)
     */
    public boolean userExists(String username)
    {
        return (userDao.getUser(username) != null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.security.service.UserService#loadUserByUsername(java.lang.String)
     */
    public User loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException
    {
        User user = userDao.getUser(username);
        if (user == null)
        {
            throw new UsernameNotFoundException("Unknown username : " + username);
        }
        return user;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.security.service.UserService#getAuthorities()
     */
    public List<Authority> getAuthorities()
    {
        return authorityDao.getAuthorities();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.security.service.UserService#grantAuthority(java.lang.String, java.lang.String)
     */
    public void grantAuthority(String username, String authority)
    {
        User user = loadUserByUsername(username);
        Authority nongrantedAuthority = authorityDao.getAuthority(authority);
        user.grantAuthority(nongrantedAuthority);
        userDao.save(user);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.security.service.UserService#revokeAuthority(java.lang.String, java.lang.String)
     */
    public void revokeAuthority(String username, String authority)
    {
        User user = loadUserByUsername(username);
        Authority grantedAuthority = authorityDao.getAuthority(authority);
        user.revokeAuthority(grantedAuthority);
        userDao.save(user);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.security.service.UserService#changeUsersPassword(java.lang.String, java.lang.String)
     */
    public void changeUsersPassword(String username, String newPassword, String confirmNewPassword) throws IllegalArgumentException
    {
        if (!newPassword.equals(confirmNewPassword))
        {
            throw new IllegalArgumentException("Passwords do not match, please try again.");
        }
        String encodedPassword = passwordEncoder.encodePassword(newPassword, null);
        User user = loadUserByUsername(username);
        user.setPassword(encodedPassword);
        userDao.save(user);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.security.service.UserService#createAuthority(org.ikasan.framework.security.model.Authority)
     */
    public void createAuthority(Authority newAuthority)
    {
        if (authorityDao.getAuthorities().contains(newAuthority))
        {
            throw new IllegalArgumentException("Cannot create new authority [" + newAuthority
                    + "] as it already exists!");
        }
        authorityDao.save(newAuthority);
    }
}
