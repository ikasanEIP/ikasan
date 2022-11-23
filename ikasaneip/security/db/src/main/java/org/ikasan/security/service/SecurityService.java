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

import org.ikasan.security.model.*;


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
     */
    public IkasanPrincipal findPrincipalByName(String name);

    /**
     *
     * @param name
     * @return
     */
    public Role findRoleByName(String name);

    /**
     *
     * @param name
     * @return
     */
    public Policy findPolicyByName(String name);

    /**
     *
     * @param name
     * @param type
     * @return
     */
    public IkasanPrincipal createNewPrincipal(String name, String type);

    /**
     *
     * @param principal
     */
    public void savePrincipal(IkasanPrincipal principal);

    /**
     *
     * @return
     */
    public List<IkasanPrincipal> getAllPrincipals();

    /**
     *
     * @return
     */
    public List<IkasanPrincipalLite> getAllPrincipalLites();

    /**
     *
     * @param roleName
     * @return
     */
    public List<IkasanPrincipal> getAllPrincipalsWithRole(String roleName);

    /**
     *
     * @param names
     * @return
     */
    public List<IkasanPrincipal> getPrincipalsByName(List<String> names);

    /**
     *
     * @param principal
     */
    public void deletePrincipal(IkasanPrincipal principal);

    /**
     *
     * @param name
     * @param description
     * @return
     */
    public Role createNewRole(String name, String description);

    /**
     *
     * @param role
     */
    public void saveRole(Role role);

    /**
     *
     * @param role
     */
    public void deleteRole(Role role);

    /**
     *
     * @return
     */
    public List<Role> getAllRoles();

    /**
     *
     * @param name
     * @param description
     * @return
     */
    public Policy createNewPolicy(String name, String description);

    /**
     *
     * @param policy
     */
    public void savePolicy(Policy policy);

    /**
     *
     * @param policy
     */
    public void deletePolicy(Policy policy);

    /**
     *
     * @param roleModule
     */
    public void deleteRoleModule(RoleModule roleModule);

    /**
     *
     * @param roleJobPlan
     */
    public void deleteRoleJobPlan(RoleJobPlan roleJobPlan);

    /**
     *
     * @return
     */
    public List<Policy> getAllPolicies();

    /**
     *
     * @param authenticationMethod
     */
    public void saveOrUpdateAuthenticationMethod(AuthenticationMethod authenticationMethod);

    /**
     *
     * @return
     */
    public List<AuthenticationMethod> getAuthenticationMethods();

    /**
     *
     * @param id
     * @return
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
     * @param roleModule
     */
    public void saveRoleModule(RoleModule roleModule);

    /**
     *
     * @param roleJobPlan
     */
    public void saveRoleJobPlan(RoleJobPlan roleJobPlan);
    
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

    /**
     *
     * @param id
     * @return
     */
    public Role getRoleById(Long id);

    /**
     *
     * @param id
     * @return
     */
    public Policy getPolicyById(Long id);
}

