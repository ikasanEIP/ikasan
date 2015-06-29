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
package org.ikasan.security.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.security.dao.SecurityDao;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.PolicyLink;
import org.ikasan.security.model.PolicyLinkType;
import org.ikasan.security.model.Role;


/**
 * @author CMI2 Development Team
 *
 */
public class SecurityServiceImpl implements SecurityService
{
	/** Logger instance */
    private static Logger logger = Logger.getLogger(SecurityServiceImpl.class);
    
    private SecurityDao securityDao;


    /**
     * Constructor
     * 
     * @param securityDao
     */
    public SecurityServiceImpl(SecurityDao securityDao)
    {
        super();
        this.securityDao = securityDao;
        if(this.securityDao == null)
        {
            throw new IllegalArgumentException("securityDao cannot be null!");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#createNewPrincipal(java.lang.String, java.lang.String)
     */
    @Override
    public IkasanPrincipal createNewPrincipal(String name, String type)
    {
        IkasanPrincipal principal = new IkasanPrincipal();
        principal.setName(name);
        principal.setType(type);
        principal.setDescription("description");
        
        this.securityDao.saveOrUpdatePrincipal(principal);

        return principal;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#savePrincipal(org.ikasan.security.model.IkasanPrincipal)
     */
    @Override
    public void savePrincipal(IkasanPrincipal principal)
    {
    	this.securityDao.saveOrUpdatePrincipal(principal);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#createNewRole(java.lang.String, java.lang.String)
     */
    @Override
    public Role createNewRole(String name, String description)
    {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);        
        this.securityDao.saveOrUpdateRole(role);

        return role;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#createNewPolicy(java.lang.String, java.lang.String)
     */
    @Override
    public Policy createNewPolicy(String name, String description)
    {
        Policy policy = new Policy();
        policy.setName(name);
        policy.setDescription(description);
 
        this.securityDao.saveOrUpdatePolicy(policy);

        return policy;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#saveRole(org.ikasan.security.model.Role)
     */
    @Override
    public void saveRole(Role role) 
    {
    	this.securityDao.saveOrUpdateRole(role);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#savePolicy(org.ikasan.security.model.Policy)
     */
    @Override
    public void savePolicy(Policy policy) 
    {
    	this.securityDao.saveOrUpdatePolicy(policy);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#findPrincipalByName(java.lang.String)
     */
    @Override
    public IkasanPrincipal findPrincipalByName(String name) 
    {
        return this.securityDao.getPrincipalByName(name);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#deletePrincipal(org.ikasan.security.model.IkasanPrincipal)
     */
    @Override
    public void deletePrincipal(IkasanPrincipal principal) 
    {
    	this.securityDao.deletePrincipal(principal);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#deleteRole(org.ikasan.security.model.Role)
     */
    @Override
    public void deleteRole(Role role) 
    {
    	this.securityDao.deleteRole(role);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#deletePolicy(org.ikasan.security.model.Policy)
     */
    @Override
    public void deletePolicy(Policy policy) 
    {
    	this.securityDao.deletePolicy(policy);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#getAllPrincipals()
     */
    @Override
    public List<IkasanPrincipal> getAllPrincipals() 
    {
        return this.securityDao.getAllPrincipals();
    }

    /* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getPrincipalByNameLike(java.lang.String)
	 */
	@Override
	public List<IkasanPrincipal> getPrincipalByNameLike(String name)
	{
		return this.securityDao.getPrincipalByNameLike(name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getAllRoles()
	 */
    @Override
    public List<Role> getAllRoles() 
    {
        return this.securityDao.getAllRoles();
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#getAllPolicies()
     */
    @Override
    public List<Policy> getAllPolicies() 
    {
        return this.securityDao.getAllPolicies();
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#saveOrUpdateAuthenticationMethod(org.ikasan.security.model.AuthenticationMethod)
     */
    public void saveOrUpdateAuthenticationMethod(AuthenticationMethod authenticationMethod) 
    {
		this.securityDao.saveOrUpdateAuthenticationMethod(authenticationMethod);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#getAuthenticationMethod(java.lang.Long)
     */
    public List<AuthenticationMethod> getAuthenticationMethods() 
    {
		return this.securityDao.getAuthenticationMethods();
    }

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#findRoleByName(java.lang.String)
	 */
	@Override
	public Role findRoleByName(String name)
	{
		return this.securityDao.getRoleByName(name);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#findPolicyByName(java.lang.String)
	 */
	@Override
	public Policy findPolicyByName(String name)
	{
		return this.securityDao.getPolicyByName(name);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getAllPrincipalsWithRole(java.lang.String)
	 */
	@Override
	public List<IkasanPrincipal> getAllPrincipalsWithRole(String roleName)
	{	
		return this.securityDao.getAllPrincipalsWithRole(roleName);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getPrincipalsByName(java.util.List)
	 */
	@Override
	public List<IkasanPrincipal> getPrincipalsByName(List<String> names)
	{
		return this.securityDao.getPrincipalsByRoleNames(names);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getAllPolicyLinkTypes()
	 */
	@Override
	public List<PolicyLinkType> getAllPolicyLinkTypes()
	{
		return this.securityDao.getAllPolicyLinkTypes();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getPolicyByNameLike(java.lang.String)
	 */
	@Override
	public List<Policy> getPolicyByNameLike(String name)
	{
		return this.securityDao.getPolicyByNameLike(name);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#savePolicyLink(org.ikasan.security.model.PolicyLink)
	 */
	@Override
	public void savePolicyLink(PolicyLink policyLink)
	{
		this.securityDao.saveOrUpdatePolicyLink(policyLink);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#deletePolicyLink(org.ikasan.security.model.PolicyLink)
	 */
	@Override
	public void deletePolicyLink(PolicyLink policyLink)
	{
		this.securityDao.deletePolicyLink(policyLink);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getAllPoliciesWithRole(java.lang.String)
	 */
	@Override
	public List<Policy> getAllPoliciesWithRole(String roleName)
	{
		return this.securityDao.getAllPoliciesWithRole(roleName);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getRoleByNameLike(java.lang.String)
	 */
	@Override
	public List<Role> getRoleByNameLike(String name)
	{
		return this.securityDao.getRoleByNameLike(name);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getAuthenticationMethod(java.lang.Long)
	 */
	@Override
	public AuthenticationMethod getAuthenticationMethod(Long id)
	{
		return this.securityDao.getAuthenticationMethod(id);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#deleteAuthenticationMethod(org.ikasan.security.model.AuthenticationMethod)
	 */
	@Override
	public void deleteAuthenticationMethod(AuthenticationMethod authenticationMethod)
	{
		this.securityDao.deleteAuthenticationMethod(authenticationMethod);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getNumberOfAuthenticationMethods()
	 */
	@Override
	public long getNumberOfAuthenticationMethods()
	{
		return this.securityDao.getNumberOfAuthenticationMethods();
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getAuthenticationMethodByOrder(int)
	 */
	@Override
	public AuthenticationMethod getAuthenticationMethodByOrder(long order)
	{
		return this.securityDao.getAuthenticationMethodByOrder(order);
	}
}
