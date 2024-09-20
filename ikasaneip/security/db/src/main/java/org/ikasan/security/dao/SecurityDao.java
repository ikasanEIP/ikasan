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
    void saveOrUpdateRole(Role role);

    /**
     * Delete a given Role
     *
     * @param role
     */
    void deleteRole(Role role);

    /**
     * Save a given Policy
     *
     * @param policy
     */
    void saveOrUpdatePolicy(Policy policy);

    /**
     * Delete a given Policy
     *
     * @param policy
     */
    void deletePolicy(Policy policy);
    
    /**
     * Save a given PolicyLink
     *
     * @param policyLink
     */
    void saveOrUpdatePolicyLink(PolicyLink policyLink);

    /**
     * Delete a given PolicyLink
     * @param policyLink
     */
    void deletePolicyLink(PolicyLink policyLink);

    /**
     * Delete a given RoleModule
     *
     * @param roleModule
     */
    void deleteRoleModule(RoleModule roleModule);

    /**
     * Save a given RoleModule
     *
     * @param roleModule
     */
    void saveRoleModule(RoleModule roleModule);

    /**
     * Delete a given RoleJobPlan
     *
     * @param roleJobPlan
     */
    void deleteRoleJobPlan(RoleJobPlan roleJobPlan);

    /**
     * Save a given RoleJobPlan
     *
     * @param roleJobPlan
     */
    void saveRoleJobPlan(RoleJobPlan roleJobPlan);

    /**
     * Save a given IkasanPrincipal
     *
     * @param principal
     */
    void saveOrUpdatePrincipal(IkasanPrincipal principal);


    /**
     * Delete a given IkasanPrincipal
     *
     * @param principal
     */
    void deletePrincipal(IkasanPrincipal principal);

    /**
     * Get an IkasanPrincipal by name.
     *
     * @param name
     * @return
     */
    IkasanPrincipal getPrincipalByName(String name);

    /**
     * Get IkasanPrincipals associated with a list of roles.
     *
     * @param names
     * @return
     */
    List<IkasanPrincipal> getPrincipalsByRoleNames(List<String> names);

    /**
     * Get all Policies
     *
     * @return     
     */
    List<Policy> getAllPolicies();

    /**
     * Get all Roles
     * @return     
     */
    List<Role> getAllRoles();

    /**
     * Get all IkasanPrincipals
     * @return     
     */
    List<IkasanPrincipal> getAllPrincipals();

    /**
     * IkasanPrincipalLites
     * @return
     */
    List<IkasanPrincipalLite> getAllPrincipalLites();

    /**
     * Get all IkasanPrincipals associated with a given Role
     * @return     
     */
    List<IkasanPrincipal> getAllPrincipalsWithRole(String roleName);
    
    /**
     * Get all Policies associated with a given Role
     *
     * @return    
     */
    List<Policy> getAllPoliciesWithRole(String roleName);

    /**
     * Get a Policy by its name.
     *
     * @return    
     */
    Policy getPolicyByName(String name);

    /**
     * Get a Role by its name
     *
     * @param name
     * @return    
     */
    Role getRoleByName(String name);

    /**
     * Get a Role by its id
     * @param id
     * @return
     */
    Role getRoleById(Long id);

    /**
     * Save a given AuthenticationMethod
     * @param authenticationMethod     
     */
    void saveOrUpdateAuthenticationMethod(AuthenticationMethod authenticationMethod);

    /**
     * Get an AuthenticationMethod by its id
     * @param id
     * @return
     */
    AuthenticationMethod getAuthenticationMethod(Long id);
    
    /**
     * Get all AuthenticationMethods
     * @return
     */
    List<AuthenticationMethod> getAuthenticationMethods();
    
    /**
     * Get IkasanPrincipals whose name is like
     *
     * @param name
     * @return
     */
    List<IkasanPrincipal> getPrincipalByNameLike(String name);
    
    /**
     * Delete the given AuthenticationMethod
     *
     * @param authenticationMethod
     */
    void deleteAuthenticationMethod(AuthenticationMethod authenticationMethod);
 
    /**
     * Get all PolicyLinkTypes
     * @return
     */
    List<PolicyLinkType> getAllPolicyLinkTypes();

    
    /**
     * Save a given PolicyLinkType
     *
     * @param policyLinkType
     */
    void saveOrUpdatePolicyLinkType(PolicyLinkType policyLinkType);
    
    /**
     * Get Policies whose name is like
     *
     * @param name
     * @return
     */
    List<Policy> getPolicyByNameLike(String name);

    /**
     * Get Roles whose name is like
     *
     * @param name
     * @return
     */
    List<Role> getRoleByNameLike(String name);
    
    /**
     * Get the number of AuthenticationMethod
     *
     * @return
     */
    long getNumberOfAuthenticationMethods();
    
    /**
     * Get the AuthenticationMethod by order
     *
     * @param order
     * @return
     */
    AuthenticationMethod getAuthenticationMethodByOrder(long order);
    
    /**
     * Get all Users associated with a principal id
     *
     * @param principalId
     * @return
     */
    List<User> getUsersAssociatedWithPrincipal(long principalId);

    /**
     * Get a Policy by id
     *
     * @param id
     * @return
     */
    Policy getPolicyById(Long id);

    /**
     * Get all RoleJobPlans by job plan name.
     *
     * @param jonPlanName
     * @return
     */
    List<RoleJobPlan> getRoleJobPlansByJobPlanName(String jonPlanName);
}
