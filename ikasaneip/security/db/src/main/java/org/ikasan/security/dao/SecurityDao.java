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
 * Interface for interacting with the security related data in the system.
 */
public interface SecurityDao
{
    /**
     * Saves or updates the provided Role in the database.
     *
     * @param role the Role object to be saved or updated
     */
    void saveOrUpdateRole(Role role);

    /**
     * Delete the specified Role from the system.
     *
     * @param role the Role to be deleted from the system
     */
    void deleteRole(Role role);

    /**
     * Save or update a Policy in the database.
     *
     * @param policy the Policy object to be saved or updated
     */
    void saveOrUpdatePolicy(Policy policy);

    /**
     * Deletes the provided Policy from the system.
     *
     * @param policy the Policy to be deleted
     */
    void deletePolicy(Policy policy);
    
    /**
     * Saves or updates the given PolicyLink.
     *
     * @param policyLink the PolicyLink to be saved or updated
     */
    void saveOrUpdatePolicyLink(PolicyLink policyLink);

    /**
     * Delete the provided PolicyLink.
     *
     * @param policyLink the PolicyLink to be deleted
     */
    void deletePolicyLink(PolicyLink policyLink);

    /**
     * Deletes the given RoleModule from the system.
     *
     * @param roleModule the RoleModule to be deleted
     */
    void deleteRoleModule(RoleModule roleModule);

    /**
     * Saves a RoleModule entity in the database.
     *
     * @param roleModule the RoleModule entity to be saved
     */
    void saveRoleModule(RoleModule roleModule);

    /**
     * Deletes the specified RoleJobPlan from the database.
     *
     * @param roleJobPlan the RoleJobPlan object to be deleted
     */
    void deleteRoleJobPlan(RoleJobPlan roleJobPlan);

    /**
     * Saves a RoleJobPlan entity to the database.
     *
     * @param roleJobPlan the RoleJobPlan entity to save
     */
    void saveRoleJobPlan(RoleJobPlan roleJobPlan);

    /**
     * Save or update the given IkasanPrincipal.
     *
     * @param principal the IkasanPrincipal to be saved or updated
     */
    void saveOrUpdatePrincipal(IkasanPrincipal principal);


    /**
     * Deletes the given IkasanPrincipal from the system.
     *
     * @param principal the IkasanPrincipal to be deleted
     */
    void deletePrincipal(IkasanPrincipal principal);

    /**
     * Retrieves an IkasanPrincipal by name.
     *
     * @param name the name of the IkasanPrincipal
     * @return the IkasanPrincipal object with the provided name
     */
    IkasanPrincipal getPrincipalByName(String name);

    /**
     * Retrieve a list of IkasanPrincipals based on the provided role names.
     *
     * @param names a list of role names to retrieve the IkasanPrincipals for
     * @return a list of IkasanPrincipals based on the provided role names
     */
    List<IkasanPrincipal> getPrincipalsByRoleNames(List<String> names);

    /**
     * Retrieves the count of IkasanPrincipals based on the provided IkasanPrincipalFilter.
     *
     * @param filter the filter to apply for counting the IkasanPrincipals
     * @return the count of IkasanPrincipals based on the provided filter
     */
    int getPrincipalCount(IkasanPrincipalFilter filter);


    /**
     * Retrieves the count of IkasanPrincipals that have a specific role and match the provided filter.
     *
     * @param roleName the name of the role to count the IkasanPrincipals for
     * @param filter the filter to apply for counting the IkasanPrincipals
     * @return the count of IkasanPrincipals with the specified role and filter
     */
    int getPrincipalsWithRoleCount(String roleName, IkasanPrincipalFilter filter);


    /**
     * Retrieves the count of IkasanPrincipals that do not have a specific role and match the provided filter.
     *
     * @param roleName the name of the role to exclude from the count
     * @param filter the filter to apply for counting the IkasanPrincipals
     * @return the count of IkasanPrincipals without the specified role and based on the provided filter
     */
    int getPrincipalsWithoutRoleCount(String roleName, IkasanPrincipalFilter filter);

    /**
     * Retrieves all policies from the system.
     *
     * @return a list of Policy objects representing all policies in the system
     */
    List<Policy> getAllPolicies();

    /**
     * Retrieves a list of all roles.
     *
     * @return a List of Role objects representing all roles
     */
    List<Role> getAllRoles();

    /**
     * Retrieve all IkasanPrincipal objects.
     *
     * @return a list of IkasanPrincipal objects
     */
    List<IkasanPrincipal> getAllPrincipals();

    /**
     * Retrieve a list of IkasanPrincipals based on the provided filter, limit, and offset.
     *
     * @param filter the filter to apply for retrieving the IkasanPrincipals
     * @param limit the maximum number of IkasanPrincipals to retrieve
     * @param offset the position in the result set from where to start retrieving IkasanPrincipals
     * @return a list of IkasanPrincipals based on the provided filter, limit, and offset
     */
    List<IkasanPrincipal> getPrincipals(IkasanPrincipalFilter filter, int limit, int offset);


    /**
     * Retrieve all IkasanPrincipalLite objects.
     *
     * @return a list of IkasanPrincipalLite objects
     */
    List<IkasanPrincipalLite> getAllPrincipalLites();

    /**
     * Retrieves a list of IkasanPrincipalLite objects based on the provided filter, limit, and offset.
     *
     * @param filter the filter to apply for retrieving the IkasanPrincipalLite objects
     * @param limit the maximum number of IkasanPrincipalLite objects to retrieve
     * @param offset the position in the result set from where to start retrieving IkasanPrincipalLite objects
     * @return a list of IkasanPrincipalLite objects based on the provided filter, limit, and offset
     */
    List<IkasanPrincipalLite> getPrincipalLites(IkasanPrincipalFilter filter, int limit, int offset);

    /**
     * Get all IkasanPrincipals associated with a given Role
     *
     * @return     
     */
    List<IkasanPrincipal> getAllPrincipalsWithRole(String roleName);


    /**
     * Retrieves a list of IkasanPrincipalLite objects associated with a specified role,
     * applying the provided filter and pagination parameters.
     *
     * @param roleName the name of the role to retrieve IkasanPrincipals for
     * @param filter   the filter to apply for retrieving the IkasanPrincipals
     * @param limit    the maximum number of IkasanPrincipalLite objects to retrieve
     * @param offset   the position in the result set from where to start retrieving objects
     * @return a list of IkasanPrincipalLite objects based on the provided role, filter, limit, and offset
     */
    List<IkasanPrincipalLite> getAllPrincipalsWithRole(String roleName, IkasanPrincipalFilter filter, int limit, int offset);

    /**
     * Retrieves a list of IkasanPrincipalLite objects that do not have the specified role.
     *
     * @param roleName the name of the role to exclude from the search
     * @param filter optional filter to apply to the search
     * @param limit maximum number of results to return
     * @param offset index from which to start the results
     * @return a list of IkasanPrincipalLite objects without the specified role
     */
    List<IkasanPrincipalLite> getAllPrincipalsWithoutRole(String roleName, IkasanPrincipalFilter filter, int limit, int offset);
    
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
