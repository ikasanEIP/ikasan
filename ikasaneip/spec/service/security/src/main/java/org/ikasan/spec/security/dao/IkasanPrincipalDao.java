package org.ikasan.spec.security.dao;

import org.ikasan.spec.security.model.IkasanPrincipal;
import org.ikasan.spec.security.model.IkasanPrincipalFilter;
import org.ikasan.spec.security.model.IkasanPrincipalLite;

import java.util.List;

/**
 * Data Access Object interface for managing Ikasan principals.
 *
 * This interface defines methods for CRUD operations on Ikasan principals,
 * including principal creation, retrieval, update, and deletion. It also provides
 * methods for querying principals by various criteria, including role associations,
 * and supports pagination and filtering.
 *
 * @author Ikasan Development Team
 */
public interface IkasanPrincipalDao {

    String PRINCIPAL_TYPE = "securityPrincipal";

    /**
     * Creates a new empty IkasanPrincipal instance.
     *
     * @return a new IkasanPrincipal instance
     */
    IkasanPrincipal createPrincipal();

    /**
     * Saves or updates a principal.
     *
     * If the principal already exists (same name), it will be updated. The principal is identified
     * by its name, which must be unique.
     *
     * @param principal the principal to save or update
     */
    void saveOrUpdatePrincipal(IkasanPrincipal principal);

    /**
     * Saves or updates the provided list of IkasanPrincipal objects.
     *
     * @param principals the list of IkasanPrincipal objects to save or update
     */
    void saveOrUpdatePrincipals(List<IkasanPrincipal> principals);

    /**
     * Deletes a principal.
     *
     * @param principal the principal to delete
     */
    void deletePrincipal(IkasanPrincipal principal);

    /**
     * Retrieves all principals.
     *
     * @return a list of all principals, or an empty list if no principals exist
     */
    List<IkasanPrincipal> getAllPrincipals();

    /**
     * Retrieves principals with pagination and optional filtering.
     *
     * @param filter the filter criteria to apply (may be null for unfiltered results)
     * @param limit the maximum number of principals to return
     * @param offset the starting position in the result set
     * @return a list of principals matching the criteria
     */
    List<IkasanPrincipal> getPrincipals(IkasanPrincipalFilter filter, int limit, int offset);

    /**
     * Retrieves all lightweight principal representations.
     *
     * @return a list of all principal lite objects, or an empty list if no principals exist
     */
    List<IkasanPrincipalLite> getAllPrincipalLites();

    /**
     * Retrieves lightweight principals with pagination and optional filtering.
     *
     * @param filter the filter criteria to apply (may be null for unfiltered results)
     * @param limit the maximum number of principals to return
     * @param offset the starting position in the result set
     * @return a list of principal lite objects matching the criteria
     */
    List<IkasanPrincipalLite> getPrincipalLites(IkasanPrincipalFilter filter, int limit, int offset);

    /**
     * Retrieves a principal by its unique ID.
     *
     * @param id the unique ID of the principal to retrieve
     * @return the IkasanPrincipal object if found, or {@code null} if no principal exists with the given ID
     */
    IkasanPrincipal findById(String id);

    /**
     * Retrieves a principal by its exact name.
     *
     * @param name the exact name of the principal to retrieve
     * @return the IkasanPrincipal object if found, or {@code null} if no principal exists with the given name
     */
    IkasanPrincipal getPrincipalByName(String name);

    /**
     * Retrieves principals whose names contain the specified search term.
     *
     * @param name the search term to match against principal names
     * @return a list of principals whose names contain the search term, or an empty list if no matches found
     */
    List<IkasanPrincipal> getPrincipalByNameLike(String name);

    /**
     * Retrieves all principals associated with a specific role.
     *
     * @param roleName the name of the role
     * @return a list of principals that have the specified role, or an empty list if none found
     */
    List<IkasanPrincipal> getAllPrincipalsWithRole(String roleName);

    /**
     * Retrieves lightweight principals associated with a specific role, with pagination and filtering.
     *
     * @param roleName the name of the role
     * @param filter the filter criteria to apply (may be null for no additional filtering)
     * @param limit the maximum number of principals to return
     * @param offset the starting position in the result set
     * @return a list of principal lite objects that have the specified role and match the filter criteria
     */
    List<IkasanPrincipalLite> getAllPrincipalsWithRole(String roleName, IkasanPrincipalFilter filter, int limit, int offset);

    /**
     * Efficiently retrieves only the names of principals with a specific role.
     *
     * @param roleName the name of the role to filter by
     * @param filter optional filter for additional principal criteria
     * @param limit maximum number of results to return
     * @param offset offset for pagination
     * @return a list of principal names (strings) for principals with the specified role
     */
    List getAllPrincipalNamesWithRole(String roleName, IkasanPrincipalFilter filter, int limit, int offset);

    /**
     * Retrieves lightweight principals NOT associated with a specific role, with pagination and filtering.
     *
     * @param roleName the name of the role to exclude
     * @param filter the filter criteria to apply (may be null for no additional filtering)
     * @param limit the maximum number of principals to return
     * @param offset the starting position in the result set
     * @return a list of principal lite objects that do NOT have the specified role
     */
    List<IkasanPrincipalLite> getAllPrincipalsWithoutRole(String roleName, IkasanPrincipalFilter filter, int limit, int offset);

    /**
     * Efficiently retrieves only the names of principals without a specific role.
     *
     * @param roleName the name of the role to exclude
     * @param filter optional filter for additional principal criteria
     * @param limit maximum number of results to return
     * @param offset offset for pagination
     * @return a list of principal names (strings) for principals without the specified role
     */
    List getAllPrincipalNamesWithoutRole(String roleName, IkasanPrincipalFilter filter, int limit, int offset);

    /**
     * Retrieves principals by their role names.
     *
     * @param roleNames list of role names to search for
     * @return a list of principals that have at least one of the specified roles, or an empty list if none found
     */
    List<IkasanPrincipal> getPrincipalsByRoleNames(List<String> roleNames);

    /**
     * Gets the total count of principals matching the filter criteria.
     *
     * @param filter the filter criteria to apply (may be null for total count)
     * @return the count of matching principals
     */
    int getPrincipalCount(IkasanPrincipalFilter filter);

    /**
     * Gets the count of principals with a specific role.
     *
     * @param roleName the name of the role
     * @param filter the filter criteria to apply (may be null for no additional filtering)
     * @return the count of principals that have the specified role and match the filter criteria
     */
    int getPrincipalsWithRoleCount(String roleName, IkasanPrincipalFilter filter);

    /**
     * Gets the count of principals without a specific role.
     *
     * @param roleName the name of the role to exclude
     * @param filter the filter criteria to apply (may be null for no additional filtering)
     * @return the count of principals that do NOT have the specified role
     */
    int getPrincipalsWithoutRoleCount(String roleName, IkasanPrincipalFilter filter);
}
