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

import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.PolicyLink;
import org.ikasan.security.model.PolicyLinkType;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;


/**
 * @author CMI2 Development Team
 *
 */
public interface SecurityService
{
    /**
     * 
     * @param name
     * @return
     * @throws SecurityServiceException
     */
    public IkasanPrincipal findPrincipalByName(String name);

    /**
     * 
     * @param name
     * @return
     * @throws SecurityServiceException
     */
    public Role findRoleByName(String name);
    
    /**
     * 
     * @param name
     * @return
     * @throws SecurityServiceException
     */
    public Policy findPolicyByName(String name);

    /**
     * 
     * @param name
     * @param type
     * @return
     * @throws SecurityServiceException
     */
    public IkasanPrincipal createNewPrincipal(String name, String type);

    /**
     * 
     * @param principal
     * @throws SecurityServiceException
     */
    public void savePrincipal(IkasanPrincipal principal);

    /**
     * 
     * @return
     * @throws SecurityServiceException
     */
    public List<IkasanPrincipal> getAllPrincipals();

    /**
     * 
     * @return
     * @throws SecurityDaoException
     */
    public List<IkasanPrincipal> getAllPrincipalsWithRole(String roleName);
    
    /**
     * 
     * @param name
     * @return
     */
    public List<IkasanPrincipal> getPrincipalsByName(List<String> names);

    /**
     * 
     * @param principal
     * @throws SecurityServiceException
     */
    public void deletePrincipal(IkasanPrincipal principal);

    /**
     * 
     * @param name
     * @return
     * @throws SecurityServiceException
     */
    public Role createNewRole(String name, String description);

    /**
     * 
     * @param role
     * @throws SecurityServiceException
     */
    public void saveRole(Role role);

    /**
     * 
     * @param role
     * @throws SecurityServiceException
     */
    public void deleteRole(Role role);

    /**
     * 
     * @return
     * @throws SecurityServiceException
     */
    public List<Role> getAllRoles();

    /**
     * 
     * @param name
     * @return
     * @throws SecurityServiceException
     */
    public Policy createNewPolicy(String name, String description);

    /**
     * 
     * @param policy
     * @throws SecurityServiceException
     */
    public void savePolicy(Policy policy);

    /**
     * 
     * @param policy
     * @throws SecurityServiceException
     */
    public void deletePolicy(Policy policy);

    /**
     * 
     * @return
     * @throws SecurityServiceException
     */
    public List<Policy> getAllPolicies();

    /**
     * 
     * @param authenticationMethod
     * @throws SecurityDaoException
     */
    public void saveOrUpdateAuthenticationMethod(AuthenticationMethod authenticationMethod);

    /**
     * 
     * @return
     * @throws SecurityDaoException
     */
    public List<AuthenticationMethod> getAuthenticationMethods();
    
    /**
     * 
     * @param id
     * @return
     * @throws SecurityDaoException
     */
    public AuthenticationMethod getAuthenticationMethod(Long id);

    /**
     * 
     * @param authenticationMethod
     */
    public void deleteAuthenticationMethod(AuthenticationMethod authenticationMethod);

    /**
     * 
     * @param name
     * @return
     */
    public List<IkasanPrincipal> getPrincipalByNameLike(String name);
    
    /**
     * 
     * @return
     */
    public List<PolicyLinkType> getAllPolicyLinkTypes();
    
    /**
     * 
     * @param name
     * @return
     */
    public List<Policy> getPolicyByNameLike(String name);
    
    /**
     * 
     * @param policyLink
     */
    public void savePolicyLink(PolicyLink policyLink);
    
    /**
     * 
     * @param policyLink
     */
    public void deletePolicyLink(PolicyLink policyLink);
    
    /**
     * 
     * @return
     */
    public List<Policy> getAllPoliciesWithRole(String roleName);
    
    /**
     * 
     * @param name
     * @return
     */
    public List<Role> getRoleByNameLike(String name);
    
    /**
     * Get the number of AuthenticationMethod records in the database.
     * @return
     */
    public long getNumberOfAuthenticationMethods();
    
    /**
     * 
     * @param order
     * @return
     */
    public AuthenticationMethod getAuthenticationMethodByOrder(long order);
    
    /**
     * 
     * @param principalId
     * @return
     */
    public List<User> getUsersAssociatedWithPrincipal(long principalId);
}

