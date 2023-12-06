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

import org.ikasan.security.model.*;


/**
 * @author CMI2 Development Team
 *
 */
public interface SecurityDao
{
    /**
     * Save a give Role
     *
     * @param role
     */
    public void saveOrUpdateRole(Role role);

    /**
     * Delete a given Role
     *
     * @param role
     */
    public void deleteRole(Role role);

    /**
     * Save a given Policy
     *
     * @param policy
     */
    public void saveOrUpdatePolicy(Policy policy);

    /**
     * Delete a given Policy
     *
     * @param policy
     */
    public void deletePolicy(Policy policy);
    
    /**
     * Save a given PolicyLink
     *
     * @param policyLink
     */
    public void saveOrUpdatePolicyLink(PolicyLink policyLink);

    /**
     * Delete a given PolicyLink
     * @param policyLink
     */
    public void deletePolicyLink(PolicyLink policyLink);

    /**
     * Delete a given RoleModule
     *
     * @param roleModule
     */
    public void deleteRoleModule(RoleModule roleModule);

    /**
     * Save a given RoleModule
     *
     * @param roleModule
     */
    public void saveRoleModule(RoleModule roleModule);

    /**
     * Delete a given RoleJobPlan
     *
     * @param roleJobPlan
     */
    public void deleteRoleJobPlan(RoleJobPlan roleJobPlan);

    /**
     * Save a given RoleJobPlan
     *
     * @param roleJobPlan
     */
    public void saveRoleJobPlan(RoleJobPlan roleJobPlan);

    /**
     * Save a given IkasanPrincipal
     *
     * @param principal
     */
    public void saveOrUpdatePrincipal(IkasanPrincipal principal);


    /**
     * Delete a given IkasanPrincipal
     *
     * @param principal
     */
    public void deletePrincipal(IkasanPrincipal principal);

    /**
     * Get an IkasanPrincipal by name.
     *
     * @param name
     * @return
     */
    public IkasanPrincipal getPrincipalByName(String name);

    /**
     * Get IkasanPrincipals associated with a list of roles.
     *
     * @param names
     * @return
     */
    public List<IkasanPrincipal> getPrincipalsByRoleNames(List<String> names);

    /**
     * Get all Policies
     *
     * @return     
     */
    public List<Policy> getAllPolicies();

    /**
     * Get all Roles
     * @return     
     */
    public List<Role> getAllRoles();

    /**
     * Get all IkasanPrincipals
     * @return     
     */
    public List<IkasanPrincipal> getAllPrincipals();

    /**
     * IkasanPrincipalLites
     * @return
     */
    public List<IkasanPrincipalLite> getAllPrincipalLites();

    /**
     * Get all IkasanPrincipals associated with a given Role
     * @return     
     */
    public List<IkasanPrincipal> getAllPrincipalsWithRole(String roleName);
    
    /**
     * Get all Policies associated with a given Role
     *
     * @return    
     */
    public List<Policy> getAllPoliciesWithRole(String roleName);

    /**
     * Get a Policy by its name.
     *
     * @return    
     */
    public Policy getPolicyByName(String name);

    /**
     * Get a Role by its name
     *
     * @param name
     * @return    
     */
    public Role getRoleByName(String name);

    /**
     * Get a Role by its id
     * @param id
     * @return
     */
    public Role getRoleById(Long id);

    /**
     * Save a given AuthenticationMethod
     * @param authenticationMethod     
     */
    public void saveOrUpdateAuthenticationMethod(AuthenticationMethod authenticationMethod);

    /**
     * Get an AuthenticationMethod by its id
     * @param id
     * @return
     */
    public AuthenticationMethod getAuthenticationMethod(Long id);
    
    /**
     * Get all AuthenticationMethods
     * @return
     */
    public List<AuthenticationMethod> getAuthenticationMethods();
    
    /**
     * Get IkasanPrincipals whose name is like
     *
     * @param name
     * @return
     */
    public List<IkasanPrincipal> getPrincipalByNameLike(String name);
    
    /**
     * Delete the given AuthenticationMethod
     *
     * @param authenticationMethod
     */
    public void deleteAuthenticationMethod(AuthenticationMethod authenticationMethod);
 
    /**
     * Get all PolicyLinkTypes
     * @return
     */
    public List<PolicyLinkType> getAllPolicyLinkTypes();

    
    /**
     * Save a given PolicyLinkType
     *
     * @param policyLinkType
     */
    public void saveOrUpdatePolicyLinkType(PolicyLinkType policyLinkType);
    
    /**
     * Get Policies whose name is like
     *
     * @param name
     * @return
     */
    public List<Policy> getPolicyByNameLike(String name);

    /**
     * Get Roles whose name is like
     *
     * @param name
     * @return
     */
    public List<Role> getRoleByNameLike(String name);
    
    /**
     * Get the number of AuthenticationMethod
     *
     * @return
     */
    public long getNumberOfAuthenticationMethods();
    
    /**
     * Get the AuthenticationMethod by order
     *
     * @param order
     * @return
     */
    public AuthenticationMethod getAuthenticationMethodByOrder(long order);
    
    /**
     * Get all Users associated with a principal id
     *
     * @param principalId
     * @return
     */
    public List<User> getUsersAssociatedWithPrincipal(long principalId);

    /**
     * Get a Policy by id
     *
     * @param id
     * @return
     */
    public Policy getPolicyById(Long id);
}
