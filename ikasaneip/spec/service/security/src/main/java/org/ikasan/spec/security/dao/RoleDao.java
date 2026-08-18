package org.ikasan.spec.security.dao;

import org.ikasan.spec.security.model.Role;
import org.ikasan.spec.security.model.RoleJobPlan;
import org.ikasan.spec.security.model.RoleModule;

import java.util.List;

/**
 * Data Access Object interface for managing security roles.
 *
 * This interface defines methods for CRUD operations on security roles,
 * including role creation, retrieval, update, and deletion. It also provides
 * methods for managing role relationships with modules, job plans, and policies.
 *
 * @author Ikasan Development Team
 */
public interface RoleDao {

    /**
     * Creates a new empty Role instance.
     *
     * @return a new Role instance
     */
    Role createRole();

    /**
     * Saves or updates a role.
     *
     * If the role already exists (same name), it will be updated. The role is identified
     * by its name, which must be unique.
     *
     * @param role the role to save or update
     */
    void saveOrUpdateRole(Role role);

    /**
     * Deletes a role.
     *
     * @param role the role to delete
     */
    void deleteRole(Role role);

    /**
     * Associates a RoleModule instance with its corresponding Role and persists the updated Role.
     *
     * @param roleModule the RoleModule to save and associate with its corresponding Role
     */
    void saveRoleModule(RoleModule roleModule);

    /**
     * Deletes the specified RoleModule from its associated Role and persists the changes.
     *
     * @param roleModule the RoleModule instance to be removed from its associated Role
     */
    void deleteRoleModule(RoleModule roleModule);

    /**
     * Associates a RoleJobPlan instance with its corresponding Role and persists the updated Role.
     *
     * @param roleJobPlan the RoleJobPlan to save and associate with its corresponding Role
     */
    void saveRoleJobPlan(RoleJobPlan roleJobPlan);

    /**
     * Deletes the specified RoleJobPlan from its associated Role and persists the changes.
     *
     * @param roleJobPlan the RoleJobPlan instance to be removed from its associated Role
     */
    void deleteRoleJobPlan(RoleJobPlan roleJobPlan);

    /**
     * Retrieves all roles.
     *
     * @return a list of all roles, or an empty list if no roles exist
     */
    List<Role> getAllRoles();

    /**
     * Retrieves a role by its exact name.
     *
     * @param name the exact name of the role to retrieve
     * @return the Role object if found, or {@code null} if no role exists with the given name
     */
    Role getRoleByName(String name);

    /**
     * Retrieves a Role by its unique identifier.
     *
     * @param id the unique identifier (name) of the role to retrieve
     * @return the Role object if a matching record is found, or {@code null}
     *         if no record exists for the provided identifier
     */
    Role getRoleById(String id);

    /**
     * Retrieves roles whose names contain the specified search term.
     *
     * @param name the search term to match against role names
     * @return a list of roles whose names contain the search term, or an empty list if no matches found
     */
    List<Role> getRoleByNameLike(String name);

    /**
     * Retrieves all role job plans associated with a specific job plan name.
     *
     * @param jobPlanName the name of the job plan
     * @return a list of RoleJobPlan objects that match the job plan name, or an empty list if none found
     */
    List<RoleJobPlan> getRoleJobPlansByJobPlanName(String jobPlanName);

    /**
     * Retrieves a list of roles associated with the specified policy identifier.
     *
     * @param policyId the unique identifier of the policy for which associated roles need to be retrieved
     * @return a list of roles associated with the specified policy, or an empty list if no roles are found
     */
    List<Role> getRolesAssociatedWithPolicy(Object policyId);
}
