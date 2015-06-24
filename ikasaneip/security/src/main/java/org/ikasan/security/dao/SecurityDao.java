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
public interface SecurityDao
{
    /**
     * 
     * @param role
     */
    public void saveOrUpdateRole(Role role);

    /**
     * 
     * @param role
     */
    public void deleteRole(Role role);

    /**
     * 
     * @param policy
     */
    public void saveOrUpdatePolicy(Policy policy);

    /**
     * 
     * @param policy
     */
    public void deletePolicy(Policy policy);
    
    /**
     * 
     * @param policyLink
     */
    public void saveOrUpdatePolicyLink(PolicyLink policyLink);

    /**
     * 
     * @param policyLink
     */
    public void deletePolicyLink(PolicyLink policyLink);

    /**
     * 
     * @param principal
     */
    public void saveOrUpdatePrincipal(IkasanPrincipal principal);


    /**
     * 
     * @param principal
     */
    public void deletePrincipal(IkasanPrincipal principal);

    /**
     * 
     * @param name
     * @return
     */
    public IkasanPrincipal getPrincipalByName(String name);

    /**
     * 
     * @param name
     * @return
     */
    public List<IkasanPrincipal> getPrincipalsByRoleNames(List<String> names);

    /**
     * 
     * @return     
     */
    public List<Policy> getAllPolicies();

    /**
     * 
     * @return     
     */
    public List<Role> getAllRoles();

    /**
     * 
     * @return     
     */
    public List<IkasanPrincipal> getAllPrincipals();

    /**
     * 
     * @return     
     */
    public List<IkasanPrincipal> getAllPrincipalsWithRole(String roleName);
    
    /**
     * 
     * @return    
     */
    public List<Policy> getAllPoliciesWithRole(String roleName);

    /**
     * 
     * @return    
     */
    public Policy getPolicyByName(String name);

    /**
     * 
     * @param name
     * @return    
     */
    public Role getRoleByName(String name);

    /**
     * 
     * @param authenticationMethod     
     */
    public void saveOrUpdateAuthenticationMethod(AuthenticationMethod authenticationMethod);

    /**
     * 
     * @param id
     * @return
     */
    public AuthenticationMethod getAuthenticationMethod(Long id);
    
    /**
     * 
     * @return
     */
    public List<AuthenticationMethod> getAuthenticationMethods();
    
    /**
     * 
     * @param name
     * @return
     */
    public List<IkasanPrincipal> getPrincipalByNameLike(String name);
    
    /**
     * 
     * @param authenticationMethod
     */
    public void deleteAuthenticationMethod(AuthenticationMethod authenticationMethod);
 
    /**
     * 
     * @return
     */
    public List<PolicyLinkType> getAllPolicyLinkTypes();

    
    /**
     * 
     * @param policyLinkType
     */
    public void saveOrUpdatePolicyLinkType(PolicyLinkType policyLinkType);
    
    /**
     * 
     * @param name
     * @return
     */
    public List<Policy> getPolicyByNameLike(String name);

    /**
     * 
     * @param name
     * @return
     */
    public List<Role> getRoleByNameLike(String name);
}
