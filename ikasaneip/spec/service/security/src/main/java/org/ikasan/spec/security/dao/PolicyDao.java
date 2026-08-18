package org.ikasan.spec.security.dao;

import org.ikasan.spec.security.model.Policy;

import java.util.List;

/**
 * Data Access Object interface for managing security policies.
 *
 * This interface defines methods for CRUD operations on security policies,
 * including policy creation, retrieval, update, and deletion.
 *
 * @author Ikasan Development Team
 */
public interface PolicyDao {

    /**
     * Creates a new empty Policy instance.
     *
     * @return a new Policy instance
     */
    Policy createPolicy();

    /**
     * Saves or updates a policy.
     *
     * If the policy already exists (same name), it will be updated. The policy is identified
     * by its name, which must be unique.
     *
     * @param policy the policy to save or update
     */
    void saveOrUpdatePolicy(Policy policy);

    /**
     * Deletes a policy.
     *
     * @param policy the policy to delete
     */
    void deletePolicy(Policy policy);

    /**
     * Retrieves all policies.
     *
     * @return a list of all policies, or an empty list if no policies exist
     */
    List<Policy> getAllPolicies();

    /**
     * Retrieves all policies associated with a specific role.
     *
     * @param roleName the name of the role to filter policies by
     * @return a list of policies associated with the given role, or an empty list if
     *         the role name is null/empty or no policies are associated with the role
     */
    List<Policy> getAllPoliciesWithRole(String roleName);

    /**
     * Retrieves a policy by its exact name.
     *
     * @param name the exact name of the policy to retrieve
     * @return the Policy object if found, or {@code null} if no policy exists with the given name
     */
    Policy getPolicyByName(String name);

    /**
     * Retrieves policies whose names contain the specified search term.
     *
     * @param name the search term to match against policy names
     * @return a list of policies whose names contain the search term, or an empty list if no matches found
     */
    List<Policy> getPolicyByNameLike(String name);

    /**
     * Retrieves a Policy by its unique identifier.
     *
     * @param id the unique identifier (name) of the policy to retrieve
     * @return the Policy object if a matching record is found, or {@code null}
     *         if no record exists for the provided identifier
     */
    Policy getPolicyById(String id);
}
