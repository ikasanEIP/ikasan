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
package org.ikasan.spec.security.dao;

import java.util.List;

import org.ikasan.spec.security.model.User;
import org.ikasan.spec.security.model.UserFilter;
import org.ikasan.spec.security.model.UserLite;

/**
 * Data Access Object interface for managing {@link User} entities in persistent storage.
 *
 * <p>Provides operations to create, retrieve, update, delete, and query users within the Ikasan
 * security framework. Supports filtering, pagination, and role-based queries for efficient user management.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface UserDao
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
     * Retrieves the count of users based on the specified user filter.
     *
     * @param userFilter The filter criteria to apply when counting users
     * @return The count of users matching the filter criteria
     */
    int getUserCount(UserFilter userFilter);

    /**
     * Retrieves all <code>User</code>s known to the system
     * 
     * @return List of all <code>Users</code>
     */
    List<User> getUsers();



    /**
     * Retrieves a paginated list of users based on the specified filter criteria.
     *
     * @param userFilter the filter criteria to apply when retrieving users, may be {@code null} for no filtering
     * @param limit the maximum number of users to retrieve, must be greater than 0
     * @param offset the number of results to skip from the beginning (0-based), must be greater than or equal to 0
     * @return a list of {@link User} instances matching the criteria, or an empty list if none exist
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
     * Retrieves a user by their unique username.
     *
     * @param username the username of the user to retrieve, must not be {@code null}
     * @return the {@link User} instance with the specified username, or {@code null} if no such user exists
     */
    User getUser(String username);

    /**
     * Retrieves users whose usernames match the specified pattern using SQL LIKE semantics.
     *
     * @param username the search pattern for usernames (supports SQL wildcard characters % and _), must not be {@code null}
     * @return a list of {@link User} instances whose usernames match the pattern, or an empty list if none match
     */
    List<User> getUserByUsernameLike(String username);

    /**
     * Retrieves users whose first names match the specified pattern using SQL LIKE semantics.
     *
     * @param firstname the search pattern for first names (supports SQL wildcard characters % and _), must not be {@code null}
     * @return a list of {@link User} instances whose first names match the pattern, or an empty list if none match
     */
    List<User> getUserByFirstnameLike(String firstname);

    /**
     * Retrieves users whose surnames match the specified pattern using SQL LIKE semantics.
     *
     * @param surname the search pattern for surnames (supports SQL wildcard characters % and _), must not be {@code null}
     * @return a list of {@link User} instances whose surnames match the pattern, or an empty list if none match
     */
    List<User> getUserBySurnameLike(String surname);

    /**
     * Persists a user to the data store.
     *
     * @param user the {@link User} instance to save, must not be {@code null}
     */
    void save(User user);

    /**
     * Deletes a user from the data store.
     *
     * @param user the {@link User} instance to delete, must not be {@code null}
     */
    void delete(User user);
}
