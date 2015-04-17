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

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.PolicyLink;
import org.ikasan.security.model.PolicyLinkType;
import org.ikasan.security.model.Role;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * @author CMI2 Development Team
 *
 */
public class HibernateSecurityDao extends HibernateDaoSupport implements SecurityDao
{

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.dao.SecurityDao#addRole(com.mizuho.cmi2.security.model.Role)
     */
    @Override
    public void saveOrUpdateRole(Role role)
    {
    	this.getHibernateTemplate().saveOrUpdate(role);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.dao.SecurityDao#addRole(com.mizuho.cmi2.security.model.Role)
     */
    @Override
    public void saveOrUpdatePolicy(Policy policy)
    {
    	this.getHibernateTemplate().saveOrUpdate(policy);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.dao.SecurityDao#addPrincipal(com.mizuho.cmi2.security.model.Principal)
     */
    @Override
    public void saveOrUpdatePrincipal(IkasanPrincipal principal)
    {
    	this.getHibernateTemplate().saveOrUpdate(principal);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.dao.SecurityDao#getPrincipalByName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	@Override 
    public IkasanPrincipal getPrincipalByName(String name)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(IkasanPrincipal.class);
        criteria.add(Restrictions.eq("name", name));
        IkasanPrincipal principal = (IkasanPrincipal) DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));

        return principal;
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.dao.SecurityDao#getAllPolicies()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Policy> getAllPolicies()
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(Policy.class);

        return (List<Policy>)this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.dao.SecurityDao#getAllRoles()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Role> getAllRoles()
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(Role.class);

        return (List<Role>)this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.dao.SecurityDao#getAllPrincipals()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<IkasanPrincipal> getAllPrincipals()
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(IkasanPrincipal.class);

        return (List<IkasanPrincipal>)this.getHibernateTemplate().findByCriteria(criteria);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.dao.SecurityDao#getPolicyByName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	@Override
    public Policy getPolicyByName(String name)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(Policy.class);
        criteria.add(Restrictions.eq("name", name));
        Policy policy = (Policy) DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));

        return policy;
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.dao.SecurityDao#getRoleByName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	@Override
    public Role getRoleByName(String name)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(Role.class);
        criteria.add(Restrictions.eq("name", name));
        Role role = (Role) DataAccessUtils.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));

        return role;
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.dao.SecurityDao#deleteRole(com.mizuho.cmi2.security.model.Role)
     */
    @Override
    public void deleteRole(Role role)
    {
        this.getHibernateTemplate().delete(role);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.dao.SecurityDao#deletePolicy(com.mizuho.cmi2.security.model.Policy)
     */
    @Override
    public void deletePolicy(Policy policy)
    {
        this.getHibernateTemplate().delete(policy);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.dao.SecurityDao#deletePrincipal(com.mizuho.cmi2.security.model.Principal)
     */
    @Override
    public void deletePrincipal(IkasanPrincipal principal)
    {
        this.getHibernateTemplate().delete(principal);
    }

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#saveOrUpdateAuthenticationMethod(org.ikasan.security.model.AuthenticationMethod)
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
		DetachedCriteria criteria = DetachedCriteria.forClass(AuthenticationMethod.class);
        criteria.add(Restrictions.eq("id", id));
        @SuppressWarnings("unchecked")
		AuthenticationMethod authenticationMethod = (AuthenticationMethod) DataAccessUtils
        		.uniqueResult(this.getHibernateTemplate().findByCriteria(criteria));

        return authenticationMethod;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAllPrincipalsWithRole(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IkasanPrincipal> getAllPrincipalsWithRole(String roleName)
	{
		 DetachedCriteria criteria = DetachedCriteria.forClass(IkasanPrincipal.class);
		 criteria.createCriteria("roles").add(Restrictions.eq("name", roleName));

	     return (List<IkasanPrincipal>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getPrincipalsByName(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IkasanPrincipal> getPrincipalsByName(List<String> names)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(IkasanPrincipal.class);
		criteria.createCriteria("roles").add(Restrictions.in("name", names));

	    return (List<IkasanPrincipal>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getPrincipalByNameLike(java.lang.String)
	 */
	@Override
	public List<IkasanPrincipal> getPrincipalByNameLike(String name)
	{
		@SuppressWarnings("unchecked")
		List<IkasanPrincipal> results = getHibernateTemplate().find("from IkasanPrincipal where name LIKE ?", name + '%');

        return results;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getAllPolicyLinkTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PolicyLinkType> getAllPolicyLinkTypes()
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(PolicyLinkType.class);

	    return (List<PolicyLinkType>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#getPolicyByNameLike(java.lang.String)
	 */
	@Override
	public List<Policy> getPolicyByNameLike(String name)
	{
		@SuppressWarnings("unchecked")
		List<Policy> results = getHibernateTemplate().find("from Policy where name LIKE ?", name + '%');

        return results;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#saveOrUpdatePolicyLink(org.ikasan.security.model.PolicyLink)
	 */
	@Override
	public void saveOrUpdatePolicyLink(PolicyLink policyLink)
	{
		this.getHibernateTemplate().saveOrUpdate(policyLink);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.dao.SecurityDao#deletePolicyLink(org.ikasan.security.model.PolicyLink)
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
		DetachedCriteria criteria = DetachedCriteria.forClass(Policy.class);
		 criteria.createCriteria("roles").add(Restrictions.eq("name", roleName));

	     return (List<Policy>)this.getHibernateTemplate().findByCriteria(criteria);
	}

	
}
