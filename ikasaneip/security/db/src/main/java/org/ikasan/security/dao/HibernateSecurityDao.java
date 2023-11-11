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

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.*;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

/**
 * @author CMI2 Development Team
 *
 */
public class HibernateSecurityDao extends HibernateDaoSupport implements SecurityDao
{

 	/*
 	 * (non-Javadoc)
 	 * @see org.ikasan.security.dao.SecurityDao#saveOrUpdateRole(org.ikasan.security.window.Role)
 	 */
    @Override
    public void saveOrUpdateRole(Role role)
    {
    	role.setUpdatedDateTime(new Date());
    	this.getHibernateTemplate().saveOrUpdate(role);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#saveOrUpdatePolicy(org.ikasan.security.window.Policy)
     */
    @Override
    public void saveOrUpdatePolicy(Policy policy)
    {
    	policy.setUpdatedDateTime(new Date());
    	this.getHibernateTemplate().saveOrUpdate(policy);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#saveOrUpdatePrincipal(org.ikasan.security.window.IkasanPrincipal)
     */
    @Override
    public void saveOrUpdatePrincipal(IkasanPrincipal principal)
    {
    	principal.setUpdatedDateTime(new Date());
    	this.getHibernateTemplate().saveOrUpdate(principal);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#getPrincipalByName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	@Override 
    public IkasanPrincipal getPrincipalByName(String name)
    {
        return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<IkasanPrincipal> criteriaQuery = builder.createQuery(IkasanPrincipal.class);

            Root<IkasanPrincipal> root = criteriaQuery.from(IkasanPrincipal.class);

            criteriaQuery.select(root)
                .where(builder.equal(root.get("name"),name));

            org.hibernate.query.Query<IkasanPrincipal> query = session.createQuery(criteriaQuery);
            List<IkasanPrincipal> results = query.getResultList();

            if(results == null || results.size() == 0)
            {
                return null;
            }

            return results.get(0);

        });
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#getAllPolicies()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Policy> getAllPolicies()
    {
        return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Policy> criteriaQuery = builder.createQuery(Policy.class);

            Root<Policy> root = criteriaQuery.from(Policy.class);

            criteriaQuery.select(root);

            Query<Policy> query = session.createQuery(criteriaQuery);
            return query.getResultList();

        });

    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#getAllRoles()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Role> getAllRoles()
    {
        return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Role> criteriaQuery = builder.createQuery(Role.class);

            Root<Role> root = criteriaQuery.from(Role.class);

            criteriaQuery.select(root);

            Query<Role> query = session.createQuery(criteriaQuery);
            return query.getResultList();

        });

    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#getAllPrincipals()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<IkasanPrincipal> getAllPrincipals()
    {
        return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<IkasanPrincipal> criteriaQuery = builder.createQuery(IkasanPrincipal.class);

            Root<IkasanPrincipal> root = criteriaQuery.from(IkasanPrincipal.class);

            criteriaQuery.select(root);

            org.hibernate.query.Query<IkasanPrincipal> query = session.createQuery(criteriaQuery);
            return query.getResultList();

        });
    }

	@Override
	public List<IkasanPrincipalLite> getAllPrincipalLites()
	{
		return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<IkasanPrincipalLite> criteriaQuery = builder.createQuery(IkasanPrincipalLite.class);

            Root<IkasanPrincipalLite> root = criteriaQuery.from(IkasanPrincipalLite.class);

            criteriaQuery.select(root);

            org.hibernate.query.Query<IkasanPrincipalLite> query = session.createQuery(criteriaQuery);
            return query.getResultList();

        });

	}

	/*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#getPolicyByName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	@Override
    public Policy getPolicyByName(String name)
    {
        return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Policy> criteriaQuery = builder.createQuery(Policy.class);

            Root<Policy> root = criteriaQuery.from(Policy.class);

            criteriaQuery.select(root)
                .where(builder.equal(root.get("name"),name));

            org.hibernate.query.Query<Policy> query = session.createQuery(criteriaQuery);
            List<Policy> results = query.getResultList();

            if(results == null || results.size() == 0)
            {
                return null;
            }

            return results.get(0);

        });
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#getRoleByName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	@Override
    public Role getRoleByName(String name)
    {

        return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Role> criteriaQuery = builder.createQuery(Role.class);

            Root<Role> root = criteriaQuery.from(Role.class);

            criteriaQuery.select(root)
                .where(builder.equal(root.get("name"),name));

            Query<Role> query = session.createQuery(criteriaQuery);
            List<Role> results = query.getResultList();

            if(results == null || results.size() == 0)
            {
                return null;
            }

            return results.get(0);

        });
    }

	/*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#getRoleByName(java.lang.String)
     */
	@SuppressWarnings("unchecked")
	@Override
	public Role getRoleById(Long id)
	{
		Role role = this.getHibernateTemplate().get(Role.class, id);
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
        Policy policy = this.getHibernateTemplate().get(Policy.class,id);
		return policy;
	}

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#deleteRole(org.ikasan.security.window.Role)
     */
    @Override
    public void deleteRole(Role role)
    {
        this.getHibernateTemplate().delete(role);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#deletePolicy(org.ikasan.security.window.Policy)
     */
    @Override
    public void deletePolicy(Policy policy)
    {
        this.getHibernateTemplate().delete(policy);
    }

    @Override
    public void deleteRoleModule(RoleModule roleModule)
    {
        this.getHibernateTemplate().delete(roleModule);
    }

    @Override
    public void saveRoleModule(RoleModule roleModule) {
        this.getHibernateTemplate().save(roleModule);
    }

    @Override
    public void deleteRoleJobPlan(RoleJobPlan roleJobPlan) {
        this.getHibernateTemplate().delete(roleJobPlan);
    }

    @Override
    public void saveRoleJobPlan(RoleJobPlan roleJobPlan) {
        this.getHibernateTemplate().save(roleJobPlan);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.dao.SecurityDao#deletePrincipal(org.ikasan.security.window.IkasanPrincipal)
     */
    @Override
    public void deletePrincipal(IkasanPrincipal principal)
    {
        this.getHibernateTemplate().delete(principal);
    }

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#saveOrUpdateAuthenticationMethod(org.ikasan.security.window.AuthenticationMethod)
	 */
	@Override
	public void saveOrUpdateAuthenticationMethod(AuthenticationMethod authenticationMethod)
	{
		this.getHibernateTemplate().saveOrUpdate(authenticationMethod);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAuthenticationMethod(java.lang.Long)
	 */
	@Override
	public AuthenticationMethod getAuthenticationMethod(Long id)
	{
		AuthenticationMethod authenticationMethod = this.getHibernateTemplate().get(AuthenticationMethod.class,id);
        return authenticationMethod;
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAuthenticationMethod(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AuthenticationMethod> getAuthenticationMethods()
	{
	    return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<AuthenticationMethod> criteriaQuery = builder.createQuery(AuthenticationMethod.class);

            Root<AuthenticationMethod> root = criteriaQuery.from(AuthenticationMethod.class);

            criteriaQuery.select(root)
                .orderBy(builder.asc(root.get("order")));

            Query<AuthenticationMethod> query = session.createQuery(criteriaQuery);
            return query.getResultList();

        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAllPrincipalsWithRole(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IkasanPrincipal> getAllPrincipalsWithRole(String roleName)
	{
        return this.getHibernateTemplate().execute((session) -> {
            Query query = session.createQuery(SecurityConstants.GET_IKASAN_PRINCIPLE_WITH_ROLE_QUERY);
            query.setParameter("name", roleName);
            return (List<IkasanPrincipal>) query.list();
        });

    }

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getPrincipalsByName(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IkasanPrincipal> getPrincipalsByRoleNames(List<String> names)
	{

        return this.getHibernateTemplate().execute((session) -> {
            Query query = session.createQuery(SecurityConstants.GET_IKASAN_PRINCIPLE_WITH_ROLE_IN_QUERY);
            query.setParameter("name", names);
            return (List<IkasanPrincipal>) query.list();
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getPrincipalByNameLike(java.lang.String)
	 */
	@Override
	public List<IkasanPrincipal> getPrincipalByNameLike(String name)
	{
		@SuppressWarnings("unchecked")
		List<IkasanPrincipal> results = (List<IkasanPrincipal>) getHibernateTemplate()
            .findByNamedParam("from IkasanPrincipal where name LIKE :name", "name", name + '%');

        return results;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAllPolicyLinkTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PolicyLinkType> getAllPolicyLinkTypes()
	{
        return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<PolicyLinkType> criteriaQuery = builder.createQuery(PolicyLinkType.class);

            Root<PolicyLinkType> root = criteriaQuery.from(PolicyLinkType.class);

            criteriaQuery.select(root);

            Query<PolicyLinkType> query = session.createQuery(criteriaQuery);
            return query.getResultList();

        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getPolicyByNameLike(java.lang.String)
	 */
	@Override
	public List<Policy> getPolicyByNameLike(String name)
	{
		@SuppressWarnings("unchecked")
		List<Policy> results = (List<Policy>) getHibernateTemplate()
            .findByNamedParam("from Policy where name LIKE :name", "name", name + '%');

        return results;
	}
	
	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getRoleByNameLike(java.lang.String)
	 */
	@Override
	public List<Role> getRoleByNameLike(String name)
	{
		@SuppressWarnings("unchecked")
		List<Role> results = (List<Role>) getHibernateTemplate()
            .findByNamedParam("from Role where name LIKE :name", "name", name + '%');

        return results;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#saveOrUpdatePolicyLink(org.ikasan.security.window.PolicyLink)
	 */
	@Override
	public void saveOrUpdatePolicyLink(PolicyLink policyLink)
	{
		this.getHibernateTemplate().saveOrUpdate(policyLink);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#saveOrUpdatePolicyLinkType(org.ikasan.security.window.PolicyLinkType)
	 */
	@Override
	public void saveOrUpdatePolicyLinkType(PolicyLinkType policyLinkType)
	{
		this.getHibernateTemplate().saveOrUpdate(policyLinkType);	
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#deletePolicyLink(org.ikasan.security.window.PolicyLink)
	 */
	@Override
	public void deletePolicyLink(PolicyLink policyLink)
	{
		this.getHibernateTemplate().delete(policyLink);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAllPoliciessWithRole(java.lang.String)
	 */
	@Override
	public List<Policy> getAllPoliciesWithRole(String roleName)
	{
        return this.getHibernateTemplate().execute((session) -> {
            Query query = session.createQuery(SecurityConstants.GET_POLICY_WITH_ROLE_QUERY);
            query.setParameter("name", roleName);
            return (List<Policy>) query.list();
        });

	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#deleteAuthenticationMethod(org.ikasan.security.window.AuthenticationMethod)
	 */
	@Override
	public void deleteAuthenticationMethod(AuthenticationMethod authenticationMethod)
	{
		this.getHibernateTemplate().delete(authenticationMethod);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getNumberOfAuthenticationMethods()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public long getNumberOfAuthenticationMethods()
	{
		return (Long)this.getHibernateTemplate().execute(new HibernateCallback()
        {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException
            {
                Query query = session.createQuery("select count(*) from AuthenticationMethod");

                return (Long)query.uniqueResult();
            }
        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAuthenticationMethodByOrder(int)
	 */
	@Override
	public AuthenticationMethod getAuthenticationMethodByOrder(long order)
	{
		return getHibernateTemplate().execute((session) -> {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<AuthenticationMethod> criteriaQuery = builder.createQuery(AuthenticationMethod.class);

            Root<AuthenticationMethod> root = criteriaQuery.from(AuthenticationMethod.class);

            criteriaQuery.select(root)
                .where(builder.equal(root.get("order"),order));

            Query<AuthenticationMethod> query = session.createQuery(criteriaQuery);
            List<AuthenticationMethod> results = query.getResultList();
            if(results == null || results.size() == 0)
            {
                return null;
            }
            return results.get(0);

        });
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getUsersAssociatedWithPrincipal(long)
	 */
	@Override
	public List<User> getUsersAssociatedWithPrincipal(final long principalId)
	{
        return this.getHibernateTemplate().execute((session) -> {
            Query query = session.createQuery(SecurityConstants.GET_USERS_BY_PRINCIPAL_QUERY);
            query.setParameter(SecurityConstants.PRINCIPAL_ID, principalId);
            return (List<User>) query.list();
        });
	}


}
