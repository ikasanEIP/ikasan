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
import org.ikasan.security.dao.SecurityDaoException;
import org.ikasan.security.dao.constants.SecurityConstants;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
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

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.service.SecurityService#createNewPrincipal(java.lang.String, java.lang.String)
     */
    @Override
    public IkasanPrincipal createNewPrincipal(String name, String type) throws SecurityServiceException
    {
        IkasanPrincipal principal = new IkasanPrincipal();
        principal.setName(name);
        principal.setType(type);
        principal.setDescription("description");
        
        try
        {
            this.securityDao.saveOrUpdatePrincipal(principal);
        }
        catch (SecurityDaoException e)
        {
            throw new SecurityServiceException(e);
        }

        return principal;
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.service.SecurityService#savePrincipal(com.mizuho.cmi2.security.model.Principal)
     */
    @Override
    public void savePrincipal(IkasanPrincipal principal) throws SecurityServiceException
    {
        try
        {
            this.securityDao.saveOrUpdatePrincipal(principal);
        }
        catch (SecurityDaoException e)
        {
            throw new SecurityServiceException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.service.SecurityService#createNewRole(java.lang.String)
     */
    @Override
    public Role createNewRole(String name, String description) throws SecurityServiceException
    {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);

        try
        {
            this.securityDao.saveOrUpdateRole(role);
        }
        catch (SecurityDaoException e)
        {
            throw new SecurityServiceException(e);
        }

        return role;
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.service.SecurityService#createNewPolicy(java.lang.String)
     */
    @Override
    public Policy createNewPolicy(String name, String description) throws SecurityServiceException
    {
        Policy policy = new Policy();
        policy.setName(name);
        policy.setDescription(description);
 
        try
        {
            this.securityDao.saveOrUpdatePolicy(policy);
        }
        catch (SecurityDaoException e)
        {
            throw new SecurityServiceException(e);
        }

        return policy;
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.service.SecurityService#saveRole(com.mizuho.cmi2.security.model.Role)
     */
    @Override
    public void saveRole(Role role) throws SecurityServiceException
    {
        try
        {
            this.securityDao.saveOrUpdateRole(role);
        }
        catch (SecurityDaoException e)
        {
            throw new SecurityServiceException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.service.SecurityService#savePolicy(com.mizuho.cmi2.security.model.Policy)
     */
    @Override
    public void savePolicy(Policy policy) throws SecurityServiceException
    {
        try
        {
            this.securityDao.saveOrUpdatePolicy(policy);
        }
        catch (SecurityDaoException e)
        {
            throw new SecurityServiceException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.service.SecurityService#findPrincipalByName(java.lang.String)
     */
    @Override
    public IkasanPrincipal findPrincipalByName(String name) throws SecurityServiceException
    {
        IkasanPrincipal principal = null;

        try
        {
            principal = this.securityDao.getPrincipalByName(name);
        }
        catch (SecurityDaoException e)
        {
            throw new SecurityServiceException(e);
        }

        return principal;
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.service.SecurityService#deletePrincipal(com.mizuho.cmi2.security.model.Principal)
     */
    @Override
    public void deletePrincipal(IkasanPrincipal principal) throws SecurityServiceException
    {
        try
        {
            this.securityDao.deletePrincipal(principal);
        }
        catch (SecurityDaoException e)
        {
            throw new SecurityServiceException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.service.SecurityService#deleteRole(com.mizuho.cmi2.security.model.Role)
     */
    @Override
    public void deleteRole(Role role) throws SecurityServiceException
    {
        try
        {
            this.securityDao.deleteRole(role);
        }
        catch (SecurityDaoException e)
        {
            throw new SecurityServiceException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.service.SecurityService#deletePolicy(com.mizuho.cmi2.security.model.Policy)
     */
    @Override
    public void deletePolicy(Policy policy) throws SecurityServiceException
    {
        try
        {
            this.securityDao.deletePolicy(policy);
        }
        catch (SecurityDaoException e)
        {
            throw new SecurityServiceException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.service.SecurityService#getAllPrincipals()
     */
    @Override
    public List<IkasanPrincipal> getAllPrincipals() throws SecurityServiceException
    {
        List<IkasanPrincipal> principals = null;

        try
        {
            principals = this.securityDao.getAllPrincipals();
        }
        catch (SecurityDaoException e)
        {
            throw new SecurityServiceException(e);
        }

        return principals;
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.service.SecurityService#getAllRoles()
     */
    @Override
    public List<Role> getAllRoles() throws SecurityServiceException
    {
        List<Role> roles = null;

        try
        {
            roles = this.securityDao.getAllRoles();
        }
        catch (SecurityDaoException e)
        {
            throw new SecurityServiceException(e);
        }

        return roles;
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.security.service.SecurityService#getAllPolicies()
     */
    @Override
    public List<Policy> getAllPolicies() throws SecurityServiceException
    {
        List<Policy> policies = null;

        try
        {
            policies = this.securityDao.getAllPolicies();
        }
        catch (SecurityDaoException e)
        {
            throw new SecurityServiceException(e);
        }

        return policies;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#saveOrUpdateAuthenticationMethod(org.ikasan.security.model.AuthenticationMethod)
     */
    public void saveOrUpdateAuthenticationMethod(AuthenticationMethod authenticationMethod) throws SecurityServiceException
    {
    	authenticationMethod.setId(SecurityConstants.AUTH_METHOD_ID);
    	try
		{
			this.securityDao.saveOrUpdateAuthenticationMethod(authenticationMethod);
		} catch (SecurityDaoException e)
		{
			throw new SecurityServiceException(e);
		}
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.security.service.SecurityService#getAuthenticationMethod(java.lang.Long)
     */
    public AuthenticationMethod getAuthenticationMethod() throws SecurityServiceException
    {
    	try
		{
			return this.securityDao.getAuthenticationMethod(SecurityConstants.AUTH_METHOD_ID);
		} catch (SecurityDaoException e)
		{
			throw new SecurityServiceException(e);
		}
    }

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#findRoleByName(java.lang.String)
	 */
	@Override
	public Role findRoleByName(String name)
			throws SecurityServiceException
	{
		try
		{
			return this.securityDao.getRoleByName(name);
		} catch (SecurityDaoException e)
		{
			throw new SecurityServiceException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#findPolicyByName(java.lang.String)
	 */
	@Override
	public Policy findPolicyByName(String name)
			throws SecurityServiceException
	{
		try
		{
			return this.securityDao.getPolicyByName(name);
		} catch (SecurityDaoException e)
		{
			throw new SecurityServiceException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getAllPrincipalsWithRole(java.lang.String)
	 */
	@Override
	public List<IkasanPrincipal> getAllPrincipalsWithRole(String roleName)
			throws SecurityServiceException
	{
		List<IkasanPrincipal> principals = null;
		try
		{
			principals =  this.securityDao.getAllPrincipalsWithRole(roleName);
		} 
		catch (SecurityDaoException e)
		{
			throw new SecurityServiceException(e);
		}
		
		return principals;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.security.service.SecurityService#getPrincipalsByName(java.util.List)
	 */
	@Override
	public List<IkasanPrincipal> getPrincipalsByName(List<String> names)
			throws SecurityServiceException
	{
		List<IkasanPrincipal> principals = null;
		try
		{
			principals =  this.securityDao.getPrincipalsByName(names);
		} 
		catch (SecurityDaoException e)
		{
			throw new SecurityServiceException(e);
		}
		
		return principals;
	}
}
