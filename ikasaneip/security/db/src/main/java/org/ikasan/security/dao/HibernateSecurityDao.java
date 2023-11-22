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
import jakarta.persistence.criteria.Root;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author CMI2 Development Team
 *
 */
@Transactional
public class HibernateSecurityDao implements SecurityDao
{
    @PersistenceContext(unitName = "security")
    private EntityManager entityManager;
 	/*
 	 * (non-Javadoc)
 	 * @see org.ikasan.security.dao.SecurityDao#saveOrUpdateRole(org.ikasan.security.window.Role)
 	 */
    @Override
    public void saveOrUpdateRole(Role role)
    {
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

        CriteriaQuery<IkasanPrincipal> criteriaQuery = builder.createQuery(IkasanPrincipal.class);

        Root<IkasanPrincipal> root = criteriaQuery.from(IkasanPrincipal.class);

        criteriaQuery.select(root)
            .where(builder.equal(root.get("name"),name));

        TypedQuery<IkasanPrincipal> query = this.entityManager.createQuery(criteriaQuery);
        List<IkasanPrincipal> results = query.getResultList();

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

        CriteriaQuery<Policy> criteriaQuery = builder.createQuery(Policy.class);

        Root<Policy> root = criteriaQuery.from(Policy.class);

        criteriaQuery.select(root);

        TypedQuery<Policy> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList();
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

        CriteriaQuery<Role> criteriaQuery = builder.createQuery(Role.class);

        Root<Role> root = criteriaQuery.from(Role.class);

        criteriaQuery.select(root);

        TypedQuery<Role> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList();
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

        CriteriaQuery<IkasanPrincipal> criteriaQuery = builder.createQuery(IkasanPrincipal.class);

        Root<IkasanPrincipal> root = criteriaQuery.from(IkasanPrincipal.class);

        criteriaQuery.select(root);

        TypedQuery<IkasanPrincipal> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

	@Override
	public List<IkasanPrincipalLite> getAllPrincipalLites()
	{
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<IkasanPrincipalLite> criteriaQuery = builder.createQuery(IkasanPrincipalLite.class);

        Root<IkasanPrincipalLite> root = criteriaQuery.from(IkasanPrincipalLite.class);

        criteriaQuery.select(root);

        TypedQuery<IkasanPrincipalLite> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList();
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

        CriteriaQuery<Policy> criteriaQuery = builder.createQuery(Policy.class);

        Root<Policy> root = criteriaQuery.from(Policy.class);

        criteriaQuery.select(root)
            .where(builder.equal(root.get("name"),name));

        TypedQuery<Policy> query = this.entityManager.createQuery(criteriaQuery);
        List<Policy> results = query.getResultList();

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

        CriteriaQuery<Role> criteriaQuery = builder.createQuery(Role.class);

        Root<Role> root = criteriaQuery.from(Role.class);

        criteriaQuery.select(root)
            .where(builder.equal(root.get("name"),name));

        TypedQuery<Role> query = this.entityManager.createQuery(criteriaQuery);
        List<Role> results = query.getResultList();

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
		Role role = this.entityManager.find(Role.class, id);
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
        Policy policy = this.entityManager.find(Policy.class,id);
		return policy;
	}

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#deleteRole(org.ikasan.security.window.Role)
     */
    @Override
    public void deleteRole(Role role)
    {
        this.entityManager.remove(role);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#deletePolicy(org.ikasan.security.window.Policy)
     */
    @Override
    public void deletePolicy(Policy policy)
    {
        this.entityManager.remove(policy);
    }

    @Override
    public void deleteRoleModule(RoleModule roleModule)
    {
        this.entityManager.remove(roleModule);
    }

    @Override
    public void saveRoleModule(RoleModule roleModule) {
        this.entityManager.remove(roleModule);
    }

    @Override
    public void deleteRoleJobPlan(RoleJobPlan roleJobPlan) {
        this.entityManager.remove(roleJobPlan);
    }

    @Override
    public void saveRoleJobPlan(RoleJobPlan roleJobPlan) {
        this.entityManager.persist(roleJobPlan);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#deletePrincipal(org.ikasan.security.window.IkasanPrincipal)
     */
    @Override
    public void deletePrincipal(IkasanPrincipal principal)
    {
        this.entityManager.remove(principal);
    }

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#saveOrUpdateAuthenticationMethod(org.ikasan.security.window.AuthenticationMethod)
	 */
	@Override
	public void saveOrUpdateAuthenticationMethod(AuthenticationMethod authenticationMethod)
	{
		this.entityManager.persist(authenticationMethod);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAuthenticationMethod(java.lang.Long)
	 */
	@Override
	public AuthenticationMethod getAuthenticationMethod(Long id)
	{
		AuthenticationMethod authenticationMethod = this.entityManager.find(AuthenticationMethod.class,id);
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

        CriteriaQuery<AuthenticationMethod> criteriaQuery = builder.createQuery(AuthenticationMethod.class);

        Root<AuthenticationMethod> root = criteriaQuery.from(AuthenticationMethod.class);

        criteriaQuery.select(root)
            .orderBy(builder.asc(root.get("order")));

        TypedQuery<AuthenticationMethod> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList();
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
		Query query = this.entityManager.createQuery("from IkasanPrincipal where name LIKE :name");
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

        CriteriaQuery<PolicyLinkType> criteriaQuery = builder.createQuery(PolicyLinkType.class);

        Root<PolicyLinkType> root = criteriaQuery.from(PolicyLinkType.class);

        criteriaQuery.select(root);

        TypedQuery<PolicyLinkType> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getPolicyByNameLike(java.lang.String)
	 */
	@Override
	public List<Policy> getPolicyByNameLike(String name)
	{
		@SuppressWarnings("unchecked")
		Query query =  this.entityManager.createQuery("from Policy where name LIKE :name");
        query.setParameter("name", name + '%');

        return query.getResultList();
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getRoleByNameLike(java.lang.String)
	 */
	@Override
	public List<Role> getRoleByNameLike(String name)
	{
		Query query = this.entityManager.createQuery("from Role where name LIKE :name");
		query.setParameter("name", name + '%');

        return query.getResultList();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#saveOrUpdatePolicyLink(org.ikasan.security.window.PolicyLink)
	 */
	@Override
	public void saveOrUpdatePolicyLink(PolicyLink policyLink)
	{
		this.entityManager.persist(policyLink);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#saveOrUpdatePolicyLinkType(org.ikasan.security.window.PolicyLinkType)
	 */
	@Override
	public void saveOrUpdatePolicyLinkType(PolicyLinkType policyLinkType)
	{
		this.entityManager.persist(policyLinkType);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#deletePolicyLink(org.ikasan.security.window.PolicyLink)
	 */
	@Override
	public void deletePolicyLink(PolicyLink policyLink)
	{
		this.entityManager.remove(policyLink);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAllPoliciessWithRole(java.lang.String)
	 */
	@Override
	public List<Policy> getAllPoliciesWithRole(String roleName)
	{
        Query query = this.entityManager.createQuery(SecurityConstants.GET_POLICY_WITH_ROLE_QUERY);
        query.setParameter("name", roleName);
        return (List<Policy>) query.getResultList();
    }

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#deleteAuthenticationMethod(org.ikasan.security.window.AuthenticationMethod)
	 */
	@Override
	public void deleteAuthenticationMethod(AuthenticationMethod authenticationMethod)
	{
		this.entityManager.remove(authenticationMethod);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getNumberOfAuthenticationMethods()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public long getNumberOfAuthenticationMethods()
	{
        Query query = this.entityManager.createQuery("select count(*) from AuthenticationMethod");
        return (Long)query.getResultList().get(0);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAuthenticationMethodByOrder(int)
	 */
	@Override
	public AuthenticationMethod getAuthenticationMethodByOrder(long order)
	{
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<AuthenticationMethod> criteriaQuery = builder.createQuery(AuthenticationMethod.class);

        Root<AuthenticationMethod> root = criteriaQuery.from(AuthenticationMethod.class);

        criteriaQuery.select(root)
            .where(builder.equal(root.get("order"),order));

        TypedQuery<AuthenticationMethod> query = this.entityManager.createQuery(criteriaQuery);
        List<AuthenticationMethod> results = query.getResultList();
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


}
