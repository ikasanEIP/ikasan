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
package org.ikasan.spec.security.service;

import org.ikasan.spec.security.model.Policy;
import org.ikasan.spec.security.model.User;
import org.ikasan.spec.security.model.UserFilter;
import org.ikasan.spec.security.model.UserLite;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.List;

/**
 * Service interface for managing user accounts and authorities in the Ikasan security framework.
 *
 * <p>Extends Spring Security's {@link UserDetailsManager} to provide comprehensive user account
 * management operations including creation, modification, deletion, and querying of users.
 * Supports user-authority associations, role-based filtering, password management, and
 * account status control (enabled/disabled).
 *
 * <p>Key functionality includes:
 * <ul>
 *   <li>User account lifecycle - create, update, delete, enable, and disable users</li>
 *   <li>Credential management - password changes and validation</li>
 *   <li>Authority management - grant and revoke permissions to users</li>
 *   <li>Role-based queries - retrieve users with or without specific roles</li>
 *   <li>Filtering and pagination - efficient querying for large user datasets</li>
 *   <li>User details loading - integration with Spring Security authentication</li>
 * </ul>
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface UserService extends UserDetailsManager
{
    /**
     * Creates a new user with the specified details.
     *
     * @param username the username of the user to be created
     * @param password the password for the user
     * @param email the email address of the user
     * @param enabled true if the user account is enabled, false otherwise
     * @return the created User instance
     */
    User createUser(String username, String password, String email, boolean enabled);
    /**
     * Retrieves a list of UserLite objects with a specified role, user filter, limit, and offset.
     *
     * @param roleName The role name to filter the users by
     * @param userFilter The additional filter criteria to apply when retrieving users
     * @param limit The maximum number of users to retrieve
     * @param offset The offset from where to start retrieving users
     * @return List of UserLite objects based on the provided role, filter, limit, and offset
     */
    List<UserLite> getUsersWithRole(String roleName, UserFilter userFilter, int limit, int offset);


    /**
     * Retrieves the count of users with a specific role and additional filter criteria.
     *
     * @param roleName The role name to filter the users by
     * @param userFilter The additional filter criteria to apply when retrieving users
     * @return The count of users with the specified role and matching the filter criteria
     */
    int getUsersWithRoleCount(String roleName, UserFilter userFilter);

    /**
     * Retrieves a list of UserLite objects that do not have the specified role, based on the provided role name,
     * user filter, limit, and offset.
     *
     * @param roleName   The role name to filter the users by
     * @param userFilter The additional filter criteria to apply when retrieving users
     * @param limit      The maximum number of users to retrieve
     * @param offset     The offset from where to start retrieving users
     * @return List of UserLite objects that do not have the specified role, based on the provided criteria
     */
    List<UserLite> getUsersWithoutRole(String roleName, UserFilter userFilter, int limit, int offset);


    /**
     * Retrieves the count of users who do not have the specified role and match the given filter criteria.
     *
     * @param roleName    The role name to exclude from counting the users
     * @param userFilter  The additional filter criteria to apply when counting users
     * @return The count of users without the specified role and matching the filter criteria
     */
    int getUsersWithoutRoleCount(String roleName, UserFilter userFilter);

    /**
     * Returns the total number of users in the system.
     *
     * @return the number of users in the system
     */
    int getUserCount(UserFilter userFilter);

    /**
     * Gets all Users in the system
     * 
     * @return all Users
     */
    public List<User> getUsers();


    /**
     * Retrieves a list of users based on the provided filter, limit, and offset.
     *
     * @param userFilter the filter to apply on users
     * @param limit the maximum number of users to retrieve
     * @param offset the offset from where to start retrieving users
     * @return a list of users based on the filter, limit, and offset provided
     */
    List<User> getUsers(UserFilter userFilter, int limit, int offset);

    /**
     * Retrieves all <code>UserLite</code>s known to the system
     *
     * @return List of all <code>UserLites</code>
     */
    List<UserLite> getUserLites();


    /**
     * Retrieves a list of UserLite objects with a specified limit and offset.
     *
     * @param limit the maximum number of UserLite objects to retrieve
     * @param offset the offset from where to start retrieving UserLite objects
     * @return a list of UserLite objects based on the limit and offset provided
     */
    List<UserLite> getUserLites(int limit, int offset);

    /**
     * Gets all Authorities in the system
     * 
     * @return all Authorities
     */
    public List<Policy> getAuthorities();

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
     * Note that this should be an administrator function or a function of that particular user
     * 
     * @param username
     * @param newPassword as plain text
     * @param confirmNewPassword as plain text
     * @throws IllegalArgumentException If the passwords don't match
     */
    public void changeUsersPassword(String username, String newPassword, String confirmNewPassword) throws IllegalArgumentException;

    /**
     * Allows a specified user's email address.
     * 
     * Note that this should be an administrator function or a function of that particular user
     * 
     * @param username
     * @param newEmail as plain text
     * @throws IllegalArgumentException If the user is not valid
     */
    public void changeUsersEmail(String username, String newEmail) throws IllegalArgumentException;
    
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
     * Retrieves a List of <code>User</code> whose username like username%
     * 
     * @param username
     * @return specified <code>User</code> or null if does not exist
     */
    public List<User> getUserByUsernameLike(String username);
    
    /**
     * Retrieves a List of <code>User</code> whose firstname like firstname%
     * 
     * @param firstname
     * @return specified <code>User</code> or null if does not exist
     */
    public List<User> getUserByFirstnameLike(String firstname);
    
    /**
     * Retrieves a List of <code>User</code> whose surname like surname%
     * 
     * @param surname
     * @return specified <code>User</code> or null if does not exist
     */
    public List<User> getUserBySurnameLike(String surname);

    /**
     * Authenticates a user based on username and password credentials.
     *
     * <p>Default implementation throws {@link UnsupportedOperationException}. Implementations
     * should override this method to provide authentication logic.
     *
     * @param username the username to authenticate, must not be {@code null}
     * @param password the password to validate, must not be {@code null}
     * @return {@code true} if authentication succeeds, {@code false} otherwise
     * @throws UnsupportedOperationException if the implementation does not support this operation
     */
    default public boolean authenticate(String username, String password) {
        throw new UnsupportedOperationException();
    }
}
