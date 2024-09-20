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
     * Retrieves an IkasanPrincipal object based on the provided name.
     *
     * @param name The name of the principal.
     * @return The IkasanPrincipal object associated with the provided name, or null if no principal was found.
     */
    IkasanPrincipal findPrincipalByName(String name);

    /**
     * Finds a Role object by its name.
     *
     * @param name The name of the Role to search for.
     * @return The Role object associated with the provided name, or null if no Role was found.
     */
    Role findRoleByName(String name);

    /**
     * Finds a policy by its name.
     *
     * @param name The name of the policy to find.
     * @return The policy with the given name, or null if no policy was found.
     */
    Policy findPolicyByName(String name);

    /**
     * Creates a new {@link IkasanPrincipal} with the given name and type.
     *
     * @param name The name of the new principal.
     * @param type The type of the new principal.
     * @return The created {@link IkasanPrincipal}.
     */
    IkasanPrincipal createNewPrincipal(String name, String type);

    /**
     * Saves an IkasanPrincipal object to the database.
     *
     * @param principal The IkasanPrincipal object to be saved.
     */
    void savePrincipal(IkasanPrincipal principal);

    /**
     * Retrieves a list of all IkasanPrincipal objects.
     *
     * @return A list of IkasanPrincipal objects
     */
    List<IkasanPrincipal> getAllPrincipals();

    /**
     * Retrieves a list of all IkasanPrincipalLite objects.
     *
     * @return A list of all IkasanPrincipalLite objects.
     */
    List<IkasanPrincipalLite> getAllPrincipalLites();

    /**
     * Retrieves a list of IkasanPrincipal objects that have the specified role.
     *
     * @param roleName The name of the role to filter the principals by.
     * @return A list of IkasanPrincipal objects that have the specified role.
     */
    List<IkasanPrincipal> getAllPrincipalsWithRole(String roleName);

    /**
     * Retrieves a list of IkasanPrincipal objects by the provided names.
     *
     * @param names The list of names to search for.
     * @return A list of IkasanPrincipal objects matching the provided names.
     */
    List<IkasanPrincipal> getPrincipalsByName(List<String> names);

    /**
     * Deletes the specified principal from the security service.
     *
     * @param principal The principal to be deleted.
     */
    void deletePrincipal(IkasanPrincipal principal);

    /**
     * Creates a new Role with the specified name and description.
     *
     * @param name        The name of the Role.
     * @param description The description of the Role.
     * @return The newly created Role object.
     */
    Role createNewRole(String name, String description);

    /**
     * Saves a Role object to the database.
     *
     * @param role The Role object to be saved.
     */
    void saveRole(Role role);

    /**
     * Deletes a Role from the SecurityService.
     *
     * @param role The Role object to be deleted.
     */
    void deleteRole(Role role);

    /**
     * Retrieves a list of all Role objects.
     *
     * @return A list of all Role objects.
     */
    List<Role> getAllRoles();

    /**
     * Creates a new Policy with the given name and description.
     *
     * @param name        The name of the Policy.
     * @param description The description of the Policy.
     * @return The newly created Policy.
     */
    Policy createNewPolicy(String name, String description);

    /**
     * Saves a Policy object to the database.
     *
     * @param policy The Policy object to be saved.
     */
    void savePolicy(Policy policy);

    /**
     * Deletes a Policy object from the SecurityService.
     *
     * @param policy The Policy object to be deleted.
     */
    void deletePolicy(Policy policy);

    /**
     * Deletes the given RoleModule from the SecurityService.
     *
     * @param roleModule The RoleModule to be deleted.
     */
    void deleteRoleModule(RoleModule roleModule);

    /**
     * Deletes a RoleJobPlan from the SecurityService.
     *
     * @param roleJobPlan The RoleJobPlan to be deleted.
     */
    void deleteRoleJobPlan(RoleJobPlan roleJobPlan);

    /**
     * Retrieves a list of all policies.
     *
     * @return A list of Policy objects.
     */
    List<Policy> getAllPolicies();

    /**
     * Saves or updates an AuthenticationMethod object in the system.
     *
     * @param authenticationMethod The AuthenticationMethod object to be saved or updated.
     */
    void saveOrUpdateAuthenticationMethod(AuthenticationMethod authenticationMethod);

    /**
     * Retrieves a list of AuthenticationMethod objects.
     *
     * @return A list of AuthenticationMethod objects.
     */
    List<AuthenticationMethod> getAuthenticationMethods();

    /**
     * Retrieves the AuthenticationMethod object associated with the provided ID.
     *
     * @param id The ID of the AuthenticationMethod to retrieve.
     * @return The AuthenticationMethod object associated with the provided ID, or null if no AuthenticationMethod was found.
     */
    AuthenticationMethod getAuthenticationMethod(Long id);

    /**
     * Deletes an AuthenticationMethod from the SecurityService.
     *
     * @param authenticationMethod The AuthenticationMethod to be deleted.
     */
    void deleteAuthenticationMethod(AuthenticationMethod authenticationMethod);

    /**
     * Retrieves a list of IkasanPrincipal objects whose name matches the specified pattern.
     *
     * @param name The pattern to search for in principal names.
     * @return A list of IkasanPrincipal objects that match the given pattern.
     */
    List<IkasanPrincipal> getPrincipalByNameLike(String name);
    
    /**
     * Retrieves a list of all PolicyLinkType objects.
     *
     * @return A list of PolicyLinkType objects representing all policy link types.
     */
    List<PolicyLinkType> getAllPolicyLinkTypes();
    
    /**
     * Retrieves a list of Policy objects whose name contains the specified pattern.
     *
     * @param name The pattern to search for in the policy names.
     * @return A list of Policy objects whose name contains the specified pattern.
     */
    List<Policy> getPolicyByNameLike(String name);
    
    /**
     * Saves a PolicyLink object to the database.
     *
     * @param policyLink The PolicyLink object to be saved.
     */
    void savePolicyLink(PolicyLink policyLink);

    /**
     * Saves a RoleModule object to the database.
     *
     * @param roleModule The RoleModule object to be saved.
     */
    void saveRoleModule(RoleModule roleModule);

    /**
     * Saves a RoleJobPlan object to the database.
     *
     * @param roleJobPlan The RoleJobPlan object to be saved.
     */
    void saveRoleJobPlan(RoleJobPlan roleJobPlan);
    
    /**
     * Deletes a PolicyLink from the SecurityService.
     *
     * @param policyLink The PolicyLink to be deleted.
     */
    void deletePolicyLink(PolicyLink policyLink);
    
    /**
     * Retrieves a list of Policy objects that have the specified role.
     *
     * @param roleName The name of the role to filter the policies by.
     * @return A list of Policy objects that have the specified role.
     */
    List<Policy> getAllPoliciesWithRole(String roleName);
    
    /**
     * Retrieves a list of Role objects whose name matches the given pattern.
     *
     * @param name The pattern to search for in role names.
     * @return A list of Role objects that match the given pattern.
     */
    List<Role> getRoleByNameLike(String name);
    
    /**
     * Retrieves the number of authentication methods.
     *
     * @return The number of authentication methods as a `long` value.
     */
    long getNumberOfAuthenticationMethods();
    
    /**
     * Retrieves an AuthenticationMethod object based on the provided order.
     *
     * @param order The order of the AuthenticationMethod to retrieve.
     * @return The AuthenticationMethod object associated with the provided order, or null if no AuthenticationMethod was found.
     */
    AuthenticationMethod getAuthenticationMethodByOrder(long order);
    
    /**
     * Retrieves a list of users associated with the given principal ID.
     *
     * @param principalId The ID of the principal.
     * @return A list of users associated with the principal.
     */
    List<User> getUsersAssociatedWithPrincipal(long principalId);

    /**
     * Retrieves a Role object based on the provided id.
     *
     * @param id The id of the role to retrieve.
     * @return The Role object associated with the provided id, or null if no role was found.
     */
    Role getRoleById(Long id);

    /**
     * Retrieves a Policy object based on the provided id.
     *
     * @param id The id of the policy to retrieve.
     * @return The Policy object associated with the provided id, or null if no policy was found.
     */
    Policy getPolicyById(Long id);

    /**
     * Sets the roles associated with a job plan.
     *
     * @param jobPlanName The name of the job plan.
     * @param roleNames   The list of role names to be associated with the job plan.
     */
    void setJobPlanRoles(String jobPlanName, List<String> roleNames);
}

