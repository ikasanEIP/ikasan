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
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.*;
import org.ikasan.spec.security.model.*;
import org.ikasan.spec.security.dao.SecurityDao;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CMI2 Development Team
 *
 */
public class HibernateSecurityDaoImpl implements SecurityDao
{
    @PersistenceContext(unitName = "security")
    protected EntityManager entityManager;

    /**
     * Creates and returns a new instance of IkasanPrincipal.
     *
     * @return a new instance of IkasanPrincipal
     */
    @Override
    public IkasanPrincipal createPrincipal() {
        return new HibernateIkasanPrincipalImpl();
    }

    @Override
    public Role createRole() {
        return new HibernateRoleImpl();
    }

    @Override
    public Policy createPolicy() {
        return new HibernatePolicyImpl();
    }

    @Override
    public RoleModule createRoleModule() {
        return new HibernateRoleModuleImpl();
    }

    @Override
    public RoleJobPlan createRoleJobPlan() {
        return new HibernateRoleJobPlanImpl();
    }

    @Override
    public AuthenticationMethod createAuthenticationMethod() {
        return new HibernateAuthenticationMethodImpl();
    }

    /*
 	 * (non-Javadoc)
 	 * @see org.ikasan.security.dao.SecurityDao#saveOrUpdateRole(org.ikasan.security.window.Role)
 	 */
    @Override
    public void saveOrUpdateRole(Role role)
    {
        if(!this.entityManager.contains(role)) {
            role = entityManager.merge(role);
        }
        role.setUpdatedDateTime(new Date());
    	this.entityManager.persist(role);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#saveOrUpdatePolicy(org.ikasan.security.window.Policy)
     */
    @Override
    public void saveOrUpdatePolicy(Policy policy)
    {
        if(!this.entityManager.contains(policy)) {
            policy = entityManager.merge(policy);
        }
        policy.setUpdatedDateTime(new Date());
    	this.entityManager.persist(policy);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#saveOrUpdatePrincipal(org.ikasan.security.window.IkasanPrincipal)
     */
    @Override
    public void saveOrUpdatePrincipal(IkasanPrincipal principal)
    {
        if(!this.entityManager.contains(principal)) {
            principal = entityManager.merge(principal);
        }
        principal.setUpdatedDateTime(new Date());
    	this.entityManager.persist(principal);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#getPrincipalByName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	@Override 
    public IkasanPrincipal getPrincipalByName(String name)
    {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<HibernateIkasanPrincipalImpl> criteriaQuery = builder.createQuery(HibernateIkasanPrincipalImpl.class);

        Root<HibernateIkasanPrincipalImpl> root = criteriaQuery.from(HibernateIkasanPrincipalImpl.class);

        criteriaQuery.select(root)
            .where(builder.equal(root.get("name"),name));

        TypedQuery<HibernateIkasanPrincipalImpl> query = this.entityManager.createQuery(criteriaQuery);
        List<HibernateIkasanPrincipalImpl> results = query.getResultList();

        if(results == null || results.size() == 0)
        {
            return null;
        }

        return results.get(0);

    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#getAllPolicies()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Policy> getAllPolicies()
    {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<HibernatePolicyImpl> criteriaQuery = builder.createQuery(HibernatePolicyImpl.class);

        Root<HibernatePolicyImpl> root = criteriaQuery.from(HibernatePolicyImpl.class);

        criteriaQuery.select(root);

        TypedQuery<HibernatePolicyImpl> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList().stream()
            .map(p -> (Policy)p)
            .collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#getAllRoles()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Role> getAllRoles()
    {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<HibernateRoleImpl> criteriaQuery = builder.createQuery(HibernateRoleImpl.class);

        Root<HibernateRoleImpl> root = criteriaQuery.from(HibernateRoleImpl.class);

        criteriaQuery.select(root);

        TypedQuery<HibernateRoleImpl> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList().stream()
            .map(p -> (Role)p)
            .collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#getAllPrincipals()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<IkasanPrincipal> getAllPrincipals()
    {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<HibernateIkasanPrincipalImpl> criteriaQuery = builder.createQuery(HibernateIkasanPrincipalImpl.class);

        Root<HibernateIkasanPrincipalImpl> root = criteriaQuery.from(HibernateIkasanPrincipalImpl.class);

        criteriaQuery.select(root);

        TypedQuery<HibernateIkasanPrincipalImpl> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList().stream()
            .map(p -> (IkasanPrincipal)p)
            .collect(Collectors.toList());
    }

	@Override
	public List<IkasanPrincipalLite> getAllPrincipalLites()
	{
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<HibernateIkasanPrincipalLiteImpl> criteriaQuery = builder.createQuery(HibernateIkasanPrincipalLiteImpl.class);

        Root<HibernateIkasanPrincipalLiteImpl> root = criteriaQuery.from(HibernateIkasanPrincipalLiteImpl.class);

        criteriaQuery.select(root);

        TypedQuery<HibernateIkasanPrincipalLiteImpl> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList().stream()
            .map(p -> (IkasanPrincipalLite)p)
            .collect(Collectors.toList());
    }

    @Override
    public List<IkasanPrincipal> getPrincipals(IkasanPrincipalFilter filter, int limit, int offset) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<HibernateIkasanPrincipalImpl> criteriaQuery = builder.createQuery(HibernateIkasanPrincipalImpl.class);

        Root<HibernateIkasanPrincipalImpl> root = criteriaQuery.from(HibernateIkasanPrincipalImpl.class);

        criteriaQuery.select(root);
        criteriaQuery.where(this.getPrincipalFilterPredicate(builder, filter, root));

        if(filter.getSortOrder() != null && filter.getSortColumn() != null) {
            if (filter.getSortOrder().equals("ASCENDING")) {
                criteriaQuery.orderBy(builder.asc(root.get(filter.getSortColumn())));
            }
            else if (filter.getSortOrder().equals("DESCENDING")) {
                criteriaQuery.orderBy(builder.desc(root.get(filter.getSortColumn())));
            }
        }

        return this.entityManager.createQuery(criteriaQuery)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList().stream()
            .map(p -> (IkasanPrincipal)p)
            .collect(Collectors.toList());
    }

    @Override
    public List<IkasanPrincipalLite> getPrincipalLites(IkasanPrincipalFilter filter, int limit, int offset) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<HibernateIkasanPrincipalLiteImpl> criteriaQuery = builder.createQuery(HibernateIkasanPrincipalLiteImpl.class);

        Root<HibernateIkasanPrincipalLiteImpl> root = criteriaQuery.from(HibernateIkasanPrincipalLiteImpl.class);

        criteriaQuery.select(root);
        criteriaQuery.where(this.getPrincipalFilterPredicate(builder, filter, root));

        if(filter.getSortOrder() != null && filter.getSortColumn() != null) {
            if (filter.getSortOrder().equals("ASCENDING")) {
                criteriaQuery.orderBy(builder.asc(root.get(filter.getSortColumn())));
            }
            else if (filter.getSortOrder().equals("DESCENDING")) {
                criteriaQuery.orderBy(builder.desc(root.get(filter.getSortColumn())));
            }
        }

        return this.entityManager.createQuery(criteriaQuery)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList().stream()
            .map(p -> (IkasanPrincipalLite)p)
            .collect(Collectors.toList());
    }

    @Override
    public int getPrincipalCount(IkasanPrincipalFilter filter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<HibernateIkasanPrincipalImpl> userRoot = criteriaQuery.from(HibernateIkasanPrincipalImpl.class);

        Predicate predicate = this.getPrincipalFilterPredicate(builder, filter, userRoot);
        criteriaQuery.where(predicate);
        // select query
        criteriaQuery.select(builder.count(userRoot));

        // execute and get the result
        return entityManager.createQuery(criteriaQuery).getSingleResult().intValue();
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#getPolicyByName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	@Override
    public Policy getPolicyByName(String name)
    {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<HibernatePolicyImpl> criteriaQuery = builder.createQuery(HibernatePolicyImpl.class);

        Root<HibernatePolicyImpl> root = criteriaQuery.from(HibernatePolicyImpl.class);

        criteriaQuery.select(root)
            .where(builder.equal(root.get("name"),name));

        TypedQuery<HibernatePolicyImpl> query = this.entityManager.createQuery(criteriaQuery);
        List<HibernatePolicyImpl> results = query.getResultList();

        if(results == null || results.size() == 0)
        {
            return null;
        }

        return results.get(0);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#getRoleByName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	@Override
    public Role getRoleByName(String name)
    {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<HibernateRoleImpl> criteriaQuery = builder.createQuery(HibernateRoleImpl.class);

        Root<HibernateRoleImpl> root = criteriaQuery.from(HibernateRoleImpl.class);

        criteriaQuery.select(root)
            .where(builder.equal(root.get("name"),name));

        TypedQuery<HibernateRoleImpl> query = this.entityManager.createQuery(criteriaQuery);
        List<HibernateRoleImpl> results = query.getResultList();

        if(results == null || results.size() == 0)
        {
            return null;
        }

        return results.get(0);
    }

	/*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#getRoleByName(java.lang.String)
     */
	@SuppressWarnings("unchecked")
	@Override
	public Role getRoleById(Long id)
	{
		Role role = this.entityManager.find(HibernateRoleImpl.class, id);
		return role;
	}

	/*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#getRoleByName(java.lang.String)
     */
	@SuppressWarnings("unchecked")
	@Override
	public Policy getPolicyById(Long id)
	{
        Policy policy = this.entityManager.find(HibernatePolicyImpl.class,id);
		return policy;
	}

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#deleteRole(org.ikasan.security.window.Role)
     */
    @Override
    public void deleteRole(Role role)
    {
        this.entityManager.createQuery("delete from HibernatePrincipalRoleImpl where id.roleId = :id")
            .setParameter("id", role.getId()).executeUpdate();
        this.entityManager.remove(this.entityManager.contains(role) ? role : entityManager.merge(role));
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#deletePolicy(org.ikasan.security.window.Policy)
     */
    @Override
    public void deletePolicy(Policy policy)
    {
        this.entityManager.remove(this.entityManager.contains(policy) ? policy : entityManager.merge(policy));
    }

    @Override
    public void deleteRoleModule(RoleModule roleModule)
    {
        this.entityManager.remove(this.entityManager.contains(roleModule) ? roleModule : entityManager.merge(roleModule));
    }

    @Override
    public void saveRoleModule(RoleModule roleModule) {
        this.entityManager.remove(this.entityManager.contains(roleModule) ? roleModule : entityManager.merge(roleModule));
    }

    @Override
    public void deleteRoleJobPlan(RoleJobPlan roleJobPlan) {
        this.entityManager.remove(this.entityManager.contains(roleJobPlan) ? roleJobPlan : entityManager.merge(roleJobPlan));
    }

    @Override
    public void saveRoleJobPlan(RoleJobPlan roleJobPlan) {
        if(!this.entityManager.contains(roleJobPlan)) {
            roleJobPlan = entityManager.merge(roleJobPlan);
        }
        this.entityManager.persist(roleJobPlan);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#deletePrincipal(org.ikasan.security.window.IkasanPrincipal)
     */
    @Override
    public void deletePrincipal(IkasanPrincipal principal)
    {
        this.entityManager.remove(this.entityManager.contains(principal) ? principal : entityManager.merge(principal));
    }

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#saveOrUpdateAuthenticationMethod(org.ikasan.security.window.AuthenticationMethod)
	 */
	@Override
	public void saveOrUpdateAuthenticationMethod(AuthenticationMethod authenticationMethod)
	{
        if(!this.entityManager.contains(authenticationMethod)) {
            authenticationMethod = entityManager.merge(authenticationMethod);
        }
		this.entityManager.persist(authenticationMethod);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAuthenticationMethod(java.lang.Long)
	 */
	@Override
	public AuthenticationMethod getAuthenticationMethod(Long id)
	{
		AuthenticationMethod authenticationMethod = this.entityManager.find(HibernateAuthenticationMethodImpl.class,id);
        return authenticationMethod;
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAuthenticationMethod(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AuthenticationMethod> getAuthenticationMethods()
	{
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<HibernateAuthenticationMethodImpl> criteriaQuery = builder.createQuery(HibernateAuthenticationMethodImpl.class);

        Root<HibernateAuthenticationMethodImpl> root = criteriaQuery.from(HibernateAuthenticationMethodImpl.class);

        criteriaQuery.select(root)
            .orderBy(builder.asc(root.get("order")));

        TypedQuery<HibernateAuthenticationMethodImpl> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList().stream()
            .map(am -> (AuthenticationMethod)am)
            .collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAllPrincipalsWithRole(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IkasanPrincipal> getAllPrincipalsWithRole(String roleName)
	{
        Query query = this.entityManager.createQuery(SecurityConstants.GET_IKASAN_PRINCIPLE_WITH_ROLE_QUERY);
        query.setParameter("name", roleName);
        return query.getResultList();
    }

    @Override
    public List<IkasanPrincipalLite> getAllPrincipalsWithRole(String roleName, IkasanPrincipalFilter filter, int limit, int offset) {
        StringBuffer queryBuffer = this.createPrincipalByRoleFilterPredicateString(
                SecurityConstants.GET_IKASAN_PRINCIPLE_LITE_WITH_ROLE_QUERY, filter);

        if(filter.getSortOrder() != null && filter.getSortColumn() != null) {
            if (filter.getSortOrder().equals("ASCENDING")) {
                queryBuffer.append(" order by p.").append(filter.getSortColumn()).append(" ASC");
            }
            else if (filter.getSortOrder().equals("DESCENDING")) {
                queryBuffer.append(" order by p.").append(filter.getSortColumn()).append(" DESC");
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
    public List<IkasanPrincipalLite> getAllPrincipalsWithoutRole(String roleName, IkasanPrincipalFilter filter, int limit, int offset) {
        StringBuffer queryBuffer = new StringBuffer("select principal from HibernateIkasanPrincipalLiteImpl principal where  principal.id NOT IN (");
        queryBuffer.append(SecurityConstants.GET_IKASAN_PRINCIPAL_LITE_IDS_WITH_ROLE_QUERY);
        if(filter.getTypeFilter() != null && !filter.getTypeFilter().isEmpty()) {
            queryBuffer.append(" AND p.type LIKE '%").append(filter.getTypeFilter()).append("%'");
        }
        queryBuffer.append(")");

        queryBuffer.append(this.createPrincipalByRoleFilterPredicateString("principal",
            "", filter));

        if(filter.getSortOrder() != null && filter.getSortColumn() != null) {
            if (filter.getSortOrder().equals("ASCENDING")) {
                queryBuffer.append(" order by principal.").append(filter.getSortColumn()).append(" ASC");
            }
            else if (filter.getSortOrder().equals("DESCENDING")) {
                queryBuffer.append(" order by principal.").append(filter.getSortColumn()).append(" DESC");
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
    public int getPrincipalsWithRoleCount(String roleName, IkasanPrincipalFilter filter) {
        StringBuffer queryBuffer = this.createPrincipalByRoleFilterPredicateString(
            SecurityConstants.GET_IKASAN_PRINCIPLE_LITE_WITH_ROLE_QUERY_COUNT, filter);

        Query query = this.entityManager.createQuery(queryBuffer.toString());
        query.setParameter("name", roleName);
        return ((Long)query.getSingleResult()).intValue();
    }

    @Override
    public int getPrincipalsWithoutRoleCount(String roleName, IkasanPrincipalFilter filter) {
        StringBuffer queryBuffer = new StringBuffer("select count(principal) from HibernateIkasanPrincipalLiteImpl principal where  principal.id NOT IN (");
        queryBuffer.append(SecurityConstants.GET_IKASAN_PRINCIPAL_LITE_IDS_WITH_ROLE_QUERY);
        if(filter.getTypeFilter() != null && !filter.getTypeFilter().isEmpty()) {
            queryBuffer.append(" AND p.type LIKE '%").append(filter.getTypeFilter()).append("%'");
        }
        queryBuffer.append(")");

        queryBuffer.append(this.createPrincipalByRoleFilterPredicateString("principal",
            "", filter));

        Query query = this.entityManager.createQuery(queryBuffer.toString());
        query.setParameter("name", roleName);
        return ((Long)query.getSingleResult()).intValue();
    }

    /* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getPrincipalsByName(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IkasanPrincipal> getPrincipalsByRoleNames(List<String> names)
	{
        Query query = this.entityManager.createQuery(SecurityConstants.GET_IKASAN_PRINCIPLE_WITH_ROLE_IN_QUERY);
        query.setParameter("name", names);
        return (List<IkasanPrincipal>) query.getResultList();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getPrincipalByNameLike(java.lang.String)
	 */
	@Override
	public List<IkasanPrincipal> getPrincipalByNameLike(String name)
	{
		@SuppressWarnings("unchecked")
		Query query = this.entityManager.createQuery("from HibernateIkasanPrincipalImpl where name LIKE :name");
        query.setParameter("name", name + '%');

        return query.getResultList();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAllPolicyLinkTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PolicyLinkType> getAllPolicyLinkTypes()
	{
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<HibernatePolicyLinkTypeImpl> criteriaQuery = builder.createQuery(HibernatePolicyLinkTypeImpl.class);

        Root<HibernatePolicyLinkTypeImpl> root = criteriaQuery.from(HibernatePolicyLinkTypeImpl.class);

        criteriaQuery.select(root);

        TypedQuery<HibernatePolicyLinkTypeImpl> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList().stream()
            .map(p -> (PolicyLinkType)p)
            .collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getPolicyByNameLike(java.lang.String)
	 */
	@Override
	public List<Policy> getPolicyByNameLike(String name)
	{
		@SuppressWarnings("unchecked")
		Query query =  this.entityManager.createQuery("from HibernatePolicyImpl where name LIKE :name");
        query.setParameter("name", name + '%');

        return query.getResultList();
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getRoleByNameLike(java.lang.String)
	 */
	@Override
	public List<Role> getRoleByNameLike(String name)
	{
		Query query = this.entityManager.createQuery("from HibernateRoleImpl where name LIKE :name");
		query.setParameter("name", name + '%');

        return query.getResultList();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#saveOrUpdatePolicyLink(org.ikasan.security.window.PolicyLink)
	 */
	@Override
	public void saveOrUpdatePolicyLink(PolicyLink policyLink)
	{
        if(!this.entityManager.contains(policyLink)) {
            policyLink = entityManager.merge(policyLink);
        }
		this.entityManager.persist(policyLink);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#saveOrUpdatePolicyLinkType(org.ikasan.security.window.PolicyLinkType)
	 */
	@Override
	public void saveOrUpdatePolicyLinkType(PolicyLinkType policyLinkType)
	{
        if(!this.entityManager.contains(policyLinkType)) {
            policyLinkType = entityManager.merge(policyLinkType);
        }
		this.entityManager.persist(policyLinkType);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#deletePolicyLink(org.ikasan.security.window.PolicyLink)
	 */
	@Override
	public void deletePolicyLink(PolicyLink policyLink)
	{
		this.entityManager.remove(this.entityManager.contains(policyLink) ? policyLink : entityManager.merge(policyLink));
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAllPoliciessWithRole(java.lang.String)
	 */
	@Override
	public List<Policy> getAllPoliciesWithRole(String roleName)
	{
        Query query = this.entityManager.createQuery(SecurityConstants.GET_POLICY_WITH_ROLE_QUERY);
        query.setParameter("name", roleName);
        return query.getResultList();
    }

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#deleteAuthenticationMethod(org.ikasan.security.window.AuthenticationMethod)
	 */
	@Override
	public void deleteAuthenticationMethod(AuthenticationMethod authenticationMethod)
	{
		this.entityManager.remove(this.entityManager.contains(authenticationMethod) ? authenticationMethod : entityManager.merge(authenticationMethod));
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getNumberOfAuthenticationMethods()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public long getNumberOfAuthenticationMethods()
	{
        Query query = this.entityManager.createQuery("select count(*) from HibernateAuthenticationMethodImpl");
        return (Long)query.getResultList().get(0);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAuthenticationMethodByOrder(int)
	 */
	@Override
	public AuthenticationMethod getAuthenticationMethodByOrder(long order)
	{
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<HibernateAuthenticationMethodImpl> criteriaQuery = builder.createQuery(HibernateAuthenticationMethodImpl.class);

        Root<HibernateAuthenticationMethodImpl> root = criteriaQuery.from(HibernateAuthenticationMethodImpl.class);

        criteriaQuery.select(root)
            .where(builder.equal(root.get("order"),order));

        TypedQuery<HibernateAuthenticationMethodImpl> query = this.entityManager.createQuery(criteriaQuery);
        List<HibernateAuthenticationMethodImpl> results = query.getResultList();
        if(results == null || results.size() == 0)
        {
            return null;
        }
        return results.get(0);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getUsersAssociatedWithPrincipal(long)
	 */
	@Override
	public List<User> getUsersAssociatedWithPrincipal(final long principalId)
	{
        Query query = this.entityManager.createQuery(SecurityConstants.GET_USERS_BY_PRINCIPAL_QUERY);
        query.setParameter(SecurityConstants.PRINCIPAL_ID, principalId);
        return query.getResultList();
	}


    @Override
    public List<RoleJobPlan> getRoleJobPlansByJobPlanName(String jonPlanName) {
        Query query = this.entityManager.createQuery(SecurityConstants.GET_ROLE_JOB_PLANS_BY_ROLE_QUERY);
        query.setParameter("name", jonPlanName);
        return query.getResultList();
    }

    /**
     * Generates a Predicate for filtering IkasanPrincipals based on the given criteria.
     *
     * @param builder the CriteriaBuilder used to construct the query predicates
     * @param filter the IkasanPrincipalFilter containing the criteria to filter by
     * @param principalRoot the Root entity representing the IkasanPrincipals
     * @return the Predicate representing the filter based on the given criteria
     */
    private Predicate getPrincipalFilterPredicate(CriteriaBuilder builder
        , IkasanPrincipalFilter filter, Root principalRoot) {
        Predicate predicate = builder.conjunction();
        if (filter.getNameFilter() != null) {
            predicate = builder.and(predicate, builder.like(builder.lower(principalRoot.get("name"))
                , "%" + filter.getNameFilter().toLowerCase() + "%"));
        }
        if (filter.getTypeFilter() != null) {
            predicate = builder.and(predicate, builder.like(builder.lower(principalRoot.get("type"))
                , "%" + filter.getTypeFilter().toLowerCase() + "%"));
        }
        if (filter.getDescriptionFilter() != null) {
            predicate = builder.and(predicate, builder.like(builder.lower(principalRoot.get("description"))
                , "%" + filter.getDescriptionFilter().toLowerCase() + "%"));
        }

        return predicate;
    }

    /**
     * Creates a query string for filtering principals based on the provided IkasanPrincipalFilter.
     *
     * @param queryBase the base query string to be extended
     * @param filter the IkasanPrincipalFilter containing the filter parameters
     * @return a StringBuffer representing the extended query string for filtering principals
     */
    private StringBuffer createPrincipalByRoleFilterPredicateString(String queryBase, IkasanPrincipalFilter filter) {
        return this.createPrincipalByRoleFilterPredicateString("p", queryBase, filter);
    }

    /**
     * Constructs a SQL query string for filtering Principals based on the provided criteria.
     *
     * @param principalName the name of the Principal table in the database
     * @param queryBase the base query string to which the filter conditions will be appended
     * @param filter the IkasanPrincipalFilter object containing the filtering criteria
     * @return a StringBuffer containing the complete SQL query string with the applied filters
     */
    private StringBuffer createPrincipalByRoleFilterPredicateString(String principalName, String queryBase
        , IkasanPrincipalFilter filter) {
        StringBuffer queryBuffer = new StringBuffer(queryBase);
        if(filter.getNameFilter() != null && !filter.getNameFilter().isEmpty()) {
            queryBuffer.append(String.format(" AND lower(%s.name) LIKE ", principalName)).append("'%").append(filter.getNameFilter().toLowerCase()).append("%'");
        }
        if(filter.getTypeFilter() != null && !filter.getTypeFilter().isEmpty()) {
            queryBuffer.append(String.format(" AND lower(%s.type) LIKE ", principalName)).append("'%").append(filter.getTypeFilter().toLowerCase()).append("%'");
        }
        if(filter.getDescriptionFilter() != null && !filter.getDescriptionFilter().isEmpty()) {
            queryBuffer.append(String.format(" AND lower(%s.description) LIKE ", principalName)).append("'%").append(filter.getDescriptionFilter().toLowerCase()).append("%'");
        }

        return queryBuffer;
    }
}
