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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.User;
import org.ikasan.security.model.UserFilter;
import org.ikasan.security.model.UserLite;

import java.util.List;

/**
 * Hibernate implementation of <code>UserDao</code>
 * 
 * @author Ikasan Development Team
 *
 */
public class HibernateUserDao implements UserDao
{
    @PersistenceContext(unitName = "security")
    private EntityManager entityManager;

    @Override
    public List<UserLite> getUsersWithRole(String roleName, UserFilter userFilter, int limit, int offset) {
        StringBuffer queryBuffer = this.getUserFilterPredicateString(
            SecurityConstants.GET_USERS_WITH_ROLE_QUERY, userFilter);

        if(userFilter.getSortOrder() != null && userFilter.getSortColumn() != null) {
            if (userFilter.getSortOrder().equals("ASCENDING")) {
                queryBuffer.append(" order by u.").append(userFilter.getSortColumn()).append(" ASC");
            }
            else if (userFilter.getSortOrder().equals("DESCENDING")) {
                queryBuffer.append(" order by u.").append(userFilter.getSortColumn()).append(" DESC");
            }
        }

        Query query = this.entityManager.createQuery(queryBuffer.toString());
        query.setParameter("name", roleName);
        return (List<UserLite>) query
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();
    }

    @Override
    public int getUsersWithRoleCount(String roleName, UserFilter userFilter) {
        StringBuffer queryBuffer = this.getUserFilterPredicateString(
            SecurityConstants.GET_USERS_WITH_ROLE_COUNT_QUERY, userFilter);

        Query query = this.entityManager.createQuery(queryBuffer.toString());
        query.setParameter("name", roleName);
        return ((Long)query.getSingleResult()).intValue();
    }

    @Override
    public List<UserLite> getUsersWithoutRole(String roleName, UserFilter userFilter, int limit, int offset) {
        StringBuffer queryBuffer = new StringBuffer("select user from UserLite user where  user.id NOT IN (");
        queryBuffer.append(SecurityConstants.GET_USER_IDS_WITH_ROLE_QUERY);
        queryBuffer.append(")");

        queryBuffer.append(this.getUserFilterPredicateString("user",
            "", userFilter));

        if(userFilter.getSortOrder() != null && userFilter.getSortColumn() != null) {
            if (userFilter.getSortOrder().equals("ASCENDING")) {
                queryBuffer.append(" order by user.").append(userFilter.getSortColumn()).append(" ASC");
            }
            else if (userFilter.getSortOrder().equals("DESCENDING")) {
                queryBuffer.append(" order by user.").append(userFilter.getSortColumn()).append(" DESC");
            }
        }

        Query query = this.entityManager.createQuery(queryBuffer.toString());
        query.setParameter("name", roleName);
        return query
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();
    }

    @Override
    public int getUsersWithoutRoleCount(String roleName, UserFilter userFilter) {
        StringBuffer queryBuffer = new StringBuffer("select count(user) from UserLite user where  user.id NOT IN (");
        queryBuffer.append(SecurityConstants.GET_USER_IDS_WITH_ROLE_QUERY);
        queryBuffer.append(")");

        queryBuffer.append(this.getUserFilterPredicateString("user",
            "", userFilter));

        Query query = this.entityManager.createQuery(queryBuffer.toString());
        query.setParameter("name", roleName);
        return ((Long)query.getSingleResult()).intValue();
    }

    @Override
    public int getUserCount(UserFilter userFilter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<User> userRoot = criteriaQuery.from(User.class);

        Predicate predicate = this.getUserFilterPredicate(builder, userFilter, userRoot);
        criteriaQuery.where(predicate);
        // select query
        criteriaQuery.select(builder.count(userRoot));

        // execute and get the result
        return entityManager.createQuery(criteriaQuery).getSingleResult().intValue();
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.security.dao.UserDao#getUser(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public User getUser(String username)
    {
        Query query = this.entityManager.createQuery("from User where username  = :name");
        query.setParameter("name", username);
        User result = null;
        List<User> results = query.getResultList();
        if (!results.isEmpty()){
            result = results.get(0);
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.security.dao.UserDao#getUsers()
     */
    @SuppressWarnings("unchecked")
    public List<User> getUsers()
    {
        return this.entityManager.createQuery("from User").getResultList();
    }

    @Override
    public List<User> getUsers(UserFilter userFilter, int limit, int offset) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
        Root<User> userRoot = criteriaQuery.from(User.class);
        Predicate predicate = this.getUserFilterPredicate(builder, userFilter, userRoot);
        criteriaQuery.where(predicate);
        criteriaQuery.select(userRoot);

        if(userFilter.getSortOrder() != null && userFilter.getSortColumn() != null) {
            if (userFilter.getSortOrder().equals("ASCENDING")) {
                criteriaQuery.orderBy(builder.asc(userRoot.get(userFilter.getSortColumn())));
            }
            else if (userFilter.getSortOrder().equals("DESCENDING")) {
                criteriaQuery.orderBy(builder.desc(userRoot.get(userFilter.getSortColumn())));
            }
        }

        return this.entityManager.createQuery(criteriaQuery)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.security.dao.UserDao#getUsers()
     */
    @SuppressWarnings("unchecked")
    public List<UserLite> getUserLites()
    {
        return this.entityManager.createQuery("from UserLite").getResultList();
    }

    @Override
    public List<UserLite> getUserLites(int limit, int offset) {
        return this.entityManager.createQuery("from UserLite")
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();
    }

    public void save(User user)
    {
        if(!this.entityManager.contains(user)) {
            user = entityManager.merge(user);
        }
        this.entityManager.persist(user);
    }

    /* (non-Javadoc)
     * @see org.ikasan.framework.security.dao.UserDao#delete(org.ikasan.framework.security.window.User)
     */
    public void delete(User user)
    {
        this.entityManager.remove(this.entityManager.contains(user) ? user : entityManager.merge(user));
        
    }

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.UserDao#getUserByUsernameLike(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUserByUsernameLike(String username)
	{
        Query query = this.entityManager.createQuery("from User where username LIKE :name");
		query.setParameter("name", username + '%');

        return query.getResultList();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.UserDao#getUserByFirstnameLike(java.lang.String)
	 */
	@Override
	public List<User> getUserByFirstnameLike(String firstname)
	{
        Query query = this.entityManager.createQuery("from User where firstName LIKE :name");
		query.setParameter("name", firstname + '%');

        return query.getResultList();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.UserDao#getUserBySurnameLike(java.lang.String)
	 */
	@Override
	public List<User> getUserBySurnameLike(String surname)
	{
        Query query = this.entityManager.createQuery("from User where surname LIKE :name");
		query.setParameter("name", surname + '%');

        return query.getResultList();
	}


    /**
     *  Generates a Predicate based on the provided UserFilter criteria.
     *
     *  @param builder the CriteriaBuilder used to construct the Predicate
     *  @param userFilter the UserFilter object containing filter criteria
     *  @param userRoot the Root entity for the User entity
     *
     *  @return a Predicate representing the combined filter criteria based on the UserFilter object
     */
    private Predicate getUserFilterPredicate(CriteriaBuilder builder
        , UserFilter userFilter, Root userRoot) {
        Predicate predicate = builder.conjunction();
        if (userFilter.getNameFilter() != null && !userFilter.getNameFilter().isEmpty()) {
            predicate = builder.and(predicate, builder.like(userRoot.get("firstName")
                , "%" + userFilter.getNameFilter() + "%"));
        }
        if (userFilter.getLastNameFilter() != null && !userFilter.getLastNameFilter().isEmpty()) {
            predicate = builder.and(predicate, builder.like(userRoot.get("surname")
                , "%" + userFilter.getLastNameFilter() + "%"));
        }
        if (userFilter.getUsernameFilter() != null && !userFilter.getUsernameFilter().isEmpty()) {
            predicate = builder.and(predicate, builder.like(userRoot.get("username")
                , "%" + userFilter.getUsernameFilter() + "%"));
        }
        if (userFilter.getEmailFilter() != null && !userFilter.getEmailFilter().isEmpty()) {
            predicate = builder.and(predicate, builder.like(userRoot.get("email")
                , "%" + userFilter.getEmailFilter() + "%"));
        }
        if (userFilter.getDepartmentFilter() != null && !userFilter.getDepartmentFilter().isEmpty()) {
            predicate = builder.and(predicate, builder.like(userRoot.get("department")
                , "%" + userFilter.getDepartmentFilter() + "%"));
        }
        return predicate;
    }

    /**
     * Generates a String representing the filter criteria based on the provided UserFilter object.
     *
     * @param queryBase the base query string to append filter criteria to
     * @param userFilter the UserFilter object containing filter criteria
     * @return a StringBuffer representing the updated query string with filter criteria applied
     */
    private StringBuffer getUserFilterPredicateString(String queryBase
        , UserFilter userFilter) {
        return this.getUserFilterPredicateString("u", queryBase, userFilter);
    }

    /**
     * Generates a String representing the filter criteria based on the provided UserFilter object.
     *
     * @param userReference the reference to the user in the query
     * @param queryBase the base query string to append filter criteria to
     * @param userFilter the UserFilter object containing filter criteria
     * @return a StringBuffer representing the updated query string with filter criteria applied
     */
    private StringBuffer getUserFilterPredicateString(String userReference, String queryBase
        , UserFilter userFilter) {
        StringBuffer queryBuffer = new StringBuffer(queryBase);
        if (userFilter.getNameFilter() != null && !userFilter.getNameFilter().isEmpty()) {
            queryBuffer.append("AND ").append(userReference).append(".firstName LIKE '%").append(userFilter.getNameFilter()).append("%'");
        }
        if (userFilter.getLastNameFilter() != null && !userFilter.getLastNameFilter().isEmpty()) {
            queryBuffer.append("AND ").append(userReference).append(".surname LIKE '%").append(userFilter.getLastNameFilter()).append("%'");
        }
        if (userFilter.getUsernameFilter() != null && !userFilter.getUsernameFilter().isEmpty()) {
            queryBuffer.append("AND ").append(userReference).append(".username LIKE '%").append(userFilter.getUsernameFilter()).append("%'");
        }
        if (userFilter.getEmailFilter() != null && !userFilter.getEmailFilter().isEmpty()) {
            queryBuffer.append("AND ").append(userReference).append(".email LIKE '%").append(userFilter.getEmailFilter()).append("%'");
        }
        if (userFilter.getDepartmentFilter() != null && !userFilter.getDepartmentFilter().isEmpty()) {
            queryBuffer.append("AND ").append(userReference).append(".department LIKE '%").append(userFilter.getDepartmentFilter()).append("%'");
        }

        return queryBuffer;
    }
}
