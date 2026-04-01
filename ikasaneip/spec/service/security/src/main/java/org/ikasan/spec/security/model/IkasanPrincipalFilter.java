package org.ikasan.spec.security.model;

/**
 * Filter criteria interface for querying and sorting {@link IkasanPrincipal} instances.
 *
 * <p>Provides filtering capabilities based on principal attributes such as name, type, and description,
 * along with sorting options to control the order of query results. All filter values use partial matching
 * semantics where applicable.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface IkasanPrincipalFilter
{
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

    /**
     * Retrieves the filter value for principal names.
     *
     * @return the name filter pattern, or {@code null} if not set
     */
    String getNameFilter();

    /**
     * Sets the filter value for principal names using partial matching.
     *
     * @param nameFilter the name pattern to filter by
     */
    void setNameFilter(String nameFilter);

    /**
     * Retrieves the filter value for principal types.
     *
     * @return the type filter pattern, or {@code null} if not set
     */
    String getTypeFilter();

    /**
     * Sets the filter value for principal types using partial matching.
     *
     * @param typeFilter the type pattern to filter by
     */
    void setTypeFilter(String typeFilter);

    /**
     * Retrieves the filter value for principal descriptions.
     *
     * @return the description filter pattern, or {@code null} if not set
     */
    String getDescriptionFilter();

    /**
     * Sets the filter value for principal descriptions using partial matching.
     *
     * @param descriptionFilter the description pattern to filter by
     */
    void setDescriptionFilter(String descriptionFilter);
}
