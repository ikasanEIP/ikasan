package org.ikasan.spec.security.model;

/**
 * Filter criteria interface for querying and sorting {@link User} instances.
 *
 * <p>Provides filtering capabilities based on user attributes such as username, first name, last name,
 * email, and department, along with sorting options to control the order of query results. All filter
 * values use partial matching semantics where applicable.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface UserFilter
{
    /**
     * Retrieves the filter value for user first names.
     *
     * @return the first name filter pattern, or {@code null} if not set
     */
    String getNameFilter();

    /**
     * Retrieves the filter value for user last names.
     *
     * @return the last name filter pattern, or {@code null} if not set
     */
    String getLastNameFilter();

    /**
     * Sets the filter value for user first names using partial matching.
     *
     * @param nameFilter the first name pattern to filter by
     */
    void setNameFilter(String nameFilter);

    /**
     * Sets the filter value for user last names using partial matching.
     *
     * @param lastNameFilter the last name pattern to filter by
     */
    void setLastNameFilter(String lastNameFilter);

    /**
     * Retrieves the filter value for usernames.
     *
     * @return the username filter pattern, or {@code null} if not set
     */
    String getUsernameFilter();

    /**
     * Sets the filter value for usernames using partial matching.
     *
     * @param usernameFilter the username pattern to filter by
     */
    void setUsernameFilter(String usernameFilter);

    /**
     * Retrieves the filter value for user email addresses.
     *
     * @return the email filter pattern, or {@code null} if not set
     */
    String getEmailFilter();

    /**
     * Sets the filter value for user email addresses using partial matching.
     *
     * @param emailFilter the email pattern to filter by
     */
    void setEmailFilter(String emailFilter);

    /**
     * Retrieves the filter value for user departments.
     *
     * @return the department filter pattern, or {@code null} if not set
     */
    String getDepartmentFilter();

    /**
     * Sets the filter value for user departments using partial matching.
     *
     * @param departmentFilter the department pattern to filter by
     */
    void setDepartmentFilter(String departmentFilter);

    /**
     * Retrieves the sort order for query results.
     *
     * @return the sort order (e.g., "ASC" or "DESC"), or {@code null} if not set
     */
    String getSortOrder();

    /**
     * Sets the sort order for query results.
     *
     * @param sortOrder the sort order to set (e.g., "ASC" for ascending, "DESC" for descending)
     */
    void setSortOrder(String sortOrder);

    /**
     * Retrieves the column name to sort by.
     *
     * @return the column name for sorting, or {@code null} if not set
     */
    String getSortColumn();

    /**
     * Sets the column name to sort by.
     *
     * @param sortColumn the column name to use for sorting query results
     */
    void setSortColumn(String sortColumn);
}
