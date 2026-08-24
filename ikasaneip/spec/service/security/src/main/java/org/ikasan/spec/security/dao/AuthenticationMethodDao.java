package org.ikasan.spec.security.dao;

import org.ikasan.spec.security.model.AuthenticationMethod;

import java.util.List;

/**
 * Data Access Object interface for managing authentication methods.
 *
 * This interface defines methods for CRUD operations on authentication methods,
 * including authentication method creation, retrieval, update, and deletion.
 * It also provides methods for querying authentication methods by order/priority.
 *
 * @author Ikasan Development Team
 */
public interface AuthenticationMethodDao {

    String AUTHENTICATION_METHOD_TYPE = "securityAuthenticationMethod";

    /**
     * Creates a new empty AuthenticationMethod instance.
     *
     * @return a new AuthenticationMethod instance
     */
    AuthenticationMethod createAuthenticationMethod();

    /**
     * Saves or updates an authentication method.
     *
     * If the authentication method already exists (same name), it will be updated.
     * The authentication method is identified by its name, which must be unique.
     *
     * @param authenticationMethod the authentication method to save or update
     */
    void saveOrUpdateAuthenticationMethod(AuthenticationMethod authenticationMethod);

    /**
     * Retrieves an authentication method by its ID.
     *
     * @param id the unique identifier of the authentication method
     * @return the authentication method, or null if not found
     */
    AuthenticationMethod getAuthenticationMethod(Object id);

    /**
     * Retrieves all authentication methods.
     *
     * @return a list of all authentication methods, ordered by their order field
     */
    List<AuthenticationMethod> getAuthenticationMethods();

    /**
     * Gets the total count of authentication methods.
     *
     * @return the number of authentication methods
     */
    long getNumberOfAuthenticationMethods();

    /**
     * Retrieves an authentication method by its order/priority.
     *
     * @param order the order/priority of the authentication method
     * @return the authentication method with the specified order, or null if not found
     */
    AuthenticationMethod getAuthenticationMethodByOrder(long order);

    /**
     * Deletes an authentication method.
     *
     * @param authenticationMethod the authentication method to delete
     */
    void deleteAuthenticationMethod(AuthenticationMethod authenticationMethod);
}
